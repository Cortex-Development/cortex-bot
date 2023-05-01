package dev.cortex.cortexbot.commands.points.menu;

import com.jagrosh.jdautilities.command.UserContextMenu;
import com.jagrosh.jdautilities.command.UserContextMenuEvent;
import dev.cortex.cortexbot.commands.IModalHandler;
import dev.cortex.cortexbot.config.DiscordConfiguration;
import dev.cortex.cortexbot.model.CortexMember;
import dev.cortex.cortexbot.repositories.CortexMemberRepository;
import dev.cortex.cortexbot.services.LoggingService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import org.springframework.stereotype.Component;

@Component
public class TakePointsContextMenu extends UserContextMenu implements IModalHandler {

    private final CortexMemberRepository cortexMemberRepository;
    private final LoggingService loggingService;

    public TakePointsContextMenu(
            CortexMemberRepository cortexMemberRepository,
            LoggingService loggingService,
            DiscordConfiguration discordConfiguration
    ) {
        this.cortexMemberRepository = cortexMemberRepository;
        this.loggingService = loggingService;
        this.name = "Take Points";
        this.userPermissions = new Permission[] {
                Permission.MODERATE_MEMBERS,
        };
    }

    @Override
    protected void execute(UserContextMenuEvent event) {
        CortexMember member = cortexMemberRepository.findByUserIDIs(event.getTarget().getId());
        
        TextInput points = TextInput.create("points", "Amount", TextInputStyle.SHORT)
                .setRequiredRange(1, String.valueOf(member.getPoints()).length())
                .setValue(String.valueOf(member.getPoints()))
                .setPlaceholder("Integer")
                .build();

        TextInput reason = TextInput.create("reason", "Reason", TextInputStyle.PARAGRAPH)
                .setRequired(false)
                .setRequiredRange(0, 128)
                .build();

        event.replyModal(Modal.create("take-" + event.getTarget().getId(), "Take Points from " + event.getTarget().getName())
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

            targetMember.takePoints(points);
            cortexMemberRepository.save(targetMember);
            
            loggingService.logPointsTaken(target, points, manager, reason);

            event.getHook().sendMessage(points + " point(s) have been taken from " + target.getName() + ".").queue();

            target.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(
                    points + " points have been taken from you. " + 
                            "You now have a total of " + targetMember.getPoints() + " community points."
            )).queue();
            
        } catch (NumberFormatException exception) {
            event.getHook().sendMessage("Points must be a positive integer, dummy.").setEphemeral(true).queue();
        }
    }
}
