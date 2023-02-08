package fastbldr.fastbldr.events;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fastbldr.fastbldr.fastbldr;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.Plugin;

public class playermove implements Listener {
    fastbldr fb = fastbldr.instance;
    public playermove(Plugin pl) {
        Bukkit.getPluginManager().registerEvents(this, pl);
    }

    @EventHandler
    public void PlayerMoveEvent(PlayerMoveEvent e){
        String[] pstart = String.valueOf(fb.getConfig().get("fastbldr.schem.player")).split("\\s*,\\s*");
        int length = Integer.parseInt((String) fb.getConfig().get("fastbldr.schem.length"));
        int index = 0;
        for (String uuid : fb.playerlist) {
            if (uuid == e.getPlayer().getUniqueId().toString()) {
                pstart[0] = String.valueOf(Float.valueOf(pstart[0])+(index*length));
            }
            index++;
        }
        if (e.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR) {
            if (e.getPlayer().getWorld().toString() == fb.getConfig().get("fastbldr.world")) {

                // variable initialization
                RegionManager regionManager =  WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(e.getPlayer().getWorld()));
                ApplicableRegionSet set = regionManager.getApplicableRegions(BukkitAdapter.asBlockVector(e.getPlayer().getLocation()));
                boolean isinregion = false;

                // check if player within their own region
                for (ProtectedRegion element : set) {
                    if (element.getId() == e.getPlayer().getName() + "-island") {
                        isinregion = true;
                    }
                }

                // if not, teleport them to their region
                if (!isinregion) {
                    //ProtectedRegion region = regionManager.getRegion(e.getPlayer().getName() + "-island");
                    e.getPlayer().teleport(new Location(e.getPlayer().getWorld(), Float.valueOf(pstart[0]), Float.valueOf(pstart[1]), Float.valueOf(pstart[2])));
                }
            }
        }
    }
}
