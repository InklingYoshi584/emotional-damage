# Emotional Damage

A Minecraft mod inspired by Steven He — everything is an EMOTIONAL DAMAGE.

## Features

- **Slipper** — throwable weapon that grows stronger as you land hits. XP tiers unlock item throwing (800) and homing (1000). Loyalty return at 600 XP.
- **Item throwing** — right-click throw any non-food, non-equippable item after unlocking.
- **Totem punishment** — totem of undying activates then kills you 1 second later.
- **Difficulty** — new "asian" and "ASIAN" difficulty levels via the options screen.
- **Gun** — craft with 1 stick. Right-click starts an impossible challenge: press 3 correct keys in 1s each, then fail an impossible action. Always dies.
- **Anti-idle** — standing still for 3 seconds hurts you.
- **Item tax** — 2% of dropped items are destroyed on pickup.
- **Cheater detection** — entering the End Portal with cheats enabled kills you and crashes your game.
- **Damage types** — 20+ custom death messages (TOO FAT, MINIMUM WAGE, ART DEGREE, STOOBID, etc.).

## Build

Requires JDK 26 at `C:/Program Files/Java/jdk-26.0.1`.

```
./gradlew.bat compileJava   # fast validation
./gradlew.bat build          # full build
./gradlew.bat runClient      # run in dev
```

Output JAR: `build/libs/emotional-damage-1.0.0.jar`

## Dependencies

- Minecraft 26.1
- Fabric Loader 0.19.2+
- Fabric API 0.145.1+26.1

## License

CC0 1.0 Universal — see LICENSE.
