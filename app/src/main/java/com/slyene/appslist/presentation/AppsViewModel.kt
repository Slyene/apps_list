package com.slyene.appslist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.slyene.appslist.domain.model.AppInfo
import com.slyene.appslist.domain.usecase.GetAppChecksumUseCase
import com.slyene.appslist.domain.usecase.GetInstalledAppsUseCase
import com.slyene.appslist.domain.model.ChecksumState
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class AppsViewModel(
    private val getInstalledAppsUseCase: GetInstalledAppsUseCase,
    private val getAppChecksumUseCase: GetAppChecksumUseCase
) : ViewModel() {

    private val apps = flow { emit(getInstalledAppsUseCase()) }
        .onEach { isLoading.update { false } }

    private val isLoading = MutableStateFlow(true)

    private val searchQuery = MutableStateFlow("")

    private val _checksumState = MutableStateFlow<ChecksumState>(ChecksumState.Idle)
    val checksumState: StateFlow<ChecksumState> = _checksumState.asStateFlow()

    @OptIn(FlowPreview::class)
    val state = combine(
        apps,
        isLoading,
        searchQuery,
        searchQuery.debounce(300L)
    ) { apps,
        isLoading,
        searchQuery,
        debouncedSearchQuery ->
        AppListUiState(
            apps.filter { appInfo ->
                appInfo.name.contains(debouncedSearchQuery, ignoreCase = true)
            },
            isLoading,
            searchQuery
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = AppListUiState()
    )

    fun onSearchTextChange(query: String) {
        searchQuery.update { query }
    }

    fun calculateChecksum(packageName: String, apkPath: String) {
        // Reset state for new calculation or when a different app is selected
        _checksumState.value = ChecksumState.Loading(0.0f)
        getAppChecksumUseCase(packageName, apkPath)
            .onEach { newState ->
                _checksumState.update { newState }
            }
            .launchIn(viewModelScope)
    }
}

data class AppListUiState(
    val apps: List<AppInfo> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = "",
)
