package io.github.flameware.common.base.command;

import lombok.Data;

@Data
public class CommandInfo {
    private final String name;
    private final String description;
    private final String permission;
    private final String usage;
    private final String[] aliases;
}
