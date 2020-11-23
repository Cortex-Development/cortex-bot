package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.utils.VersionUtil;
import net.dv8tion.jda.api.MessageBuilder;

public class PomCommand extends Command {

    private VersionUtil versionUtil;

    public PomCommand(VersionUtil versionUtil){
        this.name = "pom.xml";
        this.category = new Category("Programming");
        this.help = "Get the maven JDA dependency";
        this.versionUtil = versionUtil;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

        commandEvent.reply(String.format("```xml\n<dependency>\n" +
                "  <groupId>net.dv8tion</groupId>\n" +
                "  <artifactId>JDA</artifactId>\n" +
                "  <version>%s</version>\n" +
                "  <type>pom</type>\n" +
                "</dependency>```", versionUtil.getJDAVersion()));
        versionUtil.updateJDAVersion();
    }
}
