package fastbldr.fastbldr.events;

import fastbldr.fastbldr.fastbldr;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;

public class blockplace implements Listener {
    fastbldr fb = fastbldr.instance;
    public blockplace(Plugin pl) {
        Bukkit.getPluginManager().registerEvents(this, pl);
    }

    @EventHandler
    public void BlockPlaceEvent(BlockPlaceEvent e){
        if (e.getPlayer().getWorld().toString() == fb.getConfig().get("fastbldr.world")) {
            List<Block> blocks = fb.blocksplaced.get(e.getPlayer());
            blocks.add(e.getBlockPlaced());
            fb.blocksplaced.put(e.getPlayer(), blocks);
        }
    }
}
