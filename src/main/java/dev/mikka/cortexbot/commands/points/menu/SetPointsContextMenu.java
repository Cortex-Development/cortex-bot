package dev.mikka.cortexbot.commands.points.menu;

import com.jagrosh.jdautilities.command.UserContextMenu;
import com.jagrosh.jdautilities.command.UserContextMenuEvent;
import dev.mikka.cortexbot.commands.IModalHandler;
import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.CortexMember;
import me.kodysimpson.cortexbot.repositories.CortexMemberRepository;
import me.kodysimpson.cortexbot.services.LoggingService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import org.springframework.stereotype.Component;

@Component
public class SetPointsContextMenu extends UserContextMenu implements IModalHandler {

    private final CortexMemberRepository cortexMemberRepository;
    private final LoggingService loggingService;
    
    public SetPointsContextMenu(
            DiscordConfiguration discordConfiguration,
            CortexMemberRepository cortexMemberRepository,
            LoggingService loggingService
    ) {
        this.cortexMemberRepository = cortexMemberRepository;
        this.loggingService = loggingService;
        this.name = "Set Points";
        this.defaultEnabled = false;
        this.enabledRoles = new String[] {
                discordConfiguration.getOverlordRole().toString(),
                discordConfiguration.getStaffRole().toString()
        };
    }

    @Override
    protected void execute(UserContextMenuEvent event) {
        TextInput points = TextInput.create("points", "Total", TextInputStyle.SHORT)
                .setRequiredRange(1, 20)
                .setValue("100")
                .setPlaceholder("Integer")
                .build();

        TextInput reason = TextInput.create("reason", "Reason", TextInputStyle.PARAGRAPH)
                .setRequired(false)
                .setRequiredRange(0, 128)
                .build();

        event.replyModal(Modal.create("set-" + event.getTarget().getId(), "Set " + event.getTarget().getName() + "'s Total Points")
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
            targetMember.setPoints(points);
            cortexMemberRepository.save(targetMember);
            
            loggingService.logPointsSet(target, points, manager, reason);

            event.getHook().sendMessage(points + " point(s) have been set for " + target.getName() + ".").queue();

            target.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage(
                    "You now have a total of " + targetMember.getPoints() + " community points."
            )).queue();
            
        } catch (NumberFormatException exception) {
            event.getHook().sendMessage("Points must be a positive integer, dummy.").queue();
        }
    }
}
