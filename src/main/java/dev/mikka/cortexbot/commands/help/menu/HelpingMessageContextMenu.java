package dev.mikka.cortexbot.commands.help.menu;

import com.jagrosh.jdautilities.command.MessageContextMenu;
import com.jagrosh.jdautilities.command.MessageContextMenuEvent;
import dev.mikka.cortexbot.commands.IModalHandler;
import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.CortexMember;
import me.kodysimpson.cortexbot.repositories.CortexMemberRepository;
import me.kodysimpson.cortexbot.services.LoggingService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.stereotype.Component;

@Component
public class HelpingMessageContextMenu extends MessageContextMenu implements IModalHandler {
    
    private final CortexMemberRepository cortexMemberRepository;
    private final LoggingService loggingService;
    
    public HelpingMessageContextMenu(
            CortexMemberRepository cortexMemberRepository,
            LoggingService loggingService,
            DiscordConfiguration discordConfiguration
    ) {
        this.cortexMemberRepository = cortexMemberRepository;
        this.loggingService = loggingService;
        this.name = "Give Points for Helping";
        this.userPermissions = new Permission[] {
                Permission.MODERATE_MEMBERS,
        };
    }
    
    @Override
    protected void execute(MessageContextMenuEvent event) {
        TextInput points = TextInput.create("points", "Amount", TextInputStyle.SHORT)
                .setRequiredRange(1, 20)
                .setValue("100")
                .setPlaceholder("Integer")
                .build();

        TextInput reason = TextInput.create("reason", "Reason", TextInputStyle.PARAGRAPH)
                .setRequired(false)
                .setRequiredRange(0, 512)
                .setValue("Helping (<" + event.getTarget().getJumpUrl() + ">)")
                .build();

        event.replyModal(Modal.create("helping-" + event.getTarget().getAuthor().getId(), "Give " + event.getTarget().getAuthor().getName() + " Points for Helping")
                .addActionRow(points)
                .addActionRow(reason)
                .build()).queue();
    }

    @Override
    public void handleModal(ModalInteractionEvent event) {
        event.getHook().setEphemeral(true);
        
        User manager = event.getUser();
        User target = event.getJDA().getUserById(event.getModalId().split("-")[1]);
        
        String pointsString = event.getValue("points").getAsString();
        String reason = event.getValue("reason").getAsString();

        CortexMember targetMember = cortexMemberRepository.findByUserIDIs(target.getId());

        try {
            int points = Math.abs(Integer.parseInt(pointsString));   
            targetMember.setPoints(targetMember.getPoints() + points);
            cortexMemberRepository.save(targetMember);
            
            loggingService.logPointsGiven(target, points, manager, reason);

            event.getHook().sendMessage(points + " point(s) have been given to " + target.getName() + ".").queue();
            
            StringBuilder builder = new StringBuilder().append("You have been given ").append(points).append(" points. ");
            if (!reason.equals("")) {
                builder.append("for \"").append(reason).append("\". ");
            }
            builder.append("You now have a total of ").append(targetMember.getPoints()).append(" community points in Cortex Development.");
            
            target.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(
                    builder.toString()
                    )).queue();
        } catch (NumberFormatException exception) {
            event.getHook().sendMessage("Points must be a positive integer, dummy.").queue();
        }
    }
}
