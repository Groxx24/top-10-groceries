package com.top10.deals.domain.model

import kotlinx.datetime.LocalDate

/**
 * The last day of this list: the day its first deal ends ("valid until 07/10" is 7 October), read
 * as of [today]. Deals whose end cannot be read do not count; null when none can be.
 */
fun TopOffers.lastDay(today: LocalDate): LocalDate? =
    offers.mapNotNull { offer -> offer.deal.validUntil?.let { dueDate(it, fetchedOn = today) } }.minOrNull()

/** Whether this deal is over on [today]: its "valid until" was before. A deal with no end never is. */
fun WeeklyDeal.hasEnded(today: LocalDate): Boolean =
    validUntil?.let { dueDate(it, fetchedOn = today) }?.let { today > it } ?: false

/** Whether this list is over on [today]: its [lastDay] was before. A list with no end never is. */
fun TopOffers.hasEnded(today: LocalDate): Boolean = lastDay(today)?.let { today > it } ?: false
