package info.bytecraft.api;

public class MathUtil
{
    
    public static long percentage(long start, int percent)
    {
        if(percent == 100){
            return start;
        }
        if(percent == 0){
            return 0;
        }
        
        double calc = percent / 100.0;
        
        return (long) Math.abs((long) start * calc);
    }
    
}
