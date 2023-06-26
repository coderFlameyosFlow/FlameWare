package io.github.flameware.common.base.command;

import lombok.Data;

/**
 * Command information to simplify the code of making new commands.
 * @author FlameyosFlow
 */
@Data
public class CommandInfo {
    private final String name, description, permission, usage;
    private final String[] aliases;
}
