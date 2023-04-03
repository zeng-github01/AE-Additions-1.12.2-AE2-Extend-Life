package com.the9grounds.aeadditions.util;

import appeng.api.AEApi;
import appeng.api.config.AccessRestriction;
import appeng.api.config.Actionable;
import appeng.api.networking.security.IActionSource;
import appeng.api.storage.IMEMonitor;
import appeng.api.storage.IMEMonitorHandlerReceiver;
import appeng.api.storage.IStorageChannel;
import appeng.api.storage.data.IAEItemStack;
import appeng.api.storage.data.IItemList;

public class EmptyMeItemMonitor implements IMEMonitor<IAEItemStack> {

	@Override
	public void addListener(IMEMonitorHandlerReceiver<IAEItemStack> l, Object verificationToken) {

	}

	@Override
	public boolean canAccept(IAEItemStack input) {
		return false;
	}

	@Override
	public IAEItemStack extractItems(IAEItemStack request, Actionable mode, IActionSource src) {
		return null;
	}

	@Override
	public AccessRestriction getAccess() {
		return AccessRestriction.NO_ACCESS;
	}

	@Override
	public IItemList<IAEItemStack> getAvailableItems(IItemList out) {
		return out;
	}

	@Override
	public IStorageChannel getChannel() {
		return StorageChannels.ITEM;
	}

	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public int getSlot() {
		return 0;
	}

	@Override
	public IItemList<IAEItemStack> getStorageList() {
		return StorageChannels.ITEM.createList();
	}

	@Override
	public IAEItemStack injectItems(IAEItemStack input, Actionable type, IActionSource src) {
		return input;
	}

	@Override
	public boolean isPrioritized(IAEItemStack input) {
		return false;
	}

	@Override
	public void removeListener(IMEMonitorHandlerReceiver<IAEItemStack> l) {
	}

	@Override
	public boolean validForPass(int i) {
		return true;
	}

}
