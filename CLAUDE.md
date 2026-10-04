# Top 10 Deals

Shows the ten best offers of the week at a grocery store. The app picks a store, then reads that
store's top list from Firestore by its id; it does no ranking itself. The lists are picked and
published from this same app's debug-only screens. Built with Compose
Multiplatform, Android target only for now. Single Gradle module `:app`, package
`com.top10.deals` (the applicationId is still `com.top10.products`, the id Firebase knows the app by).

## Architecture

Same rules as the other apps here: SOLID, MVVM, Clean Architecture.

```
UI (Compose)  →  ViewModel  →  Use case  →  Repository interface (domain)  ←  data implementation
```

Dependencies point inward only: `ui` → `domain` ← `data`. `domain` is plain Kotlin. Shared code
goes in `app/src/commonMain` and must not use `java.*` or `android.*`; `androidMain` holds only
`MainActivity`, the application class, the Room database builder (it needs a `Context`), the
AdMob code in `ads/`, the manifest and launcher resources. Dependencies are
wired by hand in `di/AppContainer.kt` and nowhere else.

## Screens

- `ui/stores/`: the entry point, a list of stores from `GetStoresUseCase`.
- `ui/top/`: the top list of one store, opened with its `storeId` and loaded through
  `GetTopOffersUseCase(storeId)`. Nothing in the UI or domain is specific to a store.
- `ui/debug/`: debug builds only. A store picker (the same `StoreList` composable as the store
  list, under "Pick a store to submit 10 products for") leads to `PickTopScreen`: that store's 20
  candidate deals from `GetWeeklyDealsUseCase(storeId)` (deals already ended are dropped and the
  next ones take their place; under 10 left, the screen says so instead), a multi-select capped at exactly
  `TOP_LIST_SIZE` (10) that keeps the order of the taps (the first one picked is number 1, shown as
  the rank badge on each picked deal's picture; unpicking one moves the later ones up), and a
  "Submit list" button enabled only at 10 that publishes the picks in that order through
  `SubmitTopListUseCase`. Below the store picker, "Delete ended lists" (after a confirm
  dialog) runs `DeleteEndedTopListsUseCase`: it reads every document in `topLists` through
  `PublishedTopLists` and deletes, one by one, each list that has ended (see
  `domain/model/TopListEnd.kt`), then names the stores it deleted.
  Reached from a "Debug" button in the store list header. `MainActivity` passes
  `BuildConfig.DEBUG` to `App` as `isDebugBuild`; when it is false the button is not shown and
  none of these screens can be reached. Even in a debug build they open only after
  `UnlockScreen` gets the passphrase, asked again each time the debug screens are opened.
- ViewModels are kept for the whole session (keyed by store id), so a screen whose data can
  change in between asks again each time it opens: `TopOffersRoute` calls
  `TopOffersViewModel.onOpened()` from a `LaunchedEffect` instead of loading once in `init`.
- Colours live in `ui/theme/Theme.kt`: `Top10Theme`, a light and a dark Material 3 scheme (green,
  orange accent) following the phone's setting, with every role set in both so nothing falls back
  to Material's purple baseline. Screens use `MaterialTheme.colorScheme` roles, never fixed
  colours, except for what must look the same in both (white logo tiles, medal badges, the
  translucent category tints in `DealPicture`). The pre-Compose window background in
  `androidMain/res/values{,-night}/colors.xml` matches each scheme's `background`. Before that,
  the launch screen (`androidx.core:core-splashscreen`, `Theme.Top10Deals.Starting` on
  `MainActivity`) shows the launcher icon's badge on `splash_background`, green in both themes.
- Ads (AdMob, Android only): an anchored adaptive banner under the store list and another under a
  store's top 10, each its own ad unit, and nowhere else. `ads/Banners.kt` (`StoresBanner`,
  `DealsBanner`) and `ads/AdsConsent` live in `androidMain`; `MainActivity` hands them to `App` as
  `storesBanner` and `dealsBanner` slots and an `onOpenPrivacyOptions` callback, so common code
  knows nothing of AdMob. Belgium is in the EEA, so `AdsConsent` runs Google's UMP consent form on every
  start and starts the Mobile Ads SDK only once `canRequestAds()`; until then no banner is drawn.
  When UMP says privacy options are required, a "Privacy" button in the store list header reopens
  the form. The AdMob app id is in the manifest; the banners' unit ids are in `Banners.kt`,
  and debug builds use Google's test unit instead so our own taps never count.
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
- There are no photos of the actual products, only generic ones. A `WeeklyDeal` can have a
  `GenericProduct` (bananas, toilet paper, about 115 everyday products), and `ui/deal/DealPicture`
  shows its photo (`composeResources/drawable/product_*.webp`, mapped in `ui/deal/ProductPhotos.kt`)
  next to the deal on the pick screen and with a rank badge (gold, silver, bronze for the top 3) in
  the top list. A deal with no product shows its `ProductCategory` instead, as an emoji on a tinted
  tile; the emoji and tint live only in the UI. Every `GenericProduct` has a photo, and every photo
  is CC0 or public domain, so the app needs no credits screen; `PRODUCT_PHOTOS.md` lists where each
  came from. A new one is a 256 px square webp under the same rules.
- `StoreRepository` is implemented by `data/hardcoded/HardcodedCatalog`: six stores (Delhaize,
  Aldi, Lidl, Intermarché, Spar, Carrefour). Moving the stores to Firebase means a Firebase implementation
  swapped in `AppContainer`; nothing above the data layer changes.
- `WeeklyDealsRepository` gives a store's deals of the week, most relevant first; the debug flow
  offers the first `CANDIDATE_COUNT` (20). `data/hardcoded/HardcodedWeeklyDeals` is typed in by
  hand for week 40 of 2026: Delhaize's from its own folder (the PDF behind folder-fr.delhaize.be),
  ALDI's (week 41) from its own offers page (aldi.be/aanbiedingen and /offres, whose
  `validUntilLocalDate` is the day after the last one), the other stores' from Belgian folder sites, unchecked and padded with made-up staples where
  the sites showed fewer than 20. Scraping would replace it.
- Top lists are in Firestore. `data/firebase/FirestoreTopLists` is both the `TopOffersRepository`
  the app reads and the `TopListPublisher` and `PublishedTopLists` the debug screen writes and
  cleans up: `topLists/{storeId}`, one
  document per store replaced on each submit, with `storeId`, `storeName`, a server
  `submittedAt`, and `offers`, the 10 picks in the order they were picked (`rank` 1 to 10) with
  only the fields the pick screen shows; `name`, `packageSize` and `label` are `{en, fr, nl}`
  maps, `category` is a `ProductCategory` name (missing or unknown reads as `OTHER`, so never
  rename an entry), and `product` a `GenericProduct` name (missing or unknown shows the category
  picture, so never rename one either). A store with no document shows the empty state.
- Top lists are cached on the phone in Room (multiplatform, set up like the other apps):
  `data/local/CachedTopLists` wraps `FirestoreTopLists` as both interfaces and keeps each store's
  offers in `top-lists.db` (`top_offers`, plus `top_lists` with when each was fetched and when it
  ends). A cached list is used until the first of its deals has ended: its `validUntil` ("07/10",
  read by `domain/model/DueDate.kt` in Brussels time, through that day), and Firestore is not read
  again before then. A list in Firestore that has already ended that way shows the empty state and
  is not cached; the app never deletes it, the admin replaces or deletes it. The next open deletes it and
  reads Firestore again, so ended deals are never shown, even offline. A list with no readable
  `validUntil` is read again after `CachedTopLists.MAX_AGE` (12 hours). A store with no list
  published is not cached, so it is read from Firestore on every open until one is. When Firestore fails, a copy that has not ended is shown instead of an error, and a
  submit writes the new list straight into the cache. Other phones only see a resubmitted list
  once their copy has ended. The database is only a cache, so a schema change drops it (no migrations); its schema is
  still exported to `app/schemas/`, which is committed.
- `DebugLock` guards the debug screens. `data/firebase/FirebaseDebugLock` signs in to Firebase
  Auth as the publisher account (`FirebaseDebugLock.PUBLISHER_EMAIL`, made by hand in the Firebase
  console) with the passphrase typed on `UnlockScreen` as its password, and stays signed in. The
  password lives only in Firebase; never commit it. `firestore.rules` (pasted into the console by
  hand, with the publisher's UID in place of `PUBLISHER_UID`) lets anyone read `topLists` and only
  that account write or delete, so the lock also holds against someone using the Firebase config
  without the app. Change the passphrase by changing that account's password in the console.
- Firebase is set up: the `com.google.gms.google-services` plugin reads `app/google-services.json`
  (gitignored, so every checkout needs its own copy), and Firestore is used through GitLive's
  multiplatform SDK (`dev.gitlive:firebase-firestore`, and `firebase-auth` for the publisher) so the Firebase implementations live in
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
