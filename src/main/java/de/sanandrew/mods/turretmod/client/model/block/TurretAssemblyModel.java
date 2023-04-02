/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package de.sanandrew.mods.turretmod.client.model.block;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonHandler;
import de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader;
import de.sanandrew.mods.turretmod.api.Resources;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.tileentity.assembly.TurretAssemblyEntity;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.resources.IResourceManager;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;

public class TurretAssemblyModel
        extends Model
        implements ModelJsonHandler<TurretAssemblyModel, ModelJsonLoader.JsonBase>
{
    private ModelRenderer base;
    private ModelRenderer robotBinding;
    private ModelRenderer robotArm;

    private final ModelJsonLoader<TurretAssemblyModel, ModelJsonLoader.JsonBase> modelJson;

    public TurretAssemblyModel() {
        super(RenderType::entityCutoutNoCull);

        this.modelJson = ModelJsonLoader.create(this, Resources.MODEL_TILE_TURRET_ASSEMBLY_MODEL, "base", "robotBinding", "robotArm");
    }

    @Override
    public void renderToBuffer(@Nonnull MatrixStack mStack, @Nonnull IVertexBuilder vBuilder, int light, int overlay,
                               float red, float green, float blue, float alpha)
    {
        if( this.modelJson.isLoaded() ) {
            for( ModelRenderer box : this.modelJson.getMainBoxes() ) {
                box.render(mStack, vBuilder, light, overlay, red, green, blue, alpha);
            }
        }
    }

    @Override
    public void onReload(IResourceManager rManager, ModelJsonLoader<TurretAssemblyModel, ModelJsonLoader.JsonBase> loader) {
        loader.load();

        this.base = loader.getBox("base");
        this.robotBinding = loader.getBox("robotBinding");
        this.robotArm = loader.getBox("robotArm");
    }

    @Override
    public void setTexture(String s) { /* no-op */ }

    @Override
    public List<ModelRenderer> getBoxes() {
        return Arrays.asList(this.base, this.robotBinding, this.robotArm);
    }

    public void setupAnim(TurretAssemblyEntity tile, float armX, float armZ) {
        int meta = tile.hasLevel() ? BlockRegistry.TURRET_ASSEMBLY.getDirection(tile.getBlockState()).get2DDataValue() : 0;
        this.base.yRot = (float)(90.0D * meta / 180.0D * Math.PI);

        this.robotBinding.x = armX;
        this.robotArm.z = armZ;
    }
}
