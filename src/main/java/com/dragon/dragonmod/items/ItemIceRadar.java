package com.dragon.dragonmod.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nullable;
import java.util.List;

public class ItemIceRadar extends Item {
    public ItemIceRadar(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (level.isClientSide) {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> openScreen());
        }
        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide);
    }

    @OnlyIn(Dist.CLIENT)
    private void openScreen() {
        net.minecraft.client.Minecraft.getInstance().setScreen(new com.dragon.dragonmod.client.gui.ice.IceRadarScreen());
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        
        tooltip.add(Component.literal("Tracks Ice Dragons")
                .withStyle(ChatFormatting.DARK_BLUE));
        tooltip.add(Component.literal(""));
        tooltip.add(Component.literal("Merge with Dormant Radar in anvil")
                .withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.literal("to begin purification process")
                .withStyle(ChatFormatting.GRAY));
    }
}