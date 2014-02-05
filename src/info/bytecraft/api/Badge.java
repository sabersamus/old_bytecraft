package info.bytecraft.api;

public class Badge
{
    
    public enum BadgeType
    {
        
        VETERAN("Veteran");
        
        private final int maxLevel;
        private final String name;
        
        private BadgeType(String name)
        {
            this(name, 1);
        }
        
        private BadgeType(String name, int maxLevel)
        {
            this.maxLevel = maxLevel;
            this.name = name;
        }

        public int getMaxLevel()
        {
            return maxLevel;
        }

        public String getName()
        {
            return name;
        }
    }
    
    private BadgeType type;
    private int level;
    
    public Badge(BadgeType type, int level)
    {
        if(level > type.getMaxLevel()){
            level = type.getMaxLevel();
        }
        
        this.setLevel(level);
        this.setType(type);
    }

    public BadgeType getType()
    {
        return type;
    }

    public void setType(BadgeType type)
    {
        this.type = type;
    }

    public int getLevel()
    {
        return level;
    }

    public void setLevel(int level)
    {
        if(level > type.getMaxLevel()){
            level = type.getMaxLevel();
        }
        this.level = level;
    }
    
}
