# Development Journal

## Software Stack

- **Language**: Java 17
- **Platform**: Android (minSdk 34 / targetSdk 35)
- **Build**: Gradle 8.7, AGP 8.5.2
- **UI**: View-based (Material3, AppCompat)
- **Networking**: OkHttp 4.12.0

## Key Decisions

**minSdk 34 (Android 14+)**
Matches the project template (androdash). Keeps the codebase clean, avoids compatibility shims. Revisit if the user base turns out to include older devices.

**OkHttp over HttpURLConnection**
The forum requires persistent cookie-based sessions. OkHttp's `CookieJar` API handles this cleanly with minimal boilerplate. HttpURLConnection would require manual cookie management.

**Java over Kotlin**
Consistent with the existing androdash codebase maintained by the same author.

**No Jetpack Navigation component**
Phase 1 scope doesn't warrant the added complexity. BottomNavigationView with manual fragment transactions is sufficient for three top-level destinations.

## Core Features

- **Calendar**: Upcoming club events with date, name, author, description, participants; push notifications
- **Private Messages**: Sender/receiver threads, messenger-style UI, attachment support
- **Filebase**: File listings with GPX map preview and download
