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
import de.sanandrew.mods.turretmod.client.util.ResourceOrderer;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.Resources;

public final class LexiconGroupAmmo
        extends LexiconGroup
{
    public static final String NAME = "ammo";

    private LexiconGroupAmmo() {
        super(NAME, Resources.TINFO_GRP_AMMO.resource);
    }

    public static void register(ILexiconInst registry) {
        registry.registerPageRender(new LexiconRenderAmmo());

        ILexiconGroup grp = new LexiconGroupAmmo();
        registry.registerGroup(grp);

        AmmunitionRegistry.INSTANCE.getGroups().stream()
                .sorted(ResourceOrderer.getOrderComparator(g -> AmmunitionRegistry.INSTANCE.getTypes(g).stream().findFirst().orElseThrow(RuntimeException::new).getId(),
                                                           g -> g.getId().toString()))
                .forEach(g -> grp.addEntry(new LexiconEntryAmmo(g)));
    }
}
