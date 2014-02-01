package info.bytecraft.api;

import org.bukkit.ChatColor;

import static org.bukkit.ChatColor.*;

public enum Rank
{
    NEWCOMER(WHITE),
    SETTLER(GREEN),
    MEMBER(DARK_GREEN),
    MENTOR(AQUA),
    PROTECTOR(BLUE),
    ARCHITECT(YELLOW),
    CODER(AQUA),
    ADMIN(RED),
    PRINCESS(LIGHT_PURPLE),
    ELDER(DARK_RED);
    
    private final ChatColor color;
    
    private Rank(ChatColor color)
    {
        this.color = color;
    }

    public ChatColor getColor()
    {
        return color;
    }
    
    public static Rank getRank(String name)
    {
        for(Rank rank: values()){
            if(rank.name().equalsIgnoreCase(name)){
                return rank;
            }
        }
        return Rank.NEWCOMER;
    }
    
    public boolean canBuild()
    {
        return (this != NEWCOMER);
    }
    
    public boolean canFill()
    {
        return (this == ARCHITECT
                || this == ADMIN || this == PRINCESS || this == ELDER);
    }
    
    
    @Override
    public String toString()
    {
        return name().toLowerCase().replace("_", " ");
    }
}
