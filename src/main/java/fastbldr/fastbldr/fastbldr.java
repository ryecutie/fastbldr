package fastbldr.fastbldr;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.datatransfer.Clipboard;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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

        // create variables
        List players = Arrays.asList(this.getServer().getOnlinePlayers());
        File file = (File) this.getConfig().get("fastbldr.schem.file");
        String length = (String) this.getConfig().get("fastbldr.schem.length");
        List start = Arrays.asList(String.valueOf(this.getConfig().get("fastbldr.schem.start")).split("\\s*,\\s*,\\s*"));
        List pstart = Arrays.asList(String.valueOf(this.getConfig().get("fastbldr.schem.player")).split("\\s*,\\s*,\\s*"));
        World world = (World) this.getConfig().get("fastbldr.world");
        
        // loop player list; give each player a schem
        for (Object ob : players) {

            // edit variables
            Player p = (Player) ob;
            start.set(0, String.valueOf(((int) start.get(0)) + (Integer.valueOf(players.indexOf(ob))*Integer.valueOf(length))));
            pstart.set(0, String.valueOf(((int) pstart.get(0)) + (Integer.valueOf(players.indexOf(ob))*Integer.valueOf(length))));

            // paste schem
            Clipboard clipboard;
            ClipboardFormat format = ClipboardFormats.findByFile(file);
            try (ClipboardReader reader = format.getReader(new FileInputStream(file))) {
                clipboard = (Clipboard) reader.read();
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            try (EditSession editSession = WorldEdit.getInstance().getEditSessionFactory().getEditSession((com.sk89q.worldedit.world.World) world, -1)) {
                Operation operation = new ClipboardHolder((com.sk89q.worldedit.extent.clipboard.Clipboard) clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at((int) start.get(0), (int) start.get(1), (int) start.get(2)))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException e) {
                throw new RuntimeException(e);
            }

            // teleport player to spawn point
            p.teleport(new Location(world, (int) pstart.get(0), (int) pstart.get(1), (int) pstart.get(2)));
        }

    }
}
