package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.springframework.stereotype.Component;

@Component
public class WebsiteCommand extends Command {

    public WebsiteCommand() {
        this.name = "website";
        this.help = "Get the website link";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getChannel().sendMessage("https://cortexdev.us **WIP**").queue();
    }
}
