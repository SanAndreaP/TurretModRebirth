/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.misc;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

class LexiconEntryTCU
        implements ILexiconEntry
{
    private final String id;
    private final ItemStack item;

    LexiconEntryTCU() {
        this.id = "tcu";
        this.item = new ItemStack(ItemRegistry.TURRET_CONTROL_UNIT);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getGroupId() {
        return LexiconGroupMisc.NAME;
    }

    @Override
    public String getPageRenderId() {
        return LexiconRenderTCU.ID;
    }

    @Nonnull
    @Override
    public ItemStack getEntryIcon() {
        return this.item;
    }

    @Override
    public String getTitleLangKey(String modId) {
        return this.item.getUnlocalizedName() + ".name";
    }

    @Nonnull
    @Override
    public String getSrcTitle() {
        return ClientProxy.lexiconInstance.getTranslatedTitle(this);
    }

    @Nonnull
    @Override
    public String getSrcText() {
        return ClientProxy.lexiconInstance.getTranslatedText(this);
    }
}
