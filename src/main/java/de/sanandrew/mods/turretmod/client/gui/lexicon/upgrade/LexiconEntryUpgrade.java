/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.upgrade;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.turretmod.api.upgrade.ITurretUpgrade;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import de.sanandrew.mods.turretmod.registry.upgrades.UpgradeRegistry;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;

public final class LexiconEntryUpgrade
        implements ILexiconEntry
{
    private final String id;
    private final ItemStack item;
    final ITurretUpgrade upgrade;

    LexiconEntryUpgrade(ITurretUpgrade upgrade) {
        this.item =  UpgradeRegistry.INSTANCE.getUpgradeItem(upgrade);
        this.upgrade = upgrade;
        this.id = upgrade.getName();
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
        return LexiconRenderUpgrade.ID;
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
