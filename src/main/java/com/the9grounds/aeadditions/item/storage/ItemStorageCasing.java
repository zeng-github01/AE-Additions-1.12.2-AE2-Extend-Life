package com.the9grounds.aeadditions.item.storage;

import java.util.List;

import com.the9grounds.aeadditions.registries.CellDefinition;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import com.the9grounds.aeadditions.integration.Integration;
import com.the9grounds.aeadditions.item.ItemECBase;
import com.the9grounds.aeadditions.models.ModelManager;

public class ItemStorageCasing extends ItemECBase {

	public ItemStorageCasing() {
		setMaxDamage(0);
		setHasSubtypes(true);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(CreativeTabs creativeTab, NonNullList itemList) {
		if (!this.isInCreativeTab(creativeTab))
			return;
		for (CellDefinition definition : CellDefinition.values()) {
			if (definition == CellDefinition.GAS && !Integration.Mods.MEKANISMGAS.isEnabled()) {
				continue;
			}
			itemList.add(new ItemStack(this, 1, definition.ordinal()));
		}
	}

	@Override
	public String getTranslationKey(ItemStack itemStack) {
		CellDefinition definition = CellDefinition.get(itemStack.getItemDamage());
		return "com.the9grounds.aeadditions.item.storage.casing." + definition;
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, ModelManager manager) {
		for (CellDefinition definition : CellDefinition.values()) {
			manager.registerItemModel(item, definition.ordinal(), "storage/" + definition + "/casing");
		}
	}
}
