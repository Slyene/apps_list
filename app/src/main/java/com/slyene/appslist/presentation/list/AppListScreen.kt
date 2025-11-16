package com.slyene.appslist.presentation.list

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.slyene.appslist.R
import com.slyene.appslist.presentation.AppListUiState
import com.slyene.appslist.presentation.AppsViewModel
import com.slyene.appslist.presentation.list.component.AppListItem
import kotlinx.coroutines.launch

@Composable
fun AppListRoot(
    viewModel: AppsViewModel,
    onNavigateToDetail: (packageName: String) -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    AppListScreen(
        state = state,
        onSearchTextChange = viewModel::onSearchTextChange,
        onNavigateToDetail = onNavigateToDetail,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppListScreen(
    state: AppListUiState,
    onSearchTextChange: (String) -> Unit,
    onNavigateToDetail: (packageName: String) -> Unit,
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val lazyListState = rememberLazyListState()
    val showScrollUpButton by animateFloatAsState(
        targetValue = if (
            lazyListState.canScrollBackward
            && lazyListState.lastScrolledBackward
        ) { 1f }
        else { 0f }
    )
    val fabElevation = with(LocalDensity.current) {
        remember { 6.dp.toPx() }
    }
    val scope = rememberCoroutineScope()

    AnimatedContent(
        targetState = state.isLoading,
        modifier = Modifier
            .fillMaxSize(),
    ) { isLoading ->
        if (isLoading) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()

                Spacer(
                    modifier = Modifier
                        .height(4.dp)
                )

                Text(text = stringResource(R.string.receiving_app_list))
            }
        } else {
            Scaffold(
                topBar = {
                    CenterAlignedTopAppBar(
                        title = {
                            OutlinedTextField(
                                value = state.searchQuery,
                                onValueChange = onSearchTextChange,
                                modifier = Modifier
                                    .padding(4.dp),
                                placeholder = { Text(stringResource(R.string.app_search)) },
                                shape = CircleShape,
                            )
                        },
                        colors = with(TopAppBarDefaults.topAppBarColors()) {
                            this.copy(
                                scrolledContainerColor = containerColor
                            )
                        },
                        scrollBehavior = scrollBehavior,
                    )
                },
                floatingActionButton = {
                    FloatingActionButton(
                        onClick = {
                            scope.launch {
                                lazyListState.animateScrollToItem(0)
                            }
                        },
                        modifier = Modifier
                            .graphicsLayer {
                                scaleX = showScrollUpButton
                                scaleY = showScrollUpButton
                                alpha = showScrollUpButton
                                shadowElevation = fabElevation * showScrollUpButton
                                shape = ShapeDefaults.Large
                            },
                        elevation = FloatingActionButtonDefaults.elevation(
                            defaultElevation = 0.dp,
                            pressedElevation = 0.dp,
                        )
                    ) {
                        Icon(
                            painterResource(R.drawable.keyboard_arrow_up_24),
                            contentDescription = null,
                        )
                    }
                }
            ) { innerPadding ->
                LazyColumn(
                    modifier = Modifier
                        .nestedScroll(scrollBehavior.nestedScrollConnection)
                        .fillMaxSize(),
                    state = lazyListState,
                    contentPadding = PaddingValues(
                        start = 8.dp,
                        top = innerPadding.calculateTopPadding() + 8.dp,
                        end = 8.dp,
                        bottom = innerPadding.calculateBottomPadding() + 8.dp,
                    ),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    item {
                        Text(
                            text = stringResource(R.string.apps),
                            modifier = Modifier.padding(start = 16.dp),
                            fontWeight = FontWeight.Bold,
                        )
                    }

                    itemsIndexed(
                        items = state.apps,
                        key = { _, app -> app.packageName },
                    ) { index, app ->
                        val shape = {
                            val topRadius = if (index == 0) 16.dp else 4.dp
                            val bottomRadius = if (index == state.apps.lastIndex) 16.dp else 4.dp

                            RoundedCornerShape(
                                topStart = topRadius,
                                topEnd = topRadius,
                                bottomStart = bottomRadius,
                                bottomEnd = bottomRadius
                            )
                        }

                        AppListItem(
                            app = app,
                            onClick = { onNavigateToDetail(app.packageName) },
                            modifier = Modifier.animateItem(),
                            shape = shape(),
                        )
                    }
                }
            }
        }
    }
}

