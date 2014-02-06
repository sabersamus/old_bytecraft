package info.bytecraft.commands;

import static org.bukkit.ChatColor.DARK_AQUA;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

@SuppressWarnings("deprecation")
public class ItemCommand extends AbstractCommand
{

    private static Set<Material> disallowedItems = new HashSet<Material>();
    private Set<Material> noLore;

    public ItemCommand(Bytecraft instance)
    {
        super(instance, "item");
        disallowedItems.add(Material.BEDROCK);
        noLore = EnumSet.of(Material.BOOK, Material.BOOK_AND_QUILL);//may add more later
    }

    public boolean handlePlayer(BytecraftPlayer player, String[] args)
    {
        if(args.length == 0)return false;
        if(!player.getRank().canFill()){
            player.sendMessage(getInvalidPermsMessage());
            return true;
        }

        String param = args[0].toUpperCase();

        int materialId;
        try {
            materialId = Integer.parseInt(param);
        } catch (NumberFormatException e) {
            try {
                Material material = Material.getMaterial(param);
                materialId = material.getId();
            } catch (NullPointerException ne) {
                player.sendMessage(DARK_AQUA
                        + "/item <id|name> <amount> <data>.");
                return true;
            }
        }

        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            amount = 1;
        } catch (NumberFormatException e) {
            amount = 1;
        }

        int data;
        try {
            data = Integer.parseInt(args[2]);
        } catch (ArrayIndexOutOfBoundsException e) {
            data = 0;
        } catch (NumberFormatException e) {
            data = 0;
        }

        ItemStack item = new ItemStack(materialId, amount, (byte) data);
        if(!noLore.contains(item.getType())){
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.YELLOW + "SPAWNED");
            lore.add(ChatColor.YELLOW + "By " + player.getDisplayName());
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        if (item.getType() == Material.MONSTER_EGG || item.getType() == Material.NAME_TAG) {
            return true;
        }

        PlayerInventory inv = player.getInventory();
        inv.addItem(item);

        Material material = Material.getMaterial(materialId);
        String materialName = material.toString();
        player.sendMessage("You received " + amount + " of " + DARK_AQUA
                + materialName.toLowerCase() + ".");
        return true;
    }

}
