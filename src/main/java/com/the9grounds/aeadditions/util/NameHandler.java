package com.the9grounds.aeadditions.util;

import appeng.api.recipes.ISubItemResolver;
import appeng.api.recipes.ResolverResult;
import com.the9grounds.aeadditions.Constants;
import com.the9grounds.aeadditions.registries.BlockEnum;
import com.the9grounds.aeadditions.registries.ItemEnum;
import com.the9grounds.aeadditions.registries.PartEnum;

public class NameHandler implements ISubItemResolver {

	@Override
	public Object resolveItemByName(String namespace, String fullName) {
		if (!namespace.equals(Constants.MOD_ID)) {
			return null;
		}

		// Fluid Cells
		if (fullName.equals("fluidCell1k")) {
			return new ResolverResult(ItemEnum.FLUIDSTORAGE.getInternalName(), 0);
		}
		if (fullName.equals("fluidCell4k")) {
			return new ResolverResult(ItemEnum.FLUIDSTORAGE.getInternalName(), 1);
		}
		if (fullName.equals("fluidCell16k")) {
			return new ResolverResult(ItemEnum.FLUIDSTORAGE.getInternalName(), 2);
		}
		if (fullName.equals("fluidCell64k")) {
			return new ResolverResult(ItemEnum.FLUIDSTORAGE.getInternalName(), 3);
		}
		if (fullName.equals("fluidCell256k")) {
			return new ResolverResult(ItemEnum.FLUIDSTORAGE.getInternalName(), 4);
		}
		if (fullName.equals("fluidCell1024k")) {
			return new ResolverResult(ItemEnum.FLUIDSTORAGE.getInternalName(), 5);
		}
		if (fullName.equals("fluidCell4096k")) {
			return new ResolverResult(ItemEnum.FLUIDSTORAGE.getInternalName(), 6);
		}
		if (fullName.equals("fluidCellPortable")) {
			return new ResolverResult(ItemEnum.FLUIDSTORAGEPORTABLE.getInternalName(), 0);
		}

		// Gas Cells
		if (fullName.equals("gasCell1k")) {
			return new ResolverResult(ItemEnum.GASSTORAGE.getInternalName(), 0);
		}
		if (fullName.equals("gasCell4k")) {
			return new ResolverResult(ItemEnum.GASSTORAGE.getInternalName(), 1);
		}
		if (fullName.equals("gasCell16k")) {
			return new ResolverResult(ItemEnum.GASSTORAGE.getInternalName(), 2);
		}
		if (fullName.equals("gasCell64k")) {
			return new ResolverResult(ItemEnum.GASSTORAGE.getInternalName(), 3);
		}
		if (fullName.equals("gasCell256k")) {
			return new ResolverResult(ItemEnum.GASSTORAGE.getInternalName(), 4);
		}
		if (fullName.equals("gasCell1024k")) {
			return new ResolverResult(ItemEnum.GASSTORAGE.getInternalName(), 5);
		}
		if (fullName.equals("gasCell4096k")) {
			return new ResolverResult(ItemEnum.GASSTORAGE.getInternalName(), 6);
		}
		if (fullName.equals("gasCellPortable")) {
			return new ResolverResult(ItemEnum.GASSTORAGEPORTABLE.getInternalName(), 0);
		}

		// Physical Cells
		if (fullName.equals("physCell256k")) {
			return new ResolverResult(ItemEnum.PHYSICALSTORAGE.getInternalName(), 0);
		}
		if (fullName.equals("physCell1024k")) {
			return new ResolverResult(ItemEnum.PHYSICALSTORAGE.getInternalName(), 1);
		}
		if (fullName.equals("physCell4096k")) {
			return new ResolverResult(ItemEnum.PHYSICALSTORAGE.getInternalName(), 2);
		}
		if (fullName.equals("physCell16384k")) {
			return new ResolverResult(ItemEnum.PHYSICALSTORAGE.getInternalName(), 3);
		}
		if (fullName.equals("physCellContainer")) {
			return new ResolverResult(ItemEnum.PHYSICALSTORAGE.getInternalName(), 4);
		}

		// Fluid Storage Components
		if (fullName.equals("cell1kPartFluid")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 4);
		}
		if (fullName.equals("cell4kPartFluid")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 5);
		}
		if (fullName.equals("cell16kPartFluid")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 6);
		}
		if (fullName.equals("cell64kPartFluid")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 7);
		}
		if (fullName.equals("cell256kPartFluid")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 8);
		}
		if (fullName.equals("cell1024kPartFluid")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 9);
		}
		if (fullName.equals("cell4096kPartFluid")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 10);
		}

		// Gas Storage Components
		if (fullName.equals("cell1kPartGas")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 11);
		}
		if (fullName.equals("cell4kPartGas")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 12);
		}
		if (fullName.equals("cell16kPartGas")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 13);
		}
		if (fullName.equals("cell64kPartGas")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 14);
		}
		if (fullName.equals("cell256kPartGas")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 15);
		}
		if (fullName.equals("cell1024kPartGas")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 16);
		}
		if (fullName.equals("cell4096kPartGas")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 17);
		}

		// Physical Storage Components
		if (fullName.equals("cell256kPart")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 0);
		}
		if (fullName.equals("cell1024kPart")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 1);
		}
		if (fullName.equals("cell4096kPart")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 2);
		}
		if (fullName.equals("cell16384kPart")) {
			return new ResolverResult(ItemEnum.STORAGECOMPONET.getInternalName(), 3);
		}

		// Physical Storage Casing
		if (fullName.equals("physCasing")) {
			return new ResolverResult(ItemEnum.STORAGECASING.getInternalName(), 0);
		}

		// Fluid Storage Casing
		if (fullName.equals("fluidCasing")) {
			return new ResolverResult(ItemEnum.STORAGECASING.getInternalName(), 1);
		}

		// Fluid Storage Casing
		if (fullName.equals("gasCasing")) {
			return new ResolverResult(ItemEnum.STORAGECASING.getInternalName(), 2);
		}

		// Parts
		if (fullName.equals("partBattery")) {
			return new ResolverResult(ItemEnum.PARTITEM.getInternalName(), PartEnum.BATTERY.ordinal());
		}
		if (fullName.equals("partDrive")) {
			return new ResolverResult(ItemEnum.PARTITEM.getInternalName(), PartEnum.DRIVE.ordinal());
		}
		if (fullName.equals("partInterface")) {
			return new ResolverResult(ItemEnum.PARTITEM.getInternalName(), PartEnum.INTERFACE.ordinal());
		}
		if (fullName.equals("partOreDictExportBus")) {
			return new ResolverResult(ItemEnum.PARTITEM.getInternalName(), PartEnum.OREDICTEXPORTBUS.ordinal());
		}
		if (fullName.equals("partGasImportBus")) {
			return new ResolverResult(ItemEnum.PARTITEM.getInternalName(), PartEnum.GASIMPORT.ordinal());
		}
		if (fullName.equals("partGasExportBus")) {
			return new ResolverResult(ItemEnum.PARTITEM.getInternalName(), PartEnum.GASEXPORT.ordinal());
		}
		if (fullName.equals("partGasStorageBus")) {
			return new ResolverResult(ItemEnum.PARTITEM.getInternalName(), PartEnum.GASSTORAGE.ordinal());
		}
		if (fullName.equals("partGasTerminal")) {
			return new ResolverResult(ItemEnum.PARTITEM.getInternalName(), PartEnum.GASTERMINAL.ordinal());
		}
		if (fullName.equals("partGasLevelEmitter")) {
			return new ResolverResult(ItemEnum.PARTITEM.getInternalName(), PartEnum.GASLEVELEMITTER.ordinal());
		}
		if (fullName.equals("partGasStorageMonitor")) {
			return new ResolverResult(ItemEnum.PARTITEM.getInternalName(), PartEnum.GASMONITOR.ordinal());
		}
		if (fullName.equals("partGasConversionMonitor")) {
			return new ResolverResult(ItemEnum.PARTITEM.getInternalName(), PartEnum.GASCONVERSIONMONITOR.ordinal());
		}

		// MISC
		if (fullName.equals("certusTank")) {
			return new ResolverResult(BlockEnum.CERTUSTANK.getInternalName(), 0);
		}
		if (fullName.equals("fluidPattern")) {
			return new ResolverResult(ItemEnum.FLUIDPATTERN.getInternalName(), 0);
		}
		if (fullName.equals("fluidCrafter")) {
			return new ResolverResult(BlockEnum.FLUIDCRAFTER.getInternalName(), 0);
		}
		if (fullName.equals("wirelessFluidTerminal")) {
			return new ResolverResult(ItemEnum.FLUIDWIRELESSTERMINAL.getInternalName(), 0);
		}
		if (fullName.equals("wirelessGasTerminal")) {
			return new ResolverResult(ItemEnum.GASWIRELESSTERMINAL.getInternalName(), 0);
		}
		if (fullName.equals("fluidFiller")) {
			return new ResolverResult(BlockEnum.FILLER.getInternalName(), 1);
		}
		if (fullName.equals("blockVibrationChamberFluid")) {
			return new ResolverResult(BlockEnum.VIBRANTCHAMBERFLUID.getInternalName(), 0);
		}
		if (fullName.equals("hardMEDrive")) {
			return new ResolverResult(BlockEnum.BLASTRESISTANTMEDRIVE.getInternalName(), 0);
		}

		return null;
	}
}
