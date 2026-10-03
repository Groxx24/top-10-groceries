# Top 10 Products

Shows the ten best offers of the week at a grocery store. The app picks a store, then reads that
store's top list from Firestore by its id; it does no ranking itself. The lists are picked and
published from this same app's debug-only screens. Built with Compose
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
  none of these screens can be reached. Even in a debug build they open only after
  `UnlockScreen` gets the passphrase, asked again each time the debug screens are opened.
- Navigation is `rememberSaveable` state in `ui/App.kt`: the open store id, plus a debug flag,
  whether the debug screens are unlocked, and the store being picked for. Back goes one screen
  up; leaving the debug screens locks them again.

## Languages

The app is for Belgium: English (`composeResources/values/`, the fallback), French
(`values-fr/`) and Dutch (`values-nl/`, written for Flanders), chosen from the phone's language.
Every string goes in all three files with the same keys. Prices follow each language's format
through `deal_price` and `decimal_separator` (`€3.09`, `3,09 €`, `€ 3,09`), so never build a price
string in code. The launcher name is `app_name` in `androidMain/res/values{,-fr,-nl}/`.
A deal's name, pack size and label are data in all three languages too: `LocalizedText` (`en`,
`fr`, `nl`), shown with `inLanguage(Locale.current.language)` so they follow the same fallback as
the strings. French and Dutch come from the stores' own folders where we have them (Delhaize
prints both); English is always translated. Submitting publishes all three.

## Data

- `Store` is an `id`, a display `name` and a logo. `WeeklyDeal` is a promotion as a store's folder
  prints it; `Offer` is one already-ranked place in a published list (`rank` 1 to 10) holding
  its `WeeklyDeal`, and `ui/deal/DealInfo` draws a deal the same way on both screens.
- `StoreRepository` is implemented by `data/hardcoded/HardcodedCatalog`: five stores (Delhaize,
  Aldi, Lidl, Intermarché, Spar). Moving the stores to Firebase means a Firebase implementation
  swapped in `AppContainer`; nothing above the data layer changes.
- `WeeklyDealsRepository` gives a store's deals of the week, most relevant first; the debug flow
  offers the first `CANDIDATE_COUNT` (20). `data/hardcoded/HardcodedWeeklyDeals` is typed in by
  hand for week 40 of 2026: Delhaize's from its own folder (the PDF behind folder-fr.delhaize.be),
  the other stores' from Belgian folder sites, unchecked and padded with made-up staples where
  the sites showed fewer than 20. Scraping would replace it.
- Top lists are in Firestore. `data/firebase/FirestoreTopLists` is both the `TopOffersRepository`
  the app reads and the `TopListPublisher` the debug screen writes: `topLists/{storeId}`, one
  document per store replaced on each submit, with `storeId`, `storeName`, a server
  `submittedAt`, and `offers`, the 10 picks in the order they were listed (`rank` 1 to 10) with
  only the fields the pick screen shows; `name`, `packageSize` and `label` are `{en, fr, nl}`
  maps. A store with no document shows the empty state.
- `DebugLock` guards the debug screens. `data/lock/HashedDebugLock` holds only a salt and a
  PBKDF2-SHA256 hash of the passphrase (`data/lock/DebugPassphrase.kt`, 100,000 iterations, via
  `org.kotlincrypto.macs:hmac-sha2` because common code has no `java.security`). Change the
  passphrase with `python3 scripts/set-debug-passphrase.py` (or `--generate`), never by editing
  that file, and never commit the passphrase itself. The lock is in the app only: Firestore's
  rules cannot see it, so it does not stop someone writing to Firestore without the app.
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
same reason: Android Gradle Plugin 8.13 and compile SDK 36. Unlike the other apps, this one
targets JVM 17, because GitLive's inline Firestore functions are built for 17.
