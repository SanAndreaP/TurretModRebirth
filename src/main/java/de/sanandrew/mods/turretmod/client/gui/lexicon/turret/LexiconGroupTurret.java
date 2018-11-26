/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.turret;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGroup;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.sanlib.api.client.lexicon.LexiconGroup;
import de.sanandrew.mods.sanlib.lib.util.LangUtils;
import de.sanandrew.mods.turretmod.api.IRegistryType;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.client.util.ResourceOrderer;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.util.Resources;

public final class LexiconGroupTurret
        extends LexiconGroup
{
    public static final String NAME = "turret";

    private LexiconGroupTurret() {
        super(NAME, Resources.TINFO_GRP_TURRET.resource);
    }

    public static void register(ILexiconInst registry) {
        registry.registerPageRender(new LexiconRenderTurret());

        ILexiconGroup grp = new LexiconGroupTurret();
        registry.registerGroup(grp);

        TurretRegistry.INSTANCE.getTypes().stream()
                .sorted(ResourceOrderer.getOrderComparator(IRegistryType::getId, t -> t.getId().toString()))
                .forEach(t -> grp.addEntry(new LexiconEntryTurret(t)));
    }
}
