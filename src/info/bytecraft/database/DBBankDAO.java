package info.bytecraft.database;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.economy.Bank;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class DBBankDAO
{
    
    private Connection conn;
    
    public DBBankDAO(Connection conn)
    {
        this.conn = conn;
    }
    
    public List<Bank> getBanks()
    {
        List<Bank> banks = Lists.newArrayList();
        
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM bank");
            stm.execute();
            ResultSet rs = stm.getResultSet();
            while(rs.next()){
                Bank bank = new Bank(rs.getString("bank_name"));
                bank.setId(rs.getInt("bank_id")); 
                bank.setX1(rs.getInt("bank_x1"));
                bank.setZ1(rs.getInt("bank_z1"));
                bank.setX2(rs.getInt("bank_x2"));
                bank.setZ2(rs.getInt("bank_z2"));
                bank.setWorld(Bukkit.getWorld(rs.getString("bank_world")));
                banks.add(bank);
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
        
        return banks;
    }
    
    public Bank getBank(String name)
    {
        Bank bank = new Bank(name);
        
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM banks WHERE bank_name = ?");
            stm.setString(1, bank.getName());
            stm.execute();
            ResultSet rs = stm.getResultSet();
            if(rs.next()){
               bank.setId(rs.getInt("bank_id")); 
               bank.setX1(rs.getInt("bank_x1"));
               bank.setZ1(rs.getInt("bank_z1"));
               bank.setX2(rs.getInt("bank_x2"));
               bank.setZ2(rs.getInt("bank_z2"));
               bank.setWorld(Bukkit.getWorld(rs.getString("bank_world")));
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
        return bank;
    }
    
    public HashMap<String, Long> getAccounts(Bank b)
    {
        HashMap<String, Long> map = Maps.newHashMap();
        PreparedStatement stm = null;
        
        try{
            stm = conn.prepareStatement("SELECT * FROM bank_account WHERE bank_id= ?");
            stm.setInt(1, b.getId());
            stm.execute();
            ResultSet rs = stm.getResultSet();
            while(rs.next()){
                map.put(rs.getString("player_name"), rs.getLong("account_balance"));
            }
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
        return map;
    }
    
    public void createAccount(Bank bank, BytecraftPlayer player, long toAdd)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("INSERT INTO bank_account (bank_id, player_name, account_balance) VALUES (?,?,?)");
            stm.setInt(1, bank.getId());
            stm.setString(2, player.getName());
            stm.setLong(3, toAdd);
            stm.execute();
        }catch(SQLException e){
            throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
    }
    
    public long getBalance(Bank bank, BytecraftPlayer player)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("SELECT * FROM bank_account WHERE bank_id = ? AND player_name = ?");
            stm.setInt(1, bank.getId());
            stm.setString(2, player.getName());
            stm.execute();
            
            ResultSet rs = stm.getResultSet();
            if(!rs.next())return 0L;
            
            return rs.getLong("account_balance");
        }catch(SQLException e){
            throw new RuntimeException(e);
         }finally{
             if(stm != null){
                 try {
                     stm.close();
                 } catch (SQLException e) {}
             }
         }
    }
    
    public void deposit(Bank bank, BytecraftPlayer player, long deposit)
    {
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("UPDATE bank_account SET account_balance = account_balance " +
            		"+ ? WHERE bank_id = ? AND player_name = ?");
            stm.setLong(1, deposit);
            stm.setInt(2, bank.getId());
            stm.setString(3, player.getName());
            stm.execute();
        }catch(SQLException e){
           throw new RuntimeException(e);
        }finally{
            if(stm != null){
                try {
                    stm.close();
                } catch (SQLException e) {}
            }
        }
    }
    
    public boolean withdraw(Bank bank, BytecraftPlayer player, long withdraw)
    {
        if(getBalance(bank, player) - withdraw < 0)return false;
        PreparedStatement stm = null;
        try{
            stm = conn.prepareStatement("UPDATE bank_account SET account_balance = account_balance - ? WHERE bank_id = ? AND player_name = ?");
            stm.setLong(1, withdraw);
            stm.setInt(2, bank.getId());
            stm.setString(3, player.getName());
            stm.execute();  
        }catch(SQLException e){
            throw new RuntimeException(e);
         }finally{
             if(stm != null){
                 try {
                     stm.close();
                 } catch (SQLException e) {}
             }
         }
        return true;
    }
}
