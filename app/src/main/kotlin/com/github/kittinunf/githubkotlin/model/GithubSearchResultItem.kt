package com.github.kittinunf.githubkotlin.model

import com.github.kittinunf.githubkotlin.extension.asSequence
import org.json.JSONArray
import org.json.JSONObject
import java.net.URL

/**
 * Created by Kittinun Vantasin on 8/29/15.
 */

data class GithubSearchResultItem(var repoName: String = "",
                                  var ownerName: String = "",
                                  var repoDescription: String = "",
                                  var stargazerCount: Int = 0,
                                  var htmlLink: String = "")

fun githubSearchResultItemFromJSONObject(json: JSONObject): GithubSearchResultItem {
    return json.asSequence().fold(GithubSearchResultItem()) { result, pair ->
        val (key, value) = pair
        if (key == "name") {
            result.repoName = value as String
        } else if (key == "owner") {
            result.ownerName = (value as JSONObject).getString("login")
        } else if (key == "description") {
            result.repoDescription = value as String
        } else if (key == "stargazers_count") {
            result.stargazerCount = value as Int
        } else if (key == "html_url") {
            result.htmlLink = value as String
        }
        result
    }
}

fun githubSearchResultItemsFromJsonArray(json: JSONArray): List<GithubSearchResultItem> {
    return json.asSequence().fold(arrayListOf<GithubSearchResultItem>()) { list, item ->
        list.add(githubSearchResultItemFromJSONObject(item as JSONObject))
        list
    }
}