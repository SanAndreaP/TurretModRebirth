/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.sanlib.lib.util.LangUtils.TranslateKey;
import de.sanandrew.mods.turretmod.api.TmrConstants;

public final class Lang
{
    public static final TranslateKey TCU_BTN                     = new TranslateKey("gui.%s.tcu.button.%%s", TmrConstants.ID);
    public static final TranslateKey TCU_PAGE_TITLE              = new TranslateKey("gui.%s.tcu.page.title.%s");
    public static final TranslateKey TCU_DISMANTLE_ERROR         = new TranslateKey("gui.%s.tcu.page.info.dismantle.error", TmrConstants.ID);
    public static final TranslateKey TCU_SMARTTGT_GBOX           = new TranslateKey("gui.%s.tcu.page.smarttgt.%%s", TmrConstants.ID);
    public static final TranslateKey TCU_COLORIZER_CLRCODE       = new TranslateKey("gui.%s.tcu.page.colorizer.colorcode", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_HEALTH            = new TranslateKey("gui.%s.tcu.label.health", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_AMMO              = new TranslateKey("gui.%s.tcu.label.ammo", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_TARGET            = new TranslateKey("gui.%s.tcu.label.target", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_PRSSHIELD         = new TranslateKey("gui.%s.tcu.label.shield_pers", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_PRSSHIELD_RECV    = new TranslateKey("gui.%s.tcu.label.shield_pers_recv", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_TURRETSHIELD      = new TranslateKey("gui.%s.tcu.label.shield_turret", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_TURRETSHIELD_RECV = new TranslateKey("gui.%s.tcu.label.shield_turret_recv", TmrConstants.ID);

    public static final TranslateKey LEXICON_TURRET_ITEM     = new TranslateKey("sanlib.lexicon.%s.turret.%%s", TmrConstants.ID);
    public static final TranslateKey LEXICON_STAT_ITEM       = new TranslateKey("sanlib.lexicon.%s.stat.%%s", TmrConstants.ID);
    public static final TranslateKey LEXICON_AMMO_ITEM       = new TranslateKey("sanlib.lexicon.%s.ammo.%%s", TmrConstants.ID);
    public static final TranslateKey LEXICON_UPGRADE_ITEM    = new TranslateKey("sanlib.lexicon.%s.upgrade.%%s", TmrConstants.ID);
    public static final TranslateKey LEXICON_INFO_ITEM       = new TranslateKey("sanlib.lexicon.%s.info.%%s", TmrConstants.ID);
    public static final TranslateKey LEXICON_ASSEMBLY_RECIPE = new TranslateKey("sanlib.lexicon.%s.assembly", TmrConstants.ID);
    public static final TranslateKey LEXICON_DETAILS         = new TranslateKey("sanlib.lexicon.%s.details", TmrConstants.ID);
    public static final TranslateKey LEXICON_DESCRIPTION     = new TranslateKey("sanlib.lexicon.%s.desc", TmrConstants.ID);

    public static final TranslateKey TURRET_DESC = new TranslateKey("%s.turret.%%s.desc", TmrConstants.ID);

    public static final TranslateKey TASSEMBLY_BTN_CANCEL      = new TranslateKey("gui.%s.tassembly.cancel", TmrConstants.ID);
    public static final TranslateKey TASSEMBLY_BTN_AUTOENABLE  = new TranslateKey("gui.%s.tassembly.automate.enable", TmrConstants.ID);
    public static final TranslateKey TASSEMBLY_BTN_AUTODISABLE = new TranslateKey("gui.%s.tassembly.automate.disable", TmrConstants.ID);
    public static final TranslateKey TASSEMBLY_CRAFTING        = new TranslateKey("gui.%s.tassembly.crafting", TmrConstants.ID);
    public static final TranslateKey TASSEMBLY_RF_USING        = new TranslateKey("gui.%s.tassembly.rfUsing", TmrConstants.ID);

    public static final TranslateKey ELECTROGEN_EFFECTIVE = new TranslateKey("gui.%s.electrogen.effective", TmrConstants.ID);
    public static final TranslateKey ELECTROGEN_POWERGEN  = new TranslateKey("gui.%s.electrogen.powergen", TmrConstants.ID);

    public static final TranslateKey ENTITY_NAME   = new TranslateKey("entity.%s.name");
    public static final TranslateKey ENTITY_DESC   = new TranslateKey("entity.%s.desc");
    public static final TranslateKey CONTAINER_INV = new TranslateKey("container.inventory");

    public static final TranslateKey ITEM_UPGRADE_NAME = new TranslateKey("item.%s:turret_upgrade.%%s.name", TmrConstants.ID);
    public static final TranslateKey ITEM_UPGRADE_DESC = new TranslateKey("item.%s:turret_upgrade.%%s.desc", TmrConstants.ID);

    public static final TranslateKey JEI_ASSEMBLY_TITLE  = new TranslateKey("jei.%s.assembly.title", TmrConstants.ID);
    public static final TranslateKey JEI_ASSEMBLY_ENERGY = new TranslateKey("jei.%s.assembly.energy", TmrConstants.ID);
    public static final TranslateKey JEI_ASSEMBLY_TIME   = new TranslateKey("jei.%s.assembly.time", TmrConstants.ID);
}
