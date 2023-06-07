# FlameWare - IN PROGRESS
Performant, high code quality and extensive command framework, Inspired by High Quality Command Frameworks such as [Lamp](https://www.spigotmc.org/threads/lamp-a-highly-flexible-extremely-powerful-and-customizable-commands-framework.544055/) to ***try*** fix their (current) issues

*Okay, cool, I have heard this a million times, you probably have terrible performance, readability, understandability and/or code quality, slow support, non-extensive and/or have a massive jar size*

Look, don't assume, other command frameworks are obviously great and try their *best*, although:

Here is what makes you choose **FlameWare** over **others**:

## Performance
Here is why **FlameWare** tops in performance:
### Java 17
FlameWare is Java 17, to improve API safety, security, to promote new versions, and of course, performance.

it also helps developers suffer less by using dated features, such as records, dated expressions, sealed interfaces/classes, and newer features in java classes.

Don't try to change my opinion, FlameWare will only be other versions if necessary

### Decent Internal Readability
*but mister FlameyosFlow, how can performance make readability better? that's hard*

Your answer: Well I defeated **hard**!

FlameWare tries to take advantage of Java and other stuff to not only improve performance,
but to even improve readability in some cases!

FlameWare is very based on caches and map computing, in some cases it lowers the amount of lines and makes it more readable or understandable, making it better maintainable for even better code in the future!

### Constant searching instead of Linear searching.
FlameWare is cache-based AND hash-based, especially for argument parsing and command registration

FlameWare uses a lot of thread-safe operations, and FlameWare utilizes the smartest
features to improve functionality, extensibility and performance.

### Smart MethodHandle Usage
Didn't I just tell you how much I *try* utilizing the smartest features to improve performance and so on? that doesn't break here.

The current method handling can only happen for public command methods to significantly improve performance.
Here is the current execution:
```java
MethodHandles.publicLookup().unreflect(Method).invokeExact(Object, ...);
```

.publicLookup() improves performance by doing optimizations for public methods, significantly improving performance more than the traditional reflection actually.

.invokeExact(...) invokes the method with a lower amount of checks than invoke(...), since there's no need for extra checks (since the parameter are parsed from the Method object anyways), it improves performance using invokeExact.

## Development time and Readability
Here is why **FlameWare** tops in Readability and Development Time:

### Less annotations to work with

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

This approach leads to a slightly smaller jar file, easier maintanence *and* better development time

## Extensibility and Customizability
**Lamp *does* top FlameWare in extensibility** as of now since it is a **much earlier project**, but here is why **FlameWare** is so good in Extensibility and Customizability:

### Hash implementations for Everything
Not only does using HashMaps and ConcurrentHashMaps instead of a bunch of switch/if statements improve performance (especially in huge maps) but they even allow for much easier extensibility!

Imagine this for extensibility:
```java
if (...) {
} else if (...) {
} else if (...) {
) else if (...) {
} else if (...) {
} // and so on
```

That would be slower and *MUCH* harder to add extensive features to it.
Just 1 or 2 if statements with a map can be so much faster and more extensive too

### Suggestion and Parsing API
There is an API for suggestions *and* parsing, thanks to the hash implementations we talked about above.

this is at 1.0.0, that was added at the very first version, imagine how many features it is right now (I'm looking at the future)

## Support
Here is why **FlameWare** tops in support:

Yes, I am active every hour on discord and I am willing to help anyone.

There is quick support as long as you ping me! *(unless there is an emergency)*

## Jar Size
Speaking of small, here is the Size.

*Okay, this has to be big with all these features.. right?*

Here is why **FlameWare** tops in File size:

As of now, here are the size of the modules with ***no minimization***: (these are the last checked size, could be higher or even lower)
- Spigot & Common Combined: 50kb~

FlameWare is light despite its features!
### How is it really done?
First of all, there are no implementations, everything is compiled only, here are the used libraries:
- Lombok - compiled only
- Jetbrains annotations - compiled only
- Classics such as Spigot - compiled only

Other reasons may be:
- Because of the low amoumt of classes, interfaces, enums and annotations combined
- Because FlameWare removes all of the meta data using ShadowJar that usually comes normally in a JAR file
- Because FlameWare uses as much Java as possible, the JDK has all the java classes, meaning, using java classes will be better for file size
- Because FlameWare attracts the eye of people looking for extensibility, not complete features, leaving more space for your projects!
- and more!

# Installation
Fine, you convinced me, this is just too much to resist, how do I try this?

#### REPO SOON!

*Since you kept reading until here, there is a very big surprise at 2.0.0, it will send your heads flying :)*
