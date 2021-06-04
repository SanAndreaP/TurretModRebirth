/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.init;

import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.sanlib.lib.util.LangUtils.TranslateKey;
import de.sanandrew.mods.turretmod.api.TmrConstants;

public final class Lang
{
    public static final TranslateKey TCU_PAGE_TITLE              = LangUtils.newKey("gui.%s.tcu.page.%%s.title").format(TmrConstants.ID).build();
    public static final TranslateKey TCU_PAGE_ELEMENT            = LangUtils.newKey("gui.%s.tcu.page.%%s.%%s").format(TmrConstants.ID).build();
    public static final TranslateKey TCU_LABEL_HEALTH            = LangUtils.newKey("gui.%s.tcu.label.health").format(TmrConstants.ID).build();
    public static final TranslateKey TCU_LABEL_AMMO              = LangUtils.newKey("gui.%s.tcu.label.ammo").format(TmrConstants.ID).build();
    public static final TranslateKey TCU_LABEL_TARGET            = LangUtils.newKey("gui.%s.tcu.label.target").format(TmrConstants.ID).build();
    public static final TranslateKey TCU_LABEL_PRSSHIELD         = LangUtils.newKey("gui.%s.tcu.label.shield_pers").format(TmrConstants.ID).build();
    public static final TranslateKey TCU_LABEL_PRSSHIELD_RECV    = LangUtils.newKey("gui.%s.tcu.label.shield_pers_recv").format(TmrConstants.ID).build();
    public static final TranslateKey TCU_LABEL_TURRETSHIELD      = LangUtils.newKey("gui.%s.tcu.label.shield_turret").format(TmrConstants.ID).build();
    public static final TranslateKey TCU_LABEL_TURRETSHIELD_RECV = LangUtils.newKey("gui.%s.tcu.label.shield_turret_recv").format(TmrConstants.ID).build();

    public static final TranslateKey ITEM_TURRET_PLACER = LangUtils.newKey("item.%s.turret.%%s").format(TmrConstants.ID).build();
    public static final TranslateKey ITEM_UPGRADE = LangUtils.newKey("item.%s.upgrade.%%s").format(TmrConstants.ID).build();

    public static final TranslateKey JEI_ASSEMBLY_TITLE  = LangUtils.newKey("jei.%s.assembly.title").format(TmrConstants.ID).build();
    public static final TranslateKey JEI_ASSEMBLY_ENERGY = LangUtils.newKey("jei.%s.assembly.energy").format(TmrConstants.ID).build();
    public static final TranslateKey JEI_ASSEMBLY_TIME   = LangUtils.newKey("jei.%s.assembly.time").format(TmrConstants.ID).build();
}
