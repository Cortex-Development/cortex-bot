package me.kodysimpson.cortexbot.services;

import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.Bounty;
import me.kodysimpson.cortexbot.repositories.BountyRepository;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.interactions.Interaction;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.Button;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class BountyService {

    private final BountyRepository bountyRepository;
    private final DiscordConfiguration discordConfiguration;
    private final LoggingService loggingService;

    @Autowired
    public BountyService(BountyRepository bountyRepository, DiscordConfiguration discordConfiguration, LoggingService loggingService) {
        this.bountyRepository = bountyRepository;
        this.discordConfiguration = discordConfiguration;
        this.loggingService = loggingService;
    }

    public void createNewBounty(Interaction interaction){

        //create a new channel for this bounty
        Guild guild = interaction.getGuild();
        Member member = interaction.getMember();

        if(bountyRepository.existsBountyByUserIdEquals(member.getId())){
            interaction.getHook().sendMessage("You already have a bounty open.").queue();
        }else{

            TextChannel channel = guild.createTextChannel("Help Bounty by " + member.getEffectiveName())
                    .setParent(guild.getCategoryById("503656779213963264")).complete();

            Bounty bounty = new Bounty();
            bounty.setUserId(member.getId());
            bounty.setChannelId(channel.getId());
            bounty.setLastMessage(new Date());
            bountyRepository.insert(bounty);

            MessageBuilder messageBuilder = new MessageBuilder();

            messageBuilder.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH).append("\n");
            messageBuilder.append(member.getEffectiveName() + "'s Help Bounty", MessageBuilder.Formatting.BOLD).append("\n\n");
            messageBuilder.append("*For " + member.getEffectiveName() + "*: Describe the issue you need solving in as much detail as possible. Post the code snippets in good formatting so it can be read easily.").append("\n");
            messageBuilder.append("Once the issue has been solved, close the channel by clicking the green button.").append("\n\n");
            messageBuilder.setActionRows(ActionRow.of(Button.success("done-bounty", "Done")));
            messageBuilder.append("---------------------------------------------------------------------------------------------", MessageBuilder.Formatting.STRIKETHROUGH);

            Message message = channel.sendMessage(messageBuilder.build()).complete();

            interaction.getHook().sendMessage("A help bounty has been created for you in #" + "Help Bounty by " + member.getEffectiveName()).queue();

        }

    }

    public void deleteBounty(Interaction interaction){

        Guild guild = interaction.getGuild();
        Member member = interaction.getMember();
        if(bountyRepository.existsBountyByUserIdEquals(member.getId())){

            Bounty bounty = bountyRepository.deleteBountyByUserIdEquals(member.getId());
            guild.getTextChannelById(bounty.getChannelId()).delete().complete();

            interaction.getHook().sendMessage("Bounty help channel deleted. You can now open a new bounty.").setEphemeral(true).queue();
        }else{
            interaction.getHook().sendMessage("You don't have a bounty currently open dummy.").setEphemeral(true).queue();
        }
    }

    public void closeBounty(Interaction interaction){

        Guild guild = interaction.getGuild();
        Member member = interaction.getMember();

        //see if the bounty actually exists in the DB
        if (bountyRepository.existsBountyByChannelIdEquals(interaction.getChannel().getId())){

            //Get the bounty
            Bounty bounty = bountyRepository.findBountyByChannelIdEquals(interaction.getChannel().getId());

            //make sure the person who is trying to finish is the owner of the bounty or they are me
            if (bounty.getUserId().equalsIgnoreCase(member.getId()) || member.isOwner()){

                //mark the bounty as finished and save it
                bounty.setFinished(true);
                bountyRepository.save(bounty);

                //send it for staff approval and grading
                interaction.getTextChannel().getManager().setParent(guild.getCategoryById("786974851818192916")).complete();

                //adjust the channel view permissions
                Role role = guild.getRoleById("786974475354505248");
                interaction.getTextChannel().createPermissionOverride(guild.getPublicRole()).setDeny(Permission.VIEW_CHANNEL).queue();
                interaction.getTextChannel().putPermissionOverride(role).setAllow(Permission.VIEW_CHANNEL).queue();

                //Send a message in the channel with instructions for staff
                MessageBuilder builder = new MessageBuilder();
                builder.allowMentions(Message.MentionType.ROLE)
                        .append("""
                        <@&786974475354505248> Check this conversation to see if anyone should be given points for helping the creator of this bounty. Do /done or click the button when done.
                        """);
                builder.setActionRows(ActionRow.of(Button.primary("grade-bounty", "Finished Grading")));
                interaction.getTextChannel().sendMessage(builder.build()).queue();

                interaction.getHook().sendMessage("Bounty finished.").queue();

                //send a message to the bounty owner telling them what just happened
                member.getUser().openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("Your help bounty has been closed and transferred to Community Managers so that points can be awarded if someone helped you.").queue();
                });

            }else{
                interaction.getHook().sendMessage("This isn't your bounty noob.").queue();
            }
        }

    }

    public void finishGrading(Interaction interaction){

        Member member = interaction.getMember();
        if (member.isOwner() || member.getRoles().contains(interaction.getJDA().getRoleById(discordConfiguration.getStaffRole()))){
            if(bountyRepository.existsBountyByChannelIdEquals(interaction.getChannel().getId())){

                Bounty bounty = bountyRepository.deleteBountyByChannelIdEquals(interaction.getChannel().getId());

                //delete the channel
                interaction.getTextChannel().delete().queue();

                loggingService.log("Bounty help channel finished by " + member.getEffectiveName() + ". Bounty: " + bounty);
            }else{
                interaction.getHook().sendMessage("This isn't a bounty channel.").queue();
            }
        }else{
            interaction.getHook().sendMessage("You cannot run this command.").queue();
        }
    }

}
