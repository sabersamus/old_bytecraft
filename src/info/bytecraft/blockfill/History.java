package info.bytecraft.blockfill;

import java.util.HashMap;
import java.util.Map;

import info.bytecraft.api.BytecraftPlayer;

public class History
{
    private Map<BytecraftPlayer, SavedBlocks> currentState;

    public History()
    {
        currentState = new HashMap<BytecraftPlayer, SavedBlocks>();
    }

    public void set(BytecraftPlayer player, SavedBlocks blocks)
    {
        currentState.put(player, blocks);
    }

    public SavedBlocks get(BytecraftPlayer player)
    {
        return currentState.get(player);
    }
}
