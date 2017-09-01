package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.EventBus;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ITargetProcessor
{
    EventBus TARGET_BUS = new EventBus();

    boolean addAmmo(@Nonnull ItemStack stack);

    int getAmmoCount();

    @Nonnull
    ItemStack getAmmoStack();

    boolean hasAmmo();

    void dropExcessAmmo();

    void decrAmmo();

    boolean isAmmoApplicable(@Nonnull ItemStack stack);

    int getMaxAmmoCapacity();

    int getMaxShootTicks();

    boolean isShooting();

    Entity getProjectile();

    double getRangeVal();

    AxisAlignedBB getAdjustedRange(boolean doOffset);

    boolean shootProjectile();

    void playSound(SoundEvent sound, float volume);

    boolean isEntityValidTarget(Entity entity);

    ITurretInst getTurret();

    boolean hasTarget();

    Entity getTarget();

    void writeToNbt(NBTTagCompound nbt);

    void readFromNbt(NBTTagCompound nbt);

    List<Class<? extends Entity>> getEnabledEntityTargets();

    UUID[] getEnabledPlayerTargets();

    Map<Class<? extends Entity>, Boolean> getEntityTargets();

    Map<UUID, Boolean> getPlayerTargets();

    void updateEntityTarget(Class<? extends Entity> cls, boolean active);

    void updatePlayerTarget(UUID uid, boolean active);

    void updateEntityTargets(List<Class<? extends Entity>> classes);

    void updatePlayerTargets(UUID[] uuids);

    String getTargetName();

    void onTick();
}
