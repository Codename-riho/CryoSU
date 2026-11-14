package org.cryosu.cryosu.domain.usecase

import org.cryosu.cryosu.data.flash.FlashRepository
import org.cryosu.cryosu.domain.model.FlashOperation

class ExecuteFlashOperationUseCase(private val repository: FlashRepository) {
    operator fun invoke(operation: FlashOperation) = repository.execute(operation)
}
