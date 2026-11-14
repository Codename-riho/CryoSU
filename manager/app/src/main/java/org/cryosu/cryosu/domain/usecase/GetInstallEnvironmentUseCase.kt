package org.cryosu.cryosu.domain.usecase

import org.cryosu.cryosu.data.flash.FlashRepository
import org.cryosu.cryosu.domain.model.InstallEnvironment

class GetInstallEnvironmentUseCase(
    private val repository: FlashRepository,
) {
    fun cached(): InstallEnvironment? = repository.installEnvironment.value

    suspend operator fun invoke(forceRefresh: Boolean = false) = repository.getInstallEnvironment(forceRefresh)
}
