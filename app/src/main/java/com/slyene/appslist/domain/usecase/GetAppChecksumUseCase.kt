package com.slyene.appslist.domain.usecase

import com.slyene.appslist.domain.repository.AppsRepository
import com.slyene.appslist.domain.model.ChecksumState
import kotlinx.coroutines.flow.Flow

class GetAppChecksumUseCase(private val appsRepository: AppsRepository) {
    operator fun invoke(packageName: String, apkPath: String): Flow<ChecksumState> {
        return appsRepository.getAppChecksumWithProgress(packageName, apkPath)
    }
}
