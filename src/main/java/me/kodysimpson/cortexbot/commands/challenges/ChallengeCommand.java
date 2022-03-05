package me.kodysimpson.cortexbot.commands.challenges;

import com.jagrosh.jdautilities.command.SlashCommand;
import me.kodysimpson.cortexbot.model.challenges.Challenge;
import me.kodysimpson.cortexbot.model.challenges.Submission;
import me.kodysimpson.cortexbot.repositories.ChallengeRepository;
import me.kodysimpson.cortexbot.repositories.SubmissionRepository;
import me.kodysimpson.cortexbot.services.ChallengeService;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ChallengeCommand extends SlashCommand {

    private final ChallengeRepository challengeRepository;
    private final ChallengeService challengeService;
    private final SubmissionRepository submissionRepository;

    @Autowired
    public ChallengeCommand(ChallengeRepository challengeRepository, ChallengeService challengeService, SubmissionRepository submissionRepository) {
        this.challengeRepository = challengeRepository;
        this.challengeService = challengeService;
        this.submissionRepository = submissionRepository;
        this.name = "challenge";
        this.help = "Manage the Cortex challenges";
        this.children = new SlashCommand[]{new Create(), new End()};
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
        //yo momma built like a whale
    }

    private class Create extends SlashCommand{

        public Create() {
            this.name = "create";
            this.help = "Create a new challenge";
            this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};

            List<OptionData> options = new ArrayList<>();
            options.add(new OptionData(OptionType.STRING, "name", "The name of the challenge").setRequired(true));
            options.add(new OptionData(OptionType.STRING, "description", "The description of the challenge announcement").setRequired(true));
            options.add(new OptionData(OptionType.STRING, "link", "Link to the challenge specification document.").setRequired(true));

            options.add(new OptionData(OptionType.STRING, "end", "When the challenge ends in epoch time").setRequired(true));
            options.add(new OptionData(OptionType.STRING, "reward", "When the challenge ends in epoch time").setRequired(true));

            this.options = options;
        }

        @Override
        protected void execute(SlashCommandEvent event) {

            //If there is already an ongoing challenge, don't allow a new one to be created
            if(challengeService.isChallengeOngoing()){
                event.reply("There is already an ongoing challenge. Please wait until the current challenge is over.").queue();
                return;
            }

            //Construct a new Challenge object with the given information
            String name = event.getOption("name").getAsString();
            String description = event.getOption("name").getAsString();
            String link = event.getOption("link").getAsString();

            long whenEnd = event.getOption("end").getAsLong();
            long reward = event.getOption("reward").getAsLong();

            Challenge challenge = new Challenge();
            challenge.setName(name);
            challenge.setDescription(description);
            challenge.setLink(link);
            challenge.setStartDate(new Date().getTime());
            challenge.setEndDate(whenEnd * 1000);
            challenge.setReward(reward);

            challengeRepository.insert(challenge);

            //Make the announcement
            //803777799353270293 <- challenges channel
            String announcement = event.getGuild().getRoleById("770425465063604244").getAsMention() + "\n\n" +
                    "NEW CODING CHALLENGE!: **\"" + challenge.getName() + "\"**\n\n" +
                    "**Description**: " + challenge.getDescription() + "\n" +
                    "**Link**: " + challenge.getLink() + "\n" +
                    "**Ends on**: <t:" + whenEnd + ":D>\n\n" +
                    "**Reward**: " + challenge.getReward() + " points\n\n" +
                    "Join by clicking the button below!\n";

            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.setContent(announcement);
            messageBuilder.setActionRows(ActionRow.of(Button.success("submit-challenge", "Submit Solution"), Button.primary("get-challenge-role", "Get Alerted for Future Challenges")));

            Message message = messageBuilder.build();
            event.getGuild().getTextChannelById("803777799353270293").sendMessage(message).queue();

            event.reply("The challenge has been created and posted in the Challenges channel.").setEphemeral(true).queue();

        }
    }

    private class End extends SlashCommand{

        public End() {
            this.name = "end";
            this.help = "End the ongoing challenge";
            this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        }

        @Override
        protected void execute(SlashCommandEvent event) {

            Challenge challenge = challengeService.getCurrentChallenge();

            //If there is already an ongoing challenge, don't allow a new one to be created
            if(challenge == null){
                event.reply("There is not an ongoing challenge.").queue();
                return;
            }

            challenge.setEndDate(System.currentTimeMillis());

            //get all submission channels for the challenge
            List<Submission> submissions = submissionRepository.findAllByChallengeIdEquals(challenge.getId());

            challengeService.lockSubmissionChannels(event.getGuild(), submissions);

            challengeRepository.save(challenge);

            //Announce the end of the challenge

            //Get all of the participants into a string list in bold
            StringBuilder participants = new StringBuilder();
            for(Submission submission : submissions){
                participants.append("**").append(event.getGuild().getMemberById(submission.getUserid()).getEffectiveName()).append("**, ");
            }

            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.setContent(event.getGuild().getRoleById("770425465063604244").getAsMention() + "\n\n" +
                    "The challenge **\"" + challenge.getName() + "\"** has ended.\n" +
                    "Participants: " + participants + "\n");
            event.getGuild().getTextChannelById("803777799353270293").sendMessage(messageBuilder.build()).queue();

            event.reply("The currently ongoing challenge has been ended.").setEphemeral(true).queue();

        }
    }
}


