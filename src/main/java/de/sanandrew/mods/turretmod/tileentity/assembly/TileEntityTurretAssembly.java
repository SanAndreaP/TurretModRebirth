/*
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity.assembly;

import de.sanandrew.mods.sanlib.lib.Tuple;
import de.sanandrew.mods.sanlib.lib.util.ItemStackUtils;
import de.sanandrew.mods.sanlib.lib.util.MiscUtils;
import de.sanandrew.mods.turretmod.api.TmrConstants;
import de.sanandrew.mods.turretmod.network.PacketSyncTileEntity;
import de.sanandrew.mods.turretmod.network.TileClientSync;
import de.sanandrew.mods.turretmod.registry.assembly.TurretAssemblyRegistry;
import de.sanandrew.mods.turretmod.util.EnumParticle;
import de.sanandrew.mods.turretmod.util.TurretModRebirth;
import io.netty.buffer.ByteBuf;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;

import java.util.UUID;

public class TileEntityTurretAssembly
        extends TileEntity
        implements TileClientSync, ITickable
{
    public static final int MAX_FLUX_STORAGE = 75_000;
    public static final int MAX_FLUX_INSERT = 500;

    public float robotArmX;
    public float robotArmY;
    public float prevRobotArmX;
    public float prevRobotArmY;
    public float robotMotionX;
    public float robotMotionY;
    public float robotEndX;
    public float robotEndY;
    public Tuple spawnParticle;

    private boolean prevActive;
    boolean automate;
    public boolean isActive;
    private boolean isActiveClient;

    public Tuple currCrafting;
    int ticksCrafted;
    int maxTicksCrafted;
    int fluxConsumption;

    boolean doSync = false;

    private long ticksExisted;
    private String customName;

    AssemblyEnergyStorage energyStorage;
    AssemblyInventoryHandler invHandler;

    final IItemHandler itemHandlerBottom;
    final IItemHandler itemHandlerSide;

    public TileEntityTurretAssembly() {
        this.robotArmX = 2.0F;
        this.robotArmY = -9.0F;
        this.robotMotionX = 0.0F;
        this.robotMotionY = 0.0F;
        this.fluxConsumption = 0;
        this.ticksCrafted = 0;
        this.maxTicksCrafted = 0;
        this.ticksExisted = 0L;

        this.energyStorage = new AssemblyEnergyStorage();
        this.invHandler = new AssemblyInventoryHandler(this);
        this.itemHandlerBottom = new SidedInvWrapper(this.invHandler, EnumFacing.DOWN);
        this.itemHandlerSide = new SidedInvWrapper(this.invHandler, EnumFacing.WEST);
    }

    public void beginCrafting(UUID recipe, int count) {
        if( this.currCrafting != null && recipe.equals(this.currCrafting.getValue(0)) && !this.automate ) {
            ItemStack result = TurretAssemblyRegistry.INSTANCE.getRecipeResult(recipe);
            ItemStack currCrfStack = this.currCrafting.getValue(1);
            if( currCrfStack.getCount() + count < 1 ) {
                this.cancelCrafting();
            } else if( ItemStackUtils.isValid(result) && currCrfStack.getCount() + count * result.getCount() <= currCrfStack.getMaxStackSize() ) {
                currCrfStack.grow(count);
                this.doSync = true;
            } else {
                currCrfStack.setCount(currCrfStack.getMaxStackSize());
                this.doSync = true;
            }
        } else if( this.currCrafting == null ) {
            ItemStack stackRes = TurretAssemblyRegistry.INSTANCE.getRecipeResult(recipe);
            TurretAssemblyRegistry.RecipeEntry entry = TurretAssemblyRegistry.INSTANCE.getRecipeEntry(recipe);
            if( entry != null && ItemStackUtils.isValid(stackRes) ) {
                stackRes = stackRes.copy();
                stackRes.setCount(this.automate ? 1 : count);
                this.currCrafting = new Tuple(recipe, stackRes);
                this.maxTicksCrafted = entry.ticksProcessing;
                this.doSync = true;
            }
        }
    }

    public void cancelCrafting() {
        this.currCrafting = null;
        this.ticksCrafted = 0;
        this.fluxConsumption = 0;
        this.maxTicksCrafted = 0;
        this.isActive = false;
        this.isActiveClient = false;
        this.doSync = true;
    }

    private void initCrafting() {
        if( this.currCrafting != null && this.invHandler.canFillOutput() ) {
            UUID currCrfUUID = this.currCrafting.getValue(0);
            ItemStack addStacks = this.currCrafting.<ItemStack>getValue(1).copy();
            ItemStack recipe = TurretAssemblyRegistry.INSTANCE.getRecipeResult(currCrfUUID);
            if( ItemStackUtils.isValid(recipe) ) {
                addStacks.setCount(recipe.getCount());
                if( this.invHandler.canFillOutput(addStacks) && TurretAssemblyRegistry.INSTANCE.checkAndConsumeResources(this.invHandler, currCrfUUID) ) {
                    TurretAssemblyRegistry.RecipeEntry currentlyCrafted = TurretAssemblyRegistry.INSTANCE.getRecipeEntry(currCrfUUID);
                    if( currentlyCrafted != null ) {
                        this.maxTicksCrafted = currentlyCrafted.ticksProcessing;
                        this.fluxConsumption = MathHelper.ceil(currentlyCrafted.fluxPerTick * (this.hasSpeedUpgrade() ? 1.1F : 1.0F));
                        this.ticksCrafted = 0;
                        this.isActive = true;
                        this.doSync = true;
                    }
                }
            } else {
                this.cancelCrafting();
            }
        }
    }

    public boolean hasAutoUpgrade() {
        return this.invHandler.hasAutoUpgrade();
    }

    public boolean hasSpeedUpgrade() {
        return this.invHandler.hasSpeedUpgrade();
    }

    public boolean hasFilterUpgrade() {
        return this.invHandler.hasFilterUpgrade();
    }

    public NonNullList<ItemStack> getFilterStacks() {
        return this.invHandler.getFilterStacks();
    }

    @Override
    public void update() {
        if( !this.world.isRemote ) {
            if( this.automate && !this.hasAutoUpgrade() ) {
                this.automate = false;
                this.cancelCrafting();
            }

            if( this.energyStorage.hasFluxChanged() ) {
                this.doSync = true;
            }
            this.energyStorage.updatePrevFlux();

            int maxLoop = this.hasSpeedUpgrade() ? 4 : 1;
            boolean markDirty = false;

            for( int i = 0; i < maxLoop; i++ ) {
                this.isActiveClient = this.isActive;
                if( this.isActive && this.currCrafting != null ) {
                    if( this.energyStorage.fluxAmount >= this.fluxConsumption && this.world.isBlockIndirectlyGettingPowered(this.pos) == 0 ) {
                        this.energyStorage.fluxAmount -= this.fluxConsumption;
                        if( ++this.ticksCrafted >= this.maxTicksCrafted ) {
                            ItemStack stack = TurretAssemblyRegistry.INSTANCE.getRecipeResult(this.currCrafting.getValue(0));
                            if( !ItemStackUtils.isValid(stack) ) {
                                this.cancelCrafting();
                                return;
                            }

                            this.invHandler.fillOutput(stack);

                            if( !this.invHandler.canFillOutput(stack) ) {
                                this.isActive = false;
                                this.isActiveClient = false;
                            }

                            if( !TurretAssemblyRegistry.INSTANCE.checkAndConsumeResources(this.invHandler, this.currCrafting.getValue(0)) ) {
                                this.isActive = false;
                                this.isActiveClient = false;
                            }

                            if( this.currCrafting.<ItemStack>getValue(1).getCount() > 1 ) {
                                if( !this.automate ) {
                                    this.currCrafting.<ItemStack>getValue(1).shrink(1);
                                }
                            } else if( !this.automate ) {
                                this.cancelCrafting();
                            }
                            this.ticksCrafted = 0;

                            markDirty = true;
                        }

                        this.doSync = true;
                    } else {
                        this.isActiveClient = false;
                        this.doSync = true;
                    }
                } else {
                    this.initCrafting();
                    this.isActiveClient = false;
                }
            }

            if( markDirty ) {
                this.markDirty();
            }

            if( this.doSync ) {
                PacketSyncTileEntity.sync(this);
                this.doSync = false;
            }
        } else {
            this.processRobotArm();
        }

        this.prevActive = this.isActive;
        this.ticksExisted++;
    }

    private void processRobotArm() {
        this.prevRobotArmX = this.robotArmX;
        this.prevRobotArmY = this.robotArmY;

        this.robotArmX += this.robotMotionX;
        this.robotArmY += this.robotMotionY;

        if( this.robotArmX > this.robotEndX && this.robotMotionX > 0.0F ) {
            this.robotArmX = this.robotEndX;
            this.robotMotionX = 0.0F;
        } else if( this.robotArmX < this.robotEndX && this.robotMotionX < 0.0F ) {
            this.robotArmX = this.robotEndX;
            this.robotMotionX = 0.0F;
        }

        if( this.robotArmY > this.robotEndY && this.robotMotionY > 0.0F ) {
            this.robotArmY = this.robotEndY;
            this.robotMotionY = 0.0F;
        } else if( this.robotArmY < this.robotEndY && this.robotMotionY < 0.0F ) {
            this.robotArmY = this.robotEndY;
            this.robotMotionY = 0.0F;
        }

        if( this.isActiveClient && (!this.prevActive || this.ticksExisted % 20 == 0) ) {
            this.animateRobotArmRng();
        } else if( this.prevActive && !this.isActiveClient ) {
            this.animateRobotArmReset();
            this.spawnParticle = null;
        }

        if( this.isActiveClient && this.spawnParticle != null ) {
            TurretModRebirth.proxy.spawnParticle(EnumParticle.ASSEMBLY_SPARK, spawnParticle.getValue(0), spawnParticle.<Double>getValue(1) + 0.05D, spawnParticle.getValue(2), null);
            this.spawnParticle = null;
        }
    }

    private void animateRobotArmRng() {
        float endX = 4.0F + MiscUtils.RNG.randomFloat() * 6.0F;
        float endY = -3.5F + MiscUtils.RNG.randomFloat() * -6.0F;

        this.robotMotionX = (0.1F + MiscUtils.RNG.randomFloat() * 0.1F) * (endX > this.robotArmX ? 1.0F : -1.0F);
        this.robotMotionY = (0.1F + MiscUtils.RNG.randomFloat() * 0.1F) * (endY > this.robotArmY ? 1.0F : -1.0F);
        this.robotEndX = endX;
        this.robotEndY = endY;
    }

    private void animateRobotArmReset() {
        float endX = 2.0F;
        float endY = -9.0F;

        this.robotMotionX = (0.1F + MiscUtils.RNG.randomFloat() * 0.1F) * (endX > this.robotArmX ? 1.0F : -1.0F);
        this.robotMotionY = (0.1F + MiscUtils.RNG.randomFloat() * 0.1F) * (endY > this.robotArmY ? 1.0F : -1.0F);
        this.robotEndX = endX;
        this.robotEndY = endY;
    }

    public IInventory getInventory() {
        return this.invHandler;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        this.readNBT(pkt.getNbtCompound());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return this.writeNBT(super.getUpdateTag());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        super.handleUpdateTag(tag);
        this.readFromNBT(tag);
    }

    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(this.pos, 0, this.writeNBT(new NBTTagCompound()));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        this.writeNBT(nbt);

        return nbt;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        this.readNBT(nbt);

        this.doSync = true;
    }

    private NBTTagCompound writeNBT(NBTTagCompound nbt) {
        nbt.setTag("inventory", this.invHandler.serializeNBT());

        if( this.currCrafting != null ) {
            nbt.setString("craftingUUID", this.currCrafting.getValue(0).toString());
            ItemStackUtils.writeStackToTag(this.currCrafting.getValue(1), nbt, "craftingStack");
        }

        nbt.setTag("cap_energy", this.energyStorage.serializeNBT());

        nbt.setBoolean("isActive", this.isActive);
        nbt.setInteger("ticksCrafted", this.ticksCrafted);
        nbt.setInteger("maxTicksCrafted", this.maxTicksCrafted);
        nbt.setInteger("fluxConsumption", this.fluxConsumption);
        nbt.setBoolean("automate", this.automate);

        if( this.hasCustomName() ) {
            nbt.setString("customName", this.customName);
        }

        return nbt;
    }

    private void readNBT(NBTTagCompound nbt) {
        this.invHandler.deserializeNBT(nbt.getCompoundTag("inventory"));

        if( nbt.hasKey("craftingUUID") && nbt.hasKey("craftingStack") ) {
            this.currCrafting = new Tuple(UUID.fromString(nbt.getString("craftingUUID")), new ItemStack(nbt.getCompoundTag("craftingStack")));
        }

        this.energyStorage.deserializeNBT(nbt.getCompoundTag("cap_energy"));

        this.isActive = nbt.getBoolean("isActive");
        this.ticksCrafted = nbt.getInteger("ticksCrafted");
        this.maxTicksCrafted = nbt.getInteger("maxTicksCrafted");
        this.fluxConsumption = nbt.getInteger("fluxConsumption");
        this.automate = nbt.getBoolean("automate");

        if( nbt.hasKey("customName") ) {
            this.customName = nbt.getString("customName");
        }
    }

    public ITextComponent getDisplayName() {
        return this.hasCustomName() ? new TextComponentString(this.getCustomName()) : new TextComponentTranslation(this.getCustomName());
    }

    public int getTicksCrafted() {
        return this.ticksCrafted;
    }

    public int getMaxTicksCrafted() {
        return this.maxTicksCrafted;
    }

    public int getFluxConsumption() {
        return this.fluxConsumption;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.energyStorage.fluxAmount);
        buf.writeInt(this.fluxConsumption);
        buf.writeBoolean(this.isActive);
        buf.writeInt(this.ticksCrafted);
        buf.writeInt(this.maxTicksCrafted);
        buf.writeBoolean(this.automate);
        buf.writeBoolean(this.isActiveClient);
        if( this.currCrafting != null ) {
            ByteBufUtils.writeItemStack(buf, this.currCrafting.getValue(1));
            ByteBufUtils.writeUTF8String(buf, this.currCrafting.getValue(0).toString());
        } else {
            ByteBufUtils.writeItemStack(buf, ItemStack.EMPTY);
        }
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.energyStorage.fluxAmount = buf.readInt();
        this.fluxConsumption = buf.readInt();
        this.isActive = buf.readBoolean();
        this.ticksCrafted = buf.readInt();
        this.maxTicksCrafted = buf.readInt();
        this.automate = buf.readBoolean();
        this.isActiveClient = buf.readBoolean();
        ItemStack crfStack = ByteBufUtils.readItemStack(buf);
        if( ItemStackUtils.isValid(crfStack) ) {
            this.currCrafting = new Tuple(UUID.fromString(ByteBufUtils.readUTF8String(buf)), crfStack);
        } else {
            this.currCrafting = null;
        }
    }

    public void setAutomated(boolean b) {
        if( this.currCrafting == null ) {
            this.automate = b;
            this.doSync = true;
        }
    }

    public boolean isAutomated() {
        return this.automate;
    }

    @Override
    public TileEntity getTile() {
        return this;
    }

    @Override
    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if( capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ) {
            if( facing == EnumFacing.DOWN ) {
                return (T) itemHandlerBottom;
            } else if( facing != EnumFacing.UP ) {
                return (T) itemHandlerSide;
            }
        } else if( facing != EnumFacing.UP && capability == CapabilityEnergy.ENERGY ) {
            return (T) energyStorage;
        }

        return null;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if( facing != EnumFacing.UP ) {
            return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
        }

        return super.hasCapability(capability, facing);
    }

    public void setCustomName(String customName) {
        this.customName = customName;
    }

    public String getCustomName() {
        return this.hasCustomName() ? this.customName : TmrConstants.ID + ".container.assembly";
    }

    public boolean hasCustomName() {
        return this.customName != null;
    }
}
