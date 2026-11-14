package org.cryosu.cryosu.domain.usecase

import org.cryosu.cryosu.data.startup.StartupRepository

class ObserveStartupStateUseCase(
    private val repository: StartupRepository,
) {
    operator fun invoke() = repository.state
}
