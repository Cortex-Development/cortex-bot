package dev.cortex.cortexbot.commands.points;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import dev.cortex.cortexbot.repositories.CortexMemberRepository;
import dev.cortex.cortexbot.services.LoggingService;
import dev.cortex.cortexbot.config.DiscordConfiguration;
import dev.cortex.cortexbot.model.CortexMember;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class GivePointsCommand extends SlashCommand {

    private CortexMemberRepository cortexMemberRepository;
    private DiscordConfiguration discordConfiguration;
    private LoggingService loggingService;

    public GivePointsCommand() {
        this.name = "give-points";
        this.help = "give points to a member";

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "The person involved", true));
        options.add(new OptionData(OptionType.INTEGER, "amount", "amount of points to give", true));
        options.add(new OptionData(OptionType.STRING, "reason", "reason", false));
        this.options = options;

    }

    @Override
    protected void execute(SlashCommandEvent event) {
        event.deferReply().queue();

        if (!event.getMember().isOwner() || !event.getMember().getRoles().contains(event.getJDA().getRoleById(discordConfiguration.getStaffRole()))) {
            event.getHook().sendMessage("You must be staff to execute this command.").setEphemeral(true).queue();
        }

        List<OptionMapping> options = event.getOptions();

        if (options.isEmpty()) {
            event.getHook().sendMessage("Provide a person to give points to. Ex: /give-points 250856681724968960 100").setEphemeral(true).queue();
            return;
        }

        if (options.size() == 1) {
            event.getHook().sendMessage("An amount of points must be provided. Ex: /give-points 250856681724968960 100").setEphemeral(true).queue();
            return;
        }

        //determine who was provided as an argument to this command
        User user = event.getOption("user").getAsUser();

        //see if they are trying to give points to themself
        if (user.getId().equals(event.getMember().getId()) && !event.getMember().isOwner()) {
            event.getHook().sendMessage("You can't give points to yourself dummy.").setEphemeral(true).queue();
            return;
        }

        CortexMember cortexMember = cortexMemberRepository.findByUserIDIs(user.getId());

        if (cortexMember == null) {
            event.getHook().sendMessage("The user provided does not exist in our database.").setEphemeral(true).queue();
        }

        int points = (int) event.getOption("amount").getAsDouble();

        if (points <= 0) {
            event.getHook().sendMessage("You need to provide a positive number of points.").setEphemeral(true).queue();
            return;
        }

        cortexMember.setPoints(cortexMember.getPoints() + points);
        cortexMemberRepository.save(cortexMember);

        event.getHook().sendMessage(points + " point(s) have been given to " + user.getName() + ".").queue();

        if (event.getOption("reason") == null) {
            //log the points given
            loggingService.logPointsGiven(user.getName(), points, event.getMember().getEffectiveName(), null);

            user.openPrivateChannel().flatMap(channel ->
                    channel.sendMessage("You have been given " + points + " points. " +
                    "You now have a total of " + cortexMember.getPoints() + " community points in Cortex Development.")).queue();
        } else {

            String reason = event.getOption("reason").getAsString();

            //log the points given
            loggingService.logPointsGiven(user.getName(), points, event.getMember().getEffectiveName(), reason);

            user.openPrivateChannel().flatMap(channel ->
                    channel.sendMessage("You have been given " + points + " points for \"" + reason + "\". " +
                    "You now have a total of " + cortexMember.getPoints() + " community points in Cortex Development.")).queue();
        }
    }


    @Autowired
    public void setLoggingService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Autowired
    public void setMemberRepository(CortexMemberRepository cortexMemberRepository) {
        this.cortexMemberRepository = cortexMemberRepository;
    }

    @Autowired
    public void setDiscordConfiguration(DiscordConfiguration discordConfiguration) {
        this.discordConfiguration = discordConfiguration;
    }
}
