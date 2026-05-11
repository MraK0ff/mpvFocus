# Networking Layer (mpvFocus)

This module handles all external communication.

## Responsibility
- HTTP requests
- Network streaming (SMB, FTP, WebDAV)
- Remote media access
- API integrations

## Core principles

### 1. Isolation
- No UI logic here
- No Compose dependencies
- No player coupling

### 2. Reliability first
- Network failures must be handled gracefully
- Always assume unstable connections
- Retry logic must be controlled

### 3. Performance
- Avoid blocking main thread
- Use async IO operations
- Cache where appropriate

## Streaming rules
- Streams must not block UI
- Pre-buffering must be lightweight
- Handle partial failures gracefully

## Forbidden
- UI dependencies
- Direct Compose usage
- Synchronous network calls on main thread
- Tight coupling with player logic

## AI behavior rules
When modifying networking:
1. Consider latency and retry behavior
2. Ensure graceful failure handling
3. Avoid breaking streaming compatibility
4. Check memory usage on long sessions