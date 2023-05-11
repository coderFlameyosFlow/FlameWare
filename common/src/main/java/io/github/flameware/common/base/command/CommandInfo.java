package io.github.flameware.common.base.command;

/**
 * Command information to simplify the code of making new commands.
 */
public record CommandInfo(String name,
                          String description,
                          String permission,
                          String usage,
                          String[] aliases) {
}
