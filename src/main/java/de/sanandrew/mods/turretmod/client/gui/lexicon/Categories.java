/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.turretmod.client.gui.lexicon.ammo.LexiconGroupAmmo;
import de.sanandrew.mods.turretmod.client.gui.lexicon.info.LexiconGroupInfo;
import de.sanandrew.mods.turretmod.client.gui.lexicon.turret.LexiconGroupTurret;
import de.sanandrew.mods.turretmod.client.gui.lexicon.upgrade.LexiconGroupUpgrade;

public class Categories
{
    public static void initialize(ILexiconInst inst) {
        LexiconGroupTurret.register(inst);
        LexiconGroupAmmo.register(inst);
        LexiconGroupUpgrade.register(inst);
        LexiconGroupInfo.register(inst);
    }
}
