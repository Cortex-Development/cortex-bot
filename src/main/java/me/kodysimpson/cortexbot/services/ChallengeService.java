package me.kodysimpson.cortexbot.services;

import me.kodysimpson.cortexbot.model.challenges.Challenge;
import me.kodysimpson.cortexbot.model.challenges.ChallengeGrade;
import me.kodysimpson.cortexbot.model.challenges.ChallengeStatus;
import me.kodysimpson.cortexbot.model.challenges.Submission;
import me.kodysimpson.cortexbot.repositories.ChallengeRepository;
import me.kodysimpson.cortexbot.repositories.SubmissionRepository;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonInteraction;
import net.dv8tion.jda.api.utils.messages.MessageCreateBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final SubmissionRepository submissionRepository;
    private final LoggingService loggingService;

    @Autowired
    public ChallengeService(ChallengeRepository challengeRepository, SubmissionRepository submissionRepository, LoggingService loggingService) {
        this.challengeRepository = challengeRepository;
        this.submissionRepository = submissionRepository;
        this.loggingService = loggingService;
    }

    //If there is an ongoing challenge, return true
    public boolean isChallengeOngoing() {

        for (Challenge challenge : challengeRepository.findAll()) {
            if(challenge.isActive()){
                return true;
            }
        }
        return false;
    }

    //If there is an active challenge, return it
    public Challenge getCurrentChallenge(){

        for (Challenge challenge : challengeRepository.findAll()) {
            if(challenge.isActive()){
                return challenge;
            }
        }
        return null;
    }

    public Challenge getCurrentUngradedChallenge(){

        for (Challenge challenge : challengeRepository.findAll()) {
            if(challenge.getStatus() == ChallengeStatus.NEEDS_GRADING){
                return challenge;
            }
        }
        return null;
    }

    public void createSubmissionChannel(ButtonInteraction interaction){

        //See if there is an ongoing challenge
        Challenge challenge = getCurrentChallenge();

        if(challenge == null){
            interaction.getHook().sendMessage("There is no active challenge.").setEphemeral(true).queue();
            return;
        }

        //create a new channel for this bounty
        Guild guild = interaction.getGuild();
        Member member = interaction.getMember();

        //See if the user already has a submission channel
        if(submissionRepository.existsSubmissionByUseridEqualsAndChallengeIdEquals(member.getId(), challenge.getId())){
            interaction.getHook().sendMessage("You already have a submission channel for this challenge.").setEphemeral(true).queue();
            return;
        }

        TextChannel channel = guild.createTextChannel(member.getEffectiveName())
                .setParent(guild.getCategoryById("803777453914456104")).complete();

        Role role = guild.getRoleById("786974475354505248");
        if (role == null){
            //send a message in the bounty saying that something went wrong
            interaction.getChannel().asTextChannel().sendMessage("An error occurred. Please try again later.").queue();
            return;
        }
        channel.getManager().putRolePermissionOverride(guild.getPublicRole().getIdLong(), null, List.of(Permission.VIEW_CHANNEL)).queueAfter(3, TimeUnit.SECONDS, unused -> {
            channel.getManager().putRolePermissionOverride(role.getIdLong(), List.of(Permission.VIEW_CHANNEL), null).queueAfter(3, TimeUnit.SECONDS, unused1 -> {
                channel.getManager().putMemberPermissionOverride(member.getIdLong(), List.of(Permission.VIEW_CHANNEL), null).queue();
            });
        });

        Submission sm = new Submission();
        sm.setChannel(channel.getId());
        sm.setDate(System.currentTimeMillis());
        sm.setChallengeId(challenge.getId());
        sm.setUserid(member.getId());
        sm.setStatus(ChallengeGrade.UNGRADED);

        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        messageBuilder.addContent("---------------------------------------------------------------------------------------------").addContent("\n");
        messageBuilder.addContent(member.getEffectiveName() + "'s Challenge Submission Channel").addContent("\n\n");
        messageBuilder.addContent("*For " + member.getEffectiveName() + "*: Post a link to your code submission here. Acceptable links include: Pastebin, Github(Gist or Repository), and Hastebin. Feel free to change your answer before the challenge ends.").addContent("\n");
        messageBuilder.addContent("\nThis channel will automatically lock when the challenge ends.").addContent("\n");
        messageBuilder.setActionRow(Button.danger("challenge-close-submission", "Delete Channel"));
        messageBuilder.addContent("---------------------------------------------------------------------------------------------");

        channel.sendMessage(messageBuilder.build()).complete();

        interaction.getHook().sendMessage("A submission channel has been created for you in #" + member.getEffectiveName()).setEphemeral(true).queue();

        loggingService.log("Created submission channel for " + member.getEffectiveName());

        submissionRepository.insert(sm);
    }

    public void closeSubmissionChannel(ButtonInteraction interaction){

        //Get the current challenge
        Challenge challenge = getCurrentChallenge();

        Guild guild = interaction.getGuild();
        Member member = interaction.getMember();

        Submission submission = submissionRepository.findSubmissionByUseridEqualsAndChallengeIdEquals(member.getId(), challenge.getId());

        guild.getTextChannelById(submission.getChannel()).delete().complete();

        submissionRepository.delete(submission);

        loggingService.log("Closed submission channel for " + member.getEffectiveName());

        interaction.getHook().sendMessage("Challenge Submission Channel deleted.").setEphemeral(true).queue();
    }

    public void lockSubmissionChannels(Guild guild, List<Submission> submissions){

        //close all submission channels
        for(Submission submission : submissions){

            Member member = guild.getMemberById(submission.getUserid());
            TextChannel channel = guild.getTextChannelById(submission.getChannel());

            //make it so that only staff can see the channel
            Role role = guild.getRoleById("786974475354505248");
            channel.getManager().putRolePermissionOverride(guild.getPublicRole().getIdLong(), null, List.of(Permission.VIEW_CHANNEL)).queue();
            channel.getManager().putRolePermissionOverride(role.getIdLong(), List.of(Permission.VIEW_CHANNEL), null).queue();

            MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
            messageBuilder.setContent("Your submission has been closed and will be looked at, look out for an announcement on the results. Thank you for participating!");
            messageBuilder.setActionRow(Button.success("challenge-grade-pass", "Passed"), Button.danger("challenge-grade-fail", "Failed"));

            //tell the member that their submission has been closed and will be looked at
            channel.sendMessage(messageBuilder.build()).queue();
        }

    }

    public void gradeSubmission(ButtonInteraction interaction, boolean pass){

        Submission submission = submissionRepository.findSubmissionByChannelEquals(interaction.getChannel().getId());

        //Get the challenge associated with this submission channel
        Challenge challenge = challengeRepository.findById(submission.getChallengeId()).get();

        if(submission.getStatus() != null && submission.getStatus() != ChallengeGrade.UNGRADED){

            interaction.getHook().sendMessage("This submission has already been graded.").setEphemeral(true).queue();

            return;
        }

        if(pass){
            submission.setStatus(ChallengeGrade.PASS);

            //Append the checkmark emoji to the channel name
            TextChannel channel = (TextChannel) interaction.getChannel();
            channel.getManager().setName(channel.getName() + " ✅").queue();

            submissionRepository.save(submission);

            interaction.getHook().sendMessage("Submission grade: " + ChallengeGrade.PASS).queue();

        }else{
            submission.setStatus(ChallengeGrade.FAIL);

            //Append the cross emoji to the channel name
            TextChannel channel = (TextChannel) interaction.getChannel();
            channel.getManager().setName(channel.getName() + " ❌").queue();

            submissionRepository.save(submission);

            interaction.getHook().sendMessage("Submission grade: " + ChallengeGrade.FAIL).queue();

        }

    }

    public void finishChallenge(Challenge challenge, Guild guild){

        //Finalize the state of the challenge and announce the final results
        challenge.setStatus(ChallengeStatus.GRADED);

        List<Submission> submissions = submissionRepository.findAllByChallengeIdEquals(challenge.getId());

        submissions.forEach(submission -> {

            //Get the channel
            TextChannel channel = guild.getTextChannelById(submission.getChannel());

            //Get the member/owner of the channel
            Member member = guild.getMemberById(submission.getUserid());

            //Give the user access to the channel
            channel.getManager().putMemberPermissionOverride(member.getIdLong(), List.of(Permission.VIEW_CHANNEL), null).queue();

            MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
            if(submission.getStatus() == ChallengeGrade.PASS){

                //Send a message to the channel that the submission was passed
                messageBuilder.setContent("Your submission has been passed! **Congratulations**! You have been awarded the full reward and a shiny new role.");

            }else{

                //Send a message to the channel that the submission was failed
                messageBuilder.setContent("Your submission has been failed, it is great that you tried! You have been awarded half of the reward.");

            }

            //Send the message to the channel
            channel.sendMessage(messageBuilder.build()).queue();

            //Tell them they can view the channel for 24 hours and then it will be deleted
            channel.sendMessage("...You can view this channel for *24 hours*, it will be deleted after...").queue();

            //channel.delete().queueAfter(24, TimeUnit.HOURS);
        });

        //Get a list of names of all of the people who have participated and winners
        StringBuilder participants = new StringBuilder();
        StringBuilder winners = new StringBuilder();
        for(Submission submission : submissions){
            participants.append("**").append(guild.getMemberById(submission.getUserid()).getEffectiveName()).append("**, ");

            if (submission.getStatus() == ChallengeGrade.PASS){
                winners.append("**").append(guild.getMemberById(submission.getUserid()).getEffectiveName()).append("**, ");
            }

        }

        //Announce the end of the challenge
        MessageCreateBuilder messageBuilder = new MessageCreateBuilder();
        messageBuilder.setContent(guild.getRoleById("770425465063604244").getAsMention() + "\n\n" +
                "Results for challenge **\"" + challenge.getName() + "\"**.\n" +
                "Participants: " + participants + "\n" +
                "Winners: " + winners + "\n");
        guild.getTextChannelById("803777799353270293").sendMessage(messageBuilder.build()).queue();

        challengeRepository.save(challenge);
    }

}
