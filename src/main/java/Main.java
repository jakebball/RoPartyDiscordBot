import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import javax.security.auth.login.LoginException;

public class Main extends ListenerAdapter {

    private static Config config = new Config();
    private static JDA jda;

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDABuilder builder = JDABuilder.createDefault(config.token);
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
}
