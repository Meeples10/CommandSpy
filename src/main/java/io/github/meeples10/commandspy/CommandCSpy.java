package io.github.meeples10.commandspy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.meeples10.meepcore.MeepCommand;
import io.github.meeples10.meepcore.Messages;

public class CommandCSpy extends MeepCommand {

    public CommandCSpy(String usage) {
        super(usage);
    }

    @Override
    public boolean run(CommandSender sender, Command cmd, String label, String[] args) {
        if(!sender.hasPermission("commandspy.use")) {
            sender.sendMessage(Messages.noPermissionMessage(sender));
            return false;
        }
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
                    + Messages.translate(sender, "command.commandspy.cspy.default.version") + " $hl"
                    + Bukkit.getServer().getPluginManager().getPlugin(Main.NAME).getDescription().getVersion() + "\n$t"
                    + Messages.translate(sender, "command.commandspy.cspy.default.author") + ": $hlMeeples10\n$t"
                    + String.format(Messages.translate(sender, "command.commandspy.cspy.default.help"),
                            Messages.translate(sender, "command.commandspy.cspy.usage"))));
            return true;
        }
    }

    private static boolean reloadCommand(CommandSender sender) {
        if(sender.hasPermission("commandspy.reload")) {
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
                if(p.hasPermission("commandspy.notice")) {
                    p.sendMessage(Messages
                            .format(Messages.translate(p, "commandspy.prefix")
                                    + Messages.translate(p, "command.commandspy.cspy.disabled.success"))
                            .replace("{{PLAYER}}",
                                    sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName()));
                }
            }
        } else {
            sender.sendMessage(Messages.format(Messages.translate(sender, "commandspy.prefix")
                    + Messages.translate(sender, "command.commandspy.cspy.disabled.failure")));
        }
        return true;
    }

    private static boolean enableCommand(CommandSender sender) {
        if(Main.allowDisabling()) {
            Main.setNotifications(true);
            for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                if(p.hasPermission("commandspy.notice")) {
                    p.sendMessage(Messages
                            .format(Messages.translate(p, "commandspy.prefix")
                                    + Messages.translate(p, "command.commandspy.cspy.enabled.success"))
                            .replace("{{PLAYER}}",
                                    sender instanceof Player ? ((Player) sender).getDisplayName() : sender.getName()));
                }
            }
        } else {
            sender.sendMessage(Messages.format(Messages.translate(sender, "commandspy.prefix")
                    + Messages.translate(sender, "command.commandspy.cspy.enabled.failure")));
        }
        return true;
    }

    private static boolean helpCommand(CommandSender sender) {
        sender.sendMessage(Messages.format("$t" + ChatColor.STRIKETHROUGH + "---------------$hl " + Main.NAME
                + " Help $t" + ChatColor.STRIKETHROUGH + "---------------$hl\n" + "/cspy reload$t: "
                + Messages.translate(sender, "command.meepcore.help.reload") + "$hl\n" + "/cspy disable | d$t: "
                + Messages.translate(sender, "command.commandspy.cspy.help.disable") + "$hl\n" + "/cspy enable | e$t: "
                + Messages.translate(sender, "command.commandspy.cspy.help.enable")));
        return true;
    }
}
