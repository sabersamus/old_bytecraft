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

package info.bytecraft.api;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.material.MaterialData;
import org.bukkit.enchantments.Enchantment;

public class SaleSign
{
    private int id = 0;
    private String playerName;
    private MaterialData material = null;
    private Map<Enchantment, Integer> enchantments = null;
    private int cost = 0;
    private int availableInventory = 0;
    private Location blockLoc = null;
    private Location signLoc = null;
    private boolean storedEnchants = false;

    public SaleSign()
    {
    }

    public int getId()
    {
        return id;
    }

    public void setId(int v)
    {
        this.id = v;
    }

    public String getPlayerName()
    {
        return playerName;
    }

    public void setPlayerName(String name)
    {
        this.playerName = name;
    }

    public MaterialData getMaterial()
    {
        return material;
    }

    public void setMaterial(MaterialData v)
    {
        this.material = v;
    }

    public Map<Enchantment, Integer> getEnchantments()
    {
        return enchantments;
    }

    public void setEnchantments(Map<Enchantment, Integer> v)
    {
        this.enchantments = v;
    }

    public int getCost()
    {
        return cost;
    }

    public void setCost(int v)
    {
        this.cost = v;
    }

    public int getAvailableInventory()
    {
        return availableInventory;
    }

    public void setAvailableInventory(int v)
    {
        this.availableInventory = v;
    }

    public void addAvailableInventory(int v)
    {
        this.availableInventory += v;
    }

    public void removeAvailableInventory(int v)
    {
        this.availableInventory -= v;
    }

    public Location getBlockLocation()
    {
        return blockLoc;
    }

    public void setBlockLocation(Location v)
    {
        this.blockLoc = v;
    }

    public Location getSignLocation()
    {
        return signLoc;
    }

    public void setSignLocation(Location v)
    {
        this.signLoc = v;
    }

    public void setStoredEnchantments(boolean v)
    {
        this.storedEnchants = v;
    }

    public boolean hasStoredEnchantments()
    {
        return storedEnchants;
    }
}
