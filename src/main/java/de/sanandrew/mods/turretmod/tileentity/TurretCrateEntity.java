package de.sanandrew.mods.turretmod.tileentity;

import de.sanandrew.mods.sanlib.lib.util.ReflectionUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.api.turret.ITurretEntity;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.inventory.TurretCrateInventory;
import de.sanandrew.mods.turretmod.inventory.container.TurretCrateContainer;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.INameable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TurretCrateEntity
        extends TileEntity
        implements INamedContainerProvider, INameable
{
    private static final StringTextComponent DEF_CONTAINER_NAME = new StringTextComponent(TmrConstants.ID + ".container.turret_crate");

    private final TurretCrateInventory invHandler;
    private final IItemHandler         itemHandler;
    private final LazyOptional<IItemHandler> loItemHandler;

    private ITextComponent customName;

    public TurretCrateEntity() {
        super(BlockRegistry.TURRET_CRATE_ENTITY);
        this.invHandler = new TurretCrateInventory(this);
        this.itemHandler = new InvWrapper(this.invHandler);
        this.loItemHandler = LazyOptional.of(() -> this.itemHandler);
    }

    @Nonnull
    @Override
    public CompoundNBT save(@Nonnull CompoundNBT compound) {
        compound = super.save(compound);

        compound.put("Inventory", this.invHandler.serializeNBT());

        if( this.hasCustomName() ) {
            compound.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
        }

        return compound;
    }

    @Override
    public void load(@Nonnull BlockState state, @Nonnull CompoundNBT compound) {
        super.load(state, compound);

        this.invHandler.deserializeNBT(compound.getCompound("Inventory"));

        if( compound.contains("CustomName") ) {
            this.customName = ITextComponent.Serializer.fromJson(compound.getString("CustomName"));
        }
    }

    public void setCustomName(ITextComponent customName) {
        this.customName = customName;
    }

    @Nonnull
    @Override
    public ITextComponent getName() {
        return this.getDisplayName();
    }

    @Override
    public boolean hasCustomName() {
        return this.customName != null;
    }

    public int getAmmoCount() {
        return this.invHandler.getAmmoCount();
    }

    @Nonnull
    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? this.customName : DEF_CONTAINER_NAME;
    }

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return this.customName;
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap) {
        if( cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ) {
            return ReflectionUtils.getCasted(this.loItemHandler);
        }

        return super.getCapability(cap);
    }

    public TurretCrateInventory getInventory() {
        return this.invHandler;
    }

    public void insertTurret(ITurretEntity turretInst) {
        this.invHandler.insertTurret(turretInst);
    }

    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, -1, this.invHandler.serializeNBT());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.invHandler.deserializeNBT(pkt.getTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();

        nbt.put("Inventory", this.invHandler.serializeNBT());

        return nbt;
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        super.handleUpdateTag(state, tag);

        this.invHandler.deserializeNBT(tag.getCompound("Inventory"));
    }

    @Nullable
    @Override
    public Container createMenu(int windowId, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity player) {
        return new TurretCrateContainer(windowId, playerInventory, this);
    }
}
