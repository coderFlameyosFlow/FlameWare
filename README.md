# FlameWare - REWRITE IN PROGRESS
Performant, high code quality and extensive command framework,

inspired by High Quality Command Frameworks like [Lamp](https://www.spigotmc.org/threads/lamp-a-highly-flexible-extremely-powerful-and-customizable-commands-framework.544055/page-3) and [mCommands](https://www.spigotmc.org/threads/mcommands.600957/) to fix their (current) issues

*Okay, cool, I have heard this a million times, you probably use recursion, have terrible code quality and have a massive jar size*

Look, don't assume, other command frameworks are obviously great and try their best, although:

Here is what makes you choose FlameWare over others:

## Performance
Here is what makes FlameWare top in performance:
### No recursion, Absolutely none.
That is right, you will find NO recursion whatsoever, FlameWare is recursion-free,
And we really want to.

### Terrible readability internally, Right?
Actually, that is certainly wrong, and it's quite the opposite, FlameWare tries to take advantage of Java and other stuff to not only improve performance,
but to even improve readability in some cases, we use constant-lookups instead of linear-lookups using ConcurrentHashMaps!

FlameWare is very based on caches and computing and lambdas, making it better maintainable for even better code in the future!

### Complete thread-safety.
FlameWare tries to achieve thread safety without synchronization, we do not use normal HashMaps or synchronized maps/hashtables,
We use ConcurrentMaps to try achieve the best performance and best thread-safety without making the code look like it was made by a premature optimizer.

### Constant searching instead of Linear searching.
FlameWare is cache-based AND hash-based, especially for argument parsing and command registration

FlameWare uses a lot of thread-safe operations, and FlameWare utilizes the smartest
features to improve functionality, extensibility and performance.

## Development time and Readability
FlameWare aims to drastically decrease development time,
When it comes to development time, FlameWare does more work internally so you do less work externally!

Here is what some command frameworks make you do:
```java
@Command(aliases = { ... })
@Usage("...")
@Permission("...")
@Description("...")
public class MyCommand {
    @Default
    @MoreAnnotationsFromCmdFrameworkIfNeeded
    public void coolCommand(Player player, int number, @Join String hey) {
        ...
    }

    @Subcommand(aliases = { ... })
    // optionals
    @Usage("...")
    @Permission("...")
    @Description("...")
    public void anotherCoolCommand(Player player, int number, @Join String hey) {
        ...
    }
}
```
Wow, that's a lot of work, right?

Well, this is what you do in FlameWare:
```java
@Command(name = "...", /* optionals */ description = "...", permission = "...", usage = "...", aliases = "...")
@MoreAnnotationsFromFlameWareIfNeeded
public class MyCommand {
    @Command
    @MoreAnnotationsFromFlameWareIfNeeded
    public void coolCommand(Player player, int number, @Default OfflinePlayer target, @Default @Join String hey) {
        ...
    }

    @Subcommand(name = "...", description = "...", permission = "...", aliases = "...")
    @MoreAnnotationsFromFlameWareIfNeeded
    public void coolDudeCommand(Player player, String[] args) {
        ...
    }
}
```

18 lines to 14 lines, and thats just a sample, imagine real code, man.

## Extensibility and Customizability
FlameWare is more of the "I will let you customize and extend features" than "I will have complete features" kind of framework.

We plan to have you find a lot of Extensibility in FlameWare,
and customizability, heck, we want you to be able to customize everything!

You will soon customize messages, 

## Support
Yes, I am literally active every hour on discord and I am willing to help anyone.

There is quick support as long as you ping me! *(Unless there is an emergency)*

## Jar Size
*Okay, this has to be big with all these features.. right?*

As of now, here are the size of the modules (common included in all of them):
- Spigot: 41kb (without ASM)

FlameWare is light despite its features!
### How is it really done?
First of all, there are hardly to no implementations, everything is either compiled or a small implementation, here are the used libraries:
- ASM - implementation (5 classes in total)
- Lombok - compiled only
- Jetbrains annotations - compiled only

and then of course, classics, like spigot, etc.

There are also a low amount of classes to be able to make the jar size smaller.

# Installation
Fine, you convinced me, how do I try this?

#### REPO SOON!
