package dev.mikka.cortexbot.commands.help;

import com.jagrosh.jdautilities.command.MessageContextMenu;
import com.jagrosh.jdautilities.command.MessageContextMenuEvent;
import net.dv8tion.jda.api.interactions.components.Modal;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;

public class TestConextMenu extends MessageContextMenu {
    
    public TestConextMenu() {
        this.name = "Test";
        this.defaultEnabled = false;
        this.enabledRoles = new String[] {"503657931812634634", "786974475354505248"};
    }
    
    @Override
    protected void execute(MessageContextMenuEvent event) {
        Modal.Builder modal = Modal.create("test-modal", "Input");
        
        modal.addActionRow(TextInput.create("test-input-1", "Label-1", TextInputStyle.SHORT).build());
        modal.addActionRow(TextInput.create("test-input-2", "Label-2", TextInputStyle.SHORT).build());
        
        event.replyModal(modal.build()).queue();
    }
}
