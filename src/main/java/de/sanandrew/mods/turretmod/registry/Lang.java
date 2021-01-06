/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.registry;

import de.sanandrew.mods.sanlib.lib.util.LangUtils.TranslateKey;
import de.sanandrew.mods.turretmod.api.TmrConstants;

public final class Lang
{
    public static final TranslateKey TCU_PAGE_TITLE              = new TranslateKey("gui.%s.tcu.page.%%s.title", TmrConstants.ID);
    public static final TranslateKey TCU_PAGE_ELEMENT            = new TranslateKey("gui.%s.tcu.page.%%s.%%s", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_HEALTH            = new TranslateKey("gui.%s.tcu.label.health", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_AMMO              = new TranslateKey("gui.%s.tcu.label.ammo", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_TARGET            = new TranslateKey("gui.%s.tcu.label.target", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_PRSSHIELD         = new TranslateKey("gui.%s.tcu.label.shield_pers", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_PRSSHIELD_RECV    = new TranslateKey("gui.%s.tcu.label.shield_pers_recv", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_TURRETSHIELD      = new TranslateKey("gui.%s.tcu.label.shield_turret", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_TURRETSHIELD_RECV = new TranslateKey("gui.%s.tcu.label.shield_turret_recv", TmrConstants.ID);

    public static final TranslateKey ITEM_TURRET_PLACER = new TranslateKey("item.%s:turret.%%s", TmrConstants.ID);
    public static final TranslateKey ITEM_UPGRADE = new TranslateKey("item.%s:upgrade.%%s", TmrConstants.ID);

    public static final TranslateKey JEI_ASSEMBLY_TITLE  = new TranslateKey("jei.%s.assembly.title", TmrConstants.ID);
    public static final TranslateKey JEI_ASSEMBLY_ENERGY = new TranslateKey("jei.%s.assembly.energy", TmrConstants.ID);
    public static final TranslateKey JEI_ASSEMBLY_TIME   = new TranslateKey("jei.%s.assembly.time", TmrConstants.ID);
}
