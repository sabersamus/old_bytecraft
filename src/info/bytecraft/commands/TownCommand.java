package info.bytecraft.commands;

import static org.bukkit.ChatColor.GOLD;
import static org.bukkit.ChatColor.WHITE;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.math.Point;
import info.bytecraft.api.math.Rectangle;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IZoneDAO;
import info.bytecraft.zones.Zone;
import info.bytecraft.zones.Zone.Flag;
import info.bytecraft.zones.Zone.Permission;

public class TownCommand extends AbstractCommand
{

    public TownCommand(Bytecraft instance)
    {
        super(instance, "town");
    }
    
    
    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        Location loc = player.getLocation();
        Zone zone = plugin.getZoneAt(loc.getWorld(), new Point(loc.getBlockX(), loc.getBlockZ()));
        if(zone == null){
            player.sendMessage(ChatColor.RED + "You are not in a zone!");
            return true;
        }
        
        Permission perm = zone.getUser(player);
        
        if(args.length == 0){
            player.sendMessage(ChatColor.YELLOW + "You are currently in zone: " + zone.getName());
        }else if(args.length == 1){
            //town info
            if("info".equalsIgnoreCase(args[0])){
                return info(player, zone);
            }
        }else if(args.length == 2){
            //town deluser name
            if ("deluser".equalsIgnoreCase(args[0])) {
                if ((perm != null && perm == Permission.OWNER) || player.getRank().canEditZones()) {
                    
                    BytecraftPlayer target = plugin.getPlayerOffline(args[1]);
                    
                    if(target == null){
                        player.sendMessage(ChatColor.RED + "Player not found " + args[1]);
                        return true;
                    }
                    
                    this.delUser(zone, target, player);
                    return true;
                }
            }
        }else if(args.length == 3){
            //town adduser <player> rank
            //town flag <flag> <true/false>
            if("adduser".equalsIgnoreCase(args[0])){
                if((perm != null && perm == Permission.OWNER) || player.getRank().canEditZones()){
                    
                    BytecraftPlayer target = plugin.getPlayerOffline(args[1]);
                    
                    if(target == null){
                        player.sendMessage(ChatColor.RED + "Player not found " + args[1]);
                        return true;
                    }
                    
                    Permission otherPerm = Permission.fromString(args[2]);
                    if(otherPerm != null){
                        this.addUser(zone, player, target, otherPerm);
                        return true;
                    }else{
                        player.sendMessage(ChatColor.RED + "Invalid permission");
                        return true;
                    }
                }
            }else if("flag".equalsIgnoreCase(args[0])){
                if((perm != null && perm == Permission.OWNER) || player.getRank().canEditZones()){
                    Flag flag = Flag.fromString(args[1]);
                    if (flag == null) {
                        player.sendMessage(ChatColor.RED + "Flag not found");
                        return true;
                    }
                    boolean value = Boolean.parseBoolean(args[2]);
                    this.updateFlag(zone, flag, value);
                    player.sendMessage(ChatColor.RED + "[" + zone.getName() + "] Changed " + flag.name().toLowerCase() + " to " + value);
                }
            }
        }else{
            if (args[0].equalsIgnoreCase("entermsg")
                    || args[0].equalsIgnoreCase("exitmsg")) {
                Permission p = zone.getUser(player);
                if ((p != null && p == Permission.OWNER) || player.getRank().canEditZones()) {
                    Flag flag =
                            args[0].equalsIgnoreCase("entermsg") ? Flag.ENTERMSG
                                    : Flag.EXITMSG;

                    StringBuilder sb = new StringBuilder();
                    for (int i = 1; i < args.length; i++) {
                        sb.append(args[i] + " ");
                    }

                    this.changeSetting(zone, flag, sb.toString());
                    player.sendMessage(ChatColor.RED + "[" + zone.getName()
                            + "] Changed " + flag.name().toLowerCase() + " to " + sb.toString());
                }
            }
        }
        
        return true;
    }
    
    private boolean info(BytecraftPlayer player, Zone zone)
    {
        Rectangle rect = zone.getRectangle();
        player.sendMessage(GOLD + "Information about " + zone.getName());
        player.sendMessage(GOLD + "ID: " + WHITE + zone.getId());
        player.sendMessage((GOLD + "Rect: " + WHITE + "("+ 
        rect.getLeft() + ", " + + rect.getTop() + ") (" 
                + rect.getRight()+ ", " + + rect.getBottom() + ")").replace(",", ChatColor.GOLD + "," + ChatColor.WHITE));
        player.sendMessage(GOLD
                + "Enter: " + WHITE
                + (!zone.hasFlag(Flag.WHITELIST) ? "Everyone (true)"
                        : "Only allowed (false)"));
        player.sendMessage(GOLD
                + "Build: " + WHITE
                + (zone.hasFlag(Flag.BUILD) ? "Everyone (true)"
                        : "Only makers (false)"));
        player.sendMessage(GOLD + "PVP: " + WHITE + zone.hasFlag(Flag.PVP));
        player.sendMessage(GOLD + "Hostiles: " + WHITE + zone.hasFlag(Flag.HOSTILES));
        player.sendMessage(GOLD + "Enter message: " + WHITE + zone.getEnterMessage());
        player.sendMessage(GOLD + "Exit Message: " + WHITE + zone.getExitMessage());
        return true;
    }
    
    private void addUser(Zone zone, BytecraftPlayer player, BytecraftPlayer target, Permission p)
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
                return true;
            }
            String delConfirm = p.getDelConfirm();
            deleter.sendMessage(ChatColor.RED + "[" + zone.getName() + "] "
                    + String.format(delConfirm, (target.getNameColor() + target.getName()) + ChatColor.RED, zone.getName()));
            
            BytecraftPlayer target2 = plugin.getPlayer(target.getName());
            
            if(target2 != null){
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
}
