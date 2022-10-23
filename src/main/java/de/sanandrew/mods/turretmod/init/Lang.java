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
    public static final TranslateKey TCU_TEXT  = LangUtils.newKey("gui.%s.tcu.%%s").format(TmrConstants.ID).build();
    public static final TranslateKey TCU_LABEL = LangUtils.newKey("gui.%s.tcu.label.%%s").format(TmrConstants.ID).build();
    public static final TranslateKey ASSEMBLY_GROUP_LABEL = LangUtils.newKey("gui.%s.assembly.group.%%s").format(TmrConstants.ID).build();

    public static final TranslateKey ITEM_TURRET_PLACER = LangUtils.newKey("item.%s.turret.%%s").format(TmrConstants.ID).build();
    public static final TranslateKey ITEM_UPGRADE = LangUtils.newKey("item.%s.upgrade.%%s").format(TmrConstants.ID).build();

    public static final TranslateKey JEI_ASSEMBLY_TITLE  = LangUtils.newKey("jei.%s.assembly.title").format(TmrConstants.ID).build();
    public static final TranslateKey JEI_ASSEMBLY_ENERGY = LangUtils.newKey("jei.%s.assembly.energy").format(TmrConstants.ID).build();
    public static final TranslateKey JEI_ASSEMBLY_TIME   = LangUtils.newKey("jei.%s.assembly.time").format(TmrConstants.ID).build();

    public static final TranslateKey DEATH_OWNER = LangUtils.newKey("death.attack.%s.with_owner").format(TmrConstants.ID).build();
    public static final TranslateKey DEATH_TURRET = LangUtils.newKey("death.attack.%s").withoutRlColon().build();
}
