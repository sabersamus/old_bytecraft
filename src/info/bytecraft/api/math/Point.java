package info.bytecraft.api.math;

public class Point
{
    
    private int x;
    private int z;
    
    public Point(int x, int z)
    {
        this.setX(x);
        this.setZ(z);
    }
    
    public Point add(Point p)
    {
        return add(p.x, p.z);
    }
    
    public Point add(int x, int z)
    {
        return new Point(this.x + x, this.z + z);
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
    
    @Override
    public boolean equals(Object o)
    {
        if(o instanceof Point){
            Point p = (Point)o;
            return (p.x == x && p.z == z);
        }
        return false;
    }
    
}
