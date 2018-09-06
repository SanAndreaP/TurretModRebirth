/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.upgrade;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGroup;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.sanlib.api.client.lexicon.LexiconGroup;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.Resources;

public final class LexiconGroupUpgrade
        extends LexiconGroup
{
    public static final String NAME = "upgrade";

    private LexiconGroupUpgrade() {
        super(NAME, Resources.TINFO_GRP_UPGRADE.resource);
    }

    public static void register(ILexiconInst registry) {
        registry.registerPageRender(new LexiconRenderUpgrade());

        ILexiconGroup grp = new LexiconGroupUpgrade();
        registry.registerGroup(grp);

        UpgradeRegistry.INSTANCE.getUpgrades().forEach(u -> grp.addEntry(new LexiconEntryUpgrade(u)));
    }
}
