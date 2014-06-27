package info.bytecraft.api.util;

public class StringUtil
{
    
    private StringUtil(){}
    
    public static String join(String[] args, char split, int start, int stop)
    {
        StringBuilder buffer = new StringBuilder();
        String delim = "";
        for(int i = start; i < stop; i++){
            buffer.append(delim);
            buffer.append(args[i]);
            delim = String.valueOf(split);
        }
        
        return buffer.toString().trim();
    }

}
