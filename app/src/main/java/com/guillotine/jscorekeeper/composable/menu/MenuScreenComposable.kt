package com.guillotine.jscorekeeper.composable.menu

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.guillotine.jscorekeeper.FinalScreen
import com.guillotine.jscorekeeper.data.GameModes
import com.guillotine.jscorekeeper.GameScreen
import com.guillotine.jscorekeeper.PastGamesListScreen
import com.guillotine.jscorekeeper.R
import com.guillotine.jscorekeeper.composable.general.RadioButtonList
import com.guillotine.jscorekeeper.data.SavedGame
import com.guillotine.jscorekeeper.viewmodels.MenuScreenViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreenComposable(
    navController: NavHostController,
    savedGame: SavedGame? = null,
    viewModel: MenuScreenViewModel
) {
    val scope = rememberCoroutineScope()
    LaunchedEffect(viewModel.attemptedLoadTimestamp) {
        if (!viewModel.attemptedLoadTimestamp) {
            viewModel.loadTimestamp()
        }
    }

    val frontBackPadding = 8.dp
    val labelFrontPadding = 16.dp
    val labelBottomPadding = 4.dp

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
        TopAppBar(
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainer
            ),
            title = {
                Text(stringResource(R.string.app_name))
            },
            actions = {
                IconButton(onClick = {
                    navController.navigate(
                        PastGamesListScreen
                    )
                }) {
                    Icon(
                        painter = painterResource(id = R.drawable.baseline_history_24),
                        contentDescription = stringResource(R.string.history_icon_description)
                    )
                }
            }
        )
    }, modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier =
            Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .windowInsetsPadding(WindowInsets.displayCutout)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = labelFrontPadding, bottom = labelBottomPadding),
                    text = stringResource(R.string.select_game),
                    style = MaterialTheme.typography.titleSmall,
                    textAlign = TextAlign.Start,
                    color = MaterialTheme.colorScheme.primary
                )
                RadioButtonList(
                    currentSelectedMenuOption = viewModel.currentSelectedOption,
                    onMenuOptionSelected = {
                        viewModel.currentSelectedOption = it
                    },
                    listOfMenuOptions = listOf(
                        GameModes.USA,
                        GameModes.UK,
                        GameModes.AUSTRALIA,
                        GameModes.US_CELEB
                    ),
                    modifier = Modifier
                        .padding(frontBackPadding)
                        .fillMaxWidth()
                        .weight(1f)
                )
                Row(
                    // Per M3 spec, 40dp is the height of a button, +24+16.dp for the padding.
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(78.dp)
                        .padding(bottom = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 24.dp, start = 24.dp, end = 24.dp),
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (savedGame != null && savedGame.isFinal) {
                                    navController.navigate(
                                        FinalScreen(
                                            score = savedGame.score,
                                            round = savedGame.round,
                                            currency = savedGame.gameData.currency,
                                            moneyValues = savedGame.gameData.moneyValues,
                                            multipliers = savedGame.gameData.multipliers,
                                            columns = savedGame.gameData.columns,
                                            timestamp = viewModel.timestamp
                                        )
                                    )
                                } else {
                                    navController.navigate(
                                        GameScreen(
                                            gameMode = GameModes.RESUME,
                                            // The important bit that won't get overwritten.
                                            timestamp = viewModel.timestamp
                                        )
                                    )
                                }
                            },
                            enabled = viewModel.isSavedGame(savedGame)
                        ) {
                            Text(stringResource(R.string.resume_game))
                        }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 24.dp, start = 24.dp, end = 24.dp),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Button(
                            onClick = {
                                scope.launch {
                                    viewModel.deleteSavedGame()
                                    viewModel.createGame(viewModel.currentSelectedOption)
                                    navController.navigate(
                                        GameScreen(
                                            gameMode = viewModel.currentSelectedOption,
                                            timestamp = viewModel.timestamp
                                        )
                                    )
                                }
                            }
                        ) {
                            Text(stringResource(R.string.new_game))
                        }
                    }
                }
            }
        }
    }
}