package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.utils.VersionUtil;

public class BuildCommand extends Command {

    private VersionUtil versionUtil;

    public BuildCommand(VersionUtil versionUtil) {
        this.name = "build.gradle";
        this.category = new Category("Programming");
        this.help = "Get the gradle JDA dependency";
        this.versionUtil = versionUtil;
    }


    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.reply(String.format("```gradle\ndependencies {\n" +
                "    compile 'net.dv8tion:%s'\n" +
                "}\n" +
                "\n" +
                "repositories {\n" +
                "    jcenter()\n" +
                "}```", versionUtil.getJDAVersion()));
                /*} else if(args[1].equalsIgnoreCase("maven")) {
                    commandEvent.getChannel().sendMessage(String.format("```xml\n<dependency>\n" +
                            "  <groupId>net.dv8tion</groupId>\n" +
                            "  <artifactId>JDA</artifactId>\n" +
                            "  <version>%s</version>\n" +
                            "  <type>pom</type>\n" +
                            "</dependency>```", version)).queue();
                }*/
        versionUtil.updateJDAVersion();
    }
}
