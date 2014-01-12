package info.bytecraft.api.vector;

import org.bukkit.World;

public class Vector2D
{

    private int x;
    private int z;
    private World world;

    public Vector2D(int x, int z, World world)
    {
        setX(x);
        setZ(z);
        setWorld(world);
    }

    public boolean isInAABB(Vector2D min, Vector2D max)
    {
        return ((min.x <= x && min.z <= z) && (max.x >= x && max.z >= z) && (min.world == max.world && max.world == world));
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getZ()
    {
        return z;
    }

    public void setZ(int z)
    {
        this.z = z;
    }

    public World getWorld()
    {
        return world;
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

}
