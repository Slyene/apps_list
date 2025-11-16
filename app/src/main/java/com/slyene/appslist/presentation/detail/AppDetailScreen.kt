package com.slyene.appslist.presentation.detail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.drawablepainter.rememberDrawablePainter
import com.slyene.appslist.R
import com.slyene.appslist.domain.model.AppInfo
import com.slyene.appslist.domain.model.ChecksumState
import com.slyene.appslist.presentation.AppsViewModel
import com.slyene.appslist.presentation.AppsViewModelFactory

@Composable
fun AppDetailRoot(
    packageName: String,
    viewModel: AppsViewModel = viewModel(
        factory = AppsViewModelFactory(LocalContext.current.applicationContext)
    ),
    onNavigateUp: () -> Unit,
) {
    val apps = viewModel.state.collectAsStateWithLifecycle().value.apps
    val app = apps.find { it.packageName == packageName }
    val checksumState by viewModel.checksumState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    if (app != null) {
        LaunchedEffect(key1 = app.packageName) {
            viewModel.calculateChecksum(app.packageName, app.sourceDir)
        }

        AppDetailScreen(
            app = app,
            checksumState = checksumState,
            onOpenApp = {
                val intent = context.packageManager.getLaunchIntentForPackage(it)
                if (intent != null) {
                    context.startActivity(intent)
                }
            },
            onNavigateUp = onNavigateUp,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    app: AppInfo,
    checksumState: ChecksumState,
    onOpenApp: (packageName: String) -> Unit,
    onNavigateUp: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateUp
                    ) {
                        Icon(
                            painterResource(R.drawable.arrow_back_24),
                            contentDescription = null
                        )
                    }
                },
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = rememberDrawablePainter(drawable = app.icon),
                contentDescription = null,
                modifier = Modifier.size(80.dp),
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "${stringResource(R.string.name)}: ${app.name}")

            Text(text = "${stringResource(R.string.version)}: ${app.versionName}")

            Text(text = "${stringResource(R.string.packageName)}: ${app.packageName}")

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "${stringResource(R.string.checksum_md5)}: ")

                when (checksumState) {
                    is ChecksumState.Idle -> Text(text = stringResource(R.string.no_data))
                    is ChecksumState.Loading -> {
                        Text(text = stringResource(R.string.calculation_in_progress))
                        LinearProgressIndicator(
                            progress = { checksumState.progress }
                        )
                    }
                    is ChecksumState.Success -> Text(text = checksumState.checksum)
                    is ChecksumState.Error -> Text(text = checksumState.message)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onOpenApp(app.packageName) }
            ) {
                Text(text = stringResource(R.string.open_app))
            }
        }
    }
}
