# FlameWare
Performant, high code quality and extensive command framework.

*Okay, cool, I have heard this a million times, you probably use recursion, have terrible code quality and have a massive jar size*

Look, don't assume, other command frameworks are obviously great and try their best, although:

Here is what makes you choose FlameWare over others:

## Performance
Here is what makes FlameWare top in performance:
### No recursion, Absolutely none.
That is right, you will find NO recursion whatsoever, FlameWare is recursion-free,
And we really want to.

### Terrible readability internally, Right?
Actually, that is certainly wrong, and it's quite the opposite, FlameWare tries to take advantage of Java and other small Libraries to not only improve performance,
but to even improve readability in some cases, we use constant-lookups instead of linear-lookups using ConcurrentHashMaps!

FlameWare is very based on caches and computing and lambdas, making it better maintainable for even better code in the future!

### Complete thread-safety.
FlameWare tries to achieve thread safety without synchronization, we do not use normal HashMaps or synchronized maps/hashtables,
We use ConcurrentMaps and even [Caffeine](https://github.com/ben-manes/caffeine) to try achieve the best performance and best thread-safety without making the code look like it was made by a premature optimizer.

### Einstein-level code
Yes, it is smart, FlameWare uses [Caffeine](https://github.com/ben-manes/caffeine) for cooldowns and much more stuff to assure your memory/cpu usage doesn't skyrocket or having terrible performance, for example:

FlameWare uses with a casual for loop, to loop through all parameters at once while
even getting other variables using that for loop, so much more would be written and done with a for each.

And for now, FlameWare remains as one of the smartest command frameworks ever started.

### Constant searching instead of Linear searching.
FlameWare is cache-based, caffeine-based AND hash-based, including the execution of parsing!

FlameWare uses a lot of thread-safe operations, and FlameWare utilizes the smart
features to improve functionality and performance.

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
    public void coolCommand(Player player, @Arg(id = "number") int number, @Arg(id = "message") @Join String hey) {
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

Also, yes, for each parameter you do need @Arg, but the reason for that is to add boilerplate, to make less boilerplate in modern java! (this is still getting checked)

## Extensibility and Customizability
FlameWare is more of the "I will let you customize and extend features" than "I will have complete features" kind of framework.

We plan to have you find a lot of Extensibility in FlameWare,
and customizability, heck, we want you to be able to customize everything!

From command execution, to even parsing execution, you can choose for them to be normal/sync, or async/parallel for all commands,
If you don't want that, there is also an annotation called @Async which allows you to execute some commands asynchronously!

# Installation
Fine, you convinced me, how do I try this?
`REPO SOON!`
