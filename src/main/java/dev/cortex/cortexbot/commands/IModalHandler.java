package dev.cortex.cortexbot.commands;

import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent;

public interface IModalHandler {
    
    void handleModal(ModalInteractionEvent event);

}
