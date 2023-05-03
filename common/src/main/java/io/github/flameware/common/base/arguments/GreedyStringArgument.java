package io.github.flameware.common.base.arguments;

import io.github.flameware.common.annotations.Join;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public final class GreedyStringArgument {
    private GreedyStringArgument() {
    }

    @Contract("_, _, _ -> new")
    public static @NotNull String join(@NotNull String[] args,
                                       @NotNull Join join,
                                       int startIndex) {

        int maxChars;
        try {
            maxChars = Integer.parseInt(join.maxChars());
        } catch (NumberFormatException exception) {
            maxChars = 500;
        }
        StringBuilder builder = new StringBuilder(maxChars);
        int argsLength = args.length;
        for (int i = startIndex; i < argsLength; i++) {
            builder.append(args[i]);
            if (i < argsLength - 1) {
                builder.append(join.delimiter());
            }
        }
        String string = builder.toString();

        byte[] stringBytes = string.getBytes();
        if (join.maxChars().isEmpty() || maxChars <= stringBytes.length) {
            return string;
        }
        return new String(stringBytes, 0, maxChars);
    }
}
