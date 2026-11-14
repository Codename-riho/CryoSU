package org.cryosu.cryosu.ui.component.settings

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Cryo settings group: an icon-badge header above a single outlined card whose rows are
 * separated by hairline dividers. Reuses the [SegmentedColumnScope] item DSL (visible,
 * expandableItem) so existing settings rows work unchanged.
 */
@Composable
fun CryoSettingsGroup(
    title: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    content: SegmentedColumnScope.() -> Unit,
) {
    val items = SegmentedColumnScope().apply(content).items
    if (items.isEmpty()) return

    val colors = MaterialTheme.colorScheme
    val cardShape = RoundedCornerShape(24.dp)
    val firstVisible = items.indexOfFirst { it.visible }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
    ) {
        Row(
            modifier = Modifier.padding(start = 4.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(colors.tertiaryContainer),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = colors.onTertiaryContainer,
                    modifier = Modifier.size(18.dp),
                )
            }
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                color = colors.onSurface,
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(cardShape)
                .border(1.dp, colors.outlineVariant.copy(alpha = 0.6f), cardShape),
        ) {
            CompositionLocalProvider(LocalSegmentedItemShape provides RectangleShape) {
                items.forEachIndexed { index, data ->
                    key(data.key ?: index) {
                        AnimatedVisibility(
                            visible = data.visible,
                            enter = expandVertically(),
                            exit = shrinkVertically(),
                        ) {
                            Column {
                                if (index != firstVisible) {
                                    HorizontalDivider(
                                        modifier = Modifier.padding(start = 56.dp),
                                        thickness = 0.5.dp,
                                        color = colors.outlineVariant.copy(alpha = 0.5f),
                                    )
                                }
                                data.content(RectangleShape)
                            }
                        }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(2.dp))
    }
}
