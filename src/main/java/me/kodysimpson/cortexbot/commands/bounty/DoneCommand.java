package me.kodysimpson.cortexbot.commands.bounty;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.Bounty;
import me.kodysimpson.cortexbot.repositories.BountyRepository;
import me.kodysimpson.cortexbot.services.LoggingService;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.utils.Procedure;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

@Component
public class DoneCommand extends Command {

    public BountyRepository bountyRepository;
    private DiscordConfiguration discordConfiguration;
    private LoggingService loggingService;

    @Autowired
    public DoneCommand(BountyRepository bountyRepository, DiscordConfiguration discordConfiguration, LoggingService loggingService){
        this.discordConfiguration = discordConfiguration;
        this.loggingService = loggingService;
        this.name = "done";
        this.help = "Finish grading finished bounty channel";
        this.bountyRepository = bountyRepository;
    }

    @Override
    protected void execute(CommandEvent event) {

        if (event.getMember().isOwner() || event.getMember().getRoles().contains(event.getJDA().getRoleById(discordConfiguration.getStaffRole()))){
            if(bountyRepository.existsBountyByChannelIdEquals(event.getChannel().getId())){

                Bounty bounty = bountyRepository.findBountyByChannelIdEquals(event.getChannel().getId());
//                bounty.setFinished(true);
//                bountyRepository.save(bounty);

                MessageBuilder builder = new MessageBuilder();

                System.out.println("trying thing: " + bounty);
                event.getChannel().getIterableHistory().cache(false).forEachAsync(new Procedure<Message>() {
                    @Override
                    public boolean execute(@NotNull Message message) {
                        builder.append(message.getAuthor().getAsTag() + " : ").append(message).append("\n");
                        return true;
                    }
                }).whenComplete(new BiConsumer<Object, Throwable>() {
                    @Override
                    public void accept(Object o, Throwable throwable) {
                        event.getGuild().getTextChannelById("856772595294142475").sendMessage(builder.build()).queue();

                        event.getGuild().getTextChannelById(bounty.getChannelId()).delete().complete();

                        loggingService.log("Bounty help channel finished by " + event.getMember().getEffectiveName() + ". Bounty: " + bounty);
                    }
                });

//                event.getChannel().getIterableHistory().cache(false).forEach(message -> {
//                    builder.append(message.getAuthor().getAsTag() + " : ").append(message).append("\n");
//                });


            }else{

                event.reply("This isn't a bounty channel.");

            }
        }else{
            event.reply("You cannot run this command.");
        }
    }

}
