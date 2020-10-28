package com.discord.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.util.ArrayList;


public class Main extends ListenerAdapter {

    private static Config config = new Config();
    private static JDA jda;
    private static int BugReports = 1;

    public static void main(String[] args) throws LoginException, InterruptedException{

        JDABuilder builder = JDABuilder.createLight(config.token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.playing("RoParty"));
        jda = builder.build();
        jda.awaitReady();
        jda.addEventListener(new Main());
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return;
        if(!event.getMessage().getContentRaw().startsWith(config.prefix)) return;
        String command = event.getMessage().getContentRaw().substring(config.prefix.length()).toLowerCase();

        switch(command){
            case "ro":
                event.getMessage().getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + ">, Party! \uD83C\uDF89").complete();
                break;
            case "bugreport":
                event.getMessage().getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + ">, bug report will continue in dms").complete();
                BugReport bugReport = new BugReport(event.getAuthor(), jda, event.getGuild());
                break;
            case "studio":
                event.getMessage().getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + ">, https://www.roblox.com/groups/4946462/Surfer-Studios#!/about").complete();
                break;
            case "group":
                event.getMessage().getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + ">, https://www.roblox.com/groups/7786786/RoParty#!/about").complete();
                break;
            case "game":
                event.getMessage().getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + ">, Coming Soon :sunglasses:").complete();
                break;
            case "exploitreport":
                event.getMessage().getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + ">, exploit report will continue in dms").complete();
                break;
            case "rohelp":
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setAuthor("Commands");
                embedBuilder.addField("!ro", "Party! \uD83C\uDF89", false);
                embedBuilder.addField("!group", "Shows a link to the official RoParty group", false);
                embedBuilder.addField("!studio", "Shows a link to the studio behind RoParty", false);
                embedBuilder.addField("!game", "Shows a link to the official game, RoParty!", false);
                embedBuilder.addField("!exploitreport", "coming soon", false);
                embedBuilder.addField("!bugreport", "coming soon", false);
                embedBuilder.addField("!rohelp", "A list of all the current commands", false);

                event.getMessage().getChannel().sendMessage(embedBuilder.build()).complete();
                break;
        }
    }

    class BugReport extends ListenerAdapter {

        private final User user;
        private final Guild guild;
        private ArrayList<String> questionsArray = new ArrayList<>();
        private ArrayList<String> questionAnswers = new ArrayList<>();
        private int currentIndex = 0;

        public BugReport(User user, JDA jda, Guild guild) {
            jda.addEventListener(this);
            this.user = user;
            this.guild = guild;
            questionsArray.add("Hi! \uD83D\uDC4B Thanks for filling a bug report. Please remember to make sure there is not already a report made in the #bug-reports channel. "
                    + "If you found one, check the reaction so we know you also experienced it. If it has not been made before please answer the questions so we can help squash this bug. "
            + "You will have 3 minutes to answer each question or you can type cancel. Please state your roblox username.");
            questionsArray.add("please state your platform your experienced the bug on. PC, Mac, Xbox, Mobile, Etc.");
            questionsArray.add("please describe your bug in the best detail possible");
            askQuestion();
        }

        public boolean askQuestion(){
           if(currentIndex <= questionsArray.size() - 1){
               user.openPrivateChannel().complete().sendMessage(questionsArray.get(currentIndex)).queue();
               currentIndex += 1;
               return false;
           } else {
               user.openPrivateChannel().complete().sendMessage("Thanks for reporting the bug! You view your report in the the #bug-reports channel.").queue();
               createReport();
               jda.removeEventListener(this);
               Main.BugReports += 1;
               return true;
           }
        }

        @Override
        public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
            if(event.getAuthor().equals(user)){
                questionAnswers.add(event.getMessage().getContentRaw());
                askQuestion();
            }
        }

        public void createReport(){
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Bug Report #" + Main.BugReports);
            embedBuilder.addField("Roblox Username", questionAnswers.get(0), false);
            embedBuilder.addField("Platform", questionAnswers.get(1), false);
            embedBuilder.addField("Description", questionAnswers.get(2), false);
            embedBuilder.setAuthor(user.getAsTag(), null, user.getAvatarUrl());
            embedBuilder.setColor(Color.RED);
            if(embedBuilder.isValidLength()){
                guild.getTextChannelById("769408576485458010").sendMessage("<@" +user.getIdLong() + ">").queue();
                guild.getTextChannelById("769408576485458010").sendMessage(embedBuilder.build()).queue();
            }
        }
    }
}
