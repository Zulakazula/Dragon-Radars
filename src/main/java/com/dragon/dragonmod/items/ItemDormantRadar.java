package com.dragon.dragonmod.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDormantRadar extends Item {

    public ItemDormantRadar(Properties properties) {
        super(properties.stacksTo(1));
    }

    // --- NBT KEYS (MERGE PHASE) ---
    public static final String KEY_MERGED_FIRE = "mergedFire";
    public static final String KEY_MERGED_ICE = "mergedIce";
    public static final String KEY_MERGED_LIGHTNING = "mergedLightning";

    // --- NBT KEYS (PURIFY PHASE) ---
    public static final String KEY_KILLED_FIRE = "killedFire";
    public static final String KEY_KILLED_ICE = "killedIce";
    public static final String KEY_KILLED_LIGHTNING = "killedLightning";

    // --- MERGE GETTERS ---
    public static boolean hasMergedFire(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(KEY_MERGED_FIRE);
    }

    public static boolean hasMergedIce(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(KEY_MERGED_ICE);
    }

    public static boolean hasMergedLightning(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(KEY_MERGED_LIGHTNING);
    }

    // --- MERGE SETTERS ---
    public static void setMergedFire(ItemStack stack) {
        getOrCreateTag(stack).putBoolean(KEY_MERGED_FIRE, true);
    }

    public static void setMergedIce(ItemStack stack) {
        getOrCreateTag(stack).putBoolean(KEY_MERGED_ICE, true);
    }

    public static void setMergedLightning(ItemStack stack) {
        getOrCreateTag(stack).putBoolean(KEY_MERGED_LIGHTNING, true);
    }

    // --- ALL MERGED CHECK ---
    public static boolean isAllMerged(ItemStack stack) {
        return hasMergedFire(stack) && hasMergedIce(stack) && hasMergedLightning(stack);
    }

    // --- KILL GETTERS ---
    public static boolean hasKilledFire(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(KEY_KILLED_FIRE);
    }

    public static boolean hasKilledIce(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(KEY_KILLED_ICE);
    }

    public static boolean hasKilledLightning(ItemStack stack) {
        return stack.hasTag() && stack.getTag().getBoolean(KEY_KILLED_LIGHTNING);
    }

    // --- KILL SETTERS ---
    public static void setKilledFire(ItemStack stack) {
        getOrCreateTag(stack).putBoolean(KEY_KILLED_FIRE, true);
    }

    public static void setKilledIce(ItemStack stack) {
        getOrCreateTag(stack).putBoolean(KEY_KILLED_ICE, true);
    }

    public static void setKilledLightning(ItemStack stack) {
        getOrCreateTag(stack).putBoolean(KEY_KILLED_LIGHTNING, true);
    }

    // --- FULLY COMPLETE CHECK ---
    public static boolean isComplete(ItemStack stack) {
        return hasKilledFire(stack) && hasKilledIce(stack) && hasKilledLightning(stack);
    }

    // --- HELPER ---
    private static CompoundTag getOrCreateTag(ItemStack stack) {
        if (!stack.hasTag()) stack.setTag(new CompoundTag());
        return stack.getTag();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> lines, TooltipFlag flag) {
        super.appendHoverText(stack, level, lines, flag);

        if (!isAllMerged(stack)) {
            // --- MERGE PHASE TOOLTIP ---
            lines.add(Component.literal("Merge each radar in an anvil:")
                    .withStyle(ChatFormatting.GRAY));

            lines.add(Component.literal("  Fire Radar:      ")
                    .withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(hasMergedFire(stack) ? "✔" : "✘")
                            .withStyle(hasMergedFire(stack) ? ChatFormatting.GREEN : ChatFormatting.RED)));

            lines.add(Component.literal("  Ice Radar:       ")
                    .withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(hasMergedIce(stack) ? "✔" : "✘")
                            .withStyle(hasMergedIce(stack) ? ChatFormatting.GREEN : ChatFormatting.RED)));

            lines.add(Component.literal("  Lightning Radar: ")
                    .withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(hasMergedLightning(stack) ? "✔" : "✘")
                            .withStyle(hasMergedLightning(stack) ? ChatFormatting.GREEN : ChatFormatting.RED)));

            lines.add(Component.literal("Once merged, equip in offhand and")
                    .withStyle(ChatFormatting.DARK_GRAY));
            lines.add(Component.literal("slay Stage 4+ dragons to purify.")
                    .withStyle(ChatFormatting.DARK_GRAY));

        } else {
            // --- PURIFY PHASE TOOLTIP ---
            lines.add(Component.literal("Slay a Stage 4+ of each dragon:")
                    .withStyle(ChatFormatting.GRAY));

            lines.add(Component.literal("  Fire Dragon:      ")
                    .withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(hasKilledFire(stack) ? "✔" : "✘")
                            .withStyle(hasKilledFire(stack) ? ChatFormatting.GREEN : ChatFormatting.RED)));

            lines.add(Component.literal("  Ice Dragon:       ")
                    .withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(hasKilledIce(stack) ? "✔" : "✘")
                            .withStyle(hasKilledIce(stack) ? ChatFormatting.GREEN : ChatFormatting.RED)));

            lines.add(Component.literal("  Lightning Dragon: ")
                    .withStyle(ChatFormatting.WHITE)
                    .append(Component.literal(hasKilledLightning(stack) ? "✔" : "✘")
                            .withStyle(hasKilledLightning(stack) ? ChatFormatting.GREEN : ChatFormatting.RED)));

            if (isComplete(stack)) {
                lines.add(Component.literal("✔ READY TO TRANSFORM!")
                        .withStyle(ChatFormatting.GOLD, ChatFormatting.BOLD));
            } else {
                lines.add(Component.literal("Keep in offhand while slaying!")
                        .withStyle(ChatFormatting.DARK_GRAY));
            }
        }
    }
}