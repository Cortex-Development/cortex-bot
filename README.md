# Cortex Bot

## About

The Cortex Bot is a Discord bot that is used to help manage the [Cortex Development](https://discord.gg/cortexdev) community Discord server.

It does things that discord does not already provide that help make the server experience better for everyone.

## Commands
- `/code` - Get information on how to properly post your code.
- `/leaderboard` - Get the top ten leaderboard rankings for points
- `/javatutorials` - Get a link to Kody's Ultimate Java Tutorial Series
- `/givepoints <user> <amount> [reason]` - **STAFF** - Give points to a member
- `/pay <user> <amount> [reason]` - Give your points to someone else
- `/points [user]` - See how many points you or someone else has
- `/set-points <user> <amount> [reason]` - **STAFF** - Set points for a member
- `/take-points <user> <amount> [reason]` - **STAFF** - Take points from a member
- `/thank <user> [amount] [reason]` - Thank someone for helping you on the server. Optionally tip them an amount of points.
- `/joke` - Get a Chuck Norris joke
- `/challenge create` - **STAFF** - Create a new challenge
- `/challenge end` - **STAFF** - End the ongoing challenge
- `/challenge finishgrading` - **STAFF** - Finish grading the ongoing challenge

## Contribution
We welcome contributions from the community! To contribute to this project, please follow these guidelines:

1. Fork the repository and create a feature branch named after what you’d like to add.
2. Edit the application.properties and application-dev.properties files
3. Code and commit your changes.
4. Push your changes to your fork.
5. Open a pull request to the master branch of this repository.
6. Wait for it to be reviewed and look out for any questions or comments we might have.

This application uses Spring Boot v3 and Java 17. For the database it uses MongoDB. When configuring the application-dev.properties file, put the URI for your mongodb database -- we recommend creating a *free* one on MongoDB Atlas.

For choosing what to work on, please refer to the issues tab of this repository. 
If you have any ideas that you would like to add, please create an issue and we will let you know what we think.

If you decide to work on something without there first being an issue, just be warned it might not be accepted if we do not find it needed for the community.

You can also come talk to us on our [Discord](https://discord.gg/cortexdev) server if you have any questions or just want to chat.

## Commands & Features

TODO: Add commands