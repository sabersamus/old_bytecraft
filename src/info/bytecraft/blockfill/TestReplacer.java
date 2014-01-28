package info.bytecraft.blockfill;

import info.bytecraft.Bytecraft;

import org.bukkit.block.Block;
import org.bukkit.material.MaterialData;

import info.bytecraft.api.BytecraftPlayer;

public class TestReplacer extends AbstractFiller
{
    private BytecraftPlayer player;
    private MaterialData item;
    private MaterialData match;

    public TestReplacer(Bytecraft plugin, BytecraftPlayer player, Block block1,
            Block block2, MaterialData match, MaterialData item, int workSize)
    {
        super(plugin, block1, block2, workSize);
        this.player = player;
        this.item = item;
        this.match = match;
    }

    @Override
    public void changeBlock(Block block)
    {
        if (block.getTypeId() == match.getItemTypeId()
                && (match.getData() == 0 || block.getData() == match.getData())) {

            player.getDelegate().sendBlockChange(block.getLocation(),
                                                 item.getItemType(),
                                                 item.getData());
        }
    }
}
