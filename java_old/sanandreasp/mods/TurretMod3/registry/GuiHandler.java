package sanandreasp.mods.TurretMod3.registry;

import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import sanandreasp.mods.turretmod3.client.gui.GuiDismantledStorage;
import sanandreasp.mods.turretmod3.client.gui.TCU.GuiTCUInfo;
import sanandreasp.mods.turretmod3.client.gui.laptop.GuiLaptopGeneral;
import sanandreasp.mods.turretmod3.client.gui.laptop.GuiLaptopMisc;
import sanandreasp.mods.turretmod3.client.gui.laptop.GuiLaptopTargets;
import sanandreasp.mods.turretmod3.client.gui.laptop.GuiLaptopUpgrades;
import sanandreasp.mods.turretmod3.client.gui.turretInfo.GuiTInfoPG1;
import sanandreasp.mods.turretmod3.entity.EntityDismantleStorage;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;
import sanandreasp.mods.turretmod3.inventory.ContainerDismantleStorage;
import sanandreasp.mods.turretmod3.inventory.ContainerLaptop;
import sanandreasp.mods.turretmod3.inventory.ContainerLaptopUpgrades;
import sanandreasp.mods.turretmod3.packet.PacketRecvTargetListSrv;
import sanandreasp.mods.turretmod3.tileentity.TileEntityLaptop;

public class GuiHandler implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID) {
			case 0:
				Entity e = world.getEntityByID(x);
				if (e instanceof EntityTurret_Base) {
					EntityTurret_Base turret = (EntityTurret_Base)e;
					PacketRecvTargetListSrv.sendClient(turret, player);
					player.triggerAchievement(AchievementPageTM.control);
				}
				break;
			case 1:
				player.triggerAchievement(AchievementPageTM.turretInfo);
				break;
			case 2:
				Entity e1 = world.getEntityByID(x);
				if (e1 instanceof EntityDismantleStorage) {
					EntityDismantleStorage dismStg = (EntityDismantleStorage)e1;
					return new ContainerDismantleStorage(player.inventory, dismStg.inventory);
				}
				break;
			case 3: // FALL-THROUGH
			case 4: // FALL-THROUGH
			case 6:
				{
					TileEntity te = world.getTileEntity(x, y, z);
					if (te != null && te instanceof TileEntityLaptop) {
						TileEntityLaptop telap = (TileEntityLaptop) te;
						return new ContainerLaptop(telap, player.inventory);
					}
				}
				break;
			case 5:
				{
					TileEntity te = world.getTileEntity(x, y, z);
					if (te != null && te instanceof TileEntityLaptop) {
						TileEntityLaptop telap = (TileEntityLaptop) te;
						return new ContainerLaptopUpgrades(telap, player.inventory);
					}
				}
				break;
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		switch(ID) {
			case 0: {
                Entity e = world.getEntityByID(x);
                GuiTCUInfo gui = new GuiTCUInfo();
                gui.turret = (EntityTurret_Base) e;
                return gui;
            }
			case 1:
				return new GuiTInfoPG1(0);
			case 2:
				Entity e = world.getEntityByID(x);
				return new GuiDismantledStorage((ContainerDismantleStorage) getServerGuiElement(ID, player, world, x, y, z), e);
			case 3: // FALL-THROUGH
			case 4: // FALL-THROUGH
			case 5: // FALL-THROUGH
			case 6:
				TileEntity te = world.getTileEntity(x, y, z);
				if (te != null && te instanceof TileEntityLaptop) {
                    Container container = new ContainerLaptop((TileEntityLaptop)te, player.inventory);
					if (ID == 3)
						return new GuiLaptopGeneral(container, (TileEntityLaptop)te);
					else if (ID == 4)
						return new GuiLaptopTargets(container, (TileEntityLaptop)te);
					else if (ID == 5)
						return new GuiLaptopUpgrades(new ContainerLaptopUpgrades((TileEntityLaptop)te, player.inventory), (TileEntityLaptop)te);
					else
						return new GuiLaptopMisc(container, (TileEntityLaptop)te);
				}
		}
		return null;
	}

}
