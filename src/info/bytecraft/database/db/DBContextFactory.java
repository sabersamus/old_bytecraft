package info.bytecraft.database.db;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.dbcp.BasicDataSource;
import org.bukkit.configuration.file.FileConfiguration;

import info.bytecraft.Bytecraft;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.IContext;
import info.bytecraft.database.IContextFactory;

public class DBContextFactory implements IContextFactory
{
    private BasicDataSource ds;
    private Bytecraft plugin;

    public DBContextFactory(FileConfiguration config, Bytecraft plugin)
    {
        this.plugin = plugin;
        String driver = config.getString("database.driver");
        if (driver == null) {
            driver = "com.mysql.jdbc.Driver";
        }

        try {
            Class.forName(driver).newInstance();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        }

        String user = config.getString("database.user");
        String password = config.getString("database.password");
        String url = config.getString("database.url");

        ds = new BasicDataSource();
        ds.setDriverClassName(driver);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setUrl(url);
        ds.setMaxActive(5);
        ds.setMaxIdle(5);
        ds.setDefaultAutoCommit(true);
    }

    @Override
    public IContext createContext() 
    throws DAOException
    {
        try{
            Connection conn = ds.getConnection();
            try(Statement stm = conn.createStatement()){
                stm.execute("SET NAMES latin1");
            }
            
            return new DBContext(conn, plugin);
        }catch(SQLException e){
            throw new DAOException(e);
        }
    }

}
