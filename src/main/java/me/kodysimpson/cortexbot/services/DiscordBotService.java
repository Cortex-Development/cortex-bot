package me.kodysimpson.cortexbot.services;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import lombok.RequiredArgsConstructor;
import me.kodysimpson.cortexbot.commands.*;
import me.kodysimpson.cortexbot.commands.ceo.CEOBidCommand;
import me.kodysimpson.cortexbot.commands.ceo.CEOBidListCommand;
import me.kodysimpson.cortexbot.commands.ceo.CEOCommand;
import me.kodysimpson.cortexbot.commands.staffcommands.GivePointsCommand;
import me.kodysimpson.cortexbot.config.DiscordConfiguration;
import me.kodysimpson.cortexbot.listeners.MessageListeners;
import me.kodysimpson.cortexbot.listeners.NewMemberListener;
import me.kodysimpson.cortexbot.model.Member;
import me.kodysimpson.cortexbot.repositories.MemberRepository;
import me.kodysimpson.cortexbot.repositories.UserRepository;
import me.kodysimpson.cortexbot.utils.VersionUtil;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RequiredArgsConstructor
@Service
public class DiscordBotService {

    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private final DiscordConfiguration discordConfiguration;

    @Autowired
    GivePointsCommand givePointsCommand;

    @Autowired
    CEOCommand ceoCommand;

    @Autowired
    CEOBidCommand ceoBidCommand;

    @Autowired
    CEOBidListCommand ceoBidListCommand;

    private static JDA api;

    @PostConstruct
    public void init() {
        try {
            VersionUtil versionUtil = new VersionUtil();
            CommandClientBuilder commandClient = new CommandClientBuilder()
                    .setPrefix("$")

                    .setOwnerId(discordConfiguration.getOwnerId())
                    .setHelpWord("help")
                    .setActivity(Activity.listening("$help"))

                    //Add commands
                    .addCommand(new LeaderboardCommand(memberRepository, this))
                    .addCommand(new WebsiteCommand())
                    .addCommand(new SuggestionCommand(discordConfiguration))
                    .addCommand(new CodeBlockCommand())
                    .addCommand(new JavaTutCommand())
                    .addCommand(new BuildCommand(versionUtil))
                    .addCommand(new PointsCommand(memberRepository, this))
                    .addCommand(givePointsCommand)
                    .addCommand(new PomCommand(versionUtil))
                    .addCommand(ceoCommand)
                    .addCommand(ceoBidCommand)
                    .addCommand(ceoBidListCommand);


            api = JDABuilder.create(List.of(GatewayIntent.GUILD_EMOJIS, GatewayIntent.GUILD_MEMBERS,
                    GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS))
                    .setToken(discordConfiguration.getBotToken())
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS)
                    .addEventListeners(commandClient.build())
                    .addEventListeners(new MessageListeners(memberRepository, discordConfiguration))
                    .addEventListeners(new NewMemberListener(this, discordConfiguration))
                    .setAutoReconnect(true)
                    .setBulkDeleteSplittingEnabled(false)
                    .build();

        } catch (LoginException e) {
            e.printStackTrace();
        }
    }

    public static JDA getApi() {
        return api;
    }

    public Guild getGuild() {
        return api.getGuildById(discordConfiguration.getGuildId());
    }

    public void addRoleToMember(net.dv8tion.jda.api.entities.Member member, long roleId) {
        try {
            Role role = member.getGuild().getRoleById(roleId);

            if (role != null) {
                getGuild().addRoleToMember(member, role).queueAfter(1, TimeUnit.MINUTES);
            }
        } catch (IllegalArgumentException | InsufficientPermissionException | HierarchyException e) {
            System.out.println(member.getUser().getAsTag() + " did not get the role on join");
            System.out.println(e);
        }
    }

    public void addRoleToMember(net.dv8tion.jda.api.entities.Member member, long roleId, Consumer<Void> successResponse) {
        try {
            Role role = member.getGuild().getRoleById(roleId);

            if (role != null) {
                getGuild().addRoleToMember(member, role).queueAfter(1, TimeUnit.MINUTES, successResponse);
            }
        } catch (IllegalArgumentException | InsufficientPermissionException | HierarchyException e) {
            System.out.println(member.getUser().getAsTag() + " did not get the role on join");
            System.out.println(e);
        }
    }

    /**
     * Will give the Regular role to top 20 on the leaderboard every 1 hour
     */
    @Scheduled(fixedRate = 3600000, initialDelay = 5000L)
    public void applyRegularRoles() {

        ArrayList<String> topTwenty = (ArrayList<String>) memberRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Member::getPoints).reversed())
                .limit(20)
                .map(Member::getUserID)
                .collect(Collectors.toList());

        //Remove the regular role from the current members if they are not top ten
        getGuild().getMembers()
                .forEach(member -> {
                    if (member.getRoles().contains(getApi().getRoleById(discordConfiguration.getRegularRoleId())) && !topTwenty.contains(member.getId())) {
                        getGuild().removeRoleFromMember(member, getApi().getRoleById(discordConfiguration.getRegularRoleId())).queue();
                    }
                });

        //apply it to the top 20 members
        topTwenty.stream()
                .map(id -> getGuild().getMemberById(id))
                .filter(Objects::nonNull)
                .forEach(member -> addRoleToMember(member, Long.valueOf(discordConfiguration.getRegularRoleId())));

    }

    /**
     * Will give the Veteran Coder role to top 5 on the leaderboard every 1 hour
     */
    @Scheduled(fixedRate = 3600000, initialDelay = 5000L)
    public void applyVeteranRoles() {

        //Veteran Exclusions
        List<String> veteranExclusions = List.of("143062289631805440", "142827015009992705", "251747460026859520", "190085967309307904", "339945333494775808");

        ArrayList<String> topFive = (ArrayList<String>) memberRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Member::getPoints).reversed())
                .limit(5)
                .map(Member::getUserID)
                .collect(Collectors.toList());

        //Remove the regular role from the current members if they are not top 5
        getGuild().getMembers()
                .forEach(member -> {
                    if (member.getRoles().contains(getApi().getRoleById(discordConfiguration.getRegularRoleId())) && !topFive.contains(member.getId()) && !veteranExclusions.contains(member.getId())) {
                        getGuild().removeRoleFromMember(member, getApi().getRoleById(discordConfiguration.getVeteranRoleId())).queue();
                    }
                });

        //apply it to the top 5 members
        topFive.stream()
                .map(id -> getGuild().getMemberById(id))
                .filter(Objects::nonNull)
                .forEach(member -> addRoleToMember(member, Long.valueOf(discordConfiguration.getVeteranRoleId())));

    }

    public static String getUsernameFromUserID(String userId){

        return getApi().retrieveUserById(userId, true).complete().getAsTag();
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
