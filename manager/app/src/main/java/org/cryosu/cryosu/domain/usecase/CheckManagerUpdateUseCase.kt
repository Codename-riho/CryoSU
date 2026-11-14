package org.cryosu.cryosu.domain.usecase

import org.cryosu.cryosu.data.update.ManagerUpdateRepository
import org.cryosu.cryosu.domain.model.ManagerUpdateChannel
import org.cryosu.cryosu.domain.model.ManagerUpdateInfo

class CheckManagerUpdateUseCase(
    private val repository: ManagerUpdateRepository,
) {
    suspend operator fun invoke(channel: ManagerUpdateChannel): ManagerUpdateInfo? = when (channel) {
        ManagerUpdateChannel.STABLE -> repository.checkStableUpdate()
        ManagerUpdateChannel.BETA -> repository.checkBetaUpdate()
    }
}
