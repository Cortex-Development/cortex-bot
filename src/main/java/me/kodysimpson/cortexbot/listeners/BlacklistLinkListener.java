package me.kodysimpson.cortexbot.listeners;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class BlacklistLinkListener extends ListenerAdapter {

    public void onMessageReceived(MessageReceivedEvent event) {

        ArrayList<String> urls = extractURL(event.getMessage().getContentRaw());
        ArrayList<String> blockedContent = new ArrayList<>();

        // Path to your blacklist file
        File source = new File("blacklist.txt");
        Scanner scanner = null;
        try {
            scanner = new Scanner(source);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        while (scanner.hasNextLine())
            blockedContent.add(scanner.nextLine());

        for (String url : urls) {

            for (String blocked : blockedContent) {

                if (!url.contains("discord.gift") && !url.contains("github.com")) {

                    if (url.contains(blocked)) {

                        event.getMessage().delete().queue();

                        EmbedBuilder builder = new EmbedBuilder();

                        builder.setColor(Color.RED);
                        builder.setTitle("Compromised account detected");
                        builder.setDescription(event.getAuthor().getAsMention() + " sent a potentially harmful link. \nThe account is most likely to be compromised.");
                        builder.addField("User ID", event.getAuthor().getId(), false);
                        builder.addField("Tag", event.getAuthor().getAsTag(), false);
                        builder.addField("Link used", url, false);
                        builder.setThumbnail("https://cdn.discordapp.com/attachments/862904432606052362/898499607192031252/shield.png");

                        // Channel id to log and alert the staff
                        event.getGuild().getTextChannelById("739333112667963422").sendMessageEmbeds(builder.build()).queue();

                        break;

                    }

                }

            }

        }

    }

    public static ArrayList<String> extractURL(String string) {

        // Source
        // https://www.geeksforgeeks.org/extract-urls-present-in-a-given-string/

        ArrayList<String> list = new ArrayList<>();

        String regex
                = "\\b((?:https?|ftp|file):"
                + "//[-a-zA-Z0-9+&@#/%?="
                + "~_|!:, .;]*[-a-zA-Z0-9+"
                + "&@#/%=~_|])";

        Pattern p = Pattern.compile(
                regex,
                Pattern.CASE_INSENSITIVE);

        Matcher m = p.matcher(string);

        while (m.find()) {
            list.add(string.substring(
                    m.start(0), m.end(0)));
        }

        return list;

    }

}
