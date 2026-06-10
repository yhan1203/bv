/**
 * 搜索相关 API
 * 包含：搜索建议、综合搜索、分类搜索、搜索热榜等
 */
package dev.aaa1115910.biliapi.http.api

import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.entity.BiliResponse
import dev.aaa1115910.biliapi.http.entity.search.AppSearchSquareData
import dev.aaa1115910.biliapi.http.entity.search.KeywordSuggest
import dev.aaa1115910.biliapi.http.entity.search.SearchResultData
import dev.aaa1115910.biliapi.http.entity.search.SearchTendingData
import dev.aaa1115910.biliapi.http.entity.search.WebSearchSquareData
import dev.aaa1115910.biliapi.http.util.BiliAppConf
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.readRawBytes
import io.ktor.utils.io.InternalAPI
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromJsonElement

object SearchApi {
    private val client get() = BiliHttpApi.client
    private val json get() = BiliHttpApi.json

    /**
     * 获取搜索提示（Web）
     */
    suspend fun getWebSearchSquare(
        limit: Int = 10,
        platform: String? = null
    ): BiliResponse<WebSearchSquareData> =
        client.get("/x/web-interface/wbi/search/square") {
            parameter("limit", limit)
            platform?.let { parameter("platform", platform) }
        }.body()

    /**
     * 获取搜索提示（App）
     */
    suspend fun getAppSearchSquare(
        limit: Int = 10,
        platform: String? = null,
    ): BiliResponse<List<AppSearchSquareData>> =
        client.get("https://app.bilibili.com/x/v2/search/square") {
            parameter("limit", limit)
            platform?.let { parameter("platform", platform) }
            parameter("build", BiliAppConf.APP_BUILD_CODE)
        }.body()

    /**
     * 获取搜索趋势（App）
     */
    suspend fun getSearchTrendRank(limit: Int = 10): BiliResponse<SearchTendingData> =
        client.get("https://app.bilibili.com/x/v2/search/trending/ranking") {
            parameter("limit", limit)
        }.body()

    /**
     * 获取搜索关键词建议
     */
    @OptIn(InternalAPI::class)
    suspend fun getKeywordSuggest(
        term: String,
        mainVer: String = "v1",
        highlight: String? = null,
        buvid: String
    ): KeywordSuggest {
        val responseText = client.get("https://s.search.bilibili.com/main/suggest") {
            parameter("term", term)
            parameter("main_ver", mainVer)
            highlight?.let { parameter("highlight", it) }
            parameter("buvid", buvid)
        }.readRawBytes().toString(Charsets.UTF_8)
        val keywordSuggest = json.decodeFromString<KeywordSuggest>(responseText)
        val result = json.decodeFromJsonElement<KeywordSuggest.Result>(keywordSuggest.result!!)
        keywordSuggest.suggests.addAll(result.tag)
        return keywordSuggest
    }

    /**
     * 综合搜索与[keyword]相关的结果
     */
    suspend fun searchAll(
        keyword: String,
        page: Int = 1,
        tid: Int? = null,
        order: String? = null,
        duration: Int? = null,
        buvid3: String? = null
    ): BiliResponse<SearchResultData> = client.get("/x/web-interface/wbi/search/all/v2") {
        parameter("keyword", keyword)
        parameter("page", page)
        tid?.let { parameter("tids", it) }
        order?.let { parameter("order", it) }
        duration?.let { parameter("duration", it) }
        header("Cookie", "buvid3=$buvid3;")
    }.body()

    /**
     * 分类搜索与[keyword]相关的[type]类型的相关结果
     */
    suspend fun searchType(
        keyword: String,
        type: String,
        page: Int = 1,
        tid: Int? = null,
        order: String? = null,
        duration: Int? = null,
        buvid3: String? = null
    ): BiliResponse<SearchResultData> = client.get("/x/web-interface/wbi/search/type") {
        parameter("keyword", keyword)
        parameter("search_type", type)
        parameter("page", page)
        tid?.let { parameter("tids", it) }
        order?.let { parameter("order", it) }
        duration?.let { parameter("duration", it) }
        header("Cookie", "buvid3=$buvid3;")
    }.body()
}
