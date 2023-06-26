package io.github.flameware.common.sender;

import org.jetbrains.annotations.Nullable;

import java.util.UUID;

/**
 * The command actor that basically works with each and every platform.
 * <p>
 * Extend this to be able to make your own command actor for your platform, if there isn't one provided.
 * @author FlameyosFlow
 */
public interface CommandActor<T> {
    /**
     * Send the user a message that takes in a normal string.
     * Coloring may differ depending on the platform.
     * @param message the message to send.
     */
    void reply(String message);

    /**
     * Gets the command actor's class, default platforms currently include:
     * <p>
     * Spigot: SpigotCommandActor.class
     * @return the command actor's class.
     */
    Class<?> getSenderClass();

    /**
     * Gets the class that the command actor depend on, default platforms currently include:
     * <p>
     * Spigot: CommandSender
     * @return the platform command executor class
     */
    Class<?> getWrapperClass();

    /**
     * Gets the name of the actor.
     * <p>
     * <strong>WARNING:</strong> when possible, please avoid for persistent storage as this is not unique,
     * please use UUIDs or Snowflake IDS or something that doesn't change.
     * @return the name of the actor
     */
    String getName();

    /**
     * Gets the executor of the command, never returns null
     * @return the sender object
     */
    T getSender();

    /**
     * Checks if the user has the permission to run the command.
     * @param permission the permission
     * @return true if the user has the permission
     */
    boolean hasPermission(String permission);
}
