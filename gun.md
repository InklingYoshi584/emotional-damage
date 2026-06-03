# Gun Plan

## Overview
A meme weapon that's a suicide gun — right-click to start a challenge, but you always die.

## Recipe
**1 stick** — shapeless, any slot in a crafting grid.

## Gameplay Flow
```
Right-click with gun
  → 3 random keyboard keys (A-Z), 20 ticks each to press
      → Miss any → "[player] is too slow"
  → Random action challenge (impossible), 20-tick timer
      → Always fails → unique death message
```

The gun does **5000 damage** (flavor/instakill) but no projectile fires — it kills the player.

## State Machine (per-player, server-side)

```
IDLE ──GunItem.use()──► BUTTON[step=0] ──btnPressed──► BUTTON[step=1] ──► BUTTON[step=2] ──► ACTION ──timer>20──► DEAD
                           │                    │                    │
                           └──timer>20──► DEAD   └──timer>20──► DEAD  └──timer>20──► DEAD
```

- Each button: **20 ticks** (1 second) to press the correct key
- If `timer > 20` at any BUTTON step → kill with `too_slow`
- After 3rd button → enter ACTION phase
- After 20 ticks in ACTION → kill with the action-specific death message
- **No way to survive** — all actions are intentionally impossible

## Action Challenges

| Text shown | Death message (after 1 second) |
|------------|-------------------------------|
| Get into Harvard | `[player] was STOOBID` |
| Become a neurologist at 9 | `[player]'s cousin did better` |
| Be a billionaire at 10 | `[player] was a DISAPPOINTMENT` |
| Get Panda Express | `[player] forgot they're Asian` |

## New Damage Types

| message_id | Death message |
|------------|---------------|
| `tooSlow` | `%1$s is too slow` |
| `stoobid` | `%1$s was STOOBID` |
| `cousinDidBetter` | `%1$s's cousin did better` |
| `disappointment` | `%1$s was a DISAPPOINTMENT` |
| `forgotAsian` | `%1$s forgot they're Asian` |

## Architecture

### New files (8 source)

| File | Package | Purpose |
|------|---------|---------|
| `GunItem.java` | `attack` | `extends Item`. `use()` on server: generate 3 random letters (A-Z), send `ShowButtonS2CPacket`, start state machine |
| `GunChallengeState.java` | `attack` | Enum: `IDLE`, `BUTTON`, `ACTION` |
| `GunPackets.java` | `network` | 3 Fabric payload records + `register()` call |
| `ServerGunMixin.java` | `mixin` | `@Mixin(ServerPlayer.class)` — `@Unique` state fields (`gunState`, `gunButtons[3]`, `gunStep`, `gunTimer`, `gunAction`), `tick()` timeout kill |
| `ClientGunTracker.java` | `client/mixin` | Static state: `expectedKeyCode`, `isActive`, `inActionPhase`, `timerFraction` |
| `GunOverlayMixin.java` | `client/mixin` | `@Mixin(InGameHud.class)` — draws button/action overlay at `render` TAIL |
| `GunKeyboardMixin.java` | `client/mixin` | `@Mixin(KeyboardHandler.class)` — `@Inject` HEAD of `onKey()`, checks GLFW key code match, sends `KeyPressedC2SPacket` |

### Packets (3 custom payloads)

| Packet | Direction | Fields |
|--------|-----------|--------|
| `ShowButtonS2CPacket` | Server → Client | `char button`, `int keyCode` — which key to press |
| `ShowActionS2CPacket` | Server → Client | `int actionId` — which action to display |
| `KeyPressedC2SPacket` | Client → Server | (empty) — acknowledges correct key press |

### New data files

| File | Content |
|------|---------|
| `data/emotional-damage/damage_type/too_slow.json` | Damage type |
| `data/emotional-damage/damage_type/stoobid.json` | Damage type |
| `data/emotional-damage/damage_type/cousin_did_better.json` | Damage type |
| `data/emotional-damage/damage_type/disappointment.json` | Damage type |
| `data/emotional-damage/damage_type/forgot_asian.json` | Damage type |
| `assets/emotional-damage/lang/en_us.json` | Add 10 death message translations |
| `data/emotional-damage/recipe/gun.json` | 1 stick → gun |

### Modified files

| File | Change |
|------|--------|
| `attack/ModItems.java` | Add `GUN` registration |
| `attack/ModDamageTypes.java` | Add 5 new `ResourceKey<DamageType>` constants |
| `emotional-damage.mixins.json` | Add `ServerGunMixin` |
| `emotional-damage.client.mixins.json` | Add `ClientGunTracker`, `GunOverlayMixin`, `GunKeyboardMixin` |

## Server Side Implementation Details

### ServerGunMixin fields (on ServerPlayer)

```java
@Unique GunChallengeState gunState = IDLE;
@Unique char[] gunButtons = new char[3];
@Unique int gunStep = 0;
@Unique int gunTimer = 0;
@Unique int gunAction = 0;
```

### tick() logic

```
if gunState == IDLE: skip
gunTimer++

if gunTimer > 20:
    if gunState == BUTTON:
        kill player with "too slow"
        gunState = IDLE
    if gunState == ACTION:
        kill player with action death message
        gunState = IDLE
```

### on KeyPressedC2SPacket

```
gunStep++
gunTimer = 0

if gunStep >= 3:
    gunState = ACTION
    gunAction = random.nextInt(4)
    send ShowActionS2CPacket(gunAction)
    gunTimer = 20  // force kill next tick
else:
    send ShowButtonS2CPacket(gunButtons[gunStep], keyCode)
```

### Kill helper

```java
private void killAndReset(ServerPlayer player) {
    switch (gunState) {
        case BUTTON:
            player.hurt(tooSlowSource, Float.MAX_VALUE);
            break;
        case ACTION:
            player.hurt(actionSource[gunAction], Float.MAX_VALUE);
            break;
    }
    gunState = IDLE;
}
```

## Client Side Implementation Details

### ClientGunTracker

```java
public class ClientGunTracker {
    static boolean isActive = false;
    static int expectedKeyCode = 0;
    static char buttonChar = 0;
    static boolean inAction = false;
    static String actionText = "";
    static float timerFraction = 0f;
    
    static void setButton(char b, int code) { /* ... */ }
    static void setAction(int id) { /* ... */ }
    static void clear() { /* ... */ }
    static void tick() { /* ... */ }
}
```

### GunKeyboardMixin

```java
@Mixin(KeyboardHandler.class)
public class GunKeyboardMixin {
    @Inject(method = "onKey", at = @At("HEAD"))
    private void interceptGunKeys(long window, int key, int scancode, int action, int mods, CallbackInfo ci) {
        if (!ClientGunTracker.isActive || ClientGunTracker.inAction) return;
        if (action != GLFW_PRESS) return; // GLFW_PRESS = 1
        if (key != ClientGunTracker.expectedKeyCode) return;
        ClientPlayNetworking.send(new KeyPressedC2SPacket());
    }
}
```

### GunOverlayMixin

```java
@Mixin(InGameHud.class)
public class GunOverlayMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void renderGunOverlay(GuiGraphics graphics, float tickDelta, CallbackInfo ci) {
        if (!ClientGunTracker.isActive) return;
        // draw semi-transparent dark background
        // if button phase: draw large letter + timer bar
        // if action phase: draw action text + timer bar
    }
}
```

## Recipe JSON

```json
{
  "type": "minecraft:crafting_shapeless",
  "ingredients": [{ "item": "minecraft:stick" }],
  "result": { "id": "emotional-damage:gun", "count": 1 }
}
```
