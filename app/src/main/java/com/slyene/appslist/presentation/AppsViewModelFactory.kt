package com.slyene.appslist.presentation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.slyene.appslist.data.checksums.ChecksumCacheDataSource
import com.slyene.appslist.data.repository.AppsRepositoryImpl
import com.slyene.appslist.domain.usecase.GetAppChecksumUseCase
import com.slyene.appslist.domain.usecase.GetInstalledAppsUseCase

class AppsViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AppsViewModel::class.java)) {
            val checksumCacheDataSource = ChecksumCacheDataSource(context.applicationContext)
            val repository = AppsRepositoryImpl(
                packageManager = context.applicationContext.packageManager,
                checksumCacheDataSource = checksumCacheDataSource
            )
            val getInstalledAppsUseCase = GetInstalledAppsUseCase(repository)
            val getAppChecksumUseCase = GetAppChecksumUseCase(repository)
            @Suppress("UNCHECKED_CAST")
            return AppsViewModel(getInstalledAppsUseCase, getAppChecksumUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
