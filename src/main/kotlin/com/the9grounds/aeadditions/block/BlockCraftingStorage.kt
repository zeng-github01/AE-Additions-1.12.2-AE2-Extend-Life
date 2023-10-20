package com.the9grounds.aeadditions.block

import com.the9grounds.aeadditions.tileentity.TileEntityCraftingStorage
import net.minecraft.tileentity.TileEntity
import net.minecraft.world.World
import appeng.block.crafting.BlockCraftingStorage as AppEngBlockCraftingStorage

class BlockCraftingStorage(type: CraftingUnitType?) : AppEngBlockCraftingStorage(type) {
    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityCraftingStorage()
    }
}