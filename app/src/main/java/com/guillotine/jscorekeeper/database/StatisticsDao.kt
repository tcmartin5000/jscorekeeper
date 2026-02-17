package com.guillotine.jscorekeeper.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface StatisticsDao {
    @Query("SELECT * FROM games WHERE visible=1")
    suspend fun getFinishedGames(): List<GameEntity>

    @Query("SELECT * FROM games WHERE visible=0 LIMIT 1")
    suspend fun getSavedGame(): GameEntity?

    @Query("SELECT * FROM games WHERE timestamp=:timestamp")
    suspend fun getGame(timestamp: Long): GameEntity?

    @Query("SELECT scores.timestamp, scores.score FROM games, scores WHERE games.visible=1 AND games.timestamp=scores.timestamp ORDER BY scores.timestamp DESC")
    suspend fun getScoresList(): List<ScoreEntity>

    @Query("SELECT scores.timestamp, scores.score FROM games, scores WHERE games.visible=1 AND games.timestamp=scores.timestamp ORDER BY scores.timestamp DESC")
    fun getScoresPagingSource(): PagingSource<Int, ScoreEntity>

    @Query("SELECT * FROM clues WHERE timestamp=:timestamp")
    suspend fun getClues(timestamp: Long): List<ClueEntity>

    @Query("SELECT * FROM daily_doubles WHERE timestamp=:timestamp")
    suspend fun getDailyDoubles(timestamp: Long): List<DailyDoubleEntity>

    @Query("SELECT * FROM finals WHERE timestamp=:timestamp")
    suspend fun getFinal(timestamp: Long): FinalEntity?

    @Query("DELETE FROM games WHERE visible=0")
    suspend fun deleteSavedGame()

    @Query("DELETE FROM games")
    suspend fun deleteAllGames()

    @Query("UPDATE games SET visible=1 WHERE timestamp=:timestamp")
    suspend fun setVisible(timestamp: Long)

    @Insert
    suspend fun createGame(game: GameEntity)

    @Insert
    suspend fun insertClue(clue: ClueEntity)

    @Insert
    suspend fun insertDailyDouble(dailyDouble: DailyDoubleEntity)

    @Insert
    // can't call it final haha
    suspend fun insertFinal(finalClue: FinalEntity)

    @Insert
    suspend fun insertScore(score: ScoreEntity)
}