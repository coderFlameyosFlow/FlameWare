package io.github.flameware.common.base;

import lombok.*;

@RequiredArgsConstructor(staticName = "of")
@EqualsAndHashCode
@Getter
@ToString
public final class CommandDefinition {
    private final String name;
    private final String description;
    private final String permission;
    private final String[] aliases;
    private final String usage;
}
