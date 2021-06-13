package me.kodysimpson.cortexbot.commands.ceo;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.services.CEOService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CEOCommand extends Command {

    @Autowired
    CEOService ceoService;

    public CEOCommand(){
        this.name = "ceo";
        this.help = "see who the current CEO is";
    }

    @Override
    protected void execute(CommandEvent event) {

        event.reply("Your current **CEO** is " + ceoService.getCurrentCEO());

    }

}
