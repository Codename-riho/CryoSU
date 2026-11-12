package org.cryosu.cryosu.ui.screen.susfs.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Shield
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import org.cryosu.cryosu.ui.screen.LabelText

/**
 * Kartu utama SuSFS: perisai, versi + varian, dan sakelar aktif/nonaktif konfigurasi.
 * Menggantikan baris-baris datar gaya lama pada tab Status.
 */
@Composable
fun SuSFSHero(
    title: String,
    version: String,
    variant: String,
    summary: String,
    checked: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = MaterialTheme.colorScheme
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.linearGradient(
                    if (checked) {
                        listOf(colors.primaryContainer, colors.tertiaryContainer)
                    } else {
                        listOf(colors.surfaceContainerHigh, colors.surfaceContainerHighest)
                    },
                ),
            )
            .padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(if (checked) colors.primary else colors.outline),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = Icons.TwoTone.Shield,
                        contentDescription = null,
                        tint = if (checked) colors.onPrimary else colors.surface,
                        modifier = Modifier.size(28.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.onSurface,
                    )
                    Text(
                        text = summary,
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.onSurfaceVariant,
                    )
                }
                Switch(
                    checked = checked,
                    onCheckedChange = onCheckedChange,
                    enabled = enabled,
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                LabelText(label = version, containerColor = colors.secondary)
                LabelText(label = variant, containerColor = colors.tertiary)
            }
        }
    }
}

/** Fitur SuSFS aktif sebagai chip ringkas (mis. sus_path, sus_mount), bukan nama CONFIG panjang. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SuSFSFeatureChips(
    title: String,
    features: List<String>,
    emptyText: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(MaterialTheme.colorScheme.surfaceContainerHigh)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        if (features.isEmpty()) {
            Text(
                text = emptyText,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                features.forEach { feature ->
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(MaterialTheme.colorScheme.secondaryContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                    )
                }
            }
        }
    }
}

/** Ubah "CONFIG_KSU_SUSFS_SUS_PATH" menjadi "sus_path"; pisahkan per baris/spasi/koma. */
fun simplifySuSFSFeatures(raw: String): List<String> = raw
    .split(Regex("[\\s,]+"))
    .map { it.trim().removePrefix("CONFIG_KSU_SUSFS_").removePrefix("CONFIG_").lowercase() }
    .filter { it.isNotEmpty() }
    .distinct()
