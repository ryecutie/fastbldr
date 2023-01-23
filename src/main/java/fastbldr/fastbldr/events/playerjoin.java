package fastbldr.fastbldr.events;

import fastbldr.fastbldr.fastbldr;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class playerjoin implements Listener {
    fastbldr fb = fastbldr.instance;
    public playerjoin(Plugin pl) {
        Bukkit.getPluginManager().registerEvents(this, pl);
    }

    @EventHandler
    public void PlayerJoinEvent(PlayerJoinEvent e){
        int playercount = e.getPlayer().getServer().getOnlinePlayers().size();

    }
}
