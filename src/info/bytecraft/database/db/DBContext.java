package info.bytecraft.database.db;

import java.sql.Connection;
import java.sql.SQLException;

import info.bytecraft.Bytecraft;
import info.bytecraft.database.*;

public class DBContext implements IContext
{
    private Bytecraft plugin;
    private Connection conn;
    
    public DBContext(Connection conn, Bytecraft plugin)
    {
        this.conn = conn;
        this.plugin = plugin;
    }

    @Override
    public void close()
    {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {}
        }
    }

    @Override
    public IBlessDAO getBlessDAO()
    {
        return new DBBlessDAO(conn);
    }

    @Override
    public IHomeDAO getHomeDAO()
    {
        return new DBHomeDAO(conn);
    }

    @Override
    public ILogDAO getLogDAO()
    {
        return new DBLogDAO(conn);
    }
    
    @Override
    public ILoreDAO getLoreDAO()
    {
        return new DBLoreDAO(conn);
    }
    
    @Override
    public IMessageDAO getMessageDAO()
    {
        return new DBMessageDAO(conn);
    }

    @Override
    public IPlayerDAO getPlayerDAO()
    {
        return new DBPlayerDAO(conn, plugin);
    }
    
    @Override
    public IReportDAO getReportDAO()
    {
        return new DBReportDAO(conn);
    }
    
    @Override
    public IWarpDAO getWarpDAO()
    {
        return new DBWarpDAO(conn);
    }

    @Override
    public IZoneDAO getZoneDAO()
    {
        return new DBZoneDAO(conn);
    }

    @Override
    public IInventoryDAO getInventoryDAO()
    {
        return new DBInventoryDAO(conn);
    }
    
    @Override
    public IItemDAO getItemDAO()
    {
        return new DBItemDAO(conn);
    }

    @Override
    public ISaleSignDAO getSaleSignDAO()
    {
        return new DBSaleSignDAO(conn);
    }
    

}
