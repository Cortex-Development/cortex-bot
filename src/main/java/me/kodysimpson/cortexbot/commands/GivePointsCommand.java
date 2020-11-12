package me.kodysimpson.cortexbot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import net.dv8tion.jda.api.entities.User;

import java.util.List;
import java.util.stream.IntStream;

public class GivePointsCommand extends Command {

    private final MemberRepository memberRepository;
    private final DiscordConfiguration discordConfiguration;

    public GivePointsCommand(MemberRepository memberRepository, DiscordConfiguration discordConfiguration){
        this.memberRepository = memberRepository;
        this.discordConfiguration = discordConfiguration;
        this.name = "give-points";
        this.arguments = "<user id | name | tag> <# of points>";
    }

    @Override
    protected void execute(CommandEvent event) {

        String args = event.getArgs();

        if (event.getMember().isOwner() || event.getMember().getRoles().contains(event.getJDA().getRoleById(discordConfiguration.getStaffRole()))){

            if (args.isEmpty()){
                event.reply("Provide a person to give points to. Ex: $give-points 250856681724968960 100");
            }else{

                String[] arguments = args.split(" ");

                if (arguments.length == 1){
                    event.reply("An amount of points must be provided. Ex: $give-points 250856681724968960 100");
                }else{

                    String providedUser = arguments[0];

                    //determine who was provided as an argument to this command
                    User user;
                    if (args.startsWith("<@!")){
                        user = event.getJDA().getUserById(providedUser.substring(3, providedUser.length() - 1));
                    }else if (IntStream.range(0, providedUser.length()).boxed().map(providedUser::charAt).allMatch(Character::isDigit)){
                        user = event.getJDA().getUserById(providedUser);
                    }else{
                        List<User> users = event.getJDA().getUsersByName(providedUser, true);
                        if (!users.isEmpty()){
                            user = users.get(0);
                        }else{
                            user = null;
                        }
                    }

                    if (user == null){
                        event.reply("The user provided does not exist.");
                    }else{

                        Member member = memberRepository.findByUserIDIs(user.getId());

                        if (member != null){

                            String points = arguments[1];

                            try{
                                member.setPoints(member.getPoints() + Integer.parseInt(points));
                                memberRepository.save(member);

                                event.reply(points + " points has been given to " + user.getName() + ".");

                                user.openPrivateChannel().flatMap(channel -> {
                                    return channel.sendMessage("You have been given " + points + " points. " +
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
}