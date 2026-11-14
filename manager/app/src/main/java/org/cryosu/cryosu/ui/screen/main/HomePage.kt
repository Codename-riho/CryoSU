package org.cryosu.cryosu.ui.screen.main

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.system.Os
import android.widget.Toast
import androidx.activity.compose.ReportDrawnWhen
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.Android
import androidx.compose.material.icons.twotone.Block
import androidx.compose.material.icons.twotone.DeveloperBoard
import androidx.compose.material.icons.twotone.Error
import androidx.compose.material.icons.twotone.Extension
import androidx.compose.material.icons.twotone.FilterList
import androidx.compose.material.icons.twotone.Group
import androidx.compose.material.icons.twotone.Info
import androidx.compose.material.icons.twotone.Memory
import androidx.compose.material.icons.twotone.PowerSettingsNew
import androidx.compose.material.icons.twotone.Security
import androidx.compose.material.icons.twotone.Settings
import androidx.compose.material.icons.twotone.Smartphone
import androidx.compose.material.icons.twotone.Tag
import androidx.compose.material.icons.twotone.Tune
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuGroup
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.DropdownMenuPopup
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.cryosu.cryosu.BuildConfig
import org.cryosu.cryosu.Natives.KernelPatchImplementation
import org.cryosu.cryosu.R
import org.cryosu.cryosu.domain.model.HomeSystemInfo
import org.cryosu.cryosu.domain.model.KernelStatus
import org.cryosu.cryosu.domain.model.ManagerUpdateChannel
import org.cryosu.cryosu.domain.model.ManagerUpdateInfo
import org.cryosu.cryosu.domain.usecase.EnqueueManagerUpdateUseCase
import org.cryosu.cryosu.magica.MagicaService
import org.cryosu.cryosu.ui.component.KsuIsValid
import org.cryosu.cryosu.ui.component.SwipeableSnackbarHost
import org.cryosu.cryosu.ui.component.WarningCard
import org.cryosu.cryosu.ui.component.rememberConfirmDialog
import org.cryosu.cryosu.ui.component.rememberLoadingDialog
import org.cryosu.cryosu.ui.component.settings.SettingsBaseWidget
import org.cryosu.cryosu.ui.navigation.LocalNavigator
import org.cryosu.cryosu.ui.navigation.Route
import org.cryosu.cryosu.ui.theme.CardConfig
import org.cryosu.cryosu.ui.theme.ThemeConfig
import org.cryosu.cryosu.ui.theme.blurEffect
import org.cryosu.cryosu.ui.theme.blurSource
import org.cryosu.cryosu.ui.util.LocalHandlePageChange
import org.cryosu.cryosu.ui.util.LocalPagerState
import org.cryosu.cryosu.ui.util.LocalPermissionRequestInterface
import org.cryosu.cryosu.ui.util.LocalSnackbarHost
import org.cryosu.cryosu.ui.util.adaptiveScaffoldWindowInsets
import org.cryosu.cryosu.ui.util.downloader.downloadManagerUpdate
import org.cryosu.cryosu.ui.viewmodel.HomeUiAction
import org.cryosu.cryosu.ui.viewmodel.HomeUiEvent
import org.cryosu.cryosu.ui.viewmodel.HomeUiState
import org.cryosu.cryosu.ui.viewmodel.HomeViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * @author ShirkNeko
 * @date 2025/9/29.
 */

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomePage(
    bottomPadding: Dp,
) {
    val context = LocalContext.current
    val viewModel = koinViewModel<HomeViewModel>()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.dispatch(HomeUiAction.AwaitInitialData)
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is HomeUiEvent.Error -> if (event.message.isNotBlank()) {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    ReportDrawnWhen { uiState.isInitialDataLoaded }

    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(
        rememberTopAppBarState(
            initialHeightOffset = -154f,
            initialHeightOffsetLimit = -154f, // from debugger
        ),
    )
    val scrollState = rememberScrollState()
    val navigator = LocalNavigator.current
    val loadingDialog = rememberLoadingDialog()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopBar(
                uiState = uiState,
                onReboot = { viewModel.dispatch(HomeUiAction.Reboot(it)) },
                scrollBehavior = scrollBehavior,
            )
        },
        containerColor = Color.Transparent,
        contentColor = MaterialTheme.colorScheme.onSurface,
        contentWindowInsets = adaptiveScaffoldWindowInsets(includeBottom = false),
        snackbarHost = {
            SwipeableSnackbarHost(
                modifier = Modifier.padding(bottom = bottomPadding),
                hostState = LocalSnackbarHost.current,
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .blurSource()
                .nestedScroll(scrollBehavior.nestedScrollConnection)
                .verticalScroll(scrollState)
                .padding(
                    top = innerPadding.calculateTopPadding() + 2.dp,
                    start = 16.dp,
                    end = 16.dp,
                ),
            verticalArrangement = Arrangement.spacedBy(0.dp),
        ) {
            // 状态卡片
            if (uiState.isCoreDataLoaded) {
                if (uiState.systemStatus.isManager && !uiState.systemStatus.isFullFeatured) {
                    if ((
                            uiState.systemStatus.kernelUAPIVersion
                                ?: 1
                            ) > uiState.systemStatus.managerUAPIVersion
                    ) {
                        WarningCard(
                            message = stringResource(R.string.require_manager_version),
                            icon = {
                                Icon(
                                    imageVector = Icons.TwoTone.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(18.dp),
                                )
                            },
                            onClick = {
                                if (uiState.systemStatus.isLateLoadMode) return@WarningCard
                                navigator.push(Route.Install(preselectedKernelUri = null))
                            },
                        )
                    } else {
                        WarningCard(
                            message = if (uiState.systemStatus.lkmMode == true) {
                                stringResource(R.string.require_kernel_version)
                            } else {
                                stringResource(R.string.require_kernel_version_gki)
                            },
                            icon = {
                                Icon(
                                    imageVector = Icons.TwoTone.Error,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onErrorContainer,
                                    modifier = Modifier.size(18.dp),
                                )
                            },
                            onClick = {
                                if (uiState.systemStatus.isLateLoadMode) return@WarningCard
                                navigator.push(Route.Install(preselectedKernelUri = null))
                            },
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // 警告信息
                if (BuildConfig.DEBUG) {
                    WarningCard(
                        message = stringResource(R.string.debug_version_notice),
                        icon = {
                            Icon(
                                imageVector = Icons.TwoTone.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                val noticeContext = LocalContext.current
                val noticePrefs = remember {
                    noticeContext.getSharedPreferences("cryosu_ui", android.content.Context.MODE_PRIVATE)
                }
                var unofficialNoticeDismissed by remember {
                    mutableStateOf(noticePrefs.getBoolean("dismiss_unofficial_notice", false))
                }
                if (!uiState.systemStatus.isOfficialSignature && !unofficialNoticeDismissed) {
                    // Peringatan informatif (tertiary) yang bisa ditutup, bukan alarm merah.
                    WarningCard(
                        message = stringResource(
                            R.string.unofficial_version_notice,
                            stringResource(R.string.app_name),
                        ),
                        color = MaterialTheme.colorScheme.tertiaryContainer,
                        icon = {
                            Icon(
                                imageVector = Icons.TwoTone.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onTertiaryContainer,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                        onClose = {
                            unofficialNoticeDismissed = true
                            noticePrefs.edit().putBoolean("dismiss_unofficial_notice", true).apply()
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (BuildConfig.IS_PR_BUILD || uiState.systemStatus.isPrBuild) {
                    WarningCard(
                        message = stringResource(
                            id = R.string.home_pr_build_warning,
                        ),
                        icon = {
                            Icon(
                                imageVector = Icons.TwoTone.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (uiState.systemStatus.kernelPatchImplementation == KernelPatchImplementation.OFFICIAL) {
                    WarningCard(
                        message = stringResource(
                            R.string.conflict_with_apatch,
                        ),
                        icon = {
                            Icon(
                                imageVector = Icons.TwoTone.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (uiState.systemStatus.ksuVersion != null && !uiState.systemStatus.isRootAvailable) {
                    WarningCard(
                        message = stringResource(id = R.string.grant_root_failed),
                        icon = {
                            Icon(
                                imageVector = Icons.TwoTone.Error,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier.size(18.dp),
                            )
                        },
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                StatusCard(
                    uiState = uiState,
                    onClickInstall = {
                        if (uiState.systemStatus.isLateLoadMode) return@StatusCard
                        navigator.push(Route.Install(preselectedKernelUri = null))
                    },
                    onClickJailbreak = {
                        loadingDialog.showLoading()
                        context.startService(Intent(context, MagicaService::class.java))
                        // Manager will be force-stopped and restarted by late-load on success.
                        // If that doesn't happen within timeout, jailbreak likely failed.
                        scope.launch(Dispatchers.IO) {
                            delay(30_000.milliseconds)
                            withContext(Dispatchers.Main) {
                                loadingDialog.hide()
                                Toast.makeText(
                                    context,
                                    R.string.jailbreak_timeout,
                                    Toast.LENGTH_LONG,
                                ).show()
                            }
                        }
                    },
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            ManagerUpdateCard(uiState.stableManagerUpdate)
            ManagerUpdateCard(uiState.betaManagerUpdate)
            if (uiState.isBetaManagerUpdateCheckFailed) {
                WarningCard(
                    message = stringResource(R.string.beta_update_check_failed),
                    icon = {
                        Icon(
                            imageVector = Icons.TwoTone.Error,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                        )
                    },
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            if (uiState.isExtendedDataLoaded) {
                InfoCard(
                    systemStatus = uiState.systemStatus,
                    systemInfo = uiState.systemInfo,
                    isSimpleMode = uiState.isSimpleMode,
                    showHomeCardIcons = uiState.showHomeCardIcons,
                )
            }

            Spacer(Modifier.height(bottomPadding))
        }
    }
}

@Composable
private fun ManagerUpdateCard(update: ManagerUpdateInfo?) {
    val visibilityState = remember { MutableTransitionState(false) }
    var displayedUpdate by remember { mutableStateOf<ManagerUpdateInfo?>(null) }

    LaunchedEffect(update) {
        if (update != null) displayedUpdate = update
        visibilityState.targetState = update != null
    }

    AnimatedVisibility(
        visibleState = visibilityState,
        enter = fadeIn() + expandVertically(),
        exit = fadeOut() + shrinkVertically(),
    ) {
        displayedUpdate?.let { updateInfo ->
            ManagerUpdateCardContent(updateInfo)
        }
    }
}

@Composable
private fun ManagerUpdateCardContent(updateInfo: ManagerUpdateInfo) {
    val context = LocalContext.current
    val permissionRequestInterface = LocalPermissionRequestInterface.current
    val enqueueManagerUpdate = koinInject<EnqueueManagerUpdateUseCase>()
    val channelTitle = stringResource(
        if (updateInfo.channel == ManagerUpdateChannel.STABLE) {
            R.string.manager_update_stable
        } else {
            R.string.manager_update_beta
        },
    )
    val message = if (updateInfo.channel == ManagerUpdateChannel.STABLE) {
        stringResource(R.string.new_version_available, updateInfo.versionCode)
    } else {
        stringResource(R.string.beta_version_available, updateInfo.versionCode)
    }
    val updateText = stringResource(R.string.module_update)
    val details = stringResource(
        R.string.manager_update_details,
        updateInfo.versionName,
        updateInfo.versionCode,
        updateInfo.abi,
    )
    val dialogContent = if (updateInfo.changelog.isBlank()) {
        details
    } else {
        "$details\n\n${updateInfo.changelog}"
    }
    val updateDialog = rememberConfirmDialog(
        onConfirm = {
            downloadManagerUpdate(
                context,
                permissionRequestInterface,
                updateInfo,
                enqueueManagerUpdate,
            )
        },
    )

    WarningCard(
        message = message,
        color = MaterialTheme.colorScheme.outlineVariant,
        icon = {
            Icon(
                imageVector = Icons.TwoTone.Info,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
            )
        },
        onClick = {
            updateDialog.showConfirm(
                title = channelTitle,
                content = dialogContent,
                markdown = updateInfo.changelog.isNotBlank(),
                confirm = updateText,
            )
        },
    )

    Spacer(modifier = Modifier.height(10.dp))
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RebootDropdownItems(
    items: Map<Int, String>,
    onReboot: (String) -> Unit,
) {
    items.onEachIndexed { index, (id, reason) ->
        DropdownMenuItem(
            shape = MenuDefaults.itemShape(index, items.size).shape,
            text = { Text(stringResource(id)) },
            onClick = { onReboot(reason) },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun TopBar(
    uiState: HomeUiState,
    onReboot: (String) -> Unit,
    scrollBehavior: TopAppBarScrollBehavior? = null,
) {
    val themeConfig: ThemeConfig = koinInject()
    val cardConfig: CardConfig = koinInject()
    val navigator = LocalNavigator.current

    LargeFlexibleTopAppBar(
        modifier = Modifier.blurEffect(),
        title = {
            Text(
                text = stringResource(R.string.app_name),
            )
        },
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
        actions = {
            if (uiState.isCoreDataLoaded) {
                // SuSFS 配置按钮
                if (uiState.systemInfo.susfsVersionSupported) {
                    IconButton(onClick = {
                        navigator.push(Route.SuSFSConfig)
                    }) {
                        Icon(
                            imageVector = Icons.TwoTone.Tune,
                            contentDescription = stringResource(R.string.susfs_config_setting_title),
                        )
                    }
                }

                // 重启按钮
                var showDropdown by remember { mutableStateOf(false) }
                KsuIsValid(uiState.systemStatus) {
                    if (uiState.systemStatus.isRootAvailable) {
                        IconButton(onClick = {
                            showDropdown = true
                        }) {
                            Icon(
                                imageVector = Icons.TwoTone.PowerSettingsNew,
                                contentDescription = stringResource(id = R.string.reboot),
                            )

                            DropdownMenuPopup(expanded = showDropdown, onDismissRequest = {
                                showDropdown = false
                            }) {
                                DropdownMenuGroup(
                                    shapes = MenuDefaults.groupShapes(),
                                ) {
                                    val pm =
                                        LocalContext.current.getSystemService(Context.POWER_SERVICE) as PowerManager?
                                    var methods = mapOf(
                                        R.string.reboot to "",
                                        R.string.reboot_soft to "soft_reboot",
                                        R.string.reboot_recovery to "recovery",
                                        R.string.reboot_bootloader to "bootloader",
                                        R.string.reboot_download to "download",
                                        R.string.reboot_edl to "edl",
                                    )

                                    @Suppress("DEPRECATION")
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && pm?.isRebootingUserspaceSupported == true) {
                                        methods = methods + (R.string.reboot_userspace to "userspace")
                                    }

                                    RebootDropdownItems(methods, onReboot)
                                }
                            }
                        }
                    }
                }
            }
        },
        windowInsets = TopAppBarDefaults.windowInsets.add(WindowInsets(left = 12.dp)),
        scrollBehavior = scrollBehavior,
    )
}

@Composable
private fun StatusCard(
    uiState: HomeUiState,
    onClickInstall: () -> Unit = {},
    onClickJailbreak: () -> Unit = {},
) {
    val systemStatus = uiState.systemStatus
    val onClick = { _: Offset ->
        if (systemStatus.isRootAvailable || systemStatus.kernelVersion.isGKI()) {
            onClickInstall()
        }
    }

    when {
        systemStatus.ksuVersion != null -> {
            val workingModeText = when {
                systemStatus.isSafeMode -> stringResource(id = R.string.safe_mode)
                else -> stringResource(id = R.string.home_working)
            }

            val workingModeSurfaceText = when {
                systemStatus.lkmMode == true -> "LKM"
                else -> "Built-in"
            }

            val chips = buildList {
                add(workingModeSurfaceText)
                if (systemStatus.isLateLoadMode) add(stringResource(id = R.string.jailbreak_mode))
                if (Os.uname().machine != "aarch64") add(Os.uname().machine)
            }

            val pagerState = LocalPagerState.current
            val handlePageChange = LocalHandlePageChange.current
            val toastContext = LocalContext.current
            // Halaman: 0 Beranda, 1 Superuser, 2 Modul (hanya ada bila manajer full-featured).
            val openPage: (Int) -> Unit = { page ->
                if (pagerState.pageCount > page + 1) {
                    handlePageChange(page)
                } else {
                    Toast.makeText(toastContext, R.string.grant_root_failed, Toast.LENGTH_SHORT).show()
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                CryoStatusHero(
                    title = workingModeText,
                    subtitle = "CryoSU",
                    chips = chips,
                    onClick = { onClick(Offset.Zero) },
                )
                CryoStatTiles(
                    leftLabel = stringResource(R.string.superuser),
                    leftValue = uiState.systemInfo.superuserCount,
                    rightLabel = stringResource(R.string.module),
                    rightValue = uiState.systemInfo.moduleCount,
                    onLeftClick = { openPage(1) },
                    onRightClick = { openPage(2) },
                )
            }
        }

        systemStatus.kernelVersion.isGKI() -> {
            SettingsBaseWidget(
                icon = Icons.TwoTone.Warning,
                iconSize = 18.dp,
                isError = true,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                title = stringResource(R.string.home_not_installed),
                description = stringResource(R.string.home_click_to_install),
                onClick = onClick,
                trailingContent = if (systemStatus.isSELinuxPermissive) {
                    {
                        Button(
                            onClick = onClickJailbreak,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.error,
                                contentColor = MaterialTheme.colorScheme.onError,
                            ),

                        ) {
                            Text(stringResource(R.string.home_jailbreak))
                        }
                    }
                } else {
                    null
                },
            )
        }

        else -> {
            SettingsBaseWidget(
                icon = Icons.TwoTone.Block,
                iconSize = 18.dp,
                containerColor = MaterialTheme.colorScheme.errorContainer,
                isError = true,
                title = stringResource(R.string.home_unsupported),
                description = stringResource(R.string.home_unsupported_reason),
            )
        }
    }
}

@Composable
private fun InfoCard(
    systemStatus: KernelStatus,
    systemInfo: HomeSystemInfo,
    isSimpleMode: Boolean,
    showHomeCardIcons: Boolean,
) {
    val managersList = systemInfo.managersList

    CryoInfoSection(title = stringResource(R.string.home_version_info)) {
        CryoInfoRow(
            icon = Icons.TwoTone.Smartphone.takeIf { showHomeCardIcons },
            title = stringResource(R.string.home_device_model),
            value = systemInfo.deviceModel,
        )
        CryoInfoRow(
            icon = Icons.TwoTone.DeveloperBoard.takeIf { showHomeCardIcons },
            title = stringResource(R.string.home_kernel),
            value = systemInfo.kernelRelease,
        )
        if (!isSimpleMode) {
            CryoInfoRow(
                icon = Icons.TwoTone.Android.takeIf { showHomeCardIcons },
                title = stringResource(R.string.home_android_version),
                value = systemInfo.androidVersion,
            )
        }
        if (systemStatus.isManager) {
            CryoInfoRow(
                icon = Icons.TwoTone.Memory.takeIf { showHomeCardIcons },
                title = stringResource(R.string.home_kernel_version),
                value = systemStatus.ksuFullVersion.orEmpty(),
            )
        }
        CryoInfoRow(
            icon = Icons.TwoTone.Tag.takeIf { showHomeCardIcons },
            title = stringResource(R.string.home_manager_version),
            value = "${systemInfo.managerVersion.first} (${systemInfo.managerVersion.second}/${systemInfo.managerVersion.third})",
        )
        if (!isSimpleMode && systemInfo.susfsEnabled && systemInfo.susfsVersion.isNotEmpty()) {
            CryoInfoRow(
                icon = Icons.TwoTone.Settings.takeIf { showHomeCardIcons },
                title = stringResource(R.string.home_susfs_version),
                value = systemInfo.susfsVersion,
            )
        }
    }

    CryoInfoSection(title = stringResource(R.string.home_status_info)) {
        CryoInfoRow(
            icon = Icons.TwoTone.Security.takeIf { showHomeCardIcons },
            title = stringResource(R.string.home_selinux_status),
            value = systemInfo.selinuxStatus,
        )

        val seccompDisplay = when (systemInfo.seccompStatus) {
            -1 -> stringResource(R.string.seccomp_status_not_supported)
            0 -> stringResource(R.string.seccomp_status_disabled)
            1 -> stringResource(R.string.seccomp_status_strict)
            2 -> stringResource(R.string.seccomp_status_filter)
            else -> stringResource(R.string.seccomp_status_unknown)
        }
        CryoInfoRow(
            icon = Icons.TwoTone.FilterList.takeIf { showHomeCardIcons },
            title = stringResource(R.string.home_seccomp_status),
            value = seccompDisplay,
        )

        if (!isSimpleMode && managersList != null) {
            val signatureMap = managersList.managers.groupBy { it.signatureIndex }
            val appName = stringResource(R.string.app_name)
            val dynamicName = stringResource(R.string.dynamic_managerature)
            val unknownName = stringResource(R.string.unknown_signature)
            val signatureLabels = mutableMapOf<Int, String>()
            for (index in signatureMap.keys) {
                if (index >= 1 && index != 255) {
                    signatureLabels[index] = stringResource(R.string.signature_index, index)
                }
            }
            val managersText = signatureMap.toSortedMap().entries.joinToString(" | ") { (signatureIndex, managers) ->
                val label = when {
                    signatureIndex == 0 -> appName
                    signatureIndex == 255 -> dynamicName
                    signatureIndex >= 1 -> signatureLabels.getValue(signatureIndex)
                    else -> unknownName
                }
                managers.joinToString(", ") { "UID: ${it.uid}" } + " ($label)"
            }
            CryoInfoRow(
                icon = Icons.TwoTone.Group.takeIf { showHomeCardIcons },
                title = stringResource(R.string.multi_manager_list),
                value = managersText.ifEmpty { stringResource(R.string.no_active_manager) },
            )
        }

        if (!isSimpleMode && systemStatus.isFullFeatured) {
            CryoInfoRow(
                icon = Icons.TwoTone.Tune.takeIf { showHomeCardIcons },
                title = stringResource(R.string.home_hook_type),
                value = systemStatus.hookType,
            )
        }
        if (!isSimpleMode && systemInfo.zygiskImplement.isNotEmpty() && systemInfo.zygiskImplement != "None") {
            CryoInfoRow(
                icon = Icons.TwoTone.Extension.takeIf { showHomeCardIcons },
                title = stringResource(R.string.home_zygisk_implement),
                value = systemInfo.zygiskImplement,
            )
        }
        if (!isSimpleMode && systemInfo.metaModuleImplement.isNotEmpty() && systemInfo.metaModuleImplement != "None") {
            CryoInfoRow(
                icon = Icons.TwoTone.Extension.takeIf { showHomeCardIcons },
                title = stringResource(R.string.home_meta_module_implement),
                value = systemInfo.metaModuleImplement,
            )
        }
    }
}
