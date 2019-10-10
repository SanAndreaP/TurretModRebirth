package de.sanandrew.mods.turretmod.registry.upgrades;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Leveling
        implements IUpgrade
{
    private static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "upgrade.leveling");

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void initialize(ITurretInst turretInst) {
        turretInst.getUpgradeProcessor().setUpgradeInstance(ID, new LevelStorage());
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
        turretInst.getUpgradeProcessor().delUpgradeInstance(ID);
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    @IUpgradeInstance.Tickable
    public static class LevelStorage
            implements IUpgradeInstance<LevelStorage>
    {
        private static final int MAX_XP = 5_345; // level 50
        private int prevXp;
        private int xp;
        private int cachedLevel;

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
            if( this.prevXp != this.xp ) {
                UpgradeRegistry.INSTANCE.syncWithClients(turretInst, Leveling.ID);

                this.prevXp = this.xp;
                this.cachedLevel = -1;
            }
        }

        public int getXp() {
            return this.xp;
        }

        public void addXp(int xp) {
            if( xp > 0 ) {
                this.xp += xp;
                if( this.xp > MAX_XP ) {
                    this.xp = MAX_XP;
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
            return getXpReqForNextLevel(this.getLevel());
        }

        public int getCurrentLevelMinXp() {
            return getXpReqForNextLevel(this.getLevel() - 1);
        }

        private static int getXpReqForNextLevel(int lvl) {
            if ( lvl < 0 ) {
                return 0;
            } else if( lvl < 16 ) {
                return 2 * lvl + 7;
            } else if( lvl < 31 ) {
                return 5 * lvl - 38;
            } else {
                return 9 * lvl - 158;
            }
        }
    }
}
