package com.top10.deals.ui.top

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.top10.deals.domain.model.Offer
import com.top10.deals.ui.deal.DealInfo
import com.top10.deals.ui.deal.DealPicture

/** One place in the published top list: the deal's picture with its rank, then the deal as the pick screen shows it. */
@Composable
fun OfferCard(offer: Offer) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DealPicture(offer.deal.category, rank = offer.rank)
            DealInfo(offer.deal, Modifier.weight(1f))
        }
    }
}
