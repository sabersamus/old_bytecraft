package info.bytecraft.listener;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Notification;
import info.bytecraft.database.ConnectionPool;
import info.bytecraft.database.DBBlessDAO;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BlessListener implements Listener
{

    private static Set<Material> allowedBlocks;

    private Bytecraft plugin;

    public BlessListener(Bytecraft bytecraft)
    {
        this.plugin = bytecraft;
        allowedBlocks = new HashSet<Material>();
        allowedBlocks.add(Material.CHEST);
        allowedBlocks.add(Material.CAULDRON);
        allowedBlocks.add(Material.ENCHANTMENT_TABLE);
        allowedBlocks.add(Material.FURNACE);
        allowedBlocks.add(Material.BURNING_FURNACE);
        allowedBlocks.add(Material.WOOD_DOOR);
        allowedBlocks.add(Material.WOODEN_DOOR);
        allowedBlocks.add(Material.LEVER);
        allowedBlocks.add(Material.STONE_BUTTON);
        allowedBlocks.add(Material.STONE_PLATE);
        allowedBlocks.add(Material.WOOD_PLATE);
        allowedBlocks.add(Material.WORKBENCH);
        allowedBlocks.add(Material.SIGN_POST);
        allowedBlocks.add(Material.DIODE);
        allowedBlocks.add(Material.DIODE_BLOCK_OFF);
        allowedBlocks.add(Material.TRAP_DOOR);
        allowedBlocks.add(Material.DIODE_BLOCK_ON);
        allowedBlocks.add(Material.JUKEBOX);
        allowedBlocks.add(Material.SIGN);
        allowedBlocks.add(Material.FENCE_GATE);
        allowedBlocks.add(Material.DISPENSER);
        allowedBlocks.add(Material.WOOD_BUTTON);
    }

    @EventHandler
    public void onClick(PlayerInteractEvent event)
    {
        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK && player.isAdmin()
                && player.getItemInHand().getType() == Material.BONE
                && allowedBlocks.contains(event.getClickedBlock().getType())) {
            BytecraftPlayer target = player.getBlessTarget();
            if (target == null) {
                player.sendMessage(ChatColor.RED + "Use /bless [name] first");
                return;
            }

            Connection conn = null;
            try {
                conn = ConnectionPool.getConnection();
                DBBlessDAO dbBless = new DBBlessDAO(conn);
                dbBless.bless(event.getClickedBlock(), target);
                target.sendMessage(ChatColor.AQUA
                        + "Your god has blessed a block in your name!");
                target.sendNotification(Notification.BLESS);
                player.sendMessage(ChatColor.AQUA
                        + "You have blessed a block for "
                        + target.getDisplayName());
                player.setBlessTarget(null);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                    }
                }
            }
            event.setCancelled(true);
            return;
        }
        else {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK
                    || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Connection conn = null;
                try {
                    conn = ConnectionPool.getConnection();
                    DBBlessDAO dbBless = new DBBlessDAO(conn);

                    if (dbBless.isBlessed(event.getClickedBlock())) {
                        if (!player.getName().equalsIgnoreCase(
                                dbBless.getOwner(event.getClickedBlock()))) {
                            player.sendMessage(ChatColor.RED + "Blessed to: "
                                    + ChatColor.AQUA
                                    + dbBless.getOwner(event.getClickedBlock()));
                            if (!player.isAdmin()) {
                                event.setCancelled(true);
                                return;
                            }
                        }
                        else {
                            player.sendMessage(ChatColor.AQUA
                                    + "Blessed to you");
                        }
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                } finally {
                    if (conn != null) {
                        try {
                            conn.close();
                        } catch (SQLException e) {
                        }
                    }
                }
            }
        }
    }
}
