package com.github.kittinunf.githubkotlin.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.kittinunf.githubkotlin.R
import com.github.kittinunf.githubkotlin.model.GithubSearchResultItem
import com.github.kittinunf.githubkotlin.model.githubSearchResultItemsFromJsonArray
import fuel.httpGet
import org.json.JSONObject
import kotlin.properties.Delegates

import kotlinx.android.synthetic.activity_github_search.*
import kotlinx.android.synthetic.list_item_github_search.view.*

public class GithubSearchActivity : AppCompatActivity() {

    val layoutManager by Delegates.lazy { LinearLayoutManager(this) }

    val adapter = GithubSearchResultAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_github_search)

        setUIRecyclerView()

        requestGithubSearchAPI()
    }

    fun setUIRecyclerView() {
        searchRecyclerview.setLayoutManager(layoutManager)
        searchRecyclerview.setAdapter(adapter)
    }

    fun requestGithubSearchAPI() {
        val params = hashMapOf("q" to "language:kotlin", "sort" to "stars", "order" to "desc")

        //request to github api
        "https://api.github.com/search/repositories".httpGet(params).responseString { request, response, either ->
            Log.d("API", request.toString())
            Log.d("API", response.toString())

            val (error, data) = either

            //do something when error is null
            if (error == null) {
                val json = JSONObject(data)

                val results = githubSearchResultItemsFromJsonArray(json.getJSONArray("items"))
                updateUI(results)
            } else {
                Log.e("API", "There is something wrong with API, ${error.toString()}")
            }
        }
    }

    fun updateUI(items: List<GithubSearchResultItem>) {
        adapter.searchResults = items
    }

    class SearchResultViewHolder(val view: View) : RecyclerView.ViewHolder(view) {

        var clickListener: ((View) -> Unit)? = null

        init {
            view.setOnClickListener {
                clickListener?.invoke(it)
            }
        }

    }

    inner class GithubSearchResultAdapter : RecyclerView.Adapter<SearchResultViewHolder>() {

        var searchResults by Delegates.observable(listOf<GithubSearchResultItem>()) { meta, old, new -> notifyDataSetChanged() }

        override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): SearchResultViewHolder? {
            val v = LayoutInflater.from(this@GithubSearchActivity).inflate(R.layout.list_item_github_search, parent, false)
            return SearchResultViewHolder(v)
        }

        override fun getItemCount(): Int = searchResults.count()

        override fun onBindViewHolder(viewHolder: SearchResultViewHolder, position: Int) {
            val item = searchResults[position]

            viewHolder.view.searchResultTitleText.setText(item.repoName)
            viewHolder.view.searchResultSubtitleText.setText(item.ownerName)
            viewHolder.view.searchResultDescriptionText.setText(item.repoDescription)
            viewHolder.view.searchResultStargazerCount.setText(item.stargazerCount.toString())

            viewHolder.clickListener = {
                val browseIntent = Intent(Intent.ACTION_VIEW, Uri.parse(item.htmlLink));
                startActivity(browseIntent)
            }
        }

    }

}


