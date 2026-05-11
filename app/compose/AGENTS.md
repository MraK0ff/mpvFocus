# Compose Layer Rules (mpvFocus)

This module contains shared Compose components and UI primitives.

## Responsibility
- Reusable UI components
- UI building blocks
- Theme-aware components
- TV-safe composables

## Core rules

### 1. Performance first
- Minimize recomposition
- Avoid heavy logic in composables
- Use remember / derivedStateOf properly

### 2. Stateless design
- Prefer stateless composables
- Push state upward (ViewModel or domain layer)
- Avoid hidden internal state

### 3. TV compatibility
- All components must support focus
- Ensure focus visibility
- Large touch/focus targets

## Focus rules
- Every interactive component MUST be focusable
- Focus must be visually clear
- Focus order must be predictable

## Forbidden
- Business logic inside composables
- Heavy computations in UI layer
- Touch-only components
- Gesture-only interactions

## Performance risks to avoid
- Nested recomposition chains
- Unstable lambdas in hot UI paths
- Excessive state observers