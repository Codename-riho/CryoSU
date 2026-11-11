package org.cryosu.cryosu.domain.usecase

import org.cryosu.cryosu.data.AppSettingsRepository
import org.cryosu.cryosu.data.startup.ApplicationInitializationRepository
import org.cryosu.cryosu.data.startup.StartupRepository

class InitializeApplicationUseCase(
    private val settingsRepository: AppSettingsRepository,
    private val startupRepository: StartupRepository,
    private val initializationRepository: ApplicationInitializationRepository,
) {
    suspend operator fun invoke() {
        runCatching {
            settingsRepository.preload()
            initializationRepository.initialize()
        }.onSuccess {
            startupRepository.markReady()
        }.onFailure { error ->
            startupRepository.markFailed(error)
        }
    }
}
