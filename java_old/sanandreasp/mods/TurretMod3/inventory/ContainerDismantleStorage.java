package sanandreasp.mods.TurretMod3.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerDismantleStorage extends Container {
	private IInventory dismStgInventory;
	private IInventory dismPlyInventory;
    private int numRows;

    public ContainerDismantleStorage(IInventory par1PlayerInventory, IInventory par2DStgInventory)
    {
        this.dismStgInventory = par2DStgInventory;
        this.dismPlyInventory = par1PlayerInventory;
        this.numRows = par2DStgInventory.getSizeInventory() / 9;
        par2DStgInventory.openInventory();
        int var3 = (this.numRows - 4) * 18;
        int var4;
        int var5;

        for (var4 = 0; var4 < this.numRows; ++var4)
        {
            for (var5 = 0; var5 < 9; ++var5)
            {
                this.addSlotToContainer(new Slot(par2DStgInventory, var5 + var4 * 9, 8 + var5 * 18, 18 + var4 * 18) {
                	@Override
                	public boolean isItemValid(ItemStack par1ItemStack) {
                		return false;
                	}
                });
            }
        }

        for (var4 = 0; var4 < 3; ++var4)
        {
            for (var5 = 0; var5 < 9; ++var5)
            {
                this.addSlotToContainer(new Slot(par1PlayerInventory, var5 + var4 * 9 + 9, 8 + var5 * 18, 103 + var4 * 18 + var3));
            }
        }

        for (var4 = 0; var4 < 9; ++var4)
        {
            this.addSlotToContainer(new Slot(par1PlayerInventory, var4, 8 + var4 * 18, 161 + var3));
        }
    }

    public String getInvName() {
    	return this.dismStgInventory.getInventoryName();
    }

    public String getPInvName() {
    	return this.dismPlyInventory.getInventoryName();
    }

    @Override
    public boolean canInteractWith(EntityPlayer par1EntityPlayer)
    {
        return this.dismStgInventory.isUseableByPlayer(par1EntityPlayer);
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int par2)
    {
        ItemStack var3 = null;
        Slot var4 = (Slot)this.inventorySlots.get(par2);

        if (var4 != null && var4.getHasStack())
        {
            ItemStack var5 = var4.getStack();
            var3 = var5.copy();

            if (par2 < this.numRows * 9)
            {
                if (!this.mergeItemStack(var5, this.numRows * 9, this.inventorySlots.size(), true))
                {
                    return null;
                }
            }
            else
            {
                return null;
            }

            if (var5.stackSize == 0)
            {
                var4.putStack(null);
            }
            else
            {
                var4.onSlotChanged();
            }
        }

        return var3;
    }

    @Override
    public void onContainerClosed(EntityPlayer par1EntityPlayer)
    {
        super.onContainerClosed(par1EntityPlayer);
        this.dismStgInventory.closeInventory();
    }
}
