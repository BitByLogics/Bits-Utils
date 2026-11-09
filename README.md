<div align="center">
<img src="https://i.imgur.com/hy4lp6A.jpeg" style="width: 20%;" alt="Icon">

## Bit's Utils
[![Build](https://img.shields.io/github/actions/workflow/status/BitByLogics/Bits-Utils/maven.yml?branch=master)](https://github.com/BitByLogics/Bits-Utils/actions)
![Issues](https://img.shields.io/github/issues-raw/BitByLogics/Bits-Utils)
[![Stars](https://img.shields.io/github/stars/BitByLogics/Bits-Utils)](https://github.com/BitByLogics/Bits-Utils/stargazers)
[![Chat)](https://img.shields.io/discord/1310486866272981002?logo=discord&logoColor=white)](https://discord.gg/syngw2UQUd)

Essential utils for Java and Paper development, simplifying common tasks and supporting efficient plugin creation.
</div>

## Modules

| Artifact | Contents |
|----------|----------|
| `bits-utils-common` | Platform independent utilities: actions, context, registries, reflection, strings, time, numbers. |
| `bits-utils-bukkit` | Paper utilities: items, inventories, holograms, triggers, conditions, messages, locations, config. |

## Requirements

- Java 21
- Paper 1.21.11 or newer (Folia is supported, all scheduling uses Paper's region, entity and global schedulers)
- PlaceholderAPI is optional. Without it only `%player%` is replaced in action text.

## Install

```xml
<repositories>
    <repository>
        <id>minebit</id>
        <url>https://repo.minebit.dev/releases</url>
    </repository>
</repositories>

<dependencies>
    <dependency>
        <groupId>net.bitbylogic</groupId>
        <artifactId>bits-utils-common</artifactId>
        <version>3.0.0</version>
    </dependency>
    <dependency>
        <groupId>net.bitbylogic</groupId>
        <artifactId>bits-utils-bukkit</artifactId>
        <version>3.0.0</version>
    </dependency>
</dependencies>
```

The libraries are not shaded. Shade and relocate them into your own plugin, or load them through a shared library.

## Quick start

Items:

```java
ItemStack item = ItemBuilder.of(Material.DIAMOND)
        .name("<aqua>Shiny")
        .lore("<gray>Very shiny")
        .build();
```

Holograms. A `Hologram` is a shareable definition and `spawn` creates a live `HologramInstance`:

```java
Hologram hologram = new Hologram(HologramLine.of("<yellow>Hello"))
        .addLine(HologramLine.of("<gray>World"))
        .schedule(plugin);

HologramInstance instance = hologram.spawn(location);
instance.cleanup();
```

Triggers. A `Trigger` is a definition and `createInstance` creates the live state:

```java
List<Trigger> triggers = TriggerParser.loadTriggers(section, context -> {
});

TriggerInstance instance = triggers.getFirst().createInstance();
instance.activate(plugin);
```

Actions and conditions are registered by id in `ActionRegistry` and `ConditionRegistry`, and parsed from configuration with `BukkitActionParser` and `ConditionParser`.

## Notes

- Parsers and serializers return `Optional` or log a warning instead of throwing for bad input.
- `ContextKey` does not register itself. Register keys you want to look up by id with `ContextRegistry.register`.
- See the [changelog](CHANGELOG.md) for everything that changed in 3.0.0.

## Build

```
mvn clean verify
```

This compiles both modules, runs the tests, writes coverage reports to `target/site/jacoco` in each module and runs SpotBugs.

## Release

Set the version in the poms and push a tag named exactly the version, with no prefix. The release workflow checks the tag against the pom, deploys to repo.minebit.dev and creates the GitHub release.

## License

MIT, see [LICENSE](LICENSE).
