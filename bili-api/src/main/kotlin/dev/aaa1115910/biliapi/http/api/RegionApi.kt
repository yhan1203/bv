/**
 * 分区/UGC 相关 API
 * 包含：分区动态、分区列表、轮播图、推荐等
 */
package dev.aaa1115910.biliapi.http.api

import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.entity.BiliResponse
import dev.aaa1115910.biliapi.http.entity.region.RegionBanner
import dev.aaa1115910.biliapi.http.entity.region.RegionDynamic
import dev.aaa1115910.biliapi.http.entity.region.RegionDynamicList
import dev.aaa1115910.biliapi.http.entity.region.RegionFeedRcmd
import dev.aaa1115910.biliapi.http.entity.region.RegionLocs
import dev.aaa1115910.biliapi.http.util.BiliAppConf
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.body
import io.ktor.client.request.header
import io.ktor.client.request.parameter

object RegionApi {
    private val client get() = BiliHttpApi.client

    /**
     * 获取分区动态（App）
     */
    suspend fun getRegionDynamic(
        rid: Int,
        accessKey: String
    ): BiliResponse<RegionDynamic> = client.get("https://app.bilibili.com/x/v2/region/dynamic") {
        parameter("access_key", accessKey)
        parameter("build", BiliAppConf.APP_BUILD_CODE)
        parameter("rid", rid)
    }.body()

    /**
     * 获取分区视频列表（App）
     */
    suspend fun getRegionDynamicList(
        rid: Int,
        ctime: Long = 0,
        accessKey: String
    ): BiliResponse<RegionDynamicList> =
        client.get("https://app.bilibili.com/x/v2/region/dynamic/list") {
            parameter("access_key", accessKey)
            parameter("build", BiliAppConf.APP_BUILD_CODE)
            parameter("rid", rid)
            parameter("ctime", ctime)
            parameter("pull", "false")
        }.body()

    /**
     * 获取分区内各种插入的banner（Web）
     */
    suspend fun getLocs(
        ids: List<Int>,
        sessData: String? = null
    ): RegionLocs = client.get("/x/web-show/res/locs") {
        parameter("ids", ids.joinToString(","))
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()

    /**
     * 获取 UGC 分区轮播图
     */
    suspend fun getRegionBanner(regionId: Int): BiliResponse<RegionBanner> =
        client.get("/x/web-show/region/banner") {
            parameter("region_id", regionId)
        }.body()

    /**
     * 获取 UGC 分区推荐视频
     */
    suspend fun getRegionFeedRcmd(
        displayId: Int,
        requestCnt: Int = 15,
        fromRegion: Int,
        device: String = "web",
        plat: Int = 30,
        sessData: String? = null
    ): BiliResponse<RegionFeedRcmd> = client.get("/x/web-interface/region/feed/rcmd") {
        parameter("display_id", displayId)
        parameter("request_cnt", requestCnt)
        parameter("from_region", fromRegion)
        parameter("device", device)
        parameter("plat", plat)
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()
}
