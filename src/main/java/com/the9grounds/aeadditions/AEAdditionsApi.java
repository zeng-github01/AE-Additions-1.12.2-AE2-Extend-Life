package com.the9grounds.aeadditions;

import java.util.ArrayList;
import java.util.List;

import appeng.api.networking.security.IActionSource;
import appeng.api.storage.*;

import com.the9grounds.aeadditions.api.*;
import com.the9grounds.aeadditions.api.gas.IAEGasStack;
import com.the9grounds.aeadditions.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import net.minecraftforge.fml.common.Optional;

import appeng.api.AEApi;
import appeng.api.implementations.tiles.IWirelessAccessPoint;
import appeng.api.networking.IGrid;
import appeng.api.networking.IGridHost;
import appeng.api.networking.IGridNode;
import appeng.api.networking.storage.IStorageGrid;
import appeng.api.storage.data.IAEFluidStack;
import appeng.api.util.AEPartLocation;
import com.the9grounds.aeadditions.api.definitions.IBlockDefinition;
import com.the9grounds.aeadditions.api.definitions.IItemDefinition;
import com.the9grounds.aeadditions.api.definitions.IPartDefinition;
import com.the9grounds.aeadditions.definitions.BlockDefinition;
import com.the9grounds.aeadditions.definitions.ItemDefinition;
import com.the9grounds.aeadditions.definitions.PartDefinition;
import com.the9grounds.aeadditions.integration.Integration;
import com.the9grounds.aeadditions.integration.mekanism.gas.MekanismGas;
import com.the9grounds.aeadditions.network.GuiHandler;
import com.the9grounds.aeadditions.wireless.WirelessTermRegistry;
import mekanism.api.gas.Gas;
import mekanism.api.gas.GasStack;

public class AEAdditionsApi implements IAEAdditionsAPI {

	public static final IAEAdditionsAPI instance = new AEAdditionsApi();

	private final List<Class<? extends Fluid>> blacklistShowClass = new ArrayList<Class<? extends Fluid>>();
	private final List<Fluid> blacklistShowFluid = new ArrayList<Fluid>();
	private final List<Class<? extends Fluid>> blacklistStorageClass = new ArrayList<Class<? extends Fluid>>();
	private final List<Fluid> blacklistStorageFluid = new ArrayList<Fluid>();

	private final boolean isMekanismGasEnabled = Integration.Mods.MEKANISMGAS.isEnabled();

	@Override
	public void addFluidToShowBlacklist(Class<? extends Fluid> clazz) {
		if (clazz == null || clazz == Fluid.class) {
			return;
		}
		this.blacklistShowClass.add(clazz);
	}

	@Override
	public void addFluidToShowBlacklist(Fluid fluid) {
		if (fluid == null) {
			return;
		}
		this.blacklistShowFluid.add(fluid);
	}

	@Override
	public void addFluidToStorageBlacklist(Class<? extends Fluid> clazz) {
		if (clazz == null || clazz == Fluid.class) {
			return;
		}
		this.blacklistStorageClass.add(clazz);
	}

	@Override
	public void addFluidToStorageBlacklist(Fluid fluid) {
		if (fluid == null) {
			return;
		}
		this.blacklistStorageFluid.add(fluid);
	}

	@Override
	public IBlockDefinition blocks() {
		return BlockDefinition.instance;
	}

	@Override
	public boolean canFluidSeeInTerminal(Fluid fluid) {
		if (fluid == null) {
			return false;
		}
		if (this.blacklistShowFluid.contains(fluid)) {
			return false;
		}
		for (Class<? extends Fluid> clazz : this.blacklistShowClass) {
			if (clazz.isInstance(fluid)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean canStoreFluid(Fluid fluid) {
		if (fluid == null) {
			return false;
		}
		if (this.blacklistStorageFluid.contains(fluid)) {
			return false;
		}
		for (Class<? extends Fluid> clazz : this.blacklistStorageClass) {
			if (clazz.isInstance(fluid)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getVersion() {
		return Constants.VERSION;
	}

	@Override
	public IWirelessGasFluidTermHandler getWirelessTermHandler(ItemStack is) {
		return WirelessTermRegistry.getWirelessTermHandler(is);
	}

	@Override
	public boolean isWirelessFluidTerminal(ItemStack is) {
		return WirelessTermRegistry.isWirelessItem(is);
	}

	@Override
	public IItemDefinition items() {
		return ItemDefinition.instance;
	}

	@Override
	public ItemStack openPortableFluidCellGui(EntityPlayer player, EnumHand hand, World world) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote || stack == null || stack.getItem() == null) {
			return stack;
		}
		Item item = stack.getItem();
		if (!(item instanceof IPortableFluidStorageCell)) {
			return stack;
		}
		ICellInventoryHandler<IAEFluidStack> handler = AEApi.instance().registries().cell().getCellInventory(stack, null, StorageChannels.FLUID);
		if (handler == null)
			return stack;
		IMEMonitor<IAEFluidStack> fluidInventory = new MEMonitorHandler<>(handler, StorageChannels.FLUID);
		GuiHandler.launchGui(GuiHandler.getGuiId(3), player, hand, new Object[]{fluidInventory, item});
		return stack;
	}

	@Override
	public ItemStack openPortableGasCellGui(EntityPlayer player, EnumHand hand, World world) {
		if (!isMekanismGasEnabled)
			return player.getHeldItem(hand);

		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote || stack == null || stack.getItem() == null) {
			return stack;
		}
		Item item = stack.getItem();
		if (!(item instanceof IPortableGasStorageCell)) {
			return stack;
		}
		ICellInventoryHandler<IAEGasStack> handler = AEApi.instance().registries().cell().getCellInventory(stack, null, StorageChannels.GAS);
		if (handler == null)
			return stack;
		IMEMonitor<IAEGasStack> fluidInventory = new MEMonitorHandler<>(handler, StorageChannels.GAS);
		GuiHandler.launchGui(GuiHandler.getGuiId(6), player, hand, new Object[]{fluidInventory, item});
		return stack;
	}

	@Override
	public ItemStack openWirelessFluidTerminal(EntityPlayer player, EnumHand hand, World world) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) {
			return stack;
		}
		if (!isWirelessFluidTerminal(stack)) {
			return stack;
		}
		IWirelessGasFluidTermHandler handler = getWirelessTermHandler(stack);
		if (!handler.hasPower(player, 1.0D, stack)) {
			return stack;
		}
		Long key;
		try {
			key = Long.parseLong(handler.getEncryptionKey(stack));
		} catch (Throwable ignored) {
			return stack;
		}
		return openWirelessTerminal(player, stack, world, player.getPosition(), key, 1, hand, StorageChannels.FLUID);
	}

	@Override
	public ItemStack openWirelessGasTerminal(EntityPlayer player, EnumHand hand, World world) {
		ItemStack stack = player.getHeldItem(hand);
		if (world.isRemote) {
			return stack;
		}
		if (!isWirelessFluidTerminal(stack)) {
			return stack;
		}
		IWirelessGasFluidTermHandler handler = getWirelessTermHandler(stack);
		if (!handler.hasPower(player, 1.0D, stack)) {
			return stack;
		}
		Long key;
		try {
			key = Long.parseLong(handler.getEncryptionKey(stack));
		} catch (Throwable ignored) {
			return stack;
		}
		return openWirelessTerminal(player, stack, world, player.getPosition(), key, 5, hand, StorageChannels.GAS);
	}

	private ItemStack openWirelessTerminal(EntityPlayer player, ItemStack itemStack, World world, BlockPos pos, Long key, int guiId, EnumHand hand, IStorageChannel channel) {
		if (world.isRemote) {
			return itemStack;
		}
		IGridHost securityTerminal = (IGridHost) AEApi.instance().registries().locatable().getLocatableBy(key);
		if (securityTerminal == null) {
			return itemStack;
		}
		IGridNode gridNode = securityTerminal
			.getGridNode(AEPartLocation.INTERNAL);
		if (gridNode == null) {
			return itemStack;
		}
		IGrid grid = gridNode.getGrid();
		if (grid == null) {
			return itemStack;
		}
		for (IGridNode node : grid.getMachines((Class<? extends IGridHost>) AEApi.instance().definitions().blocks().wirelessAccessPoint().maybeEntity().get())) {
			IWirelessAccessPoint accessPoint = (IWirelessAccessPoint) node
				.getMachine();
			BlockPos distance = accessPoint.getLocation().getPos().subtract(pos);
			int squaredDistance = distance.getX() * distance.getX() + distance.getY() * distance.getY() + distance.getZ() * distance.getZ();
			if (squaredDistance <= accessPoint.getRange() * accessPoint.getRange()) {
				IStorageGrid gridCache = grid.getCache(IStorageGrid.class);
				if (gridCache != null) {
					IMEMonitor fluidInventory = gridCache.getInventory(channel);
					if (fluidInventory != null) {
						GuiHandler.launchGui(GuiHandler.getGuiId(guiId), player, hand, new Object[]{fluidInventory, getWirelessTermHandler(itemStack)});
					}
				}
			}
		}
		return itemStack;
	}

	@Override
	public IPartDefinition parts() {
		return PartDefinition.instance;
	}

	@Override
	public void registerWirelessTermHandler(IWirelessGasFluidTermHandler handler) {
		WirelessTermRegistry.registerWirelessTermHandler(handler);
	}

	@Override
	public void registerWirelessFluidTermHandler(IWirelessFluidTermHandler handler) {
		registerWirelessTermHandler(handler);
	}

	/**
	 * @deprecated Incorrect spelling
	 */
	@Override
	@Deprecated
	public void registryWirelessFluidTermHandler(IWirelessFluidTermHandler handler) {
		registerWirelessFluidTermHandler(handler);
	}

	@Override
	public void registerFuelBurnTime(Fluid fuel, int burnTime) {
		FuelBurnTime.INSTANCE.registerFuel(fuel, burnTime);
	}

	@Override
	public boolean isGasStack(IAEFluidStack stack) {
		return stack != null && isGasStack(stack.getFluidStack());
	}

	@Override
	public boolean isGasStack(FluidStack stack) {
		return stack != null && isGas(stack.getFluid());
	}

	@Override
	public boolean isGas(Fluid fluid) {
		return fluid != null && isMekanismGasEnabled && checkGas(fluid);
	}

	@Override
	public Object createGasStack(IAEFluidStack stack) {
		return Integration.Mods.MEKANISMGAS.isEnabled() ? createGasFromFluidStack(stack) : null;
	}

	@Override
	public IAEFluidStack createFluidStackFromGas(Object gasStack) {
		return isMekanismGasEnabled ? createFluidStackFromGasStack(gasStack) : null;
	}

	@Override
	public Fluid getGasFluid(Object gas) {
		return isMekanismGasEnabled ? createFluidFromGas(gas) : null;
	}

	@Override
	public void addExternalStorageInterface(IExternalGasStorageHandler esh) {
		if (isMekanismGasEnabled) {
			GasStorageRegistry.addExternalStorageInterface(esh);
		}
	}

	@Override
	public IExternalGasStorageHandler getHandler(TileEntity te, EnumFacing opposite, IActionSource mySrc) {
		return isMekanismGasEnabled ? GasStorageRegistry.getHandler(te, opposite, mySrc) : null;
	}

	@Override
	public boolean isGasSystemEnabled() {
		return isMekanismGasEnabled;
	}

	@Override
	public void registerWrenchHandler(IWrenchHandler wrenchHandler) {
		WrenchUtil.addWrenchHandler(wrenchHandler);
	}

	@Optional.Method(modid = "MekanismAPI|gas")
	private IAEFluidStack createFluidStackFromGasStack(Object gasStack) {
		return gasStack instanceof GasStack ? GasUtil.createAEFluidStack((GasStack) gasStack) : null;
	}

	@Optional.Method(modid = "MekanismAPI|gas")
	private Fluid createFluidFromGas(Object gas) {
		return gas instanceof Gas ? MekanismGas.fluidGas.containsKey(gas) ? MekanismGas.fluidGas.get(gas) : null : null;
	}

	@Optional.Method(modid = "MekanismAPI|gas")
	private Object createGasFromFluidStack(IAEFluidStack stack) {
		return stack == null ? null : GasUtil.getGasStack(stack.getFluidStack());
	}

	@Optional.Method(modid = "MekanismAPI|gas")
	private boolean checkGas(Fluid fluid) {
		return fluid instanceof MekanismGas.GasFluid;
	}

}
