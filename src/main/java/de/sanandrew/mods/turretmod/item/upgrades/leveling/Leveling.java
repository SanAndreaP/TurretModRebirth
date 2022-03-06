package de.sanandrew.mods.turretmod.item.upgrades.leveling;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.client.tcu.TcuTabEvent;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgrade;
import de.sanandrew.mods.turretmod.api.upgrade.IUpgradeData;
import de.sanandrew.mods.turretmod.item.TurretControlUnit;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nonnull;

@Mod.EventBusSubscriber(modid = TmrConstants.ID, value = Dist.CLIENT)
public class Leveling
        implements IUpgrade
{
    static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "leveling_upgrade");

    @Nonnull
    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public IUpgradeData<?> getData(ITurretEntity turretInst) {
        return new LevelStorage();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void onTabIconShow(TcuTabEvent.TabIconShow event) {
        if( event.tabId.equals(TurretControlUnit.LEVELS) && !event.turret.getUpgradeProcessor().hasUpgrade(ID) ) {
            event.setCanceled(true);
        }
    }
}