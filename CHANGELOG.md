# Changelog

## 3.0.0

### Breaking

**Holograms**
- `Hologram` is now a shareable definition. `spawn(...)` returns a `HologramInstance`.
- `cleanup()`, `teleport()`, `updateSpacing()`, `getTop()` and `getBase()` moved from `Hologram` to `HologramInstance`. `HologramInstance` also has `getDisplay(lineId)`, `getLocation()` and `refresh()`.
- `HologramLine` no longer holds a display, hologram back-reference, line offset or update time. `getTransformation()` and `getMetadata()` return copies.
- `spawn(location, plugin, viewers)` requires a plugin.

**Triggers**
- `Trigger` is a definition. Runtime state lives in `TriggerInstance`, created with `createInstance()`. `clone()` is removed.
- `setActive`, `isActive`, `setRepeatable`, `onDeactivate` and `trigger` moved from `Trigger` to `TriggerInstance` (`activate`, `deactivate`, `trigger`).
- `EventTrigger` is replaced by `EventTriggerInstance`. `PlayerChatTrigger` has a matching `PlayerChatTriggerInstance`.
- `SimpleTrigger` children are added with `addChild` and read as a copy. `equals` and `hashCode` are removed.
- `DeferredTrigger.whenAvailable` is removed. An unresolved deferred trigger logs a warning and creates an instance that never fires.

**Registries and context**
- `ContextKey` no longer registers itself. Call `ContextRegistry.register(key)`. `register` returns whether the key was added.
- `Context` is copied when executing parsed actions and conditions. `ContextBuilder.build()` returns an independent copy.
- `ParsedAction.execute` returns `boolean`.
- Action, condition, trigger, context-key and reference registries share `IdRegistry` and `ReferenceRegistry`. Ids are case-insensitive, including condition references.
- `TriggerRegistry.whenAvailable` stays. Parsers return `Optional` instead of throwing.

**Positions and locations**
- `SafeLocation` is removed. Use `WorldPosition`.
- `WorldPosition.of(String)` is replaced by `WorldPosition.parse(String)`, which returns `Optional`.
- `LocationUtil.decode` and `decodeChunk` return `Optional`. New `unpackX`, `unpackY`, `unpackZ`, `unpackChunkX` and `unpackChunkZ`.
- `OffsetLocation.parse` returns empty for fewer than three values. `ChunkPosition.distance` returns `OptionalDouble`.
- `ColorUtil.hexToRGB` is replaced by `ColorUtil.parseHex`, which returns `Optional`.

**Items and inventories**
- `ItemStackUtil.isSimilar`, `flagsMatch`, `loreMatches` and `spawnerMatches` moved to `ItemMatchUtil`. `isSimilar` also compares enchantments, custom model data, persistent data and damage.
- Spawner helpers moved to `SpawnerItemUtil`. `getSpawnerEntity` returns `Optional`.
- `InventoryUtil.serializeInventory` and `deserializeInventory` moved to `InventorySerializer`. `deserialize` returns `false` on invalid data. `InventoryUtil.getViewInventory` is removed.
- `ItemSerializer` returns `Optional` and uses one format for single items and arrays. Existing single-item base64 still loads.
- `ItemBuilder.from` copies the stack it is given.
- `ItemStackConfigSerializer` logs and skips invalid values instead of throwing. An invalid material returns empty.
- `ConfigSerializer.deserialize(section, placeholders...)` is the abstract method. `deserialize(section)` delegates to it.

**Other**
- `bits-utils-bukkit` is no longer shaded. It depends on `bits-utils-common` like any library.
- `DefaultConfigWrapper` unwraps a map section into a `LinkedHashMap` of items. It used to try to instantiate the requested class, which never worked for `Map`.
- `StringUtil.join` no longer throws. `StringUtil` reading time is in seconds.
- Progress bars show N filled symbols for progress N.
- Hologram update loop, cooldown callbacks and chat triggers use Paper's region, entity and global schedulers.

### Added
- `PlaceholderUtil`. PlaceholderAPI is optional, and only `%player%` is replaced without it.
- `OffsetLocationConfigSerializer.serialize`.
- `LocationUtil.persistentKey`, which builds valid keys for world names with capitals or spaces.
- `ObjectReference.get()` now uses its cache. `invalidateCache()` and `refresh()` reset it.
- `ColorUtil` console formatting codes and legacy hex.
- Source and javadoc jars, JaCoCo coverage reports and SpotBugs checks on every build.
- Items made with `ItemBuilder` are non-italic by default. An explicit `<italic>` is kept.

### Fixed
- `FileUtil.deleteDirectory` logs a warning when a file cannot be deleted.
- `ConfigurableMetadata` no longer re-reads its section and file between null checks.
- `ItemStackConfigSerializer` rejects air as a material.
- `ParsedAction` and `ParsedCondition` no longer mutate the caller's context.
- `ItemStackUtil.setLore` no longer mutates the list it is given.
- Event triggers register once and unregister on deactivate.
- Hologram line spacing was counted twice on spawn.
- A data supplier on a hologram line was called twice per update.
- `FileUtil` follows symbolic links safely.
- `Pair.equals` handles null members.
- `ReflectionUtil` constructor matching handles wrapper types.
