package com.pira.ccloud.utils

import android.content.Context
import android.util.Log
import com.pira.ccloud.data.model.Movie
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

object StorageUtils {
    private const val TAG = "StorageUtils"
    
    fun saveMovieToFile(context: Context, movie: Movie) {
        try {
            // Clear all existing movie files first
            clearAllMovies(context)
            
            val jsonString = Json.encodeToString(movie)
            val fileName = "movie_${movie.id}.json"
            val file = File(context.filesDir, fileName)
            file.writeText(jsonString)
            Log.d(TAG, "Movie saved to file: $fileName")
        } catch (e: Exception) {
            Log.e(TAG, "Error saving movie to file", e)
        }
    }
    
    fun loadMovieFromFile(context: Context, movieId: Int): Movie? {
        return try {
            val fileName = "movie_$movieId.json"
            val file = File(context.filesDir, fileName)
            if (file.exists()) {
                val jsonString = file.readText()
                Json.decodeFromString<Movie>(jsonString)
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error loading movie from file", e)
            null
        }
    }
    
    /**
     * Delete all movie files from storage
     */
    fun clearAllMovies(context: Context) {
        try {
            val filesDir = context.filesDir
            val movieFiles = filesDir.listFiles { file ->
                file.name.startsWith("movie_") && file.name.endsWith(".json")
            }
            
            movieFiles?.forEach { file ->
                file.delete()
                Log.d(TAG, "Deleted movie file: ${file.name}")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing all movies", e)
        }
    }
}