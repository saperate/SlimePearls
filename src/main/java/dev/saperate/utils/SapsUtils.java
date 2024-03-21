package dev.saperate.utils;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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

    private SapsUtils(){}
}
