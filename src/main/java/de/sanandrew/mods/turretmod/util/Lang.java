/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.util;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;

public final class Lang
{
    public static final TranslateKey TCU_BTN               = new TranslateKey("gui.%s.tcu.page.info.button.%%s", TmrConstants.ID);
    public static final TranslateKey TCU_TARGET_BTN        = new TranslateKey("gui.%s.tcu.page.targetsEntity.button.%%s", TmrConstants.ID);
    public static final TranslateKey TCU_PAGE_TITLE        = new TranslateKey("gui.%s.tcu.page.%%s.title", TmrConstants.ID);
    public static final TranslateKey TCU_PAGE_TAB          = new TranslateKey("gui.%s.tcu.page.%%s.tab", TmrConstants.ID);
    public static final TranslateKey TCU_DISMANTLE_ERROR   = new TranslateKey("gui.%s.tcu.page.info.button.dismantle.error", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_HEALTH      = new TranslateKey("gui.%s.tcu.label.health", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_AMMO        = new TranslateKey("gui.%s.tcu.label.ammo", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_TARGET      = new TranslateKey("gui.%s.tcu.label.target", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_PRSSHIELD = new TranslateKey("gui.%s.tcu.label.shield", TmrConstants.ID);
    public static final TranslateKey TCU_LABEL_PRSSHIELD_RECV = new TranslateKey("gui.%s.tcu.label.shield_recv", TmrConstants.ID);

    public static final TranslateKey TINFO_CATEGORY_NAME      = new TranslateKey("gui.%s.tinfo.category.%%s.name", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_INFO_TITLE   = new TranslateKey("gui.%s.tinfo.infoTitle", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_AMMO_NAME    = new TranslateKey("%s.tinfo.ammo.%%s.name", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_AMMO_DESC    = new TranslateKey("%s.tinfo.ammo.%%s.desc", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_ROUNDS       = new TranslateKey("gui.%s.tinfo.rounds", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_DPS          = new TranslateKey("gui.%s.tinfo.dps", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_HEALTH       = new TranslateKey("gui.%s.tinfo.health", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_HEALTHVAL    = new TranslateKey("gui.%s.tinfo.healthVal", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_TURRET       = new TranslateKey("gui.%s.tinfo.turret", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_CRAFTING     = new TranslateKey("gui.%s.tinfo.crafting", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_WORKBENCH    = new TranslateKey("gui.%s.tinfo.workbench", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_PREREQ       = new TranslateKey("gui.%s.tinfo.prereq", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_RANGE        = new TranslateKey("gui.%s.tinfo.range", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_RANGEVAL     = new TranslateKey("gui.%s.tinfo.rangeVal", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_AMMOCAP      = new TranslateKey("gui.%s.tinfo.ammocap", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_AMMOUSE      = new TranslateKey("gui.%s.tinfo.ammouse", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_ROUNDSVAL    = new TranslateKey("gui.%s.tinfo.roundsVal", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_EFFICIENCY   = new TranslateKey("gui.%s.tinfo.efficiency", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_DECAY        = new TranslateKey("gui.%s.tinfo.decay", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_INFO_NAME    = new TranslateKey("gui.%s.tinfo.modName", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_INFO_VERSION = new TranslateKey("gui.%s.tinfo.modVersion", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_INFO_AUTHOR  = new TranslateKey("gui.%s.tinfo.modAuthor", TmrConstants.ID);
    public static final TranslateKey TINFO_ENTRY_INFO_CREDITS = new TranslateKey("gui.%s.tinfo.modCredits", TmrConstants.ID);

    public static final TranslateKey TURRET_NAME = new TranslateKey("%s.turret.%%s.name", TmrConstants.ID);
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

    /**
     * Wrapper method to {@link net.minecraft.util.text.translation.I18n#canTranslate(String)} for abbreviation.
     * <s>Also tries to translate with [NONE] to en_US if the translation fails</s>
     * @param langKey language key to be translated
     * @return translated key or langKey, if translation fails
     */
    @SuppressWarnings("deprecation")
    public static String translate(String langKey, Object... args) {
        return net.minecraft.util.text.translation.I18n.canTranslate(langKey)
                ? net.minecraft.util.text.translation.I18n.translateToLocalFormatted(langKey, args)
                : langKey;
    }

    public static String translate(TranslateKey langKey, Object... args) {
        return translate(langKey.key, args);
    }

    @SuppressWarnings("deprecation")
    public static String translateOrDefault(String langKey, String defaultVal) {
        return net.minecraft.util.text.translation.I18n.canTranslate(langKey) ? translate(langKey) : defaultVal;
    }

    public static String translateOrDefault(TranslateKey langKey, String defaultVal) {
        return translateOrDefault(langKey.key, defaultVal);
    }

    public static String translateEntityCls(Class<? extends Entity> eClass) {
        String namedEntry = EntityList.getTranslationName(EntityList.getKey(eClass));
        if( namedEntry != null ) {
            return translate(ENTITY_NAME.get(namedEntry));
        }

        return "[UNKNOWN] " + eClass.getName();
    }

    public static String translateEntityClsDesc(Class<? extends Entity> eClass) {
        String namedEntry = EntityList.getTranslationName(EntityList.getKey(eClass));
        if( namedEntry != null ) {
            return translate(ENTITY_DESC.get(namedEntry));
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
