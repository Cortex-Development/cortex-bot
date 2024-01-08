package dev.cortex.cortexbot.commands;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import org.springframework.stereotype.Component;

@Component
public class KotlinTutCommand extends SlashCommand {

    public KotlinTutCommand() {
        this.name = "kotlintutotials";
        this.category = new Category("Programming");
        this.help = "Get a link to Kody's Ultimate Kotlin Tutorial";
    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.reply("External libraries are not always a good way to get familiar with Kotlin as so we strongly advise you to watch Kody's Kotlin tutorials before getting into Spigot or whatever else. You can find them [here](https://www.youtube.com/playlist?list=PLfu_Bpi_zcDMlkKLdIeHo3ATSln1gAh8a).").queue();
    }
}
