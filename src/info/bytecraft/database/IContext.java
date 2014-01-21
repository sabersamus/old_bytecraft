package info.bytecraft.database;

public interface IContext extends AutoCloseable
{
    public void close();
    
    public IBlessDAO getBlessDAO();
    public IHomeDAO getHomeDAO();
    public ILogDAO getLogDAO();
    public IPlayerDAO getPlayerDAO();
    public IReportDAO getReportDAO();
    public IWarpDAO getWarpDAO();
    public IZoneDAO getZoneDAO();
}
