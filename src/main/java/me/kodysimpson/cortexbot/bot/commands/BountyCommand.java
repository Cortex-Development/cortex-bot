package me.kodysimpson.cortexbot.bot.commands;

import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;

public class BountyCommand extends Command {

    public BountyCommand() {
        this.name = "bounty";
        this.help = "Create and view active bounties";
        this.arguments = "new | view";
    }

    @Override
    protected void execute(CommandEvent commandEvent) {

        if (commandEvent.getArgs().equalsIgnoreCase("new")){
            commandEvent.getChannel().sendMessage("Create a new bounty at https://cortexdev.herokuapp.com/bounty/new").queue();
        }else{
            commandEvent.getChannel().sendMessage("View all active bounties here at https://cortexdev.herokuapp.com/bounty/list and #wanted").queue();
        }

    }
}
