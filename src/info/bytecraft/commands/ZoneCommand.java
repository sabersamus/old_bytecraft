package info.bytecraft.commands;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.RED;
import static org.bukkit.ChatColor.WHITE;

import java.util.Collection;
import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.block.Block;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IZoneDAO;
import info.bytecraft.zones.Zone;
import info.bytecraft.zones.Zone.Flag;
import info.bytecraft.zones.Zone.Permission;
import info.bytecraft.zones.ZoneWorld;

import info.tregmine.quadtree.IntersectionException;
import info.tregmine.quadtree.Rectangle;

public class ZoneCommand extends AbstractCommand
{

    public ZoneCommand(Bytecraft instance)
    {
        super(instance, "zone");
    }
    
    private String messageFromArgs(String[] args)
    {
        StringBuilder buffer = new StringBuilder();
        for(int i = 3; i < args.length; i++){
            buffer.append(args[i]);
            buffer.append(" ");
        }
        
        return buffer.toString();
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (args.length == 2)// zone create|delete name
        {
            if ("create".equalsIgnoreCase(args[0])) {
                if (player.getRank().canCreateZones()) {
                    this.createZone(player, args);
                }else{
                    player.sendMessage(getInvalidPermsMessage());
                    return true;
                }
            }
            else if ("delete".equalsIgnoreCase(args[0])) {
                if (player.getRank().canCreateZones()) {
                    if (!zoneExists(player, args[1])) {
                        player.sendMessage(ChatColor.RED + "Zone " + args[1]
                                + " doesn't exist");
                    }
                    else {
                        this.deleteZone(player, args);
                    }
                }else{
                    player.sendMessage(getInvalidPermsMessage());
                    return true;
                }
            }else if("info".equalsIgnoreCase(args[0])){
                if(!zoneExists(player, args[1])){
                    player.sendMessage(ChatColor.RED + "Zone " + args[1] + " not found");
                    return true;
                }
                ZoneWorld world = plugin.getWorld(player.getWorld());
                Zone zone = world.getZone(args[1]);
                
                this.infoZone(player, zone, false);
                
                return true;
            }
        } else if (args.length == 3){ //zone deluser zone player
            if("deluser".equalsIgnoreCase(args[0])){
                if(!zoneExists(player, args[1])){
                    player.sendMessage(ChatColor.RED + "Zone not found");
                    return true;
                }
                ZoneWorld world = plugin.getWorld(player.getWorld());
                Zone zone = world.getZone(args[1]);
                Permission p = zone.getUser(player);
                if ((p != null && p == Permission.OWNER) || player.getRank().canEditZones()) {
                    BytecraftPlayer target = plugin.getPlayerOffline(args[2]);
                    
                    if(target == null){
                        player.sendMessage(ChatColor.RED + "Player not found " + args[2]);
                        return true;
                    }

                    this.delUser(zone, target, player);
                }
            }else if("info".equalsIgnoreCase(args[0])){
                if("perm".equalsIgnoreCase(args[2])){
                    if(!zoneExists(player, args[1])){
                        player.sendMessage(ChatColor.RED + "Zone not found");
                        return true;
                    }
                    ZoneWorld world = plugin.getWorld(player.getWorld());
                    Zone zone = world.getZone(args[1]);
                    
                    this.infoZone(player, zone, true);
                }
            }
        } else if (args.length == 4) {
            if ("adduser".equalsIgnoreCase(args[0])) {
                if (!zoneExists(player, args[1])) {
                    player.sendMessage(ChatColor.RED + "Zone " + args[1]
                            + " not found");
                    return true;
                } 
                ZoneWorld world = plugin.getWorld(player.getWorld());
                Zone zone = world.getZone(args[1]);
                
                Permission p = zone.getUser(player);
                if ((p != null && p == Permission.OWNER) || player.getRank().canEditZones()) {
                    
                    BytecraftPlayer target = plugin.getPlayerOffline(args[2]);
                    
                    if(target == null){
                        player.sendMessage(ChatColor.RED + "Player not found " + args[2]);
                        return true;
                    }

                    Permission p2 = Permission.fromString(args[3]);
                    if (p2 == null) {
                        player.sendMessage(ChatColor.RED + args[3] + " is not a valid permission");
                    }else{
                        this.addUser(zone, target, p2, player);
                    }
                }
            }else if(args[0].equalsIgnoreCase("flag")){
                if(!zoneExists(player, args[1])){
                    player.sendMessage(ChatColor.RED + "Zone " + args[1] + " not found");
                    return true;
                }//zone flag zone flag_name value
                ZoneWorld world = plugin.getWorld(player.getWorld());
                
                Zone zone = world.getZone(args[1]);
                
                Permission p = zone.getUser(player);
                if ((p != null && p == Permission.OWNER) || player.getRank().canEditZones()) {
                    Flag flag = Flag.valueOf(args[2].toUpperCase());
                    if (flag == null) {
                        player.sendMessage(ChatColor.RED + "Flag not found");
                        return true;
                    }
                    
                    if(flag == Flag.CREATIVE && !player.getRank().canEditZones()){
                        player.sendMessage(ChatColor.RED + "Only admins can set this flag!");
                        return true;
                    }
                    
                    if(flag == Flag.ENTERMSG || flag == Flag.EXITMSG){
                        //zone flag0 name1 flag_name2 message3+
                        String message = messageFromArgs(args);
                        this.changeSetting(zone, flag, message);
                        player.sendMessage(ChatColor.RED + "[" + zone.getName()
                                + "] Changed " + flag.name().toLowerCase() + " to " + message);
                        return true;
                    }

                    boolean value = Boolean.parseBoolean(args[3]);
                    this.updateFlag(zone, flag, value);
                    player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] Changed " + flag.name().toLowerCase() + " to " + value);
                }
            }
        }
        return true;
    }

    private void createZone(BytecraftPlayer player, String[] args)
    {
        ZoneWorld world = plugin.getWorld(player.getWorld());
        if(world == null){
            return;
        }
        
        if (args.length < 2) {
            player.sendMessage("syntax: /zone create [zone-name]");
            return;
        }
        
        String name = args[1];
        if (world.zoneExists(name)) {
            player.sendMessage(RED + "A zone named " + name
                    + " does already exist.");
            return;
        }
        
        Block b1 = player.getZoneBlock1();
        Block b2 = player.getZoneBlock2();
        if (b1 == null || b2 == null) {
            player.sendMessage("Please select two corners");
            return;
        }
        
        Rectangle rect =
                new Rectangle(b1.getX(), b1.getZ(), b2.getX(), b2.getZ());

        Zone zone = new Zone();
        zone.setWorld(world.getName());
        zone.setName(name);
        zone.setRectangle(rect);
        zone.setPermissions(new HashMap<String, Permission>());
        zone.setEnterMessage("Welcome to " + name + "!");
        zone.setExitMessage("Now leaving " + name + ".");
        zone.addPermissions(player.getName(), Permission.OWNER);
        
        player.sendMessage(RED + "Creating zone at " + rect);
        
        try {
            world.addZone(zone);
            player.sendMessage(RED + "[" + zone.getName() + "] "
                    + "Zone created successfully.");
        } catch (IntersectionException e) {
            player.sendMessage(RED
                    + "The zone you tried to create overlaps an existing zone.");
            return;
        }
        
        try (IContext ctx = plugin.createContext()) {
            IZoneDAO dao = ctx.getZoneDAO();
            dao.createZone(zone, player);
            dao.addUser(zone, player.getName(), Permission.OWNER);
            zone.addPermissions(name, Permission.OWNER);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteZone(BytecraftPlayer player, String[] args)
    {
        ZoneWorld world = plugin.getWorld(player.getWorld());
        if (world == null) {
            return;
        }

        if (args.length < 2) {
            player.sendMessage("syntax: /zone delete [name]");
            return;
        }

        String name = args[1];

        Zone zone = world.getZone(name);
        if (zone == null) {
            player.sendMessage(RED + "A zone named " + name
                    + " does not exist.");
            return;
        }

        world.deleteZone(name);
        player.sendMessage(RED + "[" + name + "] " + "Zone deleted.");

        try (IContext ctx = plugin.createContext()) {
            IZoneDAO dao = ctx.getZoneDAO();
            dao.deleteZone(zone.getName());
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean zoneExists(BytecraftPlayer player, String name)
    {
        ZoneWorld world = plugin.getWorld(player.getWorld());
        return world.zoneExists(name);
    }

    private void changeSetting(Zone zone, Flag flag, String value)
    {
        if(flag == Flag.ENTERMSG){
            zone.setEnterMessage(value);
        }else if(flag == Flag.EXITMSG){
            zone.setExitMessage(value);
        }else{
            zone.setFlag(flag, Boolean.valueOf(value));
        }
        
        try (IContext ctx = plugin.createContext()) {
            IZoneDAO dao = ctx.getZoneDAO();
            dao.updateFlag(zone, flag, value);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addUser(Zone zone, BytecraftPlayer target, Permission p,
            BytecraftPlayer player)
    {
        try (IContext ctx = plugin.createContext()) {
            IZoneDAO dao = ctx.getZoneDAO();
            String addConfirm = p.getAddedConfirm();
            player.sendMessage(ChatColor.RED
                    + "[" + zone.getName() + "] "
                    + String.format(addConfirm, (target.getNameColor() + target.getName()) + ChatColor.RED, zone.getName()));
            
            BytecraftPlayer target2 = plugin.getPlayer(target.getName());
            
            if(target2 != null){
                String addNotif = p.getAddedNotif();
                target2.sendMessage(ChatColor.RED + "[" + zone.getName() + "] "
                        + String.format(addNotif, zone.getName()));
            }
            dao.addUser(zone, target.getName(), p);
            zone.addPermissions(target.getName(), p);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean delUser(Zone zone, BytecraftPlayer target, BytecraftPlayer deleter)
    {
        try (IContext ctx = plugin.createContext()) {
            IZoneDAO dao = ctx.getZoneDAO();
            Permission p = zone.getUser(target);
            if (p == null) {
                deleter.sendMessage(ChatColor.RED + "[" + zone.getName() + "] "
                        + (target.getNameColor() + target.getName()) + ChatColor.RED
                        + " does not have any permissions in " + zone.getName());
                return false;
            }
            String delConfirm = p.getDelConfirm();
            deleter.sendMessage(ChatColor.RED + "[" + zone.getName() + "] "
                    + String.format(delConfirm, (target.getNameColor() + target.getName()) + ChatColor.RED, zone.getName()));
            
            BytecraftPlayer target2 = plugin.getPlayer(target.getName());

            if (target2 != null) {
                String delNotif = p.getDelNotif();
                target2.sendMessage(ChatColor.RED + "[" + zone.getName() + "] "
                        + String.format(delNotif, zone.getName()));
            }
            
            zone.removePermission(target.getName());
            return dao.deleteUser(zone, target.getName());
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void updateFlag(Zone zone, Flag flag, boolean value)
    {
        zone.setFlag(flag, value);
        
        try(IContext ctx = plugin.createContext()){
            IZoneDAO dao = ctx.getZoneDAO();
            dao.updateFlag(zone, flag, String.valueOf(value));
        }catch(DAOException e){
            throw new RuntimeException(e);
        }
    }
    
    private void infoZone(BytecraftPlayer player, Zone zone, boolean perm)
    {
        if(!perm){
            Rectangle rect = zone.getRectangle();
            player.sendMessage(GOLD + "Information about " + zone.getName());
            player.sendMessage(GOLD + "ID: " + WHITE + zone.getId());
            player.sendMessage((GOLD + "Rect: " + WHITE + "("+ 
            rect.getLeft() + ", " + + rect.getTop() + ") (" 
                    + rect.getRight()+ ", " + + rect.getBottom() + ")").replace(",", ChatColor.GOLD + "," + ChatColor.WHITE));
            player.sendMessage(GOLD
                    + "Enter: " + WHITE
                    + (zone.hasFlag(Flag.WHITELIST) ? "Everyone (true)"
                            : "Only allowed (false)"));
            player.sendMessage(GOLD
                    + "Build: " + WHITE
                    + (zone.hasFlag(Flag.BUILD) ? "Everyone (true)"
                            : "Only makers (false)"));
            player.sendMessage(GOLD + "PVP: " + WHITE + zone.hasFlag(Flag.PVP));
            player.sendMessage(GOLD + "Hostiles: " + WHITE + zone.hasFlag(Flag.HOSTILES));
            player.sendMessage(GOLD + "Seperate inventory: " + WHITE + zone.hasFlag(Flag.INVENTORY));
            player.sendMessage(GOLD + "Creative: " + WHITE + zone.hasFlag(Flag.CREATIVE));
            player.sendMessage(GOLD + "Enter message: " + WHITE + zone.getEnterMessage());
            player.sendMessage(GOLD + "Exit Message: " + WHITE + zone.getExitMessage());
        }else{
            Collection<String> users = zone.getUsers();
            if(users.isEmpty()){
                player.sendMessage(ChatColor.RED + "No permissions for " + zone.getName());
                return;
            }
            player.sendMessage(ChatColor.YELLOW + "Permissions for " + ChatColor.RED + zone.getName());
            for(String name: users){
                BytecraftPlayer offline = plugin.getPlayerOffline(name);
                player.sendMessage(offline.getNameColor() + offline.getName() + ChatColor.YELLOW + " - " + zone.getUser(offline));
            }
        }
    }
}
