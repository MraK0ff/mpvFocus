# Player Module Rules

Critical subsystem:

* Playback stability has highest priority
* Never risk regressions casually

Always:

* Minimize work on playback callbacks
* Avoid blocking operations
* Handle lifecycle safely
* Keep subtitle/audio sync stable
* Consider buffering edge cases

When changing playback:

1. Explain current architecture
2. Explain threading implications
3. Explain lifecycle implications
4. Mention performance risks
5. Mention regression risks

Avoid:

* Unsafe coroutine usage
* Main-thread blocking
* Unnecessary allocations during playback
* Repeated polling loops
* Memory leaks

Prefer:

* Event-driven architecture
* Stable playback state
* Lightweight overlays
* Predictable lifecycle management
