package com.top10.groceries.ui.top

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.top10.groceries.domain.model.Offer
import com.top10.groceries.domain.model.RankedOffer
import com.top10.groceries.domain.model.saving
import com.top10.groceries.resources.Res
import com.top10.groceries.resources.deal_buy_quantity
import com.top10.groceries.resources.deal_loyalty_card
import com.top10.groceries.resources.deal_percent_off
import com.top10.groceries.resources.deal_price_each
import com.top10.groceries.resources.deal_same_deal_count
import com.top10.groceries.resources.deal_saving
import com.top10.groceries.resources.deal_valid_until
import com.top10.groceries.resources.score_label
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.roundToInt

@Composable
fun OfferCard(ranked: RankedOffer, onOpenProduct: (String) -> Unit) {
    val offer = ranked.offer
    val content: @Composable () -> Unit = {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RankBadge(ranked.rank)
                ProductImage(offer.imageUrl)
                Column(Modifier.weight(1f)) {
                    offer.brand?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                    Text(
                        text = offer.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Text(
                        text = listOfNotNull(offer.categoryLabel.ifBlank { null }, offer.packageSize)
                            .joinToString(" · "),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Score(ranked.score.value)
            }
            DealSummary(offer)
            DealDetails(ranked)
        }
    }
    val url = offer.productUrl
    if (url != null) {
        Card(onClick = { onOpenProduct(url) }, modifier = Modifier.fillMaxWidth()) { content() }
    } else {
        Card(modifier = Modifier.fillMaxWidth()) { content() }
    }
}

@Composable
private fun RankBadge(rank: Int) {
    Box(
        modifier = Modifier.size(32.dp).background(MaterialTheme.colorScheme.primary, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimary,
        )
    }
}

@Composable
private fun ProductImage(url: String?) {
    // Product photos are shot on white, so the tile stays white in the dark theme too.
    AsyncImage(
        model = url,
        contentDescription = null,
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .size(64.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.White)
            .padding(4.dp),
    )
}

@Composable
private fun Score(value: Double) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = value.roundToInt().toString(),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = stringResource(Res.string.score_label),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** The store's own label, then what it comes down to: "1+1 gratis  50% off when you buy 2". */
@Composable
private fun DealSummary(offer: Offer) {
    val deal = offer.deal
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = deal.label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier
                .background(MaterialTheme.colorScheme.tertiaryContainer, RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
        )
        val percentOff = stringResource(Res.string.deal_percent_off, (deal.discount * 100).roundToInt())
        Text(
            text = if (deal.requiredQuantity > 1) {
                percentOff + " " + stringResource(Res.string.deal_buy_quantity, deal.requiredQuantity)
            } else {
                percentOff
            },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun DealDetails(ranked: RankedOffer) {
    val offer = ranked.offer
    val lines = listOfNotNull(
        listOf(
            stringResource(Res.string.deal_price_each, formatEuros(offer.price)),
            stringResource(Res.string.deal_saving, formatEuros(offer.saving)),
        ).joinToString(" · "),
        listOfNotNull(
            offer.deal.validUntil?.let { stringResource(Res.string.deal_valid_until, it) },
            if (offer.deal.needsLoyaltyCard) stringResource(Res.string.deal_loyalty_card) else null,
        ).joinToString(" · ").ifEmpty { null },
        if (ranked.sameDealCount > 0) {
            pluralStringResource(Res.plurals.deal_same_deal_count, ranked.sameDealCount, ranked.sameDealCount)
        } else {
            null
        },
    )
    Column {
        lines.forEach {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

/** 5.853 becomes "5.85". Common code has no number formatter. */
internal fun formatEuros(amount: Double): String {
    val cents = (amount * 100).roundToInt()
    return "${cents / 100}.${(cents % 100).toString().padStart(2, '0')}"
}
