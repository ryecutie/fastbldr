package fastbldr.fastbldr.cmds;

import fastbldr.fastbldr.fastbldr;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;

public class debugfunction implements CommandExecutor {
    fastbldr fb = fastbldr.instance;
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) { return true; }
        Player p = (Player) sender;

        if (command.getName().equalsIgnoreCase("debugfunction")){
            try {
                fb.resetSchem();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            p.sendMessage(ChatColor.of("#f2891f") + " ➜" + ChatColor.of("#cccccc") + " ran resetSchem()");
        }
        return true;
    }
}
