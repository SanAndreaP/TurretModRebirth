package cofh.api.block;

import cofh.api.tileentity.ITileInfo;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentBase;
import net.minecraft.world.IBlockAccess;
import net.minecraft.util.EnumFacing;

/**
 * Implement this interface on blocks which can provide information about themselves. If the block contains Tile Entities, then it is recommended that this
 * function serve as a passthrough for {@link ITileInfo}.
 * 
 * @author King Lemming
 * 
 */
public interface IBlockInfo {

	/**
	 * This function appends information to a list provided to it.
	 * 
	 * @param world
	 *            Reference to the world.
	 * @param pos
	 *            coordinates of the block.
	 * @param side
	 *            The side of the block that is being queried.
	 * @param player
	 *            Player doing the querying - this can be NULL.
	 * @param info
	 *            The list that the information should be appended to.
	 * @param debug
	 *            If true, the block should return "debug" information.
	 */
	void getBlockInfo(IBlockAccess world, BlockPos pos, EnumFacing side, EntityPlayer player, List<TextComponentBase> info, boolean debug);

}
