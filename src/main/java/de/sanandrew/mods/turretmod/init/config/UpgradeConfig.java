package de.sanandrew.mods.turretmod.init.config;

import de.sanandrew.mods.turretmod.item.upgrades.leveling.LevelStorage;
import net.minecraftforge.common.ForgeConfigSpec;

public class UpgradeConfig
{
    public final LevelingSpec leveling;

    private UpgradeConfig(ForgeConfigSpec.Builder builder) {
        this.leveling = new LevelingSpec(builder.push("leveling"));

        builder.pop();
    }

    public void load() {
        this.leveling.load();
    }

    public static UpgradeConfig build(ForgeConfigSpec.Builder builder) {
        return new UpgradeConfig(builder.push("upgrades"));
    }

    public static class LevelingSpec
    {
        public final ForgeConfigSpec.ConfigValue<Integer> maxXp;

        private LevelingSpec(ForgeConfigSpec.Builder builder) {
            this.maxXp = builder.worldRestart().define("maxExperience", 5_345);

            builder.pop();
        }

        @SuppressWarnings("java:S2696")
        void load() {
            LevelStorage.maxXp = this.maxXp.get();
        }
    }
}
