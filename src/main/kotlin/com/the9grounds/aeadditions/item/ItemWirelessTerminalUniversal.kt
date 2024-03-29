package com.the9grounds.aeadditions.item

import appeng.api.AEApi
import appeng.api.config.Actionable
import appeng.api.features.IWirelessTermHandler
import appeng.api.util.IConfigManager
import appeng.core.sync.GuiBridge
import baubles.api.BaubleType
import com.the9grounds.aeadditions.Constants
import com.the9grounds.aeadditions.api.AEAApi
import com.the9grounds.aeadditions.api.IWirelessFluidTermHandler
import com.the9grounds.aeadditions.api.IWirelessGasTermHandler
import com.the9grounds.aeadditions.integration.Integration
import com.the9grounds.aeadditions.integration.wct.WirelessCrafting
import com.the9grounds.aeadditions.integration.wit.WirelessInterface
import com.the9grounds.aeadditions.models.ModelManager
import com.the9grounds.aeadditions.util.HandlerUniversalWirelessTerminal
import com.the9grounds.aeadditions.wireless.ConfigManager
import li.cil.oc.common.block.traits.GUI
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.client.util.ITooltipFlag
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.util.*
import net.minecraft.util.text.translation.I18n
import net.minecraft.world.World
import net.minecraftforge.fml.common.Optional
import net.minecraftforge.fml.common.network.IGuiHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import p455w0rd.ae2wtlib.api.client.IBaubleRender
import p455w0rd.wit.api.IWirelessInterfaceTerminalItem
import p455w0rd.wit.api.WITApi
import java.util.*

@Optional.Interface(iface = "p455w0rd.wit.api.IWirelessInterfaceTerminalItem", modid = "wit", striprefs = true)
class ItemWirelessTerminalUniversal : WirelessTermBase(), IWirelessFluidTermHandler, IWirelessGasTermHandler,
    IWirelessTermHandler, IWirelessInterfaceTerminalItem {

    val isTeEnabled = Integration.Mods.THAUMATICENERGISTICS.isEnabled
    val isMekEnabled = Integration.Mods.MEKANISMGAS.isEnabled
    val isWiEnabled = Integration.Mods.WirelessInterfaceTerminal.isEnabled
//    val isWcEnabled = Integration.Mods.WIRELESSCRAFTING.isEnabled

    private var holder: EntityPlayer? = null

    init {
        AEAApi.instance().registerWirelessTermHandler(this)
        AEApi.instance().registries().wireless().registerWirelessHandler(this)
//        if (isWcEnabled) {
//
//        } else {
//            AEAApi.instance().registerWirelessTermHandler(HandlerUniversalWirelessTerminal)
//            AEApi.instance().registries().wireless().registerWirelessHandler(HandlerUniversalWirelessTerminal)
//        }
    }

    override fun isItemNormalWirelessTermToo(`is`: ItemStack?): Boolean = true

    override fun getConfigManager(itemStack: ItemStack?): IConfigManager {
        val nbt = ensureTagCompound(itemStack!!)
        if (!nbt.hasKey("settings")) {
            nbt.setTag("settings", NBTTagCompound())
        }

        return ConfigManager(nbt.getCompoundTag("settings"))
    }

    override fun getGuiHandler(item: ItemStack): IGuiHandler {

        val tag = ensureTagCompound(item)
        when (tag.getByte("type").toInt()) {
            0 -> return GuiBridge.GUI_WIRELESS_TERM
            1 -> return GuiBridge.GUI_WIRELESS_FLUID_TERMINAL
            4 -> return GuiBridge.GUI_WIRELESS_CRAFTING_TERMINAL
            5 -> return GuiBridge.GUI_WIRELESS_PATTERN_TERMINAL
            6 -> return GuiBridge.GUI_INTERFACE_TERMINAL;
        }

        return GuiBridge.GUI_WIRELESS_TERM;
    }

    override fun getTranslationKey(stack: ItemStack): String {
        return super.getTranslationKey(stack)
            .replace("item.com.the9grounds.aeadditions", "com.the9grounds.aeadditions.item")
    }

    override fun getItemStackDisplayName(stack: ItemStack): String {
        val tag = ensureTagCompound(stack)
        if (!tag.hasKey("type")) {
            tag.setByte("type", 0)
        }
        // TODO: Bug here
        return super.getItemStackDisplayName(stack) + " - " + I18n.translateToLocal(
            "com.the9grounds.aeadditions.tooltip." + WirelessTerminalType.values()[tag.getByte(
                "type"
            ).toInt()].toString().toLowerCase()
        )
    }

    override fun onItemRightClick(world: World, player: EntityPlayer, hand: EnumHand): ActionResult<ItemStack> {
        val itemStack = player.getHeldItem(hand)
        val tag = ensureTagCompound(itemStack)
        if (world.isRemote) {
            if (player.isSneaking) {
                return ActionResult(EnumActionResult.SUCCESS, itemStack)
            }
            if (!tag.hasKey("type")) {
                tag.setByte("type", 0)
            }
            // && isWcEnabled
            if (isWiEnabled && tag.getByte("type") == 6.toByte()) {
//                WirelessCrafting.openCraftingTerminal(player, player.inventory.currentItem)
//                AEApi.instance().registries().wireless().openWirelessTerminalGui(itemStack, world, player)
                WirelessInterface.openInterfaceTerminal(player,player.inventory.currentItem)
            }
            return ActionResult(EnumActionResult.SUCCESS, itemStack)
        }

        if (!tag.hasKey("type")) {
            tag.setByte("type", 0)
        }
        if (player.isSneaking) {
            if (itemStack == null) {
                return ActionResult(EnumActionResult.FAIL, itemStack)
            }
            return ActionResult(EnumActionResult.SUCCESS, changeMode(itemStack, player, tag)!!)
        }

        when (tag.getByte("type").toInt()) {
            2 -> AEAApi.instance().openWirelessGasTerminal(player, hand, world)
            else -> {
                AEApi.instance().registries().wireless().openWirelessTerminalGui(itemStack, world, player)
//            1 -> AEAApi.instance().openWirelessFluidTerminal(player, hand, world)

            }
        }

        return ActionResult(EnumActionResult.SUCCESS, itemStack)
    }

    fun changeMode(itemStack: ItemStack?, player: EntityPlayer, tag: NBTTagCompound): ItemStack? {
        val installed = getInstalledModules(itemStack)

        when (tag.getByte("type").toInt()) {
            0 -> {
//                isWcEnabled

                if (installed.contains(WirelessTerminalType.FLUID)) {
                    tag.setByte("type", 1)
                } else if (isMekEnabled && installed.contains(WirelessTerminalType.GAS)) {
                    tag.setByte("type", 2)
                } else if (isTeEnabled && installed.contains(WirelessTerminalType.ESSENTIA)) {
                    tag.setByte("type", 3)
                } else if (installed.contains(WirelessTerminalType.CRAFTING)) {
                    tag.setByte("type", 4)
                } else if (isWiEnabled && installed.contains(WirelessTerminalType.INTERFACE)) {
                    tag.setByte("type",6);
                }
            }

            1 -> {
//                isWcEnabled
                if (isMekEnabled && installed.contains(WirelessTerminalType.GAS)) {
                    tag.setByte("type", 2)
                } else if (isTeEnabled && installed.contains(WirelessTerminalType.ESSENTIA)) {
                    tag.setByte("type", 3)
                } else if (installed.contains(WirelessTerminalType.CRAFTING)) {
                    tag.setByte("type", 4)
                } else if (installed.contains(WirelessTerminalType.ITEM)) {
                    tag.setByte("type", 0)
                } else if (isWiEnabled && installed.contains(WirelessTerminalType.INTERFACE)) {
                    tag.setByte("type",6);
                }
            }

            2 -> {
//                isWcEnabled
                if (isTeEnabled && installed.contains(WirelessTerminalType.ESSENTIA)) {
                    tag.setByte("type", 3)
                } else if (installed.contains(WirelessTerminalType.CRAFTING)) {
                    tag.setByte("type", 4)
                } else if (installed.contains(WirelessTerminalType.ITEM)) {
                    tag.setByte("type", 0)
                } else if (installed.contains(WirelessTerminalType.FLUID)) {
                    tag.setByte("type", 1)
                } else if (isWiEnabled && installed.contains(WirelessTerminalType.INTERFACE)) {
                    tag.setByte("type",6);
                }
            }

            3 -> {
//                isWcEnabled
                if (installed.contains(WirelessTerminalType.CRAFTING)) {
                    tag.setByte("type", 4)
                } else if (installed.contains(WirelessTerminalType.ITEM)) {
                    tag.setByte("type", 0)
                } else if (installed.contains(WirelessTerminalType.FLUID)) {
                    tag.setByte("type", 1)
                } else if (isMekEnabled && installed.contains(WirelessTerminalType.GAS)) {
                    tag.setByte("type", 2)
                } else if (isWiEnabled && installed.contains(WirelessTerminalType.INTERFACE)) {
                    tag.setByte("type",6);
                }
            }

            4 -> {
                if (installed.contains(WirelessTerminalType.PATTERN)) {
                    tag.setByte("type", 5)
                } else if (installed.contains(WirelessTerminalType.ITEM)) {
                    tag.setByte("type", 0)
                } else if (installed.contains(WirelessTerminalType.FLUID)) {
                    tag.setByte("type", 1)
                } else if (isMekEnabled && installed.contains(WirelessTerminalType.GAS)) {
                    tag.setByte("type", 2)
                } else if (isWiEnabled && installed.contains(WirelessTerminalType.INTERFACE)) {
                    tag.setByte("type",6);
                }
            }

            5 -> {
                if (isWiEnabled && installed.contains(WirelessTerminalType.INTERFACE)) {
                    tag.setByte("type",6)
                } else if (installed.contains(WirelessTerminalType.ITEM)) {
                    tag.setByte("type", 0)
                } else if (installed.contains(WirelessTerminalType.FLUID)) {
                    tag.setByte("type", 1)
                } else if (isMekEnabled && installed.contains(WirelessTerminalType.GAS)) {
                    tag.setByte("type", 2)
                }
            }

            6 -> {
                if (installed.contains(WirelessTerminalType.ITEM)) {
                    tag.setByte("type", 0)
                } else if (installed.contains(WirelessTerminalType.FLUID)) {
                    tag.setByte("type", 1)
                } else if (isMekEnabled && installed.contains(WirelessTerminalType.GAS)) {
                    tag.setByte("type", 2)
                }
            }

            else -> {
//                isWcEnabled
                if (installed.contains(WirelessTerminalType.ITEM)) {
                    tag.setByte("type", 0)
                } else if (installed.contains(WirelessTerminalType.FLUID)) {
                    tag.setByte("type", 1)
                } else if (isMekEnabled && installed.contains(WirelessTerminalType.GAS)) {
                    tag.setByte("type", 2)
                } else if (isTeEnabled && installed.contains(WirelessTerminalType.ESSENTIA)) {
                    tag.setByte("type", 3)
                } else if (installed.contains(WirelessTerminalType.CRAFTING)) {
                    tag.setByte("type", 4)
                } else if (installed.contains(WirelessTerminalType.PATTERN)) {
                    tag.setByte("type", 5)
                } else if (installed.contains(WirelessTerminalType.INTERFACE)) {
                    tag.setByte("type",6)
                }
            }
        }
        return itemStack
    }

    @SideOnly(Side.CLIENT)
    override fun registerModel(item: Item?, manager: ModelManager?) {
        manager!!.registerItemModel(item, 0, "terminals/universal_wireless")
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, worldIn: World?, tooltip: MutableList<String>, flagIn: ITooltipFlag) {
        val tag = ensureTagCompound(stack)
        if (!tag.hasKey("type")) {
            tag.setByte("type", 0)
        }
        tooltip.add(
            I18n.translateToLocal("com.the9grounds.aeadditions.tooltip.mode") + ": " + I18n.translateToLocal(
                "com.the9grounds.aeadditions.tooltip." + WirelessTerminalType.values()[tag.getByte(
                    "type"
                ).toInt()].toString().toLowerCase()
            )
        )
        tooltip.add(I18n.translateToLocal("com.the9grounds.aeadditions.tooltip.installed"))
        val it = getInstalledModules(stack).iterator()
        while (it.hasNext()) {
            tooltip.add("- " + I18n.translateToLocal("com.the9grounds.aeadditions.tooltip." + it.next().name.toLowerCase()))
        }
        super.addInformation(stack, worldIn, tooltip, flagIn)
    }

    fun installModule(itemStack: ItemStack?, module: WirelessTerminalType) {
        if (isInstalled(itemStack, module) || itemStack == null) {
            return
        }

        val install = (1 shl module.ordinal)

        val tag = ensureTagCompound(itemStack)

        val installed = if (tag.hasKey("modules")) tag.getByte("modules").toInt() + install else install

        tag.setByte("modules", installed.toByte())
    }

    fun getInstalledModules(itemStack: ItemStack?): EnumSet<WirelessTerminalType> {
        if (itemStack == null || itemStack.item == null) {
            return EnumSet.noneOf(WirelessTerminalType::class.java)
        }

        val tag = ensureTagCompound(itemStack)
        val installed = if (tag.hasKey("modules")) tag.getByte("modules") else 0.toByte()

        val set = EnumSet.noneOf(WirelessTerminalType::class.java)

        for (x in WirelessTerminalType.values()) {
            if (1 == (installed.toInt() shr x.ordinal) % 2) {
                set.add(x)
            }
        }

        return set
    }

    fun isInstalled(itemStack: ItemStack?, module: WirelessTerminalType): Boolean {
        if (itemStack == null || itemStack.item == null) {
            return false
        }

        val tag = ensureTagCompound(itemStack)

        val installed = if (tag.hasKey("modules")) tag.getByte("modules").toInt() else 0

        return 1 == (installed shr module.ordinal) % 2
    }

    override fun getSubItems(tab: CreativeTabs, items: NonNullList<ItemStack>) {
        if (!isInCreativeTab(tab)) {
            return
        }

        val tag = NBTTagCompound()
        tag.setByte("modules", 23)
        val itemStack = ItemStack(this)
        itemStack.tagCompound = tag
        items.add(itemStack.copy())
        injectAEPower(itemStack, this.MAX_POWER, Actionable.MODULATE)
        items.add(itemStack)
    }

    @Optional.Method(modid = "wit")
    override fun getRender(): IBaubleRender? = null

    @Optional.Method(modid = "wit")
    override fun getBaubleType(itemStack: ItemStack?): BaubleType = BaubleType.TRINKET

    @Optional.Method(modid = "wit")
    override fun initModel() {
        // Do Nothing
    }

    @Optional.Method(modid = "wit")
    override fun getModelResource(item: Item?): ModelResourceLocation? = null

    @Optional.Method(modid = "wit")
    override fun openGui(player: EntityPlayer?, isBauble: Boolean, playerSlot: Int) {
        if (player != null) {
            WirelessInterface.openInterfaceTerminal(player, isBauble, playerSlot)
        }
    }

    @Optional.Method(modid = "wit")
    override fun getPlayer(): EntityPlayer? = this.holder

    @Optional.Method(modid = "wit")
    override fun setPlayer(player: EntityPlayer?) {
        this.holder = player
    }

    @Optional.Method(modid = "wit")
    override fun getColor(): Int = (0xFF8F15D4).toInt()

    @Optional.Method(modid = "wit")
    override fun getMenuIcon(): ResourceLocation =
        ResourceLocation(Constants.MOD_ID, "textures/items/terminal.universal.wireless.png")
}