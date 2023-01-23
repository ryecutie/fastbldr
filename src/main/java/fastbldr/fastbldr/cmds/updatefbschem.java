package fastbldr.fastbldr.cmds;

import fastbldr.fastbldr.fastbldr;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class updatefbschem implements CommandExecutor {
    fastbldr fb = fastbldr.instance;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { return true; }
        Player p = (Player) sender;

        if (command.getName().equalsIgnoreCase("updatefbschem")){
            fb.getConfig().set("fastbldr.schem.file", args[0]);
            fb.getConfig().set("fastbldr.schem.length", args[1]);
            fb.getConfig().set("fastbldr.schem.start", args[2]);
            fb.getConfig().set("fastbldr.schem.player", args[3]);
            fb.saveConfig();
            p.sendMessage(ChatColor.of("#f2891f") + " ➜" + ChatColor.of("#cccccc") + " updated fastbldr schematic");
        }

        return true;
    }
}
