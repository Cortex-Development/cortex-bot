package me.kodysimpson.cortexbot.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.bot.DiscordConfiguration;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.requests.restaction.pagination.MessagePaginationAction;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TreeMap;

public class SuggestionCommand extends Command {

    private Message calcmessage;

    private final DiscordConfiguration discordConfiguration;

    public SuggestionCommand(DiscordConfiguration discordConfiguration) {
        this.name = "suggestions";
        this.aliases = new String[]{"topsuggestions", "topsug"};
        this.help = "Shows top 10 most upvoted suggestions";
        this.discordConfiguration = discordConfiguration;
    }

    @Override
    protected void execute(CommandEvent commandEvent) {
        TreeMap<Integer, ArrayList<Message>> map = new TreeMap<>();
        commandEvent.getChannel().sendMessage(":hourglass: Calculating").queue(m -> calcmessage = m);
        MessagePaginationAction mpa = commandEvent.getGuild().getTextChannelById(discordConfiguration.getSuggestionsChannelId()).getIterableHistory();
        // Loop over all messages and put the message in the treemap with the points as key (points == upvotes - downvotes)
        for (Message m : mpa) {
            int points = 0;
            for (MessageReaction reaction : m.getReactions()) {
                if (reaction.getReactionEmote().isEmote()) {
                    if (reaction.getReactionEmote().getIdLong() == discordConfiguration.getGreenTickId())
                        points += reaction.getCount(); // upvote
                    else if (reaction.getReactionEmote().getIdLong() == discordConfiguration.getRedTickId())
                        points -= reaction.getCount(); // Downvote

                }
            }
            ArrayList<Message> list = map.computeIfAbsent(points, (k) -> new ArrayList<>());
            list.add(m);
            map.put(points, list);
        }
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("Top 10 suggestions")
                .setColor(commandEvent.getGuild().getSelfMember().getColorRaw());
        StringBuilder sb = new StringBuilder();
        Iterator<Integer> it = map.descendingKeySet().iterator();
        int i = 0;
        // For the 10 most upvoted suggestions get the first 50 characters of the suggestion
        while (i < 10) {
            if (it.hasNext()) {
                Integer amount = it.next();
                ArrayList<Message> messagelist = map.get(amount);
                for (Message m : messagelist) {
                    i++;
                    String content = m.getEmbeds().size() > 0 ? m.getEmbeds().get(0).getDescription() : m.getContentRaw();
                    sb.append(amount >= 0 ? ":arrow_up_small: " : ":arrow_down_small: ").append(amount).append(": ")
                            .append(content.length() > 50 ? content.substring(0, 50) : content)
                            .append(content.length() > 50 ? "..." : "")
                            /*.append("    [jump](")
                            .append(m.getJumpUrl()).append(")"*/
                            .append("\n");
                }
            } else {
                i = 10;
            }
        }
        eb.setDescription(sb.toString().trim());
        commandEvent.reply(eb.build());
        calcmessage.delete().queue(); // Delete the "calculating" message
    }
}
