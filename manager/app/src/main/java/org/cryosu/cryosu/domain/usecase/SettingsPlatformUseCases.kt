package org.cryosu.cryosu.domain.usecase

import org.cryosu.cryosu.data.settings.SettingsPlatformRepository
import org.cryosu.cryosu.domain.model.AppearanceSetting
import org.cryosu.cryosu.domain.model.PlatformSetting

class LoadSettingsPlatformUseCase(private val repository: SettingsPlatformRepository) {
    operator fun invoke() = repository.load()
}

class UpdateAppearanceUseCase(private val repository: SettingsPlatformRepository) {
    suspend operator fun invoke(setting: AppearanceSetting) = repository.updateAppearance(setting)
}

class UpdatePlatformSettingUseCase(private val repository: SettingsPlatformRepository) {
    operator fun invoke(setting: PlatformSetting) = repository.updatePlatform(setting)
}

class GetPlatformFeatureStatusUseCase(private val repository: SettingsPlatformRepository) {
    suspend operator fun invoke() = repository.getFeatureStatus()
}

class IsSoftRebootPreferredUseCase(private val repository: SettingsPlatformRepository) {
    operator fun invoke() = repository.isSoftRebootPreferred()
}
