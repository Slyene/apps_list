package com.slyene.appslist.presentation.detail

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.slyene.appslist.presentation.AppsViewModelFactory
import kotlinx.serialization.Serializable

@Serializable
data class AppDetail(val packageName: String)

fun NavController.navigateToAppDetail(
    packageName: String
) {
    navigate(AppDetail(packageName))
}

fun NavGraphBuilder.appDetailScreen() {
    composable<AppDetail> { backStackEntry ->
        val appDetail = backStackEntry.toRoute<AppDetail>()
        AppDetailRoot(
            viewModel = viewModel(
                factory = AppsViewModelFactory(LocalContext.current.applicationContext)
            ),
            packageName = appDetail.packageName
        )
    }
}