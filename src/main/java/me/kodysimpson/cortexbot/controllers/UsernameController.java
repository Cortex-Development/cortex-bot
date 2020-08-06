package me.kodysimpson.cortexbot.controllers;

import me.kodysimpson.cortexbot.bot.DiscordBotService;
import net.dv8tion.jda.api.entities.Member;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bot")
public class UsernameController {

    @Autowired
    DiscordBotService api;

    @GetMapping("/member/{id}")
    public String getMember(@PathVariable String id){

        return api.getApi().retrieveUserById(id, true).complete().getAsTag();

    }

}
