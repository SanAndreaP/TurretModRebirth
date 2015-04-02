package sanandreasp.mods.TurretMod3.item;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Facing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.entity.EntityMobileBase;

public class ItemMobileBase extends Item {

	public ItemMobileBase() {
		super();
	}

	@Override
    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10)
    {
        if (par3World.isRemote)
        {
            return true;
        }
        else
        {
            Block var11 = par3World.getBlock(par4, par5, par6);
            par4 += Facing.offsetsXForSide[par7];
            par5 += Facing.offsetsYForSide[par7];
            par6 += Facing.offsetsZForSide[par7];
            double var12 = 0.0D;

            if (par7 == 1 && var11.getRenderType() == 11)
            {
                var12 = 0.5D;
            }

            if (spawnBase(par3World, (double)par4 + 0.5D, (double)par5 + var12, (double)par6 + 0.5D) != null && !par2EntityPlayer.capabilities.isCreativeMode)
            {
                --par1ItemStack.stackSize;
            }

            return true;
        }
    }

    public static Entity spawnBase(World par0World, double par2, double par4, double par6)
    {
        EntityMobileBase var8 = new EntityMobileBase(par0World);
        var8.setLocationAndAngles(par2, par4, par6, MathHelper.wrapAngleTo180_float(par0World.rand.nextFloat() * 360.0F), 0.0F);
        var8.rotationYawHead = var8.rotationYaw;
        var8.renderYawOffset = var8.rotationYaw;
        par0World.spawnEntityInWorld(var8);
        var8.playLivingSound();
        return var8;
    }
}
