package io.github.meeples10.commandspy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import io.github.meeples10.meepcore.MCommand;
import io.github.meeples10.meepcore.Messages;

public class CommandCSpy extends MCommand {

    public CommandCSpy(String usage) {
        super(usage);
    }

    @Override
    public boolean run(CommandSender sender, Command cmd, String label, String[] args, String locale) {
        if(!sender.hasPermission("commandspy.use")) {
            sender.sendMessage(Messages.noPermissionMessage(locale));
            return false;
        }
        if(args.length > 0) {
            switch(args[0].toLowerCase()) {
            case "reload": {
                if(sender.hasPermission("commandspy.reload")) {
                    sender.sendMessage(Messages.reloadAttempt(locale, Main.NAME));
                    sender.sendMessage(Messages.reloadMessage(locale, Main.NAME, Main.loadConfig()));
                } else {
                    sender.sendMessage(Messages.noPermissionMessage(locale));
                }
                return true;
            }
            case "enable":
            case "e": {
                if(Main.allowDisabling()) {
                    Main.setNotifications(true);
                    for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                        if(p.hasPermission("commandspy.notice")) {
                            p.sendMessage(Messages
                                    .format(Messages.translate(locale, "commandspy.prefix")
                                            + Messages.translate(locale, "command.commandspy.cspy.enabled.success"))
                                    .replace("{{PLAYER}}", sender instanceof Player ? ((Player) sender).getDisplayName()
                                            : sender.getName()));
                        }
                    }
                } else {
                    sender.sendMessage(Messages.format(Messages.translate(locale, "commandspy.prefix")
                            + Messages.translate(locale, "command.commandspy.cspy.enabled.failure")));
                }
                return true;
            }
            case "disable":
            case "d": {
                if(Main.allowDisabling()) {
                    Main.setNotifications(false);
                    for(Player p : Bukkit.getServer().getOnlinePlayers()) {
                        if(p.hasPermission("commandspy.notice")) {
                            p.sendMessage(Messages
                                    .format(Messages.translate(locale, "commandspy.prefix")
                                            + Messages.translate(locale, "command.commandspy.cspy.disabled.success"))
                                    .replace("{{PLAYER}}", sender instanceof Player ? ((Player) sender).getDisplayName()
                                            : sender.getName()));
                        }
                    }
                } else {
                    sender.sendMessage(Messages.format(Messages.translate(locale, "commandspy.prefix")
                            + Messages.translate(locale, "command.commandspy.cspy.disabled.failure")));
                }
                return true;
            }
            case "help":
            case "?": {
                sender.sendMessage(Messages.format("$t" + ChatColor.STRIKETHROUGH + "---------------$hl " + Main.NAME
                        + " Help $t" + ChatColor.STRIKETHROUGH + "---------------$hl\n" + "/cspy reload$t: "
                        + Messages.translate(locale, "command.meepcore.help.reload") + "$hl\n" + "/cspy disable | d$t: "
                        + Messages.translate(locale, "command.commandspy.cspy.help.disable") + "$hl\n"
                        + "/cspy enable | e$t: " + Messages.translate(locale, "command.commandspy.cspy.help.enable")));
                return true;
            }
            default:
                return false;
            }
        } else {
            sender.sendMessage(Messages.format("$hl" + Main.NAME + " $t"
                    + Messages.translate(locale, "command.commandspy.cspy.default.version") + " $hl"
                    + Bukkit.getServer().getPluginManager().getPlugin(Main.NAME).getDescription().getVersion() + "\n$t"
                    + Messages.translate(locale, "command.commandspy.cspy.default.author") + ": $hlMeeples10\n$t"
                    + String.format(Messages.translate(locale, "command.commandspy.cspy.default.help"),
                            Messages.translate(locale, "command.commandspy.cspy.usage"))));
            return true;
        }
    }
}
