# CryoSU manager, Rust rewrite (branch `cryo-rs`)

Goal: replace the Kotlin/Compose manager (`manager/`, ~290 files) with Rust.

Layout
- `manager-rs/cryo-core`: UI-independent logic, std-only, unit-tested in CI.
- `manager-rs/cryo-app`: Android shell (egui/eframe + android-activity). Experimental: it has not
  been compiled yet (no crates.io / NDK access when it was written). CI job `android` is
  `continue-on-error` until the first green build.

Ported so far
- SuSFS feature simplifier (`susfs.rs`), theme defaults (`theme.rs`), tab navigation (`nav.rs`).
- App shell: bottom navigation, Home heading and SuSFS chips.

Not ported yet (still Kotlin in `manager/`)
- Root/KsuCli bridge, signature check, Superuser list, Module manager, SuSFS config screens,
  Settings, install/flash flows, WebUI (needs a WebView host; egui has none).
- Packaging: the Rust `.so` must be wrapped in an APK (NativeActivity manifest, signing).

Plan: port logic into `cryo-core` first, screen by screen, then replace each Compose screen.
