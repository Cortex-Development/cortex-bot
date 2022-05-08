package me.kodysimpson.cortexbot.commands.points;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.model.Thanked;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.repositories.ThankedRepository;
import me.kodysimpson.cortexbot.services.LoggingService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class ThankCommand extends SlashCommand {

    private final MemberRepository memberRepository;
    private final LoggingService loggingService;
    private final ThankedRepository thankedRepository;

    public ThankCommand(MemberRepository memberRepository, LoggingService loggingService, ThankedRepository thankedRepository) {
        this.memberRepository = memberRepository;
        this.loggingService = loggingService;
        this.thankedRepository = thankedRepository;
        this.name = "thank";
        this.help = "Thank someone for helping you on the server. Optionally tip them an amount of points.";

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "The person you are thanking", true));
        options.add(new OptionData(OptionType.INTEGER, "amount", "Amount of points to tip", false));
        options.add(new OptionData(OptionType.STRING, "reason", "Why are you thanking them", false));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        event.deferReply().queue();

        //determine who was provided as an argument to this command
        User user = event.getOption("user").getAsUser();

        Thanked thanked = new Thanked();
        thanked.setThankBy(event.getMember().getId());
        thanked.setWhen(new Date());
        thanked.setPersonThanked(user.getId());

        //get the reason if it was provided
        String reason = null;
        if (event.getOption("reason") != null){
            reason = event.getOption("reason").getAsString();
            thanked.setReason(reason);
            thanked.setReason(reason);
        }

        //see if they have already thanked this person recently
        //List<Thanked> thankedList = thankedRepository.findAllByPersonThankedEqualsAndThankByEquals(user.getId(), event.getMember().getId());

        //see if they are trying to give points to themself
        if (user.getId().equals(event.getMember().getId()) && !event.getMember().isOwner()) {
            event.getHook().sendMessage("You can't thank yourself dummy.").queue();
            return;
        }

        //Get the cortex Member objects of these people using their discord ID's
        Member recipient = memberRepository.findByUserIDIs(user.getId());
        Member payee = memberRepository.findByUserIDIs(event.getMember().getId());

        if (recipient != null) {

            int points = 0;
            if (event.getOption("amount") != null){
                points = (int) event.getOption("amount").getAsDouble();

                if (points <= 0) {
                    event.getHook().sendMessage("You need to provide a positive number of points.").setEphemeral(true).queue();
                    return;
                }

                thanked.setPoints(points);
            }

            //Make sure the person giving the points can afford it
            if (payee.getPoints() >= points) {

                //store the thank in the db
                thankedRepository.insert(thanked);

                //did they give any points?
                if (points == 0){
                    event.getHook().sendMessage("You have thanked " + user.getName() + ".").setEphemeral(true).queue();

                    String finalReason = reason;
                    user.openPrivateChannel().flatMap(channel -> {
                        return channel.sendMessage("You have been thanked by " + event.getMember().getEffectiveName() + "! " + ((finalReason != null) ? "Reason: " + finalReason : ""));
                    }).queue();
                }else{

                    //give the points to the recipient
                    System.out.println("recipient points: " + recipient.getPoints());
                    recipient.setPoints(recipient.getPoints() + points);
                    memberRepository.save(recipient);
                    System.out.println("recipient points: " + recipient.getPoints());

                    //take the points away from the payee
                    payee.setPoints(payee.getPoints() - points);
                    memberRepository.save(payee);

                    //log the points paid
                    loggingService.logPointsPayed(user.getName(), points, event.getMember().getEffectiveName());

                    event.getHook().sendMessage(points + " point(s) have been given to " + user.getName() + " and they have been thanked. You now have a total of " + payee.getPoints() + " point(s).").setEphemeral(true).queue();

                    int finalPoints = points;
                    String finalReason = reason;
                    user.openPrivateChannel().flatMap(channel -> {
                        return channel.sendMessage("You have been thanked by " + event.getMember().getEffectiveName() + " and also tipped " + finalPoints + " points! " +
                                        ((finalReason != null) ? "Reason: " + finalReason : "") + " \nYou now have a total of " + recipient.getPoints() + " community points.");
                    }).queue();

                }
            } else {
                event.getHook().sendMessage("You do not have " + points + " point(s).").setEphemeral(true).queue();
            }

        } else {
            event.getHook().sendMessage("The user provided does not exist in our database.").setEphemeral(true).queue();
        }

    }
}
