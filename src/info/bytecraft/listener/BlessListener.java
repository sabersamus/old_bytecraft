package info.bytecraft.listener;

import java.util.HashSet;
import java.util.Set;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.Notification;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IBlessDAO;
import info.bytecraft.database.IContext;

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

            try (IContext ctx = Bytecraft.createContext()) {
                IBlessDAO dao = ctx.getBlessDAO();
                dao.bless(event.getClickedBlock(), target);
                target.sendNotification(Notification.BLESS, ChatColor.AQUA
                        + "Your god has blessed a block in your name!");
                player.sendMessage(ChatColor.AQUA
                        + "You have blessed a block for "
                        + target.getDisplayName());
                player.setBlessTarget(null);
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }
            event.setCancelled(true);
            return;
        }
        else {
            if (event.getAction() == Action.LEFT_CLICK_BLOCK
                    || event.getAction() == Action.RIGHT_CLICK_BLOCK) {
                try (IContext ctx = Bytecraft.createContext();) {
                    IBlessDAO dao = ctx.getBlessDAO();

                    if (dao.isBlessed(event.getClickedBlock())) {
                        if (!player.getName().equalsIgnoreCase(
                                dao.getOwner(event.getClickedBlock()))) {
                            player.sendMessage(ChatColor.RED + "Blessed to: "
                                    + ChatColor.AQUA
                                    + dao.getOwner(event.getClickedBlock()));
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
                } catch (DAOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
}
