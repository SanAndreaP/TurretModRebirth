package de.sanandrew.mods.turretmod.registry.upgrades.leveling;

import de.sanandrew.mods.sanlib.lib.util.config.Category;
import de.sanandrew.mods.sanlib.lib.util.config.Init;
import de.sanandrew.mods.sanlib.lib.util.config.Value;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeInstance;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.TmrUtils;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.Loader;
import org.apache.logging.log4j.Level;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@SuppressWarnings("FieldMayBeFinal")
@IUpgradeInstance.Tickable
@Category("Leveling")
public class LevelStorage
        implements IUpgradeInstance<LevelStorage>
{
    @Value(comment = "The maximum XP a turret can gain through the Leveling upgrade. The default is 50 levels worth; see https://minecraft.gamepedia.com/Experience#Leveling_up")
    public static  int      maxXp      = 5_345; // level 50
    @Value(value = "stages", comment = "A JSON array defining the level stages which are applied once a turret levels up to a stage level.", reqWorldRestart = true)
    private static String[] stagesJson = getDefaultStages();
    private static Stage[]  stages     = {};

    int xp;

    private int     prevXp;
    private int     prevLvl;
    private int     cachedLevel;
    private Stage   currStage   = Stage.NULL_STAGE;
    private boolean initialized = false;

    LevelStorage() {
        this.xp = 0;
        this.prevXp = 0;
        this.prevLvl = 0;
        this.cachedLevel = -1;
    }

    LevelStorage(int xp) {
        this.xp = xp;
        this.prevXp = 0;
        this.prevLvl = 0;
        this.cachedLevel = -1;
    }

    @Init
    public static void initStages() {
        stages = Stage.load(String.join("\n", stagesJson));
    }

    private static int getXpReqForNextLevel(int lvl) {
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

    static String[] getDefaultStages() {
        try( BufferedReader r = TmrUtils.getFile(Loader.instance().getModList().stream()
                                                       .filter(c -> c.getModId().equals(TmrConstants.ID))
                                                       .findFirst().orElseThrow(IOException::new),
                                                 "assets/" + TmrConstants.ID + "/stages_default.json") )
        {
            if( r != null ) {
                return r.lines().toArray(String[]::new);
            }
        } catch( IOException e ) {
            TmrConstants.LOG.log(Level.ERROR, "Cannot load default stages: {}", e.getMessage());
        }

        return new String[0];
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
        EntityLivingBase e = turretInst.get();
        if( e.isServerWorld() && this.prevXp != this.xp ) {
            UpgradeRegistry.INSTANCE.syncWithClients(turretInst, Leveling.ID);

            this.prevXp = this.xp;
            this.cachedLevel = -1;

            applyEffects(turretInst, this.initialized);
        }

        if( !this.initialized ) {
            this.initialized = true;

            // cleanup dangling modifiers
            List<UUID> currModifierIds = Arrays.stream(stages).map(s -> Arrays.stream(s.modifiers).map(m -> m.modifier.getID()).collect(Collectors.toList()))
                                               .reduce(new ArrayList<>(), (result, element) -> {
                                                   result.addAll(element);
                                                   return result;
                                               });
            e.getAttributeMap().getAllAttributes().forEach(a -> a.getModifiers().forEach(m -> {
                                                               if( m.getName().startsWith(Stage.NAME_PREFIX) && !currModifierIds.contains(m.getID()) ) {
                                                                   a.removeModifier(m);
                                                               }
                                                           })
            );
        }
    }

    void applyEffects(ITurretInst turretInst, boolean playSound) {
        final int currLevel = this.getLevel();
        if( this.prevLvl != currLevel ) {
            this.prevLvl = currLevel;

            Arrays.stream(stages).forEach(s -> {
                if( s.check(currLevel, this.currStage) ) {
                    s.apply(turretInst, playSound);
                }
            });
        }
    }

    void removeEffects(ITurretInst turretInst) {
        EntityLivingBase e = turretInst.get();
        e.getAttributeMap().getAllAttributes().forEach(a -> a.getModifiers().forEach(m -> {
                                                           if( m.getName().startsWith(Stage.NAME_PREFIX) ) {
                                                               a.removeModifier(m);
                                                           }
                                                       })
        );
    }

    public Map<String, Stage.ModifierInfo> fetchCurrentModifiers(ITurretInst turretInst) {
        return Stage.fetchModifiers(this.fetchCurrentStages(), turretInst.get().getAttributeMap());
    }

    private List<Stage> fetchCurrentStages() {
        final int currLevel = this.getLevel();
        return Arrays.stream(stages).filter(s -> s.check(currLevel, Stage.NULL_STAGE)).collect(Collectors.toList());
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

    public static Stage[] getStages() {
        return stages;
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

        int xp = this.getXp();
        if( xp <= 352 ) { // below lvl 16
            return MathHelper.floor(Math.sqrt(xp + 9.0D) - 3.0D);
        } else if( xp <= 1507 ) { // below lvl 31
            return MathHelper.floor(Math.sqrt(0.4D * xp - 78.39D) + 8.1D);
        } else { // at or above lvl 31
            return MathHelper.floor(Math.sqrt((10.0D / 45.0D) * xp - 167.33D) + 18.056D);
        }
    }

    public int getNextLevelMinXp() {
        return getXpReqForNextLevel(this.getLevel() + 1);
    }

    public int getCurrentLevelMinXp() {
        return getXpReqForNextLevel(this.getLevel());
    }

}
