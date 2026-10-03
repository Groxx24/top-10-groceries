# Top 10 Products

Shows the ten best offers of the week at a grocery store. The app picks a store, then reads that
store's top list by its id; it does no ranking itself. The lists are made by a separate master app
and will be published to Firebase. Until then they are hardcoded. Built with Compose
Multiplatform, Android target only for now. Single Gradle module `:app`, package
`com.top10.products`.

## Architecture

Same rules as the other apps here: SOLID, MVVM, Clean Architecture.

```
UI (Compose)  →  ViewModel  →  Use case  →  Repository interface (domain)  ←  data implementation
```

Dependencies point inward only: `ui` → `domain` ← `data`. `domain` is plain Kotlin. Shared code
goes in `app/src/commonMain` and must not use `java.*` or `android.*`; `androidMain` holds only
`MainActivity`, the application class, the manifest and launcher resources. Dependencies are
wired by hand in `di/AppContainer.kt` and nowhere else.

## Screens

- `ui/stores/`: the entry point, a list of stores from `GetStoresUseCase`.
- `ui/top/`: the top list of one store, opened with its `storeId` and loaded through
  `GetTopOffersUseCase(storeId)`. Nothing in the UI or domain is specific to a store.
- Navigation is a single `rememberSaveable` store id in `ui/App.kt`; back goes to the store list.

## Data

- `Store` is an `id` and a display `name`. `Offer` is one already-ranked place in a list (`rank`
  1 to 10) with its `Deal`: the store's label, the share of the normal price saved, and how many
  items must be bought.
- `StoreRepository` and `TopOffersRepository` (`domain/repository/`) are the only way in. Today
  both are implemented by `data/hardcoded/HardcodedCatalog`, which holds Delhaize and ten made-up
  products. Moving to Firebase means writing Firebase implementations of those two interfaces and
  swapping them in `AppContainer`; nothing above the data layer changes.

## Build

Use Android Studio's bundled JDK; the system default is too new for this Gradle version:

```sh
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

Library versions in `gradle/libs.versions.toml` match the other apps here and are pinned for the
same reason: Android Gradle Plugin 8.13 and compile SDK 36.
