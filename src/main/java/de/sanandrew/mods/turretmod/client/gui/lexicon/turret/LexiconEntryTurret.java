/* ******************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 *                http://creativecommons.org/licenses/by-nc-sa/4.0/
 *******************************************************************************************************************/
package de.sanandrew.mods.turretmod.client.gui.lexicon.turret;

import de.sanandrew.mods.sanlib.api.client.lexicon.ILexiconEntry;
import de.sanandrew.mods.turretmod.api.ammo.IAmmunitionGroup;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.client.gui.lexicon.ammo.LexiconGroupAmmo;
import de.sanandrew.mods.turretmod.client.util.ClientProxy;
import de.sanandrew.mods.turretmod.registry.ammo.AmmunitionRegistry;
import de.sanandrew.mods.turretmod.registry.turret.TurretRegistry;
import de.sanandrew.mods.turretmod.util.Lang;
import net.minecraft.item.ItemStack;
import org.apache.logging.log4j.util.Strings;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

final class LexiconEntryTurret
        implements ILexiconEntry
{
    private final String id;
    private final ItemStack icon;
    final ITurret turret;
    private String ammos;
    private final IAmmunitionGroup[] ammoGroups;

    LexiconEntryTurret(ITurret turret) {
        this.icon =  TurretRegistry.INSTANCE.getTurretItem(turret);
        this.turret = turret;
        this.id = turret.getId().toString();
        this.ammoGroups = AmmunitionRegistry.INSTANCE.getGroups(this.turret).toArray(new IAmmunitionGroup[0]);
    }

    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getGroupId() {
        return LexiconGroupTurret.NAME;
    }

    @Override
    public String getPageRenderId() {
        return LexiconRenderTurret.ID;
    }

    @Nonnull
    @Override
    public ItemStack getEntryIcon() {
        return this.icon;
    }

    @Override
    public String getTitleLangKey(String modId) {
        return Lang.ENTITY_NAME.get(this.turret.getId());
    }

    @Nonnull
    @Override
    public String getSrcTitle() {
        return ClientProxy.lexiconInstance.getTranslatedTitle(this);
    }

    @Nonnull
    @Override
    public String getSrcText() {
        if( Strings.isEmpty(this.ammos) ) {
            this.ammos = String.join("|", Stream.of(this.ammoGroups).map(g -> ClientProxy.lexiconInstance.getGroup(LexiconGroupAmmo.NAME).getEntry(g.getName()).getSrcTitle())
                                                                    .toArray(String[]::new));
        }
        return ClientProxy.lexiconInstance.getTranslatedText(this) + this.ammos;
    }
}
