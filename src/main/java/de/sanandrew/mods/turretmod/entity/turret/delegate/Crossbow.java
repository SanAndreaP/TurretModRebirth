package de.sanandrew.mods.turretmod.entity.turret.delegate;

import de.sanandrew.mods.turretmod.api.ResourceLocations;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.api.turret.IVariant;
import de.sanandrew.mods.turretmod.api.turret.IVariantHolder;
import de.sanandrew.mods.turretmod.api.turret.Turret;
import de.sanandrew.mods.turretmod.entity.turret.variant.DualItemVariants;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;

import java.util.LinkedHashMap;
import java.util.Map;

public class Crossbow
        extends Turret
        implements IVariantHolder
{
    private DualItemVariants variantContainer;

    public Crossbow(ResourceLocation id) {
        super(id, 1, false, TargetType.GROUND, new SoundIds(ResourceLocations.SOUND_SHOOT_CROSSBOW).withDefaults());

        this.glowTexture = ResourceLocations.TEXTURE_ENTITY_CROSSBOW_GLOW;
        this.range = new AxisAlignedBB(-16.0D, -4.0D, -16.0D, 16.0D, 8.0D, 16.0D);
    }

    private void buildVariants() {
        if( this.variantContainer == null ) {
            this.variantContainer = new DualItemVariants(ResourceLocations.TEXTURE_ENTITY_CROSSBOW_BASE);
            Map<String, ItemStack> bases = new LinkedHashMap<>();
            bases.put("cobblestone", new ItemStack(Blocks.COBBLESTONE));
            bases.put("mossy_cobblestone", new ItemStack(Blocks.MOSSY_COBBLESTONE));
            bases.put("granite", new ItemStack(Blocks.GRANITE));
            bases.put("diorite", new ItemStack(Blocks.DIORITE));
            bases.put("andesite", new ItemStack(Blocks.ANDESITE));

            Map<String, ItemStack> frames = new LinkedHashMap<>();
            frames.put("oak", new ItemStack(Blocks.OAK_PLANKS));
            frames.put("spruce", new ItemStack(Blocks.SPRUCE_PLANKS));
            frames.put("birch", new ItemStack(Blocks.BIRCH_PLANKS));
            frames.put("jungle", new ItemStack(Blocks.JUNGLE_PLANKS));
            frames.put("acacia", new ItemStack(Blocks.ACACIA_PLANKS));
            frames.put("dark_oak", new ItemStack(Blocks.DARK_OAK_PLANKS));

            for( Map.Entry<String, ItemStack> base : bases.entrySet() ) {
                for( Map.Entry<String, ItemStack> frame : frames.entrySet() ) {
                    this.variantContainer.register(base.getValue(), frame.getValue(), String.format("%s_%s", base.getKey(), frame.getKey()));
                }
            }
        }
    }

    @Override
    public ResourceLocation getBaseTexture(ITurretEntity turret) {
        this.buildVariants();

        return turret.getVariant().getTexture();
    }

    @Override
    public IVariant getVariant(Object id) {
        this.buildVariants();

        return this.variantContainer.get(id);
    }

    @Override
    public void registerVariant(IVariant variant) {
        this.buildVariants();

        this.variantContainer.register(variant);
    }

    @Override
    public boolean isDefaultVariant(IVariant variant) {
        this.buildVariants();

        return this.variantContainer.isDefault(variant);
    }

    @Override
    public IVariant getVariant(String s) {
        this.buildVariants();

        return this.variantContainer.get(s);
    }
}
