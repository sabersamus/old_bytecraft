package info.bytecraft.database;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.SaleSign;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Server;

public interface ISaleSignDAO
{
    public enum TransactionType {
        DEPOSIT, WITHDRAW, BUY;
    }

    public void insert(SaleSign SaleSign) throws DAOException;

    public void update(SaleSign SaleSign) throws DAOException;

    public void delete(SaleSign SaleSign) throws DAOException;

    public void insertTransaction(SaleSign SaleSign, BytecraftPlayer player,
            TransactionType type, int amount) throws DAOException;

    public void insertCostChange(SaleSign SaleSign, int oldCost)
            throws DAOException;

    public Map<Location, SaleSign> loadSaleSigns() throws DAOException;
}
