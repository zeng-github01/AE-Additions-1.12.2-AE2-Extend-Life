package com.the9grounds.aeadditions.util

import appeng.api.AEApi
import com.sun.org.apache.xpath.internal.operations.Bool
import com.the9grounds.aeadditions.integration.Integration
import com.the9grounds.aeadditions.integration.wct.WirelessCrafting
import com.the9grounds.aeadditions.integration.wit.WirelessInterface
import com.the9grounds.aeadditions.item.WirelessTerminalType
import com.the9grounds.aeadditions.registries.ItemEnum
import com.the9grounds.aeadditions.registries.PartEnum
import net.minecraft.item.Item
import net.minecraft.item.ItemStack

object UniversalTerminal {
    val isMekLoaded = Integration.Mods.MEKANISMGAS.isEnabled
    val isThaLoaded = Integration.Mods.THAUMATICENERGISTICS.isEnabled
//    val isWcLLoaded = Integration.Mods.WIRELESSCRAFTING.isEnabled
    val isWiLoaded = Integration.Mods.WirelessInterfaceTerminal.isEnabled

    @JvmStatic val wirelessTerminals: List<ItemStack>
    get() {
        val terminals = mutableListOf<ItemStack>()
        val terminalDefinition = AEApi.instance().definitions().items().wirelessTerminal().maybeStack(1).orElse(null)
        terminals.add(terminalDefinition)
        terminals.add(ItemEnum.FLUIDWIRELESSTERMINAL.getSizedStack(1))
//        terminals.add(ItemEnum.CRAFTINGPATTERN.getSizedStack(1))
        terminals.add(AEApi.instance().definitions().items().wirelessCraftingTerminal().maybeStack(1).orElse(null))
        terminals.add(AEApi.instance().definitions().items().wirelessPatternTerminal().maybeStack(1).orElse(null))

        if (isMekLoaded) {
            terminals.add(ItemEnum.GASWIRELESSTERMINAL.getSizedStack(1))
        }
        if (isWiLoaded) {
            terminals.add(WirelessInterface.getInterfaceTerminal())
        }



//        if (isWcLLoaded) {
//            terminals.add(WirelessCrafting.getCraftingTerminal())
//        }

        return terminals.toList()
    }

    @JvmStatic val terminals: List<ItemStack>
    get() {
        val terminals = mutableListOf<ItemStack>()

        val terminalDefinition = AEApi.instance().definitions().parts().terminal().maybeStack(1).orElse(null)
        terminals.add(terminalDefinition)

        if (isMekLoaded) {
            terminals.add(ItemEnum.PARTITEM.getDamagedStack(PartEnum.GASTERMINAL.ordinal))
        }

        return terminals.toList()
    }

    @JvmStatic fun isTerminal(itemStack: ItemStack?): Boolean {
        if (itemStack == null) {
            return false
        }

        val item = itemStack.item
        val meta = itemStack.itemDamage

        if (item == null) {
            return false
        }

        val aeTerm = AEApi.instance().definitions().parts().terminal().maybeStack(1).orElse(null)

        if (aeTerm != null && item == aeTerm.item && meta == aeTerm.itemDamage) {
            return true
        }
        val aeTermFluid = AEApi.instance().definitions().parts().fluidTerminal().maybeStack(1).orElse(null)
        if (aeTermFluid != null && item == aeTermFluid.item && meta == aeTermFluid.itemDamage) {
            return true
        }
        val aeTermGas = ItemEnum.PARTITEM.getDamagedStack(PartEnum.GASTERMINAL.ordinal)
        if (item == aeTermGas.item && meta == aeTermGas.itemDamage) {
            return true
        }
        val aeTermCrafting = AEApi.instance().definitions().parts().craftingTerminal().maybeStack(1).orElse(null)
        if (aeTermCrafting != null && item == aeTermCrafting.item && meta == aeTermCrafting.itemDamage) {
            return true
        }
        val aeTermPattern = AEApi.instance().definitions().parts().patternTerminal().maybeStack(1).orElse(null)
        if (aeTermPattern != null && item == aeTermPattern.item && meta == aeTermPattern.itemDamage){
            return true
        }

        val aeTermInterface = AEApi.instance().definitions().parts().interfaceTerminal().maybeStack(1).orElse(null)
        if (aeTermInterface != null && item == aeTermInterface.item && meta == aeTermInterface.itemDamage){
            return true
        }

        return false
    }

    @JvmStatic fun isWirelessTerminal(itemStack: ItemStack?): Boolean {
        if (itemStack == null) {
            return false
        }

        val item = itemStack.item
        val meta = itemStack.itemDamage

        if (item == null) {
            return false
        }
        val wirelessTerminal = AEApi.instance().definitions().items().wirelessTerminal().maybeStack(1).orElse(null)
        if (wirelessTerminal != null && item == wirelessTerminal.item && meta == wirelessTerminal.itemDamage) {
            return true
        }
        val fluidWirelessTerminal = ItemEnum.FLUIDWIRELESSTERMINAL.getDamagedStack(0)
        if (item == fluidWirelessTerminal.item && meta == fluidWirelessTerminal.itemDamage) {
            return true
        }

        val aefluidWirelessTerminal = AEApi.instance().definitions().items().wirelessFluidTerminal().maybeStack(1).orElse(null)
        if (aefluidWirelessTerminal != null && item == aefluidWirelessTerminal.item && meta == aefluidWirelessTerminal.itemDamage){
            return true
        }
        val wirelessGasTerminal = ItemEnum.GASWIRELESSTERMINAL.getDamagedStack(0)
        if (item == wirelessGasTerminal.item && meta == wirelessGasTerminal.itemDamage) {
            return true
        }

        val  wirelessCraftingTerminal = AEApi.instance().definitions().items().wirelessCraftingTerminal().maybeStack(1).orElse(null)
        if (wirelessCraftingTerminal != null && item == wirelessCraftingTerminal.item && meta == wirelessCraftingTerminal.itemDamage){
            return true
        }

        val wirelessPatternTerminal = AEApi.instance().definitions().items().wirelessPatternTerminal().maybeStack(1).orElse(null)
        if (wirelessPatternTerminal != null && item == wirelessPatternTerminal.item && meta == wirelessPatternTerminal.itemDamage){
            return true
        }

        if (isWiLoaded){
            val wiTerm = WirelessInterface.getInterfaceTerminal()
            if (item == wiTerm.item && meta == wiTerm.itemDamage){
                return true
            }
        }

//        if(isWcLLoaded) {
//            val wcTerm = WirelessCrafting.getCraftingTerminal()
//            if(item == wcTerm.item && meta == wcTerm.itemDamage) {
//                return true
//            }
//        }
        return false
    }

    @JvmStatic fun getTerminalType(itemStack: ItemStack?): WirelessTerminalType? {
        if (itemStack == null) {
            return null
        }

        val item = itemStack.item
        val meta = itemStack.itemDamage

        if (item == null) {
            return null
        }

        val aeTerminal = AEApi.instance().definitions().parts().terminal().maybeStack(1).orElse(null)
        if (aeTerminal != null && item == aeTerminal.item && meta == aeTerminal.itemDamage)  {
            return WirelessTerminalType.ITEM
        }
        val aeTerminalFluid = AEApi.instance().definitions().parts().fluidTerminal().maybeStack(1).orElse(null)
        if (aeTerminalFluid != null && item == aeTerminalFluid.item && meta == aeTerminalFluid.itemDamage) {
            return WirelessTerminalType.FLUID
        }
        val gasTerminal = ItemEnum.PARTITEM.getDamagedStack(PartEnum.GASTERMINAL.ordinal)
        if (item == gasTerminal.item && meta == gasTerminal.itemDamage) {
            return WirelessTerminalType.GAS
        }

        val aeCraftingTerminal = AEApi.instance().definitions().parts().craftingTerminal().maybeStack(1).orElse(null)
        if (aeCraftingTerminal != null && item == aeCraftingTerminal.item && meta == aeCraftingTerminal.itemDamage) {
            return WirelessTerminalType.CRAFTING
        }

        val aeTerminalParttern = AEApi.instance().definitions().parts().patternTerminal().maybeStack(1).orElse(null)
        if (aeTerminalParttern != null && item == aeTerminalParttern.item && meta == aeTerminalParttern.itemDamage){
            return WirelessTerminalType.PATTERN
        }

        val aeTerminalInterface = AEApi.instance().definitions().parts().interfaceTerminal().maybeStack(1).orElse(null)
        if (aeTerminalInterface != null && item == aeTerminalInterface.item && meta == aeTerminalInterface.itemDamage){
            return WirelessTerminalType.INTERFACE
        }

        // Wireless Terminals
        val aeWirelessTerminal = AEApi.instance().definitions().items().wirelessTerminal().maybeStack(1).orElse(null)
        if (aeWirelessTerminal != null && item == aeWirelessTerminal.item && meta == aeWirelessTerminal.itemDamage) {
            return WirelessTerminalType.ITEM
        }
        val wirelessFluidTerminal = ItemEnum.FLUIDWIRELESSTERMINAL.getDamagedStack(0)
        if (item == wirelessFluidTerminal.item && meta == wirelessFluidTerminal.itemDamage) {
            return WirelessTerminalType.FLUID
        }
        val aewiressFluidTerminal = AEApi.instance().definitions().items().wirelessFluidTerminal().maybeStack(1).orElse(null)
        if (aewiressFluidTerminal != null && item == aewiressFluidTerminal.item && meta == aewiressFluidTerminal.itemDamage){
            return  WirelessTerminalType.FLUID
        }
        val wirelessGasTerminal = ItemEnum.GASWIRELESSTERMINAL.getDamagedStack(0)
        if (item == wirelessGasTerminal.item && meta == wirelessGasTerminal.itemDamage) {
            return WirelessTerminalType.GAS
        }

        val wirelessCraftingTerminal = AEApi.instance().definitions().items().wirelessCraftingTerminal().maybeStack(1).orElse(null)
        if(item == wirelessCraftingTerminal.item && meta == wirelessCraftingTerminal.itemDamage) {
            return WirelessTerminalType.CRAFTING
        }

        val wirelessPatternTerminal = AEApi.instance().definitions().items().wirelessPatternTerminal().maybeStack(1).orElse(null)
        if (wirelessPatternTerminal != null && item == wirelessPatternTerminal.item && meta == wirelessPatternTerminal.itemDamage){
            return WirelessTerminalType.PATTERN
        }

        if (isWiLoaded){
            val wirelessInterfaceTerminal = WirelessInterface.getInterfaceTerminal();
            if (wirelessInterfaceTerminal != null && item == wirelessInterfaceTerminal.item && meta == wirelessInterfaceTerminal.itemDamage){
                return WirelessTerminalType.INTERFACE
            }
        }

//        if(isWcLLoaded){
//
//        }
        return null
    }
}