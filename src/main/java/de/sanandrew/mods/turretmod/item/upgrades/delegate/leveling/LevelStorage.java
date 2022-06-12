package de.sanandrew.mods.turretmod.item.upgrades.delegate.leveling;

import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeData;
import de.sanandrew.mods.turretmod.network.SyncTurretStages;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifierManager;
import net.minecraft.entity.ai.attributes.ModifiableAttributeInstance;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"java:S1104", "java:S1444", "unused"})
@IUpgradeData.Syncable
public class LevelStorage
        implements IUpgradeData<LevelStorage>
{
    private static final String NBT_EXPERIENCE  = "Experience";

    private static final Map<ResourceLocation, Stage> STAGES = new ConcurrentHashMap<>();

    public static int maxXp  = 5_345; // level 50

    int xp;
    Long lastUpdate = null;

    private int     prevXp;
    private int     prevLvl;
    private int     cachedLevel;
    private boolean initialized = false;

    private Integer modifierHash = null;

    LevelStorage() {
        this.xp = 0;
        this.prevXp = 0;
        this.prevLvl = 0;
        this.cachedLevel = -1;
    }

    @Override
    public void load(ITurretEntity turretInst, @Nonnull CompoundNBT nbt) {
        this.xp = nbt.getInt(NBT_EXPERIENCE);
        this.updateModHash();
    }

    @Override
    public void save(ITurretEntity turretInst, @Nonnull CompoundNBT nbt) {
        nbt.putInt(NBT_EXPERIENCE, this.xp);
    }

    public static void addStage(ResourceLocation id, Stage s) {
        STAGES.put(id, s);
    }

    public static void applyStages(Map<ResourceLocation, Stage> s) {
        STAGES.clear();
        STAGES.putAll(s);
    }

    private static int getMinXpForLevel(int lvl) {
        if( lvl < 0 ) {
            return 0;
        } else if( lvl < 17 ) {
            return lvl * lvl + 6 * lvl;
        } else if( lvl < 32 ) {
            return MathHelper.floor(2.5D * lvl * lvl - 40.5D * lvl + 360);
        } else {
            return MathHelper.floor(4.5D * lvl * lvl - 162.5D * lvl + 2220);
        }
    }

    @Override
    public void onTick(ITurretEntity turretInst) {
        LivingEntity e = turretInst.get();
        if( !e.level.isClientSide && this.prevXp != this.xp ) {
            turretInst.getUpgradeProcessor().syncUpgrade(Leveling.ID);

            this.prevXp = this.xp;
            this.cachedLevel = -1;

            applyEffects(turretInst, this.initialized);
        }

        if( !this.initialized ) {
            this.initialized = true;

            cleanupModifiers(e.getAttributes());
        } else if( StageLoader.needsUpdate(this) ) {
            this.lastUpdate = System.currentTimeMillis();

            this.prevLvl = -1;
            cleanupModifiers(e.getAttributes());
            applyEffects(turretInst, false);
        }
    }

    private static void cleanupModifiers(AttributeModifierManager attribs) {
        List<UUID> currModifierIds = STAGES.values().stream().map(stage -> stage.modifiers).flatMap(Stream::of).map(m -> m.mod.getId()).collect(Collectors.toList());
        removeModifiers(attribs, currModifierIds);
    }

    private static void removeModifiers(AttributeModifierManager attribMgr, @Nonnull List<UUID> idsToStay) {
        ForgeRegistries.ATTRIBUTES.forEach(a -> {
            if( attribMgr.hasAttribute(a) ) {
                ModifiableAttributeInstance ai = Objects.requireNonNull(attribMgr.getInstance(a));
                List<UUID> danglingModifiers = ai.getModifiers().stream()
                                                 .filter(m -> filterDanglingModifiers(m, idsToStay)).map(AttributeModifier::getId)
                                                 .collect(Collectors.toList());
                for( UUID dmId : danglingModifiers ) {
                    ai.removeModifier(dmId);
                }
            }
        });
    }

    private static boolean filterDanglingModifiers(AttributeModifier m, List<UUID> currModifierIds) {
        return m.getName().startsWith(Stage.NAME_PREFIX) && !currModifierIds.contains(m.getId());
    }

    void applyEffects(ITurretEntity turretInst, boolean playSound) {
        final int currLevel = this.getLevel();
        if( this.prevLvl != currLevel ) {
            this.prevLvl = currLevel;

            STAGES.forEach((r, s) -> {
                if( s.check(currLevel) ) {
                    s.apply(turretInst, playSound);
                }
            });
            this.updateModHash();
        }
    }

    void clearEffects(ITurretEntity turretInst) {
        removeModifiers(turretInst.get().getAttributes(), Collections.emptyList());
        this.updateModHash();
    }

    public Map<Attribute, Stage.ModifierInfo> fetchCurrentModifiers() {
        return Stage.fetchModifiers(this.fetchCurrentStages());
    }

    private List<Stage> fetchCurrentStages() {
        final int currLevel = this.getLevel();
        return STAGES.values().stream().filter(s -> s.check(currLevel)).collect(Collectors.toList());
    }

    public int getXp() {
        return Math.min(this.xp, maxXp);
    }

    public int getExcessXp() {
        return Math.max(this.xp - maxXp, 0);
    }

    public int getFullXp() {
        return this.xp;
    }

    public int retrieveExcessXp() {
        int exp = this.getExcessXp();
        this.xp -= exp;

        return exp;
    }

    public void addXp(int xp) {
        if( xp > 0 ) {
            this.xp += xp;
        }
    }

    /*
    ------------------------------------------------------------------------------------------------------------------------
    How I figured out the formula for getting the level at {x} XP points (last formula, as it was the most complicated one)
    Base formula (y = 4.5x² - 162.5x + 2220; x = level; y = total xp points) from minecraft wiki
        https://minecraft.gamepedia.com/Experience#Leveling_up
    ------------------------------------------------------------------------------------------------------------------------

    (a - b)²            = a² - 2ab + b²                              | binomial formula base, used later

    y                            = 4.5x² - 162.5x + 2220             | switch sides
    4.5x² - 162.5x + 2220        = y                                 | - 2220 => remove this from the x-side, because it would mess up the binomial conversion later, as it would linger around in the x-side and not fit within the formula
    4.5x² - 162.5x               = y - 2220                          | / 4.5
    x² - 36.̅1x                   = 0.̅2y - 493,̅3                      | + (-36.̅1 / 2)² = (-18.0̅5)²  => reason: fullfill b variable for binomial formula (b = -18.0̅5, because -36.̅1x -> 2x*(-18.0̅5) -> -2ab)
    x² - 36.̅1x + (-18.0̅5)²       = (-18.0̅5)² + 0.̅2y - 493,̅3          | binomial conversion (a² + 2ab + b² => (a + b)²) & y-side shortening
    (x - 18.0̅5)²                 = -167.33 + 0.̅2y                    | square root
    x - 18.0̅5                    = sqrt(0.̅2y - 167.33)               | + 18.0̅5

    x = sqrt(0.̅2y - 167.33) + 18.0̅5

    Notes: - to correctly get the level and not underlevel, 0.2̅ needs to be written as a simple fraction in code -> (10.0D / 45.0D)
           - 18.05̅ can be rounded to 3 decimal places -> 18.056D
           - The resulting decimal is floored, providing an integer

    */
    public int getLevel() {
        if( this.cachedLevel > -1 ) {
            return this.cachedLevel;
        }

        int xpc = this.getXp();
        if( xpc <= 352 ) { // below lvl 16
            return MathHelper.floor(Math.sqrt(xpc + 9.0D) - 3.0D);
        } else if( xpc <= 1507 ) { // below lvl 31
            return MathHelper.floor(Math.sqrt(0.4D * xpc - 78.39D) + 8.1D);
        } else { // at or above lvl 31
            return MathHelper.floor(Math.sqrt((10.0D / 45.0D) * xpc - 167.33D) + 18.056D);
        }
    }

    /** @return the minimum amount of XP for the next level (absolute from 0) */
    public int getNextLevelMinXp() {
        return getMinXpForLevel(this.getLevel() + 1);
    }

    /** @return the minimum amount of XP for the current level (absolute from 0) */
    public int getCurrentLevelMinXp() {
        return getMinXpForLevel(this.getLevel());
    }

    public static SyncTurretStages getPacket() {
        return new SyncTurretStages(STAGES);
    }

    private void updateModHash() {
        Map<Attribute, Stage.ModifierInfo> currMod = fetchCurrentModifiers();
        this.modifierHash = 0;
        currMod.forEach((key, value) -> this.modifierHash = Objects.hash(this.modifierHash, key, value));
    }

    public int getModHash() {
        if( this.modifierHash == null ) {
            this.updateModHash();
        }

        return this.modifierHash;
    }
}
