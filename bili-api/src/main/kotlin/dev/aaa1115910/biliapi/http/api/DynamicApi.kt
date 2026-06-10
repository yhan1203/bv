/**
 * 动态相关 API
 * 包含：动态列表、动态详情
 */
package dev.aaa1115910.biliapi.http.api

import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.entity.BiliResponse
import dev.aaa1115910.biliapi.http.entity.dynamic.DynamicData
import dev.aaa1115910.biliapi.http.entity.dynamic.DynamicDetailData
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.statement.body

object DynamicApi {
    private val client get() = BiliHttpApi.client

    /**
     * 获取动态列表
     */
    suspend fun getDynamicList(
        timezoneOffset: Int = -480,
        type: String = "all",
        page: Int = 1,
        offset: String? = null,
        sessData: String = ""
    ): BiliResponse<DynamicData> = client.get("/x/polymer/web-dynamic/v1/feed/all") {
        parameter("timezone_offset", timezoneOffset)
        parameter("type", type)
        parameter("page", page)
        offset?.let { parameter("offset", offset) }
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取动态详情
     */
    suspend fun getDynamicDetail(
        timezoneOffset: Int = -480,
        id: String,
        features: String? = null,
        sessData: String = ""
    ): BiliResponse<DynamicDetailData> = client.get("/x/polymer/web-dynamic/v1/detail") {
        parameter("timezone_offset", timezoneOffset)
        parameter("id", id)
        features?.let { parameter("features", it) }
        header("Cookie", "SESSDATA=$sessData;")
    }.body()
}
