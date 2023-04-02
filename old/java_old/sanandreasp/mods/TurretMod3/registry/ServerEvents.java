package sanandreasp.mods.TurretMod3.registry;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.player.PlayerDropsEvent;
import sanandreasp.mods.turretmod3.entity.turret.EntityTurret_Base;

import java.util.Iterator;
import java.util.Random;

public class ServerEvents {

	private Random rand = new Random();

	public ServerEvents() { }

	@SubscribeEvent
	public void onLivingDamage(LivingAttackEvent event) {
		if (event.entityLiving instanceof EntityTurret_Base && event.entityLiving.riddenByEntity != null && event.entityLiving.riddenByEntity instanceof EntityPlayer) {
			if (this.rand.nextInt(100) >= 75) {
				event.entityLiving.riddenByEntity.attackEntityFrom(event.source, event.ammount);
				event.setCanceled(true);
			}
		} else if (event.entityLiving instanceof EntityPlayer && event.entityLiving.ridingEntity != null && event.entityLiving.ridingEntity instanceof EntityTurret_Base) {
			if (this.rand.nextInt(100) < 75) {
				event.entityLiving.ridingEntity.attackEntityFrom(event.source, event.ammount);
				event.setCanceled(true);
			}
		}
	}

	@SubscribeEvent
	public void onLivingDrop(LivingDropsEvent event) {
		if (!(event.entityLiving instanceof EntityPlayer)) {
			if (event.entityLiving instanceof EntityCreeper
					&& event.source.getEntity() instanceof EntitySkeleton
					&& this.rand.nextInt(14) == 0) {
				Iterator<EntityItem> iter = event.drops.iterator();
				while(iter.hasNext()) {
					EntityItem e = iter.next();
					Item itmID = e.getEntityItem().getItem();
					if (itmID instanceof ItemRecord) {
						iter.remove();
					}
				}
				event.drops.add(new EntityItem(event.entityLiving.worldObj, event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, new ItemStack(TM3ModRegistry.turretRec1)));
			}


			String playerName = null;
			if (event.source.getEntity() instanceof EntityTurret_Base) {
				EntityTurret_Base turret = (EntityTurret_Base)event.source.getEntity();
				playerName = turret.getPlayerName();
			} else if (event.source.getEntity() instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer)event.source.getEntity();
				playerName = player.getCommandSenderName();
			}

			if (playerName != null && playerName.length() > 0) {
				Iterator<EntityItem> iter = event.drops.iterator();
				while(iter.hasNext()) {
					EntityItem eItem = iter.next();
					eItem.getEntityData().setString("TM3_PlayerName", playerName);
				}
			}
		}
	}

	@SubscribeEvent
	public void onPlayerDrops(PlayerDropsEvent event) {
		Iterator<EntityItem> iter = event.drops.iterator();
		while(iter.hasNext()) {
			EntityItem eItem = iter.next();
			eItem.getEntityData().setString("TM3_PlayerName", event.entityPlayer.getCommandSenderName());
		}
	}

	@SubscribeEvent
	public void onPlayerToss(ItemTossEvent event) {
		event.entityItem.getEntityData().setString("TM3_PlayerName", event.player.getCommandSenderName());
	}
}
