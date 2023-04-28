package dev.cortex.cortexbot.listeners;

import dev.cortex.cortexbot.commands.menu.HelpingMessageContextMenu;
import dev.cortex.cortexbot.commands.points.ResetPointsSlashCommand;
import dev.cortex.cortexbot.commands.points.menu.*;
import dev.cortex.cortexbot.commands.points.menu.*;
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class InteractionListener extends ListenerAdapter {
    
    @Autowired
    private HelpingMessageContextMenu helpingMessageContextMenu;
    @Autowired
    private GivePointsContextMenu givePointsContextMenu;
    @Autowired
    private PayPointsContextMenu payPointsContextMenu;
    @Autowired
    private SetPointsContextMenu setPointsContextMenu;
    @Autowired
    private TakePointsContextMenu takePointsContextMenu;
    @Autowired
    private ThankContextMenu thankContextMenu;

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        if (event.getButton().getId().contains("economy-reset")) {
            ResetPointsSlashCommand.handleClick(event);
        }
    }

    @Override
    public void onModalInteraction(@NotNull ModalInteractionEvent event) {
        event.deferReply(true).queue();
        switch (event.getModalId().split("-")[0]) {
            case "helping" -> helpingMessageContextMenu.handleModal(event);
            case "give" -> givePointsContextMenu.handleModal(event);
            case "pay" -> payPointsContextMenu.handleModal(event);
            case "set" -> setPointsContextMenu.handleModal(event);
            case "take" -> takePointsContextMenu.handleModal(event);
            case "thank" -> thankContextMenu.handleModal(event);
            default -> {}
        }
    }
}
