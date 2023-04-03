package com.the9grounds.aeadditions.item.storage;

import com.the9grounds.aeadditions.registries.CellDefinition;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import cofh.redstoneflux.api.IEnergyContainerItem;
import com.the9grounds.aeadditions.inventory.ECCellInventory;
import com.the9grounds.aeadditions.item.EnumBlockContainerMode;
import com.the9grounds.aeadditions.models.ModelManager;
import com.the9grounds.aeadditions.registries.ItemEnum;
import com.the9grounds.aeadditions.util.AEAConfigHandler;
import com.the9grounds.aeadditions.util.StorageChannels;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.config.PowerUnits;
import appeng.api.implementations.items.IAEItemPowerStorage;
import appeng.api.storage.ICellInventory;
import appeng.api.storage.ICellInventoryHandler;
import appeng.api.storage.IMEInventoryHandler;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;

//TODO: Clean Up
@Optional.Interface(iface = "cofh.redstoneflux.api.IEnergyContainerItem", modid = "redstoneflux")
public class ItemStorageCellPhysical extends ItemStorageCell<IAEItemStack> implements IAEItemPowerStorage, IEnergyContainerItem {

	public static final String[] suffixes = {"256k", "1024k", "4096k", "16384k", "container"};

	public static final int[] bytes_cell = {262144, 1048576, 4194304, 16777216, 65536};
	public static final int[] types_cell = {63, 63, 63, 63, 1};
	private final int MAX_POWER = 32000;

	public ItemStorageCellPhysical() {
		super(CellDefinition.PHYSICAL, StorageChannels.ITEM);
	}

	@Override
	public int getBytesPerType(ItemStack cellItem) {
		int bytes = definition.cells.fromMeta(cellItem.getItemDamage()).getBytes();

		return AEAConfigHandler.dynamicTypes ? bytes / 128 : 8;
	}

	private NBTTagCompound ensureTagCompound(ItemStack itemStack) {
		if (!itemStack.hasTagCompound()) {
			itemStack.setTagCompound(new NBTTagCompound());
		}
		return itemStack.getTagCompound();
	}

	@Override
	public double extractAEPower(ItemStack itemStack, double amt, Actionable actionable) {
		if (itemStack == null || itemStack.getItemDamage() != 4) {
			return 0.0D;
		}
		NBTTagCompound tagCompound = ensureTagCompound(itemStack);
		double currentPower = tagCompound.getDouble("power");
		double toExtract = Math.min(amt, currentPower);
		if (actionable == Actionable.MODULATE)
			tagCompound.setDouble("power", currentPower - toExtract);
		return toExtract;
	}

	@Override
	@Optional.Method(modid = "redstoneflux")
	public int extractEnergy(ItemStack container, int maxExtract,
		boolean simulate) {
		if (container == null || container.getItemDamage() != 4) {
			return 0;
		}
		if (simulate) {
			return getEnergyStored(container) >= maxExtract ? maxExtract
				: getEnergyStored(container);
		} else {
			return (int) PowerUnits.AE
				.convertTo(
					PowerUnits.RF,
					extractAEPower(container, PowerUnits.RF.convertTo(
						PowerUnits.AE, maxExtract), Actionable.MODULATE));
		}
	}

	@Override
	public double getAECurrentPower(ItemStack itemStack) {
		if (itemStack == null || itemStack.getItemDamage() != 4) {
			return 0.0D;
		}
		NBTTagCompound tagCompound = ensureTagCompound(itemStack);
		return tagCompound.getDouble("power");
	}

	@Override
	public double getAEMaxPower(ItemStack itemStack) {
		if (itemStack == null || itemStack.getItemDamage() != 4) {
			return 0.0D;
		}
		return this.MAX_POWER;
	}

	@Override
	public int getBytes(ItemStack cellItem) {
		return definition.cells.fromMeta(cellItem.getItemDamage()).getBytes();
	}

	@Override
	public IItemHandler getConfigInventory(ItemStack is) {
		return new InvWrapper(new ECCellInventory(is, "config", 63, 1));
	}

	@Override
	public double getDurabilityForDisplay(ItemStack itemStack) {
		if (itemStack == null || itemStack.getItemDamage() != 4) {
			return super.getDurabilityForDisplay(itemStack);
		}
		return 1 - getAECurrentPower(itemStack) / this.MAX_POWER;
	}

	@Override
	@Optional.Method(modid = "redstoneflux")
	public int getEnergyStored(ItemStack arg0) {
		return (int) PowerUnits.AE.convertTo(PowerUnits.RF,
			getAECurrentPower(arg0));
	}

	@SideOnly(Side.CLIENT)
	@Override
	public String getItemStackDisplayName(ItemStack stack) {
		if (stack == null) {
			return super.getItemStackDisplayName(stack);
		}
		if (stack.getItemDamage() == 4) {
			try {
				IItemList list = AEApi
					.instance()
					.registries()
					.cell()
					.getCellInventory(stack, null, StorageChannels.ITEM)
					.getAvailableItems(StorageChannels.ITEM.createList());
				if (list.isEmpty()) {
					return super.getItemStackDisplayName(stack)
						+ " - "
						+ I18n
						.translateToLocal("com.the9grounds.aeadditions.tooltip.empty1");
				}
				IAEItemStack s = (IAEItemStack) list.getFirstItem();
				return super.getItemStackDisplayName(stack) + " - "
					+ s.createItemStack().getDisplayName();
			} catch (Throwable e) {
			}
			return super.getItemStackDisplayName(stack)
				+ " - "
				+ I18n
				.translateToLocal("com.the9grounds.aeadditions.tooltip.empty1");
		}
		return super.getItemStackDisplayName(stack);
	}

	@Override
	@Optional.Method(modid = "redstoneflux")
	public int getMaxEnergyStored(ItemStack arg0) {
		return (int) PowerUnits.AE
			.convertTo(PowerUnits.RF, getAEMaxPower(arg0));
	}

	@Override
	public AccessRestriction getPowerFlow(ItemStack itemStack) {
		if (itemStack == null) {
			return null;
		}
		return itemStack.getItemDamage() == 4 ? AccessRestriction.READ_WRITE
			: AccessRestriction.NO_ACCESS;
	}

	@Override
	public EnumRarity getRarity(ItemStack itemStack) {
		return EnumRarity.EPIC;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void getSubItems(CreativeTabs creativeTab, NonNullList itemList) {
		if (!this.isInCreativeTab(creativeTab))
			return;
		for (int i = 0; i < suffixes.length; i++) {
			itemList.add(new ItemStack(this, 1, i));
			if (i == 4) {
				ItemStack s = new ItemStack(this, 1, i);
				s.setTagCompound(new NBTTagCompound());
				s.getTagCompound().setDouble("power", this.MAX_POWER);
				itemList.add(s);
			}
		}
	}

	@Override
	public int getTotalTypes(ItemStack cellItem) {
		return definition.cells.fromMeta(cellItem.getItemDamage()).getNumberOfTypes();
	}

	@Override
	public String getTranslationKey(ItemStack itemStack) {
		return "com.the9grounds.aeadditions.item.storage.physical." + suffixes[itemStack.getItemDamage()];
	}

	@Override
	public IItemHandler getUpgradesInventory(ItemStack is) {
		return new InvWrapper(new ECCellInventory(is, "upgrades", 2, 1));
	}

	@Override
	public double injectAEPower(ItemStack itemStack, double amt, Actionable actionable) {
		if (itemStack == null || itemStack.getItemDamage() != 4) {
			return 0.0D;
		}
		NBTTagCompound tagCompound = ensureTagCompound(itemStack);
		double currentPower = tagCompound.getDouble("power");
		double toInject = Math.min(amt, this.MAX_POWER - currentPower);
		if (actionable == Actionable.MODULATE)
			tagCompound.setDouble("power", currentPower + toInject);
		return toInject;
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	@Override
	public ActionResult<ItemStack> onItemRightClick( World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);
		if (itemStack == null) {
			return new ActionResult(EnumActionResult.SUCCESS, itemStack);
		}
		if (itemStack.getItemDamage() == 4 && player.isSneaking()) {
			if (world.isRemote) {
				return new ActionResult(EnumActionResult.SUCCESS, itemStack);
			}
			switch (itemStack.getTagCompound().getInteger("mode")) {
				case 0:
					itemStack.getTagCompound().setInteger("mode", 1);
					player.sendMessage(new TextComponentTranslation("com.the9grounds.aeadditions.tooltip.storage.container.1"));
					break;
				case 1:
					itemStack.getTagCompound().setInteger("mode", 2);
					player.sendMessage(new TextComponentTranslation("com.the9grounds.aeadditions.tooltip.storage.container.2"));
					break;
				case 2:
					itemStack.getTagCompound().setInteger("mode", 0);
					player.sendMessage(new TextComponentTranslation("com.the9grounds.aeadditions.tooltip.storage.container.0"));
					break;
			}
			return new ActionResult(EnumActionResult.SUCCESS, itemStack);
		}
		if (!player.isSneaking()) {
			return new ActionResult(EnumActionResult.SUCCESS, itemStack);
		}
		IMEInventoryHandler<IAEItemStack> invHandler = AEApi.instance().registries().cell().getCellInventory(itemStack, null, StorageChannels.ITEM);
		ICellInventoryHandler inventoryHandler = (ICellInventoryHandler) invHandler;
		ICellInventory cellInv = inventoryHandler.getCellInv();
		if (cellInv.getUsedBytes() == 0 && player.inventory.addItemStackToInventory(ItemEnum.STORAGECASING.getDamagedStack(0))) {
			return new ActionResult(EnumActionResult.SUCCESS, ItemEnum.STORAGECOMPONET.getDamagedStack(itemStack.getItemDamage()));
		}
		return new ActionResult(EnumActionResult.SUCCESS, itemStack);
	}

	@Override
	public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		ItemStack stack = player.getHeldItem(hand);
		if (stack == null || player == null) {
			return EnumActionResult.PASS;
		}
		if (stack.getItemDamage() == 4 && !player.isSneaking()) {
			if (true) {
				player.sendMessage(new TextComponentString("Disabled pending rewrite"));
				return EnumActionResult.PASS;
			}
			double power = getAECurrentPower(stack);
			IItemList list = AEApi.instance().registries().cell().getCellInventory(stack, null, StorageChannels.ITEM).getAvailableItems(
				StorageChannels.ITEM.createList());
			if (list.isEmpty()) {
				return EnumActionResult.PASS;
			}
			IAEItemStack storageStack = (IAEItemStack) list.getFirstItem();
			if (world.isAirBlock(pos.offset(facing)) && storageStack.getStackSize() != 0 && power >= 20.0D) {
				if (!world.isRemote) {
					IAEItemStack request = storageStack.copy();
					request.setStackSize(1);
					ItemStack block = request.createItemStack();
					if (block.getItem() instanceof ItemBlock) {
						IBlockState blockState = world.getBlockState(pos);
						if (blockState.getBlock() != Blocks.BEDROCK && blockState.getBlockHardness(world, pos) >= 0.0F) {
							int modeIndex = stack.getTagCompound().getInteger("mode");
							EnumBlockContainerMode mode = EnumBlockContainerMode.get(modeIndex);
							mode.useMode(this, stack, storageStack, request, world, pos, player, facing, hand, hitX, hitY, hitZ);
							return EnumActionResult.SUCCESS;
						} else {
							return EnumActionResult.PASS;
						}
					} else {
						player.sendMessage(new TextComponentTranslation("com.the9grounds.aeadditions.tooltip.onlyblocks"));
						return EnumActionResult.PASS;
					}
				} else {
					return EnumActionResult.PASS;
				}
			} else {
				return EnumActionResult.PASS;
			}
		} else {
			return EnumActionResult.PASS;
		}
	}

	@Override
	@Optional.Method(modid = "redstoneflux")
	public int receiveEnergy(ItemStack container, int maxReceive,
		boolean simulate) {
		if (container == null || container.getItemDamage() != 4) {
			return 0;
		}
		if (simulate) {
			double current = PowerUnits.AE.convertTo(PowerUnits.RF,
				getAECurrentPower(container));
			double max = PowerUnits.AE.convertTo(PowerUnits.RF,
				getAEMaxPower(container));
			if (max - current >= maxReceive) {
				return maxReceive;
			} else {
				return (int) (max - current);
			}
		} else {
			int notStored = (int) PowerUnits.AE
				.convertTo(
					PowerUnits.RF,
					injectAEPower(container, PowerUnits.RF.convertTo(
						PowerUnits.AE, maxReceive), Actionable.MODULATE));
			return maxReceive - notStored;
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerModel(Item item, ModelManager manager) {
		for (int i = 0; i < suffixes.length; ++i) {
			manager.registerItemModel(item, i, "storage/physical/cells/" + suffixes[i]);
		}
	}

	@Override
	public boolean showDurabilityBar(ItemStack itemStack) {
		if (itemStack == null) {
			return false;
		}
		return itemStack.getItemDamage() == 4;
	}
}
