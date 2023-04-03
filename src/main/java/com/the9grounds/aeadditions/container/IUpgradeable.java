package com.the9grounds.aeadditions.container;

import net.minecraft.inventory.IInventory;

import appeng.api.util.DimensionalCoord;

public interface IUpgradeable {

	DimensionalCoord getLocation();

	IInventory getUpgradeInventory();
}
