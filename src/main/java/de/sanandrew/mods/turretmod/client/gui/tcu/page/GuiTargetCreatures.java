/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.control.GuiButtonIcon;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

@SideOnly(Side.CLIENT)
public class GuiTargetCreatures
        extends GuiTargets<Class<? extends Entity>>
{
    private GuiButton selectMobs;
    private GuiButton selectAnimals;
    private GuiButton selectOther;

    @Override
    public void initGui(IGuiTcuInst<?> gui) {
        super.initGui(gui);

        int x = gui.getPosX() + gui.getWidth();
        int y = gui.getPosY() + 190;
        this.selectMobs = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), x - 63, y, 202, 36, Resources.GUI_TCU_TARGETS.getResource(),
                                                             Lang.translate(Lang.TCU_BTN.get("select_mobs"))));
        this.selectAnimals = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), x - 44, y, 220, 36, Resources.GUI_TCU_TARGETS.getResource(),
                                                                Lang.translate(Lang.TCU_BTN.get("select_animals"))));
        this.selectOther = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), x - 25, y, 238, 36, Resources.GUI_TCU_TARGETS.getResource(),
                                                              Lang.translate(Lang.TCU_BTN.get("select_other"))));
    }

    @Override
    public void onButtonClick(IGuiTcuInst<?> gui, GuiButton button) throws IOException {
        super.onButtonClick(gui, button);
        if( button == this.selectMobs ) {
            this.tempTargets.forEach((key, val) -> {
                if( !val && IMob.class.isAssignableFrom(key) ) {
                    this.updateEntry(gui.getTurretInst(), key, true);
                }
            });
            this.updateTargets(gui.getTurretInst());
        } else if( button == this.selectAnimals ) {
            this.tempTargets.forEach((key, val) -> {
                if( !val && IAnimals.class.isAssignableFrom(key) && !IMob.class.isAssignableFrom(key) ) {
                    this.updateEntry(gui.getTurretInst(), key, true);
                }
            });
            this.updateTargets(gui.getTurretInst());
        } else if( button == this.selectOther ) {
            this.tempTargets.forEach((key, val) -> {
                if( !val && !IAnimals.class.isAssignableFrom(key) && !IMob.class.isAssignableFrom(key) ) {
                    this.updateEntry(gui.getTurretInst(), key, true);
                }
            });
            this.updateTargets(gui.getTurretInst());
        }
    }

    @Override
    protected SortedMap<Class<? extends Entity>, Boolean> getTargetList(ITurretInst turretInst) {
        TreeMap<Class<? extends Entity>, Boolean> btwSortMapCl = new TreeMap<>(new TargetComparator());
        btwSortMapCl.putAll(turretInst.getTargetProcessor().getEntityTargets());
        return btwSortMapCl;
    }

    @Override
    protected void updateEntry(ITurretInst turretInst, Class<? extends Entity> type, boolean active) {
        turretInst.getTargetProcessor().updateEntityTarget(type, active);
    }

    @Override
    protected boolean isEntryVisible(Class<? extends Entity> type, String srcText) {
        return Lang.translateEntityCls(type).toUpperCase().contains(srcText.toUpperCase());
    }

    @Override
    protected void drawEntry(IGuiTcuInst<?> gui, Class<? extends Entity> type, int posX, int posY) {
        int textColor = 0xFF000000;
        if( IMob.class.isAssignableFrom(type) ) {
            textColor = 0xFFA00000;
        } else if( IAnimals.class.isAssignableFrom(type) ) {
            textColor = 0xFF00A000;
        }

        gui.getFontRenderer().drawString(Lang.translateEntityCls(type), posX, posY, textColor, false);
    }

    @Override
    protected boolean isBlacklist(ITurretInst turretInst) {
        return turretInst.getTargetProcessor().isEntityBlacklist();
    }

    @Override
    protected void setBlacklist(ITurretInst turretInst, boolean isBlacklist) {
        turretInst.getTargetProcessor().setEntityBlacklist(isBlacklist);
    }

    private static final class TargetComparator
            implements Comparator<Class<? extends Entity>>
    {
        @Override
        public int compare(Class<? extends Entity> o1, Class<? extends Entity> o2) {
            if( IMob.class.isAssignableFrom(o1) ) {
                if( IMob.class.isAssignableFrom(o2) ) {
                    return Lang.translateEntityCls(o1).compareTo(Lang.translateEntityCls(o2));
                } else {
                    return -1;
                }
            } else if( IAnimals.class.isAssignableFrom(o1) ) {
                if( IMob.class.isAssignableFrom(o2) ) {
                    return 1;
                } else if( IAnimals.class.isAssignableFrom(o2) ) {
                    return Lang.translateEntityCls(o1).compareTo(Lang.translateEntityCls(o2));
                } else {
                    return -1;
                }
            } else {
                if( IMob.class.isAssignableFrom(o2) || IAnimals.class.isAssignableFrom(o2) ) {
                    return 1;
                } else {
                    return Lang.translateEntityCls(o1).compareTo(Lang.translateEntityCls(o2));
                }
            }
        }
    }
}
