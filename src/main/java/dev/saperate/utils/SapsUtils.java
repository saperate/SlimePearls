package dev.saperate.utils;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public final class SapsUtils {


    /**
     * Will automatically detect and use expanded tooltips.
     * If the tooltip you are trying to use has args, separate default args and
     * expanded args by putting a "\n" in between.
     * If there is no extended tooltip, the default one will be used
     */
    public static void addToTooltip(List<Text> tooltip, String key, Object... args) {
        int initialSize = tooltip.size();


        if (Screen.hasShiftDown()) {
            addTranslatable(tooltip, key.concat(".expanded"),
                    Arrays.stream(args).skip(
                            IntStream.range(0, args.length)
                                    .filter(i -> "\n".equals(args[i]))
                                    .findFirst()
                                    .orElse(0)
                            + 1 //The index found is the separator, so we need to go 1 up
                    ).toArray());
        }
        if (tooltip.size() == initialSize) {
            addTranslatable(tooltip, key, args);
        }
    }

    /**
     * Does not automatically use the expanded tooltips
     * @return the number of args used in the translatable
     */
    public static int addTranslatable(List<Text> tooltip, String key, Object... args) {
        String raw = Text.translatable(key, args).getString();
        if (raw.equals(key)) {
            return 0;
        }
        for (String str : raw.split("<br>")) {
            tooltip.add(Text.of(str));
        }
        return raw.split("%d").length;
    }

    public static int getCustomInt(ItemStack itemStack, String name) {
        NbtComponent data = itemStack.get(DataComponentTypes.CUSTOM_DATA);
        if (data != null) {
            return data.copyNbt().getInt(name);
        }
        return 1;
    }

    public static void setCustomInt(ItemStack itemStack, int val, String name) {
        NbtComponent component = itemStack.get(DataComponentTypes.CUSTOM_DATA);

        NbtCompound data;
        if(component != null){
            data = component.copyNbt();
            data.putInt(name,val);
        }else {
            data = new NbtCompound();
            data.putInt(name,val);
        }

        itemStack.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(data));
    }

    private SapsUtils(){}
}
