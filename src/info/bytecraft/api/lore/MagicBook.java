package info.bytecraft.api.lore;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import com.google.common.collect.Lists;

/**
 * This may not work right
 * @author Robert
 *
 */
public class MagicBook
{
    
    private String name;
    private int value;
    
    private final BookMeta book;

    public MagicBook(BookMeta meta, String name, int value)
    {
        book = meta;
    }
    
    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public int getValue()
    {
        if(this.value > 100000){
            return 100000;//capped at 500k
        }
        return value;
    }

    public void setValue(int value)
    {
        if(value > 100000){
            this.value = 100000;
            return;
        }
        this.value = value;
    }

    public BookMeta getBook()
    {
        return book;
    }
    
    public void update(ItemStack stack)
    {
        if(stack.getType() != Material.WRITTEN_BOOK)return;
        BookMeta meta = getBook();
        meta.setAuthor("SERVER");
        meta.setPage(0, String.format("This coupon is good for %d bytes", value));
        List<String> newLore = Lists.newArrayList();
        
        newLore.add(ChatColor.AQUA + "A magic book?!");
        newLore.add(ChatColor.RED + "It must be rare!");
        
        stack.setItemMeta(meta);
    }
    
}
