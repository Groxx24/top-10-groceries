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
- `ui/debug/`: debug builds only. A store picker (the same `StoreList` composable as the store
  list, under "Pick a store to submit 10 products for") leads to `PickTopScreen`: that store's 20
  candidate deals from `GetWeeklyDealsUseCase(storeId)`, a multi-select capped at exactly
  `TOP_LIST_SIZE` (10), and a "Submit list" button enabled only at 10 that publishes the picks
  through `SubmitTopListUseCase`.
  Reached from a "Debug" button in the store list header. `MainActivity` passes
  `BuildConfig.DEBUG` to `App` as `isDebugBuild`; when it is false the button is not shown and
  none of these screens can be reached.
- Navigation is `rememberSaveable` state in `ui/App.kt`: the open store id, plus a debug flag
  and the store being picked for. Back goes one screen up.

## Data

- `Store` is an `id` and a display `name`. `Offer` is one already-ranked place in a list (`rank`
  1 to 10) with its `Deal`: the store's label, the share of the normal price saved, and how many
  items must be bought.
- `StoreRepository` and `TopOffersRepository` (`domain/repository/`) are the only way in. Today
  both are implemented by `data/hardcoded/HardcodedCatalog`, which holds five stores (Delhaize,
  Aldi, Lidl, Intermarché, Spar), each with ten made-up products. Moving to Firebase means writing Firebase implementations of those two interfaces and
  swapping them in `AppContainer`; nothing above the data layer changes.
- `WeeklyDealsRepository` gives a store's deals of the week, most relevant first; the debug flow
  offers the first `CANDIDATE_COUNT` (20). `data/hardcoded/HardcodedWeeklyDeals` is typed in by
  hand for week 40 of 2026: Delhaize's from its own folder (the PDF behind folder-fr.delhaize.be),
  the other stores' from Belgian folder sites, unchecked and padded with made-up staples where
  the sites showed fewer than 20. Scraping would replace it.
- `TopListPublisher` publishes a store's top list. `data/firebase/FirestoreTopListPublisher`
  writes it to `topLists/{storeId}`, one document per store replaced on each submit: `storeId`,
  `storeName`, a server `submittedAt`, and `offers`, the 10 picks in the order they were listed
  (`rank` 1 to 10) with only the fields the pick screen shows. It calls the `set` overload that
  takes a serializer: GitLive's inline overloads are built for JVM 17 and the app targets 11.
- Firebase is set up: the `com.google.gms.google-services` plugin reads `app/google-services.json`
  (gitignored, so every checkout needs its own copy), and Firestore is used through GitLive's
  multiplatform SDK (`dev.gitlive:firebase-firestore`) so the Firebase implementations live in
  `commonMain` like the rest of the data layer. The Firebase BoM in `androidMain` is pinned to the
  version that GitLive release is built against. Crashlytics (plugin plus the Android SDK in
  `androidMain`) reports crashes on its own; no code calls it.

## Build

Use Android Studio's bundled JDK; the system default is too new for this Gradle version:

```sh
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

Library versions in `gradle/libs.versions.toml` match the other apps here and are pinned for the
same reason: Android Gradle Plugin 8.13 and compile SDK 36.
