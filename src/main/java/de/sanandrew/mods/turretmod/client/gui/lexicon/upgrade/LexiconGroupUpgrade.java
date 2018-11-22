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
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import de.sanandrew.mods.turretmod.util.Resources;
import net.minecraft.item.ItemStack;

public final class LexiconGroupUpgrade
        extends LexiconGroup
{
    public static final String NAME = "upgrade";

    private LexiconGroupUpgrade() {
        super(NAME, Resources.TINFO_GRP_UPGRADE.resource);
    }

    public static void register(ILexiconInst registry) {
        registry.registerPageRender(new LexiconRenderUpgrade());
        registry.registerPageRender(new LexiconRenderAssemblyUpgrade());

        ILexiconGroup grp = new LexiconGroupUpgrade();
        registry.registerGroup(grp);

        grp.addEntry(new LexiconEntryAssemblyUpgrade("assembly_auto", new ItemStack(ItemRegistry.ASSEMBLY_UPG_AUTO)));
        grp.addEntry(new LexiconEntryAssemblyUpgrade("assembly_speed", new ItemStack(ItemRegistry.ASSEMBLY_UPG_SPEED)));
        grp.addEntry(new LexiconEntryAssemblyUpgrade("assembly_filter", new ItemStack(ItemRegistry.ASSEMBLY_UPG_FILTER)).setDivideAfter());

        UpgradeRegistry.INSTANCE.getTypes().forEach(u -> grp.addEntry(new LexiconEntryUpgrade(u)));
    }
}
