# Android TV Module Rules

This directory contains TV-specific functionality.

Always prioritize:

* D-pad navigation
* Focus restoration
* Remote usability
* TV-safe layouts

Use:

* FocusRequester
* focusProperties
* onPreviewKeyEvent
* TV-friendly spacing
* Large focusable components

Avoid:

* Mobile-first assumptions
* Gesture navigation
* Tiny controls
* Complex nested focus paths

All new screens must:

* Be fully navigable via remote
* Restore focus correctly
* Support back navigation
* Avoid focus traps
