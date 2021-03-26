package de.sanandrew.mods.turretmod.api.turret;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.eventbus.EventBus;
import net.minecraftforge.eventbus.api.BusBuilder;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@SuppressWarnings("unused")
public interface ITargetProcessor
{
    EventBus TARGET_BUS = new EventBus(BusBuilder.builder().setTrackPhases(false));

    boolean addAmmo(@Nonnull ItemStack stack);

    boolean addAmmo(@Nonnull ItemStack stack, ICapabilityProvider excessInv);

    int getAmmoCount();

    @Nonnull
    ItemStack getAmmoStack();

    boolean hasAmmo();

    void dropExcessAmmo();

    void decrAmmo();

    void putAmmoInInventory(ICapabilityProvider inventory);

    boolean isAmmoApplicable(@Nonnull ItemStack stack);

    ApplyType getAmmoApplyType(@Nonnull ItemStack stack);

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

    boolean isPlayerTargeted(UUID id);

    boolean isEntityTargeted(ResourceLocation id);

    ITurretInst getTurretInst();

    boolean hasTarget();

    Entity getTarget();

    CompoundNBT save(CompoundNBT nbt);

    void load(CompoundNBT nbt);

    ResourceLocation[] getEnabledEntityTargets();

    UUID[] getEnabledPlayerTargets();

    Map<ResourceLocation, Boolean> getEntityTargets();

    Map<UUID, Boolean> getPlayerTargets();

    void updateEntityTarget(ResourceLocation res, boolean active);

    void updatePlayerTarget(UUID uid, boolean active);

    void updateEntityTargets(ResourceLocation[] keys);

    void updatePlayerTargets(UUID[] uuids);

    ITextComponent getTargetName();

    void onTick();

    void onTickClient();

    boolean isEntityBlacklist();

    boolean isPlayerBlacklist();

    void setEntityBlacklist(boolean isBlacklist);

    void setPlayerBlacklist(boolean isBlacklist);

    enum ApplyType {
        ADD,
        REPLACE,
        NOT_COMPATIBLE
    }
}
