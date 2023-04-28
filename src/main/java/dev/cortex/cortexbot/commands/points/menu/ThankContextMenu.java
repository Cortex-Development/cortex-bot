package dev.cortex.cortexbot.commands.points.menu;

import com.jagrosh.jdautilities.command.UserContextMenu;
import com.jagrosh.jdautilities.command.UserContextMenuEvent;
import dev.cortex.cortexbot.commands.IModalHandler;
import dev.cortex.cortexbot.model.CortexMember;
import dev.cortex.cortexbot.repositories.CortexMemberRepository;
import dev.cortex.cortexbot.services.LoggingService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.stereotype.Component;

@Component
public class ThankContextMenu extends UserContextMenu implements IModalHandler {

    private final CortexMemberRepository cortexMemberRepository;
    private final LoggingService loggingService;
    
    public ThankContextMenu(
            CortexMemberRepository cortexMemberRepository,
            LoggingService loggingService
    ) {
        this.cortexMemberRepository = cortexMemberRepository;
        this.loggingService = loggingService;
        this.name = "Thank Points";
    }

    @Override
    protected void execute(UserContextMenuEvent event) {
        CortexMember member = cortexMemberRepository.findByUserIDIs(event.getUser().getId());
        
        TextInput points = TextInput.create("points", "Amount", TextInputStyle.SHORT)
                .setRequiredRange(1, String.valueOf(member.getPoints()).length())
                .setValue(String.valueOf((long) Math.ceil(0.05 * member.getPoints())))
                .setPlaceholder("Integer")
                .build();

        TextInput reason = TextInput.create("reason", "Reason", TextInputStyle.PARAGRAPH)
                .setRequired(false)
                .setRequiredRange(0, 128)
                .build();

        event.replyModal(Modal.create("thank-" + event.getTarget().getId(), "Thank " + event.getTarget().getName() + " Points")
                .addActionRow(points)
                .addActionRow(reason)
                .build()).queue();
    }

    @Override
    public void handleModal(ModalInteractionEvent event) {
        event.getHook().setEphemeral(true);

        User payer = event.getUser();
        User payee = event.getJDA().getUserById(event.getModalId().split("-")[1]);

        String pointsString = event.getValue("points").getAsString();
        String reason = event.getValue("reason").getAsString();

        CortexMember payerMember = cortexMemberRepository.findByUserIDIs(payer.getId());
        CortexMember payeeMember = cortexMemberRepository.findByUserIDIs(payee.getId());

        try {
            int points = Math.abs(Integer.parseInt(pointsString));

            if (payerMember.getPoints() >= points) {

                payerMember.takePoints(points);
                payeeMember.addPoints(points);

                cortexMemberRepository.save(payerMember);
                cortexMemberRepository.save(payeeMember);

                loggingService.logPointsPayed(payee, points, payer, reason);

                event.getHook().sendMessage(points + " point(s) have been given to " + payee.getName() + " and they have been thanked. You now have a total of " + payerMember.getPoints() + " point(s).").queue();

                StringBuilder builder = new StringBuilder()
                        .append("You have been thanked by ")
                        .append(payer.getName())
                        .append(" and also tipped ")
                        .append(points);
                if (!reason.equals("")) {
                    builder.append(" for ")
                           .append(reason);
                }
                builder.append("!\n")
                        .append("You now have a total of")
                        .append(payeeMember.getPoints())
                        .append(" community points.");
                
                payee.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(
                        builder.toString()
                )).queue();

            } else {
                event.getHook().sendMessage("You do not have " + points + " point(s).").queue();
            }

        } catch (NumberFormatException exception) {
            event.getHook().sendMessage("Points must be a positive integer, dummy.").queue();
        }
    }
}
