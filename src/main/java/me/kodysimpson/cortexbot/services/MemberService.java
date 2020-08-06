package me.kodysimpson.cortexbot.services;

import me.kodysimpson.cortexbot.bot.DiscordBotService;
import me.kodysimpson.cortexbot.model.Message;
import me.kodysimpson.cortexbot.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    @Autowired
    DiscordBotService discordBotService;

    @Autowired
    UserRepository userRepository;

    public String getUsername(String userId){

        return discordBotService.getApi().retrieveUserById(userId, true).complete().getAsTag();
    }

    public String getUsername(Message message){

        if (message.isDiscordMessage()){
            return getUsername(message.getDiscordUserID());
        }else{
            return userRepository.findById(message.getUserID()).get().getUsername();
        }

    }

}

