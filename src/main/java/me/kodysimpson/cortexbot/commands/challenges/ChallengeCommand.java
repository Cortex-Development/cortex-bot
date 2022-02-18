package me.kodysimpson.cortexbot.commands.challenges;

import com.jagrosh.jdautilities.command.SlashCommand;
import me.kodysimpson.cortexbot.model.Challenge;
import me.kodysimpson.cortexbot.repositories.ChallengeRepository;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ChallengeCommand extends SlashCommand {

    private final ChallengeRepository challengeRepository;

    @Autowired
    public ChallengeCommand(ChallengeRepository challengeRepository) {
        this.challengeRepository = challengeRepository;
        this.name = "challenge";
        this.help = "Manage the Cortex challenges";
        this.children = new SlashCommand[]{new Create()};
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        //yo momma built like a whale
    }

    private class Create extends SlashCommand{

        public Create() {
            this.name = "create";
            this.help = "Create a new challenge";
//            this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "The name of the challenge").setRequired(true));
            options.add(new OptionData(OptionType.STRING, "description", "The description of the challenge announcement").setRequired(true));
            options.add(new OptionData(OptionType.STRING, "link", "Link to the challenge specification document.").setRequired(true));

            options.add(new OptionData(OptionType.NUMBER, "day", "What day the challenge ends").setRequired(true));
            options.add(new OptionData(OptionType.NUMBER, "month", "What month the challenge ends").setRequired(true));
            options.add(new OptionData(OptionType.NUMBER, "year", "What year the challenge ends").setRequired(true));

            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {

            //Construct a new Challenge object with the given information
            String name = event.getOption("name").getAsString();
            String description = event.getOption("name").getAsString();
            String link = event.getOption("link").getAsString();

            int endDay = (int) event.getOption("day").getAsDouble();
            int endMonth = (int) event.getOption("month").getAsDouble();
            int endYear = (int) event.getOption("year").getAsDouble();

            //make sure the endmonth is in the correct range
            if(endMonth < 1 || endMonth > 12){
                event.reply("The month must be between 1 and 12").queue();
            }

            //Get the Calendar enum from the endMonth
            Calendar calendar = Calendar.getInstance();
            calendar.set(endYear, endMonth - 1, endDay);

            Date endDate = calendar.getTime();

            //Get the date object from the given information using jdk 11+
//            Date endDate = new Date(endYear, Calendar, endDay);

            //get the epoch time from the date object
            long endEpoch = endDate.toInstant().toEpochMilli();

            Challenge challenge = new Challenge();
            challenge.setName(name);
            challenge.setDescription(description);
            challenge.setLink(link);
            challenge.setStartDate(new Date().getTime());
            challenge.setEndDate(endEpoch);

            challengeRepository.insert(challenge);

            //Make the announcement
            //803777799353270293 <- challenges channel
            String announcement = event.getGuild().getRoleById("770425465063604244").getAsMention() + "\n\n" +
                    "A new challenge has been created: **\"" + challenge.getName() + "\"**\n\n\n" +
                    "**Description**: " + challenge.getDescription() + "\n" +
                    "**Link**: " + challenge.getLink() + "\n" +
                    "**Ends on**: " + endDate.toString();
            event.getGuild().getTextChannelById("803777799353270293").sendMessage(announcement).queue();

            event.reply("The challenge has been created and posted in the Challenges channel.").setEphemeral(true).queue();
            //open a temporary channel so that the person making the challenge
            //can put the challenge description information
            //TextChannel channel = event.getGuild().createTextChannel("Help Bounty by " + event.getMember().getEffectiveName())
                    //.setParent(event.getGuild().getCategoryById("855667514092290048")).complete();

        }
    }
}


