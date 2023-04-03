package com.the9grounds.aeadditions.integration.jei

import com.the9grounds.aeadditions.integration.Integration
import com.the9grounds.aeadditions.registries.BlockEnum
import com.the9grounds.aeadditions.registries.ItemEnum
import com.the9grounds.aeadditions.util.CreativeTabEC
import mezz.jei.api.IModPlugin
import mezz.jei.api.IModRegistry
import mezz.jei.api.ISubtypeRegistry
import mezz.jei.api.JEIPlugin
import mezz.jei.api.ingredients.IModIngredientRegistration
import mezz.jei.api.recipe.VanillaRecipeCategoryUid
import net.minecraft.item.ItemStack
import net.minecraft.util.NonNullList

@JEIPlugin
class Plugin : IModPlugin {

    override fun register(registry: IModRegistry) {

        if (!Integration.Mods.JEI.isEnabled) {
            return
        }

        Jei.registry = registry

        val terminalRecipes = mutableListOf<UniversalTerminalRecipeWrapper>()

        terminalRecipes.add(UniversalTerminalRecipeWrapper(true))
        terminalRecipes.add(UniversalTerminalRecipeWrapper(false))

        registry.addRecipes(terminalRecipes, VanillaRecipeCategoryUid.CRAFTING)

        for (item in Jei.fluidBlackList) {
            registry.jeiHelpers.ingredientBlacklist.addIngredientToBlacklist(item)
        }

        hideItem(ItemStack(ItemEnum.FLUIDITEM.item), registry)
        hideItem(ItemStack(ItemEnum.GASITEM.item), registry)
        hideItem(ItemStack(ItemEnum.CRAFTINGPATTERN.item), registry)

        for (item in ItemEnum.values()) {
            if ((item.mod != null && !item.mod.isEnabled) || !item.isEnabled) {
                val i = item.item

                val list = NonNullList.create<ItemStack>()

                i.getSubItems(CreativeTabEC.INSTANCE, list)

                val iterator = list.iterator()
                while (iterator.hasNext()) {
                    try {
                        hideItem(iterator.next(), registry)
                    } catch(e: Throwable) {
                        continue
                    }
                }
            }
        }

        for (block in BlockEnum.values()) {
            if ((block.mod != null && !block.mod.isEnabled) || !block.enabled) {
                val b = block.block

                val list = NonNullList.create<ItemStack>()

                b.getSubBlocks(CreativeTabEC.INSTANCE, list)

                val iterator = list.iterator()
                while (iterator.hasNext()) {
                    try {
                        hideItem(iterator.next(), registry)
                    } catch(e: Throwable) {
                        continue
                    }
                }
            }
        }
    }

    private fun hideItem(item: ItemStack, registry: IModRegistry) {
        registry.jeiHelpers.ingredientBlacklist.addIngredientToBlacklist(item)
    }
}