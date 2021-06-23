package de.sanandrew.mods.turretmod.api.tcu;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public interface ITcuRegistry
{
    void registerTcuPage(@Nonnull ResourceLocation id);

    void registerTcuPage(@Nonnull ResourceLocation id, @Nullable TcuContainer.TcuContainerProvider containerProvider);

    @OnlyIn(Dist.CLIENT)
    void registerTcuScreen(@Nonnull ResourceLocation id, Supplier<ItemStack> iconSupplier,
                           de.sanandrew.mods.turretmod.client.gui.TcuScreen.ScreenProvider screenProvider);
}
