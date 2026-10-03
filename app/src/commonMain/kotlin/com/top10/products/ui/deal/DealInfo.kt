package com.top10.products.ui.deal

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.top10.products.domain.model.WeeklyDeal
import com.top10.products.domain.model.discount
import com.top10.products.resources.Res
import com.top10.products.resources.deal_loyalty_card
import com.top10.products.resources.deal_percent_minus
import com.top10.products.resources.deal_price
import com.top10.products.resources.deal_valid_until
import com.top10.products.resources.decimal_separator
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

/**
 * A deal as the folder gives it: name, brand and pack size, the deal label, and the prices. The
 * pick screen and the published top list both show deals this way.
 */
@Composable
fun DealInfo(deal: WeeklyDeal, modifier: Modifier = Modifier) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = deal.name,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        listOfNotNull(deal.brand, deal.packageSize).takeIf { it.isNotEmpty() }?.let {
            Text(
                text = it.joinToString(" · "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        DealLabel(deal)
        DealDetails(deal)
    }
}

/** The folder's own wording; the percentage off only when it printed none. */
@Composable
private fun DealLabel(deal: WeeklyDeal) {
    val label = deal.label
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
    val separator = stringResource(Res.string.decimal_separator)
    val regularPrice = deal.regularPrice?.let { stringResource(Res.string.deal_price, formatEuros(it, separator)) }
    val price = deal.price?.let { stringResource(Res.string.deal_price, formatEuros(it, separator)) }
    val rest = listOfNotNull(
        deal.validUntil?.let { stringResource(Res.string.deal_valid_until, it) },
        if (deal.needsLoyaltyCard) stringResource(Res.string.deal_loyalty_card) else null,
    )
    val dealPriceColor = MaterialTheme.colorScheme.error
    Text(
        text = buildAnnotatedString {
            if (price != null) {
                if (regularPrice != null) {
                    withStyle(SpanStyle(textDecoration = TextDecoration.LineThrough)) { append(regularPrice) }
                    append(" ")
                }
                withStyle(SpanStyle(color = dealPriceColor, fontWeight = FontWeight.Bold)) { append(price) }
                deal.priceUnit?.let { append(" $it") }
                if (rest.isNotEmpty()) append(" · ")
            }
            append(rest.joinToString(" · "))
        },
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

/** 5.853 becomes "5.85", or "5,85" with a comma [decimalSeparator]. Common code has no number formatter. */
internal fun formatEuros(amount: Double, decimalSeparator: String = "."): String {
    val cents = (amount * 100).roundToInt()
    return "${cents / 100}$decimalSeparator${(cents % 100).toString().padStart(2, '0')}"
}
