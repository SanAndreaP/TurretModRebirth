/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.upgrade;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

final class LexiconEntryAssemblyUpgrade
        implements ILexiconEntry
{
    private final String id;
    private final ItemStack item;
    private boolean doDivisor;

    LexiconEntryAssemblyUpgrade(String id, ItemStack stack) {
        this.id = id;
        this.item =  stack;
    }

    LexiconEntryAssemblyUpgrade setDivideAfter() {
        this.doDivisor = true;
        return this;
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getGroupId() {
        return LexiconGroupUpgrade.NAME;
    }

    @Override
    public String getPageRenderId() {
        return LexiconRenderAssemblyUpgrade.ID;
    }

    @Nonnull
    @Override
    public ItemStack getEntryIcon() {
        return this.item;
    }

    @Override
    public boolean divideAfter() {
        return this.doDivisor;
    }

    @Override
    public String getTitleLangKey(String modId) {
        return this.item.getTranslationKey() + ".name";
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
