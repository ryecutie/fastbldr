package fastbldr.fastbldr;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.function.pattern.Pattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockTypes;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import fastbldr.fastbldr.cmds.debugfunction;
import fastbldr.fastbldr.cmds.reloadconfig;
import fastbldr.fastbldr.events.blockbreak;
import fastbldr.fastbldr.events.blockplace;
import fastbldr.fastbldr.events.playerjoin;
import fastbldr.fastbldr.events.playermove;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public final class fastbldr extends JavaPlugin {
    public static fastbldr instance;
    public Map<Player, List<Block>> blocksplaced;
    public List<String> playerlist;
    @Override
    public void onEnable() {
        instance = this;
        this.saveDefaultConfig();
        getServer().getConsoleSender().sendMessage("enabled fastbldr plugin! :)");
        try {
            resetGrid();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        getCommand("debugfunction").setExecutor(new debugfunction());
        getCommand("reloadconfig").setExecutor(new reloadconfig());
        new playerjoin(this);
        new blockbreak(this);
        new blockplace(this);
        new playermove(this);
    }
    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage("disabled fastbldr plugin! :(");
    }
    public void resetGrid() throws IOException {

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
            playerlist.add(p.getUniqueId().toString());
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

            // creating region
            ProtectedCuboidRegion region = new ProtectedCuboidRegion(p.getName() + "-island", clipboard.getMinimumPoint(), clipboard.getMaximumPoint());
            WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(p.getWorld())).addRegion(region);

            // teleport player to spawn point
            p.teleport(new Location(world, Float.valueOf(pstart[0]), Float.valueOf(pstart[1]), Float.valueOf(pstart[2])));
            index++;
        }
    }
    public void removeFromGrid(Player p) throws MaxChangedBlocksException {
        int index = 0;
        for (String lp : playerlist) {
            if (lp == p.getUniqueId().toString()) {
                playerlist.set(index, "-");
            }
            index++;
        }
        //ProtectedRegion region = new ProtectedCuboidRegion(p.getName() + "-island", clipboard.getRegion().getMinimumPoint(), clipboard.getRegion().getMaximumPoint());
        RegionManager rm = WorldGuard.getInstance().getPlatform().getRegionContainer().get(BukkitAdapter.adapt(p.getWorld()));
        ProtectedRegion region = rm.getRegion(p.getName() + "-island");
        try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(p.getWorld()))) {
            editSession.setBlocks((Region) region, (Pattern) BlockTypes.AIR);
        }
        rm.removeRegion(p.getName() + "-island");
    }
    public void addToGrid(Player p) throws IOException {
        int playerindex = 0;
        int index = 0;
        for (String lp : playerlist) {
            if (lp == "-") {
                playerlist.set(index, p.getUniqueId().toString());
                playerindex = index;
                break;
            }
            index++;
        }
        if (!playerlist.contains(p.getUniqueId().toString())) {
            playerlist.add(p.getUniqueId().toString());
            playerindex = playerlist.size();
        }

        File file = new File(getDataFolder(), (String) this.getConfig().get("fastbldr.schem.file"));
        int length = Integer.parseInt((String) this.getConfig().get("fastbldr.schem.length"));
        String[] start = String.valueOf(this.getConfig().get("fastbldr.schem.start")).split("\\s*,\\s*");
        String[] pstart = String.valueOf(this.getConfig().get("fastbldr.schem.player")).split("\\s*,\\s*");
        World world = Bukkit.getWorld((String) this.getConfig().get("fastbldr.world"));

        start[0] = String.valueOf(Float.valueOf(start[0])+(playerindex*length));
        pstart[0] = String.valueOf(Float.valueOf(pstart[0])+(playerindex*length));

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

        p.teleport(new Location(world, Float.valueOf(pstart[0]), Float.valueOf(pstart[1]), Float.valueOf(pstart[2])));
    }
}
