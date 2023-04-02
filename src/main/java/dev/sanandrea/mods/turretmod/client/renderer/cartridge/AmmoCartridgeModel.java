/* SPDX-License-Identifier: BSD-3-Clause                     *
 * Copyright © 2011-2023 SanAndreaP                          *
 * Full license text can be found within the LICENSE.md file */
package dev.sanandrea.mods.turretmod.client.renderer.cartridge;

import dev.sanandrea.mods.turretmod.api.TmrConstants;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.model.BakedQuad;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.model.ItemOverrideList;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraftforge.client.model.data.IModelData;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class AmmoCartridgeModel
        implements IBakedModel
{
    public static final ModelResourceLocation MODEL_RESOURCE_LOCATION
            = new ModelResourceLocation(new ResourceLocation(TmrConstants.ID, "ammo_cartridge"), "inventory");

    @Nonnull
    final                   IBakedModel           original;
    final AmmoCartridgeItemOverrides overrides;

    public AmmoCartridgeModel(@Nonnull IBakedModel original) {
        this.original = original;
        this.overrides = new AmmoCartridgeItemOverrides();
    }

    @Nonnull
    @Override
    @Deprecated
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction dir, @Nonnull Random rng) {
        return this.original.getQuads(state, dir, rng);
    }

    @Nonnull
    @Override
    public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @Nonnull Random rand, @Nonnull IModelData extraData) {
        throw new AssertionError("AmmoCartridgeModel::getQuads(..., IModelData) should never be called");
    }

    @Nonnull
    @Override
    public IModelData getModelData(@Nonnull IBlockDisplayReader world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull IModelData tileData) {
        throw new AssertionError("AmmoCartridgeModel::getModelData(...) should never be called");
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.original.useAmbientOcclusion();
    }

    @Override
    public boolean isGui3d() {
        return this.original.isGui3d();
    }

    @Override
    public boolean usesBlockLight() {
        return this.original.usesBlockLight();
    }

    @Override
    public boolean isCustomRenderer() {
        return this.original.isCustomRenderer();
    }

    @Nonnull
    @Override
    @Deprecated
    public TextureAtlasSprite getParticleIcon() {
        return this.original.getParticleIcon();
    }

    @Nonnull
    @Override
    public ItemOverrideList getOverrides() {
        return this.overrides;
    }

    @Nonnull
    @Override
    @Deprecated
    public ItemCameraTransforms getTransforms() {
        return this.original.getTransforms();
    }
}
