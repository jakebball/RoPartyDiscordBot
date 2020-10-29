package com.discord.main;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;
import java.awt.*;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;


public class Main extends ListenerAdapter {

    private static Config config;
    private static JDA jda;
    private static int BugReports = 1;
    private static int ExploitReports = 1;
    private static ArrayList<User> alreadyFilling = new ArrayList<>();

    public static void main(String[] args) throws LoginException, InterruptedException, IOException {
        config = new Config();
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
                System.out.println(alreadyFilling.toString());
                if(!alreadyFilling.contains(event.getAuthor())){
                    event.getMessage().getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + ">, bug report will continue in dms").complete();
                    BugReport bugReport = new BugReport(event.getAuthor(), jda, event.getGuild());
                    Thread bugThread = new Thread(bugReport);
                    bugThread.start();
                    alreadyFilling.add(event.getAuthor());
                }
                break;
            case "studio":
                event.getMessage().getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + ">, https://www.roblox.com/groups/4946462/Surfer-Studios#!/about").complete();
                break;
            case "group":
                event.getMessage().getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + ">, https://www.roblox.com/groups/7786786/RoParty#!/about").complete();
                break;
            case "merch":
                event.getMessage().getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + ">, Coming Soon :sunglasses:").complete();
                break;
            case "game":
                event.getMessage().getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + ">, Merch Coming Soon :sunglasses:").complete();
                break;
            case "exploitreport":
                event.getMessage().getChannel().sendMessage("<@" + event.getAuthor().getIdLong() + ">, exploit report will continue in dms").complete();
                ExploitReport exploitReport = new ExploitReport(event.getAuthor(), jda, event.getGuild());
                Thread exploitThread = new Thread(exploitReport);
                exploitThread.start();
                break;
            case "rohelp":
                EmbedBuilder embedBuilder = new EmbedBuilder();
                embedBuilder.setAuthor("Commands");
                embedBuilder.addField("!ro", "Party! \uD83C\uDF89", false);
                embedBuilder.addField("!group", "Shows a link to the official RoParty group", false);
                embedBuilder.addField("!studio", "Shows a link to the studio behind RoParty", false);
                embedBuilder.addField("!game", "Shows a link to the official game, RoParty!", false);
                embedBuilder.addField("!exploitreport", "coming soon", false);
                embedBuilder.addField("!bugreport", "files a  bug report", false);
                embedBuilder.addField("!rohelp", "A list of all the current commands", false);
                embedBuilder.setColor(Color.RED);
                event.getMessage().getChannel().sendMessage(embedBuilder.build()).complete();
                break;
        }
    }

    class BugReport extends ListenerAdapter implements Runnable {

        private final User user;
        private final Guild guild;
        private ArrayList<String> questionsArray = new ArrayList<>();
        private ArrayList<String> questionAnswers = new ArrayList<>();
        private int currentIndex = 0;
        private final int timeoutLimit = 120;
        private boolean hasResponded = false;
        private boolean hasFinished = false;
        private PrivateChannel channel;

        public BugReport(User user, JDA jda, Guild guild) {
            jda.addEventListener(this);
            this.user = user;
            this.guild = guild;
            this.channel = user.openPrivateChannel().complete();

            questionsArray.add("Hi! \uD83D\uDC4B Thanks for filling a bug report. Please remember to make sure there is not already a report made in the #bug-reports channel. "
                    + "If you found one, tap the thumbs up so we know you also experienced it. If it has not been made before please answer the questions so we can help squash this bug. "
            + "You will have 3 minutes to answer each question or you can type cancel. Please state your roblox username.");
            questionsArray.add("please state your platform your experienced the bug on. PC, Mac, Xbox, Mobile, Etc.");
            questionsArray.add("please describe your bug in the best detail possible and what caused this to happen");

            askQuestion();
        }

        public boolean askQuestion(){
           if(currentIndex <= questionsArray.size() - 1){
               channel.sendMessage(questionsArray.get(currentIndex)).queue();
               currentIndex += 1;
               return false;
           } else {
               channel.sendMessage("Thanks for reporting the bug! You view your report in the the #bug-reports channel.").queue();
               createReport();
               jda.removeEventListener(this);
               Main.BugReports += 1;
               hasFinished = true;
               return true;
           }
        }

        @Override
        public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
            if(event.getAuthor().equals(user)){
                if(event.getMessage().getContentRaw().equalsIgnoreCase("cancel")){
                    hasFinished = true;
                    channel.sendMessage("Canceled Report").queue();
                } else {
                    questionAnswers.add(event.getMessage().getContentRaw());
                    askQuestion();
                    hasResponded = true;
                }
            }
        }

        public void createReport(){
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Bug Report #" + Main.BugReports);
            embedBuilder.addField("Roblox Username", questionAnswers.get(0), false);
            embedBuilder.addField("Platform", questionAnswers.get(1), false);
            embedBuilder.addField("Bug Description", questionAnswers.get(2), false);
            embedBuilder.setAuthor(user.getAsTag(), null, user.getAvatarUrl());
            embedBuilder.setColor(Color.RED);
            if(embedBuilder.isValidLength()){
                guild.getTextChannelById("769408576485458010").sendMessage("<@" +user.getIdLong() + ">").queue();
                guild.getTextChannelById("769408576485458010").sendMessage(embedBuilder.build()).queue(message -> message.addReaction("👍").queue());
            }
        }

        @Override
        public void run(){

            Thread thread = Thread.currentThread();

            for(int i=0;i<=timeoutLimit;i++){
                try {
                    thread.sleep(1000);
                } catch (Exception e) {}

                if(hasResponded){
                    i = 0;
                    hasResponded = false;
                }

                if(hasFinished) {
                    channel.close();
                    break;
                }
            }

            if(!hasFinished){
                jda.removeEventListener(this);
                channel.sendMessage("Report Timed Out").queue();
                channel.close();
            }

            alreadyFilling.remove(user);
        }
    }

    class ExploitReport extends ListenerAdapter implements Runnable{
        private final User user;
        private final Guild guild;
        private ArrayList<String> questionsArray = new ArrayList<>();
        private ArrayList<String> questionAnswers = new ArrayList<>();
        private int currentIndex = 0;
        private final int timeoutLimit = 120;
        private boolean hasResponded = false;
        private boolean hasFinished = false;

        public ExploitReport(User user, JDA jda, Guild guild) {
            jda.addEventListener(this);
            this.user = user;
            this.guild = guild;
            questionsArray.add("Hi! \uD83D\uDC4B Thanks for filling an exploit report. Please answer the questions truthfully and to the best of your ability. " +
                    "We take your exploit reports very seriously so if you are found to be lying you will receive a kick from the server. If you are kicked more then 2 times you will be permanently banned."
                    + "You will have 3 minutes to answer each question or you can type cancel. Please state your roblox username.");
            questionsArray.add("please state the roblox username of the suspected exploiter");
            questionsArray.add("please describe what happened");
            askQuestion();
        }

        public boolean askQuestion(){
            if(currentIndex <= questionsArray.size() - 1){
                user.openPrivateChannel().complete().sendMessage(questionsArray.get(currentIndex)).queue();
                currentIndex += 1;
                return false;
            } else {
                user.openPrivateChannel().complete().sendMessage("Thanks for reporting the exploit! Our moderation teams will look over the report you sent and investigate into the suspected exploiter."
                + " To protect the privacy of you and the exploiter, the exploit report will not be publicly visible").queue();
                createReport();
                jda.removeEventListener(this);
                Main.BugReports += 1;
                hasFinished = true;
                return true;
            }
        }

        @Override
        public void onPrivateMessageReceived(PrivateMessageReceivedEvent event) {
            if(event.getAuthor().equals(user)){
                System.out.println(event.getMessage().getContentRaw());
                questionAnswers.add(event.getMessage().getContentRaw());
                askQuestion();
                hasResponded = true;
            }
        }

        public void createReport(){
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Exploit Report #" + Main.ExploitReports);
            embedBuilder.addField("Reporter Roblox Username", questionAnswers.get(0), false);
            embedBuilder.addField("Exploiter Roblox Username", questionAnswers.get(1), false);
            embedBuilder.addField("Exploit Description", questionAnswers.get(2), false);
            embedBuilder.setAuthor(user.getAsTag(), null, user.getAvatarUrl());
            embedBuilder.setColor(Color.RED);
            if(embedBuilder.isValidLength()){
                guild.getTextChannelById("771192681899753483").sendMessage(embedBuilder.build()).queue();
            }
        }

        @Override
        public void run(){

            Thread thread = Thread.currentThread();

            for(int i=0;i<=timeoutLimit;i++){
                try {
                    thread.sleep(1000);
                } catch (Exception e) {}

                if(hasResponded){
                    i = 0;
                    hasResponded = false;
                }

                if(hasFinished) {
                    break;
                }
            }

            if(!hasFinished){
                jda.removeEventListener(this);
                user.openPrivateChannel().complete().sendMessage("Report Timed Out").queue();
            }
        }
    }
}
