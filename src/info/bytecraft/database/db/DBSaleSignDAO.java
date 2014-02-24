/*
 * Copyright (c) 2010-2014, Ein Andersson, Emil Hernvall, Josh Morgan, James Sherlock, Rob Catron, Joe Notaro
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. All advertising materials mentioning features or use of this software
 *    must display the following acknowledgement:
 *    This product includes software developed by the <organization>.
 * 4. Neither the name of the <organization> nor the
 *    names of its contributors may be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY <COPYRIGHT HOLDER> ''AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL <COPYRIGHT HOLDER> BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package info.bytecraft.database.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.material.MaterialData;

import info.bytecraft.api.BytecraftPlayer;
import info.bytecraft.api.SaleSign;
import info.bytecraft.database.DAOException;
import info.bytecraft.database.ISaleSignDAO;

public class DBSaleSignDAO implements ISaleSignDAO
{
    private Connection conn;
    
    public DBSaleSignDAO(Connection conn)
    {
        this.conn = conn;
    }
    
    private String serializeEnchants(Map<Enchantment, Integer> enchants)
    {
        StringBuilder buffer = new StringBuilder();
        String delim = "";
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            buffer.append(delim);
            buffer.append(entry.getKey().getName());
            buffer.append("=");
            buffer.append(entry.getValue().toString());
            delim = "&";
        }
        
        return buffer.toString();
    }
    
    private Map<Enchantment, Integer> deserializeEnchants(String data)
    {
        String[] entries = data.split("&");
        Map<Enchantment, Integer> result = new HashMap<>();
        for (String entry : entries) {
            String[] kv = entry.split("=");
            if (kv.length != 2) {
                continue;
            }

            try {
                Enchantment ench = Enchantment.getByName(kv[0]);
                if (ench == null) {
                    continue;
                }
                Integer lvl = Integer.parseInt(kv[1]);
                result.put(ench, lvl);
            } catch (NumberFormatException e) { }
        }

        return result;
    }

    @Override
    public void insert(SaleSign block) throws DAOException
    {
        String sql = "INSERT INTO salesign (player_name, salesign_created, " +
                "salesign_material, salesign_data, salesign_enchantments, " +
                "salesign_cost, salesign_inventory, salesign_world, " +
                "salesign_blockx, salesign_blocky, salesign_blockz, " +
                "salesign_signx, salesign_signy, salesign_signz, " +
                "salesign_storedenchants) ";
            sql += "VALUES (?, unix_timestamp(), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

            try (PreparedStatement stm = conn.prepareStatement(sql)) {
                stm.setString(1, block.getPlayerName());
                stm.setInt(2, block.getMaterial().getItemTypeId());
                stm.setInt(3, block.getMaterial().getData());
                stm.setString(4, serializeEnchants(block.getEnchantments()));
                stm.setInt(5, block.getCost());
                stm.setInt(6, block.getAvailableInventory());
                stm.setString(7, block.getBlockLocation().getWorld().getName());
                stm.setInt(8, block.getBlockLocation().getBlockX());
                stm.setInt(9, block.getBlockLocation().getBlockY());
                stm.setInt(10, block.getBlockLocation().getBlockZ());
                stm.setInt(11, block.getSignLocation().getBlockX());
                stm.setInt(12, block.getSignLocation().getBlockY());
                stm.setInt(13, block.getSignLocation().getBlockZ());
                stm.setString(14, block.hasStoredEnchantments() ? "1" : "0");
                stm.execute();

                stm.executeQuery("SELECT LAST_INSERT_ID()");

                try (ResultSet rs = stm.getResultSet()) {
                    if (!rs.next()) {
                        throw new DAOException("Failed to get insert_id!", sql);
                    }

                    block.setId(rs.getInt(1));
                }
            } catch (SQLException e) {
                throw new DAOException(sql, e);
            }
    }

    @Override
    public void update(SaleSign block) throws DAOException
    {
        String sql = "UPDATE salesign SET player_name = ?, " +
                "salesign_material = ?, salesign_data = ?, " +
                "salesign_enchantments = ?, salesign_cost = ?, " +
                "salesign_inventory = ?, salesign_world = ?, " +
                "salesign_blockx = ?, salesign_blocky = ?, " +
                "salesign_blockz = ?, salesign_signx = ?, " +
                "salesign_signy = ?, salesign_signz = ?, " +
                "salesign_storedenchants = ? ";
            sql += "WHERE salesign_id = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, block.getPlayerName());
                stmt.setInt(2, block.getMaterial().getItemTypeId());
                stmt.setInt(3, block.getMaterial().getData());
                stmt.setString(4, serializeEnchants(block.getEnchantments()));
                stmt.setInt(5, block.getCost());
                stmt.setInt(6, block.getAvailableInventory());
                stmt.setString(7, block.getBlockLocation().getWorld().getName());
                stmt.setInt(8, block.getBlockLocation().getBlockX());
                stmt.setInt(9, block.getBlockLocation().getBlockY());
                stmt.setInt(10, block.getBlockLocation().getBlockZ());
                stmt.setInt(11, block.getSignLocation().getBlockX());
                stmt.setInt(12, block.getSignLocation().getBlockY());
                stmt.setInt(13, block.getSignLocation().getBlockZ());
                stmt.setString(14, block.hasStoredEnchantments() ? "1" : "0");
                stmt.setInt(15, block.getId());
                stmt.execute();
            } catch (SQLException e) {
                throw new DAOException(sql, e);
            }
    }

    @Override
    public void delete(SaleSign block) throws DAOException
    {
        String sql = "UPDATE salesign SET salesign_status = 'deleted' ";
        sql += "WHERE salesign_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, block.getId());
            stmt.execute();
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }
    }

    @Override
    public void insertTransaction(SaleSign block, BytecraftPlayer player,
            TransactionType type, int amount) throws DAOException
    {
        String sql = "INSERT INTO salesign_transaction (salesign_id, " +
                "player_name, transaction_type, transaction_timestamp, " +
                "transaction_amount, transaction_unitcost, transaction_totalcost) ";
            sql += "VALUES (?, ?, ?, unix_timestamp(), ?, ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, block.getId());
                stmt.setString(2, player.getName());
                stmt.setString(3, type.toString());
                stmt.setInt(4, amount);
                stmt.setInt(5, block.getCost());
                stmt.setInt(6, amount * block.getCost());
                stmt.execute();
            } catch (SQLException e) {
                throw new DAOException(sql, e);
            }
    }

    @Override
    public void insertCostChange(SaleSign block, int oldCost)
            throws DAOException
    {
        String sql = "INSERT INTO salesign_costlog (salesign_id, " +
                "costlog_timestamp, costlog_newcost, costlog_oldcost) ";
            sql += "VALUES (?, unix_timestamp(), ?, ?)";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, block.getId());
                stmt.setInt(2, block.getCost());
                stmt.setInt(3, oldCost);
                stmt.execute();
            } catch (SQLException e) {
                throw new DAOException(sql, e);
            }
    }

    @Override
    public Map<Location, SaleSign> loadSaleSigns()
            throws DAOException
    {
        String sql = "SELECT * FROM salesign WHERE salesign_status = 'active'";

        Map<Location, SaleSign> salesigns = new HashMap<>();

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.execute();

            try (ResultSet rs = stmt.getResultSet()) {
                while (rs.next()) {
                    int id = rs.getInt("salesign_id");
                    String playerName = rs.getString("player_name");

                    int material = rs.getInt("salesign_material");
                    byte data = (byte)rs.getInt("salesign_data");
                    Map<Enchantment, Integer> enchants =
                        deserializeEnchants(rs.getString("salesign_enchantments"));
                    MaterialData materialData = new MaterialData(material, data);

                    int cost = rs.getInt("salesign_cost");
                    int inventory = rs.getInt("salesign_inventory");

                    String worldName = rs.getString("salesign_world");
                    int blockX = rs.getInt("salesign_blockx");
                    int blockY = rs.getInt("salesign_blocky");
                    int blockZ = rs.getInt("salesign_blockz");
                    int signX = rs.getInt("salesign_signx");
                    int signY = rs.getInt("salesign_signy");
                    int signZ = rs.getInt("salesign_signz");
                    boolean storedEnchants =
                        "1".equals(rs.getString("salesign_storedenchants"));

                    World world = Bukkit.getWorld(worldName);
                    Location blockLoc =
                        new Location(world, blockX, blockY, blockZ);
                    Location signLoc =
                        new Location(world, signX, signY, signZ);

                    SaleSign block = new SaleSign();
                    block.setId(id);
                    block.setPlayerName(playerName);
                    block.setMaterial(materialData);
                    block.setEnchantments(enchants);
                    block.setCost(cost);
                    block.setAvailableInventory(inventory);
                    block.setBlockLocation(blockLoc);
                    block.setSignLocation(signLoc);
                    block.setStoredEnchantments(storedEnchants);

                    salesigns.put(blockLoc, block);
                    salesigns.put(signLoc, block);
                }
            }
        } catch (SQLException e) {
            throw new DAOException(sql, e);
        }

        return salesigns;
    }

}
