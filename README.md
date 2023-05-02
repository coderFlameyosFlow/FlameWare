# FlameWare - REWRITE IN PROGRESS
Performant, high code quality and extensive command framework, Inspired by High Quality Command Frameworks like [Lamp](https://www.spigotmc.org/threads/lamp-a-highly-flexible-extremely-powerful-and-customizable-commands-framework.544055/) and [mCommands](https://www.spigotmc.org/threads/mcommands.600957/) to ***try*** fix their (current) issues

*Okay, cool, I have heard this a million times, you probably use recursion, have terrible performance, readability, understandability and/or code quality, slow support, non-extensive and/or have a massive jar size*

Look, don't assume, other command frameworks are obviously great and try their *best*, although:

Here is what makes you choose **FlameWare** over **others**:

## Performance
Here is why **FlameWare** tops in performance:
### No recursion, Absolutely none.
That is right, you will find **no recursion whatsoever**, FlameWare is recursion-free,
And we really want to.

### Decent Internal Readability
Actually, that is certainly wrong, and it's quite the *opposite*

*but mister FlameyosFlow, how can performance make readability better? that's nearly impossible*

Your answer: Well I defeated **nearly impossible**!

FlameWare tries to take advantage of Java and other stuff to not only improve performance,
but to even improve readability in some cases!

FlameWare is very based on caches and map computing, in some cases it lowers the amount of lines and makes it more readable or understandable, making it better maintainable for even better code in the future!

### Complete thread-safety.
FlameWare tries to achieve thread safety **without synchronization**, we do not use normal HashMaps or synchronized maps/hashtables,
We use ConcurrentMaps to try achieve the best performance and best thread-safety without making the code look like it was made by a premature optimizer.

Wow, awesome, right? anyways let's go to the next reason

### Constant searching instead of Linear searching.
FlameWare is cache-based AND hash-based, especially for argument parsing and command registration

FlameWare uses a lot of thread-safe operations, and FlameWare utilizes the smartest
features to improve functionality, extensibility and performance.

## Development time and Readability
Here is why **FlameWare** tops in Readability and Development Time:

FlameWare aims to drastically decrease development time,
When it comes to development time, FlameWare does more work internally so you do less work externally!

Here is what some command frameworks make you do:
```java
@Command(aliases = { ... })
// optionals
@Usage("...")
@CommandPermission("...")
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
    @CommandPermission("...")
    @Description("...")
    public void anotherCoolCommand(Player player, int number, @Optional @Join String hey) {
        ...
    }
}
```
Wow, that's a lot of work, right?

Well, this is what you do in FlameWare:
```java
@Command(name = "...", /* optionals */ desc = "...", perm = "...", usage = "...", aliases = "...")
@MoreAnnotationsFromFlameWareIfNeeded
public class MyCommand {
    @Command
    @MoreAnnotationsFromFlameWareIfNeeded
    public void coolCommand(Player player, int number, @Default OfflinePlayer target, @Default @Join String hey) {
        ...
    }

    @Subcommand(name = "...", /* optionals */ desc = "...", perm = "...", aliases = "...")
    @MoreAnnotationsFromFlameWareIfNeeded
    public void coolDudeCommand(Player player, String[] args) {
        ...
    }
}
```

18 lines to 14 lines, and thats just a sample, imagine real code, man.

## Extensibility and Customizability
**Lamp *does* top FlameWare in extensibility** as of now since it is a **much earlier project**, but here is why **FlameWare** is so good in Extensibility and Customizability:

FlameWare is more of the "I will let you customize and extend features" than "I will have complete features" kind of framework, which is also why it is so small

We plan to have you find a lot of Extensibility in FlameWare, even in the first versions!

## Support
Here is why **FlameWare** tops in support:

Yes, I am active every hour on discord and I am willing to help anyone.

There is quick support as long as you ping me! *(unless there is an emergency)*

## Jar Size
Speaking of small, here is the Size.

*Okay, this has to be big with all these features.. right?*

Here is why **FlameWare** tops in File size:

As of now, here are the size of the modules with ***no minimization***: (these are the last checked size, could be higher or even lower)
- Spigot & Common Combined: 39.1kb

FlameWare is light despite its features!
### How is it really done?
First of all, there are no implementations, everything is compiled only, here are the used libraries:
- Lombok - compiled only
- Jetbrains annotations - compiled only

Other reasons may be:
- Because of the incredibly low amount of classes, interfaces, enums and annotations combined (22 as of now for Common + Spigot)
- Because FlameWare removes all of the meta data using ShadowJar that usually comes normally in a JAR file
- Because FlameWare uses as much Java as possible, since the JDK has all the java classes, meaning, using java classes will be better for file size
- Because FlameWare attracts the eye of people looking for extensibility, not complete features, leaving more space for your projects!
- and more!

# Installation
Fine, you convinced me, how do I try this?

#### REPO SOON!
