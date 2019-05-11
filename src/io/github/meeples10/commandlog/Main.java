package io.github.meeples10.commandlog;

import java.io.File;

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

    private static String chatFormat;
    private static String chatPrefix;
    private static boolean enableNotifications = true;
    private static boolean allowDisable;
    private static File df, cfg;

    public void onEnable() {
        df = Bukkit.getServer().getPluginManager().getPlugin(NAME).getDataFolder();
        cfg = new File(df, "config.yml");
        getServer().getPluginManager().registerEvents(this, this);
        loadConfig();
        getCommand("commandlog").setExecutor(new CommandCL("/cl help"));
    }

    public static boolean loadConfig() {
        if(!df.exists()) {
            df.mkdirs();
        }
        if(!cfg.exists()) {
            Bukkit.getServer().getPluginManager().getPlugin(NAME).saveDefaultConfig();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(cfg);
        chatPrefix = ChatColor.translateAlternateColorCodes('&', config.getString("chat-prefix"));
        chatFormat = ChatColor.translateAlternateColorCodes('&', config.getString("chat-format"));
        allowDisable = config.getBoolean("allow-disabling-notices");

        if(chatFormat.length() == 0 || (chatFormat.indexOf("[{Player}]") == -1)
                || (chatFormat.indexOf("[{Command}]") == -1)) {
            Bukkit.getPluginManager().getPlugin(NAME).getLogger().warning(
                    "chat-format string must contain both [{Player}] and [{Command}] --- Using default chat-format string instead");
            chatFormat = config.getDefaults().getString("chat-format");
        }
        return true;
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
                if(p != e.getPlayer()) {
                    p.sendMessage(chatPrefix + chatFormat.replace("{Player}", e.getPlayer().getDisplayName())
                            .replace("{Command}", e.getMessage()));
                }
            }
        }
    }

    public static boolean allowDisable() {
        return allowDisable;
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
}