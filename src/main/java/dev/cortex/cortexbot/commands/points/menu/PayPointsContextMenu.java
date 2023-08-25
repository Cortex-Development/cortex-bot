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
public class PayPointsContextMenu extends UserContextMenu implements IModalHandler {

    private final CortexMemberRepository cortexMemberRepository;
    private final LoggingService loggingService;

    public PayPointsContextMenu(
            CortexMemberRepository cortexMemberRepository,
            LoggingService loggingService
    ) {
        this.cortexMemberRepository = cortexMemberRepository;
        this.loggingService = loggingService;
        this.name = "Pay Points";
    }

    @Override
    protected void execute(UserContextMenuEvent event) {
        CortexMember member = cortexMemberRepository.findByUserIDIs(event.getUser().getId());
        
        TextInput points = TextInput.create("points", "Amount", TextInputStyle.SHORT)
                .setRequiredRange(1, String.valueOf(member.getPoints()).length())
                .setValue(String.valueOf(member.getPoints()))
                .setPlaceholder("Integer")
                .build();

        TextInput reason = TextInput.create("reason", "Reason", TextInputStyle.PARAGRAPH)
                .setRequired(false)
                .setRequiredRange(0, 128)
                .build();

        event.replyModal(Modal.create("pay-" + event.getTarget().getId(), "Pay " + event.getTarget().getName() + " Points")
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
            
            if (payerMember.getPoints() < points) {
                event.getHook().sendMessage("You do not have " + points + " point(s).").setEphemeral(true).queue();
            }
            payerMember.takePoints(points);
            payeeMember.addPoints(points);

            cortexMemberRepository.save(payerMember);
            cortexMemberRepository.save(payeeMember);

            loggingService.logPointsPayed(payee, points, payer, reason);

            event.getHook().sendMessage(points + " point(s) have been given to " + payee.getName() + ". You now have a total of " + payerMember.getPoints() + " point(s).").queue();

            StringBuilder builder = new StringBuilder()
                    .append("You have been given ")
                    .append(points)
                    .append(" points by ")
                    .append(payer.getName())
                    .append(".");
            if (!reason.equals("")) {
                builder.append("for \"").append(reason).append("\". ");
            }
            builder.append("You now have a total of ").append(payeeMember.getPoints()).append(" community points in Cortex Development.");

            payee.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(
                    builder.toString()
            )).queue();
            
        } catch (NumberFormatException exception) {
            event.getHook().sendMessage("Points must be a positive integer, dummy.").setEphemeral(true).queue();
        }
    }
}