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

import io.github.meeples10.meepcore.I18n;
import io.github.meeples10.meepcore.Messages;

public final class Main extends JavaPlugin implements Listener {
    public static final String NAME = "CommandLog";

    private static boolean enableNotifications = true;
    private static boolean allowDisabling;
    private static File df, cfg;

    public void onEnable() {
        df = Bukkit.getServer().getPluginManager().getPlugin(NAME).getDataFolder();
        cfg = new File(df, "config.yml");
        getServer().getPluginManager().registerEvents(this, this);
        loadConfig();
        try {
            I18n.loadMessages(NAME);
        } catch(Exception e) {
            e.printStackTrace();
        }
        getCommand("commandlog").setExecutor(new CommandCL("command.commandlog.cl.usage"));
    }

    public static boolean loadConfig() {
        if(!df.exists()) {
            df.mkdirs();
        }
        if(!cfg.exists()) {
            Bukkit.getServer().getPluginManager().getPlugin(NAME).saveDefaultConfig();
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(cfg);
        allowDisabling = config.getBoolean("allow-disabling-notices");
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
                    p.sendMessage(Messages
                            .format(Messages.translate(p, "commandlog.prefix")
                                    + Messages.translate(p, "commandlog.notification"))
                            .replace("{{PLAYER}}", e.getPlayer().getDisplayName())
                            .replace("{{COMMAND}}", e.getMessage()));
                }
            }
        }
    }

    public static boolean allowDisabling() {
        return allowDisabling;
    }

    public static void setNotifications(boolean state) {
        enableNotifications = state;
    }

    public static boolean allowNotifications() {
        return enableNotifications;
    }
}
