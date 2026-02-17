package com.guillotine.jscorekeeper.composable.general

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.ListItemDefaults.colors
import androidx.compose.material3.ListItemDefaults.shapes
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SegmentedListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.guillotine.jscorekeeper.R
import com.guillotine.jscorekeeper.data.ClueTypeRadioButtonOptions
import com.guillotine.jscorekeeper.data.GameModes

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RadioButtonList(
    currentSelectedOption: ClueTypeRadioButtonOptions? = null,
    currentSelectedMenuOption: GameModes? = null,
    onOptionSelected: ((ClueTypeRadioButtonOptions) -> Unit)? = null,
    onMenuOptionSelected: ((GameModes) -> Unit)? = null,
    listOfOptions: List<ClueTypeRadioButtonOptions>? = null,
    listOfMenuOptions: List<GameModes>? = null,
    scrollable: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(modifier) {
        // selectableGroup used for accessibility reasons.
        Column(
            verticalArrangement = Arrangement.spacedBy(ListItemDefaults.SegmentedGap),
            modifier = if (scrollable) {
                Modifier
                    .selectableGroup()
                    // Given I know there's only ever going to be at most four options, I'm not going
                    // through the hassle of making this a LazyColumn and dealing with the layout
                    // implications that results in.
                    .verticalScroll(rememberScrollState())
            } else {
                Modifier.selectableGroup()
            }
        ) {
            if (listOfOptions != null) {
                // Could I have stored this as a list in the XML? Probably. But I feel like it's going
                // to be nicer to handle this in the ViewModel as an enum than as a bunch of strings.
                listOfOptions.forEach { stringID ->
                    SegmentedListItem(
                        selected = (currentSelectedOption == stringID),
                        onClick = {
                            if (onOptionSelected != null) {
                                onOptionSelected(stringID)
                            }
                        },
                        shapes = ListItemDefaults.segmentedShapes(
                            listOfOptions.indexOf(stringID),
                            listOfOptions.size
                        ),
                        colors = ListItemDefaults.segmentedColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        leadingContent = {
                            RadioButton(
                                selected = (currentSelectedOption == stringID),
                                // Set to null for accessibility reasons.
                                onClick = null
                            )
                        }
                    )
                    {
                        Text(
                            text = stringResource(stringID.stringResourceID),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            } else {
                listOfMenuOptions?.forEach { gameMode ->
                    SegmentedListItem(
                        selected = (currentSelectedMenuOption == gameMode),
                        onClick = {
                            if (onMenuOptionSelected != null) {
                                onMenuOptionSelected(gameMode)
                            }
                        },
                        shapes = ListItemDefaults.segmentedShapes(
                            gameMode.ordinal,
                            listOfMenuOptions.size
                        ),
                        colors = ListItemDefaults.segmentedColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                        ),
                        leadingContent = {
                            RadioButton(
                                selected = (currentSelectedMenuOption == gameMode),
                                // Set to null for accessibility reasons.
                                onClick = null
                            )
                        }
                    )
                    {
                        Text(
                            text = stringResource(
                                when (gameMode) {
                                    GameModes.USA -> R.string.us
                                    GameModes.UK -> R.string.uk
                                    GameModes.AUSTRALIA -> R.string.australia
                                    GameModes.US_CELEB -> R.string.us_celeb
                                    else -> R.string.resume_game
                                }
                            ),
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        }
    }
}