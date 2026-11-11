# CryoSU
<img align='right' src='CryoSU.svg' width='220px' alt="CryoSU Icon">

A fork of [BakaSU](https://github.com/Baka-SU/BakaSU) (formerly known as ReSukiSU).

**English** | [简体中文](./zh/README.md)

A [`tiann/KernelSU`](https://github.com/tiann/KernelSU)'s downstream, added some personal changes.

[![Latest release](https://img.shields.io/github/v/release/Codename-riho/CryoSU?label=Release&logo=github)](https://github.com/Codename-riho/CryoSU/releases/latest)
[![Latest CI build (nightly.link)](https://img.shields.io/badge/nightly.link-Latest%20CI%20Build-800080)](https://nightly.link/Codename-riho/CryoSU/workflows/build-manager/main)
[![Kernel License: GPL v2](https://img.shields.io/badge/License-GPL%20v2-orange.svg?logo=gnu)](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)
[![Other part License：GPL v3](https://img.shields.io/github/license/Codename-riho/CryoSU?logo=gnu)](../LICENSE)

## Features

1. Kernel-based `su` and root access management
2. Module system based on [metamodules](https://kernelsu.org/guide/metamodule.html): Pluggable infrastructure for systemless modifications.
3. [App Profile](https://kernelsu.org/guide/app-profile.html): Lock up the root power in a cage
4. Support non-GKI and GKI 1.0
5. Tweaks to the manager theme and the built-in susfs management tool.
6. Multi manager support, for default [Official KernelSU](https://github.com/tiann/KernelSU)/[KOWSU](https://github.com/KOWX712/KernelSU) is supported work as manager with CryoSU's kernel

## Compatibility Status

- CryoSU officially supports Android GKI 2.0 devices (kernel 5.10+).

- Older kernels (3.4+) are also compatible, but the kernel will have to be built manually.

- Currently, only `arm64-v8a`, `armeabi-v7a` and `X86_64`are supported.

- [SUSFS](https://gitlab.com/simonpunk/susfs4ksu) in this project is **ONLY** support backport to kernel 4.3+

- `Tracepoint Syscall Redirect hook` is only support with GKI2(5.10+) kernel

## Hook Mode
- `Tracepoint Syscall Redirect hook` The default hook mode, from [upstream](https://github.com/tiann/KernelSU), but its only support GKI2 kernel with `arm64-v8a` or `x86_64` ABI
- `Manual Hook` The most compatible Hook, support from Linux kernel 3.4 to Linux kernel 6.18
- `SUSFS Inline Hook` An hook from [SuSFS](https://github.com/simonpunk/susfs4ksu), like `Manual Hook`, but provide from `SUSFS` project, not this project

## Integration

See the [BakaSU documentation](https://bakasu.org) (upstream; most of it also applies to CryoSU).

## Translation

If you want to submit a translation for the manager, please open a pull request (see Localization below).

## Credits

- [BakaSU](https://github.com/Baka-SU/BakaSU) (GPL-3.0): the project CryoSU is forked from; its authors keep their copyright.
- [tiann/KernelSU](https://github.com/tiann/KernelSU): the original KernelSU.

## Sponsor

- [weishu](https://github.com/sponsors/tiann) (author of KernelSU)

## License

- The file in the “kernel” directory is under [GPL-2.0-only](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html) license.
- The CryoSU artwork ([`docs/CryoSU.svg`](./CryoSU.svg) and [`manager/app/src/main/res/drawable/ic_launcher_foreground.xml`](../manager/app/src/main/res/drawable/ic_launcher_foreground.xml)) is original to CryoSU and is under the same license as the rest of the project.
- Except for the `kernel` directory, all other parts are under [GPL-3.0 or later](https://www.gnu.org/licenses/gpl-3.0.html) license.

## Localization

Translations live in `manager/app/src/main/res/values-*/strings.xml`. Pull requests that add or improve a translation are welcome.
