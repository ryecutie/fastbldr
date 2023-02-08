package fastbldr.fastbldr.events;

import com.sk89q.worldedit.MaxChangedBlocksException;
import fastbldr.fastbldr.fastbldr;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;

public class playerquit implements Listener {
    fastbldr fb = fastbldr.instance;
    public playerquit(Plugin pl) {
        Bukkit.getPluginManager().registerEvents(this, pl);
    }

    @EventHandler
    public void PlayerQuitEvent(PlayerQuitEvent e) throws MaxChangedBlocksException {
        fb.removeFromGrid(e.getPlayer());
    }
}
