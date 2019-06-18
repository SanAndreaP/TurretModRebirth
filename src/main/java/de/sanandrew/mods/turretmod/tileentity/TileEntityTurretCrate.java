package de.sanandrew.mods.turretmod.tileentity;

import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.inventory.TurretCrateInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

public class TileEntityTurretCrate
        extends TileEntity
{
    private final TurretCrateInventory invHandler;
    private final IItemHandler itemHandler;

    private String customName;

    public TileEntityTurretCrate() {
        this.invHandler = new TurretCrateInventory(this);
        this.itemHandler = new InvWrapper(this.invHandler);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        compound = super.writeToNBT(compound);

        compound.setTag("Inventory", this.invHandler.serializeNBT());

        if( this.hasCustomName() ) {
            compound.setString("CustomName", this.customName);
        }

        return compound;
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);

        this.invHandler.deserializeNBT(compound.getCompoundTag("Inventory"));

        if( compound.hasKey("CustomName") ) {
            this.customName = compound.getString("CustomName");
        }
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getName() {
        return this.hasCustomName() ? this.customName : TmrConstants.ID + ".container.turret_crate";
    }

    public boolean hasCustomName() {
        return this.customName != null;
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getName()) : new TextComponentTranslation(this.getName());
    }

    @Override
    @SuppressWarnings({"unchecked", "ObjectEquality"})
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if( capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ) {
            return (T) this.itemHandler;
        }

        return null;
    }

    @Override
    @SuppressWarnings("ObjectEquality")
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    public IInventory getInventory() {
        return this.invHandler;
    }
}