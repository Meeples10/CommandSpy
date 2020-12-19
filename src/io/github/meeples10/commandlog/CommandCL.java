package io.github.meeples10.commandlog;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import net.ddns.meepnet.meepcore.MeepCommand;
import net.ddns.meepnet.meepcore.Messages;

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
            sender.sendMessage(Messages.format("$hl" + Main.NAME + " $t"
                    + Messages.translate(sender, "command.commandlog.cl.default.version") + " $hl"
                    + Bukkit.getServer().getPluginManager().getPlugin(Main.NAME).getDescription().getVersion() + "\n$t"
                    + Messages.translate(sender, "command.commandlog.cl.default.author") + ": $hlMeeples10\n$t"
                    + String.format(Messages.translate(sender, "command.commandlog.cl.default.help"),
                            Messages.translate(sender, "command.commandlog.cl.usage"))));
            return true;
        }
    }

    private static boolean reloadCommand(CommandSender sender) {
        if(sender.hasPermission("commandlog.reload")) {
            sender.sendMessage(Messages.reloadAttempt(sender, Main.NAME));
            sender.sendMessage(Messages.reloadMessage(sender, Main.NAME, Main.loadConfig()));
        } else {
            sender.sendMessage(Messages.noPermissionMessage(sender));
        }
        return true;
    }

    private static boolean disableCommand(CommandSender sender) {
        if(Main.allowDisabling()) {
            Main.setNotifications(false);
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(p.hasPermission("commandlog.notice")) {
                    p.sendMessage(Messages
                            .format(Messages.translate(p, "commandlog.prefix")
                                    + Messages.translate(p, "commandlog.command.cl.disabled.success"))
                            .replace("{{PLAYER}}",
                                    sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName()));
                }
            }
        } else {
            sender.sendMessage(Messages.format(Messages.translate(sender, "commandlog.prefix")
                    + Messages.translate(sender, "commandlog.command.cl.disabled.failure")));
        }
        return true;
    }

    private static boolean enableCommand(CommandSender sender) {
        if(Main.allowDisabling()) {
            Main.setNotifications(true);
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(p.hasPermission("commandlog.notice")) {
                    p.sendMessage(Messages
                            .format(Messages.translate(p, "commandlog.prefix")
                                    + Messages.translate(p, "commandlog.command.cl.enabled.success"))
                            .replace("{{PLAYER}}",
                                    sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName()));
                }
            }
        } else {
            sender.sendMessage(Messages.format(Messages.translate(sender, "commandlog.prefix")
                    + Messages.translate(sender, "commandlog.command.cl.enabled.failure")));
        }
        return true;
    }

    private static boolean helpCommand(CommandSender sender) {
        sender.sendMessage(Messages.format("$t" + ChatColor.STRIKETHROUGH + "---------------$hl " + Main.NAME
                + " Help $t" + ChatColor.STRIKETHROUGH + "---------------$hl\n" + "/cl reload$t: "
                + Messages.translate(sender, "command.meepcore.help.reload") + "$hl\n" + "/cl disable | d$t: "
                + Messages.translate(sender, "command.commandlog.cl.help.disable") + "$hl\n" + "/cl enable | e$t: "
                + Messages.translate(sender, "command.commandlog.cl.help.enable")));
        return true;
    }
}