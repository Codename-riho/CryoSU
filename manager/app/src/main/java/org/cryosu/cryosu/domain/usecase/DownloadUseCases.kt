package org.cryosu.cryosu.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import org.cryosu.cryosu.data.download.DownloadRepository
import org.cryosu.cryosu.domain.model.DownloadState
import org.cryosu.cryosu.domain.model.ExpectedFile
import org.cryosu.cryosu.domain.model.ManagerUpdateInfo
import org.cryosu.cryosu.domain.model.ModuleReleaseAsset

class EnqueueDownloadUseCase(private val repository: DownloadRepository) {
    operator fun invoke(url: String, fileName: String, expected: ExpectedFile? = null): Int = repository.enqueue(url, fileName, expected)
}

class EnqueueManagerUpdateUseCase(private val repository: DownloadRepository) {
    operator fun invoke(update: ManagerUpdateInfo): Int = repository.enqueueManagerUpdate(update)
}

class ObserveDownloadUseCase(private val repository: DownloadRepository) {
    operator fun invoke(id: Int): Flow<DownloadState?> = repository.downloads.map { it[id] }
}

class ObserveAssetDownloadUseCase(private val repository: DownloadRepository) {
    operator fun invoke(asset: ModuleReleaseAsset): Flow<DownloadState?> = repository.downloads.map { find(it, asset) }.distinctUntilChanged()

    fun current(asset: ModuleReleaseAsset): DownloadState? = find(repository.downloads.value, asset)

    private fun find(downloads: Map<Int, DownloadState>, asset: ModuleReleaseAsset): DownloadState? {
        if (asset.verificationRequired && asset.expected == null) return null
        return downloads.values.lastOrNull {
            it.url == asset.downloadUrl && if (asset.expected == null) it.expected == null else asset.expected.sameAs(it.expected)
        }
    }
}
