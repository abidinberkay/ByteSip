# ByteSip

A fun developer micro-break reminder plugin for IntelliJ IDEA.

ByteSip nudges you with non-blocking notification balloons so your body keeps up
with your IDE:

| Break type | Cadence       | Vibe                                  |
|------------|---------------|---------------------------------------|
| Water      | every 25 min  | "Code can wait. Kidneys cannot."      |
| Eyes       | every 50 min  | "Pixels are not nutrients."           |
| Move       | every 90 min  | "Your chair is getting emotionally attached." |

Every notification has two actions:

- **Done** — dismiss the balloon, regular cadence continues.
- **Snooze 1 min** — bump the same reminder by 1 minutes.

## Tech stack

- Kotlin (JVM toolchain 21)
- Gradle Kotlin DSL
- [IntelliJ Platform Gradle Plugin 2.x](https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html)
- Target platform: IntelliJ IDEA 2024.2+ (`sinceBuild = 242`)

## Project structure

```
bytesip/
├── build.gradle.kts
├── settings.gradle.kts
├── gradle.properties
└── src/main/
    ├── kotlin/com/bytesip/
    │   ├── ByteSipStartupActivity.kt     # boots scheduler on project open
    │   ├── BreakScheduler.kt             # @Service app-level scheduler
    │   ├── BreakType.kt                  # WATER / EYES / MOVE enum
    │   ├── BreakMessages.kt              # random message pools
    │   └── ByteSipNotificationService.kt # builds the balloon notifications
    └── resources/META-INF/plugin.xml
```

## Run from IntelliJ IDEA

1. Open the `bytesip` folder in IntelliJ IDEA — it will be detected as a Gradle
   project. Let Gradle sync.
2. Make sure a JDK 21 is configured (`File → Project Structure → SDKs`).
3. In the Gradle tool window, open **bytesip → Tasks → intellij platform**.
4. Double-click **runIde**. A sandbox IntelliJ instance launches with ByteSip
   installed.
5. Open any project inside the sandbox IDE. ByteSip starts automatically and
   the first water reminder appears after 25 minutes.

> Tip: to verify behavior faster, temporarily shorten the intervals in
> `BreakType.kt` (e.g. `Duration.ofSeconds(15)`), then rerun `runIde`.

## Run from the command line

From the project root:

```powershell
./gradlew runIde
```

To build a distributable plugin ZIP:

```powershell
./gradlew buildPlugin
```

The artifact is produced at `build/distributions/ByteSip-<version>.zip` and can
be installed via **Settings → Plugins → ⚙ → Install Plugin from Disk…**.

## Design notes

- Uses the IntelliJ **balloon notification system** — no modal popups.
- A single application-level `@Service` (`BreakScheduler`) owns all timers, so
  opening multiple projects does not stack duplicate reminders.
- Notifications dispatch onto the EDT before showing.
- "Snooze 1 min" reschedules the affected break once; regular cadence resumes
  automatically after the snoozed fire.
