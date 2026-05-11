# UI Layer (mpvFocus)

This module contains screens and UI logic.

## Responsibility
- Screens (browser, player UI, settings)
- Navigation logic
- UI state management
- User interaction handling

## Core principles

### 1. TV-first UX
- Everything must be D-pad navigable
- No touch-only interactions
- Focus must be visible and predictable

### 2. State separation
- UI state comes from ViewModels
- No business logic in UI
- UI is a renderer, not a controller

### 3. Composition discipline
- Keep composables small
- Avoid deeply nested UI trees
- Avoid recomposition-heavy patterns

## Navigation rules
- Back button must always be handled explicitly
- Focus must restore after navigation
- No hidden navigation paths

## Focus system rules
- Every interactive element must be focusable
- Focus order must be deterministic
- Avoid focus traps
- Ensure focus survives recomposition

## Forbidden
- Business logic in UI layer
- Direct mpv interaction
- Gesture-only interactions
- Touch-only flows

## Performance rules
- Avoid heavy recomposition chains
- Minimize state observers in UI
- Avoid unnecessary redraw triggers