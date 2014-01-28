package info.bytecraft.blockfill;

import info.bytecraft.Bytecraft;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;

import info.bytecraft.api.BytecraftPlayer;

public class TestFiller extends AbstractFiller
{
    private BytecraftPlayer player;
    private MaterialData item;

    public TestFiller(Bytecraft plugin, BytecraftPlayer player, Block block1,
            Block block2, MaterialData item, int workSize)
    {
        super(plugin, block1, block2, workSize);
        this.player = player;
        this.item = item;
    }

    @Override
    public void changeBlock(Block block)
    {
        player.getDelegate().sendBlockChange(block.getLocation(),
                                             item.getItemType(),
                                             item.getData());
    }
}
