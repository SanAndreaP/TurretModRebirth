/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.api.turret;

import de.sanandrew.mods.turretmod.api.IRegistryObject;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.Pose;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

@SuppressWarnings({"SameReturnValue", "unused"})
public interface ITurret
        extends IRegistryObject, Comparable<ITurret>
{
    default void entityInit(ITurretEntity turret) { }

    default void applyEntityAttributes(ITurretEntity turret) { }

    @Nonnull
    ResourceLocation getModelLocation();

    ResourceLocation getBaseTexture(ITurretEntity turret);

    ResourceLocation getGlowTexture(ITurretEntity turret);

    SoundEvent getShootSound(ITurretEntity turret);

    AxisAlignedBB getRangeBB(@Nullable ITurretEntity turret);

    /**
     * <p>Returns the classpath to a custom render class. If no custom class is needed, this returns <tt>null</tt>.</p>
     * <p>There are certain requirements this class needs to fullfill:</p>
     * <ul>
     *     <li>extends {@link net.minecraft.client.renderer.entity.LivingRenderer net.minecraft.client.renderer.entity.LivingRenderer}<br>
     *         {@link net.minecraft.client.renderer.entity.LivingRenderer &lt;T extends net.minecraft.entity.LivingEntity & ITurretInst,<br>}
     *         {@link net.minecraft.client.renderer.entity.LivingRenderer &nbsp;M extends EntityModel&lt;T&gt;&gt;}.</li>
     *     <li>has a constructor with following parameters:
     *          <ul>
     *              <li>{@link net.minecraft.client.renderer.entity.EntityRendererManager}</li>
     *              <li>{@link java.util.function.Supplier java.util.function.Supplier&lt;M&gt;} (This supplier should be called to get the model instance for the renderer)</li>
     *          </ul>
     *     </li>
     * </ul>
     * @return A classpath to a render class or <tt>null</tt>
     */
    default String getCustomRenderClass() {
        return null;
    }

    /**
     * <p>Returns the classpath to a custom model class. If no custom class is needed, this returns <tt>null</tt>.</p>
     * <p>There are certain requirements this class needs to fullfill:</p>
     * <ul>
     *     <li>extends {@link net.minecraft.client.renderer.entity.model.EntityModel net.minecraft.client.renderer.entity.model.EntityModel}<br>
     *         {@link net.minecraft.client.renderer.entity.model.EntityModel &lt;T extends net.minecraft.entity.LivingEntity & ITurretInst&gt;}.</li>
     *     <li>has a constructor with following parameters:
     *          <ul>
     *              <li>{@link ResourceLocation} (this points to a JSON file, which can be loaded with the {@link de.sanandrew.mods.sanlib.lib.client.ModelJsonLoader} class)</li>
     *          </ul>
     *     </li>
     * </ul>
     *
     * @return A classpath to a model class or <tt>null</tt>
     */
    default String getCustomModelClass() {
        return null;
    }

    int getTier();

    default void tick(ITurretEntity turret) { }

    default void writeSpawnData(ITurretEntity turret, PacketBuffer buf) { }

    default void readSpawnData(ITurretEntity turret, PacketBuffer buf) { }

    default void writeSyncData(ITurretEntity turret, ObjectOutputStream stream) throws IOException { }

    default void readSyncData(ITurretEntity turret, ObjectInputStream stream) throws IOException { }

    default void onSave(ITurretEntity turret, CompoundNBT nbt) { }

    default void onLoad(ITurretEntity turret, CompoundNBT nbt) { }

    default float getDeactiveHeadPitch() {
        return 30.0F;
    }

    default boolean canSeeThroughBlocks() {
        return false;
    }

    default SoundEvent getIdleSound(ITurretEntity turret) {
        return null;
    }

    default SoundEvent getHurtSound(ITurretEntity turret) {
        return null;
    }

    default SoundEvent getDeathSound(ITurretEntity turret) {
        return null;
    }

    default SoundEvent getEmptySound(ITurretEntity turret) {
        return null;
    }

    default SoundEvent getPickupSound(ITurretEntity turret) {
        return null;
    }

    float getHealth();

    int getAmmoCapacity();

    int getReloadTicks();

    default TargetType getTargetType() {
        return TargetType.GROUND;
    }

    default boolean isBuoy() {
        return false;
    }

    default float getEyeHeight(Pose poseIn, EntitySize sizeIn) {
        return sizeIn.height * 0.85F;
    }

    @Override
    default int compareTo(ITurret t) {
        return this.getId().compareTo(t.getId());
    }

    enum TargetType
    {
        ALL,
        GROUND,
        AIR,
        WATER
    }
}
