package me.kodysimpson.cortexbot.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class WebsiteCommand extends Command {

    public WebsiteCommand() {
        this.name = "website";
        this.help = "Get the website link";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.getChannel().sendMessage("https://cortexdev.herokuapp.com").queue();
    }
}
