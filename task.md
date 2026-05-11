# Android TV Compatibility — Phase 3: Make It Usable

## Phase 3 Goals
Polish the TV experience with complete focus traversal, sheet conversion, seekbar support, and comprehensive overscan handling. The app launches and navigates on TV; now make every interaction smooth.

---

## Task 1: Browser Screens Focus (Continued from Phase 2)

**Current Issue:** All list items use bare `.clickable()` without focus.

- [ ] `FolderListScreen` — add `tvFocusable()` to folder/video items
- [ ] `RecentlyPlayedScreen` — add focus to recent items
- [ ] `PlaylistScreen` — add focus to playlist items
- [ ] `NetworkStreamingScreen` — add focus to stream items
- [ ] Create `TvListItem` reusable component for consistent focus
- [ ] All screens: handle DPAD_BACK for navigation up

---

## Task 2: Player Controls Focus Traversal (Continued from Phase 2)

**Current Issue:** Player buttons have no explicit focus chaining.

- [ ] Add `FocusRequester` chain to bottom control bar
- [ ] Add focus to play/pause, seek, volume buttons
- [ ] Implement focus highlight on all player buttons
- [ ] Ensure focus loops logically (leftmost → rightmost wraps)
- [ ] Add focus to top bar controls (back, title, more options)

---

## Task 3: Sheet → Dialog Conversion for TV (Continued from Phase 2)

**Current Issue:** `ModalBottomSheet` requires swipe-to-dismiss (touch-only).

- [ ] Create `TvAwareSheet` wrapper (sheet on mobile, dialog on TV)
- [ ] Convert `PlayerSheets` to use dialog on TV
- [ ] Add DPAD_BACK dismissal for TV dialogs
- [ ] Ensure focus trap inside TV dialogs
- [ ] Convert browser sheets (PlaylistActionSheet, etc.)

---

## Task 4: Preference Screens Focus (Continued from Phase 2)

**Current Issue:** 16 preference screens with no focus support.

- [ ] Add `tvFocusable()` to all preference rows
- [ ] Create `TvPreferenceItem` wrapper component
- [ ] Add focus to switch/checkbox preferences
- [ ] Add focus to slider preferences (volume/brightness)
- [ ] Add focus to list/dialog preferences

---

## Task 5: Seekbar D-Pad Support (Continued from Phase 2)

**Current Issue:** Custom Seeker is touch-drag only.

- [ ] Add `onPreviewKeyEvent` to Seeker for LEFT/RIGHT seeking
- [ ] Implement incremental seek (5s per press)
- [ ] Implement fast seek (hold LEFT/RIGHT)
- [ ] Visual feedback during key-driven seek
- [ ] Ensure seekbar has focus in player control traversal

---

## Task 6: Overscan-Safe Layouts (Continued from Phase 2)

**Current Issue:** No TV overscan compensation.

- [x] Applied to MainScreen TV layout
- [ ] Apply to PlayerControls top/bottom bars
- [ ] Apply to all dialog/sheet content
- [ ] Test at 48dp and 60dp margins
- [ ] Add `TvSafeArea` composable wrapper

---

## Task 7: TV Polish & Performance (New)

**Goal:** Ensure TV experience is production-ready.

- [ ] Remove polling loop from MainScreen (performance)
- [ ] Add proper content descriptions for accessibility
- [ ] Test focus restoration after rotation/resize
- [ ] Verify no gesture handlers on TV (brightness, zoom, pan)
- [ ] Add TV-specific haptic feedback (if supported)
- [ ] Ensure consistent 56dp touch targets throughout

---

## Task 8: Documentation & Testing (New)

**Goal:** Document TV support and prepare for release.

- [ ] Add TV setup instructions to README
- [ ] Document remote control key mappings
- [ ] Create TV testing checklist
- [ ] Verify no regressions on mobile
- [ ] Test on Android TV emulator (API 30+)

---

## Phase 3 Definition of Done
- [ ] Complete browse → play → settings flow using only D-pad
- [ ] All interactive elements show focus highlight and are reachable
- [ ] No focus traps, unreachable elements, or dead-ends
- [ ] Back button navigates up correctly at every level
- [ ] Sheets/dialogs dismissible via BACK on TV
- [ ] Seekbar fully controllable via D-pad
- [ ] Consistent 48dp+ touch targets, 56dp for primary actions
- [ ] Overscan-safe margins on all screens
- [ ] Mobile experience unchanged (no regressions)
