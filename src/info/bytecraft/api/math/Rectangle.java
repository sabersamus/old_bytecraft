package info.bytecraft.api.math;

public class Rectangle
{
    
    private Point p1;
    private Point p2;
    
    public Rectangle(Point p1, Point p2)
    {
        this.setPoint1(p1);
        this.setPoint2(p2);
    }
    
    public Rectangle(int x1, int z1, int x2, int z2)
    {
        this(new Point(x1, z1), new Point(x2, z2));
    }

    public Point getPoint1()
    {
        return p1;
    }

    public void setPoint1(Point p1)
    {
        this.p1 = p1;
    }

    public Point getPoint2()
    {
        return p2;
    }

    public void setPoint2(Point p2)
    {
        this.p2 = p2;
    }
    
    public boolean contains(Point p)
    {
        int xMin, zMin, xMax, zMax;
        xMin = Math.min(p1.getX(), p2.getX());
        zMin = Math.min(p1.getZ(), p2.getZ());
        xMax = Math.max(p1.getX(), p2.getX());
        zMax = Math.max(p1.getZ(), p2.getZ());
        return (xMin <= p.getX() && zMin <= p.getZ() && xMax >= p.getX() && zMax >= p.getZ());
    }
    
}
