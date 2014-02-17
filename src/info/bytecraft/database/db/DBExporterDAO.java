package info.bytecraft.database.db;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.mojang.nbt.CompoundTag;
import com.mojang.nbt.NbtIo;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.RegionFile;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IExporterDAO;
import info.bytecraft.zones.Zone;

public class DBExporterDAO implements IExporterDAO
{
    private Connection conn;
    private Bytecraft plugin;
    
    public DBExporterDAO(Bytecraft plugin, Connection conn)
    {
        this.conn = conn;
        this.plugin = plugin;
    }

    @Override
    public void export(BytecraftPlayer player, Zone zone, String warp) throws DAOException, Exception
    {
        File srcDir;
        try{
            srcDir = new File(Bukkit.getWorldContainer()  + "/" + zone.getWorld());
            if (!srcDir.exists()) {
                throw new RuntimeException(zone.getWorld() + " doesn't exist");
            } else if (!srcDir.isDirectory()) {
                throw new RuntimeException(zone.getWorld() + " is not a folder");
            }
        }catch(Exception e){ 
            return;
        }
        
        String worldName = srcDir.getName();
        player.sendMessage("Processing world: " + worldName);
        
        Rectangle rect = getZoneRect(zone.getName());
        if(rect == null){
            player.sendMessage(ChatColor.RED + zone.getName() + " was not found");
        }
        
        String warpName = null;
        if(warp != null){
            warpName = warp;
        }
        
        Map<String, Point> warps = getWarps(rect);
        Point spawn = null;
        if (warps.size() == 0) {
            player.sendMessage(ChatColor.RED + "No warps found!");
            return;
        }
        else if (warps.size() > 1 && warpName != null) {
            spawn = warps.get(warpName);
            if (spawn == null) {
                player.sendMessage(ChatColor.RED + warpName + " not found.");
                return;
            }
            String s = String.format("Found warp %s\n", warpName);
            player.sendMessage(ChatColor.YELLOW + s);
        }
        else if (warps.size() > 1) {
            player.sendMessage("Multiple warps found:");
            for (Map.Entry<String, Point> warpX : warps.entrySet()) {
                player.sendMessage(warpX.getKey());
            }
            return;
        }
        else {
            for (Map.Entry<String, Point> warpX : warps.entrySet()) {
                String s = String.format("Found warp %s\n", warpX.getKey());
                player.sendMessage(ChatColor.YELLOW + s);
                spawn = warpX.getValue();
            }
        }
        
        String s = String.format("Using %d, %d, %d as spawn\n", spawn.x, spawn.y, spawn.z);
        player.sendMessage(s);
        
        File dstDir = new File(plugin.getDataFolder(), zone.getName());
        if (!dstDir.exists()) {
            dstDir.mkdir();
        }
        
        copyLevel(zone.getName(), srcDir, dstDir, spawn);

        // Copy region files
        copyRegions(player, zone.getName(), rect, srcDir, dstDir);
    }
    
    public static class Rectangle
    {
        int x1, y1;
        int x2, y2;

        public Rectangle(int x1, int y1, int x2, int y2)
        {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
    }

    public static class Point
    {
        int x, y, z;

        public Point(int x, int y, int z)
        {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }
    
    private static void copyLevel(String zoneName, File srcDir, File dstDir, Point newSpawn)
    throws Exception
    {
        File srcLevel = new File(srcDir, "level.dat");
        File dstLevel = new File(dstDir, "level.dat");

        DataInputStream levelInput = new DataInputStream(new FileInputStream(srcLevel));
        CompoundTag levelData = NbtIo.readCompressed(levelInput);
        levelInput.close();

        if (levelData == null) {
            throw new RuntimeException("Failed to open level for processing.");
        }

        CompoundTag data = levelData.getCompound("Data");
        data.putString("LevelName", zoneName);
        data.putInt("SpawnX", newSpawn.x);
        data.putInt("SpawnY", newSpawn.y);
        data.putInt("SpawnZ", newSpawn.z);
        data.putInt("GameType", 1);

        DataOutputStream levelOutput = new DataOutputStream(new FileOutputStream(dstLevel));
        NbtIo.writeCompressed(levelData, levelOutput);
        levelOutput.close();
    }

    private static void copyRegions(BytecraftPlayer player, String zoneName, Rectangle rect, File srcDir, File dstDir)
    throws Exception
    {
        int minX = Math.min(rect.x1, rect.x2);
        int maxX = Math.max(rect.x1, rect.x2);
        int minY = Math.min(rect.y1, rect.y2);
        int maxY = Math.max(rect.y1, rect.y2);

        int minRegX = minX / 32 / 16 - 1;
        int maxRegX = maxX / 32 / 16 + 1;
        int minRegY = minY / 32 / 16 - 1;
        int maxRegY = maxY / 32 / 16 + 1;

        int regionFiles = Math.abs((maxRegX - minRegX) * (minRegY - maxRegY));

        String s = String.format("Exporting zone %s at (%d, %d) - (%d, %d), covering %d regions\n",
                zoneName, rect.x1, rect.y1, rect.x2, rect.y2, regionFiles);

        player.sendMessage(s);
        
        File srcRegionDir = new File(srcDir, "region");
        File dstRegionDir = new File(dstDir, "region");
        if (!dstRegionDir.exists()) {
            dstRegionDir.mkdir();
        }

        int regionCounter = 0,
            chunkCounter = 0,
            errorCounter = 0;
        for (int regionX = minRegX; regionX <= maxRegX; regionX++) {
            for (int regionY = minRegY; regionY <= maxRegY; regionY++) {
                String regionName = String.format("r.%d.%d.mca", regionX, regionY);

                File srcRegionFile = new File(srcRegionDir, regionName);
                File dstRegionFile = new File(dstRegionDir, regionName);

                if (!srcRegionFile.exists()) {
                    continue;
                }

                RegionFile srcRegion = new RegionFile(srcRegionFile);
                RegionFile dstRegion = new RegionFile(dstRegionFile);
                for (int x = 0; x < 32; x++) {
                    for (int z = 0; z < 32; z++) {
                        if (!srcRegion.hasChunk(x, z)) {
                            continue;
                        }

                        DataInputStream chunkInput = srcRegion.getChunkDataInputStream(x, z);
                        if (chunkInput == null) {
                            System.out.println("Failed to fetch input stream");
                            continue;
                        }

                        CompoundTag chunkData;
                        try {
                            chunkData = NbtIo.read(chunkInput);
                        }
                        catch (ZipException e) {
                            errorCounter++;
                            System.out.printf("Zip Error in file %s, chunk %d, %d\n",
                                              regionName, x, z);
                            continue;
                        }
                        catch (IOException e) {
                            errorCounter++;
                            System.out.printf("IO Error in file %s, chunk %d, %d\n",
                                              regionName,
                                              x, z);
                            continue;
                        }

                        chunkInput.close();

                        CompoundTag level = chunkData.getCompound("Level");
                        int xIdx = level.getInt("xPos") * 16;
                        int zIdx = level.getInt("zPos") * 16;

                        //System.out.printf("xIdx=%d minX=%d maxX=%d\n", xIdx, minX, maxX);
                        //System.out.printf("xIdx=%d minX=%d maxX=%d\n", zIdx, minY, maxY);

                        if (xIdx < minX || xIdx > maxX) {
                            continue;
                        }
                        if (zIdx < minY || zIdx > maxY) {
                            continue;
                        }

                        chunkCounter++;

                        DataOutputStream chunkOutput = dstRegion.getChunkDataOutputStream(x, z);
                        if (chunkOutput == null) {
                            System.out.println("Failed to fetch input stream");
                            continue;
                        }

                        NbtIo.write(chunkData, chunkOutput);

                        chunkOutput.close();
                    }
                }

                srcRegion.close();

                regionCounter++;
            }
        }

        System.out.printf("Found %d chunks\n", chunkCounter);
        System.out.printf("Got %d errors\n", errorCounter);
    }
    
    private Rectangle getZoneRect(String zoneName)
    {
        String sql = "SELECT * FROM zone INNER JOIN zone_rect USING (zone_name) WHERE zone_name = ?";
        ResultSet rs = null;
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setString(1, zoneName);
            stm.execute();

            rs = stm.getResultSet();
            if (!rs.next()) {
                return null;
            }

            int x1 = rs.getInt("rect_x1");
            int y1 = rs.getInt("rect_z1");
            int x2 = rs.getInt("rect_x2");
            int y2 = rs.getInt("rect_z2");

            return new Rectangle(x1, y1, x2, y2);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }
    
    private Map<String, Point> getWarps(Rectangle rect)
    {
        String sql = "SELECT * FROM warps WHERE (x BETWEEN ? AND ?) AND (z BETWEEN ? AND ?) ";
        Map<String, Point> points = new HashMap<String, Point>();
        try (PreparedStatement stm = conn.prepareStatement(sql)){
            stm.setInt(1, Math.min(rect.x1, rect.x2));
            stm.setInt(2, Math.max(rect.x1, rect.x2));
            stm.setInt(3, Math.min(rect.y1, rect.y2));
            stm.setInt(4, Math.max(rect.y1, rect.y2));
            stm.execute();

            try(ResultSet rs = stm.getResultSet()){
                while (rs.next()) {
                    String name = rs.getString("name");
                    int x = rs.getInt("x");
                    int y = rs.getInt("y");
                    int z = rs.getInt("z");

                    points.put(name, new Point(x, y, z));
                }
            }

            return points;
        }catch(SQLException e){
            throw new RuntimeException(e);
        }
    }

}
