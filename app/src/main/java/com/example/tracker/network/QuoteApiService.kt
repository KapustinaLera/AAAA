package com.example.tracker.network // <-- Пакет network!

import retrofit2.http.GET
import com.google.gson.annotations.SerializedName

data class QuoteResponse(
    @SerializedName("q") val quoteText: String,
    @SerializedName("a") val quoteAuthor: String
)

interface QuoteApiService {
    @GET("api/random")
    suspend fun getRandomQuote(): List<QuoteResponse>
}