package io.github.meeples10.commandlog;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
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
    private static File df, cfg, data;
    private static Logger log;
    private static HashMap<Player, Boolean> hidden = new HashMap<Player, Boolean>();

    public void onEnable() {
        df = Bukkit.getServer().getPluginManager().getPlugin(NAME).getDataFolder();
        cfg = new File(df, "config.yml");
        data = new File(df, "data");
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
    public void onPlayerJoin(PlayerJoinEvent e) {
        File f = new File(data, e.getPlayer().getUniqueId().toString() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        if(!f.exists()) {
            c.set("hide", false);
            try {
                c.save(f);
            } catch(IOException ex) {
                ex.printStackTrace();
            }
        }
        hidden.put(e.getPlayer(), c.getBoolean("hide"));
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        hidden.remove(e.getPlayer());
    }

    @EventHandler
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent e) {
        if(!enableNotifications) {
            return;
        }
        String s = e.getMessage();
        if(s.length() == 0 || s.split(" ").length == 0) {
            return;
        }

        for(Player p : Bukkit.getServer().getOnlinePlayers()) {
            if(p.hasPermission("commandlog.notice")) {
                logCommandToOnlinePlayer(e.getMessage(), p, e.getPlayer());
            }
        }

        Player player = e.getPlayer();
        HistoryItem hi = new HistoryItem(s, player.getDisplayName(), new Date(), player.getLocation());
        commandHistory.add(hi);

        if(enableFileLog) {
            logCommandToFile(hi);
        }
    }

    private void logCommandToOnlinePlayer(String s, Player p, Player sender) {
        if(p != sender) {
            if(!hidden.get(p)) {
                p.sendMessage(
                        chatPrefix + chatFormat.replace("{Player}", sender.getDisplayName()).replace("{Command}", s));
            }
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

    public static boolean toggleHidden(Player p) {
        hidden.put(p, !hidden.get(p));
        File f = new File(data, p.getUniqueId().toString() + ".yml");
        FileConfiguration c = YamlConfiguration.loadConfiguration(f);
        c.set("hide", hidden.get(p));
        try {
            c.save(f);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return hidden.get(p);
    }
}