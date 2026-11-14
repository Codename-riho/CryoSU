//! UI-independent logic of the CryoSU manager, ported from the Kotlin app.
//! Std-only on purpose: it builds and tests anywhere, and the UI shell
//! (`cryo-app`) depends on it.

pub mod nav;
pub mod susfs;
pub mod theme;
