package de.sanandrew.mods.turretmod.client.renderer.projectile;

import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.ammo.IProjectileEntity;
import de.sanandrew.mods.turretmod.item.ammo.AmmunitionRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;

public class CrossbowBoltRenderer<E extends Entity & IProjectileEntity>
        extends TurretProjectileBaseRenderer<E>
{
    public CrossbowBoltRenderer(EntityRendererManager manager) {
        super(manager);
    }

    @Override
    public void vertex(E projectileInst, Matrix4f pose, Matrix3f normal, IVertexBuilder builder, int x, int y, int z, float u, float v, int nrmX, int nrmZ, int nrmY, int light) {
        ItemStack ammoItem = AmmunitionRegistry.INSTANCE.getItem(projectileInst.getAmmunition().getId(), projectileInst.getAmmunitionSubtype());
        ColorObj cObj = new ColorObj(Minecraft.getInstance().getItemColors().getColor(ammoItem, 0));

        builder.vertex(pose, (float)x, (float)y, (float)z).color(cObj.red(), cObj.green(), cObj.blue(), 255).uv(u, v)
               .overlayCoords(OverlayTexture.NO_OVERLAY).uv2(light).normal(normal, (float)nrmX, (float)nrmY, (float)nrmZ).endVertex();
    }
}
