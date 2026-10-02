# Top 10 Groceries

Shows the ten best offers of the week at a grocery store. Delhaize (Belgium) is the only store so
far; the app is laid out so that more can be added. Built with Compose Multiplatform, Android
target only for now. Single Gradle module `:app`, package `com.top10.groceries`.

## Architecture

Same rules as the other apps here: SOLID, MVVM, Clean Architecture.

```
UI (Compose)  →  ViewModel  →  Use case  →  Repository  →  StoreOfferSource (one per store)
```

Dependencies point inward only: `ui` → `domain` ← `data`. `domain` is plain Kotlin. Shared code
goes in `app/src/commonMain` and must not use `java.*` or `android.*`; `androidMain` holds only
`MainActivity`, the application class, the manifest and launcher resources. Dependencies are
wired by hand in `di/AppContainer.kt` and nowhere else.

## Offers

- `Offer` and `Deal` (`domain/model/`) are store-independent. Whatever a store calls its
  promotion, a `Deal` is two numbers: the share of the normal price saved, and how many items
  must be bought to get it. Everything downstream compares those.
- `ProductCategory` is the app's own list of aisles. Each store maps its category tree onto it.

## Ranking

All in `domain/ranking/`, and every number in `RankingPolicy.kt`. To tune the list, edit that
file and nothing else.

`score = 100 × discount factor × category weight × quantity factor × saving factor`

- Discount factor: a straight line from 0 at 15% to 1 at 50%, capped at 1.2. So 20% is worth
  0.14 and never makes the list on its own, whatever the category.
- Category weight: meat, fish, bread, fruit and vegetables 1.0, down to baby and pet at 0.15.
- Quantity factor: each extra item that must be bought takes a little off.
- Saving factor: between 0.8 and 1, full at €5 saved.

`TopOffersSelector` then keeps the list varied: one place per promotion (shown by its
best-scoring product, with a count of the others) and at most three places per category.

## Delhaize

- Source: the GraphQL endpoint behind delhaize.be/promotions, `POST /api/v1/` with the
  `ProductList` query and `productListingType: PROMOTION_SEARCH`. Public, no key, undocumented.
- It serves at most 50 products a page, about 1,300 products a week, and takes up to ten seconds
  a page however many are asked for at once. A full load takes about a minute. The result is
  kept in memory for the life of the process; nothing is stored on disk yet.
- `DelhaizePromotionParser` reads the amounts out of the shelf label using digits and symbols
  only, because the label comes in the language requested (`fr`, `nl`, `en`). Free-delivery
  promotions and ones running longer than six weeks are dropped.
- `DelhaizeCategoryMapper` maps the top-level category code, and tells apart a few mixed aisles
  (vegetarian under meat, juice under fruit, pastry under bakery) by the second-level slug in
  the product URL.

## Build

Use Android Studio's bundled JDK; the system default is too new for this Gradle version:

```sh
export JAVA_HOME="/Applications/Android Studio.app/Contents/jbr/Contents/Home"
./gradlew :app:assembleDebug
./gradlew :app:testDebugUnitTest
```

Library versions in `gradle/libs.versions.toml` match the other apps here and are pinned for the
same reason: Android Gradle Plugin 8.13 and compile SDK 36.
