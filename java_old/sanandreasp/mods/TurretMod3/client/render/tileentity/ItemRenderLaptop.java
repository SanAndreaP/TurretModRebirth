package sanandreasp.mods.TurretMod3.client.render.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import sanandreasp.mods.turretmod3.block.BlockLaptop;
import sanandreasp.mods.turretmod3.client.model.ModelLaptop;
import sanandreasp.mods.turretmod3.registry.TM3ModRegistry;

public class ItemRenderLaptop implements IItemRenderer {
	private ModelLaptop model = new ModelLaptop();

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		switch (type) {
			case ENTITY: renderLaptop(0.0F, -0.45F, 0.0F, BlockLaptop.getType(item.getItemDamage())); break;
			case EQUIPPED: renderLaptop(0.4F, 0.4F, 0.5F, BlockLaptop.getType(item.getItemDamage())); break;
			case INVENTORY: renderLaptop(1F, 0.26F, 1F, BlockLaptop.getType(item.getItemDamage())); break;
			default: break;
		}
	}

	private void renderLaptop(float x, float y, float z, int type){
		//Tessellator tesselator = Tessellator.instance;
		Minecraft.getMinecraft().getTextureManager().bindTexture(type == 1 ? TM3ModRegistry.TEX_BLACKLAP : TM3ModRegistry.TEX_WHITELAP);
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y+1.6F, z);
		GL11.glRotatef(180F, 1F, 0F, 0F);
		model.renderBlock();
		GL11.glPopMatrix();
	}

}
