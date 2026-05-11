---
trigger: always_on
---

# Architecture Rules

## Structure
- UI layer (Compose)
- Domain layer (business logic)
- Data layer (repositories)
- Playback layer (mpv integration)

## Principles
- Unidirectional data flow
- Clear separation of concerns
- No direct UI → mpv coupling

## Dependency rules
- UI depends on domain only
- Domain does not depend on UI
- Playback is isolated subsystem

## Forbidden
- Business logic inside composables
- Direct database access from UI
- Tight coupling between modules

## Goal
Maintain scalable architecture for long-term TV app evolution