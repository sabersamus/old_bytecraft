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

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;

public class ScoreboardClearTask implements Runnable
{
    private BytecraftPlayer player;

    private ScoreboardClearTask(BytecraftPlayer player)
    {
        this.player = player;
    }

    @Override
    public void run()
    {
        if (!player.isOnline()) {
            return;
        }

        try {
            ScoreboardManager manager = Bukkit.getScoreboardManager();
            player.setScoreboard(manager.getNewScoreboard());
        } catch (IllegalStateException e) {
            // We don't really care
        }
    }

    public static void start(Plugin plugin, BytecraftPlayer player)
    {
        Runnable runnable = new ScoreboardClearTask(player);

        Server server = Bukkit.getServer();
        BukkitScheduler scheduler = server.getScheduler();
        scheduler.scheduleSyncDelayedTask(plugin, runnable, 400);
    }
}
