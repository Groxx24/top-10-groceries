package com.top10.deals.ui.deal

import com.top10.deals.domain.model.GenericProduct
import com.top10.deals.domain.model.GenericProduct.APPLES
import com.top10.deals.domain.model.GenericProduct.ASPARAGUS
import com.top10.deals.domain.model.GenericProduct.AVOCADO
import com.top10.deals.domain.model.GenericProduct.BAGUETTE
import com.top10.deals.domain.model.GenericProduct.BANANAS
import com.top10.deals.domain.model.GenericProduct.BEEF_STEAK
import com.top10.deals.domain.model.GenericProduct.BEER
import com.top10.deals.domain.model.GenericProduct.BELL_PEPPERS
import com.top10.deals.domain.model.GenericProduct.BISCUITS
import com.top10.deals.domain.model.GenericProduct.BLACK_GRAPES
import com.top10.deals.domain.model.GenericProduct.BLUEBERRIES
import com.top10.deals.domain.model.GenericProduct.BREAD
import com.top10.deals.domain.model.GenericProduct.BROCCOLI
import com.top10.deals.domain.model.GenericProduct.BRUSSELS_SPROUTS
import com.top10.deals.domain.model.GenericProduct.BURGERS
import com.top10.deals.domain.model.GenericProduct.BUTTER
import com.top10.deals.domain.model.GenericProduct.CAKE
import com.top10.deals.domain.model.GenericProduct.CANDY
import com.top10.deals.domain.model.GenericProduct.CANNED_FOOD
import com.top10.deals.domain.model.GenericProduct.CARROTS
import com.top10.deals.domain.model.GenericProduct.CAULIFLOWER
import com.top10.deals.domain.model.GenericProduct.CEREAL
import com.top10.deals.domain.model.GenericProduct.CEREAL_BARS
import com.top10.deals.domain.model.GenericProduct.CHEESE
import com.top10.deals.domain.model.GenericProduct.CHERRIES
import com.top10.deals.domain.model.GenericProduct.CHICKEN_BREAST
import com.top10.deals.domain.model.GenericProduct.CHOCOLATE
import com.top10.deals.domain.model.GenericProduct.CLEANING
import com.top10.deals.domain.model.GenericProduct.COD
import com.top10.deals.domain.model.GenericProduct.COFFEE_BEANS
import com.top10.deals.domain.model.GenericProduct.COFFEE_CAPSULES
import com.top10.deals.domain.model.GenericProduct.COURGETTE
import com.top10.deals.domain.model.GenericProduct.CRISPS
import com.top10.deals.domain.model.GenericProduct.CROISSANTS
import com.top10.deals.domain.model.GenericProduct.CROQUETTES
import com.top10.deals.domain.model.GenericProduct.CUCUMBER
import com.top10.deals.domain.model.GenericProduct.DISH_SOAP
import com.top10.deals.domain.model.GenericProduct.DOG_FOOD
import com.top10.deals.domain.model.GenericProduct.DONUTS
import com.top10.deals.domain.model.GenericProduct.EGGS
import com.top10.deals.domain.model.GenericProduct.ENDIVES
import com.top10.deals.domain.model.GenericProduct.FISH_FINGERS
import com.top10.deals.domain.model.GenericProduct.FRIES
import com.top10.deals.domain.model.GenericProduct.GARLIC
import com.top10.deals.domain.model.GenericProduct.GIN
import com.top10.deals.domain.model.GenericProduct.GREEN_BEANS
import com.top10.deals.domain.model.GenericProduct.GROUND_COFFEE
import com.top10.deals.domain.model.GenericProduct.HAM
import com.top10.deals.domain.model.GenericProduct.HONEY
import com.top10.deals.domain.model.GenericProduct.ICED_TEA
import com.top10.deals.domain.model.GenericProduct.ICE_CREAM
import com.top10.deals.domain.model.GenericProduct.INSTANT_COFFEE
import com.top10.deals.domain.model.GenericProduct.JAM
import com.top10.deals.domain.model.GenericProduct.KIWI
import com.top10.deals.domain.model.GenericProduct.LASAGNE
import com.top10.deals.domain.model.GenericProduct.LAUNDRY_DETERGENT
import com.top10.deals.domain.model.GenericProduct.LEEKS
import com.top10.deals.domain.model.GenericProduct.LEMONADE
import com.top10.deals.domain.model.GenericProduct.LEMONS
import com.top10.deals.domain.model.GenericProduct.LETTUCE
import com.top10.deals.domain.model.GenericProduct.MANDARINS
import com.top10.deals.domain.model.GenericProduct.MANGO
import com.top10.deals.domain.model.GenericProduct.MELON
import com.top10.deals.domain.model.GenericProduct.MILK
import com.top10.deals.domain.model.GenericProduct.MINCED_MEAT
import com.top10.deals.domain.model.GenericProduct.MUSHROOMS
import com.top10.deals.domain.model.GenericProduct.MUSSELS
import com.top10.deals.domain.model.GenericProduct.NUTS
import com.top10.deals.domain.model.GenericProduct.OLIVES
import com.top10.deals.domain.model.GenericProduct.OLIVE_OIL
import com.top10.deals.domain.model.GenericProduct.ONIONS
import com.top10.deals.domain.model.GenericProduct.ORANGES
import com.top10.deals.domain.model.GenericProduct.ORANGE_JUICE
import com.top10.deals.domain.model.GenericProduct.PASTA
import com.top10.deals.domain.model.GenericProduct.PEACHES
import com.top10.deals.domain.model.GenericProduct.PEARS
import com.top10.deals.domain.model.GenericProduct.PINEAPPLE
import com.top10.deals.domain.model.GenericProduct.PIZZA
import com.top10.deals.domain.model.GenericProduct.PLUMS
import com.top10.deals.domain.model.GenericProduct.POPCORN
import com.top10.deals.domain.model.GenericProduct.PORK_CHOPS
import com.top10.deals.domain.model.GenericProduct.POTATOES
import com.top10.deals.domain.model.GenericProduct.RASPBERRIES
import com.top10.deals.domain.model.GenericProduct.RED_WINE
import com.top10.deals.domain.model.GenericProduct.RICE
import com.top10.deals.domain.model.GenericProduct.ROSE_WINE
import com.top10.deals.domain.model.GenericProduct.SALAMI
import com.top10.deals.domain.model.GenericProduct.SALMON
import com.top10.deals.domain.model.GenericProduct.SAUSAGES
import com.top10.deals.domain.model.GenericProduct.SCALLOPS
import com.top10.deals.domain.model.GenericProduct.SOAP
import com.top10.deals.domain.model.GenericProduct.SOFT_CHEESE
import com.top10.deals.domain.model.GenericProduct.SOFT_DRINK_CANS
import com.top10.deals.domain.model.GenericProduct.SOUP
import com.top10.deals.domain.model.GenericProduct.SPAGHETTI
import com.top10.deals.domain.model.GenericProduct.SPARKLING_WINE
import com.top10.deals.domain.model.GenericProduct.SPINACH
import com.top10.deals.domain.model.GenericProduct.SPONGES
import com.top10.deals.domain.model.GenericProduct.SQUASH
import com.top10.deals.domain.model.GenericProduct.STRAWBERRIES
import com.top10.deals.domain.model.GenericProduct.SUGAR
import com.top10.deals.domain.model.GenericProduct.SWEETCORN
import com.top10.deals.domain.model.GenericProduct.TEA
import com.top10.deals.domain.model.GenericProduct.TISSUES
import com.top10.deals.domain.model.GenericProduct.TOILET_PAPER
import com.top10.deals.domain.model.GenericProduct.TOMATOES
import com.top10.deals.domain.model.GenericProduct.TOOTHPASTE
import com.top10.deals.domain.model.GenericProduct.WAFFLES
import com.top10.deals.domain.model.GenericProduct.WATER
import com.top10.deals.domain.model.GenericProduct.WATERMELON
import com.top10.deals.domain.model.GenericProduct.WHISKY
import com.top10.deals.domain.model.GenericProduct.WHITE_GRAPES
import com.top10.deals.domain.model.GenericProduct.WHITE_WINE
import com.top10.deals.domain.model.GenericProduct.WHOLE_CHICKEN
import com.top10.deals.domain.model.GenericProduct.YOGHURT
import com.top10.deals.resources.Res
import com.top10.deals.resources.product_apples
import com.top10.deals.resources.product_asparagus
import com.top10.deals.resources.product_avocado
import com.top10.deals.resources.product_baguette
import com.top10.deals.resources.product_bananas
import com.top10.deals.resources.product_beef_steak
import com.top10.deals.resources.product_beer
import com.top10.deals.resources.product_bell_peppers
import com.top10.deals.resources.product_biscuits
import com.top10.deals.resources.product_black_grapes
import com.top10.deals.resources.product_blueberries
import com.top10.deals.resources.product_bread
import com.top10.deals.resources.product_broccoli
import com.top10.deals.resources.product_brussels_sprouts
import com.top10.deals.resources.product_burgers
import com.top10.deals.resources.product_butter
import com.top10.deals.resources.product_cake
import com.top10.deals.resources.product_candy
import com.top10.deals.resources.product_canned_food
import com.top10.deals.resources.product_carrots
import com.top10.deals.resources.product_cauliflower
import com.top10.deals.resources.product_cereal
import com.top10.deals.resources.product_cereal_bars
import com.top10.deals.resources.product_cheese
import com.top10.deals.resources.product_cherries
import com.top10.deals.resources.product_chicken_breast
import com.top10.deals.resources.product_chocolate
import com.top10.deals.resources.product_cleaning
import com.top10.deals.resources.product_cod
import com.top10.deals.resources.product_coffee_beans
import com.top10.deals.resources.product_coffee_capsules
import com.top10.deals.resources.product_courgette
import com.top10.deals.resources.product_crisps
import com.top10.deals.resources.product_croissants
import com.top10.deals.resources.product_croquettes
import com.top10.deals.resources.product_cucumber
import com.top10.deals.resources.product_dish_soap
import com.top10.deals.resources.product_dog_food
import com.top10.deals.resources.product_donuts
import com.top10.deals.resources.product_eggs
import com.top10.deals.resources.product_endives
import com.top10.deals.resources.product_fish_fingers
import com.top10.deals.resources.product_fries
import com.top10.deals.resources.product_garlic
import com.top10.deals.resources.product_gin
import com.top10.deals.resources.product_green_beans
import com.top10.deals.resources.product_ground_coffee
import com.top10.deals.resources.product_ham
import com.top10.deals.resources.product_honey
import com.top10.deals.resources.product_ice_cream
import com.top10.deals.resources.product_iced_tea
import com.top10.deals.resources.product_instant_coffee
import com.top10.deals.resources.product_jam
import com.top10.deals.resources.product_kiwi
import com.top10.deals.resources.product_lasagne
import com.top10.deals.resources.product_laundry_detergent
import com.top10.deals.resources.product_leeks
import com.top10.deals.resources.product_lemonade
import com.top10.deals.resources.product_lemons
import com.top10.deals.resources.product_lettuce
import com.top10.deals.resources.product_mandarins
import com.top10.deals.resources.product_mango
import com.top10.deals.resources.product_melon
import com.top10.deals.resources.product_milk
import com.top10.deals.resources.product_minced_meat
import com.top10.deals.resources.product_mushrooms
import com.top10.deals.resources.product_mussels
import com.top10.deals.resources.product_nuts
import com.top10.deals.resources.product_olive_oil
import com.top10.deals.resources.product_olives
import com.top10.deals.resources.product_onions
import com.top10.deals.resources.product_orange_juice
import com.top10.deals.resources.product_oranges
import com.top10.deals.resources.product_pasta
import com.top10.deals.resources.product_peaches
import com.top10.deals.resources.product_pears
import com.top10.deals.resources.product_pineapple
import com.top10.deals.resources.product_pizza
import com.top10.deals.resources.product_plums
import com.top10.deals.resources.product_popcorn
import com.top10.deals.resources.product_pork_chops
import com.top10.deals.resources.product_potatoes
import com.top10.deals.resources.product_raspberries
import com.top10.deals.resources.product_red_wine
import com.top10.deals.resources.product_rice
import com.top10.deals.resources.product_rose_wine
import com.top10.deals.resources.product_salami
import com.top10.deals.resources.product_salmon
import com.top10.deals.resources.product_sausages
import com.top10.deals.resources.product_scallops
import com.top10.deals.resources.product_soap
import com.top10.deals.resources.product_soft_cheese
import com.top10.deals.resources.product_soft_drink_cans
import com.top10.deals.resources.product_soup
import com.top10.deals.resources.product_spaghetti
import com.top10.deals.resources.product_sparkling_wine
import com.top10.deals.resources.product_spinach
import com.top10.deals.resources.product_sponges
import com.top10.deals.resources.product_squash
import com.top10.deals.resources.product_strawberries
import com.top10.deals.resources.product_sugar
import com.top10.deals.resources.product_sweetcorn
import com.top10.deals.resources.product_tea
import com.top10.deals.resources.product_tissues
import com.top10.deals.resources.product_toilet_paper
import com.top10.deals.resources.product_tomatoes
import com.top10.deals.resources.product_toothpaste
import com.top10.deals.resources.product_waffles
import com.top10.deals.resources.product_water
import com.top10.deals.resources.product_watermelon
import com.top10.deals.resources.product_whisky
import com.top10.deals.resources.product_white_grapes
import com.top10.deals.resources.product_white_wine
import com.top10.deals.resources.product_whole_chicken
import com.top10.deals.resources.product_yoghurt
import org.jetbrains.compose.resources.DrawableResource

/**
 * The product's photo, a generic one (not of a particular brand), cropped square in
 * `composeResources/drawable/product_*.webp`. Where each came from is in `PRODUCT_PHOTOS.md`.
 */
internal fun GenericProduct.photo(): DrawableResource = when (this) {
    APPLES -> Res.drawable.product_apples
    BANANAS -> Res.drawable.product_bananas
    PEARS -> Res.drawable.product_pears
    ORANGES -> Res.drawable.product_oranges
    MANDARINS -> Res.drawable.product_mandarins
    LEMONS -> Res.drawable.product_lemons
    WHITE_GRAPES -> Res.drawable.product_white_grapes
    BLACK_GRAPES -> Res.drawable.product_black_grapes
    STRAWBERRIES -> Res.drawable.product_strawberries
    RASPBERRIES -> Res.drawable.product_raspberries
    BLUEBERRIES -> Res.drawable.product_blueberries
    MANGO -> Res.drawable.product_mango
    PINEAPPLE -> Res.drawable.product_pineapple
    KIWI -> Res.drawable.product_kiwi
    AVOCADO -> Res.drawable.product_avocado
    WATERMELON -> Res.drawable.product_watermelon
    MELON -> Res.drawable.product_melon
    CHERRIES -> Res.drawable.product_cherries
    PEACHES -> Res.drawable.product_peaches
    PLUMS -> Res.drawable.product_plums
    TOMATOES -> Res.drawable.product_tomatoes
    CUCUMBER -> Res.drawable.product_cucumber
    CARROTS -> Res.drawable.product_carrots
    POTATOES -> Res.drawable.product_potatoes
    ONIONS -> Res.drawable.product_onions
    BROCCOLI -> Res.drawable.product_broccoli
    CAULIFLOWER -> Res.drawable.product_cauliflower
    ENDIVES -> Res.drawable.product_endives
    BRUSSELS_SPROUTS -> Res.drawable.product_brussels_sprouts
    LETTUCE -> Res.drawable.product_lettuce
    BELL_PEPPERS -> Res.drawable.product_bell_peppers
    COURGETTE -> Res.drawable.product_courgette
    MUSHROOMS -> Res.drawable.product_mushrooms
    LEEKS -> Res.drawable.product_leeks
    SPINACH -> Res.drawable.product_spinach
    SQUASH -> Res.drawable.product_squash
    GARLIC -> Res.drawable.product_garlic
    GREEN_BEANS -> Res.drawable.product_green_beans
    ASPARAGUS -> Res.drawable.product_asparagus
    SWEETCORN -> Res.drawable.product_sweetcorn
    BEEF_STEAK -> Res.drawable.product_beef_steak
    MINCED_MEAT -> Res.drawable.product_minced_meat
    PORK_CHOPS -> Res.drawable.product_pork_chops
    SAUSAGES -> Res.drawable.product_sausages
    HAM -> Res.drawable.product_ham
    SALAMI -> Res.drawable.product_salami
    BURGERS -> Res.drawable.product_burgers
    CHICKEN_BREAST -> Res.drawable.product_chicken_breast
    WHOLE_CHICKEN -> Res.drawable.product_whole_chicken
    SALMON -> Res.drawable.product_salmon
    COD -> Res.drawable.product_cod
    MUSSELS -> Res.drawable.product_mussels
    SCALLOPS -> Res.drawable.product_scallops
    FISH_FINGERS -> Res.drawable.product_fish_fingers
    EGGS -> Res.drawable.product_eggs
    MILK -> Res.drawable.product_milk
    BUTTER -> Res.drawable.product_butter
    YOGHURT -> Res.drawable.product_yoghurt
    CHEESE -> Res.drawable.product_cheese
    SOFT_CHEESE -> Res.drawable.product_soft_cheese
    BREAD -> Res.drawable.product_bread
    BAGUETTE -> Res.drawable.product_baguette
    CROISSANTS -> Res.drawable.product_croissants
    DONUTS -> Res.drawable.product_donuts
    CAKE -> Res.drawable.product_cake
    WAFFLES -> Res.drawable.product_waffles
    BISCUITS -> Res.drawable.product_biscuits
    CHOCOLATE -> Res.drawable.product_chocolate
    CANDY -> Res.drawable.product_candy
    CEREAL_BARS -> Res.drawable.product_cereal_bars
    CEREAL -> Res.drawable.product_cereal
    JAM -> Res.drawable.product_jam
    HONEY -> Res.drawable.product_honey
    PASTA -> Res.drawable.product_pasta
    SPAGHETTI -> Res.drawable.product_spaghetti
    RICE -> Res.drawable.product_rice
    LASAGNE -> Res.drawable.product_lasagne
    PIZZA -> Res.drawable.product_pizza
    CANNED_FOOD -> Res.drawable.product_canned_food
    SOUP -> Res.drawable.product_soup
    FRIES -> Res.drawable.product_fries
    CROQUETTES -> Res.drawable.product_croquettes
    ICE_CREAM -> Res.drawable.product_ice_cream
    CRISPS -> Res.drawable.product_crisps
    NUTS -> Res.drawable.product_nuts
    POPCORN -> Res.drawable.product_popcorn
    OLIVES -> Res.drawable.product_olives
    ORANGE_JUICE -> Res.drawable.product_orange_juice
    LEMONADE -> Res.drawable.product_lemonade
    SOFT_DRINK_CANS -> Res.drawable.product_soft_drink_cans
    ICED_TEA -> Res.drawable.product_iced_tea
    WATER -> Res.drawable.product_water
    COFFEE_BEANS -> Res.drawable.product_coffee_beans
    GROUND_COFFEE -> Res.drawable.product_ground_coffee
    COFFEE_CAPSULES -> Res.drawable.product_coffee_capsules
    INSTANT_COFFEE -> Res.drawable.product_instant_coffee
    TEA -> Res.drawable.product_tea
    BEER -> Res.drawable.product_beer
    RED_WINE -> Res.drawable.product_red_wine
    WHITE_WINE -> Res.drawable.product_white_wine
    ROSE_WINE -> Res.drawable.product_rose_wine
    SPARKLING_WINE -> Res.drawable.product_sparkling_wine
    WHISKY -> Res.drawable.product_whisky
    GIN -> Res.drawable.product_gin
    LAUNDRY_DETERGENT -> Res.drawable.product_laundry_detergent
    DISH_SOAP -> Res.drawable.product_dish_soap
    SPONGES -> Res.drawable.product_sponges
    CLEANING -> Res.drawable.product_cleaning
    TOILET_PAPER -> Res.drawable.product_toilet_paper
    TISSUES -> Res.drawable.product_tissues
    TOOTHPASTE -> Res.drawable.product_toothpaste
    SOAP -> Res.drawable.product_soap
    DOG_FOOD -> Res.drawable.product_dog_food
    OLIVE_OIL -> Res.drawable.product_olive_oil
    SUGAR -> Res.drawable.product_sugar
}
