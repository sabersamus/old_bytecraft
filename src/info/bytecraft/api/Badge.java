package info.bytecraft.api;

public enum Badge {

    VETERAN("Veteran");

    private final int maxLevel;
    private final String name;

    private Badge(String name)
    {
        this(name, 1);
    }

    private Badge(String name, int maxLevel)
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
    
    public static Badge fromString(String name)
    {
        for(Badge badge: values()){
            if(badge.name.equalsIgnoreCase(name)){
                return badge;
            }
        }
        return null;
    }
}
