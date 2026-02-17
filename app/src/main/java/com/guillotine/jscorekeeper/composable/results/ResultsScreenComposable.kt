package com.guillotine.jscorekeeper.composable.results

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.window.core.layout.WindowSizeClass
import com.guillotine.jscorekeeper.R
import com.guillotine.jscorekeeper.viewmodels.ResultsScreenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultsScreenComposable(
    viewModel: ResultsScreenViewModel,
) {

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                title = {
                    Text(stringResource(R.string.results))
                }
            )
        }, modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        if (windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            ResultsHorizontalComposable(viewModel, innerPadding)
        } else {
            ResultsVerticalComposable(viewModel, innerPadding)
        }
    }
}