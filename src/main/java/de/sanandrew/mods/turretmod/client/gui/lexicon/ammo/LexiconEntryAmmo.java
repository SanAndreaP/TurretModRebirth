/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.ammo;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunition;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionGroup;
import de.sanandrew.mods.turretmod.client.gui.lexicon.turret.LexiconGroupTurret;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import net.minecraft.item.ItemStack;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

final class LexiconEntryAmmo
        implements ILexiconEntry
{
    private final String id;
    private final ItemStack icon;
    final IAmmunition[] ammoTypes;

    private final String turretName;
    private final String ammoItemNames;

    LexiconEntryAmmo(IAmmunitionGroup group) {
        this.ammoTypes = AmmunitionRegistry.INSTANCE.getTypes(group).toArray(new IAmmunition[0]);
        this.icon = group.getIcon();
        this.id = group.getId().toString();
        this.turretName = group.getTurret().getId().toString();
        this.ammoItemNames = String.join("|", Stream.of(this.ammoTypes).map(a -> AmmunitionRegistry.INSTANCE.getItem(a.getId()).getDisplayName()).toArray(String[]::new));
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
        return this.icon;
    }

    @Nonnull
    @Override
    public String getSrcTitle() {
        return ClientProxy.lexiconInstance.getTranslatedTitle(this);
    }

    @Nonnull
    @Override
    public String getSrcText() {
        return ClientProxy.lexiconInstance.getTranslatedText(this) + ammoItemNames
               + ClientProxy.lexiconInstance.getGroup(LexiconGroupTurret.NAME).getEntry(this.turretName).getSrcTitle();
    }
}
