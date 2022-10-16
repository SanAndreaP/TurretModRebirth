package de.sanandrew.mods.turretmod.init.config;

import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.entity.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.entity.turret.delegate.Crossbow;
import de.sanandrew.mods.turretmod.init.TmrConfig;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class TurretConfig
{
    private final ForgeConfigSpec.ConfigValue<Boolean> showVariantsInItemGroup;
    private final ForgeConfigSpec.ConfigValue<Boolean> renderFancyShields;
    private final ForgeConfigSpec.ConfigValue<Boolean> canPlayersEditAll;
    private final ForgeConfigSpec.ConfigValue<Boolean> canAdminsEditAll;

    private final Map<Class<? extends ITurret>, TurretSpec> turretMap = new HashMap<>();

    private TurretConfig(ForgeConfigSpec.Builder builder) {
        this.showVariantsInItemGroup = builder.define("showVariantsInItemGroup", false);
        this.renderFancyShields = builder.define("renderFancyShields", true);
        this.canPlayersEditAll = builder.define("canPlayersEditAll", false);
        this.canAdminsEditAll = builder.define("canAdminsEditAll", true);

        this.turretMap.put(Crossbow.class, new TurretSpec(builder.push("crossbow"), 20, 256, 20, new RangeSpec(16, 8, 4)));
    }

    public void load() {
        TurretRegistry.INSTANCE.getAll().forEach(ITurret::initializeFromConfig);
    }

    public boolean showVariantsInItemGroup() {
        return Boolean.TRUE.equals(this.showVariantsInItemGroup.get());
    }

    public boolean renderFancyShields() {
        return Boolean.TRUE.equals(this.renderFancyShields.get());
    }

    public boolean canPlayersEditAll() {
        return Boolean.TRUE.equals(this.canPlayersEditAll.get());
    }

    public boolean canAdminsEditAll() {
        return Boolean.TRUE.equals(this.canAdminsEditAll.get());
    }

    public static TurretConfig build(ForgeConfigSpec.Builder builder) {
        return new TurretConfig(builder.push("turrets"));
    }

    public static TurretSpec getConfig(Class<? extends ITurret> turretCls) {
        return Objects.requireNonNull(TmrConfig.TURRETS.turretMap.get(turretCls));
    }

    public static class TurretSpec
    {
        public final ForgeConfigSpec.ConfigValue<Double>  health;
        public final ForgeConfigSpec.ConfigValue<Integer> ammoCapacity;
        public final ForgeConfigSpec.ConfigValue<Integer> reloadTicks;
        private final Range range;

        private TurretSpec(ForgeConfigSpec.Builder builder, double health, int ammoCap, int reloadTicks, RangeSpec range) {
            this.health = builder.worldRestart().define("health", health, h -> h instanceof Number && ((Number) h).doubleValue() > 0);
            this.ammoCapacity = builder.worldRestart().define("ammoCapacity", ammoCap, a -> a instanceof Number && ((Number) a).intValue() > 0);
            this.reloadTicks = builder.worldRestart().define("ticksToReload", reloadTicks, r -> r instanceof Number && ((Number) r).intValue() > 0);
            this.range = new Range(builder, range);
            builder.pop();
        }

        public AxisAlignedBB getRange() {
            return this.range.getAABB();
        }
    }

    public static class RangeSpec {
        public final int minX;
        public final int minY;
        public final int minZ;
        public final int maxX;
        public final int maxY;
        public final int maxZ;

        public RangeSpec(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        public RangeSpec(int h, int vd, int vu) {
            this(-h, -vd, -h, h, vu, h);
        }

    }

    public static class Range
    {
        public final ForgeConfigSpec.ConfigValue<Integer> minX;
        public final ForgeConfigSpec.ConfigValue<Integer> minY;
        public final ForgeConfigSpec.ConfigValue<Integer> minZ;
        public final ForgeConfigSpec.ConfigValue<Integer> maxX;
        public final ForgeConfigSpec.ConfigValue<Integer> maxY;
        public final ForgeConfigSpec.ConfigValue<Integer> maxZ;

        public Range(ForgeConfigSpec.Builder builder, RangeSpec rangeSpec) {
            builder.push("range");
            this.minX = builder.worldRestart().define("minX", rangeSpec.minX);
            this.minY = builder.worldRestart().define("minY", rangeSpec.minY);
            this.minZ = builder.worldRestart().define("minZ", rangeSpec.minZ);
            this.maxX = builder.worldRestart().define("maxX", rangeSpec.maxX);
            this.maxY = builder.worldRestart().define("maxY", rangeSpec.maxY);
            this.maxZ = builder.worldRestart().define("maxZ", rangeSpec.maxZ);
            builder.pop();
        }

        public AxisAlignedBB getAABB() {
             return new AxisAlignedBB(this.minX.get(), this.minY.get(), this.minZ.get(), this.maxX.get(), this.maxY.get(), this.maxZ.get());
        }
    }
}
