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

package info.bytecraft.listener;

import info.bytecraft.Bytecraft;
import info.bytecraft.api.BytecraftPlayer;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.event.*;
import org.bukkit.event.player.*;

public class WorldPortalListener implements Listener{
    
    private Bytecraft plugin;
    
    public WorldPortalListener(Bytecraft instance)
    {
        this.plugin = instance;
    }
    
    @EventHandler
    public void portalHandler(PlayerMoveEvent event)
    {
        final BytecraftPlayer player = plugin.getPlayer(event.getPlayer());
        Block under = player.getLocation().subtract(0, 1, 0).getBlock();
        Block in = event.getTo().getBlock();
        
        // Simply add another line changing frame, under, world and name to add a new portal! (Similar to end portal)
        handlePortal(player, Material.OBSIDIAN, Material.EMERALD_BLOCK, Bukkit.getWorld("albion"), "albion", in, under);
        handlePortal(player, Material.OBSIDIAN, Material.DIAMOND_BLOCK, Bukkit.getWorld("world"), "world", in, under);
        //handlePortal(player, Material.OBSIDIAN, Material.OBSIDIAN, null, "anarchy nether", in, under);
        //handlePortal(player, Material.OBSIDIAN, Material.ENDER_STONE, null, "anarchy end", in, under);
    }

    public void handlePortal(BytecraftPlayer player, Material underType, Material frame, World newWorld, String worldName, Block in, Block under)
    {
        if (under.getType() != underType || !in.isLiquid()) {
            return;
        }

        if (  !(frameCheck(player, -1, 3, -1, 3, frame) ||
                frameCheck(player, -1, 3, -2, 2, frame) ||
                frameCheck(player, -1, 3, -3, 1, frame) ||
                frameCheck(player, -2, 2, -1, 3, frame) ||
                frameCheck(player, -2, 2, -2, 2, frame) ||
                frameCheck(player, -2, 2, -3, 1, frame) ||
                frameCheck(player, -3, 1, -1, 3, frame) ||
                frameCheck(player, -3, 1, -2, 2, frame) ||
                frameCheck(player, -3, 1, -3, 1, frame))) {
            return;
        }

        if (player.getWorld().getName().equalsIgnoreCase(newWorld.getName())) {
            player.teleportWithHorse(plugin.getWorldSpawn("world"));
            player.sendMessage(ChatColor.GOLD + "[PORTAL] " + ChatColor.GREEN + "Teleporting to main world!");
        } else {
            player.teleportWithHorse(plugin.getWorldSpawn(worldName));
            player.sendMessage(ChatColor.GOLD + "[PORTAL] " + ChatColor.GREEN + "Teleporting to " + worldName + "!");
        }
        player.setFireTicks(0);
    }
    
    public boolean frameCheck(BytecraftPlayer p, int x1, int x2, int z1, int z2, Material portal)
    {
        if(     p.getLocation().add(x1, 0, 0).getBlock().getType().equals(portal) && 
                p.getLocation().add(0, 0, z1).getBlock().getType().equals(portal) && 
                p.getLocation().add(x2, 0, 0).getBlock().getType().equals(portal) && 
                p.getLocation().add(0, 0, z2).getBlock().getType().equals(portal)) {
            return true;
        } else {
            return false;
        }
    }
}
