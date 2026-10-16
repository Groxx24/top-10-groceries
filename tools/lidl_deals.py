#!/usr/bin/env python3
"""
Reads this week's Lidl Belgium offers from lidl.be (Dutch and French pages) and writes them,
most relevant first, to LidlWeeklyDeals.kt: the candidates the debug screen picks Lidl's top 10
from. Standard library only. Run it once Lidl's new week is online, then rebuild the debug app:

    python3 tools/lidl_deals.py            # writes the Kotlin file
    python3 tools/lidl_deals.py --dry-run  # only prints the ranking

No translation service is used, so a deal's English name is its French one; the deal labels
("1+1 gratis") are translated by pattern.
"""

import argparse
import datetime
import html
import json
import re
import sys
import urllib.request
from pathlib import Path
from zoneinfo import ZoneInfo

ROOT = Path(__file__).resolve().parent.parent
OUTPUT = ROOT / "app/src/commonMain/kotlin/com/top10/deals/data/hardcoded/LidlWeeklyDeals.kt"
# "Offers this week"; the slug before /a10082242 does not matter, the list id does.
PAGES = {
    "nl": "https://www.lidl.be/c/nl-BE/aanbiedingen-deze-week/a10082242",
    "fr": "https://www.lidl.be/c/fr-BE/actions-cette-semaine/a10082242",
}
USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 14_0) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/17.0 Safari/605.1.15"
BRUSSELS = ZoneInfo("Europe/Brussels")
# Written out, so ended deals can drop off and later ones still fill the 20 candidates.
MAX_DEALS = 40
MAX_PER_CATEGORY = 3

# --- Products the app has a photo of: (GenericProduct, its ProductCategory, words in Dutch or French).
# Checked in order against the lower-cased Dutch and French names, so put the specific ones first.
PRODUCTS = [
    ("ORANGE_JUICE", "SOFT_DRINKS", ["sinaasappelsap", "jus d'orange", "jus d’orange"]),
    ("ICED_TEA", "SOFT_DRINKS", ["ice tea", "iced tea", "ijsthee", "thé glacé"]),
    ("LEMONADE", "SOFT_DRINKS", ["limonade", "schweppes", "fanta", "frisdrank", "boisson rafraîchissante", "agrumes"]),
    ("SOFT_DRINK_CANS", "SOFT_DRINKS", ["energy drink", "energiedrank", "boisson énergisante", "coca-cola"]),
    ("WATER", "WATER", ["bronwater", "mineraalwater", "spuitwater", "eau de source", "eau minérale", "eau pétillante"]),
    ("COFFEE_CAPSULES", "COFFEE_TEA", ["capsules", "pads", "dosettes"]),
    ("COFFEE_BEANS", "COFFEE_TEA", ["koffiebonen", "café en grains"]),
    ("INSTANT_COFFEE", "COFFEE_TEA", ["oploskoffie", "café soluble"]),
    ("GROUND_COFFEE", "COFFEE_TEA", ["gemalen koffie", "café moulu", "koffie", "café"]),
    ("TEA", "COFFEE_TEA", ["thee", "thé"]),
    ("SPARKLING_WINE", "WINE", ["cava", "prosecco", "champagne", "crémant", "schuimwijn", "mousseux"]),
    ("ROSE_WINE", "WINE", ["rosé"]),
    ("WHITE_WINE", "WINE", ["witte wijn", "vin blanc"]),
    ("RED_WINE", "WINE", ["rode wijn", "vin rouge"]),
    ("WHISKY", "SPIRITS", ["whisky", "whiskey"]),
    ("GIN", "SPIRITS", [" gin"]),
    ("BEER", "BEER", ["pils", "bier", "bière", "jupiler", "leffe", "stella", "duvel", "cider"]),
    ("CHICKEN_BREAST", "POULTRY", ["kippenborst", "kipfilet", "kippenfilet", "kippenhaas", "kippenreepjes", "kippenblokjes",
                                   "kippengyros", "filet de poulet", "aiguillettes de poulet", "émincé de poulet", "dés de poulet", "blanc de poulet"]),
    ("WHOLE_CHICKEN", "POULTRY", ["hele kip", "poulet entier"]),
    ("BURGERS", "MEAT", ["burger", "hamburger"]),
    ("MINCED_MEAT", "MEAT", ["gehakt", "haché"]),
    ("SAUSAGES", "MEAT", ["chipolata", "worst", "saucisse", "boudin", "pensen"]),
    ("PORK_CHOPS", "MEAT", ["varkenskotelet", "côte de porc", "côtelette"]),
    ("BEEF_STEAK", "MEAT", ["steak", "biefstuk", "entrecote", "entrecôte", "runderstoofvlees", "stoofvlees", "carbonnade"]),
    ("SALAMI", "MEAT", ["salami"]),
    ("HAM", "MEAT", ["hesp", "ham", "jambon", "ontbijtspek", "spek", "lard", "kalkoenfilet", "filet de dinde"]),
    ("SALMON", "FISH", ["zalm", "saumon"]),
    ("COD", "FISH", ["kabeljauw", "cabillaud"]),
    ("MUSSELS", "FISH", ["mosselen", "moules"]),
    ("SCALLOPS", "FISH", ["sint-jakobsnoten", "saint-jacques"]),
    ("FISH_FINGERS", "FROZEN", ["vissticks", "fish sticks", "bâtonnets de poisson", "surimi"]),
    ("EGGS", "EGGS", ["eieren", "œufs", "oeufs"]),
    ("SOFT_CHEESE", "CHEESE", ["brie", "camembert", "verse kaas", "fromage frais", "mozzarella"]),
    ("CHEESE", "CHEESE", ["kaas", "fromage", "gouda", "emmental", "pecorino", "parmigiano", "comté"]),
    ("CROISSANTS", "BAKERY", ["croissant"]),
    ("BAGUETTE", "BAKERY", ["baguette", "stokbrood"]),
    ("DONUTS", "BAKERY", ["donut"]),
    ("WAFFLES", "BAKERY", ["wafel", "gaufre"]),
    ("CAKE", "BAKERY", ["taart", "cake", "gâteau", "flap", "chausson"]),
    ("BREAD", "BAKERY", ["brood", "pain"]),
    ("CHOCOLATE", "SWEETS", ["chocolade", "chocolat", "nougatti", "kinder", "milka", "côte d'or", "côte d’or"]),
    ("BISCUITS", "SWEETS", ["koekjes", "biscuits", "cookies", "speculoos"]),
    ("CEREAL_BARS", "SWEETS", ["mueslireep", "barre de céréales", "graanreep"]),
    ("CANDY", "SWEETS", ["snoep", "bonbons", "gummy", "lolly", "sucettes", "drop"]),
    ("MILK", "DAIRY", ["melk", "lait"]),
    ("BUTTER", "DAIRY", ["boter", "beurre"]),
    ("YOGHURT", "DAIRY", ["yoghurt", "yaourt", "skyr"]),
    ("CEREAL", "OTHER", ["ontbijtgranen", "céréales", "muesli", "cornflakes"]),
    ("JAM", "OTHER", ["confituur", "confiture", "jam"]),
    ("HONEY", "OTHER", ["honing", "miel"]),
    ("LASAGNE", "PASTA", ["lasagne"]),
    ("SPAGHETTI", "PASTA", ["spaghetti"]),
    ("RICE", "PASTA", ["rijst", "riz "]),
    ("PASTA", "PASTA", ["pasta", "pâtes", "penne", "fusilli", "tagliatelle"]),
    ("PIZZA", "FROZEN", ["pizza"]),
    ("FRIES", "FROZEN", ["frieten", "frites", "potato shapes"]),
    ("CROQUETTES", "FROZEN", ["kroketten", "croquettes de pommes"]),
    ("SOUP", "CANNED", ["soep", "soupe", "potage"]),
    ("OLIVES", "CANNED", ["olijven", "olives"]),
    ("OLIVE_OIL", "CANNED", ["olijfolie", "huile d'olive", "huile d’olive"]),
    ("CANNED_FOOD", "CANNED", ["blik", "conserve", "tomatensaus", "sauce tomate"]),
    ("ICE_CREAM", "ICE_CREAM", ["ijs ", "roomijs", "glace"]),
    ("CRISPS", "SNACKS", ["chips"]),
    ("NUTS", "SNACKS", ["noten", "noix", "cashew", "pinda", "cacahuète", "amandel", "amande"]),
    ("POPCORN", "SNACKS", ["popcorn"]),
    ("APPLES", "FRUIT", ["appel", "pomme", "royal gala", "jonagold", "pink lady", "elstar"]),
    ("BANANAS", "FRUIT", ["banaan", "bananen", "banane"]),
    ("PEARS", "FRUIT", ["peer", "peren", "poire", "conference"]),
    ("MANDARINS", "FRUIT", ["mandarijn", "mandarine", "clementine", "clémentine"]),
    ("ORANGES", "FRUIT", ["sinaasappel", "appelsien", "orange"]),
    ("LEMONS", "FRUIT", ["citroen", "citron"]),
    ("WHITE_GRAPES", "FRUIT", ["witte druiven", "raisins blancs"]),
    ("BLACK_GRAPES", "FRUIT", ["druiven", "raisins"]),
    ("STRAWBERRIES", "FRUIT", ["aardbei", "fraise"]),
    ("RASPBERRIES", "FRUIT", ["framboos", "frambozen", "framboise"]),
    ("BLUEBERRIES", "FRUIT", ["blauwe bes", "bosbes", "myrtille"]),
    ("MANGO", "FRUIT", ["mango", "mangue"]),
    ("PINEAPPLE", "FRUIT", ["ananas"]),
    ("KIWI", "FRUIT", ["kiwi"]),
    ("AVOCADO", "FRUIT", ["avocado", "avocat"]),
    ("WATERMELON", "FRUIT", ["watermeloen", "pastèque"]),
    ("MELON", "FRUIT", ["meloen", "melon"]),
    ("CHERRIES", "FRUIT", ["kers", "cerise"]),
    ("PEACHES", "FRUIT", ["perzik", "pêche", "nectarine"]),
    ("PLUMS", "FRUIT", ["pruim", "prune"]),
    ("TOMATOES", "VEGETABLES", ["tomaat", "tomaten", "tomate"]),
    ("CUCUMBER", "VEGETABLES", ["komkommer", "concombre"]),
    ("CARROTS", "VEGETABLES", ["wortel", "carotte"]),
    ("POTATOES", "VEGETABLES", ["aardappel", "pommes de terre", "grenaille"]),
    ("ONIONS", "VEGETABLES", ["ui ", "uien", "oignon"]),
    ("BROCCOLI", "VEGETABLES", ["broccoli", "brocoli"]),
    ("CAULIFLOWER", "VEGETABLES", ["bloemkool", "chou-fleur"]),
    ("ENDIVES", "VEGETABLES", ["witloof", "chicon"]),
    ("BRUSSELS_SPROUTS", "VEGETABLES", ["spruitjes", "choux de bruxelles"]),
    ("SPINACH", "VEGETABLES", ["spinazie", "épinard"]),
    ("LETTUCE", "VEGETABLES", ["sla", "salade", "rucola", "roquette", "ijsberg", "iceberg"]),
    ("BELL_PEPPERS", "VEGETABLES", ["paprika", "poivron"]),
    ("COURGETTE", "VEGETABLES", ["courgette"]),
    ("MUSHROOMS", "VEGETABLES", ["champignon"]),
    ("LEEKS", "VEGETABLES", ["prei", "poireau"]),
    ("SQUASH", "VEGETABLES", ["pompoen", "butternut", "potiron", "courge"]),
    ("GARLIC", "VEGETABLES", ["look", "ail "]),
    ("GREEN_BEANS", "VEGETABLES", ["sperzieboon", "boontjes", "haricots"]),
    ("ASPARAGUS", "VEGETABLES", ["asperge"]),
    ("SWEETCORN", "VEGETABLES", ["maïs", "mais"]),
    ("LAUNDRY_DETERGENT", "LAUNDRY", ["wasmiddel", "lessive", "wasverzachter", "adoucissant", "wasparels"]),
    ("DISH_SOAP", "CLEANING", ["afwasmiddel", "liquide vaisselle", "vaatwas", "lave-vaisselle", "finish"]),
    ("SPONGES", "CLEANING", ["spons", "éponge"]),
    ("CLEANING", "CLEANING", ["reiniger", "reiniging", "nettoyant", "nettoie", "allesreiniger"]),
    ("TOILET_PAPER", "PAPER", ["toiletpapier", "papier toilette", "papier-toilette"]),
    ("TISSUES", "PAPER", ["zakdoek", "mouchoir", "keukenpapier", "essuie-tout"]),
    ("TOOTHPASTE", "PERSONAL_CARE", ["tandpasta", "dentifrice"]),
    ("SOAP", "PERSONAL_CARE", ["zeep", "savon", "douchegel", "gel douche"]),
    ("DOG_FOOD", "PET", ["hondenvoer", "hondenbrok", "chien"]),
    ("SUGAR", "OTHER", ["suiker", "sucre"]),
]
# Words of 4 letters or fewer only match at the start of a word, so "ham" does not find "champignon".
SHORT_WORD = 4
# Products whose photo would be wrong for anything else with the same word in its name.
NOT_A_PRODUCT = ["halloween", "vegan", "végétalien", "kat", "chat", "bakvulling", "litière"]

# --- Lidl's own category names (the last part of "wonCategoryPrimary", in Dutch) for deals that match no product.
LIDL_CATEGORIES = {
    "Groenten": "VEGETABLES", "Fruit": "FRUIT", "Verse kruiden": "VEGETABLES",
    "Vis & zeevruchten": "FISH", "Diepvriesvis & zeevruchten": "FISH",
    "Gevogelte": "POULTRY", "Varkensvlees": "MEAT", "rundvlees": "MEAT", "Rundvlees": "MEAT", "Lamsvlees": "MEAT",
    "Worst & vleeswaren": "MEAT", "Diepvriesvlees & gevogelte": "MEAT",
    "Eieren": "EGGS", "Boter": "DAIRY", "Melk": "DAIRY", "Desserts": "DAIRY", "Yoghurt": "DAIRY", "Kaas": "CHEESE",
    "Vers brood": "BAKERY", "Broodjes & klein gebak": "BAKERY", "Zoet gebak": "BAKERY",
    "Chocoladeproducten": "SWEETS", "Koekjes & gebak": "SWEETS", "Snoepjes & fruitgummies": "SWEETS",
    "Gekoelde kant-en-klaarmaaltijden": "PASTA", "Pasta": "PASTA", "Rijst": "PASTA",
    "Soepen & sauzen": "CANNED", "Conserven": "CANNED",
    "Diepvriespizza's & snacks": "FROZEN", "Diepvriesgroenten": "FROZEN", "IJs": "ICE_CREAM",
    "Hartige hapjes": "SNACKS", "Gedroogd fruit & noten": "SNACKS",
    "Frisdranken": "SOFT_DRINKS", "Vruchtensappen": "SOFT_DRINKS", "Water": "WATER",
    "Koffie, thee & cacao": "COFFEE_TEA", "Bier & Cider": "BEER", "Wijn": "WINE", "Sterke drank": "SPIRITS",
    "Wasmiddelen & wasverzorging": "LAUNDRY", "Huishoudelijke reiniging": "CLEANING", "schoonmaakmiddelen": "CLEANING",
    "Toiletpapier": "PAPER", "Lichaamsverzorging": "PERSONAL_CARE", "Mondverzorging": "PERSONAL_CARE",
    "Babyverzorging": "BABY", "Kattenvoer": "PET", "Hondenvoer": "PET", "Kattenbakvulling": "PET",
}
# Lidl's top-level "category" of the page, used when the detailed one is missing or wrong.
LIDL_PAGE_CATEGORIES = {"Groenten en fruit": None, "Vis": "FISH", "Vlees": "MEAT", "Bakkerij": "BAKERY"}

# Categories people shop for every week rank a little higher than treats and extras.
EVERYDAY = {"FRUIT", "VEGETABLES", "MEAT", "POULTRY", "FISH", "EGGS", "DAIRY", "CHEESE", "BAKERY",
            "PASTA", "COFFEE_TEA", "WATER"}


def fetch(url):
    request = urllib.request.Request(url, headers={"User-Agent": USER_AGENT, "Accept-Language": "nl-BE,fr-BE"})
    with urllib.request.urlopen(request, timeout=30) as response:
        return response.read().decode("utf-8")


def products_on(page):
    """The page's product tiles, by product id."""
    tiles = re.findall(r'data-grid-data="([^"]*)"', page)
    if not tiles:
        sys.exit("No products found: lidl.be may have changed its page layout.")
    products = (json.loads(html.unescape(tile)) for tile in tiles)
    return {product["productId"]: product for product in products}


def price_of(product):
    """(price, regular price or None, discount text or None, base price text or None, needs Lidl Plus)."""
    plain = product.get("price") or {}
    if plain.get("price"):
        price, card = plain, False
    else:
        plus = (product.get("lidlPlus") or [{}])[0].get("price") or {}
        if not plus.get("price"):
            return None
        price, card = plus, True
    discount = price.get("discount") or {}
    regular = price.get("oldPrice") or discount.get("deletedPrice") or None
    if regular is not None and regular <= price["price"]:
        regular = None
    return price["price"], regular, discount.get("discountText"), (price.get("basePrice") or {}).get("text"), card


def tidy_label(text):
    """"1 + 1 GRATIS" -> "1+1 gratis", "2E AAN -50%" -> "2e aan -50%"."""
    if not text:
        return None
    return re.sub(r"\s*\+\s*", "+", text.strip()).lower()


def english_label(nl):
    patterns = [
        (r"^(\d+\+\d+) gratis$", r"\1 free"),
        (r"^2e aan (-\d+%)$", r"2nd at \1"),
        (r"^(\d+) voor$", r"\1 for"),
        (r"^(-\d+%)$", r"\1"),
    ]
    for pattern, english in patterns:
        if re.match(pattern, nl):
            return re.sub(pattern, english, nl)
    return None


def label_discount(label):
    """Share saved that a label stands for, when it says so."""
    if not label:
        return None
    if m := re.match(r"^(\d+)\+(\d+) gratis$", label):
        paid, free = int(m[1]), int(m[2])
        return free / (paid + free)
    if m := re.match(r"^2e aan -(\d+)%$", label):
        return int(m[1]) / 200
    if m := re.match(r"^-(\d+)%$", label):
        return int(m[1]) / 100
    return None


def pack_size(text):
    """The pack size from Lidl's base price text, without its price per kilo or litre."""
    if not text or text.startswith("/"):
        return None
    parts = [part for part in text.split(" - ") if "EUR" not in part]
    return " - ".join(parts) or None


def contains(text, word):
    if len(word.strip()) > SHORT_WORD:
        return word in text
    return re.search(r"(?<![a-zà-ÿ])" + re.escape(word), text) is not None


def match_product(*names):
    text = " ".join(names).lower() + " "
    if any(word in text for word in NOT_A_PRODUCT):
        return None, None
    for product, category, words in PRODUCTS:
        if any(contains(text, word) for word in words):
            return product, category
    return None, None


def category_of(product_nl):
    detailed = product_nl.get("keyfacts", {}).get("wonCategoryPrimary", "").split("/")[-1]
    return LIDL_CATEGORIES.get(detailed) or LIDL_PAGE_CATEGORIES.get(product_nl.get("category"))


def brand_and_name(title):
    """Lidl prints the brand in capitals first: "COME A CASA Lasagne" -> ("COME A CASA", "Lasagne")."""
    words = title.split(" ")
    brand = []
    for word in words:
        letters = re.sub(r"[^A-Za-zÀ-ÿ]", "", word)
        if len(letters) >= 2 and letters.isupper():
            brand.append(word)
        else:
            break
    if not brand or len(brand) == len(words):
        return None, title
    return " ".join(brand), " ".join(words[len(brand):])


def deals_from(nl_products, fr_products, today):
    deals = []
    for product_id, nl in nl_products.items():
        fr = fr_products.get(product_id)
        if fr is None:
            continue
        ends = datetime.datetime.fromtimestamp(nl.get("storeEndDate", 0), BRUSSELS).date()
        if ends < today:
            continue
        nl_price, fr_price = price_of(nl), price_of(fr)
        if nl_price is None or fr_price is None:
            continue
        price, regular, nl_label, nl_base, card = nl_price
        fr_label, fr_base = fr_price[2], fr_price[3]
        brand, nl_name = brand_and_name(nl["fullTitle"])
        _, fr_name = brand_and_name(fr["fullTitle"])
        generic, category = match_product(nl_name, fr_name)
        category = category if category and category != "OTHER" else category_of(nl)
        if category is None:
            continue
        nl_label, fr_label = tidy_label(nl_label), tidy_label(fr_label)
        unit = "/kg" if (nl_base or "").startswith("/kg") else None
        deals.append({
            "id": product_id,
            "name": {"en": fr_name, "fr": fr_name, "nl": nl_name},
            "brand": brand,
            "size": (pack_size(nl_base), pack_size(fr_base)),
            "label": (nl_label, fr_label),
            "price": price, "unit": unit, "regular": regular, "card": card,
            "category": category, "product": generic,
            "until": ends.strftime("%d/%m"),
            "score": score(price, regular, nl_label, generic, category, card, nl["fullTitle"]),
        })
    return rank(deals)


def score(price, regular, label, generic, category, card, title):
    saved = (1 - price / regular) if regular else (label_discount(label) or 0.1 if label else 0)
    points = saved
    points += 0.15 if generic else 0
    points += 0.1 if category in EVERYDAY else 0
    points -= 0.03 if card else 0
    points -= 0.2 if "halloween" in title.lower() else 0
    return points


def rank(deals):
    """Best first, no more than MAX_PER_CATEGORY of one category ahead of the rest."""
    deals.sort(key=lambda deal: deal["score"], reverse=True)
    first, rest, per_category = [], [], {}
    for deal in deals:
        count = per_category.get(deal["category"], 0)
        (first if count < MAX_PER_CATEGORY else rest).append(deal)
        per_category[deal["category"]] = count + 1
    return (first + rest)[:MAX_DEALS]


def kotlin_string(text):
    return '"' + text.replace("\\", "\\\\").replace('"', '\\"').replace("$", "\\$") + '"'


def localized(en, fr, nl):
    if en == fr == nl:
        return f"LocalizedText.same({kotlin_string(nl)})"
    return f"LocalizedText(en = {kotlin_string(en)}, fr = {kotlin_string(fr)}, nl = {kotlin_string(nl)})"


def kotlin_deal(deal):
    nl_size, fr_size = deal["size"]
    nl_label, fr_label = deal["label"]
    size = localized(fr_size or nl_size, fr_size or nl_size, nl_size or fr_size) if (nl_size or fr_size) else "null"
    if nl_label or fr_label:
        english = english_label(nl_label) if nl_label else None
        label = localized(english or fr_label or nl_label, fr_label or nl_label, nl_label or fr_label)
    else:
        label = "null"
    lines = [
        f"name = {localized(**deal['name'])}",
        f"brand = {kotlin_string(deal['brand']) if deal['brand'] else 'null'}",
        f"packageSize = {size}",
        f"label = {label}",
        f"price = {deal['price']}",
    ]
    if deal["unit"]:
        lines.append(f"priceUnit = {kotlin_string(deal['unit'])}")
    if deal["regular"]:
        lines.append(f"regularPrice = {deal['regular']}")
    if deal["card"]:
        lines.append("needsLoyaltyCard = true")
    lines.append(f"category = ProductCategory.{deal['category']}")
    if deal["product"]:
        lines.append(f"product = GenericProduct.{deal['product']}")
    lines.append(f"validUntil = {kotlin_string(deal['until'])}")
    body = "".join(f"        {line},\n" for line in lines)
    return f"    // lidl.be product {deal['id']}\n    WeeklyDeal(\n        id = \"\",\n{body}    ),\n"


def kotlin_file(deals, today):
    return (
        f"// Generated by tools/lidl_deals.py from lidl.be on {today.isoformat()}. Do not edit by hand:\n"
        "// run the script again for a new week.\n"
        "package com.top10.deals.data.hardcoded\n\n"
        "import com.top10.deals.domain.model.GenericProduct\n"
        "import com.top10.deals.domain.model.LocalizedText\n"
        "import com.top10.deals.domain.model.ProductCategory\n"
        "import com.top10.deals.domain.model.WeeklyDeal\n\n"
        "/**\n"
        " * Lidl's offers of the week as lidl.be lists them, most relevant first; [HardcodedWeeklyDeals]\n"
        " * gives them their ids. English names are the French ones (no translation service is used).\n"
        " */\n"
        "internal val LIDL_WEEKLY_DEALS: List<WeeklyDeal> = listOf(\n"
        + "".join(kotlin_deal(deal) for deal in deals)
        + ")\n"
    )


def main():
    parser = argparse.ArgumentParser(description=__doc__, formatter_class=argparse.RawDescriptionHelpFormatter)
    parser.add_argument("--dry-run", action="store_true", help="print the ranking, write nothing")
    args = parser.parse_args()

    today = datetime.datetime.now(BRUSSELS).date()
    nl, fr = (products_on(fetch(PAGES[language])) for language in ("nl", "fr"))
    deals = deals_from(nl, fr, today)
    for place, deal in enumerate(deals, 1):
        nl_label = deal["label"][0] or ""
        print(f"{place:2}. {deal['score']:.2f} {deal['category']:<11} {deal['until']}  "
              f"€{deal['price']:<6} {nl_label:<14} {deal['brand'] or ''} {deal['name']['nl']}")
    if len(deals) < 20:
        print(f"\nOnly {len(deals)} deals: the debug screen needs 20 candidates.", file=sys.stderr)
    if args.dry_run:
        return
    OUTPUT.write_text(kotlin_file(deals, today), encoding="utf-8")
    print(f"\nWrote {len(deals)} deals to {OUTPUT.relative_to(ROOT)}. Rebuild and install the debug app to pick from them.")


if __name__ == "__main__":
    main()
