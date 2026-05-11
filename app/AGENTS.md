# App Module (mpvFocus)

This is the root application module containing all major features.

## Role
Orchestrates:
- UI layer (Compose)
- Player integration (mpv)
- Data flow between modules
- Navigation and application state

## Core principles

### 1. Separation of concerns
- UI must NOT contain business logic
- Domain logic must stay in domain layer
- Playback logic must remain isolated

### 2. TV-first architecture
- Every feature must support D-pad navigation
- Focus must be predictable and recoverable
- No touch-only flows

### 3. Stability over complexity
- Do not over-engineer refactors
- Preserve playback stability
- Avoid large architectural rewrites unless necessary

## AI behavior rules
When modifying app-level code:
1. Understand feature scope first
2. Identify impacted modules (ui/player/networking)
3. Ensure no playback regression risk
4. Prefer incremental changes
5. Always consider TV UX impact

## Forbidden
- UI + business logic mixing
- Direct mpv manipulation from UI layer
- Global state abuse