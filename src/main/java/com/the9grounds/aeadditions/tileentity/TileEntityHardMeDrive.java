package com.the9grounds.aeadditions.tileentity;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import appeng.api.storage.*;
import appeng.api.storage.data.IAEStack;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

import appeng.api.AEApi;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridNode;
import appeng.api.networking.energy.IEnergyGrid;
import appeng.api.networking.events.MENetworkCellArrayUpdate;
import appeng.api.networking.events.MENetworkEventSubscribe;
import appeng.api.networking.events.MENetworkPowerStatusChange;
import appeng.api.networking.security.IActionHost;
import appeng.api.util.AECableType;
import appeng.api.util.AEPartLocation;
import appeng.api.util.DimensionalCoord;
import com.the9grounds.aeadditions.api.IECTileEntity;
import com.the9grounds.aeadditions.container.ContainerHardMEDrive;
import com.the9grounds.aeadditions.gridblock.ECGridBlockHardMEDrive;
import com.the9grounds.aeadditions.gui.GuiHardMEDrive;
import com.the9grounds.aeadditions.inventory.IInventoryListener;
import com.the9grounds.aeadditions.inventory.InventoryPlain;
import com.the9grounds.aeadditions.models.drive.IECDrive;
import com.the9grounds.aeadditions.network.IGuiProvider;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityHardMeDrive extends TileBase implements IActionHost, IECTileEntity, ICellContainer, IInventoryListener, IECDrive, IGuiProvider {

	private int priority = 0;
	private boolean isPowerd;

	boolean isFirstGridNode = true;
	byte[] cellStatuses = new byte[3];
	List<IMEInventoryHandler> handlers = new ArrayList<IMEInventoryHandler>();
	private final ECGridBlockHardMEDrive gridBlock = new ECGridBlockHardMEDrive(this);

	private InventoryPlain inventory = new InventoryPlain("com.the9grounds.aeadditions.part.drive", 3, 1, this) {

		ICellRegistry cellRegistry = AEApi.instance().registries().cell();

		@Override
		public boolean isItemValidForSlot(int i, ItemStack itemStack) {
			return this.cellRegistry.isCellHandled(itemStack);
		}

		@Override
		protected void onContentsChanged() {
			saveData();
		}
	};

	public IInventory getInventory() {
		return inventory;
	}

	public boolean isUseableByPlayer(EntityPlayer player) {
		return this.world.getTileEntity(this.pos) == this && player.getDistanceSq((double) this.pos.getX() + 0.5D, (double) this.pos.getY() + 0.5D, (double) this.pos.getZ() + 0.5D) <= 64.0D;
	}

	IGridNode node = null;


	@Override
	public void blinkCell(int i) {

	}

	@Override
	public IGridNode getActionableNode() {
		return getGridNode(AEPartLocation.INTERNAL);
	}

	@Override
	public List<IMEInventoryHandler> getCellArray(IStorageChannel channel) {
		if (!isActive()) {
			return new ArrayList<IMEInventoryHandler>();
		}

		List<IMEInventoryHandler> channelHandlers = new ArrayList<IMEInventoryHandler>();
		this.handlers = this.updateHandlers();

		for (IMEInventoryHandler handler : handlers) {
			if(handler.getChannel() == channel)
				channelHandlers.add(handler);
		}
		return channelHandlers;
	}

	@Override
	public int getPriority() {
		return priority;
	}

	@Override
	public DimensionalCoord getLocation() {
		return new DimensionalCoord(this);
	}

	@Override
	public double getPowerUsage() {
		return 0;
	}

	@Override
	public IGridNode getGridNode(AEPartLocation location) {
		if (isFirstGridNode && hasWorld() && !getWorld().isRemote) {
			isFirstGridNode = false;
			try {
				node = AEApi.instance().grid().createGridNode(gridBlock);
				node.updateState();
			} catch (Exception e) {
				isFirstGridNode = true;
			}
		}

		return node;
	}

	@Override
	public AECableType getCableConnectionType(AEPartLocation location) {
		return AECableType.SMART;
	}

	@Override
	public void securityBreak() {

	}

	@Override
	public void saveChanges(ICellInventory<?> imeInventory) {
		this.world.markChunkDirty( this.pos, this );
	}

	//TODO
	boolean isActive() {
		return true;
	}

	public int getColorByStatus(int status) {
		switch (status) {
			case 1:
				return 0x00FF00;
			case 2:
				return 0xFFFF00;
			case 3:
				return 0xFF0000;
			default:
				return 0x000000;
		}
	}

	@Override
	public void onInventoryChanged() {
		this.handlers = updateHandlers();
		Collection<IStorageChannel<? extends IAEStack<?>>> channels = AEApi.instance().storage().storageChannels();
		for (int i = 0; i < this.cellStatuses.length; i++) {
			ItemStack stackInSlot = this.inventory.getStackInSlot(i);
			IMEInventoryHandler inventoryHandler = null;
			for(IStorageChannel channel : channels){
				inventoryHandler = AEApi.instance().registries().cell().getCellInventory(stackInSlot, null, channel);
				if(inventoryHandler != null)
					break;
			}
			ICellHandler cellHandler = AEApi.instance().registries().cell().getHandler(stackInSlot);
			if (cellHandler == null || inventoryHandler == null) {
				this.cellStatuses[i] = 0;
			} else {
				this.cellStatuses[i] = (byte) cellHandler.getStatusForCell(
					stackInSlot, (ICellInventoryHandler) inventoryHandler);
			}
		}
		IGridNode node = getGridNode(AEPartLocation.INTERNAL);
		if (node != null) {
			IGrid grid = node.getGrid();
			if (grid != null) {
				grid.postEvent(new MENetworkCellArrayUpdate());
			}
			updateBlock();
		}
		if (world != null && world.isRemote) {
			world.markBlockRangeForRenderUpdate(pos, pos);
		}
	}

	private List<IMEInventoryHandler> updateHandlers() {
		ICellRegistry cellRegistry = AEApi.instance().registries().cell();
		List<IMEInventoryHandler> handlers = new ArrayList<IMEInventoryHandler>();
		for(IStorageChannel channel : AEApi.instance().storage().storageChannels()) {
			for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
				ItemStack cell = this.inventory.getStackInSlot(i);
				if (cellRegistry.isCellHandled(cell)) {
					IMEInventoryHandler cellInventory = cellRegistry.getCellInventory(cell, null, channel);
					if (cellInventory != null) {
						handlers.add(cellInventory);
					}
				}
			}
		}
		return handlers;
	}

	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inventory.readFromNBT(tag.getTagList("inventory", 10));
		onInventoryChanged();
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("inventory", inventory.writeToNBT());
		return tag;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = new NBTTagCompound();//new NBTTagCompound());
		int i = 0;
		for (byte aCellStati : this.cellStatuses) {
			tag.setByte("status#" + i, aCellStati);
			i++;
		}
		tag.setBoolean("isPowerd", isPowerd);
		return tag;
	}

	@Override
	public int getCellCount() {
		return 3;
	}

	@Override
	public int getCellStatus(int index) {
		return cellStatuses[index];
	}

	@Override
	public boolean isPowered() {
		return isPowerd;
	}

	@SideOnly(Side.CLIENT)
	@Override
	public GuiContainer getClientGuiElement(EntityPlayer player, Object... args) {
		return new GuiHardMEDrive(player.inventory, this);
	}

	@Override
	public Container getServerGuiElement(EntityPlayer player, Object... args) {
		return new ContainerHardMEDrive(player.inventory, this);
	}

	@MENetworkEventSubscribe
	@SuppressWarnings("unused")
	public void setPower(MENetworkPowerStatusChange notUsed) {
		if (this.node != null) {
			IGrid grid = this.node.getGrid();
			if (grid != null) {
				IEnergyGrid energy = grid.getCache(IEnergyGrid.class);
				if (energy != null) {
					this.isPowerd = energy.isNetworkPowered();
				}
			}
			updateBlock();
		}
	}

	@Override
	public void handleUpdateTag(NBTTagCompound tag) {
		//super.handleUpdateTag(tag);
		isPowerd = tag.getBoolean("isPowerd");
		for (int i = 0; i < this.cellStatuses.length; i++){
			this.cellStatuses[i] = tag.getByte("status#" + i);
		}
		if(world != null && world.isRemote){
			world.markBlockRangeForRenderUpdate(getPos(), getPos());
		}
	}
}
