package com.guillotine.jscorekeeper.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.cachedIn
import androidx.paging.map
import com.guillotine.jscorekeeper.data.GameModes
import com.guillotine.jscorekeeper.database.ScoreEntity
import com.guillotine.jscorekeeper.database.StatisticsDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HistoryScreenViewModel(
    private val statisticsDatabase: StatisticsDatabase,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {
    //private lateinit var gamesPagingSource: PagingSource<Int, ScoreEntity>
    var isPagingSourceLoaded by mutableStateOf(false)
        private set

    @OptIn(SavedStateHandleSaveableApi::class)
    var isShowDeleteDialog by savedStateHandle.saveable { mutableStateOf(false) }
        private set

    // Number items per page.
    private val pageSize = 20

    init {
        refreshPagingSource(true)
    }

    fun refreshPagingSource(isLoaded: Boolean) {
        viewModelScope.launch {
            statisticsDatabase.statisticsDao().getScoresPagingSource()
                .also { isPagingSourceLoaded = isLoaded }
        }
    }

    fun getGamesPageData(): Flow<PagingData<ScoreEntity>> = Pager(
        PagingConfig(
            pageSize = pageSize
        )
    ) {
        statisticsDatabase.statisticsDao().getScoresPagingSource()
    }.flow.map { pagingData: PagingData<ScoreEntity> ->
        pagingData.map { scoreEntity ->
            ScoreEntity(scoreEntity.timestamp, scoreEntity.score)
        }
    }

    fun getGamesList(): Flow<PagingData<ScoreEntity>> {
        return getGamesPageData().cachedIn(viewModelScope)
    }

    fun showDeleteDialog() {
        isShowDeleteDialog = true
    }

    fun onDeleteDialogDismiss() {
        isShowDeleteDialog = false
    }

    fun deleteAllGames() {
        isPagingSourceLoaded = false
        viewModelScope.launch {
            statisticsDatabase.statisticsDao().deleteAllGames()
            refreshPagingSource(false)
        }
    }

    suspend fun getGameMode(timestamp: Long): GameModes {
        return statisticsDatabase.statisticsDao().getGame(timestamp)!!.gameMode
    }

    companion object {
        val STATISTICS_DATABASE_KEY = object : CreationExtras.Key<StatisticsDatabase> {}

        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val statisticsDatabase = (this[STATISTICS_DATABASE_KEY] as StatisticsDatabase)
                val savedStateHandle = createSavedStateHandle()
                HistoryScreenViewModel(statisticsDatabase, savedStateHandle)
            }
        }
    }
}
