package com.top10.deals.domain.model

/**
 * An everyday product a deal can be for ("bananas", "toilet paper"), each one a product the app has
 * a generic photo of; a deal that is none of these shows its [ProductCategory]'s picture instead.
 * Published top lists store the entry's [name], so renaming one breaks lists already in Firestore,
 * and removing one makes those deals fall back to their category.
 */
enum class GenericProduct {
    // Fruit
    APPLES,
    BANANAS,
    PEARS,
    ORANGES,
    MANDARINS,
    LEMONS,
    WHITE_GRAPES,
    BLACK_GRAPES,
    STRAWBERRIES,
    RASPBERRIES,
    BLUEBERRIES,
    MANGO,
    PINEAPPLE,
    KIWI,
    AVOCADO,
    WATERMELON,
    MELON,
    CHERRIES,
    PEACHES,
    PLUMS,

    // Vegetables
    TOMATOES,
    CUCUMBER,
    CARROTS,
    POTATOES,
    ONIONS,
    BROCCOLI,
    CAULIFLOWER,
    ENDIVES,
    BRUSSELS_SPROUTS,
    LETTUCE,
    BELL_PEPPERS,
    COURGETTE,
    MUSHROOMS,
    LEEKS,
    SPINACH,
    SQUASH,
    GARLIC,
    GREEN_BEANS,
    ASPARAGUS,
    SWEETCORN,

    // Meat and poultry
    BEEF_STEAK,
    MINCED_MEAT,
    PORK_CHOPS,
    SAUSAGES,
    HAM,
    SALAMI,
    BURGERS,
    CHICKEN_BREAST,
    WHOLE_CHICKEN,

    // Fish and seafood
    SALMON,
    COD,
    MUSSELS,
    SCALLOPS,
    FISH_FINGERS,

    // Eggs and dairy
    EGGS,
    MILK,
    BUTTER,
    YOGHURT,
    CHEESE,
    SOFT_CHEESE,

    // Bread, cakes and sweets
    BREAD,
    BAGUETTE,
    CROISSANTS,
    DONUTS,
    CAKE,
    WAFFLES,
    BISCUITS,
    CHOCOLATE,
    CANDY,
    CEREAL_BARS,
    CEREAL,
    JAM,
    HONEY,

    // Pasta, ready meals and frozen
    PASTA,
    SPAGHETTI,
    RICE,
    LASAGNE,
    PIZZA,
    CANNED_FOOD,
    SOUP,
    FRIES,
    CROQUETTES,
    ICE_CREAM,

    // Snacks
    CRISPS,
    NUTS,
    POPCORN,
    OLIVES,

    // Drinks
    ORANGE_JUICE,
    LEMONADE,
    SOFT_DRINK_CANS,
    ICED_TEA,
    WATER,
    COFFEE_BEANS,
    GROUND_COFFEE,
    COFFEE_CAPSULES,
    INSTANT_COFFEE,
    TEA,
    BEER,
    RED_WINE,
    WHITE_WINE,
    ROSE_WINE,
    SPARKLING_WINE,
    WHISKY,
    GIN,

    // Household
    LAUNDRY_DETERGENT,
    DISH_SOAP,
    SPONGES,
    CLEANING,
    TOILET_PAPER,
    TISSUES,

    // Personal care and baby
    TOOTHPASTE,
    SOAP,

    // Pets
    DOG_FOOD,

    // Cupboard
    OLIVE_OIL,
    SUGAR,
    ;

    companion object {
        /** The product stored as [name], or null for a missing or unknown one. */
        fun fromName(name: String?): GenericProduct? = entries.firstOrNull { it.name == name }
    }
}
