package org.cryosu.cryosu.di

import coil.ImageLoader
import java.io.File
import java.util.Locale
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import me.zhanghai.android.appiconloader.coil.AppIconFetcher
import me.zhanghai.android.appiconloader.coil.AppIconKeyer
import okhttp3.Cache
import okhttp3.OkHttpClient
import org.cryosu.cryosu.BuildConfig
import org.cryosu.cryosu.data.AppSettingsRepository
import org.cryosu.cryosu.data.application.ApplicationControlRepository
import org.cryosu.cryosu.data.application.DynamicManagerRepository
import org.cryosu.cryosu.data.count.CountRepository
import org.cryosu.cryosu.data.download.DownloadRepository
import org.cryosu.cryosu.data.file.ModuleFileRepository
import org.cryosu.cryosu.data.flash.FlashRepository
import org.cryosu.cryosu.data.kernel.KernelRepository
import org.cryosu.cryosu.data.kernel.UmountRepository
import org.cryosu.cryosu.data.logging.BugreportRepository
import org.cryosu.cryosu.data.logging.SulogRepository
import org.cryosu.cryosu.data.module.ModuleActionRepository
import org.cryosu.cryosu.data.module.ModuleCatalogRepository
import org.cryosu.cryosu.data.module.ModuleIconRepository
import org.cryosu.cryosu.data.module.ModulePreferencesRepository
import org.cryosu.cryosu.data.module.ModuleReleaseRepository
import org.cryosu.cryosu.data.module.ModuleRepository
import org.cryosu.cryosu.data.module.RepositorySourceStore
import org.cryosu.cryosu.data.module.registry.KernelSuRegistryRepository
import org.cryosu.cryosu.data.network.NetworkRequestRepository
import org.cryosu.cryosu.data.network.NetworkStatusRepository
import org.cryosu.cryosu.data.network.WebResourceRepository
import org.cryosu.cryosu.data.packageinfo.AppIconDataSource
import org.cryosu.cryosu.data.packageinfo.InstalledPackageCache
import org.cryosu.cryosu.data.packageinfo.InstalledPackageRepository
import org.cryosu.cryosu.data.packageinfo.RootServiceRepository
import org.cryosu.cryosu.data.packageinfo.SuperUserRepository
import org.cryosu.cryosu.data.profile.ProfileRepository
import org.cryosu.cryosu.data.profile.ProfileTemplateRepository
import org.cryosu.cryosu.data.script.ScriptRepository
import org.cryosu.cryosu.data.settings.LocaleHelper
import org.cryosu.cryosu.data.settings.LocaleRepository
import org.cryosu.cryosu.data.settings.SettingsPlatformRepository
import org.cryosu.cryosu.data.shell.KsuCliRepository
import org.cryosu.cryosu.data.shell.ShortcutRepository
import org.cryosu.cryosu.data.startup.ApplicationInitializationRepository
import org.cryosu.cryosu.data.startup.StartupRepository
import org.cryosu.cryosu.data.susfs.SuSFSConfigHelper
import org.cryosu.cryosu.data.susfs.SuSFSRepository
import org.cryosu.cryosu.data.system.HomeRuntimeRepository
import org.cryosu.cryosu.data.system.HomeStateRepository
import org.cryosu.cryosu.data.text.HanziToPinyin
import org.cryosu.cryosu.data.theme.MonetCompatColorSource
import org.cryosu.cryosu.data.theme.ThemeRepository
import org.cryosu.cryosu.data.update.ManagerUpdateRepository
import org.cryosu.cryosu.data.webui.WebUiRepository
import org.cryosu.cryosu.domain.text.TextTransliterator
import org.cryosu.cryosu.domain.usecase.AddModuleRepositoryUseCase
import org.cryosu.cryosu.domain.usecase.AddUmountPathUseCase
import org.cryosu.cryosu.domain.usecase.ApplyLanguageUseCase
import org.cryosu.cryosu.domain.usecase.BackupAllowlistUseCase
import org.cryosu.cryosu.domain.usecase.CalculateInstalledModuleSizeUseCase
import org.cryosu.cryosu.domain.usecase.CheckFlashModuleMountUseCase
import org.cryosu.cryosu.domain.usecase.CheckManagerUpdateUseCase
import org.cryosu.cryosu.domain.usecase.CleanSulogUseCase
import org.cryosu.cryosu.domain.usecase.ClearDynamicManagerUseCase
import org.cryosu.cryosu.domain.usecase.ConfigureSuLogUseCase
import org.cryosu.cryosu.domain.usecase.ControlAppUseCase
import org.cryosu.cryosu.domain.usecase.DeleteProfileTemplateUseCase
import org.cryosu.cryosu.domain.usecase.EnableSulogUseCase
import org.cryosu.cryosu.domain.usecase.EnqueueDownloadUseCase
import org.cryosu.cryosu.domain.usecase.EnqueueManagerUpdateUseCase
import org.cryosu.cryosu.domain.usecase.EnsureManagerInstalledUseCase
import org.cryosu.cryosu.domain.usecase.ExecuteFlashOperationUseCase
import org.cryosu.cryosu.domain.usecase.ExecuteModuleActionUseCase
import org.cryosu.cryosu.domain.usecase.ExportProfileTemplatesUseCase
import org.cryosu.cryosu.domain.usecase.ExtractModuleIdUseCase
import org.cryosu.cryosu.domain.usecase.ExtractModuleNameUseCase
import org.cryosu.cryosu.domain.usecase.FetchRemoteTextUseCase
import org.cryosu.cryosu.domain.usecase.GenerateBugreportUseCase
import org.cryosu.cryosu.domain.usecase.GetAppProfileUseCase
import org.cryosu.cryosu.domain.usecase.GetAppSepolicyUseCase
import org.cryosu.cryosu.domain.usecase.GetBooleanPreferenceUseCase
import org.cryosu.cryosu.domain.usecase.GetCatalogModuleUseCase
import org.cryosu.cryosu.domain.usecase.GetDefaultUmountModulesUseCase
import org.cryosu.cryosu.domain.usecase.GetHomeBasicInfoUseCase
import org.cryosu.cryosu.domain.usecase.GetInstallEnvironmentUseCase
import org.cryosu.cryosu.domain.usecase.GetKernelFeatureSettingsUseCase
import org.cryosu.cryosu.domain.usecase.GetKernelStatusUseCase
import org.cryosu.cryosu.domain.usecase.GetLastScriptRunUseCase
import org.cryosu.cryosu.domain.usecase.GetManagerRuntimeInfoUseCase
import org.cryosu.cryosu.domain.usecase.GetModuleReadmeUseCase
import org.cryosu.cryosu.domain.usecase.GetModuleReleaseDetailsUseCase
import org.cryosu.cryosu.domain.usecase.GetModuleUpdateChangelogUseCase
import org.cryosu.cryosu.domain.usecase.GetPlatformFeatureStatusUseCase
import org.cryosu.cryosu.domain.usecase.GetProfileTemplateUseCase
import org.cryosu.cryosu.domain.usecase.GetStringPreferenceUseCase
import org.cryosu.cryosu.domain.usecase.GetStringSetPreferenceUseCase
import org.cryosu.cryosu.domain.usecase.GetSuSFSStatusUseCase
import org.cryosu.cryosu.domain.usecase.GetSuperUserAppGroupUseCase
import org.cryosu.cryosu.domain.usecase.ImportAllowlistUseCase
import org.cryosu.cryosu.domain.usecase.ImportProfileTemplatesUseCase
import org.cryosu.cryosu.domain.usecase.InitializeApplicationUseCase
import org.cryosu.cryosu.domain.usecase.IsCatalogModuleCurrentUseCase
import org.cryosu.cryosu.domain.usecase.IsLateLoadModeUseCase
import org.cryosu.cryosu.domain.usecase.IsModuleUriAccessibleUseCase
import org.cryosu.cryosu.domain.usecase.IsNetworkAvailableUseCase
import org.cryosu.cryosu.domain.usecase.IsSoftRebootPreferredUseCase
import org.cryosu.cryosu.domain.usecase.IsSystemLanguageSettingsUseCase
import org.cryosu.cryosu.domain.usecase.LaunchSystemLanguageSettingsUseCase
import org.cryosu.cryosu.domain.usecase.LoadModuleIconUseCase
import org.cryosu.cryosu.domain.usecase.LoadSettingsPlatformUseCase
import org.cryosu.cryosu.domain.usecase.ObserveAssetDownloadUseCase
import org.cryosu.cryosu.domain.usecase.ObserveCatalogModulesUseCase
import org.cryosu.cryosu.domain.usecase.ObserveDownloadUseCase
import org.cryosu.cryosu.domain.usecase.ObserveDynamicManagerStateUseCase
import org.cryosu.cryosu.domain.usecase.ObserveInstalledModulesUseCase
import org.cryosu.cryosu.domain.usecase.ObserveKernelFlashUseCase
import org.cryosu.cryosu.domain.usecase.ObserveModuleCatalogRefreshingUseCase
import org.cryosu.cryosu.domain.usecase.ObserveProfileTemplateOfflineUseCase
import org.cryosu.cryosu.domain.usecase.ObserveProfileTemplateRefreshingUseCase
import org.cryosu.cryosu.domain.usecase.ObserveProfileTemplatesUseCase
import org.cryosu.cryosu.domain.usecase.ObserveRepositorySourcesUseCase
import org.cryosu.cryosu.domain.usecase.ObserveStartupStateUseCase
import org.cryosu.cryosu.domain.usecase.ObserveSulogStateUseCase
import org.cryosu.cryosu.domain.usecase.ObserveSuperUserStateUseCase
import org.cryosu.cryosu.domain.usecase.ObserveUmountStateUseCase
import org.cryosu.cryosu.domain.usecase.PeekOneshotScriptUseCase
import org.cryosu.cryosu.domain.usecase.PrepareScriptUseCase
import org.cryosu.cryosu.domain.usecase.RebootUseCase
import org.cryosu.cryosu.domain.usecase.RefreshDynamicManagerUseCase
import org.cryosu.cryosu.domain.usecase.RefreshInstalledModulesUseCase
import org.cryosu.cryosu.domain.usecase.RefreshModuleCatalogUseCase
import org.cryosu.cryosu.domain.usecase.RefreshProfileTemplatesUseCase
import org.cryosu.cryosu.domain.usecase.RefreshSulogUseCase
import org.cryosu.cryosu.domain.usecase.RefreshSuperUsersUseCase
import org.cryosu.cryosu.domain.usecase.RefreshUmountPathsUseCase
import org.cryosu.cryosu.domain.usecase.ReleaseScriptUseCase
import org.cryosu.cryosu.domain.usecase.RemoveModuleRepositoryUseCase
import org.cryosu.cryosu.domain.usecase.RemovePreferenceUseCase
import org.cryosu.cryosu.domain.usecase.RemoveUmountPathUseCase
import org.cryosu.cryosu.domain.usecase.RenameModuleRepositoryUseCase
import org.cryosu.cryosu.domain.usecase.ResolveModuleUpdateUseCase
import org.cryosu.cryosu.domain.usecase.RunScriptUseCase
import org.cryosu.cryosu.domain.usecase.SaveModuleActionLogUseCase
import org.cryosu.cryosu.domain.usecase.SaveProfileTemplateUseCase
import org.cryosu.cryosu.domain.usecase.SelectDynamicManagerUseCase
import org.cryosu.cryosu.domain.usecase.SetAppProfileUseCase
import org.cryosu.cryosu.domain.usecase.SetAppSepolicyUseCase
import org.cryosu.cryosu.domain.usecase.SetBooleanPreferenceUseCase
import org.cryosu.cryosu.domain.usecase.SetDefaultUmountModulesUseCase
import org.cryosu.cryosu.domain.usecase.SetKernelUmountEnabledUseCase
import org.cryosu.cryosu.domain.usecase.SetManualDynamicManagerUseCase
import org.cryosu.cryosu.domain.usecase.SetModuleEnabledUseCase
import org.cryosu.cryosu.domain.usecase.SetModuleRemovedUseCase
import org.cryosu.cryosu.domain.usecase.SetSelinuxHideEnabledUseCase
import org.cryosu.cryosu.domain.usecase.SetShellSuAdbOnlyEnabledUseCase
import org.cryosu.cryosu.domain.usecase.SetStringPreferenceUseCase
import org.cryosu.cryosu.domain.usecase.SetStringSetPreferenceUseCase
import org.cryosu.cryosu.domain.usecase.SetSuEnabledUseCase
import org.cryosu.cryosu.domain.usecase.StartKernelFlashUseCase
import org.cryosu.cryosu.domain.usecase.SuSFSConfigUseCase
import org.cryosu.cryosu.domain.usecase.TakeModuleUriPermissionUseCase
import org.cryosu.cryosu.domain.usecase.TransliterateTextUseCase
import org.cryosu.cryosu.domain.usecase.UpdateAppearanceUseCase
import org.cryosu.cryosu.domain.usecase.UpdateCachedModuleEnabledUseCase
import org.cryosu.cryosu.domain.usecase.UpdatePlatformSettingUseCase
import org.cryosu.cryosu.domain.usecase.ValidateSepolicyUseCase
import org.cryosu.cryosu.ui.activity.util.ThemeUtils
import org.cryosu.cryosu.ui.component.ZipFileDetector
import org.cryosu.cryosu.ui.theme.BackgroundManager
import org.cryosu.cryosu.ui.theme.CardConfig
import org.cryosu.cryosu.ui.theme.ThemeConfig
import org.cryosu.cryosu.ui.util.Shortcut
import org.cryosu.cryosu.ui.viewmodel.AppProfileViewModel
import org.cryosu.cryosu.ui.viewmodel.DynamicManagerViewModel
import org.cryosu.cryosu.ui.viewmodel.ExecuteModuleActionViewModel
import org.cryosu.cryosu.ui.viewmodel.FlashViewModel
import org.cryosu.cryosu.ui.viewmodel.HomeViewModel
import org.cryosu.cryosu.ui.viewmodel.InstallViewModel
import org.cryosu.cryosu.ui.viewmodel.KernelFlashViewModel
import org.cryosu.cryosu.ui.viewmodel.MainIntentViewModel
import org.cryosu.cryosu.ui.viewmodel.ModuleDetailViewModel
import org.cryosu.cryosu.ui.viewmodel.ModuleRepoViewModel
import org.cryosu.cryosu.ui.viewmodel.ModuleViewModel
import org.cryosu.cryosu.ui.viewmodel.RepositorySourcesViewModel
import org.cryosu.cryosu.ui.viewmodel.ScriptRunViewModel
import org.cryosu.cryosu.ui.viewmodel.SettingsViewModel
import org.cryosu.cryosu.ui.viewmodel.SuSFSViewModel
import org.cryosu.cryosu.ui.viewmodel.SulogViewModel
import org.cryosu.cryosu.ui.viewmodel.SuperUserViewModel
import org.cryosu.cryosu.ui.viewmodel.TemplateEditorViewModel
import org.cryosu.cryosu.ui.viewmodel.TemplateViewModel
import org.cryosu.cryosu.ui.viewmodel.UmountManagerScreenViewModel
import org.cryosu.cryosu.ui.webui.MonetColorsProvider
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.core.qualifier.named
import org.koin.dsl.bind
import org.koin.dsl.module

val applicationScopeQualifier = named("applicationScope")

val coreModule = module {
    single<CoroutineScope>(applicationScopeQualifier) {
        CoroutineScope(SupervisorJob() + Dispatchers.IO)
    }
    single {
        OkHttpClient.Builder()
            .cache(Cache(File(androidApplication().cacheDir, "okhttp"), 10L * 1024L * 1024L))
            .addInterceptor { chain ->
                chain.proceed(
                    chain.request().newBuilder()
                        .header("User-Agent", "CryoSU/${BuildConfig.VERSION_CODE}")
                        .header("Accept-Language", Locale.getDefault().toLanguageTag())
                        .build(),
                )
            }
            .connectTimeout(5, TimeUnit.SECONDS)
            .readTimeout(5, TimeUnit.SECONDS)
            .writeTimeout(5, TimeUnit.SECONDS)
            .build()
    }
    single {
        val application = androidApplication()
        val iconSize = application.resources.getDimensionPixelSize(android.R.dimen.app_icon_size)
        ImageLoader.Builder(application)
            .components {
                add(AppIconKeyer())
                add(AppIconFetcher.Factory(iconSize, false, application))
            }
            .build()
    }
}

val repositoryModule = module {
    single { KsuCliRepository(androidApplication()) }
    singleOf(::CountRepository)
    singleOf(::InstalledPackageCache)
    singleOf(::AppIconDataSource)
    singleOf(::RootServiceRepository)
    singleOf(::InstalledPackageRepository)
    single {
        SuperUserRepository(
            application = get(),
            cache = get(),
            installedPackageRepository = get(),
            profileRepository = get(),
            applicationScope = get(applicationScopeQualifier),
        )
    }
    single {
        AppSettingsRepository(
            context = androidApplication(),
            applicationScope = get(applicationScopeQualifier),
        )
    }
    singleOf(::StartupRepository)
    single {
        ApplicationInitializationRepository(
            application = get(),
            imageLoader = get(),
            applicationScope = get(applicationScopeQualifier),
            flashRepository = get(),
            ksuCliRepository = get(),
        )
    }
    singleOf(::ManagerUpdateRepository)
    singleOf(::ApplicationControlRepository)
    singleOf(::DownloadRepository)
    single { FlashRepository(get(), get(applicationScopeQualifier), get(), get()) }
    singleOf(::KernelRepository)
    singleOf(::HomeRuntimeRepository)
    singleOf(::HomeStateRepository)
    singleOf(::NetworkStatusRepository)
    singleOf(::NetworkRequestRepository)
    singleOf(::DynamicManagerRepository)
    singleOf(::SulogRepository)
    singleOf(::BugreportRepository)
    singleOf(::UmountRepository)
    singleOf(::ModuleCatalogRepository)
    single { ModuleReleaseRepository(get(), get(applicationScopeQualifier)) }
    singleOf(::ModuleIconRepository)
    single { KernelSuRegistryRepository(get(), get(), get(), get()) }
    singleOf(::RepositorySourceStore)
    singleOf(::ModuleRepository)
    singleOf(::ModulePreferencesRepository)
    singleOf(::ModuleActionRepository)
    single { ScriptRepository(androidApplication(), get(), get(applicationScopeQualifier)) }
    singleOf(::WebResourceRepository)
    singleOf(::WebUiRepository)
    singleOf(::ModuleFileRepository)
    singleOf(::ProfileRepository)
    singleOf(::ProfileTemplateRepository)
    singleOf(::SuSFSConfigHelper)
    singleOf(::SuSFSRepository)
    singleOf(::MonetCompatColorSource)
    singleOf(::ThemeRepository)
    single {
        val themeRepository = get<ThemeRepository>()
        ThemeConfig(themeRepository::defaultSeedColor)
    }
    singleOf(::CardConfig)
    singleOf(::BackgroundManager)
    singleOf(::ThemeUtils)
    singleOf(::LocaleHelper)
    singleOf(::LocaleRepository)
    singleOf(::SettingsPlatformRepository)
    singleOf(::ShortcutRepository)
    singleOf(::Shortcut)
    singleOf(::MonetColorsProvider)
    singleOf(::ZipFileDetector)
    single { HanziToPinyin.create() } bind TextTransliterator::class
}

val useCaseModule = module {
    factoryOf(::InitializeApplicationUseCase)
    factoryOf(::GetHomeBasicInfoUseCase)
    factoryOf(::IsNetworkAvailableUseCase)
    factoryOf(::LoadSettingsPlatformUseCase)
    factoryOf(::UpdateAppearanceUseCase)
    factoryOf(::UpdatePlatformSettingUseCase)
    factoryOf(::GetPlatformFeatureStatusUseCase)
    factoryOf(::IsSoftRebootPreferredUseCase)
    factoryOf(::CheckManagerUpdateUseCase)
    factoryOf(::EnsureManagerInstalledUseCase)
    factoryOf(::RebootUseCase)
    factoryOf(::EnqueueDownloadUseCase)
    factoryOf(::EnqueueManagerUpdateUseCase)
    factoryOf(::ObserveDownloadUseCase)
    factoryOf(::ObserveAssetDownloadUseCase)
    factoryOf(::GetKernelStatusUseCase)
    factoryOf(::GetInstallEnvironmentUseCase)
    factoryOf(::ExecuteFlashOperationUseCase)
    factoryOf(::CheckFlashModuleMountUseCase)
    factoryOf(::GetManagerRuntimeInfoUseCase)
    factoryOf(::GetKernelFeatureSettingsUseCase)
    factoryOf(::SetSuEnabledUseCase)
    factoryOf(::SetKernelUmountEnabledUseCase)
    factoryOf(::SetShellSuAdbOnlyEnabledUseCase)
    factoryOf(::ConfigureSuLogUseCase)
    factoryOf(::SetSelinuxHideEnabledUseCase)
    factoryOf(::SetDefaultUmountModulesUseCase)
    factoryOf(::IsLateLoadModeUseCase)
    factoryOf(::GetAppProfileUseCase)
    factoryOf(::SetAppProfileUseCase)
    factoryOf(::GetAppSepolicyUseCase)
    factoryOf(::SetAppSepolicyUseCase)
    factoryOf(::ControlAppUseCase)
    factoryOf(::ValidateSepolicyUseCase)
    factoryOf(::GetDefaultUmountModulesUseCase)
    factoryOf(::GetSuSFSStatusUseCase)
    factoryOf(::SuSFSConfigUseCase)
    factoryOf(::ApplyLanguageUseCase)
    factoryOf(::IsSystemLanguageSettingsUseCase)
    factoryOf(::LaunchSystemLanguageSettingsUseCase)
    factoryOf(::GenerateBugreportUseCase)
    factoryOf(::ObserveStartupStateUseCase)
    factoryOf(::GetSuperUserAppGroupUseCase)
    factoryOf(::ObserveCatalogModulesUseCase)
    factoryOf(::ObserveModuleCatalogRefreshingUseCase)
    factoryOf(::RefreshModuleCatalogUseCase)
    factoryOf(::GetCatalogModuleUseCase)
    factoryOf(::IsCatalogModuleCurrentUseCase)
    factoryOf(::GetModuleReadmeUseCase)
    factoryOf(::GetModuleReleaseDetailsUseCase)
    factoryOf(::LoadModuleIconUseCase)
    factoryOf(::ResolveModuleUpdateUseCase)
    factoryOf(::GetModuleUpdateChangelogUseCase)
    factoryOf(::ObserveRepositorySourcesUseCase)
    factoryOf(::AddModuleRepositoryUseCase)
    factoryOf(::RenameModuleRepositoryUseCase)
    factoryOf(::RemoveModuleRepositoryUseCase)
    factoryOf(::ObserveProfileTemplatesUseCase)
    factoryOf(::ObserveProfileTemplateRefreshingUseCase)
    factoryOf(::ObserveProfileTemplateOfflineUseCase)
    factoryOf(::RefreshProfileTemplatesUseCase)
    factoryOf(::GetProfileTemplateUseCase)
    factoryOf(::SaveProfileTemplateUseCase)
    factoryOf(::DeleteProfileTemplateUseCase)
    factoryOf(::ImportProfileTemplatesUseCase)
    factoryOf(::ExportProfileTemplatesUseCase)
    factoryOf(::GetBooleanPreferenceUseCase)
    factoryOf(::SetBooleanPreferenceUseCase)
    factoryOf(::GetStringPreferenceUseCase)
    factoryOf(::SetStringPreferenceUseCase)
    factoryOf(::GetStringSetPreferenceUseCase)
    factoryOf(::SetStringSetPreferenceUseCase)
    factoryOf(::ObserveDynamicManagerStateUseCase)
    factoryOf(::RefreshDynamicManagerUseCase)
    factoryOf(::SelectDynamicManagerUseCase)
    factoryOf(::SetManualDynamicManagerUseCase)
    factoryOf(::ClearDynamicManagerUseCase)
    factoryOf(::ObserveSulogStateUseCase)
    factoryOf(::RefreshSulogUseCase)
    factoryOf(::EnableSulogUseCase)
    factoryOf(::CleanSulogUseCase)
    factoryOf(::ObserveUmountStateUseCase)
    factoryOf(::RefreshUmountPathsUseCase)
    factoryOf(::AddUmountPathUseCase)
    factoryOf(::RemoveUmountPathUseCase)
    factoryOf(::ObserveKernelFlashUseCase)
    factoryOf(::StartKernelFlashUseCase)
    factoryOf(::RemovePreferenceUseCase)
    factoryOf(::ObserveSuperUserStateUseCase)
    factoryOf(::RefreshSuperUsersUseCase)
    factoryOf(::BackupAllowlistUseCase)
    factoryOf(::ImportAllowlistUseCase)
    factoryOf(::FetchRemoteTextUseCase)
    factoryOf(::IsModuleUriAccessibleUseCase)
    factoryOf(::TakeModuleUriPermissionUseCase)
    factoryOf(::ExtractModuleNameUseCase)
    factoryOf(::ExtractModuleIdUseCase)
    factoryOf(::ObserveInstalledModulesUseCase)
    factoryOf(::RefreshInstalledModulesUseCase)
    factoryOf(::CalculateInstalledModuleSizeUseCase)
    factoryOf(::UpdateCachedModuleEnabledUseCase)
    factoryOf(::ExecuteModuleActionUseCase)
    factoryOf(::PeekOneshotScriptUseCase)
    factoryOf(::PrepareScriptUseCase)
    factoryOf(::ReleaseScriptUseCase)
    factoryOf(::RunScriptUseCase)
    factoryOf(::GetLastScriptRunUseCase)
    factoryOf(::SaveModuleActionLogUseCase)
    factoryOf(::SetModuleEnabledUseCase)
    factoryOf(::SetModuleRemovedUseCase)
    factoryOf(::TransliterateTextUseCase)
}

val viewModelModule = module {
    viewModel { parameters -> ScriptRunViewModel(parameters[0], get(), get(), get(), get(), get()) }
    viewModel { parameters ->
        AppProfileViewModel(
            uid = parameters[0],
            packageName = parameters[1],
            getAppGroup = get(),
            getProfile = get(),
            getDefaultUmountModules = get(),
            setProfile = get(),
            getSepolicy = get(),
            setSepolicy = get(),
            controlApp = get(),
            validateSepolicy = get(),
        )
    }
    viewModelOf(::HomeViewModel)
    viewModelOf(::InstallViewModel)
    viewModelOf(::MainIntentViewModel)
    viewModelOf(::KernelFlashViewModel)
    viewModelOf(::SettingsViewModel)
    viewModelOf(::ModuleViewModel)
    viewModelOf(::SuperUserViewModel)
    viewModelOf(::SuSFSViewModel)
    viewModel { parameters -> ModuleRepoViewModel(get(), get(), get(), get(), get(), get(), get(), get(), get(), get(), parameters.getOrNull<String>()) }
    viewModel { parameters -> ModuleDetailViewModel(parameters[0], parameters[1], get(), get(), get(), get(), get(), get(), get()) }
    viewModelOf(::RepositorySourcesViewModel)
    viewModelOf(::TemplateViewModel)
    viewModel { parameters ->
        TemplateEditorViewModel(
            templateId = parameters[0],
            readOnly = parameters[1],
            isCreation = parameters[2],
            getTemplate = get(),
            saveTemplate = get(),
            deleteTemplate = get(),
        )
    }
    viewModelOf(::SulogViewModel)
    viewModelOf(::DynamicManagerViewModel)
    viewModelOf(::FlashViewModel)
    viewModelOf(::UmountManagerScreenViewModel)
    viewModel { parameters ->
        ExecuteModuleActionViewModel(
            moduleId = parameters[0],
            executeModuleAction = get(),
            saveModuleActionLog = get(),
        )
    }
}

val appModules = listOf(coreModule, repositoryModule, useCaseModule, viewModelModule)
