package de.sanandrew.mods.turretmod.client.gui.element.tinfo;

import com.google.gson.JsonObject;
import de.sanandrew.mods.sanlib.lib.client.gui.GuiElementInst;
import de.sanandrew.mods.sanlib.lib.client.gui.IGui;
import de.sanandrew.mods.sanlib.lib.client.gui.element.Texture;
import de.sanandrew.mods.sanlib.lib.util.JsonUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurret;
import de.sanandrew.mods.turretmod.api.turret.ITurretInst;
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import de.sanandrew.mods.turretmod.client.gui.GuiTurretInfo;
import de.sanandrew.mods.turretmod.registry.turret.TurretCrossbow;
import de.sanandrew.mods.turretmod.registry.turret.TurretCryolator;
import de.sanandrew.mods.turretmod.registry.turret.TurretFlamethrower;
import de.sanandrew.mods.turretmod.registry.turret.TurretHarpoon;
import de.sanandrew.mods.turretmod.registry.turret.TurretLaser;
import de.sanandrew.mods.turretmod.registry.turret.TurretMinigun;
import de.sanandrew.mods.turretmod.registry.turret.TurretRevolver;
import de.sanandrew.mods.turretmod.registry.turret.TurretShotgun;
import de.sanandrew.mods.turretmod.registry.turret.forcefield.TurretForcefield;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.function.Function;

public class InfoBgTexture
        extends Texture
{
    private static final HashMap<Class<? extends ITurret>, TextureVariant> TEXTURES = new HashMap<>();

    public static final ResourceLocation ID = new ResourceLocation(TmrConstants.ID, "tinfo_texture");

    @Override
    public void bakeData(IGui gui, JsonObject data, GuiElementInst inst) {
        if( gui instanceof GuiTurretInfo ) {
            ITurretInst turretInst = ((GuiTurretInfo) gui).getTurretInst();
            ITurret turret = turretInst.getTurret();

            TextureVariant variant = TEXTURES.get(turret.getClass());
            if( variant != null ) {
                String tx = TmrConstants.ID + ":textures/gui/turretinfo/"
                            + TEXTURES.get(turret.getClass()).getTexturePath(turretInst.getVariant());
                JsonUtils.addJsonProperty(data, "texture", tx);
            }
        }

        super.bakeData(gui, data, inst);
    }

    public static void addTexture(Class<? extends ITurret> turretClass, String texture) {
        addTexture(turretClass, texture, null);
    }

    public static void addTexture(Class<? extends ITurret> turretClass, String path, Function<IVariant, String> variantProcessor) {
        TEXTURES.put(turretClass, new TextureVariant(path, variantProcessor));
    }

    public static void initialize() {
        addTexture(TurretCrossbow.class, "t1_crossbow", v -> {
            String p = v.getId().getPath();
            if( p.startsWith("mossy_cobblestone") ) { return "mossy_cobblestone"; }
            if( p.startsWith("diorite") )           { return "diorite"; }
            if( p.startsWith("andesite") )          { return "andesite"; }
            if( p.startsWith("granite") )           { return "granite"; }

            return "cobblestone";
        });
        addTexture(TurretShotgun.class, "t1_shotgun", v -> {
            String p = v.getId().getPath();
            if( p.startsWith("stonebrick") )          { return "stonebrick"; }
            if( p.startsWith("mossy_stonebrick") )    { return "mossy_stonebrick"; }
            if( p.startsWith("cracked_stonebrick") )  { return "cracked_stonebrick"; }
            if( p.startsWith("chiseled_stonebrick") ) { return "chiseled_stonebrick"; }

            return "stone";
        });
        addTexture(TurretHarpoon.class, "t1_harpoon", v -> {
            String p = v.getId().getPath();
            if( !p.equals("terracotta") ) {
                for( EnumDyeColor clr : EnumDyeColor.values() ) {
                    if( p.startsWith(clr.getName()) ) {
                        return clr.getName() + "_terracotta";
                    }
                }
            }

            return "terracotta";
        });
        addTexture(TurretCryolator.class, "t1_cryolator");
        addTexture(TurretRevolver.class, "t2_revolver");
        addTexture(TurretMinigun.class, "t2_minigun");
        addTexture(TurretForcefield.class, "t2_forcefield");
        addTexture(TurretLaser.class, "t3_laser");
        addTexture(TurretFlamethrower.class, "t3_flamethrower");
    }

    private static class TextureVariant
    {
        private final String                     base;
        private final Function<IVariant, String> variantProc;

        TextureVariant(String base, Function<IVariant, String> variantProc) {
            this.base = base;
            this.variantProc = variantProc;
        }

        String getTexturePath(IVariant variant) {
            StringBuilder sb = new StringBuilder(this.base);

            if( this.variantProc != null && variant != null ) {
                sb.append("/");
                sb.append(this.variantProc.apply(variant));
            }

            sb.append(".png");

            return sb.toString();
        }
    }
}
