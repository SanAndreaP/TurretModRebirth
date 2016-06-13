/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.minecraft.util.StatCollector;

public final class Lang
{
    public static final String TCU_BTN = String.format("gui.%s.tcu.page.info.button.%%s", TurretModRebirth.ID);

    public static final String TINFO_ENTRY_INFO_TITLE = String.format("gui.%s.tinfo.infoTitle", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_AMMO_NAME = String.format("%s.tinfo.ammo.%%s.name", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_AMMO_DESC = String.format("%s.tinfo.ammo.%%s.desc", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_ROUNDS = String.format("gui.%s.tinfo.rounds", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_DPS = String.format("gui.%s.tinfo.dps", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_HEALTH = String.format("gui.%s.tinfo.health", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_HEALTHVAL = String.format("gui.%s.tinfo.healthVal", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_TURRET = String.format("gui.%s.tinfo.turret", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_CRAFTING = String.format("gui.%s.tinfo.crafting", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_WORKBENCH = String.format("gui.%s.tinfo.workbench", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_PREREQ = String.format("gui.%s.tinfo.prereq", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_RANGE = String.format("gui.%s.tinfo.range", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_RANGEVAL = String.format("gui.%s.tinfo.rangeVal", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_AMMOCAP = String.format("gui.%s.tinfo.ammocap", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_AMMOUSE = String.format("gui.%s.tinfo.ammouse", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_ROUNDSVAL = String.format("gui.%s.tinfo.roundsVal", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_INFO_NAME = String.format("gui.%s.tinfo.modName", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_INFO_VERSION = String.format("gui.%s.tinfo.modVersion", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_INFO_AUTHOR = String.format("gui.%s.tinfo.modAuthor", TurretModRebirth.ID);
    public static final String TINFO_ENTRY_INFO_CREDITS = String.format("gui.%s.tinfo.modCredits", TurretModRebirth.ID);

    public static final String TASSEMBLY_BTN_CANCEL = String.format("gui.%s.tassembly.cancel", TurretModRebirth.ID);
    public static final String TASSEMBLY_BTN_AUTOENABLE = String.format("gui.%s.tassembly.automate.enable", TurretModRebirth.ID);
    public static final String TASSEMBLY_BTN_AUTODISABLE = String.format("gui.%s.tassembly.automate.disable", TurretModRebirth.ID);

    public static final String ELECTROGEN_EFFECTIVE = String.format("gui.%s.electrogen.effective", TurretModRebirth.ID);
    public static final String ELECTROGEN_POWERGEN = String.format("gui.%s.electrogen.powergen", TurretModRebirth.ID);

    public static final String ENTITY_NAME = "entity.%s.name";
    public static final String ENTITY_DESC = "entity.%s.desc";
    public static final String CONTAINER_INV = "container.inventory";

    public static final String ITEM_UPGRADE_NAME = String.format("item.%s:turret_upgrade.%%s.name", TurretModRebirth.ID);
    public static final String ITEM_UPGRADE_DESC = String.format("item.%s:turret_upgrade.%%s.desc", TurretModRebirth.ID);

    /**
     * Wrapper method to {@link StatCollector#translateToLocal(String)} for abbreviation.
     * Also tries to translate with {@link StatCollector#translateToFallback(String)} to en_US if the translation fails
     * @param langKey language key to be translated
     * @return translated key or langKey, if translation fails
     */
    public static String translate(String langKey) {
        return StatCollector.canTranslate(langKey) ? StatCollector.translateToLocal(langKey) : StatCollector.translateToFallback(langKey);
    }

    /**
     * Formats the language key before it gets translated with {@link Lang#translate(String)}
     * @param langKey language key to be translated
     * @param args formatting arguments to be applied to the key
     * @return translated key or langKey, if translation fails
     */
    public static String translate(String langKey, Object... args) {
        return translate(String.format(langKey, args));
    }
}
