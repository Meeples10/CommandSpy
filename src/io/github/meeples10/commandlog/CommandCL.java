package io.github.meeples10.commandlog;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.meeples10.meepcore.MeepCommand;
import io.github.meeples10.meepcore.Messages;
import net.md_5.bungee.api.ChatColor;

public class CommandCL extends MeepCommand {

    public CommandCL(String usage) {
        super(usage);
    }

    @Override
    public boolean run(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length > 0) {
            if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                return reloadCommand(sender, args);
            } else if(args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("d")) {
                return disableCommand(sender, args);
            } else if(args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("e")) {
                return enableCommand(sender, args);
            } else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                return helpCommand(sender, args);
            } else if(args[0].equalsIgnoreCase("hide")) {
                return hideCommand(sender, args);
            } else {
                return false;
            }
        } else {
            sender.sendMessage(Messages.format("$hlCommandLog $tversion $hl"
                    + Bukkit.getServer().getPluginManager().getPlugin(Main.NAME).getDescription().getVersion()
                    + "\n$tAuthor: $hlMeeples10\n$tUse $hl/cl help $tfor more information."));
            return true;
        }
    }

    private boolean reloadCommand(CommandSender sender, String[] args) {
        if(sender instanceof Player) {
            Player player = (Player) sender;

            if(player.hasPermission("commandlog.admin")) {
                Bukkit.getServer().getPluginManager().getPlugin(Main.NAME).reloadConfig();
                Main.loadConfig();
                sender.sendMessage(Main.getChatPrefix() + "Config reloaded");
            } else {
                sender.sendMessage(Messages.noPermissionMessage());
            }
        } else {
            Bukkit.getServer().getPluginManager().getPlugin(Main.NAME).reloadConfig();
            Main.loadConfig();
            Bukkit.getPluginManager().getPlugin(Main.NAME).getLogger().info("Config reloaded");
        }
        return true;
    }

    private boolean disableCommand(CommandSender sender, String[] args) {
        Player p;
        if(Main.allowDisable()) {
            if(sender instanceof Player) {
                Player player = (Player) sender;

                if(player.hasPermission("commandlog.admin")) {
                    Main.setNotifications(false);

                    List<Player> playerList = (List<Player>) Bukkit.getServer().getOnlinePlayers();
                    if(playerList != null) {
                        for(Iterator<Player> localIterator = playerList.iterator(); localIterator.hasNext();) {
                            p = (Player) localIterator.next();
                            if(p.hasPermission("commandlog.notice")) {
                                p.sendMessage(Main.getChatPrefix() + player.getDisplayName() + ChatColor.RESET
                                        + " has disabled notifications.");
                            }
                        }
                    }
                } else {
                    sender.sendMessage(Messages.noPermissionMessage());
                }
            } else {
                Main.setNotifications(false);

                List<Player> playerList = (List<Player>) Bukkit.getServer().getOnlinePlayers();
                if(playerList != null) {
                    for(Player p1 : playerList) {
                        if(p1.hasPermission("commandlog.notice")) {
                            p1.sendMessage(Main.getChatPrefix() + "Console has disabled notifications.");
                        }
                    }
                }
                sender.sendMessage(Main.getChatPrefix() + "Notifications are now disabled.");
            }
        } else {
            sender.sendMessage(Main.getChatPrefix() + "Notifications cannot be disabled.");
        }
        return true;
    }

    private boolean enableCommand(CommandSender sender, String[] args) {
        Player p;
        if(Main.allowDisable()) {
            if(sender instanceof Player) {
                Player player = (Player) sender;

                if(player.hasPermission("commandlog.admin")) {
                    Main.setNotifications(true);

                    List<Player> playerList = (List<Player>) Bukkit.getServer().getOnlinePlayers();
                    if(playerList != null) {
                        for(Iterator<Player> localIterator = playerList.iterator(); localIterator.hasNext();) {
                            p = (Player) localIterator.next();
                            if(p.hasPermission("commandlog.notice")) {
                                p.sendMessage(Main.getChatPrefix() + player.getDisplayName() + ChatColor.RESET
                                        + " has enabled notifications.");
                            }
                        }
                    }
                } else {
                    sender.sendMessage(Messages.noPermissionMessage());
                }
            } else {
                Main.setNotifications(true);

                List<Player> playerList = (List<Player>) Bukkit.getServer().getOnlinePlayers();
                if(playerList != null) {
                    for(Player p2 : playerList) {
                        if(p2.hasPermission("commandlog.notice")) {
                            p2.sendMessage(Main.getChatPrefix() + "Console has enabled notifications.");
                        }
                    }
                }
                sender.sendMessage(Main.getChatPrefix() + "Notifications are now disabled.");
            }
        }
        return true;
    }

    private boolean helpCommand(CommandSender sender, String[] args) {
        sender.sendMessage(
                Messages.format("$t" + ChatColor.STRIKETHROUGH + "---------------$hl " + Main.NAME + " Help $t"
                        + ChatColor.STRIKETHROUGH + "---------------$hl\n" + "/cl reload | rl$t: Reload the plugin$hl\n"
                        + "/cl disable | d$t: Disable command notifications for everyone$hl\n"
                        + "/cl enable | e$t: Enable command notifications for everyone"));
        return true;
    }

    private boolean hideCommand(CommandSender sender, String[] args) {
        if(sender.hasPermission("commandlog.notice")) {
            if(sender instanceof Player) {
                sender.sendMessage(
                        Main.getChatPrefix() + "You will " + (Main.toggleHidden((Player) sender) ? "no longer" : "now")
                                + " receive command notifications.");
            } else {
                sender.sendMessage(Messages.getPlayersOnlyMessage());
            }
        }
        return true;
    }
}