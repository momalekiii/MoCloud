package com.pira.ccloud.data.repository

import com.pira.ccloud.data.model.Country
import com.pira.ccloud.data.model.Genre
import com.pira.ccloud.data.model.Movie
import com.pira.ccloud.data.model.Source
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

class MovieRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()
    
    private val BASE_URL = "https://hostinnegar.com/api/movie/by/filtres/0/created"
    private val API_KEY = "4F5A9C3D9A86FA54EACEDDD635185"
    
    suspend fun getMovies(page: Int = 0): List<Movie> {
        return withContext(Dispatchers.IO) {
            try {
                val url = "$BASE_URL/$page/$API_KEY"
                val request = Request.Builder()
                    .url(url)
                    .build()
                
                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        throw Exception("Failed to fetch movies: ${response.code}")
                    }
                    
                    val jsonData = response.body?.string()
                        ?: throw Exception("Empty response body")
                    
                    parseMovies(jsonData)
                }
            } catch (e: Exception) {
                throw Exception("Error fetching movies: ${e.message}")
            }
        }
    }
    
    private fun parseMovies(jsonData: String): List<Movie> {
        val movies = mutableListOf<Movie>()
        val jsonArray = JSONArray(jsonData)
        
        for (i in 0 until jsonArray.length()) {
            val movieObj = jsonArray.getJSONObject(i)
            val movie = parseMovie(movieObj)
            movies.add(movie)
        }
        
        return movies
    }
    
    private fun parseMovie(movieObj: JSONObject): Movie {
        return Movie(
            id = movieObj.getInt("id"),
            type = movieObj.getString("type"),
            title = movieObj.getString("title"),
            description = movieObj.getString("description"),
            year = movieObj.getInt("year"),
            imdb = movieObj.getDouble("imdb"),
            rating = movieObj.getDouble("rating"),
            duration = movieObj.optString("duration").takeIf { it != "null" && it != "N/A" },
            image = movieObj.getString("image"),
            cover = movieObj.getString("cover"),
            genres = parseGenres(movieObj.getJSONArray("genres")),
            sources = parseSources(movieObj.getJSONArray("sources")),
            country = parseCountries(movieObj.getJSONArray("country"))
        )
    }
    
    private fun parseGenres(genresArray: JSONArray): List<Genre> {
        val genres = mutableListOf<Genre>()
        for (i in 0 until genresArray.length()) {
            val genreObj = genresArray.getJSONObject(i)
            genres.add(
                Genre(
                    id = genreObj.getInt("id"),
                    title = genreObj.getString("title")
                )
            )
        }
        return genres
    }
    
    private fun parseSources(sourcesArray: JSONArray): List<Source> {
        val sources = mutableListOf<Source>()
        for (i in 0 until sourcesArray.length()) {
            val sourceObj = sourcesArray.getJSONObject(i)
            sources.add(
                Source(
                    id = sourceObj.getInt("id"),
                    quality = sourceObj.getString("quality"),
                    type = sourceObj.getString("type"),
                    url = sourceObj.getString("url")
                )
            )
        }
        return sources
    }
    
    private fun parseCountries(countriesArray: JSONArray): List<Country> {
        val countries = mutableListOf<Country>()
        for (i in 0 until countriesArray.length()) {
            val countryObj = countriesArray.getJSONObject(i)
            countries.add(
                Country(
                    id = countryObj.getInt("id"),
                    title = countryObj.getString("title"),
                    image = countryObj.getString("image")
                )
            )
        }
        return countries
    }
}