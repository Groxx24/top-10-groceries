package com.top10.groceries.domain.ranking

import com.top10.groceries.domain.model.ProductCategory

/**
 * Every number the ranking depends on, in one place. Tuning the list means editing this file.
 *
 * The idea: the category decides how much a deal can be worth at most, and the discount decides
 * how much of that it earns. The two multiply, so a staple cannot get in on a weak discount and a
 * deep discount on something minor still scores less than the same discount on meat.
 */
data class RankingPolicy(
    /** Discounts at or below this are not worth showing and score nothing. */
    val discountFloor: Double = 0.15,
    /** The discount that earns full marks: half price. */
    val discountTarget: Double = 0.50,
    /** How far above full marks a discount beyond [discountTarget] may go. */
    val discountFactorCap: Double = 1.2,
    /**
     * How much each extra item that must be bought takes off. Three packs of fresh meat are
     * harder to use up than one, so "2+1" loses to a plain discount of the same depth.
     */
    val penaltyPerExtraItem: Double = 0.08,
    /** Share of the score that depends on the euros saved rather than the percentage. */
    val savingShare: Double = 0.2,
    /** Saving, in euros, at which [savingShare] is earned in full. */
    val savingTarget: Double = 5.0,
    val categoryWeights: Map<ProductCategory, Double> = DefaultCategoryWeights,
    /** Weight for a category missing from [categoryWeights]. */
    val fallbackCategoryWeight: Double = 0.3,
    val listSize: Int = 10,
    /** At most this many places per category, so one aisle cannot fill the list. */
    val maxPerCategory: Int = 3,
)

val DefaultCategoryWeights: Map<ProductCategory, Double> = mapOf(
    // What a weekly shop is built around.
    ProductCategory.MEAT_FISH to 1.0,
    ProductCategory.BREAD to 1.0,
    ProductCategory.FRUIT_VEGETABLES to 1.0,
    ProductCategory.VEGETARIAN to 0.85,
    ProductCategory.DAIRY_EGGS to 0.8,
    // Food, but not the fresh core.
    ProductCategory.READY_MEALS to 0.65,
    ProductCategory.FROZEN to 0.6,
    ProductCategory.PANTRY to 0.6,
    ProductCategory.CANNED to 0.55,
    ProductCategory.SPECIAL_DIET to 0.5,
    ProductCategory.DRINKS to 0.45,
    ProductCategory.SWEETS_PASTRY to 0.4,
    // Not food. Worth a place only when the discount is deep.
    ProductCategory.HYGIENE to 0.35,
    ProductCategory.HOUSEHOLD to 0.35,
    ProductCategory.ALCOHOL to 0.25,
    ProductCategory.HOME to 0.2,
    // Only useful to some households, and both run a great many promotions.
    ProductCategory.BABY to 0.15,
    ProductCategory.PET to 0.15,
    ProductCategory.OTHER to 0.3,
)
