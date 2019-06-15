package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fml.common.eventhandler.EventBus;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public interface ITargetProcessor
{
    EventBus TARGET_BUS = new EventBus();

    boolean addAmmo(@Nonnull ItemStack stack);

    boolean addAmmo(@Nonnull ItemStack stack, ICapabilityProvider excessInv);

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

    boolean canShoot();

    void setShot(boolean success);

    void decrInitShootTicks();

    void resetInitShootTicks();

    Entity getProjectile();

    double getRangeVal();

    AxisAlignedBB getAdjustedRange(boolean doOffset);

    boolean shootProjectile();

    void playSound(SoundEvent sound, float volume);

    boolean isEntityValidTarget(Entity entity);

    List<Entity> getValidTargetList();

    boolean isEntityTargeted(Entity entity);

    ITurretInst getTurretInst();

    boolean hasTarget();

    Entity getTarget();

    void writeToNbt(NBTTagCompound nbt);

    void readFromNbt(NBTTagCompound nbt);

    ResourceLocation[] getEnabledEntityTargets();

    UUID[] getEnabledPlayerTargets();

    Map<ResourceLocation, Boolean> getEntityTargets();

    Map<UUID, Boolean> getPlayerTargets();

    void updateEntityTarget(ResourceLocation res, boolean active);

    void updatePlayerTarget(UUID uid, boolean active);

    void updateEntityTargets(ResourceLocation[] keys);

    void updatePlayerTargets(UUID[] uuids);

    String getTargetName();

    void onTick();

    void onTickClient();

    boolean isEntityBlacklist();

    boolean isPlayerBlacklist();

    void setEntityBlacklist(boolean isBlacklist);

    void setPlayerBlacklist(boolean isBlacklist);
}
