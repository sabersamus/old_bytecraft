package info.bytecraft.commands;

import org.bukkit.ChatColor;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Zone;
import info.bytecraft.api.Zone.Flag;
import info.bytecraft.api.Zone.Permission;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IZoneDAO;

public class ZoneCommand extends AbstractCommand
{

    public ZoneCommand(Bytecraft instance)
    {
        super(instance, "zone");
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if (args.length == 2)// zone create|delete name
        {
            if ("create".equalsIgnoreCase(args[0])) {
                if (player.isAdmin()) {
                    if (!zoneExists(args[1])) {
                        if (createZone(player, args[1])) {
                            player.sendMessage(ChatColor.RED + "Created zone "
                                    + args[1]);
                            return true;
                        }
                    }
                }
            }
            else if ("delete".equalsIgnoreCase(args[0])) {
                if (player.isAdmin()) {
                    if (!zoneExists(args[1])) {
                        player.sendMessage(ChatColor.RED + "Zone " + args[1]
                                + " doesn't exist");
                    }
                    else {
                        deleteZone(args[1]);
                        player.sendMessage(ChatColor.RED + "Deleted zone "
                                + args[1]);
                    }
                }
            }
        }
        else if (args.length == 3) {// zone [flag] [name] [true/false]
            if (!zoneExists(args[1])) {
                player.sendMessage(ChatColor.RED + "Zone " + args[1]
                        + " not found");
            }
            else {
                Zone zone = plugin.getZone(args[1]);
                Permission p = zone.getUser(player);
                if ((p != null && p == Permission.OWNER) || player.isAdmin()) {
                    if ("pvp".equalsIgnoreCase(args[0])) {
                        changeSetting(args[1], Flag.PVP,
                                (args[2].equalsIgnoreCase("true")) ? "true"
                                        : "false");
                        player.sendMessage(ChatColor.RED
                                + "["
                                + zone.getName()
                                + "] Changed pvp to "
                                + ((args[2].equalsIgnoreCase("true")) ? "true"
                                        : "false"));
                    }
                    else if ("whitelist".equalsIgnoreCase(args[0])) {
                        changeSetting(args[1], Flag.WHITELIST,
                                (args[2].equalsIgnoreCase("true")) ? "true"
                                        : "false");
                        player.sendMessage(ChatColor.RED
                                + "["
                                + zone.getName()
                                + "] Changed whitelist to "
                                + ((args[2].equalsIgnoreCase("true")) ? "true"
                                        : "false"));
                    }
                    else if ("build".equalsIgnoreCase(args[0])) {
                        changeSetting(args[1], Flag.BUILD,
                                (args[2].equalsIgnoreCase("true")) ? "true"
                                        : "false");
                        player.sendMessage(ChatColor.RED
                                + "["
                                + zone.getName()
                                + "] Changed build settings to "
                                + ((args[2].equalsIgnoreCase("true")) ? "true"
                                        : "false"));
                    }
                    else if ("hostile".equalsIgnoreCase(args[0])) {
                        changeSetting(args[1], Flag.HOSTILE,
                                (args[2].equalsIgnoreCase("true")) ? "true"
                                        : "false");
                        player.sendMessage(ChatColor.RED
                                + "["
                                + zone.getName()
                                + "] Changed hostile mob spawning to "
                                + ((args[2].equalsIgnoreCase("true")) ? "true"
                                        : "false"));
                    }
                    else if ("deluser".equalsIgnoreCase(args[0])) {
                        String target = args[2];
                        if (delUser(zone, target, player))
                            return true;
                        else {
                            player.sendMessage(ChatColor.RED + "["
                                    + zone.getName()
                                    + "] Could not find player " + target
                                    + " in zone " + zone.getName());
                        }
                    }
                }
            }
        }
        else if (args.length == 4) {
            if ("adduser".equalsIgnoreCase(args[0])) {
                if (!zoneExists(args[1])) {
                    player.sendMessage(ChatColor.RED + "Zone " + args[1]
                            + " not found");
                }
                else {
                    Zone zone = plugin.getZone(args[1]);
                    Permission p = zone.getUser(player);
                    if ((p != null && p == Permission.OWNER)
                            || player.isAdmin()) {
                        String target = args[2];
                        Permission p2 =
                                Permission.valueOf(args[3].toUpperCase());
                        if (p2 != null) {
                            this.addUser(zone, target, p2, player);
                        }
                    }
                }
            }
        }
        else if (args.length >= 4) {
            if (args[0].equalsIgnoreCase("entermsg")
                    || args[0].equalsIgnoreCase("exitmsg")) {
                if (!zoneExists(args[1])) {
                    player.sendMessage(ChatColor.RED + "Zone " + args[1]
                            + " does not exist");
                }
                else {
                    Permission p = plugin.getZone(args[1]).getUser(player);
                    if ((p != null && p == Permission.OWNER)
                            || player.isAdmin()) {
                        StringBuilder sb = new StringBuilder();
                        for (int i = 2; i < args.length; i++) {
                            sb.append(args[i] + " ");
                        }
                        Flag f =
                                args[0].equalsIgnoreCase("entermsg") ? Flag.ENTERMSG
                                        : Flag.EXITMSG;
                        changeSetting(args[1], f, sb.toString().trim());
                        player.sendMessage(ChatColor.RED + "[" + args[1]
                                + "] Changed " + f.name().toLowerCase()
                                + " to: " + sb.toString().trim());
                    }
                }
            }
        }
        return true;
    }

    private boolean createZone(BytecraftPlayer player, String name)
    {
        if (zoneExists(name)) {
            player.sendMessage(ChatColor.RED + "Zone " + name
                    + " already exists");
            return false;
        }
        Zone zone = new Zone(name);
        zone.setWorld(player.getWorld().getName());
        for (Zone other : plugin.getZones(player.getWorld().getName())) {
            if (zone.intersects(other)) {
                player.sendMessage(ChatColor.RED
                        + "Zone intersects with zone: " + other.getName()
                        + " . Please try somewhere else.");
                return false;
            }
        }
        try (IContext ctx = plugin.createContext()) {
            IZoneDAO dao = ctx.getZoneDAO();
            dao.createZone(zone, player);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private boolean deleteZone(String name)
    {
        try (IContext ctx = plugin.createContext()) {
            IZoneDAO dao = ctx.getZoneDAO();
            dao.deleteZone(name);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    private boolean zoneExists(String name)
    {
        return plugin.getZone(name) != null;
    }

    private void changeSetting(String zone, Flag flag, String value)
    {
        try (IContext ctx = plugin.createContext()) {
            IZoneDAO dao = ctx.getZoneDAO();
            dao.updateFlag(zone, flag, value);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private void addUser(Zone zone, String target, Permission p,
            BytecraftPlayer player)
    {
        try (IContext ctx = plugin.createContext()) {
            IZoneDAO dao = ctx.getZoneDAO();

            BytecraftPlayer victim = plugin.getPlayerOffline(target);

            String addConfirm = p.getAddedConfirm();
            player.sendMessage(ChatColor.RED
                    + "["
                    + zone.getName()
                    + "] "
                    + String.format(addConfirm, victim.getDisplayName()
                            + ChatColor.RED, zone.getName()));

            BytecraftPlayer player2 = plugin.getPlayer(target);
            if (player2 != null) {
                String addNotif = p.getAddedNotif();
                player2.sendMessage(ChatColor.RED + "[" + zone.getName() + "] "
                        + String.format(addNotif, zone.getName()));
            }

            dao.addUser(zone, target, p);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean delUser(Zone zone, String name, BytecraftPlayer deleter)
    {
        try (IContext ctx = plugin.createContext()) {
            IZoneDAO dao = ctx.getZoneDAO();
            BytecraftPlayer target = plugin.getPlayerOffline(name);
            if (target == null) {
                deleter.sendMessage(ChatColor.RED + "Could not find user.");
                return false;
            }
            Permission p = zone.getUser(target);
            if (p == null) {
                deleter.sendMessage(ChatColor.RED + "[" + zone.getName() + "] "
                        + target.getDisplayName() + ChatColor.RED
                        + " does not have any permissions in " + zone.getName());
                return false;
            }
            String delConfirm = p.getDelConfirm();
            deleter.sendMessage(ChatColor.RED
                    + "["
                    + zone.getName()
                    + "] "
                    + String.format(delConfirm, target.getDisplayName()
                            + ChatColor.RED, zone.getName()));
            BytecraftPlayer player2 = plugin.getPlayer(name);
            if (player2 != null) {
                String delNotif = p.getDelNotif();
                player2.sendMessage(ChatColor.RED + "[" + zone.getName() + "] "
                        + String.format(delNotif, zone.getName()));
            }
            return dao.deleteUser(zone, name);
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }
    }
}
