package de.sanandrew.mods.turretmod.client.renderer.turret.label;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import de.sanandrew.mods.sanlib.lib.ColorObj;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRegistry;
import de.sanandrew.mods.turretmod.api.client.tcu.ILabelRenderer;
import de.sanandrew.mods.turretmod.api.turret.ITargetProcessor;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.init.Lang;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector2f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nonnull;

public class Ammo
        extends ValueBar
{
    public Ammo(ResourceLocation id) {
        super(id);
    }

    @Override
    public int getSortOrder() {
        return 2;
    }

    @Override
    protected ITextComponent getLabelTxt() {
        return new TranslationTextComponent(Lang.TCU_LABEL.get("ammo.text"));
    }

    @Override
    protected ITextComponent getValueTxt(ITurretEntity turret) {
        int ammo = turret.getTargetProcessor().getAmmoCount();

        return new TranslationTextComponent(Lang.TCU_LABEL.get("ammo.value"), ammo);
    }

    @Override
    protected float getValue(ITurretEntity turret) {
        return turret.getTargetProcessor().getAmmoCount();
    }

    @Override
    protected float getMaxValue(ITurretEntity turret) {
        return turret.getTargetProcessor().getMaxAmmoCapacity();
    }

    @Override
    protected ColorObj getFgColor(float opacity) {
        return new ColorObj(0.5F, 0.5F, 1.0F, opacity);
    }

    @Override
    protected ColorObj getBgColor(float opacity) {
        return new ColorObj(0.0F, 0.0F, 0.3F, opacity);
    }
}
