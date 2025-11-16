package com.slyene.appslist.domain.usecase

import com.slyene.appslist.domain.model.AppInfo
import com.slyene.appslist.domain.repository.AppsRepository

class GetInstalledAppsUseCase(private val appsRepository: AppsRepository) {
    suspend operator fun invoke(): List<AppInfo> {
        return appsRepository.getInstalledApps()
    }
}