package dev.mikka.cortexbot.commands.points;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import me.kodysimpson.cortexbot.repositories.CortexMemberRepository;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ResetPointsSlashCommand extends SlashCommand {
    
    @Autowired
    private CortexMemberRepository cortexMemberRepository;
    
    public ResetPointsSlashCommand() {
        this.name = "resetPoints";
        this.ownerCommand = true;
    }
    
    @Override
    protected void execute(SlashCommandEvent event) {
//        event.deferReply(true).queue();
//
//        event.getHook().sendMessage(
//                new MessageBuilder("Do you really want to reset the economy?").setActionRows(
//                        ActionRow.of(Button.danger("economy-reset-confirm", "Reset"), Button.primary("economy-reset-cancel", "Cancel"))
//                ).build()
//        ).queue();
    }
    
    public static void handleClick(ButtonInteractionEvent event) {
//        if (event.getButton().getId().equals("economy-reset-confirm")) {
//
//        } else {
//
//        }
    }
}
