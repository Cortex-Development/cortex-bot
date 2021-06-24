package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.springframework.stereotype.Component;

@Component
public class JavaTutCommand extends Command {

    public JavaTutCommand(){
        this.name = "javatutorials";
        this.aliases = new String[]{"learnjava", "javatutorial"};
        this.category = new Category("Programming");
        this.help = "Get a link to Kody's Ultimate Java Tutorial";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.reply("External libraries are not always a good way to get familiar with Java as so we strongly advise you to watch Kody's Java tutorials before getting into Spigot or JDA. You can find it here.\n\nhttps://www.youtube.com/playlist?list=PLfu_Bpi_zcDPNy6qznvbkGZi7eP_0EL77");
    }
}
