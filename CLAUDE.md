# BV 项目 CLAUDE.md

## 项目概述

BV 是哔哩哔哩（Bilibili）的第三方 Android 客户端，使用 Kotlin + Jetpack Compose 开发。

## 技术栈

- **语言**: Kotlin
- **UI**: Jetpack Compose
- **DI**: Koin (KSP 编译期注入)
- **网络**: Ktor Client + OkHttp 引擎
- **数据库**: Room
- **存储**: DataStore
- **播放器**: ExoPlayer + 自定义渲染器
- **API**: HTTP (BiliBili API) + gRPC
- **构建**: Gradle Kotlin DSL
- **最低版本**: Android 6.0 (API 23)
- **目标版本**: Android 15 (API 36)

## 模块结构

```
app/mobile/   — 手机端 UI (76 files)
app/tv/       — TV 端 UI (136 files)
app/shared/   — 公共层：ViewModel、DAO、Repository (115 files)
bili-api/     — B站 API 封装 HTTP + gRPC (191 files)
player/       — 播放器抽象 + 各端实现 (64 files)
utils/        — 工具类 (10 files)
bili-subtitle/ — 字幕解析
buildSrc/     — Gradle 构建配置
```

## 代码规范

- 遵循 Kotlin 官方编码规范
- API 方法必须有中文注释
- 网络请求使用 `runCatching` 异常处理模式
- ViewModel 中使用 `StateFlow` 暴露状态
- 不要使用 `runBlocking`，优先使用协程的 suspend/Flow

## 当前已知问题（需修复）

参考 `CODE_ANALYSIS_REPORT.md`：
1. **Prefs.kt** — 使用 `runBlocking` 阻塞线程，需改为异步
2. **BiliHttpApi.kt** — 1800 行超大类，需按业务拆分
3. **WebView** — `setWebContentsDebuggingEnabled` 应在 Debug 构建条件中
4. **GlobalScope** — 需替换为生命周期感知的 CoroutineScope

## Karpathy 准则

1. **Think Before Coding** — 先理解再动手，不确定就问
2. **Simplicity First** — 最少代码解决问题
3. **Surgical Changes** — 只碰需要的文件，不改无关代码
4. **Goal-Driven Execution** — 可验证的目标驱动

## 网络代理

本机 v2rayn SOCKS5 代理: `127.0.0.1:10808`
访问 GitHub 等海外资源时使用。

## 测试

- 运行测试: `./gradlew test`
- 构建: `./gradlew assembleDefaultRelease`
