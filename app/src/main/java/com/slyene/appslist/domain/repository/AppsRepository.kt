package com.slyene.appslist.domain.repository

import com.slyene.appslist.domain.model.AppInfo
import com.slyene.appslist.domain.model.ChecksumState
import kotlinx.coroutines.flow.Flow

interface AppsRepository {
    suspend fun getInstalledApps(): List<AppInfo>
    fun getAppChecksumWithProgress(packageName: String, apkPath: String): Flow<ChecksumState>
}
