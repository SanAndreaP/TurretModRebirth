/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity.assembly;

import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.turretmod.api.assembly.IAssemblyRecipe;
import de.sanandrew.mods.turretmod.block.BlockRegistry;
import de.sanandrew.mods.turretmod.inventory.container.ElectrolyteGeneratorContainer;
import de.sanandrew.mods.turretmod.inventory.container.TurretAssemblyContainer;
import de.sanandrew.mods.turretmod.item.ItemRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneWireBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Arrays;

@SuppressWarnings("NullableProblems")
public class TurretAssemblyEntity
        extends TileEntity
        implements ITickableTileEntity, INamedContainerProvider, INameable
{
    private static final String NBT_IS_ACTIVE         = "IsActive";
    private static final String NBT_RECIPE            = "Recipe";
    private static final String NBT_CRAFTING_AMOUNT   = "CraftingAmount";
    private static final String NBT_AUTOMATE          = "Automate";
    private static final String NBT_TICKS_CRAFTED     = "TicksCrafted";
    private static final String NBT_MAX_TICKS_CRAFTED = "MaxTicksCrafted";
    private static final String NBT_FLUX_CONSUMPTION  = "FluxConsumption";
    private static final String NBT_CAP_ENERGY        = "CapabilityEnergy";
    private static final String NBT_CAP_INVENTORY     = "CapabilityInventory";
    private static final String NBT_CUSTOM_NAME       = "CustomName";
    private static final String NBT_CACHE             = "CraftingCache";

    private boolean isActive;
    private ResourceLocation currRecipeId;
    private int              craftingAmount;
    private boolean automate;

    private int ticksCrafted = 0;
    private int maxTicksCrafted = 0;
    private int fluxConsumption = 0;

    protected final AssemblySyncData   syncData      = new AssemblySyncData(this);
    final           AssemblyEnergyStorage energyStorage = new AssemblyEnergyStorage();

    private final AssemblyInventory itemHandler       = new AssemblyInventory(this::getLevel);
    private final IItemHandler      itemHandlerBottom = new SidedInvWrapper(this.itemHandler, Direction.DOWN);
    private final IItemHandler      itemHandlerSide   = new SidedInvWrapper(this.itemHandler, Direction.NORTH);

    private final AssemblyCache cache = new AssemblyCache(this, this.itemHandler);

    private ITextComponent customName;

    private final RobotArm robotArm = new RobotArm();

    public TurretAssemblyEntity() {
        super(BlockRegistry.TURRET_ASSEMBLY_ENTITY);
    }

    public void beginCrafting(IAssemblyRecipe recipe, int count) {
        if( this.currRecipeId != null && recipe.getId().equals(this.currRecipeId) && !this.automate ) {
            if( this.craftingAmount + count < 1 ) {
                this.cancelCrafting();
            } else {
                this.craftingAmount += count;
            }
        } else if( this.currRecipeId == null ) {
            this.currRecipeId = recipe.getId();
            this.craftingAmount = this.automate ? 1 : count;
        }
    }

    public void cancelCrafting() {
        this.cache.dropItems();

        this.currRecipeId = null;
        this.craftingAmount = 0;
        this.ticksCrafted = 0;
        this.fluxConsumption = 0;
        this.maxTicksCrafted = 0;
        this.isActive = false;
    }

    public boolean hasAutoUpgrade() {
        return this.itemHandler.hasAutoUpgrade();
    }

    public boolean hasSpeedUpgrade() {
        return this.itemHandler.hasSpeedUpgrade();
    }

    public boolean hasFilterUpgrade() {
        return this.itemHandler.hasFilterUpgrade();
    }

    public boolean hasRedstoneUpgrade() {
        return this.itemHandler.hasRedstoneUpgrade();
    }

    public NonNullList<ItemStack> getFilterStacks() {
        return this.itemHandler.getFilterStacks();
    }

    @Override
    public void tick() {
        if( this.level == null ) {
            return;
        }

        if( !this.level.isClientSide ) {
            boolean prevActive = this.isActive;
            boolean doSync = false;
            this.energyStorage.updatePrevFlux();

            if( this.automate && !this.hasAutoUpgrade() ) {
                this.automate = false;
                this.cancelCrafting();
            }

            if( this.energyStorage.hasFluxChanged() ) {
                doSync = true;
            }

            int maxLoop = this.hasSpeedUpgrade() ? 4 : 1;

            if( this.isActive && this.currRecipeId != null ) {
                if( this.hasRedstoneUpgrade() && this.isRedstonePowered() ) {
                    this.isActive = false;
                } else {
                    if( !prevActive ) {
                        doSync = true;
                    }

                    for( int i = 0; i < maxLoop; i++ ) {
                        if( !this.process() ) {
                            break;
                        }
                    }
                }
            } else {
                this.initCrafting();
            }

            if( doSync || prevActive ^ this.isActive ) {
                this.setChanged();
            }
        } else {
            this.robotArm.process(this.isActive, this.hasSpeedUpgrade());
        }
    }

    private void initCrafting() {
        if( this.currRecipeId != null && this.craftingAmount >= 1 ) {
            IAssemblyRecipe recipe = AssemblyManager.INSTANCE.getRecipe(this.level, this.currRecipeId);
            ItemStack result = recipe.assemble(this.itemHandler);
            if( ItemStackUtils.isValid(result) && this.itemHandler.canFillOutput(result) ) {
                this.cache.insert(AssemblyManager.INSTANCE.checkAndConsumeResources(this.itemHandler, this.level, recipe, AssemblyInventory.SLOTS_INSERT));
                if( !this.cache.isEmpty() ) {
                    this.maxTicksCrafted = recipe.getProcessTime();
                    this.fluxConsumption = MathHelper.ceil(recipe.getEnergyConsumption() * (this.hasSpeedUpgrade() ? 1.1F : 1.0F));
                    this.ticksCrafted = 0;
                    this.isActive = true;
                    return;
                }
            }
        }

        this.isActive = false;
    }

    private boolean process() {
        if( this.energyStorage.fluxAmount >= this.fluxConsumption ) {
            this.energyStorage.fluxAmount -= this.fluxConsumption;
            if( ++this.ticksCrafted >= this.maxTicksCrafted ) { // if finished crafting...
                ItemStack stack = AssemblyManager.INSTANCE.getRecipe(this.level, this.currRecipeId).getResultItem();
                if( !ItemStackUtils.isValid(stack) ) {
                    this.cancelCrafting();
                    return false;
                }

                this.itemHandler.fillOutput(stack);
                this.cache.clearContent();

                if( this.craftingAmount > 1 || this.automate ) {
                    if( !this.automate ) {
                        this.craftingAmount--;
                    }

                    if( this.isActive ) {
                        this.initCrafting();
                    }
                } else {
                    this.cancelCrafting();
                    return false;
                }
                this.ticksCrafted = 0;
            }
        } else {
            this.isActive = false;
        }

        return this.isActive && this.currRecipeId != null;
    }

    private boolean isRedstonePowered() {
        if( this.level == null ) {
            return false;
        }

        BlockPos pos = this.getBlockPos();
        return Arrays.stream(Direction.values()).anyMatch(dir -> isPowered(this.level, pos, dir));
//        if( isPowered(this.level, this.getBlockPos().below(), Direction.DOWN) || isPowered(this.level, this.getBlockPos().above(), Direction.UP) ) {
//            return true;
//        }
//
//        return Direction.Plane.HORIZONTAL.stream().anyMatch(f -> RedstonePowerProxy.INSTANCE.isPowered(this.level, this.getBlockPos(), f));
    }

    public IInventory getInventory() {
        return this.itemHandler;
    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);

        this.isActive = tag.getBoolean(NBT_IS_ACTIVE);
        this.currRecipeId = tag.contains(NBT_RECIPE) ? new ResourceLocation(tag.getString(NBT_RECIPE)) : null;
        this.craftingAmount = tag.getInt(NBT_CRAFTING_AMOUNT);

        this.ticksCrafted = tag.getInt(NBT_TICKS_CRAFTED);
        this.maxTicksCrafted = tag.getInt(NBT_MAX_TICKS_CRAFTED);
        this.fluxConsumption = tag.getInt(NBT_FLUX_CONSUMPTION);

        this.energyStorage.deserializeNBT(tag.getCompound(NBT_CAP_ENERGY));
        this.itemHandler.deserializeNBT(tag.getCompound(NBT_CAP_INVENTORY));
        this.cache.deserializeNBT(tag.getCompound(NBT_CACHE));

        if( tag.contains(NBT_CUSTOM_NAME, Constants.NBT.TAG_STRING) ) {
            this.customName = ITextComponent.Serializer.fromJson(tag.getString(NBT_CUSTOM_NAME));
        }
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        tag = super.save(tag);

        tag.putBoolean(NBT_IS_ACTIVE, this.isActive);
        if( this.currRecipeId != null ) {
            tag.putString(NBT_RECIPE, this.currRecipeId.toString());
        }
        tag.putInt(NBT_CRAFTING_AMOUNT, this.craftingAmount);
        tag.putBoolean(NBT_AUTOMATE, this.automate);

        tag.putInt(NBT_TICKS_CRAFTED, this.ticksCrafted);
        tag.putInt(NBT_MAX_TICKS_CRAFTED, this.maxTicksCrafted);
        tag.putInt(NBT_FLUX_CONSUMPTION, this.fluxConsumption);

        tag.put(NBT_CAP_ENERGY, this.energyStorage.serializeNBT());
        tag.put(NBT_CAP_INVENTORY, this.itemHandler.serializeNBT());
        tag.put(NBT_CACHE, this.cache.serializeNBT());

        if( this.customName != null ) {
            tag.putString(NBT_CUSTOM_NAME, ITextComponent.Serializer.toJson(this.customName));
        }

        return tag;
    }

    @Nullable
    @Override
    public SUpdateTileEntityPacket getUpdatePacket() {
        return new SUpdateTileEntityPacket(this.worldPosition, -1, this.saveSyncNBT(new CompoundNBT()));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        this.loadSyncNBT(pkt.getTag());
    }

    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        return this.saveSyncNBT(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        this.loadSyncNBT(tag);
    }

    private void loadSyncNBT(CompoundNBT tag) {
        this.isActive = tag.getBoolean(NBT_IS_ACTIVE);
        this.currRecipeId = tag.contains(NBT_RECIPE) ? new ResourceLocation(tag.getString(NBT_RECIPE)) : null;

        if( tag.getBoolean("HasAutomationUpgrade") && !this.hasAutoUpgrade() ) {
            this.itemHandler.setItem(AssemblyInventory.SLOT_UPGRADE_AUTO, new ItemStack(ItemRegistry.ASSEMBLY_UPG_AUTO));
        }
        if( tag.getBoolean("HasSpeedUpgrade") && !this.hasSpeedUpgrade() ) {
            this.itemHandler.setItem(AssemblyInventory.SLOT_UPGRADE_SPEED, new ItemStack(ItemRegistry.ASSEMBLY_UPG_SPEED));
        }
        if( tag.getBoolean("HasFilterUpgrade") && !this.hasFilterUpgrade() ) {
            this.itemHandler.setItem(AssemblyInventory.SLOT_UPGRADE_FILTER, new ItemStack(ItemRegistry.ASSEMBLY_UPG_FILTER));
        }
        if( tag.getBoolean("HasRedstoneUpgrade") && !this.hasRedstoneUpgrade() ) {
            this.itemHandler.setItem(AssemblyInventory.SLOT_UPGRADE_REDSTONE, new ItemStack(ItemRegistry.ASSEMBLY_UPG_REDSTONE));
        }
    }

    private CompoundNBT saveSyncNBT(CompoundNBT tag) {
        tag.putBoolean(NBT_IS_ACTIVE, this.isActive);
        if( this.currRecipeId != null ) {
            tag.putString(NBT_RECIPE, this.currRecipeId.toString());
        }

        tag.putBoolean("HasAutomationUpgrade", this.hasAutoUpgrade());
        tag.putBoolean("HasSpeedUpgrade", this.hasSpeedUpgrade());
        tag.putBoolean("HasFilterUpgrade", this.hasFilterUpgrade());
        tag.putBoolean("HasRedstoneUpgrade", this.hasRedstoneUpgrade());

        return tag;
    }


//
//    @Override
//    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
//        nbt = super.writeToNBT(nbt);
//
//        this.writeNBT(nbt);
//
//        if( !this.cache.isEmpty() ) {
//            nbt.setTag("RemovedItems", this.cache.getCompound());
//        }
//
//        return nbt;
//    }
//
//    @Override
//    public void readFromNBT(NBTTagCompound nbt) {
//        super.readFromNBT(nbt);
//
//        this.readNBT(nbt);
//
//        if( nbt.hasKey("RemovedItems", Constants.NBT.TAG_LIST) ) {
//            NBTTagList remItems = nbt.getTagList("RemovedItems", Constants.NBT.TAG_COMPOUND);
//            NonNullList<ItemStack> remStacks = NonNullList.withSize(remItems.tagCount(), ItemStack.EMPTY);
//            ItemStackUtils.readItemStacksFromTag(remStacks, remItems);
//
//            this.cache.insert(remStacks);
//        }
//
//        this.doSync = true;
//    }
//
//    private NBTTagCompound writeNBT(NBTTagCompound nbt) {
//        nbt.setTag("inventory", this.itemHandler.serializeNBT());
//
//        if( this.currRecipe != null ) {
//            nbt.setString("CraftingId", this.currRecipe.getId().toString());
//            nbt.setInteger("CraftingAmount", this.craftingAmount);
//        }
//
//        nbt.setTag("cap_energy", this.energyStorage.serializeNBT());
//
//        nbt.setBoolean("isActive", this.isActive);
//        nbt.setInteger("ticksCrafted", this.ticksCrafted);
//        nbt.setInteger("maxTicksCrafted", this.maxTicksCrafted);
//        nbt.setInteger("fluxConsumption", this.fluxConsumption);
//        nbt.setBoolean("automate", this.automate);
//
//        if( this.hasCustomName() ) {
//            nbt.setString("customName", this.customName);
//        }
//
//        return nbt;
//    }
//
//    private void readNBT(NBTTagCompound nbt) {
//        this.itemHandler.deserializeNBT(nbt.getCompoundTag("inventory"));
//
//        if( nbt.hasKey("CraftingId") && nbt.hasKey("CraftingAmount") ) {
//            this.currRecipe = AssemblyManager.INSTANCE.getRecipe(new ResourceLocation(nbt.getString("CraftingId")));
//            this.craftingAmount = nbt.getInteger("CraftingAmount");
//        }
//
//        this.energyStorage.deserializeNBT(nbt.getCompoundTag("cap_energy"));
//
//        this.isActive = nbt.getBoolean("isActive");
//        this.ticksCrafted = nbt.getInteger("ticksCrafted");
//        this.maxTicksCrafted = nbt.getInteger("maxTicksCrafted");
//        this.fluxConsumption = nbt.getInteger("fluxConsumption");
//        this.automate = nbt.getBoolean("automate");
//
//        if( nbt.hasKey("customName") ) {
//            this.customName = nbt.getString("customName");
//        }
//    }

    public int getTicksCrafted() {
        return this.ticksCrafted;
    }

    public int getMaxTicksCrafted() {
        return this.maxTicksCrafted;
    }

    public int getFluxConsumption() {
        return this.fluxConsumption;
    }

    public void setAutomated(boolean b) {
        if( this.currRecipeId == null ) {
            this.automate = b;
        } else if( !b ) {
            this.cancelCrafting();
            this.automate = false;
        }
    }

    public boolean isAutomated() {
        return this.automate;
    }

    @Override
    @SuppressWarnings({"unchecked", "ObjectEquality"})
    public <T> LazyOptional<T> getCapability(Capability<T> capability, Direction facing) {
        if( capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ) {
            if( facing == Direction.DOWN ) {
                return LazyOptional.of(() -> (T) this.itemHandlerBottom);
            } else if( facing != Direction.UP ) {
                return LazyOptional.of(() -> (T) this.itemHandlerSide);
            }
        } else if( facing != Direction.UP && capability == CapabilityEnergy.ENERGY ) {
            return LazyOptional.of(() -> (T) this.energyStorage);
        }

        return super.getCapability(capability, facing);
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

    @Nullable
    @Override
    public ITextComponent getCustomName() {
        return this.customName;
    }

    public void setCustomName(ITextComponent name) {
        this.customName = name;
    }

    private static boolean isPowered(World level, BlockPos pos, Direction facing) {
        int i = level.getSignal(pos, facing);
        if (i >= 15) {
            return true;
        } else {
            BlockState blockstate = level.getBlockState(pos.relative(facing));

            return Math.max(i, blockstate.is(Blocks.REDSTONE_WIRE) ? blockstate.getValue(RedstoneWireBlock.POWER) : 0) > 0;
        }
    }

    @Nullable
    @Override
    public Container createMenu(int id, @Nonnull PlayerInventory playerInventory, @Nonnull PlayerEntity playerEntity) {
        return new TurretAssemblyContainer(id, playerInventory, this, this.syncData);
    }

    @Override
    public ITextComponent getDisplayName() {
        return this.customName != null ? this.customName : this.getBlockState().getBlock().getName();
    }
}
