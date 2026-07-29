---
name: architecture-base-monolith
description: Base architecture scaffold for a single-module Android project — core/domain/data/presentation layer packages inside `:app` and their import rules, Hilt wiring, base MVVM and use-case classes, the interface/implementation pattern for sources and providers. Apply right after creating a new project to lay out this structure; in a project already built on this template — before adding a screen/ViewModel/UseCase/Source/Provider, a Hilt binding or a Gradle dependency, to follow the same patterns.
---

# Base architecture (single-module scaffold)

A Clean Architecture template to lay out right after creating a new Android project: four layer
packages inside the single `:app` module. Layer boundaries are import rules, not module boundaries:
the compiler doesn't enforce them, the check in section "Layers" does. DI is Hilt.

Files named `Xxx*` (`XxxSource.kt`, `XxxSourceImpl.kt`, `XxxProvider.kt`) aren't ready-made code,
but a naming template: `Xxx` is replaced with the feature name. Files without the `Xxx` prefix are
ready-made: they sit in `root_dir` next to this file and are created once when laying out the
template.

This file is a reference scheme for laying out new projects. It is not kept in sync with any
project built from it and must not be updated to match one. Once real code appears in a project,
that code is the source of truth: if its structure has diverged from the template, follow the code
and leave this file as it is.

## Layers

| Package | Imports from the app | Contents |
|---|---|---|
| `core` | — | pure Kotlin code shared by all layers: provider interfaces, utilities, constants |
| `domain` | `core` | entities, source interfaces, use cases |
| `data` | `domain`, `core` | implementations of `domain` and `core` contracts, access to external sources |
| `presentation` | `domain`, `core` | UI layer: MVVM, Compose |
| root package | all | `App` only |

- `core` and `domain` don't import `android.*` or `androidx.*`. Code that needs the Android SDK goes
  to `data` or `presentation`.
- `presentation` and `data` don't import each other. They meet only through `domain` and `core`
  interfaces, wired by Hilt.
- `domain` and `presentation` never import an `*Impl` class.
- A feature is a package inside the layer packages, not a new Gradle module.
- After a change, run the check from the project root, with `app_package` and `<app_package>`
  replaced. Every printed line is a violation:
  ```bash
  src=app/src/main/java/app_package
  grep -rnE '^import (android|androidx)\.' $src/core $src/domain
  grep -rnE '^import <app_package>\.(domain|data|presentation)\.' $src/core
  grep -rnE '^import <app_package>\.(data|presentation)\.' $src/domain
  grep -rnE '^import <app_package>\.data\.' $src/presentation
  grep -rnE '^import <app_package>\.presentation\.' $src/data
  grep -rnE '^import .*Impl$' $src/domain $src/presentation
  ```

## Gradle rules

- Versions are declared only in `gradle/libs.versions.toml`. `app/build.gradle.kts` reads them
  from `libs`.
- A new library is added to the catalog first, then to `app/build.gradle.kts`.
- Hilt's annotation processor runs through KSP: `ksp(...)`, never `kapt`.
- `:app` applies no Kotlin Android plugin: AGP 9 compiles Kotlin itself.

## Placeholders in `root_dir`

`root_dir` mirrors the project root.

| Placeholder | Where | Replaced with |
|---|---|---|
| directory `app_package` | paths under `src/main/java/`, the layer check | the app package as a path: `com/example/app` |
| `<app_package>` | the layer check | the app package: `com.example.app` |
| `<package>` | first line of every `.kt` file | the `package` declaration: the app package plus the directories after `app_package/`. `app_package/App.kt` → `package com.example.app`, `app_package/core/di/ProviderModule.kt` → `package com.example.app.core.di` |
| `<hilt_version>`, `<androidx_hilt_version>`, `<ksp_version>` | `gradle/libs.versions.toml` | the latest stable release |

The `import` lines of each `.kt` file are sorted lexicographically, `java`, `javax` and `kotlin`
imports last. An import added during layout goes to its sorted place.

## Laying out a new project

Starting point: a project created by the Android Studio wizard (Empty Activity, Compose, Kotlin
DSL, version catalog, AGP 9) with a single `:app` module. `./gradlew :app:assembleDebug` passes on
it before step 1: a failure there is the wizard's, not the template's, and is fixed first.

1. Merge `root_dir/gradle/libs.versions.toml` into `gradle/libs.versions.toml`, replacing the
   placeholders. The merge rule is in the comment at the top of that file.
2. In the root `build.gradle.kts` add to `plugins`:
   ```kotlin
   alias(libs.plugins.ksp) apply false
   alias(libs.plugins.hilt) apply false
   ```
3. In `app/build.gradle.kts` add to `plugins`:
   ```kotlin
   alias(libs.plugins.ksp)
   alias(libs.plugins.hilt)
   ```
   and to `dependencies`:
   ```kotlin
   implementation(libs.hilt.android)
   ksp(libs.hilt.compiler)
   implementation(libs.androidx.hilt.lifecycle.viewmodel.compose)
   ```
4. Copy `root_dir/app/src/main/java/app_package` into `app/src/main/java/`, replacing the
   placeholders.
5. Move the wizard's code inside `app/src/main/java/app_package/`:
   - `ui/theme/*.kt` → `presentation/theme/`, replacing the empty `Color.kt`, `Theme.kt`,
     `Type.kt`. Their `package` becomes `<app_package>.presentation.theme`.
   - From the wizard's `MainActivity.kt` two things move into `presentation/MainActivity.kt`: the
     `enableEdgeToEdge()` call, before `setContent`, and the theme wrapper `XxxTheme { }`, placed
     inside `setContent` with an empty body. The wizard's `MainActivity.kt` is then deleted
     together with its sample composables `Greeting` and `GreetingPreview`, and the empty `ui/`
     directory with it.
6. In `app/src/main/AndroidManifest.xml` set `android:name=".App"` on `<application>` and
   `android:name=".presentation.MainActivity"` on the activity.
7. Run `./gradlew :app:assembleDebug`.

## Project structure

```
<project root>
├── build.gradle.kts                             plugins declared with `apply false`
├── gradle/libs.versions.toml                    versions
│
└── app/                                         the single Gradle module
    ├── build.gradle.kts                         KSP and Hilt plugins, Hilt dependencies
    └── src/main/
        ├── AndroidManifest.xml                  <application android:name=".App">, activity declarations
        └── java/app_package/
            ├── App.kt                           @HiltAndroidApp
            │
            ├── core/                            pure Kotlin, visible to all layers
            │   ├── di/         ProviderModule.kt    binds XxxProvider → XxxProviderImpl
            │   ├── provider/   XxxProvider.kt       interface
            │   │               XxxProviderImpl.kt   implementation that needs no Android SDK
            │   ├── utils/                       pure Kotlin utilities
            │   └── Constant.kt
            │
            ├── domain/                          pure Kotlin
            │   ├── base/usecase/                BaseNoParamUseCase, BaseParamUseCase, BaseNoParamFlowUseCase, BaseParamFlowUseCase
            │   ├── entity/                      domain models
            │   ├── source/     XxxSource.kt     source interfaces — data contract for data
            │   ├── usecase/    XxxUseCase.kt    inherits from one of base/usecase (optionally grouped into <feature>/ subfolders)
            │   └── Constant.kt
            │
            ├── data/
            │   ├── di/         SourceModule.kt      binds XxxSource → XxxSourceImpl
            │   │               ProviderModule.kt    binds XxxProvider → XxxProviderImpl; created with the first data/provider implementation
            │   ├── local/source/   XxxSourceImpl.kt implements domain.source.XxxSource over local data
            │   ├── remote/api/                  Retrofit/network contract
            │   ├── remote/source/  XxxSourceImpl.kt implements domain.source.XxxSource over network
            │   ├── provider/   XxxProviderImpl.kt   implements core.provider.XxxProvider with the Android SDK
            │   ├── utils/                       Android utilities of the data layer
            │   └── Constant.kt
            │
            └── presentation/
                ├── base/mvvm/                   BaseViewModel, BaseState, BaseInternalState, BaseEvent, BaseLabel
                ├── compose/                     compose components
                │   ├── component
                │   └── modifier
                ├── entity/                      UI models
                ├── navigation/
                │   └── Router.kt                navigation — a separate mechanism, not via Label/labelFlow
                ├── screen/<feature>/[mapper/]   specific screens: ViewModel + InternalState/State/Event/Label + mappers
                ├── theme/
                ├── utils/      OsUtils.kt       Android utilities of the UI layer
                ├── Constant.kt
                └── MainActivity.kt              @AndroidEntryPoint
```

Paths in the sections below that start with a layer name (`domain/usecase/`,
`presentation/screen/<feature>/`) are package paths under `app_package`, not directories of the
project root.

## Hilt

- `App` is `@HiltAndroidApp`. An activity, fragment, view, service or broadcast receiver that gets
  objects from the graph is `@AndroidEntryPoint`. The activity hosting an `@AndroidEntryPoint`
  fragment is `@AndroidEntryPoint` too. A content provider gets objects through an `@EntryPoint`
  interface and `EntryPointAccessors.fromApplication`.
- A Hilt module is `@Module @InstallIn(SingletonComponent::class)`: an `interface` for `@Binds`, an
  `object` for `@Provides`. It sits in `di/` of the layer whose types it binds. A new Hilt module
  is a new file: Hilt collects modules itself, nothing lists them.
- Own classes enter the graph through `@Inject constructor`. `@Binds` maps an interface to its
  implementation. `@Provides` is only for types whose constructor the project doesn't own.
- Modules are installed only in `SingletonComponent`, so `@Singleton` is the only scope. It goes on
  the class with the `@Inject` constructor or on the `@Provides` function. A ViewModel has no scope
  annotation: the `ViewModelStore` owns its lifetime.
- The application `Context` is injected in `data` and `presentation` as
  `@param:ApplicationContext private val context: Context`. The `@param:` target keeps the
  qualifier on the constructor parameter, where Hilt reads it: without it Kotlin 2.2 warns.

## Constants per layer

One `Constant.kt` per layer at the root of its package (`core`, `domain`, `data`, `presentation`).
It holds constants that belong to the layer as a whole, not to one class. No separate constants
file per feature.

## Source: interface (domain) → implementation (data)

`domain/source/XxxSource` declares the contract. `data/.../source/XxxSourceImpl` is a class with
`@Inject constructor` that implements it. `data/di/SourceModule` binds them:
`@Binds fun bindXxxSource(impl: XxxSourceImpl): XxxSource`. `domain` and `presentation` work only
with the interface.

## Provider: interface (core) → implementation (core or data)

A provider is an injectable cross-cutting service that is not a data source and is used by more
than one layer. `core/provider/XxxProvider` declares the interface.

| Implementation needs | `XxxProviderImpl` location | Bound in |
|---|---|---|
| only Kotlin/JVM | `core/provider/` | `core/di/ProviderModule` |
| the Android SDK | `data/provider/` | `data/di/ProviderModule` |

`XxxProviderImpl` is a class with `@Inject constructor`, bound with
`@Binds fun bindXxxProvider(impl: XxxProviderImpl): XxxProvider`.

## Domain: base use case

| Class | Signature | When to use |
|---|---|---|
| `BaseNoParamUseCase<Unit>` | `suspend operator fun invoke(): Unit` | A one-off action with no parameters and no return value |
| `BaseNoParamUseCase<Result>` | `suspend operator fun invoke(): Result` | A one-off action with no input |
| `BaseParamUseCase<Param, Result>` | `suspend operator fun invoke(param: Param): Result` | A one-off action with input |
| `BaseNoParamFlowUseCase<Result>` | `operator fun invoke(): Flow<Result>` | Subscribing to data with no parameters |
| `BaseParamFlowUseCase<Param, Result>` | `operator fun invoke(param: Param): Flow<Result>` | Subscribing to data with a parameter |

Each class overrides only `protected abstract fun execute(...)`: `suspend` in the one-off use cases,
not `suspend` in the Flow ones (a `Flow` is cold, the work runs on `collect`). Calling code invokes
the use case as a function via `operator fun invoke`. A use case is a class with
`@Inject constructor`. Its dependencies are `XxxSource`, `XxxProvider`, other use cases.

## Presentation: BaseViewModel (MVVM)

`BaseViewModel<State : BaseState, InternalState : BaseInternalState, Event : BaseEvent, Label : BaseLabel>` provides:

- `stateFlow: StateFlow<State>` / `state` — public UI state, read-only from outside.
- `internalStateFlow` / `internalState` — the private part of the state, accessible only to subclasses.
- `labelFlow: SharedFlow<Label>` — one-shot screen-level UI effects: messages (snackbar, toast) and
  one-off actions (scroll to top, hide the keyboard), emitted via `publishLabel(...)`. A label is
  not stored: it fires once and is not replayed after a configuration change. Anything that must
  survive a configuration change belongs in `State`. **Not for navigation.**
- `updateUiState { ... }` / `updateInternalState { ... }` — immutable state update via a `copy` lambda on the receiver.
- `launch { ... }` — a wrapper over `viewModelScope.launch`.
- `open fun onEvent(event: Event)` — the single entry point for events from the UI, overridden in the subclass.

A new screen:

1. In `presentation/screen/<feature>/` create `XxxState : BaseState`,
   `XxxInternalState : BaseInternalState`, `XxxEvent : BaseEvent`, `XxxLabel : BaseLabel` and
   `@HiltViewModel class XxxViewModel @Inject constructor(...) : BaseViewModel<XxxState, XxxInternalState, XxxEvent, XxxLabel>`.
2. Get it in the screen's composable with `hiltViewModel<XxxViewModel>()` from
   `androidx.hilt.lifecycle.viewmodel.compose`. The ViewModel is scoped to the nearest
   `ViewModelStoreOwner`: the navigation back stack entry, otherwise the activity.

A ViewModel without `@HiltViewModel` fails at runtime, when its screen opens.

A ViewModel that keeps state across process death or reads its screen's arguments declares
`SavedStateHandle` as a constructor parameter: Hilt provides it to every `@HiltViewModel`. Values
written to the handle are restored after process death. The owner's arguments are readable from it
by key: the activity's intent extras, the navigation back stack entry's arguments.

## Placement rule

In `presentation`, concrete screen code sits next to its feature: everything a screen needs
(ViewModel, InternalState/State/Event/Label, mappers) goes together in `presentation/screen/<feature>/`.

In `domain`, use cases sit flat in `domain/usecase/` by default. When one feature has many use
cases, they can optionally be grouped in a subfolder named after that feature:
`domain/usecase/<feature>/XxxUseCase.kt`.

Android utilities sit in `utils/` of the layer that uses them: `presentation/utils/`,
`data/utils/`. A capability that `domain` or both `data` and `presentation` need is a provider.
