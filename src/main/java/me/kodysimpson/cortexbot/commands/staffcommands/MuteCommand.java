package me.kodysimpson.cortexbot.commands.staffcommands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.model.infractions.Infraction;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.services.DiscordBotService;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MuteCommand extends Command {

    private final DiscordConfiguration discordConfiguration;
    private final MemberRepository memberRepository;
    private final DiscordBotService discordBotService;

    public MuteCommand(DiscordConfiguration discordConfiguration, MemberRepository memberRepository, DiscordBotService discordBotService){
        this.discordConfiguration = discordConfiguration;
        this.discordBotService = discordBotService;
        this.memberRepository = memberRepository;
        this.name = "mute";
        this.arguments = "<user id | name | tag> <duration: 10d5h400s> <reason>";
    }

    @Override
    protected void execute(CommandEvent event) {

        String args = event.getArgs();

        if (event.getMember().isOwner() || event.getMember().getRoles().contains(event.getJDA().getRoleById(discordConfiguration.getStaffRole()))){

            if (args.isEmpty()){
                event.reply("Provide a person to mute. Ex: $mute 250856681724968960 10d5m5h20min400s being too sexy");
            }else{

                String[] arguments = args.split(" ");

                String providedUserIdentifier = arguments[0];

                //determine who was provided as an argument to this command
                User user = discordBotService.findUser(providedUserIdentifier);

                if (user == null){
                    event.reply("The user provided does not exist.");
                }else{

                    //get the member in our database
                    Member member = memberRepository.findByUserIDIs(user.getId());

                    if (member != null){

                        try{

                            LocalDateTime expireDate = LocalDateTime.now();
                            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM dd, yyyy hh:mm:ss");
                            String duration = arguments[1];

                            expireDate = expireDate.plusYears(parseTime(duration, "y"));
                            expireDate = expireDate.plusMonths(parseTime(duration, "m"));
                            expireDate = expireDate.plusDays(parseTime(duration, "d"));
                            expireDate = expireDate.plusHours(parseTime(duration, "h"));
                            expireDate = expireDate.plusMinutes(parseTime(duration, "min"));
                            expireDate = expireDate.plusSeconds(parseTime(duration, "s"));

                            Infraction infraction;
                            String expireDateFormatted = expireDate.format(dateTimeFormatter);
                            if (arguments.length >= 3){

                                StringBuilder reason = new StringBuilder();
                                for (int i = 2; i < arguments.length; i++){
                                    reason.append(arguments[i]).append(" ");
                                }

                                infraction = new Infraction(expireDate, reason.toString());

                                user.openPrivateChannel().flatMap(channel -> {
                                    return channel.sendMessage("You have been muted for " + duration + ". " +
                                            "The mute will expire: " + expireDateFormatted
                                            + "\nReason: ( " + reason.toString() + ")");
                                }).queue();

                                event.reply(user.getName() + " has been muted and will be unmuted on " + expireDateFormatted);
                                event.reply("Reason: (" + reason.toString() + ")");
                            }else{
                                infraction = new Infraction(expireDate);

                                user.openPrivateChannel().flatMap(channel -> {
                                    return channel.sendMessage("You have been muted for " + duration + ". " +
                                            "The mute will expire: " + expireDateFormatted);
                                }).queue();

                                event.reply(user.getName() + " has been muted and will be unmuted on " + expireDateFormatted);
                            }
                            member.addMute(infraction);
                            memberRepository.save(member);
                            Role mutedRole = discordBotService.getGuild().getRoleById(discordConfiguration.getMuteRole());
                            discordBotService.getGuild().addRoleToMember(member.getUserID(), mutedRole).queue();

                        }catch (NumberFormatException ex){
                            event.reply("Unable to process request, invalid arguments provided.");
                        }

                    }else{
                        event.reply("The user provided does not exist in our database.");
                    }

                }

            }

        }else{
            event.reply("You must be staff to execute this command.");
        }

    }

    public static int parseTime(String s, String c) {
        if (s.indexOf(c)==-1) return 0;
        int count=0;
        for (int i=s.indexOf(c); i<s.length(); ++i) {
            if (!Character.isDigit(s.charAt(i))) count++;
            else break;
        }
        if (count!=c.length()) return 0;
        int index=0;
        for (int i=s.indexOf(c)-1; i>=0; i--) {
            if (!Character.isDigit(s.charAt(i))) {
                index=i+1;break;
            }
        }
        return Integer.parseInt(s.substring(index,s.indexOf(c)));
    }

}
