package info.bytecraft.listener;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

public class ElevatorListener implements Listener
{
    private Bytecraft plugin;
    
    public ElevatorListener(Bytecraft plugin)
    {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event){

        BytecraftPlayer player = plugin.getPlayer(event.getPlayer());

        if (event.getPlayer().getItemInHand().getType().equals(Material.PAPER) && event.getBlock().getType().equals(Material.DIRT)) {
            ItemStack paper = player.getItemInHand();
            ItemMeta meta = paper.getItemMeta();
            if(meta.hasDisplayName()){
                if(meta.getDisplayName().equals(ChatColor.GREEN + "DIRT -> SPONGE Coupon")){
                    event.getBlock().setType(Material.SPONGE);
                    paper.setAmount(paper.getAmount() - 1);
                    if(paper.getAmount() <= 0) {
                        event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                    }
                    event.setCancelled(true);
                }
            }
        }
    }
    
    
    @EventHandler
    public void onUseElevator(PlayerInteractEvent event)
    {
        Player player = event.getPlayer();
        Block block = event.getClickedBlock();
        if (block == null) {
            return;
        }

        if (block.getType().equals(Material.STONE_BUTTON)) {
            Location loc = player.getLocation();
            World world = player.getWorld();
            Block standOn = world.getBlockAt(loc.getBlockX(), loc.getBlockY()-1, loc.getBlockZ());

            if (Material.SPONGE.equals(standOn.getType())) {
                Location bLoc = block.getLocation();
                Block signBlock = world.getBlockAt(bLoc.getBlockX(), bLoc.getBlockY()+1, bLoc.getBlockZ());

                if(signBlock.getState() instanceof Sign) {
                    Sign sign = (Sign) signBlock.getState();

                    if (sign.getLine(0).contains("up")) {

                        sign.setLine(0, ChatColor.DARK_RED + "Elevator");
                        sign.setLine(2, ChatColor.GOLD + "[" + ChatColor.DARK_GRAY + "UP" + ChatColor.GOLD + "]");
                        sign.update(true);

                        player.sendMessage(ChatColor.GREEN + "Elevator Setup!");

                    }

                    else if (sign.getLine(0).equals(ChatColor.DARK_RED + "Elevator")
                            && sign.getLine(2).equals(ChatColor.GOLD + "[" + ChatColor.DARK_GRAY + "UP" + ChatColor.GOLD + "]")) {

                        int i = standOn.getLocation().getBlockY();

                        while (i < 255) {
                            i++;
                            Block sponge = event.getPlayer().getWorld().getBlockAt(standOn.getLocation().getBlockX(),  i, standOn.getLocation().getBlockZ());

                            if (sponge.getType().equals(Material.SPONGE)) {
                                i=256;
                                Location tp = sponge.getLocation();
                                tp.setY(tp.getBlockY() + 1.5);
                                tp.setZ(tp.getBlockZ() + 0.5);
                                tp.setX(tp.getBlockX() + 0.5);
                                tp.setPitch(player.getLocation().getPitch());
                                tp.setYaw(player.getLocation().getYaw());

                                player.teleport(tp);

                            }
                        }
                        player.sendMessage(ChatColor.AQUA + "Going up");
                    }

                    // sign.setLine(0, ChatColor.DARK_PURPLE + "Elevator");
                    // sign.setLine(2, ChatColor.GOLD + "[ " + ChatColor.DARK_GRAY + "UP" + ChatColor.GOLD + " ]");

                    if (sign.getLine(0).contains("down")) {

                        sign.setLine(0, ChatColor.DARK_RED + "Elevator");
                        sign.setLine(2, ChatColor.GOLD + "[" + ChatColor.DARK_GRAY + "DOWN" + ChatColor.GOLD + "]");
                        sign.update(true);

                        player.sendMessage(ChatColor.GREEN + "Elevator Setup!");

                    }

                    else if (sign.getLine(0).equals(ChatColor.DARK_RED + "Elevator")
                            && sign.getLine(2).equals(ChatColor.GOLD + "[" + ChatColor.DARK_GRAY + "DOWN" + ChatColor.GOLD + "]")) {


                        int i = standOn.getLocation().getBlockY();

                        while (i > 0) {
                            i--;
                            Block sponge = event.getPlayer().getWorld().getBlockAt(standOn.getLocation().getBlockX(),  i, standOn.getLocation().getBlockZ());

                            if (sponge.getType().equals(Material.SPONGE)) {
                                i=0;
                                Location tp = sponge.getLocation();
                                tp.setY(tp.getBlockY() + 1.5);
                                tp.setZ(tp.getBlockZ() + 0.5);
                                tp.setX(tp.getBlockX() + 0.5);
                                tp.setPitch(player.getLocation().getPitch());
                                tp.setYaw(player.getLocation().getYaw());

                                player.teleport(tp);

                            }
                        }
                        player.sendMessage(ChatColor.AQUA +"Going down");
                    }
                }
            }
        }
    }
}
