package me.kodysimpson.cortexbot.commands.points;

import com.jagrosh.jdautilities.command.SlashCommand;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.services.LoggingService;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PayCommand extends SlashCommand {

    private final MemberRepository memberRepository;
    private final LoggingService loggingService;

    @Autowired
    public PayCommand(MemberRepository memberRepository, LoggingService loggingService) {
        this.memberRepository = memberRepository;
        this.loggingService = loggingService;
        this.name = "pay";
        this.help = "give your points to someone else";

        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "The person involved", true));
        options.add(new OptionData(OptionType.INTEGER, "amount", "amount of points to give", true));
        options.add(new OptionData(OptionType.STRING, "reason", "reason", false));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        event.deferReply().queue();

        //determine who was provided as an argument to this command
        User user = event.getOption("user").getAsUser();

        //see if they are trying to give points to themself
        if (user.getId().equals(event.getMember().getId()) && !event.getMember().isOwner()) {
            event.reply("You can't give points to yourself dummy.").queue();
            return;
        }

        Member recipient = memberRepository.findByUserIDIs(user.getId());
        Member payee = memberRepository.findByUserIDIs(event.getMember().getId());

        if (recipient != null) {

            int points = (int) event.getOption("amount").getAsDouble();

            if (points <= 0) {
                event.getHook().sendMessage("You need to provide a positive number of points.").queue();
                return;
            }

            if (payee.getPoints() >= points) {
                System.out.println("recipient points: " + recipient.getPoints());
                recipient.setPoints(recipient.getPoints() + points);
                memberRepository.save(recipient);
                System.out.println("recipient points: " + recipient.getPoints());

                //take the points away from the payee
                payee.setPoints(payee.getPoints() - points);
                memberRepository.save(payee);

                event.getHook().sendMessage(points + " point(s) have been given to " + user.getName() + ". You now have a total of " + payee.getPoints() + " point(s).").queue();

                //log the points payed
                loggingService.logPointsPayed(user.getName(), points, event.getMember().getEffectiveName());

                user.openPrivateChannel().flatMap(channel -> {
                    return channel.sendMessage("You have been given " + points + " points by " + event.getMember().getEffectiveName() + ". " +
                            "You now have a total of " + recipient.getPoints() + " community points.");
                }).queue();
            } else {
                event.getHook().sendMessage("You do not have " + points + " point(s).").queue();
            }

        } else {
            event.getHook().sendMessage("The user provided does not exist in our database.").queue();
        }
    }

}
