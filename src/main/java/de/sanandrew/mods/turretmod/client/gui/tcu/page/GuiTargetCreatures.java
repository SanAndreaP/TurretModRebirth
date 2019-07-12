/* ******************************************************************************************************************
   * Authors:   SanAndreasP
   * Copyright: SanAndreasP
   * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
   *                http://creativecommons.org/licenses/by-nc-sa/4.0/
   *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.tcu.page;

import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.client.tcu.IGuiTcuInst;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.client.gui.control.GuiButtonIcon;
import de.sanandrew.mods.turretmod.entity.turret.TargetList;
import de.sanandrew.mods.turretmod.util.Lang;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.Comparator;
import java.util.SortedMap;
import java.util.TreeMap;

public class GuiTargetCreatures
        extends GuiTargets<ResourceLocation>
{
    private GuiButton selectMobs;
    private GuiButton selectAnimals;
    private GuiButton selectOther;

    @Override
    public void initialize(IGuiTcuInst<?> gui) {
        super.initialize(gui);

        int x = gui.getPosX() + gui.getWidth();
        int y = gui.getPosY() + 190;
        this.selectMobs = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), x - 63, y, 202, 36, Resources.GUI_TCU_TARGETS.resource,
                                                             LangUtils.translate(Lang.TCU_BTN.get("select_mobs"))));
        this.selectAnimals = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), x - 44, y, 220, 36, Resources.GUI_TCU_TARGETS.resource,
                                                                LangUtils.translate(Lang.TCU_BTN.get("select_animals"))));
        this.selectOther = gui.addNewButton(new GuiButtonIcon(gui.getNewButtonId(), x - 25, y, 238, 36, Resources.GUI_TCU_TARGETS.resource,
                                                              LangUtils.translate(Lang.TCU_BTN.get("select_other"))));
    }

    @Override
    public void onButtonClick(IGuiTcuInst<?> gui, GuiButton button) throws IOException {
        super.onButtonClick(gui, button);
        if( button == this.selectMobs ) {
            this.tempTargets.forEach((key, val) -> {
                Class<?> c = MiscUtils.defIfNull(EntityList.getClass(key), Object.class);
                if( !val && IMob.class.isAssignableFrom(c) ) {
                    this.updateEntry(gui.getTurretInst(), key, true);
                }
            });
            this.updateTargets(gui.getTurretInst());
        } else if( button == this.selectAnimals ) {
            this.tempTargets.forEach((key, val) -> {
                Class<?> c = MiscUtils.defIfNull(EntityList.getClass(key), Object.class);
                if( !val && IAnimals.class.isAssignableFrom(c) && !IMob.class.isAssignableFrom(c) ) {
                    this.updateEntry(gui.getTurretInst(), key, true);
                }
            });
            this.updateTargets(gui.getTurretInst());
        } else if( button == this.selectOther ) {
            this.tempTargets.forEach((key, val) -> {
                Class<?> c = MiscUtils.defIfNull(EntityList.getClass(key), Object.class);
                if( !val && !IAnimals.class.isAssignableFrom(c) && !IMob.class.isAssignableFrom(c) ) {
                    this.updateEntry(gui.getTurretInst(), key, true);
                }
            });
            this.updateTargets(gui.getTurretInst());
        }
    }

    @Override
    protected SortedMap<ResourceLocation, Boolean> getTargetList(ITurretInst turretInst) {
        TreeMap<ResourceLocation, Boolean> btwSortMapCl = new TreeMap<>(new TargetComparator());
        btwSortMapCl.putAll(TargetList.getStandardTargetList(turretInst.getAttackType()));
        btwSortMapCl.putAll(turretInst.getTargetProcessor().getEntityTargets());
        return btwSortMapCl;
    }

    @Override
    protected void updateEntry(ITurretInst turretInst, ResourceLocation type, boolean active) {
        turretInst.getTargetProcessor().updateEntityTarget(type, active);
    }

    @Override
    protected boolean isEntryVisible(ResourceLocation type, String srcText) {
        return LangUtils.translateEntityCls(MiscUtils.defIfNull(EntityList.getClass(type), Entity.class)).toUpperCase().contains(srcText.toUpperCase());
    }

    @Override
    protected void drawEntry(IGuiTcuInst<?> gui, ResourceLocation type, int posX, int posY) {
        int textColor = 0xFF000000;
        Class<? extends Entity> c = MiscUtils.defIfNull(EntityList.getClass(type), Entity.class);
        if( IMob.class.isAssignableFrom(c) ) {
            textColor = 0xFFA00000;
        } else if( IAnimals.class.isAssignableFrom(c) ) {
            textColor = 0xFF00A000;
        }

        gui.getFontRenderer().drawString(LangUtils.translateEntityCls(c), posX, posY, textColor, false);
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
            implements Comparator<ResourceLocation>
    {
        @Override
        public int compare(ResourceLocation o1, ResourceLocation o2) {
            Class<? extends Entity> c1 = MiscUtils.defIfNull(EntityList.getClass(o1), Entity.class);
            Class<? extends Entity> c2 = MiscUtils.defIfNull(EntityList.getClass(o2), Entity.class);
            if( IMob.class.isAssignableFrom(c1) ) {
                if( IMob.class.isAssignableFrom(c2) ) {
                    return LangUtils.translateEntityCls(c1).compareTo(LangUtils.translateEntityCls(c2));
                } else {
                    return -1;
                }
            } else if( IAnimals.class.isAssignableFrom(c1) ) {
                if( IMob.class.isAssignableFrom(c2) ) {
                    return 1;
                } else if( IAnimals.class.isAssignableFrom(c2) ) {
                    return LangUtils.translateEntityCls(c1).compareTo(LangUtils.translateEntityCls(c2));
                } else {
                    return -1;
                }
            } else {
                if( IMob.class.isAssignableFrom(c2) || IAnimals.class.isAssignableFrom(c2) ) {
                    return 1;
                } else {
                    return LangUtils.translateEntityCls(c1).compareTo(LangUtils.translateEntityCls(c2));
                }
            }
        }
    }
}
