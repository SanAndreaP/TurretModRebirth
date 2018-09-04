/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.ammo;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGroup;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.sanlib.api.client.lexicon.LexiconGroup;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.util.Resources;

public final class LexiconGroupAmmo
        extends LexiconGroup
{
    static final String NAME = "ammo";

    private LexiconGroupAmmo() {
        super(NAME, Resources.TINFO_GRP_AMMO.resource);
    }

    public static void register(ILexiconInst registry) {
        registry.registerPageRender(new LexiconRenderAmmo());

        ILexiconGroup grp = new LexiconGroupAmmo();
        registry.registerGroup(grp);

        AmmunitionRegistry.INSTANCE.getGroups().forEach(g -> grp.addEntry(new LexiconEntryAmmo(g)));
    }
}
