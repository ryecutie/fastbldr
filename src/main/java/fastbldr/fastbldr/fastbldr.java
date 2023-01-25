package fastbldr.fastbldr;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import fastbldr.fastbldr.cmds.debugfunction;
import fastbldr.fastbldr.cmds.reloadconfig;
import fastbldr.fastbldr.events.playerjoin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class fastbldr extends JavaPlugin {
    public static fastbldr instance;
    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        getServer().getConsoleSender().sendMessage("enabled fastbldr plugin! :)");

        getCommand("debugfunction").setExecutor(new debugfunction());
        getCommand("reloadconfig").setExecutor(new reloadconfig());
        new playerjoin(this);
    }
    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("disabled fastbldr plugin! :(");
    }
    public void resetSchem() throws IOException {

        // create variables
        File file = new File(getDataFolder(), (String)this.getConfig().get("fastbldr.schem.file"));
        int length = Integer.parseInt((String) this.getConfig().get("fastbldr.schem.length"));
        String[] start = String.valueOf(this.getConfig().get("fastbldr.schem.start")).split("\\s*,\\s*");
        String[] pstart = String.valueOf(this.getConfig().get("fastbldr.schem.player")).split("\\s*,\\s*");
        World world = Bukkit.getWorld((String) this.getConfig().get("fastbldr.world"));

        // loop player list; give each player a schem
        int index = 0;
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {

            // edit variables
            start[0] = String.valueOf(Float.valueOf(start[0])+(index*length));
            pstart[0] = String.valueOf(Float.valueOf(pstart[0])+(index*length));

            // paste schem
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            ClipboardReader reader = format.getReader(new FileInputStream(file));
            Clipboard clipboard = reader.read();

            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession(BukkitAdapter.adapt(world), -1)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(Float.valueOf(start[0]), Float.valueOf(start[1]), Float.valueOf(start[2])))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }

            // teleport player to spawn point
            p.teleport(new Location(world, Float.valueOf(pstart[0]), Float.valueOf(pstart[1]), Float.valueOf(pstart[2])));
            index++;
        }
    }
}
