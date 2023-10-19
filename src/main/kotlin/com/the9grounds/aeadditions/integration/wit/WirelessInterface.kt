package com.the9grounds.aeadditions.integration.wit

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import p455w0rd.ae2wtlib.api.WTApi
import p455w0rd.wct.api.WCTApi
import p455w0rd.wit.api.WITApi

object WirelessInterface {

    @JvmStatic fun openInterfaceTerminal (player: EntityPlayer, slot: Int) = WITApi.instance()?.openWITGui(player,false, slot)

    @JvmStatic fun openInterfaceTerminal(player: EntityPlayer, isBauble: Boolean, slot: Int) = WITApi.instance()?.openWITGui(player, isBauble, slot)

//    @JvmStatic fun getBoosterItem() = ItemStack(Item.getByNameOrId("wct:infinity_booster_card")!!)
//
//    @JvmStatic fun isBoosterEnabled() = WTApi.instance()?.config?.isInfinityBoosterCardEnabled

    @JvmStatic fun getInterfaceTerminal() = ItemStack(Item.getByNameOrId("wit:wit")!!)
}