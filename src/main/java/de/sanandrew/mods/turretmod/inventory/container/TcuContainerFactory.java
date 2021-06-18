package de.sanandrew.mods.turretmod.inventory.container;

import de.sanandrew.mods.turretmod.api.tcu.TcuContainer;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TcuContainerFactory
        implements IContainerFactory<TcuContainer>
{
    public static final ContainerType.IFactory<TcuContainer> INSTANCE = new TcuContainerFactory();

    public static void openTcu(ServerPlayerEntity player, ItemStack stack, ITurretEntity turret) {
        NetworkHooks.openGui(player, new TcuContainerFactory.Provider(stack, turret), buf -> buf.writeVarInt(turret.get().getId()));
    }

    @Override
    public TcuContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
        Entity e = inv.player.level.getEntity(data.readVarInt());
        if( e instanceof ITurretEntity ) {
            return new TcuContainer(windowId, inv, (ITurretEntity) e);
        }

        return null;
    }

    public static class Provider
            implements INamedContainerProvider
    {
        @Nonnull
        private final ItemStack tcu;
        private final ITurretEntity turret;

        public Provider(@Nonnull ItemStack tcu, ITurretEntity turret) {
            this.tcu = tcu;
            this.turret = turret;
        }

        @Nonnull
        @Override
        public ITextComponent getDisplayName() {
            return this.tcu.getDisplayName();
        }

        @Nullable
        @Override
        public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
            return new TcuContainer(id, playerInventory, this.turret);
        }
    }
}
