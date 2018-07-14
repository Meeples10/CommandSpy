package io.github.meeples10.commandlog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public final class Main extends JavaPlugin implements Listener {
    public static final String NAME = "CommandLog";

    private static String chatFormat = "";
    private static String chatPrefix = "";
    private static boolean enableNotifications = true;
    private static boolean enableFileLog = true;
    private static List<HistoryItem> commandHistory;
    private static boolean allowDisable;
    private static File df, cfg;
    private static Logger log;

    public void onEnable() {
        df = Bukkit.getServer().getPluginManager().getPlugin(NAME).getDataFolder();
        cfg = new File(df, "config.yml");
        log = Bukkit.getServer().getPluginManager().getPlugin(NAME).getLogger();
        getServer().getPluginManager().registerEvents(this, this);
        loadConfig();
        getCommand("commandlog").setExecutor(new CommandCL());
        commandHistory = new ArrayList<HistoryItem>();
    }

    public static void loadConfig() {
        Bukkit.getServer().getPluginManager().getPlugin(NAME).saveDefaultConfig();

        FileConfiguration config = YamlConfiguration.loadConfiguration(cfg);

        chatPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("chatprefix"));
        chatFormat = ChatColor.translateAlternateColorCodes('&', config.getString("chatformat"));

        allowDisable = config.getBoolean("allowdisable");

        int pIndex = -1;
        int cIndex = -1;
        if(chatFormat.length() > 0) {
            pIndex = chatFormat.indexOf("{Player}");
            cIndex = chatFormat.indexOf("{Command}");
        }
        if((pIndex < 0) || (cIndex < 0)) {
            log.warning("chatformat string must at least contain {Player} and {Command}");
            log.warning("Using default chatformat string now");

            chatFormat = config.getDefaults().getString("chatformat");
        }

        enableFileLog = config.getBoolean("filelog");
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if(!enableNotifications) {
            return;
        }
        String s = event.getMessage();
        if(s.length() == 0 || s.split(" ").length == 0) {
            return;
        }

        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            if(p.hasPermission("commandlog.notice")) {
                logCommandToOnlinePlayer(event, p);
            }
        }

        Player player = event.getPlayer();
        HistoryItem hi = new HistoryItem(s, player.getDisplayName(), new Date(), player.getLocation());
        commandHistory.add(hi);

        if(enableFileLog) {
            logCommandToFile(hi);
        }
    }

    private void logCommandToOnlinePlayer(PlayerCommandPreprocessEvent event, Player p) {
        Player player = event.getPlayer();
        String s = event.getMessage();

        if(p != player) {
            p.sendMessage(chatPrefix + chatFormat.replace("{Player}", player.getDisplayName()).replace("{Command}", s));
        }
    }

    private void logCommandToFile(HistoryItem hi) {
        File dataFolder = getDataFolder();
        if(!dataFolder.exists()) {
            dataFolder.mkdir();
        }

        String fileName = "commandlog_" + formatDate("yMMdd", new Date()) + ".txt";
        File saveTo = new File(getDataFolder(), fileName);
        if(!saveTo.exists()) {
            try {
                saveTo.createNewFile();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileWriter fw = new FileWriter(saveTo, true);
            PrintWriter pw = new PrintWriter(fw);

            pw.println("[" + formatDate("yyyy/MM/dd HH:mm:ss", hi.getDate()) + "] " + hi.getSender() + " executed "
                    + hi.getCommand() + " at " + hi.getLocation());
            pw.flush();
            pw.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static String formatDate(String format, Date ts) {
        return new SimpleDateFormat(format).format(ts);
    }

    public static boolean allowDisable() {
        return allowDisable;
    }

    public static String getChatFormat() {
        return chatFormat;
    }

    public static String getChatPrefix() {
        return chatPrefix;
    }

    public static void setNotifications(boolean state) {
        enableNotifications = state;
    }

    public static boolean allowNotifications() {
        return enableNotifications;
    }

    public static boolean allowFileLog() {
        return enableFileLog;
    }

    public static List<HistoryItem> getCommandHistory() {
        return commandHistory;
    }
}