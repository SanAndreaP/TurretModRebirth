/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.control.GuiSlimButton;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;

import java.io.IOException;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

public class GuiTargetCreatures
        extends GuiTargets<Class<? extends Entity>>
{
    private GuiButton selectMobs;
    private GuiButton selectAnimals;
    private GuiButton selectOther;

    @Override
    public void initGui(IGuiTcuInst<?> gui) {
        super.initGui(gui);

        int center = gui.getPosX() + (gui.getGuiWidth() - 150) / 2;
        this.selectMobs = gui.addNewButton(new GuiSlimButton(gui.getNewButtonId(), center, gui.getPosY() + 164, 150, Lang.translate(Lang.TCU_TARGET_BTN.get("selectMobs"))));
        this.selectAnimals = gui.addNewButton(new GuiSlimButton(gui.getNewButtonId(), center, gui.getPosY() + 177, 150, Lang.translate(Lang.TCU_TARGET_BTN.get("selectAnimals"))));
        this.selectOther = gui.addNewButton(new GuiSlimButton(gui.getNewButtonId(), center, gui.getPosY() + 190, 150, Lang.translate(Lang.TCU_TARGET_BTN.get("selectOther"))));
    }

    @Override
    public void onButtonClick(IGuiTcuInst<?> gui, GuiButton button) throws IOException {
        super.onButtonClick(gui, button);
        if( button == this.selectMobs ) {
            this.tempTargetList.forEach((key, val) -> {
                if( !val && IMob.class.isAssignableFrom(key) ) {
                    this.updateEntry(gui.getTurretInst(), key, true);
                }
            });
            this.updateTargets(gui.getTurretInst());
        } else if( button == this.selectAnimals ) {
            this.tempTargetList.forEach((key, val) -> {
                if( !val && IAnimals.class.isAssignableFrom(key) && !IMob.class.isAssignableFrom(key) ) {
                    this.updateEntry(gui.getTurretInst(), key, true);
                }
            });
            this.updateTargets(gui.getTurretInst());
        } else if( button == this.selectOther ) {
            this.tempTargetList.forEach((key, val) -> {
                if( !val && !IAnimals.class.isAssignableFrom(key) && !IMob.class.isAssignableFrom(key) ) {
                    this.updateEntry(gui.getTurretInst(), key, true);
                }
            });
            this.updateTargets(gui.getTurretInst());
        }
    }

    @Override
    protected Map<Class<? extends Entity>, Boolean> getTargetList(ITurretInst turretInst) {
        TreeMap<Class<? extends Entity>, Boolean> btwSortMapNm = new TreeMap<>(new TargetComparatorName());
        TreeMap<Class<? extends Entity>, Boolean> btwSortMapCl = new TreeMap<>(new TargetComparatorClass());
        btwSortMapNm.putAll(turretInst.getTargetProcessor().getEntityTargets());
        btwSortMapCl.putAll(btwSortMapNm);
        return btwSortMapCl;
    }

    @Override
    protected void updateEntry(ITurretInst turretInst, Class<? extends Entity> type, boolean active) {
        turretInst.getTargetProcessor().updateEntityTarget(type, active);
    }

    @Override
    protected void drawEntry(IGuiTcuInst<?> gui, Class<? extends Entity> type, int posX, int posY) {
        int textColor = 0xFFFFFFFF;
        if( IMob.class.isAssignableFrom(type) ) {
            textColor = 0xFFFFAAAA;
        } else if( IAnimals.class.isAssignableFrom(type) ) {
            textColor = 0xFFAAFFAA;
        }

        gui.getFontRenderer().drawString(Lang.translateEntityCls(type), posX, posY, textColor, false);
    }

    private static final class TargetComparatorClass
            implements Comparator<Class<? extends Entity>>
    {
        @Override
        public int compare(Class<? extends Entity> o1, Class<? extends Entity> o2) {
            if( IMob.class.isAssignableFrom(o1) && IAnimals.class.isAssignableFrom(o2) ) {
                return -1;
            }
            if( IAnimals.class.isAssignableFrom(o1) && !IMob.class.isAssignableFrom(o2) && !IAnimals.class.isAssignableFrom(o2) ) {
                return -1;
            }
            if( o1.equals(o2) ) {
                return 0;
            }
            return 1;
        }
    }

    private static final class TargetComparatorName
            implements Comparator<Class<? extends Entity>>
    {
        @Override
        public int compare(Class<? extends Entity> o1, Class<? extends Entity> o2) {
            return Lang.translateEntityCls(o2).compareTo(Lang.translateEntityCls(o1));
        }
    }
}
