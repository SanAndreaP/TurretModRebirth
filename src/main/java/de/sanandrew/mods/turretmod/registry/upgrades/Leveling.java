package de.sanandrew.mods.turretmod.registry.upgrades;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IUpgradeProcessor;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Leveling
        implements IUpgrade
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "upgrade.leveling");

    private static final String NBT_ITEM_LEVELS = "Levels";
    private static final String NBT_EXPERIENCE = "Experience";

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void initialize(ITurretInst turretInst, ItemStack stack) {
        IUpgradeProcessor processor = turretInst.getUpgradeProcessor();
        if( processor.getUpgradeInstance(ID) == null ) {
            if( turretInst.get().isServerWorld() ) {
                NBTTagCompound lvlNbt = stack.getSubCompound(NBT_ITEM_LEVELS);
                LevelStorage stg = lvlNbt != null && lvlNbt.hasKey(NBT_EXPERIENCE)
                                   ? new LevelStorage(lvlNbt.getInteger(NBT_EXPERIENCE))
                                   : new LevelStorage();
                processor.setUpgradeInstance(ID, stg);
                UpgradeRegistry.INSTANCE.syncWithClients(turretInst, ID);
            } else {
                processor.setUpgradeInstance(ID, new LevelStorage());
            }
        }
    }

    @Override
    public void onLoad(ITurretInst turretInst, NBTTagCompound nbt) {
        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, new LevelStorage(nbt.getInteger("Experience")));
    }

    @Override
    public void onSave(ITurretInst turretInst, NBTTagCompound nbt) {
        nbt.setInteger("Experience", turretInst.getUpgradeProcessor().<LevelStorage>getUpgradeInstance(ID).getXp());
    }

    @Override
    public void terminate(ITurretInst turretInst, ItemStack stack) {
        LevelStorage stg = turretInst.getUpgradeProcessor().getUpgradeInstance(ID);
        if( stg != null ) {
            NBTTagCompound lvlNbt = stack.getOrCreateSubCompound(NBT_ITEM_LEVELS);
            lvlNbt.setInteger(NBT_EXPERIENCE, stg.xp);

            turretInst.getUpgradeProcessor().delUpgradeInstance(ID);
        }
    }

    @SuppressWarnings("unused")
    @IUpgradeInstance.Tickable
    @Category("Leveling")
    public static class LevelStorage
            implements IUpgradeInstance<LevelStorage>
    {
        @Value(comment = "The maximum XP a turret can gain through the Leveling upgrade. The default is 50 levels worth; see https://minecraft.gamepedia.com/Experience#Leveling_up")
        public static int maxXp = 5_345; // level 50
        @Value()
        public static String stagesJson = "";
        public static Stages[] stages;

        private int prevXp;
        private int xp;
        private int cachedLevel;
        private Stages currStage;

        LevelStorage() {
            this.xp = 0;
            this.prevXp = 0;
            this.cachedLevel = -1;
        }

        LevelStorage(int xp) {
            this.xp = xp;
            this.prevXp = 0;
            this.cachedLevel = -1;
        }

        @Override
        public void fromBytes(ObjectInputStream stream) throws IOException {
            this.xp = stream.readInt();
            this.prevXp = 0;
            this.cachedLevel = -1;
        }

        @Override
        public void toBytes(ObjectOutputStream stream) throws IOException {
            stream.writeInt(this.xp);
        }

        @Override
        public void onTick(ITurretInst turretInst) {
            if( turretInst.get().isServerWorld() && this.prevXp != this.xp ) {
                UpgradeRegistry.INSTANCE.syncWithClients(turretInst, Leveling.ID);

                this.prevXp = this.xp;
                this.cachedLevel = -1;
            }
        }

        private void applyEffects() {
        }

        public int getXp() {
            return this.xp;
        }

        public void addXp(int xp) {
            if( xp > 0 ) {
                this.xp += xp;
                if( this.xp > maxXp) {
                    this.xp = maxXp;
                }
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

Notes: - to correctly get the level and not underlevel, 0.̅2 needs to be written as a simple fraction in code -> (10.0D / 45.0D)
       - 18.0̅5 can be rounded to 3 decimal places -> 18.056D
       - The resulting decimal is floored, providing an integer

*/
        public int getLevel() {
            if( this.cachedLevel > -1 ) {
                return this.cachedLevel;
            }

            if( this.xp <= 352 ) { // below lvl 16
                return MathHelper.floor(Math.sqrt(this.xp + 9.0D) - 3.0D);
            } else if( this.xp <= 1507 ) { // below lvl 31
                return MathHelper.floor(Math.sqrt(0.4D * this.xp - 78.39D) + 8.1D);
            } else { // at or above lvl 31
                return MathHelper.floor(Math.sqrt((10.0D / 45.0D) * this.xp - 167.33D) + 18.056D);
            }
        }

        public int getNextLevelMinXp() {
            return getXpReqForNextLevel(this.getLevel() + 1);
        }

        public int getCurrentLevelMinXp() {
            return getXpReqForNextLevel(this.getLevel());
        }

        private static int getXpReqForNextLevel(int lvl) {
            if ( lvl < 0 ) {
                return 0;
            } else if( lvl < 17 ) {
                return lvl * lvl + 6 * lvl;
            } else if( lvl < 32 ) {
                return MathHelper.floor(2.5D * lvl * lvl - 40.5D * lvl + 360);
            } else {
                return MathHelper.floor(4.5D * lvl * lvl - 162.5D * lvl + 2220);
            }
        }

        private Stages[] loadFromJson() {
            JsonArray jArray = JsonUtils.GSON.fromJson(stagesJson, JsonArray.class);
            List<Stages> stages = new ArrayList<>();
            jArray.forEach(e -> {
                JsonObject stage = e.getAsJsonObject();
                int lvl = stage.get("level").getAsInt();
                List<AttributeModifier> modifiers = new ArrayList<>();
                stage.get("modifiers").getAsJsonArray().forEach(m -> {
                    JsonObject mod = m.getAsJsonObject();
                    modifiers.add(new AttributeModifier(UUID.fromString(mod.get("id").getAsString()),
                                                        mod.get("name").getAsString(),
                                                        mod.get("amount").getAsDouble(),
                                                        mod.get("mode").getAsInt()));
                });

                stages.add(new Stages(lvl, modifiers.toArray(new AttributeModifier[0])));
            });

            return stages.toArray(new Stages[0]);
        }

        static class Stages
        {
            public final int level;

            Stages(int level, AttributeModifier... modifiers) {
                this.level = level;
            }

            public boolean check(int level, Stages currStage) {
                return level >= this.level && currStage.level < this.level;
            }
        }
    }
}
