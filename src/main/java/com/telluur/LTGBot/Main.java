package com.telluur.LTGBot;


import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.telluur.LTGBot.commands.user.PingCmd;
import com.telluur.LTGBot.config.Config;
import com.telluur.LTGBot.config.ConfigLoader;
import com.vdurmont.emoji.EmojiParser;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

/**
 * Entry point of the Looking-to-game bot
 *
 * @author Rick Fontein
 */
public class Main {
    static final Logger logger = LoggerFactory.getLogger("SYSTEM");

    public static void main(String[] args) {
        logger.info("Starting up");
        logger.info("Loading config.yaml");
        Config config = ConfigLoader.loadYAML();

        logger.info("Bot start");
        LTGBot ltgBot = new LTGBot(config);

        logger.info("Building commands");
        CommandClientBuilder cmdBuilder = new CommandClientBuilder();
        cmdBuilder.setOwnerId(config.getOwner());
        cmdBuilder.setPrefix(config.getPrefix());
        cmdBuilder.setAlternativePrefix(config.getAltprefix());
        cmdBuilder.setGame(Game.playing(EmojiParser.parseToUnicode("with traffic :car:")));
        cmdBuilder.addCommands(
                new PingCmd()
        );
        CommandClient cmdClient = cmdBuilder.build();

        logger.info("Building JDA client and logging in");
        try {
            String token = config.getToken();
            JDA jda = new JDABuilder()
                    .setToken(token)
                    .addEventListener(cmdClient)
                    .setAudioEnabled(false)
                    .setGame(Game.playing(EmojiParser.parseToUnicode("with myself")))
                    .build();
            ltgBot.finishBot(jda);
        } catch (LoginException e) {
            logger.error("Failed to login", e.getCause());
            shutdown("caught exception");
        } catch (IllegalArgumentException e) {
            logger.error("Malformed config file", e.getCause());
            shutdown("caught exception");
        }
    }

    public static void shutdown() {
        logger.info("Shutting down");
        System.exit(1);
    }

    public static void shutdown(String reason) {
        logger.info(String.format("Shutting down (%s)", reason));
        System.exit(1);
    }
}