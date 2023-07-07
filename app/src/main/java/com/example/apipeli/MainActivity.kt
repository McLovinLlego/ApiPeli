package com.example.apipeli

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException


class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = MyAdapter()
        recyclerView.adapter = adapter

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("https://unogs-unogs-v1.p.rapidapi.com/search/titles?order_by=date&type=movie")
            .get()
            .addHeader("X-RapidAPI-Key", "9a72d0612fmsha7efb757da8b11ep160d05jsn757cd4fdc71b")
            .addHeader("X-RapidAPI-Host", "unogs-unogs-v1.p.rapidapi.com")
            .build()

        client.newCall(request).enqueue(object : okhttp3.Callback {
            override fun onFailure(call: okhttp3.Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: okhttp3.Call, response: Response) {
                val jsonData = response.body?.string()
                val movies = parseJson(jsonData)
                runOnUiThread {
                    adapter.setData(movies)
                }
            }
        })
    }

    private fun parseJson(jsonData: String?): List<Movie> {
        val movies = mutableListOf<Movie>()
        val jsonObject = JSONObject(jsonData)
        val resultsArray = jsonObject.getJSONArray("results")
        var count = 0
        for (i in 0 until resultsArray.length()) {
            if (count >= 50) break
            val movieObject = resultsArray.getJSONObject(i)
            val title = movieObject.getString("title")
            val year = movieObject.getInt("year")
            val movie = Movie(title, year)
            movies.add(movie)
            count++
        }
        return movies
    }
}
