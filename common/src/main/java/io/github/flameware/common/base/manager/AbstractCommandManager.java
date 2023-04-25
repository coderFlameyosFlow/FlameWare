package io.github.flameware.common.base.manager;

import io.github.flameware.common.AbstractArgumentParser;
import io.github.flameware.common.annotations.Async;

import lombok.Getter;

import revxrsal.asm.MethodCaller;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractCommandManager<P> {
    @Getter
    private final P plugin;
    @Getter
    protected AbstractArgumentParser parser;
    protected final Map<String, Long> cooldownMap = new HashMap<>();

    protected AbstractCommandManager(P plugin) {
        this.plugin = plugin;
    }

    public void setCommandArgumentParser(AbstractArgumentParser parser) {
        this.parser = parser;
    }

    public void runMethodsAsync(Object object) {
        for (Method method : object.getClass().getDeclaredMethods()) {
            if (method.isAnnotationPresent(Async.class)) {
                MethodCaller caller = MethodCaller.wrap(method);
                CompletableFuture.runAsync(() -> caller.call(object));
            }
        }
    }
}
