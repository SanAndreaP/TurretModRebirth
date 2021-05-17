/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.client.model.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonHandler;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.init.Resources;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class ModelTurretBase<E extends LivingEntity & ITurretInst>
		extends EntityModel<E>
		implements ModelJsonHandler<ModelTurretBase<E>, ModelJsonLoader.JsonBase>
{
	private ModelRenderer head;
	private ModelRenderer healthBar;
	private ModelRenderer ammoBar;

	private final ModelJsonLoader<ModelTurretBase<E>, ModelJsonLoader.JsonBase> modelJson;

	public ModelTurretBase() {
	    this(Resources.TURRET_T1_BASE.resource);
    }

    public ModelTurretBase(ResourceLocation modelLocation) {
		super();
		this.modelJson = ModelJsonLoader.create(this, modelLocation, this.getMandatoryBoxes().toArray(new String[0]));
	}

	@Override
	public void renderToBuffer(@Nonnull MatrixStack stack, @Nonnull IVertexBuilder builder, int light, int overlay, float red, float green, float blue, float alpha) {
		if( this.modelJson.isLoaded() ) {
			Arrays.asList(this.modelJson.getMainBoxes()).forEach((box) -> {
//					ITurretInst turret = (ITurretInst) entity;
//					int         b      = turret.getPartBrightnessForRender(getSubmergedBoxOffset(box));

				box.render(stack, builder, light, overlay, red, green, blue, alpha);
			});
		}
	}

//	private static double getSubmergedBoxOffset(ModelRenderer box) {
//		if( box.cubes.size() > 0 ) {
//			ModelBox cube = box.cubeList.get(0);
//			return (23.0D - box.rotationPointY - box.offsetY - cube.posY1) / 16.0D;
//		}
//
//		return (23.0D - box.rotationPointY - box.offsetY) / 16.0D;
//	}

	@Override
	public void setupAnim(E turret, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
		this.head.yRot = netHeadYaw / (180.0F / (float)Math.PI);
        this.head.xRot = headPitch / (180.0F / (float)Math.PI);

		float maxHealth = turret.isInGui() ? 2.0F : turret.get().getMaxHealth();
		float health = turret.isInGui() ? 1.0F : turret.get().getHealth();
		int maxAmmo = turret.isInGui() ? 2 : turret.getTargetProcessor().getMaxAmmoCapacity();
		int ammo = turret.isInGui() ? 1 : turret.getTargetProcessor().getAmmoCount();

        this.healthBar.zRot = -((float)Math.PI / 2.0F) * (Math.max(0.0F, maxHealth - health) / maxHealth);
        this.ammoBar.zRot = ((float)Math.PI / 2.0F) * (Math.max(0.0F, maxAmmo - ammo) / (float) maxAmmo);
	}

	@Override
	public void onReload(IResourceManager iResourceManager, ModelJsonLoader<ModelTurretBase<E>, ModelJsonLoader.JsonBase> loader) {
		loader.load();

		this.head = loader.getBox("head");
		this.healthBar = loader.getBox("healthBar");
		this.ammoBar = loader.getBox("ammoBar");
	}

	@Override
	public void setTexture(String s) { }

	@Override
	public float getBaseScale() {
		return 0.0F;
	}

	@Override
	public List<ModelRenderer> getBoxes() {
		return Arrays.asList(head, healthBar, ammoBar);
	}

	protected List<String> getMandatoryBoxes() {
		return Arrays.asList("head", "healthBar", "ammoBar");
	}
}
