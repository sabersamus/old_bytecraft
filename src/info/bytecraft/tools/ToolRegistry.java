package info.bytecraft.tools;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;

import com.google.common.collect.Lists;

import static org.bukkit.ChatColor.*;

public class ToolRegistry
{
    
    public static final String DURABILITY_TAG = "1000/1000";
    public final static Set<Material> veinMinerAllowedTools = EnumSet.of(
            Material.WOOD_PICKAXE, Material.STONE_PICKAXE,
            Material.IRON_PICKAXE, Material.GOLD_PICKAXE,
            Material.DIAMOND_PICKAXE);
    
    public static final String VEIN_TAG = ChatColor.GREEN + "[TOOL] Vein Miner";
    
    public static ItemStack getVeinMinerCoupon()
    {
        ItemStack coupon = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = coupon.getItemMeta();
        
        meta.setDisplayName(GREEN + "Coupon: Vein Miner");
        
        List<String> lore = Lists.newArrayList();
        lore.add(AQUA + "Turn a pickaxe into a vein miner");
        meta.setLore(lore);
        coupon.setItemMeta(meta);
        return coupon;
    }
    
    public static void registerRecipes(Server server) {
        List<Material> items = new ArrayList<Material>();
        
        items.add(Material.DIAMOND_PICKAXE);
        
        items.add(Material.GOLD_PICKAXE);
        
        items.add(Material.IRON_PICKAXE);
        
        items.add(Material.STONE_PICKAXE);
        
        items.add(Material.WOOD_PICKAXE);
        
        for (Material i : items) {
            ShapelessRecipe recipe = new ShapelessRecipe(new ItemStack(Material.PAPER));
            recipe.addIngredient(Material.PAPER);
            recipe.addIngredient(i);
            server.addRecipe(recipe);
        }
        
    }

}