package me.kodysimpson.cortexbot.commands.bounty;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.Bounty;
import me.kodysimpson.cortexbot.repositories.BountyRepository;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Objects;

@Component
public class CreateBountyCommand extends Command {

    private final BountyRepository bountyRepository;
    private final DiscordConfiguration discordConfiguration;

    public CreateBountyCommand(BountyRepository bountyRepository, DiscordConfiguration discordConfiguration){
        this.bountyRepository = bountyRepository;
        this.discordConfiguration = discordConfiguration;
        this.name = "create-bounty";
        this.help = "Create a new help bounty.";
    }

    @Override
    protected void execute(CommandEvent event) {

        if(bountyRepository.existsBountyByUserIdEquals(event.getMember().getId())){
            event.reply("You already have a bounty open.");
        }else{

            //create a new channel for this bounty
            TextChannel channel = event.getGuild().createTextChannel("Help Bounty by " + event.getMember().getEffectiveName())
                    .setParent(event.getGuild().getCategoryById("855667514092290048")).complete();

            Bounty bounty = new Bounty();
            bounty.setUserId(event.getMember().getId());
            bounty.setChannelId(channel.getId());
            bounty.setLastMessage(new Date());
            bountyRepository.insert(bounty);

            MessageBuilder messageBuilder = new MessageBuilder();

            messageBuilder.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH).append("\n");
            messageBuilder.append(event.getMember().getEffectiveName() + "'s Help Bounty", MessageBuilder.Formatting.BOLD).append("\n\n");
            messageBuilder.append("*For " + event.getMember().getEffectiveName() + "*: Describe the issue you need solving in as much detail as possible. Post the code snippets in good formatting so it can be read easily.").append("\n");
            messageBuilder.append("Once the issue has been solved, close the channel by clicking the green checkmark under this message.").append("\n");
            messageBuilder.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH);

            Message message = channel.sendMessage(messageBuilder.build()).complete();

            message.addReaction(Objects.requireNonNull(event.getGuild().getEmoteById(discordConfiguration.getGreenTickId()))).queue();

            event.reply("A help bounty has been created for you in #" + "Help Bounty by " + event.getMember().getEffectiveName());

        }

    }
}
