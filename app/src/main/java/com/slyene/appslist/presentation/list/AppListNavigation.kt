package com.slyene.appslist.presentation.list

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.slyene.appslist.presentation.AppsViewModelFactory
import kotlinx.serialization.Serializable

@Serializable
data object AppList

fun NavGraphBuilder.appListScreen(
    onNavigateToDetail: (packageName: String) -> Unit
) {
    composable<AppList> {
        AppListRoot(
           viewModel = viewModel(
                factory = AppsViewModelFactory(LocalContext.current.applicationContext)
                ),
            onNavigateToDetail = onNavigateToDetail,
        )
    }
}