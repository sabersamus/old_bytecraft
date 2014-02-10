package info.bytecraft.api.lore;

import static org.bukkit.ChatColor.*;

import java.util.HashMap;
import java.util.Random;

import com.google.common.collect.Maps;

public enum God
{
    SABERSAMUS(DARK_RED + "Sabersamus"),
    ICEFIREMAN99(DARK_RED + "icefireman99"),
    RAGENZEN(DARK_RED + "Ragenzen"),
    ANON16(DARK_RED + "anon16"),
    HEYALYSSARAE(LIGHT_PURPLE + "heyalyssarae"),
    KOALA37(DARK_PURPLE + "koala37"),
    MGUERRA11(RED + "mguerra11");
    
    private String name;
    private static HashMap<Integer, God> gods = Maps.newHashMap();
    
    static{
        for(God god: values()){
            gods.put(god.ordinal(), god);
        }
    }
    
    private God(String name)
    {
        this.setName(name);
    }

    public static God getRandom()
    {
        return gods.get(new Random().nextInt(God.values().length - 1));
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }
    
    public String toString()
    {
        return name;
    }
}
