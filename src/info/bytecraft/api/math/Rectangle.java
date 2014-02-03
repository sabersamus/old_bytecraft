package info.bytecraft.api.math;

public class Rectangle
{
    int x1, z1;
    int x2, z2;
    
    long width, height;
    int centerX, centerY;

    public Rectangle(int x1, int y1, int x2, int y2)
    {
        int tmp;
        
        // swap values so that we can guarantee the
        // relative position of the points
        if (x1 > x2) {
            tmp = x2;
            x2 = x1;
            x1 = tmp;
        }
        
        if (y1 < y2) {
            tmp = y2;
            y2 = y1;
            y1 = tmp;
        }
    
        this.x1 = x1;
        this.z1 = y1;
        this.x2 = x2;
        this.z2 = y2;
        
        this.width = (long)x2 - (long)x1;
        this.height = (long)y1 - (long)y2;
        
        this.centerX = (int)(x1 + width/2);
        this.centerY = (int)(y1 - height/2);
    }
    
    public int getLeft() { return x1; }
    public int getRight() { return x2; }
    public int getTop() { return z1; }
    public int getBottom() { return z2; }
    
    public long getWidth() { return width; }
    public long getHeight() { return height; }
    
    public int getCenterX() { return centerX; }
    public int getCenterY() { return centerY; }
    
    public boolean contains(Point p)
    {
        return (p.x >= x1 && p.x <= x2) &&
            (p.z <= z1 && p.z >= z2);
    }
    
    public boolean intersects(Rectangle rect)
    {
        return !(rect.getLeft() > getRight() ||
            rect.getRight() < getLeft() ||
            rect.getTop() < getBottom() ||
            rect.getBottom() > getTop());
    }
    
    public Point[] getPoints()
    {
        Point tl = new Point(x1, z1);
        Point tr = new Point(x2, z1);
        Point bl = new Point(x1, z2);
        Point br = new Point(x2, z2);
        return new Point[] { tl, tr, bl, br };
    }
    
    @Override
    public String toString()
    {
        return String.format("(%d, %d),(%d, %d)", x1, z1, x2, z2);
    }
    
    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Rectangle)) {
            return false;
        }
        
        Rectangle b = (Rectangle)obj;
        return b.x1 == x1 && b.z1 == z1 && b.x2 == x2 && b.z2 == z2;
    }
    
    @Override
    public int hashCode()
    {
        int code = 17;
        code = 31 * code + x1;
        code = 31 * code + z1;
        code = 31 * code + x2;
        code = 31 * code + z2;
        
        return code;
    }
}
