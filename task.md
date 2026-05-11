# Android TV Compatibility — Execution Tasks

## Phase 1: Make It Launchable (BLOCKERS)

- [x] **Task 1: Manifest TV Support**
  - [x] Add `LEANBACK_LAUNCHER` category to MainActivity
  - [x] Add `uses-feature` for leanback (not required)
  - [x] Add `uses-feature` touchscreen not required
  - [x] Add `android:banner` to application tag
  - [x] Generate TV banner asset (320x180dp)

- [x] **Task 2: TV Runtime Detection**
  - [x] Create `TvDetector.kt` utility
  - [x] Expose as `CompositionLocal`

- [x] **Task 3: Complete Key Event Handler**
  - [x] DPAD_CENTER → toggle play/pause
  - [x] DPAD_UP → show/hide controls
  - [x] Add MEDIA_PLAY, MEDIA_PAUSE, MEDIA_PLAY_PAUSE
  - [x] Add MEDIA_NEXT, MEDIA_PREVIOUS
  - [x] Add KEYCODE_MENU → show more options
  - [x] Add KEYCODE_INFO → show media info

- [x] **Task 4: Basic Focus Framework**
  - [x] Create `TvFocusModifiers.kt`
  - [x] Create focus highlight composable
  - [ ] Add initial focus to MainScreen NavigationBar

- [x] **Task 5: Player Controls TV Mode**
  - [x] Conditional gesture handler (skip on TV)
  - [x] SlideToUnlock → button unlock on TV
  - [x] Larger button sizes on TV

## Phase 2+ (Deferred)
- [ ] MainScreen TV layout
- [ ] Sheet → Dialog conversion
- [ ] Seekbar D-pad support
- [ ] Overscan-safe margins
- [ ] Compose for TV dependencies
- [ ] Watch Next / Recommendations
