package org.cryosu.cryosu.ui.screen.about

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.MenuBook
import androidx.compose.material.icons.twotone.Code
import androidx.compose.material.icons.twotone.EmojiEvents
import androidx.compose.material.icons.twotone.Favorite
import androidx.compose.material.icons.twotone.Group
import androidx.compose.material.icons.twotone.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import org.cryosu.cryosu.R
import org.cryosu.cryosu.ui.component.settings.AppBackButton
import org.cryosu.cryosu.ui.component.settings.CryoSettingsGroup
import org.cryosu.cryosu.ui.component.settings.SettingsJumpPageWidget
import org.cryosu.cryosu.ui.navigation.LocalNavigator
import org.cryosu.cryosu.ui.screen.main.CryoPageBanner
import org.cryosu.cryosu.ui.theme.CardConfig
import org.cryosu.cryosu.ui.theme.ThemeConfig
import org.cryosu.cryosu.ui.theme.blurEffect
import org.cryosu.cryosu.ui.theme.blurSource
import org.cryosu.cryosu.ui.util.adaptiveScaffoldWindowInsets
import org.koin.compose.koinInject

private const val BAKASU_GITHUB = "https://github.com/Baka-SU/BakaSU"
private const val BAKASU_TELEGRAM = "https://t.me/BakaSU"
private const val KERNELSU_GITHUB = "https://github.com/tiann/KernelSU"
private const val KERNELSU_LEARN = "https://kernelsu.org/guide/what-is-kernelsu.html"

/**
 * Honor Hall: every credit and link that points to the projects CryoSU descends from
 * (BakaSU, KernelSU) lives here instead of being scattered across the app.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HonorHallScreen() {
    val themeConfig: ThemeConfig = koinInject()
    val cardConfig: CardConfig = koinInject()
    val navigator = LocalNavigator.current
    val uriHandler = LocalUriHandler.current
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

    Scaffold(
        contentWindowInsets = adaptiveScaffoldWindowInsets(),
        topBar = {
            LargeFlexibleTopAppBar(
                modifier = Modifier.blurEffect(),
                windowInsets = TopAppBarDefaults.windowInsets.add(WindowInsets(left = 12.dp)),
                title = { Text(text = stringResource(id = R.string.honor_hall)) },
                scrollBehavior = scrollBehavior,
                navigationIcon = { AppBackButton(onClick = { navigator.pop() }) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor =
                        if (themeConfig.isEnableBlur) {
                            Color.Transparent
                        } else {
                            MaterialTheme.colorScheme.surfaceContainer.copy(cardConfig.cardAlpha)
                        },
                    scrolledContainerColor =
                        if (themeConfig.isEnableBlur) {
                            Color.Transparent
                        } else {
                            MaterialTheme.colorScheme.surfaceContainer.copy(cardConfig.cardAlpha)
                        },
                ),
            )
        },
        contentColor = MaterialTheme.colorScheme.onSurface,
        containerColor = Color.Transparent,
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .blurSource(),
        ) {
            item { Spacer(modifier = Modifier.height(innerPadding.calculateTopPadding())) }

            item {
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    CryoPageBanner(
                        icon = Icons.TwoTone.EmojiEvents,
                        title = stringResource(R.string.honor_hall),
                        subtitle = stringResource(R.string.honor_hall_intro),
                    )
                }
            }

            item {
                CryoSettingsGroup(
                    title = stringResource(R.string.honor_bakasu_title),
                    icon = Icons.TwoTone.Favorite,
                ) {
                    item {
                        SettingsJumpPageWidget(
                            icon = Icons.TwoTone.Code,
                            title = stringResource(R.string.honor_bakasu_github),
                            description = stringResource(R.string.honor_bakasu_desc),
                            onClick = { uriHandler.openUri(BAKASU_GITHUB) },
                        )
                    }
                    item {
                        SettingsJumpPageWidget(
                            icon = Icons.TwoTone.Group,
                            title = stringResource(R.string.honor_bakasu_telegram),
                            onClick = { uriHandler.openUri(BAKASU_TELEGRAM) },
                        )
                    }
                }
            }

            item {
                CryoSettingsGroup(
                    title = stringResource(R.string.honor_kernelsu_title),
                    icon = Icons.TwoTone.Favorite,
                ) {
                    item {
                        SettingsJumpPageWidget(
                            icon = Icons.TwoTone.Code,
                            title = stringResource(R.string.honor_kernelsu_github),
                            description = stringResource(R.string.honor_kernelsu_desc),
                            onClick = { uriHandler.openUri(KERNELSU_GITHUB) },
                        )
                    }
                    item {
                        SettingsJumpPageWidget(
                            icon = Icons.AutoMirrored.TwoTone.MenuBook,
                            title = stringResource(R.string.honor_kernelsu_learn),
                            onClick = { uriHandler.openUri(KERNELSU_LEARN) },
                        )
                    }
                }
            }

            item {
                CryoSettingsGroup(
                    title = stringResource(R.string.honor_shirkneko_title),
                    icon = Icons.TwoTone.Person,
                ) {
                    item {
                        SettingsJumpPageWidget(
                            icon = Icons.TwoTone.Person,
                            title = stringResource(R.string.honor_shirkneko_title),
                            description = stringResource(R.string.honor_shirkneko_desc),
                        )
                    }
                }
            }

            item {
                Column(modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)) {
                    Text(
                        text = stringResource(R.string.honor_license_note),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(innerPadding.calculateBottomPadding() + 16.dp)) }
        }
    }
}
