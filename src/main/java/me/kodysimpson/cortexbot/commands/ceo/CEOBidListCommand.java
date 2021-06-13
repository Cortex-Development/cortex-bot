package me.kodysimpson.cortexbot.commands.ceo;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.model.CEOBid;
import me.kodysimpson.cortexbot.services.CEOService;
import me.kodysimpson.cortexbot.services.DiscordBotService;
import net.dv8tion.jda.api.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CEOBidListCommand extends Command {

    CEOService ceoService;

    DiscordBotService discordBotService;

    public CEOBidListCommand(){
        this.name = "ceo-bids";
        this.help = "view the top bids for CEO";
    }

    @Autowired
    public void setCeoService(CEOService ceoService) {
        this.ceoService = ceoService;
    }

    @Autowired
    public void setDiscordBotService(DiscordBotService discordBotService) {
        this.discordBotService = discordBotService;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

        MessageBuilder message = new MessageBuilder();

        List<CEOBid> bids = ceoService.getCurrentBids();

        message.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH).append("\n");
        message.append("Top 10 CEO Bids Currently", MessageBuilder.Formatting.BOLD).append("\n\n");

        int sentinel = 0;
        for (CEOBid bid : bids) {
            if (sentinel == 20){
                break;
            }
            String key = bid.getUserId();
            Integer value = bid.getPoints();
            message.append("(" + (sentinel + 1) + ") - ", MessageBuilder.Formatting.BOLD).append(discordBotService.getUsernameFromUserID(key) + " *-* " + value + " pts").append("\n");
            sentinel++;
        }

        message.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH);

        commandEvent.getChannel().sendMessage(message.build()).queue();

    }
}
