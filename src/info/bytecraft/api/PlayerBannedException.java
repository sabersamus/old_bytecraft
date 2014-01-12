package info.bytecraft.api;

public class PlayerBannedException extends Exception
{
    private static final long serialVersionUID = 4405142029142530929L;

    public PlayerBannedException(String message)
    {
        super(message);
    }
}
