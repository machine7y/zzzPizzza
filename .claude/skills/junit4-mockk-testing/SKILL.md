---
name: junit4-mockk-testing
description: Local unit tests (`src/test`) for Kotlin/Android code with JUnit 4, MockK, kotlinx-coroutines-test, Truth and Turbine — dependencies, test class template, which tests to write (positive, negative, equivalence classes, boundary values), mocks vs. fakes, coroutine, ViewModel and Flow testing, and the traps that make tests hang or pass without checking anything. Load when writing, fixing, reviewing or running such tests or adding their dependencies. Not for JUnit 5, Kotest or Mockito projects, instrumented tests or Compose UI tests.
---

# JUnit 4 + MockK unit tests

## Stack

| Concern | Library | Coordinates (latest on 2026-11-18) |
|---|---|---|
| Test framework | JUnit 4 | `junit:junit:4.13.2` |
| Mocks | MockK | `io.mockk:mockk:1.14.12` |
| Coroutines | kotlinx-coroutines-test | `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.11.0` |
| Assertions | Truth | `com.google.truth:truth:1.4.5` |
| Flows | Turbine | `app.cash.turbine:turbine:1.2.1` |

- Add them as `testImplementation`, the way the build already declares dependencies (version catalog or coordinates).
- These versions need Kotlin 2.1 or newer. On an older Kotlin, take the newest versions that support it.
- `kotlinx-coroutines-test` gets the same version as `kotlinx-coroutines-core` in the module.
- Existing tests in the module win: follow their conventions. If they contradict this skill, say so and ask.
- No other test libraries unless the project already has them or the user asks.

## Test class

Start from `TestClassTemplate.kt` next to this skill. Its rules:

- File `<ClassUnderTest>Test.kt`, same package as the class under test, under `src/test`.
- JUnit 4 creates a new instance for every test, so mocks are `private val` fields: no `lateinit`, `@MockK`, `@Before`
  setup or `clearMocks`. `@get:Rule` properties stay public.
- A class whose construction starts coroutines (`init { launch }`, `stateIn` / `shareIn` on `viewModelScope`) is
  created inside the test through `create<ClassUnderTest>()`, after its stubs.
- Name: `` `<action> <expected result> when <condition>` ``. Names can't contain `. ; [ ] / < > : \`, so limits are
  written in words.
- Given / when / then blocks separated by blank lines. One behavior per test. No `if`, loops or `try/catch` in
  assertions; `repeat(n)` is fine in the given block.
- Test data: real instances from factory functions with a default for every property. Same-typed properties get
  different defaults, so a swapped or dropped field fails the comparison.

## Which tests to write

A test is written only if it catches a defect that no other test catches. Cases come from the contract first
(requirements, the user's task, what a dependency can return), then from the code.

| The code has | Technique | Tests |
|---|---|---|
| Any behavior | Positive | One for the main scenario |
| Branches: `if`, `when`, sealed / enum, `?:`, `?.let`, early `return` | Equivalence classes | One per branch, with a representative value |
| Error handling: `try/catch`, `require`, a branch for a failed, empty or null dependency result | Negative | One per handling: the result, and that forbidden side effects didn't happen |
| A limit, an emptiness check, indexing, a delay that the contract fixes | Boundary values | The last value of one class and the first value of the next |
| Only passes a dependency's result through | — | The positive test only |

- Boundary pairs: `x > n` and `x <= n` → `n`, `n + 1`; `x >= n` and `x < n` → `n - 1`, `n`; `isEmpty()` → sizes 0
  and 1; a delay `d` → one test that checks `d - 1` (not yet) and `d` (done).
- `a && b`: both true, then each operand false on its own. `a || b`: both false, then each operand true on its own.
- No second test for another value of the same class. No tests for values the type system excludes.

## Mocks and fakes

- Mock only direct dependencies at a boundary: repositories, data sources, API interfaces, DAOs, navigators. Use real
  data classes, mappers, pure functions, flows and `SavedStateHandle(mapOf(...))`.
- A dependency with state (save, then read back) gets a fake in `src/test` instead of a chain of stubs.
- Mocks are strict. Unit functions are stubbed with `justRun` / `coJustRun`. `relaxed = true` only for loggers and
  analytics that no test checks.
- Queries: stub with exact arguments and check the result. Commands: stub with `any()` and check the arguments with
  `coVerify`.
- `mockkStatic` / `mockkObject` only for code that can't be injected (`android.util.Log`, third-party singletons),
  with `@After fun tearDown() = unmockkAll()`. For other hard-to-test code (launches on a hardcoded dispatcher, direct
  singleton calls, `now()`, `UUID`, `Random`, Android calls in logic), propose injecting the dependency and ask.

## Coroutines and ViewModels

- A test that runs coroutines is `= runTest { }`, never `runBlocking`. The class gets
  `@OptIn(ExperimentalCoroutinesApi::class)`.
- `Dispatchers.Main`: copy `MainDispatcherRule.kt` from this skill's folder into `src/test` once and add a `package`
  line. It sets `UnconfinedTestDispatcher`, so `viewModelScope` coroutines start at once, and `runTest` shares its
  scheduler. If the order of coroutines matters in a test class, pass `MainDispatcherRule(StandardTestDispatcher())`
  and drive it with `runCurrent()` / `advanceUntilIdle()`.
- `delay` runs on virtual time. `advanceTimeBy(ms)` runs tasks scheduled before `now + ms`; a task at exactly
  `now + ms` also needs `runCurrent()`.
- Before asserting that something did not happen, run everything that path schedules: on a `StandardTestDispatcher`
  call `runCurrent()`, and advance past every delay. Otherwise the check passes on broken code.
- A class that switches to another dispatcher takes it as a constructor parameter with a default. Tests pass
  `mainDispatcherRule.testDispatcher`. A test dispatcher created in a field initializer gets its own scheduler, which
  `runTest` doesn't advance.
- **Endless work hangs the test**, even after a failed assertion: after the body, `runTest` runs every remaining task
  with no timeout. Make retry loops finite through stubs: `returnsMany listOf(error, success)` for a returned error,
  `throws exception andThen success` for a thrown one. Stub everything those iterations call. For code that loops
  forever by design, wrap the test body in
  `try { … } finally { viewModel.viewModelScope.cancel() }`. While such a loop is active, `advanceUntilIdle()` never
  returns either: drive these tests with `advanceTimeBy(ms)` + `runCurrent()`.

## Flows (Turbine)

- `StateFlow`: treat it as a value holder and assert `.value` after the action.
- A sequence of states, one-off events (`SharedFlow`, `Channel`) and flows built with
  `stateIn(…, WhileSubscribed(…) or Lazily, …)`: `flow.test { }`. Without a subscriber, the `.value` of such a
  `stateIn` flow never leaves its initial value. Turbine subscribes before the block runs, so the action goes inside
  the block.
- Every emitted event is consumed with `awaitItem()`; leftover events fail the test when the block ends. The first
  `awaitItem()` of a `StateFlow` is its current value.
- An intermediate state (loading) is asserted only when the code suspends between states: stub with
  `coAnswers { delay(n); result }`, check the loading state, then `advanceTimeBy(n)` + `runCurrent()`.
- `SharedFlow` events emitted from `init` are gone before `test { }` subscribes. Use
  `MainDispatcherRule(StandardTestDispatcher())` in that test class and call `runCurrent()` inside the block.

## Assertions (Truth)

- `assertThat(actual).isEqualTo(expected)` on whole objects; an expected state is built with `copy(...)` from a known
  one. Floating point: `isWithin(tolerance).of(expected)`.
- Exceptions compare by identity: stub a specific instance and check it with `isSameInstanceAs`. For an exception that
  the code creates itself, check its type and `hasMessageThat()`.
- Expected exception: `assertThrows(Type::class.java) { … }` for regular code, `runCatching { … }.exceptionOrNull()`
  for suspend code.
- Code that catches `Exception` also catches MockK's "no answer found" error, so its negative tests check the wrapped
  stubbed instance. If the result doesn't carry the exception, the test also checks with `coVerify(exactly = 1)` that
  the call stubbed to throw was made. Such code also gets a test that `CancellationException` is rethrown.

## Android

- `android.*` classes throw `Method ... not mocked` in local tests. Don't set `isReturnDefaultValues = true`; keep
  Android calls out of the tested logic.
- Assert `R` resource ids, not resolved strings.
- No DI framework in unit tests: construct the class with its mocks.

## Running and reporting

- Android module: `./gradlew :<module>:test<Variant>UnitTest`. JVM module: `./gradlew :<module>:test`. One class:
  `--tests "<package>.<ClassUnderTest>Test"`. Add `--rerun` when nothing changed since the last run. Run with a time
  limit.
- Report the real result: passed and failed counts and failure output.
- A test that exposes a bug stays failing: report it and ask. An expected value changes only when the user's task
  changed that behavior.
