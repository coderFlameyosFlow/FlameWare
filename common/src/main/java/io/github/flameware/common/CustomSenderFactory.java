package io.github.flameware.common;

public interface CustomSenderFactory<Sender, PlatformSender> {
    Sender createCustomSender(PlatformSender sender);
}
