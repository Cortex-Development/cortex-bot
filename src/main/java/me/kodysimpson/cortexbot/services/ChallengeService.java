package me.kodysimpson.cortexbot.services;

import me.kodysimpson.cortexbot.model.challenges.Challenge;
import me.kodysimpson.cortexbot.model.challenges.Submission;
import me.kodysimpson.cortexbot.repositories.ChallengeRepository;
import me.kodysimpson.cortexbot.repositories.SubmissionRepository;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public void createSubmissionChannel(Interaction interaction){

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
        channel.createPermissionOverride(guild.getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue();
        channel.putPermissionOverride(role).setAllow(Permission.VIEW_CHANNEL).queue();
        channel.putPermissionOverride(member).setAllow(Permission.VIEW_CHANNEL).queue();

        Submission sm = new Submission();
        sm.setChannel(channel.getId());
        sm.setDate(System.currentTimeMillis());
        sm.setChallengeId(challenge.getId());
        sm.setUserid(member.getId());


        MessageBuilder messageBuilder = new MessageBuilder();

        messageBuilder.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH).append("\n");
        messageBuilder.append(member.getEffectiveName() + "'s Challenge Submission Channel", MessageBuilder.Formatting.BOLD).append("\n\n");
        messageBuilder.append("*For " + member.getEffectiveName() + "*: Post a link to your code submission here. Acceptable links include: Pastebin, Github(Gist or Repository), and Hastebin. Feel free to change your answer before the challenge ends.").append("\n");
        messageBuilder.append("\nThis channel will automatically lock when the challenge ends.").append("\n");
        messageBuilder.setActionRows(ActionRow.of(Button.danger("challenge-close-submission", "Delete Channel")));
        messageBuilder.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH);

        channel.sendMessage(messageBuilder.build()).complete();

        interaction.getHook().sendMessage("A submission channel has been created for you in #" + member.getEffectiveName()).setEphemeral(true).queue();

        loggingService.log("Created submission channel for " + member.getEffectiveName());

        submissionRepository.insert(sm);
    }

    public void closeSubmissionChannel(Interaction interaction){

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
            channel.createPermissionOverride(guild.getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue();
            channel.putPermissionOverride(role).setAllow(Permission.VIEW_CHANNEL).queue();

            //tell the member that their submission has been closed and will be looked at
            member.getUser().openPrivateChannel().queue(privateChannel -> {
                privateChannel.sendMessage("Your submission has been closed and will be looked at. Look out for an announcement on the results. Thank you for participating!").queue();
            });
        }

    }
//
//    public void closeBounty(Interaction interaction){
//
//        Guild guild = interaction.getGuild();
//        Member member = interaction.getMember();
//
//        //see if the bounty actually exists in the DB
//        if (bountyRepository.existsBountyByChannelIdEquals(interaction.getChannel().getId())){
//
//            //Get the bounty
//            Bounty bounty = bountyRepository.findBountyByChannelIdEquals(interaction.getChannel().getId());
//
//            //make sure the person who is trying to finish is the owner of the bounty or they are me
//            if (bounty.getUserId().equalsIgnoreCase(member.getId()) || member.isOwner()){
//
//                //mark the bounty as finished and save it
//                bounty.setFinished(true);
//                bountyRepository.save(bounty);
//
//                //send it for staff approval and grading
//                interaction.getTextChannel().getManager().setParent(guild.getCategoryById("786974851818192916")).complete();
//
//                //adjust the channel view permissions
//                Role role = guild.getRoleById("786974475354505248");
//                interaction.getTextChannel().createPermissionOverride(guild.getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue();
//                interaction.getTextChannel().putPermissionOverride(role).setAllow(Permission.VIEW_CHANNEL).queue();
//
//                //Send a message in the channel with instructions for staff
//                MessageBuilder builder = new MessageBuilder();
//                builder.allowMentions(Message.MentionType.ROLE)
//                        .append("""
//                        <@&786974475354505248> Check this conversation to see if anyone should be given points for helping the creator of this bounty. Do /done or click the button when done.
//                        """);
//                builder.setActionRows(ActionRow.of(Button.primary("grade-bounty", "Finished Grading")));
//                interaction.getTextChannel().sendMessage(builder.build()).queue();
//
//                interaction.getHook().sendMessage("Bounty finished.").queue();
//
//                //send a message to the bounty owner telling them what just happened
//                member.getUser().openPrivateChannel().queue(privateChannel -> {
//                    privateChannel.sendMessage("Your help bounty has been closed and transferred to Community Managers so that points can be awarded if someone helped you.").queue();
//                });
//
//            }else{
//                interaction.getHook().sendMessage("This isn't your bounty noob.").queue();
//            }
//        }
//
//    }
//
//    public void finishGrading(Interaction interaction){
//
//        Member member = interaction.getMember();
//        if (member.isOwner() || member.getRoles().contains(interaction.getJDA().getRoleById(discordConfiguration.getStaffRole()))){
//            if(bountyRepository.existsBountyByChannelIdEquals(interaction.getChannel().getId())){
//
//                Bounty bounty = bountyRepository.deleteBountyByChannelIdEquals(interaction.getChannel().getId());
//
//                //delete the channel
//                interaction.getTextChannel().delete().queue();
//
//                loggingService.log("Bounty help channel finished by " + member.getEffectiveName() + ". Bounty: " + bounty);
//            }else{
//                interaction.getHook().sendMessage("This isn't a bounty channel.").queue();
//            }
//        }else{
//            interaction.getHook().sendMessage("You cannot run this command.").queue();
//        }
//    }

}
