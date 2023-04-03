package com.the9grounds.aeadditions.gridblock;

import java.util.EnumSet;

import com.the9grounds.aeadditions.api.gas.IAEGasStack;
import com.the9grounds.aeadditions.integration.Integration;
import com.the9grounds.aeadditions.integration.mekanism.gas.MEMonitorFluidGasWrapper;
import com.the9grounds.aeadditions.util.StorageChannels;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;

import appeng.api.networking.GridFlags;
import appeng.api.networking.GridNotification;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridBlock;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.parts.PartItemStack;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.util.AEColor;
import appeng.api.util.DimensionalCoord;
import com.the9grounds.aeadditions.part.PartECBase;

public class ECBaseGridBlock implements IGridBlock {

	protected final PartECBase host;
	protected IGrid grid;
	protected int usedChannels;

	public ECBaseGridBlock(PartECBase host) {
		this.host = host;
	}

	@Override
	public final EnumSet<EnumFacing> getConnectableSides() {
		return EnumSet.noneOf(EnumFacing.class);
	}

	@Override
	public EnumSet<GridFlags> getFlags() {
		return EnumSet.of(GridFlags.REQUIRE_CHANNEL);
	}

	public IMEMonitor<IAEFluidStack> getFluidGasMonitor() {
		IMEMonitor<IAEGasStack> gasMonitor = getGasMonitor();
		if (gasMonitor == null)
			return null;
		return new MEMonitorFluidGasWrapper(gasMonitor);
	}

	public IMEMonitor<IAEFluidStack> getFluidMonitor() {
		IGridNode node = this.host.getGridNode();
		if (node == null) {
			return null;
		}
		IGrid grid = node.getGrid();
		if (grid == null) {
			return null;
		}
		IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
		if (storageGrid == null) {
			return null;
		}
		return storageGrid.getInventory(StorageChannels.FLUID);

	}

	public IMEMonitor<IAEGasStack> getGasMonitor() {
		if (!Integration.Mods.MEKANISMGAS.isEnabled())
			return null;

		IGridNode node = this.host.getGridNode();
		if (node == null) {
			return null;
		}
		IGrid grid = node.getGrid();
		if (grid == null) {
			return null;
		}
		IStorageGrid storageGrid = grid.getCache(IStorageGrid.class);
		if (storageGrid == null) {
			return null;
		}
		return storageGrid.getInventory(StorageChannels.GAS);

	}

	@Override
	public final AEColor getGridColor() {
		return AEColor.TRANSPARENT;
	}

	@Override
	public double getIdlePowerUsage() {
		return this.host.getPowerUsage();
	}

	@Override
	public final DimensionalCoord getLocation() {
		return this.host.getLocation();
	}

	@Override
	public IGridHost getMachine() {
		return this.host;
	}

	@Override
	public ItemStack getMachineRepresentation() {
		return this.host.getItemStack(PartItemStack.NETWORK);
	}

	@Override
	public void gridChanged() {
	}

	@Override
	public final boolean isWorldAccessible() {
		return false;
	}

	@Override
	public void onGridNotification(GridNotification notification) {
	}

	@Override
	public final void setNetworkStatus(IGrid grid, int usedChannels) {
		this.grid = grid;
		this.usedChannels = usedChannels;
	}
}
