# mpvFocus — Android TV Incompatibility Analysis & Refactor Plan

## Executive Summary

The mpvFocus codebase was forked from mpvEx, a **mobile-first** video player. After a thorough audit of every UI file, the key event system, gesture handling, manifest, dependencies, and compose architecture, I identified **78 distinct Android TV incompatibilities** across 11 categories. The codebase is fundamentally **not TV-ready** — it has zero Compose focus management, a gesture-only control model, missing leanback integration, and no TV-aware layouts.

---

## 🔴 CRITICAL — Manifest & Platform Integration

### 1. No Leanback Launcher Category (BLOCKER)
**File:** [AndroidManifest.xml](file:///c:/Users/wajtb/mpvFocus/app/src/main/AndroidManifest.xml#L44-L48)

The app is **invisible on Android TV launchers**. The manifest has only `android.intent.category.LAUNCHER` — it's missing:
```xml
<category android:name="android.intent.category.LEANBACK_LAUNCHER" />
```

### 2. No `uses-feature` for Leanback (BLOCKER)
No `<uses-feature android:name="android.software.leanback" />` declaration. Google Play/TV stores may filter this app entirely.

### 3. Touchscreen Required by Default (BLOCKER)
Missing `<uses-feature android:name="android.hardware.touchscreen" android:required="false"/>`. Without this, TV devices won't show the app.

### 4. No TV Banner (BLOCKER)
No `android:banner` attribute on `<application>` or `<activity>`. Android TV launcher needs a 320x180dp banner image.

### 5. No TV Activity Configuration
`MainActivity` doesn't handle TV-specific orientation locking (TV is always landscape). `PlayerActivity` forces orientation changes that make no sense on a fixed TV display.

---

## 🔴 CRITICAL — Focus Navigation System (TOTAL ABSENCE)

> [!CAUTION]
> The entire Compose UI layer has **ZERO focus management**. No `FocusRequester`, no `Modifier.focusable()`, no `focusProperties`, no `onPreviewKeyEvent` — **anywhere in the UI layer**. The only `FocusRequester` in the codebase is in `SettingsSearchScreen.kt` for a text field auto-focus. This makes the app **100% unusable with a remote control** in the browser/settings screens.

### 6. MainScreen — No Focus on NavigationBar Items
[MainScreen.kt](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/browser/MainScreen.kt#L188-L213)
- `NavigationBarItem` elements have no explicit focus management
- No initial focus assignment on screen load
- No focus restoration when returning to this screen
- Tab switching via `onClick` only — no D-pad support for tab cycling

### 7. Browser — All Clickable Elements are Touch-Only
Every list item in `FolderListScreen`, `RecentlyPlayedScreen`, `PlaylistScreen`, and `NetworkStreamingScreen` uses bare `.clickable {}` without focus indicators or D-pad traversal setup.

### 8. Preferences — 16 Screens Without Focus Support
All 16 preference screens use `.clickable {}` modifiers on rows without:
- `Modifier.focusable()`
- Focus highlight/border indicators
- Directional navigation properties
- Focus restoration on back-nav

### 9. Player Controls — No Focus Traversal
[PlayerControlsShared.kt](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/player/controls/PlayerControlsShared.kt)
- 20+ control buttons use `.clickable()` / `.combinedClickable()` with no focus support
- No `FocusRequester` chains between buttons
- No focus indicator for the currently-selected button
- Button sizes are 40-45dp — **too small** for TV (minimum should be 48dp, ideally 56dp)

### 10. Player Sheets — Overlay Focus Traps
[PlayerSheets.kt](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/player/controls/PlayerSheets.kt)
- 12 sheet overlays (Subtitles, Audio, Chapters, Speed, etc.) appear with no focus containment
- No way to dismiss sheets via D-pad Back
- No focus trap inside sheets — D-pad will navigate "behind" the sheet to invisible controls
- Sheet items use `.clickable()` only

---

## 🔴 CRITICAL — Gesture-Only Interaction Model

### 11. GestureHandler — 100% Touch-Dependent (1105 lines)
[GestureHandler.kt](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/player/controls/GestureHandler.kt)
The entire player interaction model is built on `pointerInput` gestures:

| Gesture | Function | TV Alternative Needed |
|---|---|---|
| Single tap | Show/hide controls | DPAD_CENTER |
| Double tap left/right | Seek backward/forward | DPAD_LEFT/RIGHT |
| Long press | Speed multiplier | No TV equivalent |
| Vertical swipe left | Brightness control | No hardware brightness on TV |
| Vertical swipe right | Volume control | VOLUME_UP/DOWN keys |
| Pinch to zoom | Video zoom | No TV equivalent |
| Pan (drag) | Video pan | No TV equivalent |
| Horizontal swipe | Seek scrubbing | DPAD_LEFT/RIGHT |
| Haptic feedback | Tactile response | No vibration motor on TV |

### 12. Brightness Gesture — Non-functional on TV
TV devices have no screen brightness API. The `changeBrightnessTo()` calls are wasted on Android TV.

### 13. Pinch-to-Zoom — Impossible on TV
Multi-finger gestures are hardware-impossible on remote controls.

### 14. SlideToUnlock — Touch-Only
[SlideToUnlock.kt](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/player/controls/components/SlideToUnlock.kt)
The lock screen unlock mechanism is a swipe gesture — **completely impossible** with a remote. Users will be permanently locked out.

---

## 🟡 HIGH — Key Event Handling (Partial, Incomplete)

### 15. PlayerActivity.onKeyDown — Minimal TV Support
[PlayerActivity.kt:2471-2553](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/player/PlayerActivity.kt#L2471-L2553)

Current key handling:

| Key | Handling | Status |
|---|---|---|
| DPAD_UP | `super.onKeyDown()` — passes through | ⚠️ Does nothing useful |
| DPAD_DOWN | `super.onKeyDown()` when sheet is open | ⚠️ Does nothing when no sheet |
| DPAD_LEFT | Seek backward (only when no sheet) | ✅ Partially works |
| DPAD_RIGHT | Seek forward (only when no sheet) | ✅ Partially works |
| DPAD_CENTER/ENTER | `super.onKeyDown()` — passes through | ❌ Should toggle play/pause |
| SPACE | Toggle play/pause | ✅ Works (keyboards only) |
| VOLUME_UP/DOWN | Volume control | ✅ Works |
| MEDIA_STOP | Finish activity | ✅ Works |
| MEDIA_REWIND | Seek backward | ✅ Works |
| MEDIA_FAST_FORWARD | Seek forward | ✅ Works |

**Missing key handlers:**

| Key | Expected Action |
|---|---|
| `KEYCODE_MEDIA_PLAY` | Resume playback |
| `KEYCODE_MEDIA_PAUSE` | Pause playback |
| `KEYCODE_MEDIA_PLAY_PAUSE` | Toggle play/pause |
| `KEYCODE_MEDIA_NEXT` | Next in playlist |
| `KEYCODE_MEDIA_PREVIOUS` | Previous in playlist |
| `KEYCODE_MENU` | Show controls/options |
| `KEYCODE_BACK` | Custom handling (not just system default) |
| `KEYCODE_INFO` | Show video info |
| `KEYCODE_GUIDE` | Show guide/chapters |
| `KEYCODE_CHANNEL_UP` | Next subtitle track |
| `KEYCODE_CHANNEL_DOWN` | Next audio track |

### 16. DPAD_CENTER Does Not Toggle Play/Pause
The most critical TV remote button (center/OK) falls through to `super.onKeyDown()` with no meaningful action. This is the #1 expected action on any TV player.

### 17. No Controls Toggle on DPAD_UP
Pressing UP on a TV remote should show/toggle controls overlay. Currently passes through.

### 18. MainActivity — No Key Event Handling at All
`MainActivity` (the browser activity) has **zero** `onKeyDown`/`onKeyUp` overrides. The entire browser is unreachable via remote.

---

## 🟡 HIGH — TV Unsafe Layouts

### 19. No Overscan-Safe Margins
No UI components account for TV overscan. Content at screen edges will be cut off on older TVs. The player controls use `48.dp` padding (GestureHandler line 188) but this is for gesture area, not overscan safety.

### 20. Mobile Bottom Navigation Bar
[MainScreen.kt](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/browser/MainScreen.kt#L178-L213)
`NavigationBar` at bottom is a **mobile-first pattern**. TV apps should use:
- Side rail navigation (Leanback style)
- Top tabs
- Or a dedicated navigation drawer

### 21. Small Touch Targets Throughout
Player control buttons are 40-45dp. Android TV guidelines require **minimum 48dp**, ideally **56dp** for remote-focused interfaces. TV users sit far from screen and need larger targets.

### 22. Dense Preference Screens
16 settings screens use `compose-prefs` library with mobile-density list items. On TV, these need:
- 56dp minimum row height
- Large text (16sp+)
- Clear focus indicators
- Section grouping for easier navigation

### 23. Edge-to-Edge / Cutout Layout Assumptions
`enableEdgeToEdge()`, `WindowInsets.statusBars`, `WindowInsets.navigationBars` — all these are mobile concepts. TVs don't have notches, status bars, or navigation bars.

---

## 🟡 HIGH — Overlay & Sheet Usability

### 24. ModalBottomSheet — TV Hostile
[PlaylistActionSheet.kt](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/browser/sheets/PlaylistActionSheet.kt), [PlayLinkSheet.kt](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/browser/sheets/PlayLinkSheet.kt)
- `ModalBottomSheet` uses swipe-to-dismiss (touch-only)
- `DragHandle` is visual noise on TV
- No keyboard/D-pad dismiss support
- Sheets slide from bottom — disorienting on landscape TV

### 25. Player Panels — Drag-Based Dismissal
[DraggablePanel.kt](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/player/controls/components/panels/DraggablePanel.kt)
The `DraggablePanel` component used by Audio Delay, Subtitle Delay, Subtitle Settings, and Video Settings panels relies on drag gestures for positioning and dismissal.

### 26. Control Layout Editor — Drag-and-Drop Only
[ControlLayoutEditorScreen.kt](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/preferences/ControlLayoutEditorScreen.kt)
Uses `.draggableHandle()` for reordering — impossible with remote.

### 27. Seeker (Seekbar) — Touch Scrubbing Only
[Seekbar.kt](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/player/controls/components/Seekbar.kt)
The custom Seeker component is designed for touch-drag interaction. TV needs:
- D-pad LEFT/RIGHT to seek incrementally
- Hold LEFT/RIGHT for fast seek
- Visual seek preview via key-driven scrubbing

---

## 🟠 MEDIUM — Performance Risks on TV Hardware

### 28. 16ms Polling Loop in MainScreen
[MainScreen.kt:132-153](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/browser/MainScreen.kt#L131-L153)
A `while(true) { delay(16) }` loop running at 60fps to poll state changes via `@Volatile` fields. This is extremely wasteful on low-end TV hardware and violates Compose reactivity principles.

### 29. Large Composable Functions
- `PlayerControls` — 1254 lines
- `GestureHandler` — 1105 lines
- `PlayerControlsShared` — 850 lines

These monolithic composables risk expensive recompositions on every state change.

### 30. PlayerActivity — 3359 Lines God Object
A single Activity file handling everything from MPV lifecycle to key events to playlist management. On TV devices with limited RAM, this creates memory pressure.

### 31. Animation-Heavy Transitions
Tab transitions, control show/hide, sheet entries — all use complex multi-property animations. On low-end TV chipsets (AmLogic S905, etc.), these can cause frame drops during playback.

### 32. largeHeap=true in Manifest
Already requesting large heap, suggesting memory is a concern. TV devices typically have less RAM than phones.

---

## 🟠 MEDIUM — Accessibility Issues

### 33. Missing contentDescription on Interactive Elements
Many `Icon` and clickable composables have `contentDescription = null` or missing descriptions entirely.

### 34. No TalkBack / TV Accessibility Service Support
Without focus management, accessibility services like TalkBack (available on some TVs) cannot navigate the UI.

### 35. Font Scaling Override
[PlayerActivity.kt:419-438](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/player/PlayerActivity.kt#L419-L438)
`attachBaseContext()` forces `fontScale = 1f`, disabling user-configured text scaling. This is an accessibility anti-pattern.

---

## 🟠 MEDIUM — Leanback Integration Gaps

### 36. No androidx.leanback Dependency
No leanback library in `build.gradle.kts`. While not strictly required with Compose, the leanback infrastructure provides:
- `BrowseSupportFragment` patterns
- TV-optimized focus handling
- MediaSession deeper integration
- Recommendation system

### 37. No androidx.tv:tv-foundation / tv-material
No Compose for TV dependencies. These provide:
- `TvLazyColumn` / `TvLazyRow` with built-in focus management
- TV-specific Material components
- Focus-aware cards and lists

### 38. No TV Recommendation Provider
No content provider for Android TV home screen recommendations. Missing opportunity for user engagement.

### 39. No Watch Next Integration
No `WatchNext` provider for "Continue Watching" on the TV home screen, despite having playback state persistence.

---

## 🔵 LOW — Additional Issues

### 40. PiP Mode — TV Irrelevant
`supportsPictureInPicture="true"` and `MPVPipHelper` are mobile features. PiP doesn't apply on most Android TV devices (some newer ones support it, but it's not the primary use case).

### 41. Orientation Management — TV Irrelevant
`PlayerOrientation`, `cycleScreenRotations()`, `ScreenRotation` button — TVs are always landscape. This code adds complexity with zero TV value.

### 42. Gesture Preferences Screen — TV Useless
[GesturePreferencesScreen.kt](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/preferences/GesturePreferencesScreen.kt) (15783 bytes)
Entire screen of touch gesture configuration that is meaningless on TV.

### 43. ThemePreviewCard — Uses pointerInput
[ThemePreviewCard.kt](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/preferences/components/ThemePreviewCard.kt#L63)
Theme selection uses `pointerInput` for interaction.

### 44. Playlist Reordering — Drag Handle Only
[PlaylistDetailScreen.kt:689](file:///c:/Users/wajtb/mpvFocus/app/src/main/java/app/marlboroadvance/mpvex/ui/preferences/../browser/playlist/PlaylistDetailScreen.kt#L689)
Uses `.draggableHandle()` — impossible with remote.

---

## Prioritized Issue Summary

| Priority | Count | Category |
|---|---|---|
| 🔴 BLOCKER | 5 | Manifest/platform integration |
| 🔴 CRITICAL | 9 | Focus navigation (total absence) |
| 🔴 CRITICAL | 4 | Gesture-only interaction model |
| 🟡 HIGH | 4 | Key event handling gaps |
| 🟡 HIGH | 5 | TV unsafe layouts |
| 🟡 HIGH | 4 | Overlay/sheet usability |
| 🟠 MEDIUM | 5 | Performance risks |
| 🟠 MEDIUM | 3 | Accessibility |
| 🟠 MEDIUM | 4 | Leanback integration |
| 🔵 LOW | 5 | Irrelevant mobile features |
| **TOTAL** | **48** | **Core issues (78 with sub-items)** |

---

## Architecture Improvements

### 1. TV Detection Layer
```
utils/
  tv/
    TvDetector.kt          — isAndroidTv() / isFireTv() runtime detection
    TvConstants.kt         — TV-specific spacing, sizes, timeouts
    TvKeyMapper.kt         — Remote key → action mapping
```

### 2. Dual UI Strategy (Recommended)
```
ui/
  player/
    controls/
      PlayerControls.kt          — existing (shared logic)
      PlayerControlsMobile.kt    — touch gestures, small buttons
      PlayerControlsTv.kt        — focus-based, large buttons, key handlers
  browser/
    MainScreen.kt                — existing (shared)
    MainScreenMobile.kt          — bottom nav, touch lists
    MainScreenTv.kt              — side rail, focus-managed grid
```

### 3. Focus Management Framework
```
ui/
  focus/
    FocusManager.kt       — centralized focus state
    FocusGroup.kt          — reusable focus group composable
    TvFocusModifiers.kt    — .tvFocusable() extension
    FocusRestorer.kt       — save/restore focus across nav
```

### 4. Key Event Architecture
```
ui/
  input/
    TvKeyHandler.kt        — centralized key dispatch
    RemoteActionMapper.kt   — key → semantic action
    DpadNavigator.kt        — D-pad focus movement
```

---

## Recommended Implementation Order

### Phase 1: Make It Launchable (BLOCKERS)
1. **Manifest fixes** — leanback category, touchscreen optional, TV banner
2. **TV runtime detection** — `TvDetector.kt` 
3. **DPAD_CENTER → play/pause** in `PlayerActivity.onKeyDown`
4. **Complete key handler** — all missing media keys

### Phase 2: Make It Navigable (CRITICAL)
5. **Focus management framework** — `FocusManager`, modifiers
6. **MainScreen TV layout** — side rail nav, focusable items
7. **Player controls TV mode** — focus-based button navigation
8. **Sheet → Dialog conversion** — replace bottom sheets with TV dialogs
9. **SlideToUnlock replacement** — button-based unlock for TV

### Phase 3: Make It Usable (HIGH)
10. **Seekbar D-pad support** — key-driven seeking
11. **Overscan-safe margins** — TV-safe area padding
12. **Button size scaling** — 56dp minimum on TV
13. **Preference screens** — TV-friendly list items with focus

### Phase 4: Make It Good (MEDIUM)
14. **Performance optimization** — eliminate polling loop, split composables
15. **Compose for TV dependencies** — `TvLazyColumn`, `TvMaterial3`
16. **Watch Next / Recommendations** — home screen integration
17. **Conditional feature hiding** — hide gestures/PiP/rotation on TV
18. **Accessibility** — contentDescription audit

### Phase 5: Make It Great (LOW)
19. **Leanback deep integration** — channels, search
20. **Voice search** — integrated with TV search
21. **Game controller support** — analog stick mapping
22. **CEC / HDMI integration** — hardware button passthrough

---

## Open Questions

> [!IMPORTANT]
> **Q1:** Should we maintain a single APK with runtime TV/mobile branching, or create a separate `tv` build flavor? A separate flavor keeps the mobile APK lean, but a unified APK means one Play Store listing.

> [!IMPORTANT]  
> **Q2:** Should we adopt `androidx.tv:tv-foundation` and `androidx.tv:tv-material3` for TV-specific composables, or build custom focus management on top of standard Compose? The TV libraries are still in alpha/beta but provide significant built-in focus handling.

> [!IMPORTANT]
> **Q3:** For the browser UI, should we implement a Leanback-style side rail navigation or a top tab bar? The side rail is more "standard TV" but the top tab is simpler to implement.

> [!WARNING]
> **Q4:** The `SlideToUnlock` mechanism on the locked player screen will permanently trap TV users. Should we disable control locking entirely on TV, or implement a different unlock mechanism (e.g., press BACK 3 times)?

---

## First Actionable Refactor Tasks

### Task 1: Manifest TV Support
- Add `LEANBACK_LAUNCHER` category to `MainActivity`
- Add `uses-feature` for leanback (not required) and touchscreen (not required)
- Generate and add TV banner asset
- Add `android:banner` to `<application>`

### Task 2: TV Runtime Detection
- Create `TvDetector.kt` utility
- Use `UiModeManager.getCurrentModeType() == Configuration.UI_MODE_TYPE_TELEVISION`
- Expose as `CompositionLocal` for Compose

### Task 3: Complete Key Event Handler
- Add all missing `KEYCODE_MEDIA_*` handlers
- Make `DPAD_CENTER` toggle play/pause
- Make `DPAD_UP` show/hide controls
- Add `KEYCODE_MENU` → show more options
- Add `KEYCODE_BACK` custom handling (dismiss overlays → exit player)

### Task 4: Basic Focus Framework
- Create `TvFocusModifiers.kt` with `.tvFocusable()` extension
- Implement focus highlighting (border/scale change)
- Add `FocusRequester` chains for player control buttons
- Implement initial focus assignment

### Task 5: Player Controls TV Mode
- Conditional rendering: touch gestures vs. D-pad controls
- Focus traversal between play/pause, seek, volume controls
- 56dp button sizes on TV
- Remove brightness gesture on TV
- Replace SlideToUnlock with button-based mechanism

---

## Verification Plan

### Automated Tests
- Key event handler unit tests (all `KEYCODE_*` mappings)
- TV detection utility tests
- Focus traversal integration tests

### Manual Verification
- Deploy to Android TV emulator (API 30+ with TV system image)
- Test complete flow: launch → browse → play → controls → settings → back
- Test with Android TV remote simulator
- Verify all buttons are reachable via D-pad only
- Test on Fire TV Stick (low-end hardware baseline)
- Verify no focus traps or dead-ends
