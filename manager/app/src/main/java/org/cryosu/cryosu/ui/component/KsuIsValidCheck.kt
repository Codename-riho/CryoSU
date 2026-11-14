package org.cryosu.cryosu.ui.component

import androidx.compose.runtime.Composable
import org.cryosu.cryosu.domain.model.KernelStatus

@Composable
inline fun KsuIsValid(
    status: KernelStatus,
    content: @Composable () -> Unit,
) {
    if (status.isFullFeatured) {
        content()
    }
}
