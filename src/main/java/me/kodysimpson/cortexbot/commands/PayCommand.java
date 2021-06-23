package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.services.LoggingService;
import me.kodysimpson.cortexbot.services.MemberUserService;
import net.dv8tion.jda.api.entities.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PayCommand extends Command {

    private final MemberRepository memberRepository;
    private final LoggingService loggingService;
    private final MemberUserService memberUserService;

    @Autowired
    public PayCommand(MemberRepository memberRepository, LoggingService loggingService, MemberUserService memberUserService){
        this.memberRepository = memberRepository;
        this.loggingService = loggingService;
        this.memberUserService = memberUserService;
        this.name = "pay";
        this.arguments = "<user id | name | tag> <# of points>";
        this.help = "give your points to someone else";
    }

    @Override
    protected void execute(CommandEvent event) {

        String args = event.getArgs();

            if (args.isEmpty()){
                event.reply("Provide a person to give points to. Ex: $pay 250856681724968960 100");
            }else{

                String[] arguments = args.split(" ");

                if (arguments.length == 1){
                    event.reply("An amount of points must be provided. Ex: $pay 250856681724968960 100");
                }else{

                    String providedUserIdentifier = arguments[0];

                    //determine who was provided as an argument to this command
                    User user = memberUserService.findUser(providedUserIdentifier);

                    if (user == null){
                        event.reply("The user provided does not exist.");
                    }else{

                        //see if they are trying to give points to themself
                        if (user.getId().equals(event.getMember().getId()) && !event.isOwner()){
                            event.reply("You can't give points to yourself dummy.");
                            return;
                        }

                        Member recipient = memberRepository.findByUserIDIs(user.getId());
                        Member payee = memberRepository.findByUserIDIs(event.getMember().getId());

                        if (recipient != null){

                            try{

                                int points = Integer.parseInt(arguments[1]);

                                if (points <= 0){
                                    event.reply("You need to provide a positive number of points.");
                                    return;
                                }

                                if (payee.getPoints() >= points){
                                    recipient.setPoints(recipient.getPoints() + points);
                                    memberRepository.save(recipient);

                                    //take the points away from the payee
                                    payee.setPoints(payee.getPoints() - points);

                                    event.reply(points + " point(s) have been given to " + user.getName() + ". You now have a total of " + payee.getPoints() + " point(s).");

                                    //log the points payed
                                    loggingService.logPointsPayed(user.getName(), points, event.getMember().getEffectiveName());

                                    user.openPrivateChannel().flatMap(channel -> {
                                        return channel.sendMessage("You have been given " + points + " points by " + event.getMember().getEffectiveName() + ". " +
                                                "You now have a total of " + recipient.getPoints() + " community points.");
                                    }).queue();
                                }else{
                                    event.reply("You do not have " + points + " point(s).");
                                }


                            }catch (NumberFormatException ex){
                                event.reply("Unable to process request, invalid points value provided.");
                            }

                        }else{
                            event.reply("The user provided does not exist in our database.");
                        }

                    }

                }

            }



    }
}
