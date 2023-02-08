package fastbldr.fastbldr.events;

import fastbldr.fastbldr.fastbldr;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class blockbreak implements Listener {
    fastbldr fb = fastbldr.instance;
    public blockbreak(Plugin pl) {
        Bukkit.getPluginManager().registerEvents(this, pl);
    }

    @EventHandler
    public void BlockBreakEvent(BlockBreakEvent e){
        if (e.getPlayer().getWorld().toString() == fb.getConfig().get("fastbldr.world")) {
            List<Block> blocks = fb.blocksplaced.get(e.getPlayer());
            if (!blocks.contains(e.getBlock())) {
                e.setCancelled(true);
            }
        }
    }
}
