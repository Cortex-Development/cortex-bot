package dev.cortex.cortexbot.commands.menu;

import com.jagrosh.jdautilities.command.CooldownScope;
import com.jagrosh.jdautilities.command.MessageContextMenu;
import com.jagrosh.jdautilities.command.MessageContextMenuEvent;
import org.springframework.stereotype.Component;

@Component
public class ReportHelpContextMenu extends MessageContextMenu {
    
    public ReportHelpContextMenu() {
        this.name = "I Got Helped";
        this.cooldownScope = CooldownScope.USER;
        this.cooldown = 10;
    }
    
    @Override
    protected void execute(MessageContextMenuEvent event) {
        event.reply("A staff member will soon take a look!").setEphemeral(true).queue();
        // TODO
    }
}
