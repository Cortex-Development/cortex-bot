package me.kodysimpson.cortexbot.commands.jokes;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import me.kodysimpson.cortexbot.services.JokeService;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class JokeCommand extends SlashCommand {

    private final JokeService jokeService;

    @Autowired
    public JokeCommand(JokeService jokeService) {
        this.jokeService = jokeService;
        this.name = "joke";
        this.help = "Get a Chuck Norris joke.";
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        //Tell a chunk norris joke
//        try{
//            String joke = jokeService.getChuckNorrisJoke();
//
//            if (joke != null){
//
//               event.reply(joke).queue();
//
//            }
//
//        }catch (Exception e){
//            e.printStackTrace();
//        }

        Role role = event.getGuild().getRoleById("786974475354505248");
        event.getChannel()
                .asTextChannel()
                .getManager()
                .putRolePermissionOverride(event.getGuild().getPublicRole().getIdLong(), null, List.of(Permission.VIEW_CHANNEL))
                .queue(unused -> {
                    event.getChannel().asTextChannel().getManager().putRolePermissionOverride(role.getIdLong(), List.of(Permission.VIEW_CHANNEL), null).queue();
                    System.out.println("pickle lol");
                    event.getInteraction().reply("pickle lol").queue();
                });


    }

}
