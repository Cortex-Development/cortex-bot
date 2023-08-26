package dev.cortex.cortexbot.commands.challenges;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import dev.cortex.cortexbot.repositories.ChallengeRepository;
import dev.cortex.cortexbot.repositories.SubmissionRepository;
import dev.cortex.cortexbot.services.ChallengeService;
import dev.cortex.cortexbot.model.challenges.Challenge;
import dev.cortex.cortexbot.model.challenges.ChallengeStatus;
import dev.cortex.cortexbot.model.challenges.Submission;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.text.TextInput;
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle;
import net.dv8tion.jda.api.interactions.modals.Modal;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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
        this.children = new SlashCommand[]{new Create(), new End(), new FinishGrading()};
    }

    @Override
    protected void execute(SlashCommandEvent slashCommandEvent) {
    }

    private class Create extends SlashCommand {

        public Create() {
            this.name = "create";
            this.help = "Create a new challenge";
            this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        }

        @Override
        protected void execute(SlashCommandEvent event) {

            //If there is already an ongoing challenge, don't allow a new one to be created
            if (challengeService.isChallengeOngoing()) {
                event.reply("There is already an ongoing challenge. Please wait until the current challenge is over.").queue();
                return;
            }

            //Create a modal that asks for the challenge information
            TextInput name = TextInput.create("challenge-name", "Name", TextInputStyle.SHORT)
                    .setMinLength(1)
                    .setRequired(true)
                    .build();

            TextInput description = TextInput.create("challenge-desc", "Description", TextInputStyle.PARAGRAPH)
                    .setMinLength(10)
                    .setMaxLength(100)
                    .setRequired(true)
                    .build();

            TextInput link = TextInput.create("challenge-link", "Link", TextInputStyle.SHORT)
                    .setMinLength(1)
                    .setRequired(true)
                    .build();

            TextInput endTime = TextInput.create("challenge-end", "End Time(Epoch)", TextInputStyle.SHORT)
                    .setMinLength(1)
                    .setPlaceholder("in seconds you donut")
                    .setRequired(true)
                    .build();

            TextInput reward = TextInput.create("challenge-reward", "Reward Amount", TextInputStyle.SHORT)
                    .setMinLength(1)
                    .setRequired(true)
                    .build();

            Modal modal = Modal.create("new-challenge-modal", "New Challenge")
                    .addActionRows(ActionRow.of(name), ActionRow.of(description), ActionRow.of(link), ActionRow.of(endTime), ActionRow.of(reward))
                    .build();

            event.replyModal(modal).queue();
        }
    }

    private class End extends SlashCommand {

        public End() {
            this.name = "end";
            this.help = "End the ongoing challenge";
            this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        }

        @Override
        protected void execute(SlashCommandEvent event) {

            Challenge challenge = challengeService.getCurrentChallenge();

            //If there is already an ongoing challenge, don't allow a new one to be created
            if (challenge == null) {
                event.reply("There is not an ongoing challenge.").queue();
                return;
            }

            challenge.setEndDate(System.currentTimeMillis());
            challenge.setStatus(ChallengeStatus.NEEDS_GRADING);

            //get all submission channels for the challenge
            List<Submission> submissions = submissionRepository.findAllByChallengeIdEquals(challenge.getId());

            challengeService.lockSubmissionChannels(event.getGuild(), submissions);

            challengeRepository.save(challenge);

            //Announce the end of the challenge
            MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
            messageBuilder.setContent(event.getGuild().getRoleById("770425465063604244").getAsMention() + "\n\n" +
                    "The challenge **\"" + challenge.getName() + "\"** has ended.\n");
            event.getGuild().getTextChannelById("803777799353270293").sendMessage(messageBuilder.build()).queue();

            event.reply("The currently ongoing challenge has been ended.").setEphemeral(true).queue();

        }
    }

    private class FinishGrading extends SlashCommand {

        public FinishGrading() {
            this.name = "finishgrading";
            this.help = "Finish grading the ongoing challenge";
            this.userPermissions = new Permission[]{Permission.ADMINISTRATOR};
        }

        @Override
        protected void execute(SlashCommandEvent event) {

            Challenge challenge = challengeService.getCurrentUngradedChallenge();

            challengeService.finishChallenge(challenge, event.getGuild());

            event.reply("The challenge has finished.").setEphemeral(true).queue();

        }
    }
}


