package de.sanandrew.mods.turretmod.api.turret;

import de.sanandrew.mods.turretmod.api.ResourceLocations;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * An abstract "data-like" class of {@link ITurret} for easy implementation.
 */
@SuppressWarnings("unused")
public abstract class Turret
        implements ITurret
{
    protected final ResourceLocation id;
    protected final int tier;
    protected final boolean isBuoy;
    protected final AttackType attackType;
    protected final SoundIds soundIds;
    /**
     * <p>Holds the location to a custom model JSON. If needed, this should be set during construction.</p>
     */
    protected ResourceLocation model;
    /**
     * <p>Holds the location to the base texture. If needed, this should be set during construction.</p>
     */
    protected ResourceLocation baseTexture;
    /**
     * <p>Holds the location to the glowing texture. This should be set during construction.</p>
     */
    protected ResourceLocation glowTexture;
    /**
     * <p>Holds the classpath to a custom render class. If needed, this should be set during construction.</p>
     * @see ITurret#getCustomRenderClass()
     */
    protected String customRenderClass;
    /**
     * <p>Holds the classpath to a custom model class. If needed, this should be set during construction.</p>
     * @see ITurret#getCustomModelClass()
     * <p>The custom class needs to be extending {@link net.minecraft.client.renderer.entity.LivingRenderer LivingRenderer&lt;T extends LivingEntity & ITurretInst, M extends EntityModel<T>&gt;}.</p>
     */
    protected String customModelClass;
    protected AxisAlignedBB range;

    protected SoundEvent    shootSound;  // lazy-loaded
    protected SoundEvent    emptySound;  // lazy-loaded
    protected SoundEvent    idleSound;   // lazy-loaded
    protected SoundEvent    hurtSound;   // lazy-loaded
    protected SoundEvent    deathSound;  // lazy-loaded
    protected SoundEvent    pickupSound; // lazy-loaded

    protected float         health;       // configurable
    protected int           ammoCapacity; // configurable
    protected int           reloadTicks;  // configurable

    public Turret(ResourceLocation id, int tier, boolean isBuoy, AttackType attackType, SoundIds soundIds) {
        this.id = id;
        this.tier = tier;
        this.isBuoy = isBuoy;
        this.attackType = attackType;
        this.soundIds = soundIds;

        this.health = 20.0F;
        this.ammoCapacity = 256;
        this.reloadTicks = 20;
        this.model = isBuoy ? ResourceLocations.MODEL_ENTITY_BASE_BUOY : ResourceLocations.MODEL_ENTITY_BASE;
    }

    /**
     * <p>Returns the ID used during construction of the object.</p>
     * @see ITurret#getId()
     */
    @Nonnull
    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Nonnull
    @Override
    public ResourceLocation getModelLocation() {
        return this.model;
    }

    @Override
    public String getCustomRenderClass() {
        return this.customRenderClass;
    }

    @Override
    public String getCustomModelClass() {
        return this.customModelClass;
    }

    @Override
    public ResourceLocation getBaseTexture(ITurretInst turretInst) {
        return this.baseTexture;
    }

    @Override
    public ResourceLocation getGlowTexture(ITurretInst turretInst) {
        return this.glowTexture;
    }

    @Override
    public SoundEvent getShootSound(ITurretInst turretInst) {
        return this.shootSound = lazyLoad(this.soundIds.shootSound, this.shootSound);
    }

    @Override
    public SoundEvent getEmptySound(ITurretInst turretInst) {
        return this.emptySound = lazyLoad(this.soundIds.emptySound, this.emptySound);
    }

    @Override
    public SoundEvent getIdleSound(ITurretInst turretInst) {
        return this.idleSound = lazyLoad(this.soundIds.idleSound, this.idleSound);
    }

    @Override
    public SoundEvent getHurtSound(ITurretInst turretInst) {
        return this.hurtSound = lazyLoad(this.soundIds.hurtSound, this.hurtSound);
    }

    @Override
    public SoundEvent getDeathSound(ITurretInst turretInst) {
        return this.deathSound = lazyLoad(this.soundIds.deathSound, this.deathSound);
    }

    @Override
    public SoundEvent getPickupSound(ITurretInst turretInst) {
        return this.pickupSound = lazyLoad(this.soundIds.pickupSound, this.pickupSound);
    }

    @Override
    public AxisAlignedBB getRangeBB(@Nullable ITurretInst turretInst) {
        return this.range;
    }

    @Override
    public int getTier() {
        return this.tier;
    }

    @Override
    public float getHealth() {
        return this.health;
    }

    @Override
    public int getAmmoCapacity() {
        return this.ammoCapacity;
    }

    @Override
    public int getReloadTicks() {
        return this.reloadTicks;
    }

    @Override
    public AttackType getAttackType() {
        return this.attackType;
    }

    @Override
    public boolean isBuoy() {
        return this.isBuoy;
    }

    protected static SoundEvent lazyLoad(ResourceLocation id, SoundEvent currEvent) {
        return currEvent != null ? currEvent : (id != null ? ForgeRegistries.SOUND_EVENTS.getValue(id) : null);
    }

    public static final class SoundIds
    {
        public final ResourceLocation shootSound;
        public ResourceLocation emptySound;
        public ResourceLocation idleSound;
        public ResourceLocation hurtSound;
        public ResourceLocation deathSound;
        public ResourceLocation pickupSound;

        public SoundIds(ResourceLocation shootSoundId) {
            this.shootSound = shootSoundId;
        }

        public SoundIds withEmpty(ResourceLocation soundId) {
            this.emptySound = soundId;

            return this;
        }

        public SoundIds withIdle(ResourceLocation soundId) {
            this.idleSound = soundId;

            return this;
        }

        public SoundIds withHurt(ResourceLocation soundId) {
            this.hurtSound = soundId;

            return this;
        }

        public SoundIds withDeath(ResourceLocation soundId) {
            this.deathSound = soundId;

            return this;
        }

        public SoundIds withPickup(ResourceLocation soundId) {
            this.pickupSound = soundId;

            return this;
        }

        public SoundIds withDefaults() {
            this.emptySound = ResourceLocations.SOUND_TURRET_EMPTY;
            this.idleSound = ResourceLocations.SOUND_TURRET_IDLE;
            this.hurtSound = ResourceLocations.SOUND_TURRET_HIT;
            this.deathSound = ResourceLocations.SOUND_TURRET_DEATH;

            return this;
        }
    }
}
