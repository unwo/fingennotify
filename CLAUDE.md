# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FingenNotify is an Android app that intercepts system notifications and forwards them to the [Fingen](https://play.google.com/store/apps/details?id=com.yoshione.fingen) personal finance app. It captures bank notifications and relays them as incoming messages. Entirely local — no network communication.

## Build Commands

```bash
./gradlew build          # Full build (lint + compile + package)
./gradlew assembleDebug  # Debug APK only → app/build/outputs/apk/debug/
./gradlew clean          # Clean build artifacts
```

No tests are configured in this project.

## Build Configuration

- Android Gradle Plugin 8.2.2, Gradle 8.5, compileSdk 34, minSdk 25
- AndroidX libraries (appcompat:1.6.1, recyclerview:1.3.2, constraintlayout:2.1.4, core:1.12.0)
- Package ID: `com.unwo.FingenNotifyHomeCredit`, namespace: `com.unwo.FingenNotify`

## Architecture

**Single-activity app** with a background notification listener service.

### Core Data Flow

1. `notifyListenerService` (extends `NotificationListenerService`) intercepts system notifications
2. Matches against user-configured package rules in SQLite (wildcard `*` matches all)
3. Optionally saves to local DB and/or sends broadcast Intent to Fingen (`com.yoshione.fingen.intent.action.CREATE_SMS`)
4. Broadcasts `com.unwo.FingenNotify.servicebackbroadcast` to update the UI
5. `Main` activity receives broadcast via registered `BroadcastReceiver` and refreshes lists

### Key Source Files (`app/src/main/java/com/unwo/FingenNotify/`)

- **Main.java** — Single Activity with two RecyclerViews (configured apps + captured notifications), menu-driven UI
- **notifyListenerService.java** — Background service that captures and filters notifications
- **Db.java** — Database access wrapper (CRUD for packages, notifications, preferences)
- **DBHelper.java** — SQLite schema (3 tables: `package`, `notify`, `preference`)
- **SendFingen.java** — Sends broadcast Intent to Fingen app
- **Constants.java** — All hardcoded values, menu IDs, preference keys, broadcast actions
- **autostart.java** — BroadcastReceiver that starts the service on `BOOT_COMPLETED`
- **AdapterPackage.java / AdapterNotify.java** — RecyclerView adapters with context menu support

### Database Tables

- `package` — Configured app filters (package name + sender name)
- `notify` — Captured notifications (package, sender, message, datetime)
- `preference` — Boolean settings stored as SQLite rows (not SharedPreferences)

### Patterns

- Service↔Activity communication via local broadcast Intents
- Boolean preferences (`savenotify`, `sendfingen`) stored in SQLite `preference` table, toggled via options menu
- Context menus implemented through ViewHolder's `OnCreateContextMenuListener`
- Bilingual: English (`values/strings.xml`) + Russian (`values-ru/strings.xml`)
