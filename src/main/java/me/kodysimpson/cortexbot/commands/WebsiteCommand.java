package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.SlashCommand;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import org.springframework.stereotype.Component;

@Component
public class WebsiteCommand extends SlashCommand {

    public WebsiteCommand() {
        this.name = "website";
        this.help = "Get the website link";
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        slashCommandEvent.reply("COMING BACK SOON").queue();
    }

}
