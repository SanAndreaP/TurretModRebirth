/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.ammo;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.UUID;

public final class LexiconEntryAmmo
        implements ILexiconEntry
{
    private final String id;
    private final ItemStack[] icons;
    final IAmmunition<?>[] ammoTypes;

    public LexiconEntryAmmo(UUID groupId) {
        this.ammoTypes = AmmunitionRegistry.INSTANCE.getTypes(groupId);
        this.icons = Arrays.stream(this.ammoTypes).map(AmmunitionRegistry.INSTANCE::getAmmoItem).toArray(ItemStack[]::new);
        this.id = this.ammoTypes[0].getGroupName();
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getGroupId() {
        return LexiconGroupAmmo.NAME;
    }

    @Override
    public String getPageRenderId() {
        return LexiconRenderAmmo.ID;
    }

    @Nonnull
    @Override
    public ItemStack getEntryIcon() {
        return this.icons[0];
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
