package info.bytecraft.listener;

import info.bytecraft.tools.ToolRegistry;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ToolListener implements Listener
{

    @EventHandler
    public void CraftTool(PrepareItemCraftEvent event)
    {
        if (event.isRepair()) return;
        
        List<ItemStack> items = new ArrayList<ItemStack>();
        boolean success = false;
        for(ItemStack a : event.getInventory().getContents()) {
            if(a.getType().equals(Material.AIR)) continue;
            items.add(a);
        } // Get an easier to handle list.

        if (items.size() != 3) return;
        
        if(items.contains(ToolRegistry.getVeinMinerCoupon())){
            for (ItemStack i : items){
                if(ToolRegistry.veinMinerAllowedTools.contains(i.getType())) {
                    ItemMeta iMeta = i.getItemMeta();
                    
                    List<String> lore = new ArrayList<String>();
                    lore.add(ToolRegistry.VEIN_TAG);
                    iMeta.setLore(lore);
                    
                    i.setItemMeta(iMeta);
                    event.getInventory().setResult(i);
                    success = true;
                }
            }
        }else{
            return;
        }
        

        if (success == false) {
            event.getInventory().setResult(null);
        }
    }
}
