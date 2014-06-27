package info.bytecraft.database;

public interface IContext extends AutoCloseable
{
    public void close();
    
    public IBlessDAO getBlessDAO();
    public IHomeDAO getHomeDAO();    
    public IInventoryDAO getInventoryDAO();
    public ILogDAO getLogDAO();
    public ILoreDAO getLoreDAO();
    public IItemDAO getItemDAO();
    public IMessageDAO getMessageDAO();
    public IPlayerDAO getPlayerDAO();
    public IReportDAO getReportDAO();
    public ISaleSignDAO getSaleSignDAO();
    public IWarpDAO getWarpDAO();
    public IZoneDAO getZoneDAO();
    
}
