package com.top10.deals.ui.deal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.top10.deals.domain.model.WeeklyDeal
import com.top10.deals.domain.model.discount
import com.top10.deals.resources.Res
import com.top10.deals.resources.deal_loyalty_card
import com.top10.deals.resources.deal_percent_minus
import com.top10.deals.resources.deal_price
import com.top10.deals.resources.deal_valid_until
import com.top10.deals.resources.decimal_separator
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * A deal as the folder gives it, in the phone's language: its brand as the title with what the
 * product is below it (or that as the title when there is no brand), the pack size, the deal
 * label, and the prices. The pick screen and the published top list both show deals this way.
 */
@Composable
fun DealInfo(deal: WeeklyDeal, modifier: Modifier = Modifier) {
    // The phone's language, falling back to English like the app's own strings do.
    val language = Locale.current.language
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        val name = deal.name.inLanguage(language)
        Text(
            text = deal.brand ?: name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Description(description = name.takeIf { deal.brand != null }, size = deal.packageSize?.inLanguage(language))
        DealLabel(deal, language)
        DealDetails(deal)
    }
}

/**
 * "Cheese · 180 g" under a branded deal's brand, or only the pack size when the deal has no brand
 * and its description is already the title.
 */
@Composable
private fun Description(description: String?, size: String?) {
    val parts = listOfNotNull(description, size)
    if (parts.isEmpty()) return
    Text(
        text = parts.joinToString(SEPARATOR),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/** The folder's own wording; the percentage off only when it printed none. */
@Composable
private fun DealLabel(deal: WeeklyDeal, language: String) {
    val label = deal.label?.inLanguage(language)
        ?: deal.discount?.let { stringResource(Res.string.deal_percent_minus, (it * 100).roundToInt()) }
        ?: return
    Text(
        text = label,
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onTertiaryContainer,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

/**
 * "~~€6.18~~ €3.09 /2 · Until 07/10 · Loyalty card needed" (in French "~~6,18 €~~ 3,09 € /2 · …"),
 * with only what the folder gives. The deal price is in red; the original price before it is
 * struck through.
 */
@Composable
private fun DealDetails(deal: WeeklyDeal) {
    val dealPriceColor = MaterialTheme.colorScheme.error
    val price = deal.price?.let { euros(it) }
    val regularPrice = deal.regularPrice?.let { euros(it) }
    val parts = listOfNotNull(
        price?.let { priceText(it, regularPrice, deal.priceUnit, dealPriceColor) },
        deal.validUntil?.let { AnnotatedString(stringResource(Res.string.deal_valid_until, it)) },
        if (deal.needsLoyaltyCard) AnnotatedString(stringResource(Res.string.deal_loyalty_card)) else null,
    )
    Text(
        text = parts.joinToAnnotatedString(SEPARATOR),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

/** "€3.09" in the phone's language's format. */
@Composable
private fun euros(amount: Double): String =
    stringResource(Res.string.deal_price, formatEuros(amount, stringResource(Res.string.decimal_separator)))

/** "~~€6.18~~ €3.09 /2": the [regularPrice] struck through, then the deal [price] in [priceColor]. */
private fun priceText(price: String, regularPrice: String?, unit: String?, priceColor: Color) = buildAnnotatedString {
    if (regularPrice != null) {
        withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { append(regularPrice) }
        append(" ")
    }
    withStyle(SpanStyle(color = priceColor, fontWeight = FontWeight.Bold)) { append(price) }
    unit?.let { append(" $it") }
}

private fun List<AnnotatedString>.joinToAnnotatedString(separator: String) = buildAnnotatedString {
    this@joinToAnnotatedString.forEachIndexed { index, part ->
        if (index > 0) append(separator)
        append(part)
    }
}

private const val SEPARATOR = " · "

/** 5.853 becomes "5.85", or "5,85" with a comma [decimalSeparator]. Common code has no number formatter. */
internal fun formatEuros(amount: Double, decimalSeparator: String = "."): String {
    val cents = (amount * 100).roundToInt()
    return "${cents / 100}$decimalSeparator${(cents % 100).toString().padStart(2, '0')}"
}
