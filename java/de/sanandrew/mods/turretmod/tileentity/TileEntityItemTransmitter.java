/**
 * ****************************************************************************************************************
 * Authors:   SanAndreasP
 * Copyright: SanAndreasP
 * License:   Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International
 * http://creativecommons.org/licenses/by-nc-sa/4.0/
 * *****************************************************************************************************************
 */
package de.sanandrew.mods.turretmod.tileentity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.helpers.ItemUtils;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.turretmod.api.Turret;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.ForgeDirection;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TileEntityItemTransmitter
        extends TileEntity
        implements ISidedInventory
{
    private static final int MAX_SEC_TIMEOUT = 5;

    private Request<?> currentRequest = null;
    private int currRequestIndex = -1;
    private int currRequestTimeout = 0;

//    private Table<Integer, Turret, Request<?>> requestQueue = HashBasedTable.create();
    private RequestQueueMap<Turret, Request> requestQueue = new RequestQueueMap<>();

    private ItemStack bufItem;
    private int ticksExisted = 0;

    @SideOnly(Side.CLIENT)
    public int renderPass;

    public boolean hasRequests() {
        return this.requestQueue.size() > 0;
    }

    public boolean hasTurretRequested(Turret turret) {
        return this.hasRequests() && this.requestQueue.containsKey(turret);
    }

    public boolean requestItem(Turret turret, Request request) {
        if( !this.hasTurretRequested(turret) ) {
            this.requestQueue.put(turret, request);
//            this.requestingTurret = turret;
//            this.currentRequest = request;
//            this.requestTimeout = MAX_SEC_TIMEOUT;
//            this.bannedTurret = null;

            this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);

            return true;
        }

        return false;
    }

    public void removeRequest(Turret turret) {
        if( this.hasTurretRequested(turret) ) {
            this.requestQueue.remove(turret);
        }
    }

    @Override
    public void updateEntity() {
        if( !this.worldObj.isRemote ) {
            if( this.ticksExisted % 20 == 0 ) {
                if( this.requestQueue.size() > 0 ) {
                    if( this.currRequestIndex >= 0 && this.currentRequest != null ) {
                        this.currRequestTimeout--;
                        if( this.currRequestTimeout <= 0 || this.currentRequest.turret == null || !this.currentRequest.turret.getEntity().isEntityAlive() ) {
                            this.currRequestIndex += 1;
                            if( this.currRequestIndex >= this.requestQueue.size() ) {
                                this.currRequestIndex = 0;
                            }

                            this.currentRequest = this.requestQueue.getValue(this.currRequestIndex);
                            this.currRequestTimeout = MAX_SEC_TIMEOUT;
                        }
                    } else if( this.bufItem != null ) {

                    }
                }
//                if( this.hasRequest() ) {
//                    this.requestTimeout--;
//
//                    if( this.requestTimeout <= 0 || this.requestingTurret == null || !this.requestingTurret.getEntity().isEntityAlive() ) {
//                        this.bannedTurret = this.requestingTurret;
//                        this.banTimeout = MAX_SEC_TIMEOUT;
//                        this.updateRequestAndItem(0, 0);
//                    } else if( this.bufItem != null ) {
//                        switch( this.currentRequest.group ) {
//                            case Request.AMMO:
//                                EntityLiving el = this.requestingTurret.getEntity();
//                                TurretMod.particleProxy.spawnParticle(this.xCoord + 0.5D, this.yCoord + 0.8D, this.zCoord + 0.5D, this.worldObj.provider.dimensionId,
//                                                                      ParticleProxy.ITEM_TRANSMITTER, Triplet.with(el.posX, el.posY + el.getEyeHeight(), el.posZ)
//                                                                     );
//                                int removed = this.requestingTurret.addAmmo(this.bufItem);
//                                this.updateRequestAndItem(removed, this.requestingTurret.getMaxAmmo() - this.requestingTurret.getAmmo());
//                                break;
//                            default:
//                                this.updateRequestAndItem(0, 0);
//                        }
//                    } else {
//                        this.updateRequestAndItem(0, this.requestingTurret.getMaxAmmo() - this.requestingTurret.getAmmo());
//                    }
//
//                    PacketSendTransmitterExpTime.sendToAllAround(this);
//                } else if( !this.hasRequest() && this.bufItem != null ) {
//                    this.updateRequestAndItem(0, 0);
//                }
//
//                if( --this.banTimeout <= 0 ) {
//                    this.bannedTurret = null;
//                }
            }
        }

        this.ticksExisted++;
    }

    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound packetNBT = new NBTTagCompound();
        if( this.currentRequest == null ) {
            packetNBT.setByte(Request.NBT_REQ_GROUP, (byte) -1);
        } else {
            Request.writeToNbt(packetNBT, this.currentRequest);
//                NBTTagCompound itemNbt = new NBTTagCompound();
//                saveStack(itemNbt, this.requestItem);
//                packetNBT.setInteger("turretId", this.requestingTurret.getEntity().getEntityId());
//                packetNBT.setInteger("timeout", this.requestTimeout);
//                packetNBT.setTag("item", itemNbt);
        }

        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 0, packetNBT);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound data = pkt.func_148857_g();
        if( data.getByte(Request.NBT_REQ_GROUP) != -1 ) {
            this.currentRequest = Request.readFromNbt(data);
        } else {
            this.currentRequest = null;
        }
        //Request.VALUES[data.getByte("currentRequest")];
//        if( this.currentRequest != Request.NONE ) {
//            this.requestingTurret = (Turret) this.worldObj.getEntityByID(data.getInteger("turretId"));
//            this.requestTimeout = data.getInteger("timeout");
//            this.requestItem = readStack(data.getCompoundTag("item"));
//        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);

        if( this.bufItem != null ) {
            nbt.setTag("bufferItem", this.bufItem.writeToNBT(new NBTTagCompound()));
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if( nbt.hasKey("bufferItem") ) {
            this.bufItem = ItemStack.loadItemStackFromNBT(nbt.getCompoundTag("bufferItem"));
        }
    }

    @Override
    public boolean canUpdate() {
        return true;
    }

//    private void updateRequestAndItem(int removed, Number stillNeeded) {
//        if( removed > 0 || stillNeeded > 0 ) {
//            if( !this.hasRequest() ) {
//                this.updateRequestAndItem(0, 0);
//                return;
//            }
//
//            if( removed > 0 ) {
//                this.requestingTurret.getEntity().playSound(TurretMod.MOD_ID + ":collect.ia_get", 1.0F, 1.0F);
//                this.bufItem.stackSize -= removed;
//                if( this.bufItem.stackSize <= 0 ) {
//                    this.bufItem = null;
//                }
//
//                this.requestTimeout = MAX_SEC_TIMEOUT;
//            }
//
//            this.requestItem.stackSize = stillNeeded;
//
//            if( this.requestItem.stackSize <= 0 ) {
//                this.currentRequest = Request.NONE;
//                this.requestingTurret = null;
//            }
//        } else {
//            if( this.bufItem != null ) {
//                for( ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS ) {
//                    if( dir == ForgeDirection.UP ) {
//                        continue;
//                    }
//
//                    TileEntity te = this.worldObj.getTileEntity(this.xCoord + dir.offsetX, this.yCoord + dir.offsetY, this.zCoord + dir.offsetZ);
//                    if( te instanceof IInventory ) {
//                        this.bufItem = InventoryUtils.addStackToInventory(this.bufItem, (IInventory) te);
//                        if( this.bufItem == null ) {
//                            break;
//                        }
//                    }
//                }
//
//                if( this.bufItem != null ) {
//                    for( ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS ) {
//                        if( dir == ForgeDirection.UP ) {
//                            continue;
//                        }
//
//                        int blockX = this.xCoord + dir.offsetX;
//                        int blockY = this.yCoord + dir.offsetY;
//                        int blockZ = this.zCoord + dir.offsetZ;
//
//                        if( !this.worldObj.getBlock(blockX, blockY, blockZ).isNormalCube(this.worldObj, blockX, blockY, blockZ) ) {
//                            this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, blockX + 0.5D, blockY + 0.5D, blockZ + 0.5D, this.bufItem));
//                            this.bufItem = null;
//                            break;
//                        }
//                    }
//
//                    if( this.bufItem != null ) {
//                        this.worldObj.spawnEntityInWorld(new EntityItem(this.worldObj, this.xCoord + 0.5D, this.yCoord + 1.5D, this.zCoord + 0.5D, this.bufItem));
//                        this.bufItem = null;
//                    }
//                }
//            }
//
//            this.currentRequest = Request.NONE;
//            this.requestingTurret = null;
//        }
//
//        this.worldObj.markBlockForUpdate(this.xCoord, this.yCoord, this.zCoord);
//    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return this.bufItem;
    }

    @Override
    public ItemStack decrStackSize(int slot, int amount) {
        return this.bufItem;
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack stack) {
        if( stack != null && this.hasRequest() ) {
            this.bufItem = stack;
            if( stack.stackSize > this.getInventoryStackLimit() ) {
                stack.stackSize = this.getInventoryStackLimit();
            }
        }
    }

    @Override
    public String getInventoryName() {
        return null;
    }

    @Override
    public boolean hasCustomInventoryName() {
        return false;
    }

    @Override
    public int getInventoryStackLimit() {
        return this.hasRequest() ? this.requestItem.stackSize : 0;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return false;
    }

    @Override
    public void openInventory() {
    }

    @Override
    public void closeInventory() {
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return this.hasRequest() && ItemUtils.areStacksEqual(stack, this.requestItem, true) && this.requestItem.stackSize > 0;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        ForgeDirection dir = ForgeDirection.getOrientation(side);
        return dir != ForgeDirection.UP ? new int[]{0} : new int[0];
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return ForgeDirection.getOrientation(side) != ForgeDirection.UP && isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return false;
    }

    public ItemStack getRequestItem() {
        return this.requestItem;
    }

    public Request getRequest() {
        return this.currentRequest;
    }

    public Turret getRequestingTurret() {
        return this.requestingTurret;
    }

    /**
     * Taken from {@link ItemStack#writeToNBT(NBTTagCompound)} - {@link ItemStack#stackSize} will be written as short instead of byte here.
     * @param nbt the NBTTagCompound the item will be saved in
     * @param stack the stack to be saved
     */
    private static void saveStack(NBTTagCompound nbt, ItemStack stack) {
        nbt.setShort("id", (short) Item.getIdFromItem(stack.getItem()));
        nbt.setShort("Count", (short) stack.stackSize);
        nbt.setShort("Damage", (short) stack.getItemDamage());

        if( stack.stackTagCompound != null ) {
            nbt.setTag("tag", stack.stackTagCompound);
        }
    }

    /**
     * Taken from {@link ItemStack#readFromNBT(NBTTagCompound)} - {@link ItemStack#stackSize} will be read as short instead of byte here.
     * @param nbt the NBTTagCompound the item will be loaded from
     * @return the loaded stack
     */
    private static ItemStack readStack(NBTTagCompound nbt) {
        ItemStack stack = new ItemStack(Blocks.air);
        stack.func_150996_a(Item.getItemById(nbt.getShort("id")));
        stack.stackSize = nbt.getShort("Count");
        stack.setItemDamage(nbt.getShort("Damage"));

        if( nbt.hasKey("tag", NBT.TAG_COMPOUND) ) {
            stack.stackTagCompound = nbt.getCompoundTag("tag");
        }

        return stack;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean shouldRenderInPass(int pass) {
        this.renderPass = pass;
        return pass <= 1;
    }

    private static class RequestQueueMap<K, V>
            extends LinkedHashMap<K, V>
    {
        public V getValue(int i) {
            Map.Entry<K, V>entry = this.getEntry(i);
            if(entry == null) return null;

            return entry.getValue();
        }

        public Map.Entry<K, V> getEntry(int i) {
            // check if negetive index provided
            Set<Map.Entry<K,V>> entries = entrySet();
            int j = 0;

            for( Map.Entry<K, V> entry : entries ) {
                if( j++ == i ) {
                    return entry;
                }
            }

            return null;
        }
    }

    public static class Request<T extends Number>
    {
        public static final String NBT_REQ_GROUP = "requestType";

        public static final byte AMMO = 0;
        public static final byte HEAL = 1;

        public final UUID uuid;
        public final byte group;
        public final Turret turret;
        public T amount;

        private Request(byte group, UUID uuid, Turret turret, T amount) {
            this.uuid = uuid;
            this.group = group;
            this.amount = amount;
            this.turret = turret;
        }

        public void sendRequestedItem(ItemStack stack) {

        }

        public static void writePacket(S35PacketUpdateTileEntity packet, Request<?> type) {
            NBTTagCompound nbt = packet.func_148857_g();

            if( nbt == null ) {
                return;
            }

            nbt.setByte(NBT_REQ_GROUP, type.group);
            nbt.setString("requestUuid", type.uuid.toString());
            nbt.setInteger("turretEID", type.turret.getEntity().getEntityId());

            try( ByteArrayOutputStream bos = new ByteArrayOutputStream();
                 ObjectOutputStream oos = new ObjectOutputStream(bos);
            ) {
                oos.writeObject(type.amount);
                nbt.setByteArray("requestValue", bos.toByteArray());
            } catch( IOException e ) {
                e.printStackTrace();
            }
        }

        public static <T extends Number> Request<?> readPacket(S35PacketUpdateTileEntity packet, World world) {
            NBTTagCompound nbt = packet.func_148857_g();

            if( nbt == null ) {
                return null;
            }

            byte group = nbt.getByte(NBT_REQ_GROUP);
            UUID uuid = UUID.fromString(nbt.getString("requestUuid"));
            Turret turret = (Turret) world.getEntityByID(nbt.getInteger("rturretEID"))
            T val = null;

            try( ByteArrayInputStream bis = new ByteArrayInputStream(nbt.getByteArray("requestValue"));
                 ObjectInputStream ois = new ObjectInputStream(bis);
            ) {
                val = SAPUtils.getCasted(ois.readObject());
            } catch( IOException | ClassNotFoundException e ) {
                e.printStackTrace();
            }

            if( val == null ) {
                return null;
            }

            return new Request<>(group, uuid, turret, val);
        }

        public static class RequestAmmo
                extends Request<Integer>
        {
            public RequestAmmo(UUID type, Turret turret, int amount) {
                super(AMMO, type, turret, amount);
            }

            @Override
            public void sendRequestedItem(ItemStack stack) {
                super.sendRequestedItem(stack);
            }
        }

        public static class RequestHeal
                extends Request<Float>
        {
            public RequestHeal(UUID type, Turret turret, float amount) {
                super(HEAL, type, turret, amount);
            }
        }
    }
}
