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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.IContainerFactory;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class TcuContainerFactory
        implements IContainerFactory<TcuContainer>
{
    public static final ContainerType.IFactory<TcuContainer> INSTANCE = new TcuContainerFactory();

    public static final Map<ResourceLocation, TcuContainer.TcuContainerProvider> TCU_CONTAINERS = new HashMap<>();

    @Override
    public TcuContainer create(int windowId, PlayerInventory inv, PacketBuffer data) {
        Entity e = inv.player.level.getEntity(data.readVarInt());
        ResourceLocation pageId = data.readResourceLocation();
        TcuContainer.TcuContainerProvider prv = TCU_CONTAINERS.get(pageId);
        if( e instanceof ITurretEntity ) {
            return prv != null ? prv.apply(windowId, inv, (ITurretEntity) e, pageId) : new TcuContainer(windowId, inv, (ITurretEntity) e, pageId);
        }

        return null;
    }

    public static class Provider
            implements INamedContainerProvider
    {
        @Nonnull
        private final ItemStack tcu;
        private final ITurretEntity turret;
        private final ResourceLocation type;

        public Provider(@Nonnull ItemStack tcu, ITurretEntity turret, ResourceLocation type) {
            this.tcu = tcu;
            this.turret = turret;
            this.type = type;
        }

        @Nonnull
        @Override
        public ITextComponent getDisplayName() {
            return this.tcu.getDisplayName();
        }

        @Nullable
        @Override
        public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
            TcuContainer.TcuContainerProvider prv = TCU_CONTAINERS.get(this.type);
            return prv != null ? prv.apply(id, playerInventory, this.turret, this.type) : new TcuContainer(id, playerInventory, this.turret, this.type);
        }
    }
}
