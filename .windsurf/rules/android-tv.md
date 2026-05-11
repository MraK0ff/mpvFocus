---
trigger: always_on
---

# Android TV Rules

This project is TV-first.

All generated UI must:

* Support D-pad navigation
* Support focus restoration
* Avoid touch assumptions
* Be usable from long viewing distance
* Use large focus targets
* Preserve predictable navigation

Playback controls must:

* Work fully with remote
* Support center/play-pause
* Support directional seek
* Support menu button
* Support back button behavior

Never generate:

* Swipe-only interactions
* Gesture-only controls
* Tiny clickable areas
* Mobile-exclusive UX patterns

Always think about:

* Leanback compatibility
* Android TV launcher behavior
* Low-end TV hardware
* Overscan-safe layouts
* Remote latency
