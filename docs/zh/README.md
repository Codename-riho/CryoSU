# CryoSU
<img align='right' src='../CryoSU.svg' width='220px' alt="CryoSU Icon">

[BakaSU](https://github.com/Baka-SU/BakaSU)（曾用名 ReSukiSU）的分支。

[English](../README.md) | **简体中文** 

一个 [`tiann/KernelSU`](https://github.com/tiann/KernelSU) 的下游分支, 添加了一些个人修改。

[![最新发行](https://img.shields.io/github/v/release/Codename-riho/CryoSU?label=Release&logo=github)](https://github.com/Codename-riho/CryoSU/releases/latest)
[![最新 CI 构建（nightly.link）](https://img.shields.io/badge/nightly.link-%E6%9C%80%E6%96%B0%20CI%20%E6%9E%84%E5%BB%BA-800080)](https://nightly.link/Codename-riho/CryoSU/workflows/build-manager/main)
[![Kernel License: GPL v2](https://img.shields.io/badge/License-GPL%20v2-orange.svg?logo=gnu)](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)
[![其他部分 License：GPL v3](https://img.shields.io/github/license/Codename-riho/CryoSU?logo=gnu)](../../LICENSE)

## 特性

1. 基于内核的 `su` 和权限管理。
2. 基于 [metamodules](https://kernelsu.org/zh_CN/guide/metamodule.html) 的模块系统：可插拔的模块架构。
3. [App Profile](https://kernelsu.org/zh_CN/guide/app-profile.html): 把 Root 权限关进笼子里。
4. 支持 non-GKI 与 GKI 1.0。
5. 可调整管理器外观，可自定义 susfs 配置。
6. 多管理器支持，默认支持使用 [官方KernelSU](https://github.com/tiann/KernelSU)/[KOWSU](https://github.com/KOWX712/KernelSU) 作为管理器与 CryoSU 内核共同工作

## 兼容状态

- CryoSU 官方支持 GKI 2.0 的设备（内核版本 5.10 以上）。

- 旧内核也是兼容的（3.4+），不过需要自己编译内核。

- 目前支持架构 : `arm64-v8a`、`armeabi-v7a`、`x86_64`。

- `Tracepoint Syscall Redirect Hook` 只支持在 GKI2 内核(5.10+) 工作

## Hook 模式
- `Tracepoint Syscall Redirect hook` 默认模式, 来自于 [上游](https://github.com/tiann/KernelSU), 但是只支持 GKI2 内核且为 `arm64-v8a` 或 `x86_64` 架构
- `Manual Hook` 兼容性最强的钩子，支持 Linux Kernel 3.4 - Linux Kernel 6.18
- `SUSFS Inline Hook` 一个来自 [SUSFS](https://github.com/simonpunk/susfs4ksu) 的 Hook, 类似于 `Manual Hook`, 但是由 `SUSFS` 项目，而非本项目

## 集成

请参考[文档](https://bakasu.org)

## 参与翻译

翻译位于 `manager/app/src/main/res/values-*/strings.xml`，欢迎通过 Pull Request 添加或完善翻译。

## 许可证

- 目录 `kernel` 下所有文件为 [GPL-2.0-only](https://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html)。
- CryoSU 的图标（[`docs/CryoSU.svg`](../CryoSU.svg) 与 [`manager/app/src/main/res/drawable/ic_launcher_foreground.xml`](../../manager/app/src/main/res/drawable/ic_launcher_foreground.xml)）为 CryoSU 原创，与项目其余部分使用相同许可证。
- 除目录 `kernel` 外，其他部分均为 [GPL-3.0-or-later](https://www.gnu.org/licenses/gpl-3.0.html)。

## 致谢

- [BakaSU](https://github.com/Baka-SU/BakaSU) (GPL-3.0)：CryoSU 所基于的项目，其作者保留各自版权。
- [weishu](https://github.com/sponsors/tiann) (KernelSU 作者)
