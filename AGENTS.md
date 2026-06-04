# AGENTS.md — Emotional Damage (Minecraft 26.1 Fabric Mod)

## Quick Reference

- **Build**: `./gradlew.bat build`
- **Compile only**: `./gradlew.bat compileJava` — **ALWAYS use this first to validate code**, it's faster than full `build` and catches all errors. Only run `build` for final verification.
- **Java**: 25 (`--release 25`), JDK at `C:/Program Files/Java/jdk-26.0.1`
- **Minecraft**: 26.1 (intermediary/yarn mappings), Fabric Loader 0.19.2, Fabric API 0.145.1+26.1
- **Game run config**: `run/` directory, use `gradlew.bat runClient`

## Source Set Layout

Uses `loom.splitEnvironmentSourceSets()`. Server/shared code goes in `src/main/`; client-only code in `src/client/`. Each has its own mixin config:

| Source set | Mixin config | Package |
|---|---|---|
| `src/main/` (common) | `emotional-damage.mixins.json` | `online.inklingyoshi.asian.mixin` |
| `src/client/` (client only) | `emotional-damage.client.mixins.json` | `online.inklingyoshi.asian.client.mixin` |

Entrypoints: `EmotionalDamage` (main), `EmotionalDamageClient` (client), `EmotionalDamageDataGenerator` (datagen).

## Critical 26.1 API Differences

Minecraft 26.1 has several API departures from 1.20/1.21 that will cause silent build failures:

### NBT is gone — use Components
```java
// ❌ Old: ItemStack NBT
stack.getOrCreateTag().putInt("key", val);
stack.getOrCreateTag().getInt("key");

// ✅ 26.1: DataComponents.CUSTOM_DATA
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.nbt.CompoundTag;

// Read
CustomData data = stack.get(DataComponents.CUSTOM_DATA);
CompoundTag tag = data != null ? data.copyTag() : new CompoundTag();
int xp = tag.getIntOr("key", 0);

// Write
tag.putInt("key", xp);
stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
```

### Player save/load uses ValueInput/ValueOutput — NOT CompoundTag
```java
// ❌ Old
@Inject(method = "readAdditionalSaveData", ...)
private void read(CompoundTag tag, CallbackInfo ci) { ... }

// ✅ 26.1
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

@Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
private void read(ValueInput input, CallbackInfo ci) {
    myField = input.getIntOr("key", 0);
}

@Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
private void write(ValueOutput output, CallbackInfo ci) {
    output.putInt("key", myField);
}
```

### No `serverLevel()` on ServerPlayer — use `level()`
```java
// ❌ player.serverLevel()    — doesn't exist
// ✅ player.level()          — returns ServerLevel on server
ServerLevel level = (ServerLevel) player.level();
```

### No `getDayTime()` — use `getLevelData().getGameTime()`
```java
long gameTime = serverLevel.getLevelData().getGameTime();
long currentDay = gameTime / 24000;
```

### DamageSource takes `Holder<DamageType>`, not `DamageType`
```java
// Don't unwrap the Holder. Just pass getOrThrow() result directly:
return new DamageSource(
    level.registryAccess()
        .lookupOrThrow(Registries.DAMAGE_TYPE)
        .getOrThrow(key),    // returns Holder<DamageType>
    attacker, attacker
);
```

### `hurtEnemy()` returns void (not boolean)
```java
@Override
public void hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) { }
```

### `Direction.getNearest()` takes 4 params
```java
// ✅ Direction.getNearest(dx, dy, dz, fallback)
Direction.getNearest((int)dx, 0, (int)dz, Direction.NORTH);
```

### `isClientSide` is now a method
```java
// ✅ level.isClientSide()
```

## Mixin Conventions

- Always use `@Unique` for injected fields on target classes
- For cross-mixin access to `@Unique` fields/methods, define an interface in `attack/` and have the mixin implement it. Cast via `instanceof`:
  ```java
  if (player instanceof IPlayerStatsTracker tracker) { tracker.emotionalDamage$method(); }
  ```
- Multiple mixins on `Player.class` are used throughout — this is fine as long as injection points don't conflict
- Mixins on `AbstractContainerMenu.class` fire for all menu types — always check `instanceof` at the top of injectors
- `@Shadow` on `NonNullList` fields fails — prefer shadowing public methods (`getSlot(int)`) over fields

## Package Structure

```
attack/     — items, entities, damage types, insult system, slipper, interfaces
command/    — /difficulty command
difficulty/ — ModDifficulty enum, state, networking, pending
mixin/      — common mixins
network/    — custom packet payloads
util/       — DifficultyHelper
client/     — client entrypoint, client mixins (separate source set!)
```

## Data-driven Damage Types

Custom damage types are JSON files in `src/main/resources/data/emotional-damage/damage_type/`. Each maps to a translation key:
```json
{
  "exhaustion": 0.1,
  "message_id": "tooFat",
  "scaling": "when_caused_by_living_non_player",
  "death_message_type": "default"
}
```
Translations go in `assets/emotional-damage/lang/en_us.json` with keys `death.attack.<message_id>` and `death.attack.<message_id>.player`.

Register keys in `ModDamageTypes.java` via `ResourceKey.create(Registries.DAMAGE_TYPE, id)`. Use `ModDamageTypes.source(level, attacker, key)` to get a `DamageSource`.

## Custom Entities

Register in `ModEntities.java` using `ResourceKey.create(BuiltInRegistries.ENTITY_TYPE.key(), id)` for the builder. Then `Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type)`.
No renderer = invisible projectile (acceptable for MVP).

## Existing Mixin Targets (avoid conflicts)

| Common (`src/main/`) | Client (`src/client/`) |
|---|---|
| Player, ServerPlayer, Mob, EnderDragon, Phantom, FoodProperties, Item, AbstractContainerMenu, ServerPlayerGameMode, PhantomSpawner, MerchantMenu | DifficultyButtons, CreateWorldScreenGameTab |
