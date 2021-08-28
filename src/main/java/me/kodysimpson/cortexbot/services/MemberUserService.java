package me.kodysimpson.cortexbot.services;

import net.dv8tion.jda.api.entities.User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.IntStream;

@Service
public class MemberUserService {

    /**
     * @param identifier Can be a user id, username, or tag
     * @return null if no user found or the found User
     */
    public User findUser(String identifier){

        User user;
        if (identifier.startsWith("<@")){
            identifier = identifier.replace("<@", "").replace("!","").replace(">", "");
            user = DiscordBotService.getApi().getUserById(identifier);
        }else if (IntStream.range(0, identifier.length()).boxed().map(identifier::charAt).allMatch(Character::isDigit)){
            user = DiscordBotService.getApi().getUserById(identifier);
        }else{
            List<User> users = DiscordBotService.getApi().getUsersByName(identifier, true);
            if (!users.isEmpty()){
                user = users.get(0);
            }else{
                user = null;
            }
        }
        return user;
    }

}
