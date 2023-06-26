package io.github.flameware.common;

import org.jetbrains.annotations.NotNull;

/**
 * Execute every command {@link CommandExecutionType#SYNC synchronously} or {@link CommandExecutionType#ASYNC asynchronously}, usually selectable via the command manager.
 * <p>
 * The default execution is {@link CommandExecutionType#SYNC synchronous}, so no need to explicitly set it.
 *
 * @author FlameyosFlow
 */
public enum CommandExecutionType {
    /**
     * The synchronous execution, slower but usually safer than {@link CommandExecutionType#ASYNC}
     * <p>
     * This is the default execution, you can explicitly set it, but you don't need to.
     * @see CommandExecutionType#ASYNC
     */
    SYNC,
    /**
     * The asynchronous execution, more things are done at once but less safe than {@link CommandExecutionType#SYNC}.
     * @see CommandExecutionType#SYNC
     */
    ASYNC;

    public static CommandExecutionType getDefault() {
        return SYNC;
    }

    public static CommandExecutionType from(@NotNull String name) {
        return valueOf(name.toUpperCase());
    }
}
