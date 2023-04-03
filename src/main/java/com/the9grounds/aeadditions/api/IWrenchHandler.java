package com.the9grounds.aeadditions.api;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;

public interface IWrenchHandler {

    boolean canWrench(ItemStack itemStack, EntityPlayer user, RayTraceResult rayTraceResult, EnumHand hand);

    void wrenchUsed(ItemStack itemStack, EntityPlayer user, RayTraceResult rayTraceResult, EnumHand hand);

}