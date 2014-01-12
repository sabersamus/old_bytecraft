package info.bytecraft.api.economy;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.vector.Vector2D;
import info.bytecraft.database.ConnectionPool;
import info.bytecraft.database.DBBankDAO;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.World;

public class Bank
{
    private final String name;
    private int id;
    private int x1;
    private int z1;
    private int x2;
    private int z2;
    private World world;
    
    public Bank(String name)
    {
        this.name = name;
    }
    
    public String getName()
    {
        return name;
    }

    public int getId()
    {
        return id;
    }

    public void setId(int id)
    {
        this.id = id;
    }

    public HashMap<String, Long> getAccounts()
    {
        Connection conn = null;
        DBBankDAO dbBank = null;
        try{
            conn = ConnectionPool.getConnection();
            dbBank = new DBBankDAO(conn);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(conn != null){
                try{
                    conn.close();
                }catch(SQLException e){}
            }
        }
        return dbBank.getAccounts(this);
    }
    
    public void createAccount(Bank bank, BytecraftPlayer player, long toAdd)
    {
        Connection conn = null;
        try{
            conn = ConnectionPool.getConnection();
            DBBankDAO dbBank = new DBBankDAO(conn);
            dbBank.createAccount(this, player, toAdd);
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(conn != null){
                try{
                    conn.close();
                }catch(SQLException e){}
            }
        }
    }

    public int getX1()
    {
        return x1;
    }

    public void setX1(int x1)
    {
        this.x1 = x1;
    }

    public int getZ1()
    {
        return z1;
    }

    public void setZ1(int z1)
    {
        this.z1 = z1;
    }

    public int getX2()
    {
        return x2;
    }

    public void setX2(int x2)
    {
        this.x2 = x2;
    }

    public int getZ2()
    {
        return z2;
    }

    public void setZ2(int z2)
    {
        this.z2 = z2;
    }
    
    public World getWorld()
    {
        return world;
    }

    public void setWorld(World world)
    {
        this.world = world;
    }

    public Vector2D getVector1()
    {
        return new Vector2D(x1, z1, world);
    }
    
    public Vector2D getVector2()
    {
        return new Vector2D(x2, z1, world);
    }
}
