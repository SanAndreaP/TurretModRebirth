/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.text.translation.I18n;

@SuppressWarnings("deprecation")
public final class Lang
{
    public static final TranslateKey TCU_BTN = new TranslateKey("gui.%s.tcu.page.info.button.%%s", TurretModRebirth.ID);
    public static final TranslateKey TCU_TARGET_BTN = new TranslateKey("gui.%s.tcu.page.targetsEntity.button.%%s", TurretModRebirth.ID);
    public static final TranslateKey TCU_PAGE_TITLE = new TranslateKey("gui.%s.tcu.page.%%s.title", TurretModRebirth.ID);
    public static final TranslateKey TCU_PAGE_TAB = new TranslateKey("gui.%s.tcu.page.%%s.tab", TurretModRebirth.ID);

    public static final TranslateKey TINFO_CATEGORY_NAME = new TranslateKey("gui.%s.tinfo.category.%%s.name", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_INFO_TITLE = new TranslateKey("gui.%s.tinfo.infoTitle", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_AMMO_NAME = new TranslateKey("%s.tinfo.ammo.%%s.name", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_AMMO_DESC = new TranslateKey("%s.tinfo.ammo.%%s.desc", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_ROUNDS = new TranslateKey("gui.%s.tinfo.rounds", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_DPS = new TranslateKey("gui.%s.tinfo.dps", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_HEALTH = new TranslateKey("gui.%s.tinfo.health", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_HEALTHVAL = new TranslateKey("gui.%s.tinfo.healthVal", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_TURRET = new TranslateKey("gui.%s.tinfo.turret", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_CRAFTING = new TranslateKey("gui.%s.tinfo.crafting", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_WORKBENCH = new TranslateKey("gui.%s.tinfo.workbench", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_PREREQ = new TranslateKey("gui.%s.tinfo.prereq", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_RANGE = new TranslateKey("gui.%s.tinfo.range", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_RANGEVAL = new TranslateKey("gui.%s.tinfo.rangeVal", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_AMMOCAP = new TranslateKey("gui.%s.tinfo.ammocap", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_AMMOUSE = new TranslateKey("gui.%s.tinfo.ammouse", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_ROUNDSVAL = new TranslateKey("gui.%s.tinfo.roundsVal", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_EFFICIENCY = new TranslateKey("gui.%s.tinfo.efficiency", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_DECAY = new TranslateKey("gui.%s.tinfo.decay", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_INFO_NAME = new TranslateKey("gui.%s.tinfo.modName", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_INFO_VERSION = new TranslateKey("gui.%s.tinfo.modVersion", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_INFO_AUTHOR = new TranslateKey("gui.%s.tinfo.modAuthor", TurretModRebirth.ID);
    public static final TranslateKey TINFO_ENTRY_INFO_CREDITS = new TranslateKey("gui.%s.tinfo.modCredits", TurretModRebirth.ID);

    public static final TranslateKey TASSEMBLY_BTN_CANCEL = new TranslateKey("gui.%s.tassembly.cancel", TurretModRebirth.ID);
    public static final TranslateKey TASSEMBLY_BTN_AUTOENABLE = new TranslateKey("gui.%s.tassembly.automate.enable", TurretModRebirth.ID);
    public static final TranslateKey TASSEMBLY_BTN_AUTODISABLE = new TranslateKey("gui.%s.tassembly.automate.disable", TurretModRebirth.ID);
    public static final TranslateKey TASSEMBLY_CRAFTING = new TranslateKey("gui.%s.tassembly.crafting", TurretModRebirth.ID);
    public static final TranslateKey TASSEMBLY_RF_USING = new TranslateKey("gui.%s.tassembly.rfUsing", TurretModRebirth.ID);

    public static final TranslateKey ELECTROGEN_EFFECTIVE = new TranslateKey("gui.%s.electrogen.effective", TurretModRebirth.ID);
    public static final TranslateKey ELECTROGEN_POWERGEN = new TranslateKey("gui.%s.electrogen.powergen", TurretModRebirth.ID);

    public static final TranslateKey ENTITY_NAME = new TranslateKey("entity.%s.name");
    public static final TranslateKey ENTITY_DESC = new TranslateKey("entity.%s.desc");
    public static final TranslateKey CONTAINER_INV = new TranslateKey("container.inventory");

    public static final TranslateKey ITEM_UPGRADE_NAME = new TranslateKey("item.%s:turret_upgrade.%%s.name", TurretModRebirth.ID);
    public static final TranslateKey ITEM_UPGRADE_DESC = new TranslateKey("item.%s:turret_upgrade.%%s.desc", TurretModRebirth.ID);

    /**
     * Wrapper method to {@link I18n#canTranslate(String)} for abbreviation.
     * <s>Also tries to translate with [NONE] to en_US if the translation fails</s>
     * @param langKey language key to be translated
     * @return translated key or langKey, if translation fails
     */
    public static String translate(String langKey, Object... args) {
        return I18n.canTranslate(langKey) ? I18n.translateToLocalFormatted(langKey, args) : langKey;
    }

    public static String translateOrDefault(String langKey, String defaultVal) {
        return I18n.canTranslate(langKey) ? translate(langKey) : defaultVal;
    }

    public static String translateEntityCls(Class<? extends Entity> eClass) {
        if( EntityList.CLASS_TO_NAME.containsKey(eClass) ) {
            return translate(ENTITY_NAME.get(EntityList.CLASS_TO_NAME.get(eClass)));
        }

        return "[UNKNOWN] " + eClass.getName();
    }

    public static String translateEntityClsDesc(Class<? extends Entity> eClass) {
        if( EntityList.CLASS_TO_NAME.containsKey(eClass) ) {
            return translate(ENTITY_DESC.get(EntityList.CLASS_TO_NAME.get(eClass)));
        }

        return "";
    }

    public static final class TranslateKey
    {
        private final String key;

        TranslateKey(String key) {
            this.key = key;
        }

        TranslateKey(String key, Object... args) {
            this(String.format(key, args));
        }

        public String get() {
            return this.key;
        }

        public String get(Object... args) {
            return String.format(this.key, args);
        }
    }
}
