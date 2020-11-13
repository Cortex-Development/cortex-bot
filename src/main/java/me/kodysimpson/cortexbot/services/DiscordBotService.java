package me.kodysimpson.cortexbot.services;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import lombok.RequiredArgsConstructor;
import me.kodysimpson.cortexbot.commands.*;
import me.kodysimpson.cortexbot.commands.staffcommands.GivePointsCommand;
import me.kodysimpson.cortexbot.commands.staffcommands.MuteCommand;
import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.listeners.MessageListener;
import me.kodysimpson.cortexbot.listeners.OtherListener;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.repositories.UserRepository;
import me.kodysimpson.cortexbot.utils.VersionUtil;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class DiscordBotService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final DiscordConfiguration discordConfiguration;

    private JDA api;

    @PostConstruct
    public void init() {
        try {
            VersionUtil versionUtil = new VersionUtil();
            CommandClientBuilder commandClient = new CommandClientBuilder()
                    .setPrefix("$")

                    .setOwnerId("250856681724968960")
                    .setHelpWord("help")
                    .setActivity(Activity.listening("$help"))

                    //Add commands
                    .addCommand(new LeaderboardCommand(memberRepository))
                    .addCommand(new WebsiteCommand())
                    .addCommand(new SuggestionCommand(discordConfiguration))
                    .addCommand(new CodeBlockCommand())
                    .addCommand(new JavaTutCommand())
                    .addCommand(new BuildCommand(versionUtil))
                    .addCommand(new PointsCommand(memberRepository, this))
                    .addCommand(new GivePointsCommand(memberRepository, discordConfiguration, this))
                    .addCommand(new MuteCommand(discordConfiguration, memberRepository, this))
                    .addCommand(new PomCommand(versionUtil));


            api = JDABuilder.create(List.of(GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS))
                    .setToken(discordConfiguration.getBotToken())
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS)
                    .addEventListeners(commandClient.build())
                    .addEventListeners(new MessageListener(memberRepository, discordConfiguration))
                    .addEventListeners(new OtherListener(this, discordConfiguration))
                    .setAutoReconnect(true)
                    .setBulkDeleteSplittingEnabled(false)
                    .build();

        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public JDA getApi() {
        return api;
    }

    public Guild getGuild() {
        return api.getGuildById(discordConfiguration.getGuildId());
    }

    public void addRoleToMember(net.dv8tion.jda.api.entities.Member member, long roleId) {
        try {
            Role role = member.getGuild().getRoleById(roleId);

            if (role != null) {
                getGuild().addRoleToMember(member, role).queue();
            }
        } catch (IllegalArgumentException | InsufficientPermissionException | HierarchyException e) {
            System.out.println(member.getUser().getAsTag() + " did not get the role on join");
            System.out.println(e);
        }
    }

    /**
     * Will give the Regular role to top ten on the leaderboard every 1 hour
     */
    @Scheduled(fixedRate = 3600000, initialDelay = 5000L)
    public void applyRegularRoles() {

        ArrayList<String> topTen = (ArrayList<String>) memberRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Member::getPoints).reversed())
                .limit(10)
                .map(Member::getUserID)
                .collect(Collectors.toList());

        //Remove the regular role from the current members if they are not top ten
        getGuild().getMembers()
                .forEach(member -> {
                    if (member.getRoles().contains(getApi().getRoleById(discordConfiguration.getRegularRoleId())) && !topTen.contains(member.getId())) {
                        getGuild().removeRoleFromMember(member, getApi().getRoleById(discordConfiguration.getRegularRoleId())).queue();
                    }
                });

        //apply it to the top ten members
        topTen.stream()
                .map(id -> getGuild().getMemberById(id))
                .filter(Objects::nonNull)
                .forEach(member -> addRoleToMember(member, Long.valueOf(discordConfiguration.getRegularRoleId())));

    }

    public String getUsername(String userId){

        return getApi().retrieveUserById(userId, true).complete().getAsTag();
    }

    public String getUsername(me.kodysimpson.cortexbot.model.Message message){

        if (message.isDiscordMessage()){
            return getUsername(message.getDiscordUserID());
        }else{
            return userRepository.findById(message.getUserID()).get().getUsername();
        }

    }

    /**
     * @param identifier Can be a user id, username, or tag
     * @return null if no user found or the found User
     */
    public User findUser(String identifier){

        User user;
        if (identifier.startsWith("<@!")){
            user = getApi().getUserById(identifier.substring(3, identifier.length() - 1));
        }else if (IntStream.range(0, identifier.length()).boxed().map(identifier::charAt).allMatch(Character::isDigit)){
            user = getApi().getUserById(identifier);
        }else{
            List<User> users = getApi().getUsersByName(identifier, true);
            if (!users.isEmpty()){
                user = users.get(0);
            }else{
                user = null;
            }
        }
        return user;
    }

}
