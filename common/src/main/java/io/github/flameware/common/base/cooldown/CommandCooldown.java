package io.github.flameware.common.base.cooldown;

import lombok.*;

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@ToString
@EqualsAndHashCode
@RequiredArgsConstructor(staticName = "of")
@Getter
public class CommandCooldown {
    private final long time;
    @NotNull
    private final TimeUnit unit;
    private final Duration duration = unit.toChronoUnit().getDuration();
}
