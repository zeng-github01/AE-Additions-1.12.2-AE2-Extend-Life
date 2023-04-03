package com.the9grounds.aeadditions.integration.opencomputers;

import li.cil.oc.api.network.*;
import li.cil.oc.api.prefab.AbstractManagedEnvironment;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import appeng.api.parts.IPartHost;
import appeng.api.util.AEPartLocation;
import com.the9grounds.aeadditions.part.gas.PartGasExport;
import com.the9grounds.aeadditions.registries.PartEnum;
import com.the9grounds.aeadditions.util.GasUtil;
import li.cil.oc.api.Network;
import li.cil.oc.api.driver.NamedBlock;
import li.cil.oc.api.internal.Database;
import li.cil.oc.api.machine.Arguments;
import li.cil.oc.api.machine.Callback;
import li.cil.oc.api.machine.Context;
import mekanism.api.gas.GasStack;

public class DriverGasExportBus extends DriverBase<PartGasExport> {

	public DriverGasExportBus() {
		super(PartEnum.GASEXPORT, Enviroment.class);
	}

	@Override
	protected ManagedEnvironment createEnvironment(IPartHost host) {
		return new Enviroment(host);
	}

	public class Enviroment extends AbstractManagedEnvironment implements NamedBlock {

		protected final TileEntity tile;
		protected final IPartHost host;

		public Enviroment(IPartHost host) {
			tile = (TileEntity) host;
			this.host = host;
			setNode(Network.newNode(this, Visibility.Network).
				withComponent("me_exportbus").
				create());
		}

		@Callback(doc = "function(side:number, [ slot:number]):table -- Get the configuration of the gas export bus pointing in the specified direction.")
		public Object[] getGasExportConfiguration(Context context, Arguments args) {
			AEPartLocation dir = AEPartLocation.fromOrdinal(args.checkInteger(0));
			if (dir == null || dir == AEPartLocation.INTERNAL) {
				return new Object[]{null, "unknown side"};
			}
			PartGasExport part = OCUtils.getPart(tile.getWorld(), tile.getPos(), dir, PartGasExport.class);
			if (part == null) {
				return new Object[]{null, "no export bus"};
			}
			int slot = args.optInteger(1, 4);
			try {
				Fluid fluid = part.filterFluids[slot];
				if (fluid == null) {
					return new Object[]{null};
				}
				return new Object[]{GasUtil.getGasStack(new FluidStack(fluid, 1000))};
			} catch (Throwable e) {
				return new Object[]{null, "Invalid slot"};
			}

		}

		@Callback(doc = "function(side:number[, slot:number][, database:address, entry:number]):boolean -- Configure the gas export bus pointing in the specified direction to export gas stacks matching the specified descriptor.")
		public Object[] setGasExportConfiguration(Context context, Arguments args) {
			AEPartLocation dir = AEPartLocation.fromOrdinal(args.checkInteger(0));
			if (dir == null || dir == AEPartLocation.INTERNAL) {
				return new Object[]{null, "unknown side"};
			}
			PartGasExport part = OCUtils.getPart(tile.getWorld(), tile.getPos(), dir, PartGasExport.class);
			if (part == null) {
				return new Object[]{null, "no export bus"};
			}
			int slot;
			String address;
			int entry;
			if (args.count() == 3) {
				address = args.checkString(1);
				entry = args.checkInteger(2);
				slot = 4;
			} else if (args.count() < 3) {
				slot = args.optInteger(1, 4);
				try {
					part.filterFluids[slot] = null;
					part.onInventoryChanged();
					context.pause(0.5);
					return new Object[]{true};
				} catch (Throwable e) {
					return new Object[]{false, "invalid slot"};
				}
			} else {
				slot = args.optInteger(1, 4);
				address = args.checkString(2);
				entry = args.checkInteger(3);
			}
			Node node = node().network().node(address);
			if (node == null) {
				throw new IllegalArgumentException("no such component");
			}
			if (!(node instanceof Component)) {
				throw new IllegalArgumentException("no such component");
			}
			Component component = (Component) node;
			Environment env = node.host();
			if (!(env instanceof Database)) {
				throw new IllegalArgumentException("not a database");
			}
			Database database = (Database) env;
			try {
				ItemStack data = database.getStackInSlot(entry - 1);
				if (data == null) {
					part.filterFluids[slot] = null;
				} else {
					GasStack fluid = GasUtil.getGasFromContainer(data);
					if (fluid == null || fluid.getGas() == null) {
						return new Object[]{false, "not a fluid container"};
					}
					part.filterFluids[slot] = GasUtil.getFluidStack(fluid).getFluid();
				}
				part.onInventoryChanged();
				context.pause(0.5);
				return new Object[]{true};
			} catch (Throwable e) {
				return new Object[]{false, "invalid slot"};
			}
		}

		@Callback(doc = "function(side:number, amount:number):boolean -- Make the gas export bus facing the specified direction perform a single export operation.")
		public Object[] exportGas(Context context, Arguments args) {
			AEPartLocation dir = AEPartLocation.fromOrdinal(args.checkInteger(0));
			if (dir == null || dir == AEPartLocation.INTERNAL) {
				return new Object[]{null, "unknown side"};
			}
			PartGasExport part = OCUtils.getPart(tile.getWorld(), tile.getPos(), dir, PartGasExport.class);
			if (part == null) {
				return new Object[]{false, "no export bus"};
			}
			if (part.getFacingGasTank() == null) {
				return new Object[]{false, "no tank"};
			}
			int amount = Math.min(args.optInteger(1, 625), 125 + part.getSpeedState() * 125);
			boolean didSomething = part.doWork(amount, 1);
			if (didSomething) {
				context.pause(0.25);
			}
			return new Object[]{didSomething};
		}

		@Override
		public String preferredName() {
			return "me_exportbus";
		}

		@Override
		public int priority() {
			return 2;
		}

	}

}
