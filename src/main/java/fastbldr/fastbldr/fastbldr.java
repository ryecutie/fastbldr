package fastbldr.fastbldr;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.datatransfer.Clipboard;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public final class fastbldr extends JavaPlugin {
    public static fastbldr instance;
    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
    }
    @Override
    public void onDisable() {
    }
    public void resetSchem() {
        List players = Arrays.asList(this.getServer().getOnlinePlayers());
        String file = (String) this.getConfig().get("fastbldr.schem.file");
        String length = (String) this.getConfig().get("fastbldr.schem.length");
        List start = Arrays.asList(String.valueOf(this.getConfig().get("fastbldr.schem.start")).split("\\s*,\\s*,\\s*"));
        List pstart = Arrays.asList(String.valueOf(this.getConfig().get("fastbldr.schem.player")).split("\\s*,\\s*,\\s*"));
        World world = (World) this.getConfig().get("fastbldr.world");
        for (Object ob : players) {
            Player p = (Player) ob;
            start.set(0, String.valueOf(((int) start.get(0)) + (Integer.valueOf(players.indexOf(ob))*Integer.valueOf(length))));
            pstart.set(0, String.valueOf(((int) pstart.get(0)) + (Integer.valueOf(players.indexOf(ob))*Integer.valueOf(length))));
            // paste schem

            Clipboard clipboard;

            ClipboardFormat format = ClipboardFormats.findByFile(file);
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                clipboard = reader.read();
            }

            p.teleport(new Location(world, (int) pstart.get(0), (int) pstart.get(1), (int) pstart.get(2)));
        }

    }
}
