# Android TV Compatibility — Phase 2: Make It Navigable

## Phase 2 Goals
Enable full D-pad navigation throughout the app. The player works on TV now, but the browser screens (MainScreen, lists, settings) have no focus management.

---

## Task 1: MainScreen TV Layout

**Current Issue:** Bottom navigation bar is mobile-only, no focus support on NavigationBarItem.

- [x] Create TV layout with side rail navigation (`MainScreenTV`)
- [x] Add `tvFocusable()` to navigation items
- [x] Implement focus restoration on tab switch
- [x] Add initial focus to first tab on launch
- [x] Apply overscan-safe padding

**Status:** Complete. MainScreen now branches between mobile (bottom nav) and TV (side rail).

---

## Task 2: Browser Screens Focus

**Current Issue:** All list items use bare `.clickable()` without focus.

- [ ] `FolderListScreen` — add focus to folder/video items
- [ ] `RecentlyPlayedScreen` — add focus to recent items
- [ ] `PlaylistScreen` — add focus to playlist items
- [ ] `NetworkStreamingScreen` — add focus to stream items
- [ ] All screens: handle DPAD_BACK for navigation up

---

## Task 3: Player Controls Focus Traversal

**Current Issue:** Player buttons have no explicit focus chaining.

- [ ] Add `FocusRequester` chain to bottom control bar
- [ ] Add focus to play/pause, seek, volume buttons
- [ ] Implement focus highlight on all player buttons
- [ ] Ensure focus loops logically (leftmost → rightmost wraps)

---

## Task 4: Sheet → Dialog Conversion for TV

**Current Issue:** `ModalBottomSheet` requires swipe-to-dismiss (touch-only).

- [ ] Create `TvAwareSheet` wrapper (sheet on mobile, dialog on TV)
- [ ] Convert `PlayerSheets` to use dialog on TV
- [ ] Add DPAD_BACK dismissal for TV dialogs
- [ ] Ensure focus trap inside TV dialogs

---

## Task 5: Preference Screens Focus

**Current Issue:** 16 preference screens with no focus support.

- [ ] Add `tvFocusable()` to all preference rows
- [ ] Create `TvPreferenceItem` wrapper component
- [ ] Add focus to switch/checkbox preferences
- [ ] Add focus to slider preferences (volume/brightness)

---

## Task 6: Seekbar D-Pad Support

**Current Issue:** Custom Seeker is touch-drag only.

- [ ] Add `onPreviewKeyEvent` to Seeker for LEFT/RIGHT seeking
- [ ] Implement incremental seek (5s per press)
- [ ] Implement fast seek (hold LEFT/RIGHT)
- [ ] Visual feedback during key-driven seek

---

## Task 7: Overscan-Safe Layouts

**Current Issue:** No TV overscan compensation.

- [ ] Apply `overscanSafePadding()` to MainScreen
- [ ] Apply to PlayerControls top/bottom bars
- [ ] Apply to all dialog/sheet content
- [ ] Test at 48dp and 60dp margins

---

## Phase 2 Definition of Done
- [ ] Complete browse → play flow using only D-pad
- [ ] All interactive elements show focus highlight
- [ ] No focus traps or unreachable elements
- [ ] Back button navigates up correctly
- [ ] Sheets dismissible via BACK on TV
