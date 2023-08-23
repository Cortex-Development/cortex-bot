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
public class SetPointsCommand extends SlashCommand {

    private final CortexMemberRepository cortexMemberRepository;
    private final DiscordConfiguration discordConfiguration;
    private final LoggingService loggingService;

    @Autowired
    public SetPointsCommand(CortexMemberRepository cortexMemberRepository, DiscordConfiguration discordConfiguration, LoggingService loggingService){
        this.cortexMemberRepository = cortexMemberRepository;
        this.discordConfiguration = discordConfiguration;
        this.loggingService = loggingService;
        this.name = "set-points";
        this.help = "set points for a member";

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "The person involved", true));
        options.add(new OptionData(OptionType.INTEGER, "amount", "amount of points to give", true));
        options.add(new OptionData(OptionType.STRING, "reason", "reason", false));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        event.deferReply().queue();

        if (event.getMember().isOwner() || event.getMember().getRoles().contains(event.getJDA().getRoleById(discordConfiguration.getStaffRole()))){

            List<OptionMapping> options = event.getOptions();

            if (options.isEmpty()){
                event.getHook().sendMessage("Provide a person. Ex: $set-points 250856681724968960 100").setEphemeral(true).queue();
            }else{

                if (options.size() == 1){
                    event.getHook().sendMessage("An amount of points must be provided. Ex: $set-points 250856681724968960 100").setEphemeral(true).queue();
                }else{

                    //determine who was provided as an argument to this command
                    User user = event.getOption("user").getAsUser();

                    //see if they are trying to give points to themself
                    if (user.getId().equals(event.getMember().getId()) && !event.getMember().isOwner()){
                        event.getHook().sendMessage("You can't set your own points dummy.").setEphemeral(true).queue();
                        return;
                    }

                    CortexMember cortexMember = cortexMemberRepository.findByUserIDIs(user.getId());

                    if (cortexMember != null){

                        try{

                            int points = (int) event.getOption("amount").getAsDouble();
                            if (points <= 0){
                                event.getHook().sendMessage("You need to provide a positive number of points.").setEphemeral(true).queue();
                                return;
                            }

                            cortexMember.setPoints(points);
                            cortexMemberRepository.save(cortexMember);

                            event.getHook().sendMessage(points + " point(s) have been set for " + user.getName() + ".").queue();

                            //log the points given
                            loggingService.logPointsSet(user.getName(), points, event.getMember().getEffectiveName());

                            user.openPrivateChannel().flatMap(channel -> {
                                return channel.sendMessage("You now have a total of " + cortexMember.getPoints() + " community points.");
                            }).queue();
                        }catch (NumberFormatException ex){
                            event.getHook().sendMessage("Unable to process request, invalid points value provided.").setEphemeral(true).queue();
                        }

                    }else{
                        event.getHook().sendMessage("The user provided does not exist in our database.").setEphemeral(true).queue();
                    }

                }

            }

        }else{
            event.getHook().sendMessage("You must be staff to execute this command.").setEphemeral(true).queue();
        }

    }

}
