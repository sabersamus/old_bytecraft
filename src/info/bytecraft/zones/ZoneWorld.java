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

package info.bytecraft.zones;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import org.bukkit.Location;
import org.bukkit.World;

import info.tregmine.quadtree.IntersectionException;
import info.tregmine.quadtree.Point;
import info.tregmine.quadtree.QuadTree;

public class ZoneWorld
{
    private World world;
    private QuadTree<Zone> zonesLookup;
    private QuadTree<Lot> lotLookup;
    private Map<String, Zone> zoneNameLookup;
    private Map<String, Lot> lotNameLookup;

    public ZoneWorld(World world)
    {
        this.world = world;
        this.zonesLookup = new QuadTree<Zone>();
        this.zoneNameLookup =
                new TreeMap<String, Zone>(new Comparator<String>() {
                    @Override
                    public int compare(String a, String b)
                    {
                        return a.compareToIgnoreCase(b);
                    }
                });
        this.lotLookup = new QuadTree<Lot>();
        this.lotNameLookup = new TreeMap<String, Lot>(new Comparator<String>() {
            @Override
            public int compare(String a, String b)
            {
                return a.compareToIgnoreCase(b);
            }
        });
    }

    public String getName()
    {
        return world.getName();
    }

    public void addZone(Zone zone) throws IntersectionException
    {
        zonesLookup.insert(zone.getRectangle(), zone);
        zoneNameLookup.put(zone.getName(), zone);
    }

    public void addLot(Lot lot) throws IntersectionException
    {
        lotLookup.insert(lot.getRect(), lot);
        lotNameLookup.put(lot.getName(), lot);
    }

    public boolean zoneExists(String name)
    {
        return zoneNameLookup.containsKey(name);
    }

    public boolean lotExists(String name)
    {
        return lotNameLookup.containsKey(name);
    }

    public Zone getZone(String name)
    {
        return zoneNameLookup.get(name);
    }

    public Lot getLot(String name)
    {
        return lotNameLookup.get(name);
    }

    public Zone findZone(Point p)
    {
        return zonesLookup.find(p);
    }

    public Lot findLot(Point p)
    {
        return lotLookup.find(p);
    }

    public Zone findZone(Location location)
    {
        Point p = new Point(location.getBlockX(), location.getBlockZ());
        return zonesLookup.find(p);
    }

    public Lot findLot(Location location)
    {
        Point p = new Point(location.getBlockX(), location.getBlockZ());
        return lotLookup.find(p);
    }

    public void deleteZone(String name)
    {
        if (!zoneNameLookup.containsKey(name)) {
            return;
        }

        zoneNameLookup.remove(name);

        this.zonesLookup = new QuadTree<Zone>(0);
        for (Zone zone : zoneNameLookup.values()) {
            try {
                zonesLookup.insert(zone.getRectangle(), zone);
            } catch (IntersectionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void deleteLot(String name)
    {
        if (!lotNameLookup.containsKey(name)) {
            return;
        }

        lotNameLookup.remove(name);

        this.lotLookup = new QuadTree<Lot>(0);
        for (Lot lot : lotNameLookup.values()) {
            try {
                lotLookup.insert(lot.getRect(), lot);
            } catch (IntersectionException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    public QuadTree<Zone> getQuadTree()
    {
        return this.zonesLookup;
    }
}
