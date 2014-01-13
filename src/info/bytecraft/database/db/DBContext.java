package info.bytecraft.database.db;

import java.sql.Connection;
import java.sql.SQLException;

import info.bytecraft.database.IBlessDAO;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IHomeDAO;
import info.bytecraft.database.ILogDAO;
import info.bytecraft.database.IPlayerDAO;
import info.bytecraft.database.IWarpDAO;
import info.bytecraft.database.IZoneDAO;

public class DBContext implements IContext
{
    private Connection conn;
    
    public DBContext(Connection conn)
    {
        this.conn = conn;
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
    public IPlayerDAO getPlayerDAO()
    {
        return new DBPlayerDAO(conn);
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

}
