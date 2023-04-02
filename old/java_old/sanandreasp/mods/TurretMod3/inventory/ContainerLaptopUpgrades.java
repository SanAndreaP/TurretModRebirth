package sanandreasp.mods.TurretMod3.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import sanandreasp.mods.turretmod3.item.ItemTurret;
import sanandreasp.mods.turretmod3.registry.TurretInfo.TurretInfo;
import sanandreasp.mods.turretmod3.registry.TurretUpgrades.TurretUpgrades;

public class ContainerLaptopUpgrades extends Container {
	private IInventory invBlock;
	private IInventory invPlayer;

	public ContainerLaptopUpgrades(IInventory block, IInventory player) {
		this.invBlock = block;
		this.invPlayer = player;
		this.invBlock.openInventory();
        int var3 = 137;
        int var4;
        int var5;

        for (var4 = 0; var4 < 4; ++var4) {
        	this.addSlotToContainer(new Slot(this.invBlock, var4*2, 7, var3 + 18*var4) {
        		@Override
        		public boolean isItemValid(ItemStack par1ItemStack) {
        			return par1ItemStack.getItem() instanceof ItemTurret;
        		}

        		@Override
        		public int getSlotStackLimit() {
        			return 1;
        		}
        	});
        	this.addSlotToContainer(new Slot(this.invBlock, var4*2 + 1, 233, var3 + 18*var4) {
        		@Override
        		public boolean isItemValid(ItemStack par1ItemStack) {
        			return par1ItemStack.getItem() instanceof ItemTurret;
        		}

        		@Override
        		public int getSlotStackLimit() {
        			return 1;
        		}
        	});
        }

        for (var4 = 0; var4 < 8; ++var4) {
        	this.addSlotToContainer(new Slot(this.invBlock, var4 + 8, 57 + 18*var4, 56) {
        		@Override
        		public boolean isItemValid(ItemStack par1ItemStack) {
        			return TurretUpgrades.getUpgradeFromItem(par1ItemStack, TurretInfo.getTurretClass(par1ItemStack.getItemDamage())) != null;
        		}

        		@Override
        		public int getSlotStackLimit() {
        			return 1;
        		}
        	});
        }

        for (var4 = 0; var4 < 3; ++var4) {
            for (var5 = 0; var5 < 9; ++var5) {
                this.addSlotToContainer(new Slot(this.invPlayer, var5 + var4 * 9 + 9, 48 + var5 * 18, var4 * 18 + var3));
            }
        }

        for (var4 = 0; var4 < 9; ++var4) {
            this.addSlotToContainer(new Slot(this.invPlayer, var4, 48 + var4 * 18, 58 + var3));
        }
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer) {
        return this.invBlock.isUseableByPlayer(entityplayer);
	}

    public String getInvName() {
    	return this.invBlock.getInventoryName();
    }

    public String getPInvName() {
    	return this.invBlock.getInventoryName();
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer) {
        super.onContainerClosed(par1EntityPlayer);
        this.invBlock.closeInventory();
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2) {
        ItemStack itemstack = null;
        Slot slot = (Slot)this.inventorySlots.get(par2);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            boolean isTurret = itemstack1.getItem() instanceof ItemTurret;
            boolean isUpgrade = TurretUpgrades.getUpgradeFromItem(itemstack1, null) != null;

            if (par2 < 16) {
                if (!this.mergeItemStack(itemstack1, 16, 52, true)) {
                    return null;
                }
            } else if (!(isTurret && this.mergeOwnItemStack(itemstack1, 0, 8)) && !isUpgrade) {
                return null;
            } else if (!(isUpgrade && this.mergeOwnItemStack(itemstack1, 8, 16)) && !isTurret) {
            	return null;
            }

            if (itemstack1.stackSize == 0) {
                slot.putStack((ItemStack)null);
            } else {
                slot.onSlotChanged();
            }

            if (itemstack1.stackSize == itemstack.stackSize) {
                return null;
            }

            slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
        }

        return itemstack;
    }

    protected boolean mergeOwnItemStack(ItemStack par1ItemStack, int par2, int par3) {
        boolean flag1 = false;
        int k = par2;

        Slot slot;
        ItemStack itemstack1;

        if (par1ItemStack.stackSize < 1)
        	return false;

        while (k < par3) {
            slot = (Slot)this.inventorySlots.get(k);
            itemstack1 = slot.getStack();

            if (itemstack1 == null) {
            	ItemStack newIS = par1ItemStack.copy();
            	newIS.stackSize = 1;
                slot.putStack(newIS.copy());
                slot.onSlotChanged();
                par1ItemStack.stackSize -= 1;
                flag1 = true;
                break;
            } else {
                ++k;
            }
        }

        return flag1;
    }
}
