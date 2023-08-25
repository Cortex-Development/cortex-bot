package dev.cortex.cortexbot.services;

import com.jagrosh.jdautilities.command.CommandClientBuilder;
import dev.cortex.cortexbot.commands.*;
import dev.cortex.cortexbot.commands.challenges.ChallengeCommand;
import dev.cortex.cortexbot.commands.jokes.JokeCommand;
import dev.cortex.cortexbot.commands.menu.HelpingMessageContextMenu;
import dev.cortex.cortexbot.commands.menu.ReportHelpContextMenu;
import dev.cortex.cortexbot.commands.points.*;
import dev.cortex.cortexbot.commands.points.menu.*;
import dev.cortex.cortexbot.config.DiscordConfiguration;
import dev.cortex.cortexbot.listeners.ButtonClickListener;
import dev.cortex.cortexbot.listeners.InteractionListener;
import dev.cortex.cortexbot.listeners.MessageListeners;
import dev.cortex.cortexbot.listeners.ModalListener;
import dev.cortex.cortexbot.model.CortexMember;
import dev.cortex.cortexbot.repositories.ChallengeRepository;
import dev.cortex.cortexbot.repositories.CortexMemberRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Lazy(false)
public class DiscordBot {

    private final CortexMemberRepository cortexMemberRepository;
    private final DiscordConfiguration discordConfiguration;
    private final GivePointsCommand givePointsCommand;
    private final PointsCommand pointsCommand;
    private final PayCommand payCommand;
    private final MessageListeners messageListeners;
    private final ModalListener modalListener;
    private final SuggestionCommand suggestionCommand;
    private final JavaTutCommand javaTutCommand;
    private final CodeBlockCommand codeBlockCommand;
    private final LeaderboardCommand leaderboardCommand;
    private final TakePointsCommand takePointsCommand;
    private final SetPointsCommand setPointsCommand;
    private final ThankCommand thankCommand;
    private final ButtonClickListener buttonClickListener;
    private final ChallengeCommand challengeCommand;
    private final JokeCommand jokeCommand;
    private final ClearNicknamesCommand clearNicknamesCommand;
    private final HelpingMessageContextMenu helpingMessageContextMenu;
    private final ReportHelpContextMenu reportHelpContextMenu;
    private final GivePointsContextMenu givePointsContextMenu;
    private final PayPointsContextMenu payPointsContextMenu;
    private final SetPointsContextMenu setPointsContextMenu;
    private final TakePointsContextMenu takePointsContextMenu;
    private final ThankContextMenu thankContextMenu;
    private final InteractionListener interactionListener;
    private final ChallengeRepository challengeRepository;

    private static JDA api;

    @PostConstruct
    public void init() {

        try {
            CommandClientBuilder commandClient = new CommandClientBuilder()
                    .setPrefix("/")
                    .setOwnerId(discordConfiguration.getOwnerId())
                    .setHelpWord("help")
                    .setActivity(Activity.listening("Pootin cant code"))
                    //Add commands
                    .addSlashCommand(leaderboardCommand)
                    .addSlashCommand(suggestionCommand)
                    .addSlashCommand(codeBlockCommand)
                    .addSlashCommand(javaTutCommand)
                    .addSlashCommand(pointsCommand)
                    .addSlashCommand(givePointsCommand)
                    .addSlashCommand(payCommand)
                    .addSlashCommand(takePointsCommand)
                    .addSlashCommand(setPointsCommand)
                    .addSlashCommand(thankCommand)
                    .addSlashCommand(clearNicknamesCommand).forceGuildOnly(discordConfiguration.getGuildId())
                    .addSlashCommand(challengeCommand).forceGuildOnly(discordConfiguration.getGuildId())
                    .addSlashCommand(jokeCommand).forceGuildOnly(discordConfiguration.getGuildId())
                    .addContextMenu(helpingMessageContextMenu)
                    .addContextMenu(reportHelpContextMenu)
                    .addContextMenu(givePointsContextMenu)
                    .addContextMenu(payPointsContextMenu)
                    .addContextMenu(setPointsContextMenu)
                    .addContextMenu(takePointsContextMenu)
                    .addContextMenu(thankContextMenu);

            api = JDABuilder.create(List.of(GatewayIntent.GUILD_EMOJIS_AND_STICKERS, GatewayIntent.GUILD_MEMBERS, GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS))
                    .setToken(discordConfiguration.getBotToken())
                    .disableCache(CacheFlag.ACTIVITY, CacheFlag.VOICE_STATE, CacheFlag.CLIENT_STATUS)
                    .addEventListeners(commandClient.build())
                    .addEventListeners(messageListeners)
                    .addEventListeners(buttonClickListener)
                    .addEventListeners(interactionListener)
                    .addEventListeners(modalListener)
                    .setAutoReconnect(true)
                    .setBulkDeleteSplittingEnabled(false)
                    .build().awaitReady();

            System.out.println("BOT STARTED SUCCESSFULLY");
            System.out.flush();

        } catch (InvalidTokenException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static JDA getApi() {
        return api;
    }

    public Guild getGuild() {
        return getApi().getGuildById(discordConfiguration.getGuildId());
    }

    public void addRoleToMember(net.dv8tion.jda.api.entities.Member member, long roleId) {
        try {
            Role role = member.getGuild().getRoleById(roleId);

            if (role != null) {
                member.getGuild().addRoleToMember(member, role).queueAfter(1, TimeUnit.MINUTES);
            }
        } catch (IllegalArgumentException | InsufficientPermissionException | HierarchyException e) {
            System.out.println(member.getUser().getAsTag() + " did not get the role on join");
            System.out.println(e);
        }
    }

    /**
     * Will give the Regular role to top 20 on the leaderboard every 1 hour
     */
    @Scheduled(cron = "0 0 * * * *")
    public void applyRegularRoles() {

        ArrayList<String> topTwenty = (ArrayList<String>) cortexMemberRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(CortexMember::getPoints).reversed())
                .limit(20)
                .map(CortexMember::getUserID)
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
    @Scheduled(cron = "0 0 * * * *")
    public void applyVeteranRoles() {

        ArrayList<String> topFive = (ArrayList<String>) cortexMemberRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(CortexMember::getPoints).reversed())
                .limit(5)
                .map(CortexMember::getUserID)
                .collect(Collectors.toList());

        //Remove the regular role from the current members if they are not top 5
        getGuild().getMembers()
                .forEach(member -> {
                    if (member.getRoles().contains(getApi().getRoleById(discordConfiguration.getRegularRoleId())) && !topFive.contains(member.getId())) {
                        getGuild().removeRoleFromMember(member, getApi().getRoleById(discordConfiguration.getVeteranRoleId())).queue();
                    }
                });

        //apply it to the top 5 members
        topFive.stream()
                .map(id -> getGuild().getMemberById(id))
                .filter(Objects::nonNull)
                .forEach(member -> addRoleToMember(member, Long.valueOf(discordConfiguration.getVeteranRoleId())));

    }

    public static String getUsernameFromUserID(String userId) {
        return getApi().retrieveUserById(userId).complete().getAsTag();
    }
}
