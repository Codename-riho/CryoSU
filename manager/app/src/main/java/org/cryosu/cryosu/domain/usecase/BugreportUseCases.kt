package org.cryosu.cryosu.domain.usecase

import java.io.File
import org.cryosu.cryosu.data.logging.BugreportRepository

class GenerateBugreportUseCase(
    private val repository: BugreportRepository,
) {
    operator fun invoke(): File = repository.create()
}
