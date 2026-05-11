---
trigger: always_on
---

# Compose Performance Rules

## Core principle
Minimize recomposition cost at all times.

## Rules
- Use remember only when necessary
- Use stable models for UI state
- Prefer stateless composables
- Avoid deep nesting of composables

## State rules
- Avoid unnecessary state hoisting
- Use derivedStateOf for computed state
- Avoid recomposition-triggering mutations

## UI rendering
- Keep composables small
- Avoid heavy work inside composable bodies
- Move logic to ViewModels or domain layer

## Forbidden
- Heavy loops inside composables
- Allocations during recomposition
- Unstable lambda usage in hot UI paths