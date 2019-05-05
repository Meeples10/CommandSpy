package io.github.meeples10.commandlog;

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
            switch(args[0].toLowerCase()) {
            case "reload":
                return reloadCommand(sender);
            case "enable":
            case "e":
                return enableCommand(sender);
            case "disable":
            case "d":
                return disableCommand(sender);
            case "help":
            case "?":
                return helpCommand(sender);
            default:
                return false;
            }
        } else {
            sender.sendMessage(Messages.format("$hlCommandLog $tversion $hl"
                    + Bukkit.getServer().getPluginManager().getPlugin(Main.NAME).getDescription().getVersion()
                    + "\n$tAuthor: $hlMeeples10\n$tUse $hl/cl help $tfor more information."));
            return true;
        }
    }

    private boolean reloadCommand(CommandSender sender) {
        if(sender.hasPermission("commandlog.reload")) {
            sender.sendMessage(Messages.reloadAttempt(Main.NAME));
            sender.sendMessage(Messages.reloadMessage(Main.NAME, Main.loadConfig()));
        } else {
            sender.sendMessage(Messages.noPermissionMessage());
        }
        return true;
    }

    private boolean disableCommand(CommandSender sender) {
        if(Main.allowDisable()) {
            Main.setNotifications(false);
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(p.hasPermission("commandlog.notice")) {
                    p.sendMessage(Main.getChatPrefix()
                            + (sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName())
                            + ChatColor.RESET + " has disabled notifications.");
                }
            }
        } else {
            sender.sendMessage(Main.getChatPrefix() + "Notifications cannot be disabled.");
        }
        return true;
    }

    private boolean enableCommand(CommandSender sender) {
        if(Main.allowDisable()) {
            Main.setNotifications(true);
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(p.hasPermission("commandlog.notice")) {
                    p.sendMessage(Main.getChatPrefix()
                            + (sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName())
                            + ChatColor.RESET + " has enabled notifications.");
                }
            }
        } else {
            sender.sendMessage(Main.getChatPrefix() + "Notifications are already enabled.");
        }
        return true;
    }

    private boolean helpCommand(CommandSender sender) {
        sender.sendMessage(Messages.format("$t" + ChatColor.STRIKETHROUGH + "---------------$hl " + Main.NAME
                + " Help $t" + ChatColor.STRIKETHROUGH + "---------------$hl\n" + "/cl reload$t: Reload the plugin$hl\n"
                + "/cl disable | d$t: Disable command notifications for everyone$hl\n"
                + "/cl enable | e$t: Enable command notifications for everyone"));
        return true;
    }
}