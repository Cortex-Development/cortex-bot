package me.kodysimpson.cortexbot.commands.etc;

import com.jagrosh.jdautilities.command.SlashCommand;
import com.jagrosh.jdautilities.command.SlashCommandEvent;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class VeteranCommand extends SlashCommand {

    private final MemberRepository memberRepository;

    @Autowired
    public VeteranCommand(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
        this.name = "veteran";
        this.help = "Assign the Veteran Role to someone manually.";
        List<OptionData> options = new ArrayList<>();
        options.add(new OptionData(OptionType.USER, "user", "The person involved", true));
        this.options = options;
    }

    @Override
    protected void execute(SlashCommandEvent event) {

        event.deferReply().queue();

        if (event.getMember().isOwner()) {

            if (event.getOptions().isEmpty()) {
                event.getHook().sendMessage("Provide a person to take points from. Ex: $take-points 250856681724968960 100").queue();
            } else {

                //determine who was provided as an argument to this command
                User user = event.getOption("user").getAsUser();

                Member member = memberRepository.findByUserIDIs(user.getId());

                if (member != null) {
                    memberRepository.save(member);
                    user.openPrivateChannel().flatMap(channel -> channel.sendMessage("You are now a Veteran on Cortex Development for your activity.")).queue();
                } else {
                    event.getHook().sendMessage("The user provided does not exist in our database.").queue();
                }
            }

        } else {
            event.getHook().sendMessage("You must be a Kody to execute this command.").queue();
        }

    }



}
