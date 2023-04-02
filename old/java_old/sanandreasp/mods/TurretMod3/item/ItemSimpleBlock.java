package sanandreasp.mods.TurretMod3.item;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlockWithMetadata;

public class ItemSimpleBlock extends ItemBlockWithMetadata{
    public ItemSimpleBlock(Block block) {
        super(block, block);
    }
}
