package com.top10.deals.ui.deal

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.top10.deals.domain.model.ProductCategory
import com.top10.deals.domain.model.ProductCategory.BABY
import com.top10.deals.domain.model.ProductCategory.BAKERY
import com.top10.deals.domain.model.ProductCategory.BEER
import com.top10.deals.domain.model.ProductCategory.CANNED
import com.top10.deals.domain.model.ProductCategory.CHEESE
import com.top10.deals.domain.model.ProductCategory.CLEANING
import com.top10.deals.domain.model.ProductCategory.COFFEE_TEA
import com.top10.deals.domain.model.ProductCategory.DAIRY
import com.top10.deals.domain.model.ProductCategory.EGGS
import com.top10.deals.domain.model.ProductCategory.FISH
import com.top10.deals.domain.model.ProductCategory.FROZEN
import com.top10.deals.domain.model.ProductCategory.FRUIT
import com.top10.deals.domain.model.ProductCategory.ICE_CREAM
import com.top10.deals.domain.model.ProductCategory.LAUNDRY
import com.top10.deals.domain.model.ProductCategory.MEAT
import com.top10.deals.domain.model.ProductCategory.OTHER
import com.top10.deals.domain.model.ProductCategory.PAPER
import com.top10.deals.domain.model.ProductCategory.PASTA
import com.top10.deals.domain.model.ProductCategory.PERSONAL_CARE
import com.top10.deals.domain.model.ProductCategory.PET
import com.top10.deals.domain.model.ProductCategory.POULTRY
import com.top10.deals.domain.model.ProductCategory.SNACKS
import com.top10.deals.domain.model.ProductCategory.SOFT_DRINKS
import com.top10.deals.domain.model.ProductCategory.SPIRITS
import com.top10.deals.domain.model.ProductCategory.SWEETS
import com.top10.deals.domain.model.ProductCategory.VEGETABLES
import com.top10.deals.domain.model.ProductCategory.WATER
import com.top10.deals.domain.model.ProductCategory.WINE
import com.top10.deals.domain.model.WeeklyDeal
import org.jetbrains.compose.resources.painterResource

/**
 * A deal's picture: a photo of the product when it is an everyday one we have a photo of (see
 * [photo]), or else its category drawn as an emoji on a tile tinted for the kind of product. With a
 * [rank], a medal-coloured badge in the corner gives its place. The picture is decorative (the
 * deal's name says what it is), so only the rank is read out.
 */
@Composable
fun DealPicture(deal: WeeklyDeal, modifier: Modifier = Modifier, rank: Int? = null) {
    val photo = deal.product?.photo()
    Box(modifier.size(64.dp)) {
        if (photo != null) {
            Image(
                painter = painterResource(photo),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .clearAndSetSemantics {},
            )
        } else {
            CategoryTile(deal.category)
        }
        if (rank != null) RankBadge(rank, Modifier.align(Alignment.TopStart).offset(x = (-6).dp, y = (-6).dp))
    }
}

@Composable
private fun CategoryTile(category: ProductCategory) {
    val art = category.art()
    Box(
        modifier = Modifier
            .size(64.dp)
            // Translucent, so the same tint reads on the light and the dark theme.
            .background(art.tint.copy(alpha = 0.22f), RoundedCornerShape(16.dp))
            .clearAndSetSemantics {},
        contentAlignment = Alignment.Center,
    ) {
        Text(text = art.emoji, fontSize = 32.sp)
    }
}

@Composable
private fun RankBadge(rank: Int, modifier: Modifier) {
    val (background, content) = when (rank) {
        1 -> GOLD to Color.Black
        2 -> SILVER to Color.Black
        3 -> BRONZE to Color.Black
        else -> MaterialTheme.colorScheme.primary to MaterialTheme.colorScheme.onPrimary
    }
    Box(
        modifier = modifier
            .size(26.dp)
            .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            .background(background, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = rank.toString(),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = content,
        )
    }
}

private class CategoryArt(val emoji: String, val tint: Color)

private fun ProductCategory.art(): CategoryArt = when (this) {
    FRUIT -> CategoryArt("🍎", RED)
    VEGETABLES -> CategoryArt("🥦", GREEN)
    MEAT -> CategoryArt("🥩", RED)
    POULTRY -> CategoryArt("🍗", ORANGE)
    FISH -> CategoryArt("🐟", BLUE)
    EGGS -> CategoryArt("🥚", YELLOW)
    DAIRY -> CategoryArt("🥛", BLUE)
    CHEESE -> CategoryArt("🧀", YELLOW)
    BAKERY -> CategoryArt("🥖", ORANGE)
    SWEETS -> CategoryArt("🍩", PINK)
    PASTA -> CategoryArt("🍝", ORANGE)
    CANNED -> CategoryArt("🥫", RED)
    FROZEN -> CategoryArt("❄️", BLUE)
    ICE_CREAM -> CategoryArt("🍨", PINK)
    SNACKS -> CategoryArt("🍿", YELLOW)
    SOFT_DRINKS -> CategoryArt("🥤", RED)
    WATER -> CategoryArt("💧", BLUE)
    COFFEE_TEA -> CategoryArt("☕", BROWN)
    BEER -> CategoryArt("🍺", YELLOW)
    WINE -> CategoryArt("🍷", PURPLE)
    SPIRITS -> CategoryArt("🥃", BROWN)
    LAUNDRY -> CategoryArt("🧺", PURPLE)
    CLEANING -> CategoryArt("🧽", TEAL)
    PAPER -> CategoryArt("🧻", TEAL)
    PERSONAL_CARE -> CategoryArt("🧴", TEAL)
    BABY -> CategoryArt("🍼", PINK)
    PET -> CategoryArt("🐾", BROWN)
    OTHER -> CategoryArt("🛒", GREEN)
}

private val RED = Color(0xFFE5484D)
private val ORANGE = Color(0xFFF76B15)
private val YELLOW = Color(0xFFFFC53D)
private val GREEN = Color(0xFF30A46C)
private val TEAL = Color(0xFF12A594)
private val BLUE = Color(0xFF0090FF)
private val PURPLE = Color(0xFF8E4EC6)
private val PINK = Color(0xFFD6409F)
private val BROWN = Color(0xFFAD7F58)

private val GOLD = Color(0xFFF5C518)
private val SILVER = Color(0xFFC0C7CE)
private val BRONZE = Color(0xFFD08A4E)
