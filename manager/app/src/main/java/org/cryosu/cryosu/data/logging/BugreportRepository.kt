package org.cryosu.cryosu.data.logging

import android.app.Application
import java.io.File
import org.cryosu.cryosu.data.shell.KsuCliRepository

class BugreportRepository(
    private val application: Application,
    private val ksuCliRepository: KsuCliRepository,
) {
    fun create(): File = getBugreportFile(application, ksuCliRepository)
}
