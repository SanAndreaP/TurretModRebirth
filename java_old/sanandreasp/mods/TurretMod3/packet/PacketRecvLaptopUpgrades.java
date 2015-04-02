package sanandreasp.mods.TurretMod3.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import sanandreasp.mods.turretmod3.item.ItemTurret;
import sanandreasp.mods.turretmod3.tileentity.TileEntityLaptop;

public class PacketRecvLaptopUpgrades extends PacketBase {
    private int x, y, z;
    private int turretSlot, upgradeSlot;
    public PacketRecvLaptopUpgrades(){}
    public PacketRecvLaptopUpgrades(TileEntity te, int turret, int upgrade){
        x = te.xCoord;
        y = te.yCoord;
        z = te.zCoord;
        turretSlot = turret;
        upgradeSlot = upgrade;
    }

	@Override
	public void handle(EntityPlayer player) {
        TileEntity te = player.worldObj.getTileEntity(x, y, z);
        if (te != null && te instanceof TileEntityLaptop) {
            TileEntityLaptop lap = ((TileEntityLaptop)te);

            ItemStack turretItem = lap.getStackInSlot(turretSlot);
            ItemStack upgradeItem = lap.getStackInSlot(upgradeSlot);

            if (ItemTurret.isUpgradeValid(turretItem, upgradeItem, ItemTurret.getUpgItems(turretItem))) {
                ItemTurret.addUpgItem(turretItem, upgradeItem);
                lap.decrStackSize(upgradeSlot, 1);
                player.openContainer.detectAndSendChanges();
            }
        }
	}

    @Override
    public void fromBytes(ByteBuf buf) {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
        turretSlot = buf.readInt();
        upgradeSlot = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
        buf.writeInt(turretSlot);
        buf.writeInt(upgradeSlot);
    }
}
