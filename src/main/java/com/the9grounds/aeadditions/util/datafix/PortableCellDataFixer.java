package com.the9grounds.aeadditions.util.datafix;

import javax.annotation.Nonnull;

import com.the9grounds.aeadditions.Constants;
import net.minecraft.nbt.NBTTagCompound;

import com.the9grounds.aeadditions.util.Log;

/**
 * @author BrockWS
 */
@SuppressWarnings("Duplicates")
public class PortableCellDataFixer extends BasicCellDataFixer {

    @Override
    @Nonnull
    public NBTTagCompound fixTagCompound(@Nonnull NBTTagCompound oldItem) {
        String id = oldItem.getString("id");
        if (!id.equalsIgnoreCase(Constants.MOD_ID + ":storage.fluid.portable") && !id.equalsIgnoreCase(Constants.MOD_ID + ":storage.gas.portable"))
            return oldItem;
        if (!oldItem.hasKey("tag") || oldItem.getCompoundTag("tag").isEmpty())
            return oldItem;

        boolean isFluid = id.contains("fluid");

        boolean needsConvert = false;
        for (int i = 0; i < 5; i++)
            if (oldItem.getCompoundTag("tag").hasKey((isFluid ? "Fluid#" : "Gas#") + i))
                needsConvert = true;

        if (!needsConvert)
            return oldItem;

        NBTTagCompound item = isFluid ? this.fixFluidCell(oldItem) : this.fixGasCell(oldItem);

        Log.info("Converted tag for portable {} cell from {} to {}", isFluid ? "fluid" : "gas", oldItem, item);
        return item;
    }
}
