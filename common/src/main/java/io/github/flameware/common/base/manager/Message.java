package io.github.flameware.common.base.manager;

import lombok.Getter;
import lombok.Setter;

/**
 * Customizable messages for users to be able to customize all messages/errors without having to change the internal code
 * <p>
 * This is so good to have in many cases, but also so good to have in the common code, because it's the best way to handle all
 * <p>
 * messages and errors.
 * @author FlameyosFlow
 */
public enum Message {
    NOT_ALLOWED("You are not allowed to execute this command."),
    NOT_ENOUGH_PERMISSION("You don't have enough permission to execute this command."),
    NOT_IN_RANGE("Number %arg% is not in range %min% to %max% for %arg%."),
    USAGE("Usage: ");

    @Getter @Setter
    @SuppressWarnings("NonFinalFieldInEnum")
    private String message;

    Message(String s) {
        this.message = s;
    }
}
