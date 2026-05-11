---
trigger: always_on
---

# Playback Safety Rules (mpv core)

Playback stability is the highest priority.

## Critical rules
- Never block mpv playback thread
- Never introduce heavy computation in playback callbacks
- Avoid UI logic inside playback pipeline

## Lifecycle safety
- Handle pause/resume correctly
- Handle backgrounding safely
- Prevent leaks from player lifecycle

## State handling
- Playback state must be immutable where possible
- Avoid frequent state updates during playback

## Forbidden
- Reinitializing player unnecessarily
- Frequent restart of playback pipeline
- Blocking IO on main thread during playback

## Performance rules
- Prefer event-driven updates
- Batch UI updates outside playback loop