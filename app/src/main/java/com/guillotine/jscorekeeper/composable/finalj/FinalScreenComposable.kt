package com.guillotine.jscorekeeper.composable.finalj

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.guillotine.jscorekeeper.FinalScreen
import com.guillotine.jscorekeeper.MenuScreen
import com.guillotine.jscorekeeper.R
import com.guillotine.jscorekeeper.ResultsScreen
import com.guillotine.jscorekeeper.composable.general.RadioButtonList
import com.guillotine.jscorekeeper.composable.general.ScoreCardComposable
import com.guillotine.jscorekeeper.composable.general.WagerFieldComposable
import com.guillotine.jscorekeeper.data.ClueTypeRadioButtonOptions
import com.guillotine.jscorekeeper.viewmodels.FinalScreenViewModel
import kotlin.math.round

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun FinalScreenComposable(
    navController: NavHostController,
    viewModel: FinalScreenViewModel,
    route: FinalScreen
) {
    viewModel.round = route.round + 2
    viewModel.score = route.score
    viewModel.currency = route.currency

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
                title = {
                    Text(
                        stringArrayResource(R.array.round_names_indexed_by_multiplier)[0]
                    )
                },
                subtitle = {Text("${stringResource(R.string.round)} ${viewModel.round}")}
            )
        },
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .windowInsetsPadding(WindowInsets.displayCutout)
                .padding(start = 16.dp, end = 16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Column(modifier = Modifier.padding(bottom = 16.dp)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = stringResource(R.string.entering_final_your_score_is)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ScoreCardComposable(
                        viewModel.currency,
                        viewModel.score,
                        true,
                        Modifier.fillMaxWidth()
                    )
                }
            }
            HorizontalDivider()
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        style = MaterialTheme.typography.titleLarge,
                        text = stringResource(R.string.final_wager_instructions),
                        textAlign = TextAlign.Center
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    WagerFieldComposable(
                        wagerText = viewModel.wagerText,
                        setWagerText = { viewModel.wagerText = it },
                        isShowError = viewModel.isShowError,
                        currency = viewModel.currency,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButtonList(
                    currentSelectedOption = viewModel.currentSelectedRadioButton,
                    onOptionSelected = { viewModel.onRadioButtonSelected(it) },
                    listOfOptions = listOf(
                        ClueTypeRadioButtonOptions.CORRECT,
                        ClueTypeRadioButtonOptions.INCORRECT
                    ),
                    // Since parent is scrollable when overflow, this must not be to compile.
                    scrollable = false
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    modifier = Modifier
                        .fillMaxWidth(),
                    onClick = {
                        if (viewModel.wagerText.isNotEmpty()) {
                            val timestamp = viewModel.submitFinalWager(
                                viewModel.wagerText.toInt(),
                                viewModel.score,
                                viewModel.currentSelectedRadioButton == ClueTypeRadioButtonOptions.CORRECT
                            )

                            if (timestamp != null) {
                                navController.navigate(
                                    route = ResultsScreen(
                                        timestamp = timestamp,
                                        score = if (viewModel.currentSelectedRadioButton == ClueTypeRadioButtonOptions.CORRECT) {
                                            viewModel.score + viewModel.wagerText.toInt()
                                        } else {
                                            viewModel.score - viewModel.wagerText.toInt()
                                        },
                                        moneyValues = route.moneyValues,
                                        multipliers = route.multipliers,
                                        currency = route.currency,
                                        columns = route.columns,
                                        deleteCurrentSavedGame = true
                                    )
                                ) {
                                    popUpTo(MenuScreen) {
                                        inclusive = false
                                    }
                                }
                            }
                        } else {
                            viewModel.showError()
                        }
                    }
                ) {
                    Text(stringResource(R.string.submit))
                }
            }
        }
        /*FinalVerticalComposable(
            innerPadding = innerPadding,
            score = viewModel.score,
            currentSelectedOption = viewModel.currentSelectedRadioButton,
            onOptionSelected = { viewModel.onRadioButtonSelected(it) },
            wagerText = viewModel.wagerText,
            setWagerText = { viewModel.wagerText = it },
            isShowError = viewModel.isShowError,
            submitFinalWager = viewModel::submitFinalWager,
            currency = viewModel.currency,
            moneyValues = route.moneyValues,
            multipliers = route.multipliers,
            columns = route.columns,
            navController = navController,
            showError = viewModel::showError
        )*/
    }
}