package me.kodysimpson.cortexbot.commands.jokes;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import me.kodysimpson.cortexbot.services.JokeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        try{
            String joke = jokeService.getChuckNorrisJoke();

            if (joke != null){

               event.reply(joke).queue();

            }

        }catch (Exception e){
            e.printStackTrace();
        }

    }

}
