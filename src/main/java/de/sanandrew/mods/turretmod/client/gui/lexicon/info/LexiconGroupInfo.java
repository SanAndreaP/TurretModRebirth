/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.info;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconGroup;
import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconInst;
import de.sanandrew.mods.sanlib.api.client.lexicon.LexiconGroup;
import de.sanandrew.mods.turretmod.util.Resources;

public final class LexiconGroupInfo
        extends LexiconGroup
{
    static final String GRP_NAME = "info";

    private LexiconGroupInfo() {
        super(GRP_NAME, Resources.TINFO_GRP_INFO.resource);
    }

    public static void register(ILexiconInst registry) {
        registry.registerPageRender(new LexiconRenderInfo());

        ILexiconGroup grp = new LexiconGroupInfo();
        registry.registerGroup(grp);

        grp.addEntry(new LexiconEntryInfo());
    }
}
