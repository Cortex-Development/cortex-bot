package me.kodysimpson.cortexbot.commands.points;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.services.LoggingService;
import me.kodysimpson.cortexbot.services.MemberUserService;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TakePointsCommand extends Command {

    private MemberRepository memberRepository;
    private DiscordConfiguration discordConfiguration;
    private LoggingService loggingService;
    private MemberUserService memberUserService;

    public TakePointsCommand(){
        this.name = "take-points";
        this.arguments = "<user id | name | tag> <# of points>";
        this.help = "take points from a member";
    }

    @Override
    protected void execute(CommandEvent event) {

        String args = event.getArgs();

        if (event.getMember().isOwner() || event.getMember().getRoles().contains(event.getJDA().getRoleById(discordConfiguration.getStaffRole()))){

            if (args.isEmpty()){
                event.reply("Provide a person to take points from. Ex: $take-points 250856681724968960 100");
            }else{

                String[] arguments = args.split(" ");

                if (arguments.length == 1){
                    event.reply("An amount of points must be provided. Ex: $take-points 250856681724968960 100");
                }else{

                    String providedUserIdentifier = arguments[0];

                    //determine who was provided as an argument to this command
                    User user = memberUserService.findUser(providedUserIdentifier);

                    if (user == null){
                        event.reply("The user provided does not exist.");
                    }else{

                        //see if they are trying to give points to themself
                        if (user.getId().equals(event.getMember().getId()) && !event.isOwner()){
                            event.reply("You can't take points from yourself dummy.");
                            return;
                        }

                        Member member = memberRepository.findByUserIDIs(user.getId());

                        if (member != null){

                            try{

                                int points = Integer.parseInt(arguments[1]);
                                if (points <= 0){
                                    event.reply("You need to provide a positive number of points.");
                                    return;
                                }

                                member.setPoints(member.getPoints() - points);
                                memberRepository.save(member);

                                event.reply(points + " point(s) have been taken from " + user.getName() + ".");

                                //log the points given
                                loggingService.logPointsTaken(user.getName(), points, event.getMember().getEffectiveName());

                                user.openPrivateChannel().flatMap(channel -> {
                                    return channel.sendMessage(points + " points have been taken from you. " +
                                            "You now have a total of " + member.getPoints() + " community points.");
                                }).queue();
                            }catch (NumberFormatException ex){
                                event.reply("Unable to process request, invalid points value provided.");
                            }

                        }else{
                            event.reply("The user provided does not exist in our database.");
                        }

                    }

                }

            }

        }else{
            event.reply("You must be staff to execute this command.");
        }

    }

    @Autowired
    public void setLoggingService(LoggingService loggingService) {
        this.loggingService = loggingService;
    }

    @Autowired
    public void setMemberRepository(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    @Autowired
    public void setDiscordConfiguration(DiscordConfiguration discordConfiguration) {
        this.discordConfiguration = discordConfiguration;
    }
    @Autowired
    public void setMemberUserService(MemberUserService memberUserService) {
        this.memberUserService = memberUserService;
    }

}
