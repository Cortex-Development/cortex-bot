package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.api.EmbedBuilder;
import org.springframework.stereotype.Component;

@Component
public class CodeBlockCommand extends Command {

    public CodeBlockCommand(){
        this.name = "code";
        this.aliases = new String[]{"codeblock"};
        this.category = new Category("Programming");
        this.help = "Get information on how to properly post your code";
    }


    @Override
    protected void execute(CommandEvent commandEvent) {
        commandEvent.reply(new EmbedBuilder()
                .setColor(commandEvent.getGuild().getSelfMember().getColorRaw())
                .setTitle("Code Blocks")
                .setDescription("Put your code in code blocks!")
                .addField("Example", "\\`\\`\\`java\n" +
                        "public static void main(String[] args) {\n" +
                        "System.out.println(\"Hello World!\");\n" +
                        "}\n" +
                        "\\`\\`\\`", false)
                .addField("Becomes", "```java\n" +
                        "public static void main(String[] args) {\n" +
                        "    System.out.println(\"Hello World!\");\n" +
                        "}\n" +
                        "```", false)
                .addField("\u200b",
                        "Use [CortexDev](https://cortexdev.us/code) for larger segments of code!", false)
                .build());
    }
}
