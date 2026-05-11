# mpvFocus Development Rules

Repository:
https://github.com/MraK0ff/mpvFocus

Project type:

* Android TV media player
* Fork of mpvEx
* Kotlin + Jetpack Compose + libmpv
* TV-first architecture

Core mission:
Build the best open-source mpv-based Android TV player with excellent remote-control UX, playback stability, and modern TV-focused architecture.

# Core Priorities

Priority order:

1. Playback stability
2. Android TV usability
3. D-pad navigation
4. Performance on low-end TV hardware
5. Maintainable architecture
6. Clean Compose implementation
7. Mobile compatibility

# Android TV Requirements

Always assume:

* User uses remote control
* User sits far from screen
* User does NOT use touch input
* Device may have weak CPU/GPU
* Device may have limited RAM

Required:

* Focusable UI elements
* Predictable focus traversal
* Focus restoration
* TV-safe spacing
* Large touch/focus targets
* Overscan-safe layouts
* Remote-friendly navigation
* Smooth playback overlays

Never:

* Introduce touch-only interactions
* Create hidden gestures
* Create focus traps
* Break D-pad navigation
* Block playback thread
* Add heavy dependencies

# Compose Rules

Always:

* Minimize recompositions
* Prefer immutable UI state
* Use remember carefully
* Use derivedStateOf where appropriate
* Use stable models
* Keep composables small and reusable

Avoid:

* Massive composables
* Unnecessary state hoisting
* Nested recomposition-heavy layouts
* Expensive calculations inside composables

# Focus System Rules

All interactive UI must:

* Be reachable via D-pad
* Be focusable
* Support focus restoration
* Have deterministic navigation

Use:

* FocusRequester
* focusProperties
* Modifier.focusable()
* onPreviewKeyEvent

Avoid:

* Implicit focus behavior
* Broken directional navigation
* Focus loss after recomposition

# Playback Rules

Playback stability is CRITICAL.

Never:

* Interrupt playback unnecessarily
* Block mpv render thread
* Introduce expensive operations on playback callbacks
* Break subtitle/audio synchronization

Always:

* Handle lifecycle correctly
* Handle backgrounding safely
* Handle overlay visibility efficiently
* Consider buffering edge cases

# Code Quality Rules

Always:

* Generate production-ready code
* Include imports
* Explain architectural decisions
* Mention edge cases
* Mention performance implications

Prefer:

* Small modular changes
* Reusable abstractions
* Explicit naming
* Clear state management

Avoid:

* Placeholder implementations
* Fake TODO logic
* Dead code
* Duplicate logic

# UI Design Rules

Use:

* 10-foot TV UI principles
* Large readable typography
* High contrast
* Minimal clutter
* Clear focus indicators

Avoid:

* Mobile-first layouts
* Tiny controls
* Dense menus
* Hidden controls

# Git Workflow

Preferred branch naming:

* feature/*
* fix/*
* refactor/*
* perf/*
* tv/*

Commit style:

* feat:
* fix:
* refactor:
* perf:
* ui:
* tv:

# Before Writing Code

Always:

1. Analyze existing implementation
2. Explain current issue
3. Explain root cause
4. Propose architecture
5. Then generate code

Never immediately rewrite large systems without analysis first.
