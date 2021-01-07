package me.kodysimpson.cortexbot.commands.staffcommands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.services.DiscordBotService;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

public class UnmuteCommand extends Command {

    private final DiscordConfiguration discordConfiguration;
    private final MemberRepository memberRepository;
    private final DiscordBotService discordBotService;

    public UnmuteCommand(DiscordConfiguration discordConfiguration, MemberRepository memberRepository, DiscordBotService discordBotService){
        this.discordConfiguration = discordConfiguration;
        this.discordBotService = discordBotService;
        this.memberRepository = memberRepository;
        this.name = "unmute";
        this.arguments = "<user id | name | tag>";
    }

    @Override
    protected void execute(CommandEvent event) {

        String args = event.getArgs();

        if (args.isEmpty()){
            event.reply("You did not provide a person to unmute ding-dong.");
        }else{

            String arg = event.getArgs();

            User user = discordBotService.findUser(arg);

            if (user == null){
                event.reply("The user provided does not exist.");
            }else{

                //since the user does exist, try and get them from our DB
                Member member = memberRepository.findByUserIDIs(user.getId());

                if (member == null){
                    event.reply("This member does not exist in our database.");
                }else{

                    //unmute the member
                    if (member.unmute()){

                        Role mutedRole = discordBotService.getGuild().getRoleById(discordConfiguration.getMuteRole());

                        net.dv8tion.jda.api.entities.Member discordMember = discordBotService.getGuild().getMemberById(member.getUserID());

                        if (discordMember != null && discordMember.getRoles().contains(mutedRole)){
                            discordBotService.getGuild().removeRoleFromMember(member.getUserID(), mutedRole).queue();
                        }

                        memberRepository.save(member);

                        event.reply(user.getAsTag() + " has been unmuted.");


                    }else{
                        event.reply("They are not muted, ding dong.");
                    }

                }

            }

        }

    }
}
