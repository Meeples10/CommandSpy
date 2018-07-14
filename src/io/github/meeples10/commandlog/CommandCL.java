package io.github.meeples10.commandlog;

import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class CommandCL implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("commandlog")) {
            if(args.length == 0) {
                sender.sendMessage("§aCommandLog §fversion §a"
                        + Bukkit.getServer().getPluginManager().getPlugin(Main.NAME).getDescription().getVersion());
                sender.sendMessage("§fBy §aMeeples10");
                sender.sendMessage("§fType §a/cl help §ffor more information");
                return true;
            }

            if(args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                return reloadCommand(sender, args);
            } else if(args[0].equalsIgnoreCase("disable") || args[0].equalsIgnoreCase("d")) {
                return disableCommand(sender, args);
            } else if(args[0].equalsIgnoreCase("enable") || args[0].equalsIgnoreCase("e")) {
                return enableCommand(sender, args);
            } else if(args[0].equalsIgnoreCase("history") || args[0].equalsIgnoreCase("h")) {
                return historyCommand(sender, args);
            } else if(args[0].equalsIgnoreCase("help") || args[0].equalsIgnoreCase("?")) {
                return helpCommand(sender, args);
            }
        }

        return false;
    }

    private boolean reloadCommand(CommandSender sender, String[] args) {
        if((sender instanceof Player)) {
            Player player = (Player) sender;

            if(player.hasPermission("commandlog.admin")) {
                Bukkit.getServer().getPluginManager().getPlugin(Main.NAME).reloadConfig();
                Main.loadConfig();
                sender.sendMessage("§a[§eCL§a] §fConfig reloaded");
            } else {
                sender.sendMessage(ChatColor.RED + "Insufficient permissions");
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
            if((sender instanceof Player)) {
                Player player = (Player) sender;

                if(player.hasPermission("commandlog.admin")) {
                    Main.setNotifications(false);

                    List<Player> playerList = (List<Player>) Bukkit.getServer().getOnlinePlayers();
                    if(playerList != null) {
                        for(Iterator<Player> localIterator = playerList.iterator(); localIterator.hasNext();) {
                            p = (Player) localIterator.next();
                            if(p.hasPermission("commandlog.notice")) {
                                p.sendMessage(Main.getChatPrefix() + ChatColor.RESET + player.getDisplayName()
                                        + ChatColor.RESET + " has disabled notifications");
                            }
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Insufficient permissions");
                }
            } else {
                Main.setNotifications(false);

                List<Player> playerList = (List<Player>) Bukkit.getServer().getOnlinePlayers();
                if(playerList != null) {
                    for(Player p1 : playerList) {
                        if(p1.hasPermission("commandlog.notice")) {
                            p1.sendMessage(
                                    Main.getChatPrefix() + ChatColor.RESET + " Console has disabled notifications");
                        }
                    }
                }
                sender.sendMessage(Main.getChatPrefix() + ChatColor.RESET + " Notifications are now disabled");
            }
        } else {
            sender.sendMessage(Main.getChatPrefix() + ChatColor.RESET + " Notifications cannot be disabled");
        }
        return true;
    }

    private boolean enableCommand(CommandSender sender, String[] args) {
        Player p;
        if(Main.allowDisable()) {
            if((sender instanceof Player)) {
                Player player = (Player) sender;

                if(player.hasPermission("commandlog.admin")) {
                    Main.setNotifications(true);

                    List<Player> playerList = (List<Player>) Bukkit.getServer().getOnlinePlayers();
                    if(playerList != null) {
                        for(Iterator<Player> localIterator = playerList.iterator(); localIterator.hasNext();) {
                            p = (Player) localIterator.next();
                            if(p.hasPermission("commandlog.notice")) {
                                p.sendMessage(Main.getChatPrefix() + ChatColor.RESET + player.getDisplayName()
                                        + ChatColor.RESET + " has enabled notifications");
                            }
                        }
                    }
                } else {
                    sender.sendMessage(ChatColor.RED + "Insufficient permissions");
                }
            } else {
                Main.setNotifications(true);

                List<Player> playerList = (List<Player>) Bukkit.getServer().getOnlinePlayers();
                if(playerList != null) {
                    for(Player p2 : playerList) {
                        if(p2.hasPermission("commandlog.notice")) {
                            p2.sendMessage(
                                    Main.getChatPrefix() + ChatColor.RESET + " Console has enabled notifications");
                        }
                    }
                }
                sender.sendMessage(Main.getChatPrefix() + ChatColor.RESET + " Notifications are now disabled");
            }
        }
        return true;
    }

    private boolean historyCommand(CommandSender sender, String[] args) {
        if((sender instanceof Player)) {
            Player player = (Player) sender;

            if(player.hasPermission("commandlog.history")) {
                int numHistory = 10;
                if(args.length == 2) {
                    try {
                        numHistory = Integer.parseInt(args[1]);
                    } catch(NumberFormatException e) {
                        sender.sendMessage(ChatColor.RED + "Please type a number, for example: " + ChatColor.DARK_RED
                                + "/cl history 10");
                        return true;
                    }
                }
                if(numHistory > Main.getCommandHistory().size()) {
                    numHistory = Main.getCommandHistory().size();
                }

                sender.sendMessage(Main.getChatPrefix() + ChatColor.GOLD + "Here are the last " + ChatColor.RED
                        + numHistory + ChatColor.GOLD + " commands:");
                for(int iHistory = numHistory - 1; iHistory >= 0; iHistory--) {
                    HistoryItem hi = (HistoryItem) Main.getCommandHistory().get(iHistory);
                    sender.sendMessage("[" + Main.formatDate("y/M/d HH:mm", hi.getDate()) + "] " + hi.getSender()
                            + " executed " + hi.getCommand());
                }
            } else {
                sender.sendMessage(ChatColor.RED + "Insufficient permissions");
            }
        } else {
            int numHistory = 10;
            if(args.length == 2) {
                try {
                    numHistory = Integer.parseInt(args[1]);
                } catch(NumberFormatException e) {
                    sender.sendMessage(ChatColor.RED + "Please type a number, for example: " + ChatColor.DARK_RED
                            + "/cl history 10");
                    return true;
                }
            }
            if(numHistory > Main.getCommandHistory().size()) {
                numHistory = Main.getCommandHistory().size();
            }

            sender.sendMessage(Main.getChatPrefix() + "§6Here are the last " + ChatColor.RED + numHistory
                    + ChatColor.GOLD + " commands:");
            for(int iHistory = numHistory - 1; iHistory >= 0; iHistory--) {
                HistoryItem hi = (HistoryItem) Main.getCommandHistory().get(iHistory);
                sender.sendMessage("[" + Main.formatDate("y/M/d HH:mm", hi.getDate()) + "] " + hi.getSender()
                        + " executed " + hi.getCommand());
            }
        }
        return true;
    }

    private boolean helpCommand(CommandSender sender, String[] args) {
        if((sender instanceof Player)) {
            Player player = (Player) sender;

            if(player.hasPermission("commandlog.notice")) {
                sender.sendMessage(ChatColor.GOLD + "CommandLog commands:");
                sender.sendMessage("§c/cl history [x]| hi [x] §f: Shows the last <x> commands executed, default = 10");
            }

            if(player.hasPermission("commandlog.admin")) {
                sender.sendMessage("§6CommandLog Admin commands:");
                sender.sendMessage("§c/cl reload | rl §f: Reload the configuration");
                sender.sendMessage("§c/cl disable | d §f: Disables command notifications for everyone");
                sender.sendMessage("§c/cl enable | e §f: Enables command notifications for everyone");
            }
        } else {
            sender.sendMessage("§6CommandLog commands:");
            sender.sendMessage("§c/cl history <x>| h <x> §f: Shows the last <x> commands executed, default = 10");
            sender.sendMessage("§6CommandLog Admin commands:");
            sender.sendMessage("§c/cl reload | rl §f: Reload the configuration");
            sender.sendMessage("§c/cl disable | d §f: Disables command notifications for everyone");
            sender.sendMessage("§c/cl enable | e §f: Enables command notifications for everyone");
        }
        return true;
    }
}