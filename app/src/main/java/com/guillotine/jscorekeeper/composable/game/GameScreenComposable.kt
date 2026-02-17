package com.guillotine.jscorekeeper.composable.game

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarVisuals
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.window.core.layout.WindowSizeClass
import com.guillotine.jscorekeeper.FinalScreen
import com.guillotine.jscorekeeper.MenuScreen
import com.guillotine.jscorekeeper.R
import com.guillotine.jscorekeeper.data.ClueDialogState
import com.guillotine.jscorekeeper.viewmodels.GameScreenViewModel
import com.guillotine.jscorekeeper.data.GameScreenSnackbarVisuals
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun GameScreenComposable(navController: NavHostController, viewModel: GameScreenViewModel) {

    val noMoreDailyDoublesSnackbarVisuals =
        GameScreenSnackbarVisuals(stringResource(R.string.no_remaining_daily_doubles))

    // Requires Context, so again, shouldn't be done in the ViewModel.
    val snackbarScope = rememberCoroutineScope()

    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                title = {
                    Text(

                        stringArrayResource(R.array.round_names_indexed_by_multiplier)[viewModel.getMultiplier()]

                    )
                },
                subtitle = { Text("${stringResource(R.string.round)} ${viewModel.round + 1}") }
            )
        },
        snackbarHost = {
            SnackbarHost(viewModel.snackbarHostState)
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->

        if (viewModel.isFinal) {
            navController.navigate(
                route = FinalScreen(
                    currency = viewModel.currency,
                    score = viewModel.score,
                    round = viewModel.round,
                    moneyValues = viewModel.getBaseMoneyValues(),
                    multipliers = viewModel.getMultipliers(),
                    columns = viewModel.getColumns(),
                    timestamp = viewModel.gameTimestamp
                )
            ) {
                popUpTo(MenuScreen) {
                    inclusive = false
                }
            }
        }

        if (windowSizeClass.isHeightAtLeastBreakpoint(WindowSizeClass.HEIGHT_DP_MEDIUM_LOWER_BOUND))
            GameBoardVerticalComposable(
                innerPadding = innerPadding,
                moneyValues = viewModel.moneyValues,
                currency = viewModel.currency,
                score = viewModel.score,
                onClueClick = { viewModel.showClueDialog(it) },
                onNextRoundClick = { viewModel.showRoundDialog() },
                isRemainingValue = {
                    if (viewModel.columnsPerValue[it] != 0) {
                        true
                    } else {
                        false
                    }
                }
            )
        else {
            GameBoardHorizontalComposable(
                innerPadding = innerPadding,
                moneyValues = viewModel.moneyValues,
                currency = viewModel.currency,
                score = viewModel.score,
                onClueClick = { viewModel.showClueDialog(it) },
                onNextRoundClick = { viewModel.showRoundDialog() },
                isRemainingValue = {
                    viewModel.columnsPerValue[it] != 0
                }
            )
        }

        if (viewModel.isShowRoundDialog) {
            NextRoundDialog(
                onDismissRequest = {
                    viewModel.onRoundDialogDismiss()
                },
                onConfirmation = {
                    viewModel.nextRound()
                }
            )
        } else if (viewModel.clueDialogState != ClueDialogState.NONE) {
            ClueDialog(
                onDismissRequest = {
                    viewModel.onClueDialogDismiss()
                },
                value = viewModel.currentValue,
                currency = viewModel.currency,
                onCorrect = {
                    viewModel.onCorrectResponse(it)
                },
                onIncorrect = {
                    viewModel.onIncorrectResponse(it)
                },
                onPass = {
                    viewModel.onPass(it)
                },
                onDailyDouble = {
                    viewModel.onDailyDouble()
                },
                listOfOptions = viewModel.getClueDialogOptions(),
                isWagerValid = viewModel::isWagerValid,
                clueDialogState = viewModel.clueDialogState,
                onNoMoreDailyDoubles = {
                    showSnackbar(
                        snackbarScope,
                        viewModel.snackbarHostState,
                        noMoreDailyDoublesSnackbarVisuals
                    )
                },
                onOptionSelected = {
                    viewModel.onClueDialogOptionSelected(it)
                },
                currentSelectedOption = viewModel.currentSelectedClueDialogOption,
                wagerText = viewModel.wagerFieldText,
                setWagerText = { viewModel.wagerFieldText = it },
                isShowError = viewModel.isShowWagerFieldError,
                setIsShowError = { viewModel.isShowWagerFieldError = it },
                currentScore = viewModel.score
            )
        }
    }
}

private fun showSnackbar(
    snackbarScope: CoroutineScope,
    snackbarHostState: SnackbarHostState,
    snackbarVisuals: SnackbarVisuals
) {
    snackbarScope.launch {
        snackbarHostState.showSnackbar(snackbarVisuals)
    }
}