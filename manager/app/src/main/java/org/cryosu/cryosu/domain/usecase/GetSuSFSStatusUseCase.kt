package org.cryosu.cryosu.domain.usecase

import org.cryosu.cryosu.data.susfs.SuSFSRepository

class GetSuSFSStatusUseCase(private val repository: SuSFSRepository) {
    suspend operator fun invoke() = repository.getStatus()
}
