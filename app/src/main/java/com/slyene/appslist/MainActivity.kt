package com.slyene.appslist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.slyene.appslist.presentation.detail.appDetailScreen
import com.slyene.appslist.presentation.detail.navigateToAppDetail
import com.slyene.appslist.presentation.list.AppList
import com.slyene.appslist.presentation.list.appListScreen
import com.slyene.appslist.ui.theme.AppsListTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppsListTheme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val navController = rememberNavController()

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                    ) {
                        NavHost(
                            navController = navController,
                            startDestination = AppList,
                        ) {
                            appListScreen(
                                onNavigateToDetail = navController::navigateToAppDetail
                            )

                            appDetailScreen(
                                onNavigateUp = navController::navigateUp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .background(color = colorScheme.surface.copy(alpha = .5f))
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .align(Alignment.TopCenter)
                        )

                        Box(
                            modifier = Modifier
                                .background(color = colorScheme.surface.copy(alpha = .5f))
                                .fillMaxWidth()
                                .navigationBarsPadding()
                                .align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }
}
