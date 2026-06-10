/**
 * PGC（番剧/影视）相关 API
 * 包含：剧集信息、追番/追剧、时间表、索引等
 */
package dev.aaa1115910.biliapi.http.api

import dev.aaa1115910.biliapi.entity.pgc.PgcType
import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.entity.BiliResponse
import dev.aaa1115910.biliapi.http.entity.index.IndexResultData
import dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedData
import dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedV3Data
import dev.aaa1115910.biliapi.http.entity.pgc.PgcWebInitialStateData
import dev.aaa1115910.biliapi.http.entity.season.AppSeasonData
import dev.aaa1115910.biliapi.http.entity.season.FollowingSeasonAppData
import dev.aaa1115910.biliapi.http.entity.season.FollowingSeasonWebData
import dev.aaa1115910.biliapi.http.entity.season.SeasonFollowData
import dev.aaa1115910.biliapi.http.entity.season.WebSeasonData
import dev.aaa1115910.biliapi.http.entity.video.PlayUrlData
import dev.aaa1115910.biliapi.http.entity.video.PlayUrlV2Data
import dev.aaa1115910.biliapi.http.entity.video.Timeline
import dev.aaa1115910.biliapi.http.entity.video.TimelineAppData
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.body
import io.ktor.http.Parameters
import org.jsoup.nodes.Document

object PgcApi {
    private val client get() = BiliHttpApi.client
    private val json get() = BiliHttpApi.json

    /**
     * 获取剧集视频流
     */
    suspend fun getPgcVideoPlayUrl(
        av: Long? = null,
        bv: String? = null,
        epid: Int? = null,
        cid: Long? = null,
        qn: Int? = null,
        fnval: Int? = null,
        fnver: Int? = null,
        fourk: Int? = null,
        session: String? = null,
        supportMultiAudio: Boolean? = null,
        drmTechType: Int? = null,
        fromClient: String? = null,
        sessData: String? = null,
        dedeUserID: Long? = null
    ): BiliResponse<PlayUrlData> = client.get("/pgc/player/web/playurl") {
        require(av != null || bv != null) { "av and bv cannot be null at the same time" }
        require(epid != null || cid != null) { "epid and cid cannot be null at the same time" }
        av?.let { parameter("avid", it) }
        bv?.let { parameter("bvid", it) }
        epid?.let { parameter("ep_id", it) }
        cid?.let { parameter("cid", it) }
        qn?.let { parameter("qn", it) }
        fnval?.let { parameter("fnval", it) }
        fnver?.let { parameter("fnver", it) }
        fourk?.let { parameter("fourk", it) }
        session?.let { parameter("session", it) }
        supportMultiAudio?.let { parameter("support_multi_audio", it) }
        drmTechType?.let { parameter("drm_tech_type", it) }
        fromClient?.let { parameter("from_client", it) }
        sessData?.let { header("Cookie", "SESSDATA=$sessData;DedeUserID=$dedeUserID") }
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 获取剧集视频流 v2
     */
    suspend fun getPgcVideoPlayUrlV2(
        av: Long? = null,
        bv: String? = null,
        epid: Int? = null,
        cid: Long? = null,
        qn: Int? = null,
        fnval: Int? = null,
        fnver: Int? = null,
        fourk: Int? = null,
        session: String? = null,
        supportMultiAudio: Boolean? = null,
        drmTechType: Int? = null,
        fromClient: String? = null,
        sessData: String? = null
    ): BiliResponse<PlayUrlV2Data> = client.get("/pgc/player/web/v2/playurl") {
        av?.let { parameter("avid", it) }
        bv?.let { parameter("bvid", it) }
        epid?.let { parameter("ep_id", it) }
        cid?.let { parameter("cid", it) }
        qn?.let { parameter("qn", it) }
        fnval?.let { parameter("fnval", it) }
        fnver?.let { parameter("fnver", it) }
        fourk?.let { parameter("fourk", it) }
        session?.let { parameter("session", it) }
        supportMultiAudio?.let { parameter("support_multi_audio", it) }
        drmTechType?.let { parameter("drm_tech_type", it) }
        fromClient?.let { parameter("from_client", it) }
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 获取剧集[seasonId]或[epId]的详细信息（Web）
     */
    suspend fun getWebSeasonInfo(
        seasonId: Int? = null,
        epId: Int? = null,
        sessData: String = ""
    ): BiliResponse<WebSeasonData> = client.get("/pgc/view/web/season") {
        require(seasonId != null || epId != null) { "seasonId and epId cannot be null at the same time" }
        seasonId?.let { parameter("season_id", it) }
        epId?.let { parameter("ep_id", it) }
        header("Cookie", "SESSDATA=$sessData;")
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 获取剧集[seasonId]或[epId]的详细信息（App）
     */
    suspend fun getAppSeasonInfo(
        seasonId: Int? = null,
        epId: Int? = null,
        mobiApp: String,
        adExtra: String? = null,
        autoPlay: Int? = null,
        build: Int? = null,
        cLocale: String? = null,
        channel: String? = null,
        disableRcmd: Int? = null,
        fromAv: String? = null,
        fromSpmid: String? = null,
        isShowAllSeries: Int? = null,
        platform: String? = null,
        sLocale: String? = null,
        spmid: String? = null,
        statistics: String? = null,
        trackPath: String? = null,
        trackid: String? = null,
        ts: Int? = null,
        accessKey: String? = ""
    ): BiliResponse<AppSeasonData> = client.get("/pgc/view/v2/app/season") {
        require(seasonId != null || epId != null) { "seasonId and epId cannot be null at the same time" }
        seasonId?.let { parameter("season_id", it) }
        epId?.let { parameter("ep_id", it) }
        parameter("mobi_app", mobiApp)
        adExtra?.let { parameter("ad_extra", it) }
        autoPlay?.let { parameter("auto_play", it) }
        build?.let { parameter("build", it) }
        cLocale?.let { parameter("c_locale", it) }
        channel?.let { parameter("channel", it) }
        disableRcmd?.let { parameter("disable_rcmd", it) }
        fromAv?.let { parameter("from_av", it) }
        fromSpmid?.let { parameter("from_spmid", it) }
        isShowAllSeries?.let { parameter("is_show_all_series", it) }
        platform?.let { parameter("platform", it) }
        sLocale?.let { parameter("s_locale", it) }
        spmid?.let { parameter("spmid", it) }
        statistics?.let { parameter("statistics", it) }
        trackPath?.let { parameter("track_path", it) }
        trackid?.let { parameter("trackid", it) }
        ts?.let { parameter("ts", it) }
        accessKey?.let { parameter("access_key", accessKey) }
    }.body()

    /**
     * 添加番剧[seasonId]的追番（Web）
     */
    suspend fun addSeasonFollow(
        seasonId: Int,
        csrf: String,
        sessData: String
    ): BiliResponse<SeasonFollowData> = client.post("/pgc/web/follow/add") {
        setBody(
            FormDataContent(
                Parameters.build {
                    append("season_id", "$seasonId")
                    append("csrf", csrf)
                }
            ))
        header("Cookie", "SESSDATA=$sessData;")
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 添加番剧[seasonId]的追番（App）
     */
    suspend fun addSeasonFollow(
        seasonId: Int,
        accessKey: String
    ): BiliResponse<SeasonFollowData> = client.post("/pgc/app/follow/add") {
        setBody(
            FormDataContent(
                Parameters.build {
                    append("season_id", "$seasonId")
                    append("access_key", accessKey)
                }
            ))
    }.body()

    /**
     * 取消番剧[seasonId]的追番（Web）
     */
    suspend fun delSeasonFollow(
        seasonId: Int,
        csrf: String,
        sessData: String
    ): BiliResponse<SeasonFollowData> = client.post("/pgc/web/follow/del") {
        setBody(
            FormDataContent(
                Parameters.build {
                    append("season_id", "$seasonId")
                    append("csrf", csrf)
                }
            ))
        header("Cookie", "SESSDATA=$sessData;")
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 取消番剧[seasonId]的追番（App）
     */
    suspend fun delSeasonFollow(
        seasonId: Int,
        accessKey: String
    ): BiliResponse<SeasonFollowData> = client.post("/pgc/app/follow/del") {
        setBody(
            FormDataContent(
                Parameters.build {
                    append("season_id", "$seasonId")
                    append("access_key", accessKey)
                }
            ))
    }.body()

    /**
     * 获取剧集[seasonId]的用户状态
     */
    suspend fun getSeasonUserStatus(
        seasonId: Int,
        sessData: String
    ): BiliResponse<WebSeasonData.UserStatus> = client.get("/pgc/view/web/season/user/status") {
        parameter("season_id", seasonId)
        header("Cookie", "SESSDATA=$sessData;")
        header("referer", "https://www.bilibili.com")
    }.body()

    /**
     * 获取剧集更新时间表（Web）
     */
    suspend fun getTimeline(
        type: Int,
        before: Int,
        after: Int
    ): BiliResponse<List<Timeline>> = client.get("/pgc/web/timeline") {
        require(before in 0..7) { "before must in [0,7]" }
        require(after in 0..7) { "after must in [0,7]" }
        parameter("types", type)
        parameter("before", before)
        parameter("after", after)
    }.body()

    /**
     * 获取剧集更新时间表（App）
     */
    suspend fun getTimeline(filterType: Int): BiliResponse<TimelineAppData> =
        client.get("/pgc/app/timeline") {
            parameter("filter_type", filterType)
            parameter("access_key", "")
        }.body()

    /** 获取番剧首页数据 */
    suspend fun getPgcWebInitialStateData(pgcType: PgcType): PgcWebInitialStateData {
        val path = pgcType.name.lowercase()
        val htmlDocuments = client.get("https://www.bilibili.com/$path").body<Document>()

        val dataScriptTagContent = htmlDocuments.body().select("script").find {
            it.html().contains("__INITIAL_STATE__")
        }?.html() ?: throw IllegalStateException("initial state data cannot be null")
        val dataJson =
            dataScriptTagContent.split("__INITIAL_STATE__=", ";(function()")[1]
        val initinalData = runCatching {
            json.decodeFromString<PgcWebInitialStateData>(dataJson)
        }.onFailure {
            println("parse initial state data failed: ${it.stackTraceToString()}")
        }.getOrNull() ?: throw IllegalStateException("parse initial state data failed")
        return initinalData
    }

    /**
     * 获取 PGC 猜你喜欢（V3）
     */
    suspend fun getPgcFeedV3(
        name: String = "anime",
        cursor: Int = 0
    ): BiliResponse<PgcFeedV3Data> = client.get("/pgc/page/web/v3/feed") {
        parameter("name", name)
        parameter("coursor", cursor)
    }.body()

    /**
     * 获取 PGC 猜你喜欢
     */
    suspend fun getPgcFeed(
        name: String = "movie",
        cursor: Int = 0
    ): BiliResponse<PgcFeedData> = client.get("/pgc/page/web/feed") {
        parameter("name", name)
        parameter("coursor", cursor)
        parameter("new_cursor_status", true)
    }.body()

    /**
     * 获取用户[mid]的追剧列表（Web）
     */
    suspend fun getFollowingSeasons(
        type: Int,
        status: Int,
        pageNumber: Int = 1,
        pageSize: Int = 15,
        mid: Long,
        sessData: String? = ""
    ): BiliResponse<FollowingSeasonWebData> = client.get("/x/space/bangumi/follow/list") {
        parameter("type", type)
        parameter("follow_status", status)
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
        parameter("vmid", mid)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取用户的追剧列表（App）
     */
    suspend fun getFollowingSeasons(
        type: String,
        status: Int,
        pageNumber: Int = 1,
        pageSize: Int = 15,
        build: Int,
        accessKey: String
    ): BiliResponse<FollowingSeasonAppData> = client.get("/pgc/app/follow/v2/$type") {
        parameter("status", status)
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
        parameter("build", build)
        parameter("access_key", accessKey)
    }.body()

    /**
     * 番剧索引结果（内部方法）
     */
    private suspend fun seasonIndexResult(
        seasonIndexType: SeasonIndexType,
        order: Int? = null,
        seasonVersion: Int? = null,
        spokenLanguageType: Int? = null,
        area: Int? = null,
        isFinish: Int? = null,
        copyright: Int? = null,
        seasonStatus: Int? = null,
        seasonMonth: Int? = null,
        year: String? = null,
        releaseDate: String? = null,
        styleId: Int? = null,
        producerId: Int? = null,
        sort: Int? = null,
        page: Int? = null,
        pagesize: Int? = null,
        type: Int? = null
    ): BiliResponse<IndexResultData> = client.get("/pgc/season/index/result") {
        parameter("st", seasonIndexType.id)
        order?.let { parameter("order", it) }
        seasonVersion?.let { parameter("season_version", it) }
        spokenLanguageType?.let { parameter("spoken_language_type", it) }
        area?.let { parameter("area", it) }
        isFinish?.let { parameter("is_finish", it) }
        copyright?.let { parameter("copyright", it) }
        seasonStatus?.let { parameter("season_status", it) }
        seasonMonth?.let { parameter("season_month", it) }
        year?.let { parameter("year", it) }
        releaseDate?.let { parameter("release_date", it) }
        styleId?.let { parameter("style_id", it) }
        producerId?.let { parameter("producer_id", it) }
        sort?.let { parameter("sort", it) }
        page?.let { parameter("page", it) }
        parameter("season_type", seasonIndexType.id)
        pagesize?.let { parameter("pagesize", it) }
        type?.let { parameter("type", it) }
    }.body()

    suspend fun seasonIndexAnimeResult(
        order: Int = 0, seasonVersion: Int = -1, spokenLanguageType: Int = -1,
        area: Int = -1, isFinish: Int = -1, copyright: Int = -1,
        seasonStatus: Int = -1, seasonMonth: Int = -1, year: String = "-1",
        styleId: Int = -1, sort: Int = 0, page: Int = 1, pagesize: Int = 20, type: Int = 1
    ) = seasonIndexResult(SeasonIndexType.Anime, order, seasonVersion, spokenLanguageType, area, isFinish, copyright, seasonStatus, seasonMonth, year, styleId, sort, page, pagesize, type)

    suspend fun seasonIndexGuochuangResult(
        order: Int = 0, seasonVersion: Int = -1, isFinish: Int = -1,
        copyright: Int = -1, seasonStatus: Int = -1, year: String = "-1",
        styleId: Int = -1, sort: Int = 0, page: Int = 1, pagesize: Int = 20, type: Int = 1
    ) = seasonIndexResult(SeasonIndexType.Guochuang, order, seasonVersion, null, null, isFinish, copyright, seasonStatus, null, year, null, styleId, null, sort, page, pagesize, type)

    suspend fun seasonIndexVarietyResult(
        order: Int = 0, seasonStatus: Int = -1, styleId: Int = -1,
        sort: Int = 0, page: Int = 1, pagesize: Int = 20, type: Int = 1
    ) = seasonIndexResult(SeasonIndexType.Variety, order, null, null, null, null, null, seasonStatus, null, null, null, styleId, null, sort, page, pagesize, type)

    suspend fun seasonIndexMovieResult(
        order: Int = 0, area: Int = -1, styleId: Int = -1,
        releaseDate: String = "-1", seasonStatus: Int = -1,
        sort: Int = 0, page: Int = 1, pagesize: Int = 20, type: Int = 1
    ) = seasonIndexResult(SeasonIndexType.Movie, order, null, null, area, null, null, seasonStatus, null, null, releaseDate, styleId, null, sort, page, pagesize, type)

    suspend fun seasonIndexTvResult(
        order: Int = 0, area: Int = -1, styleId: Int = -1,
        releaseDate: String = "-1", seasonStatus: Int = -1,
        sort: Int = 0, page: Int = 1, pagesize: Int = 20, type: Int = 1
    ) = seasonIndexResult(SeasonIndexType.Tv, order, null, null, area, null, null, seasonStatus, null, null, releaseDate, styleId, null, sort, page, pagesize, type)

    suspend fun seasonIndexDocumentaryResult(
        order: Int = 0, area: Int = -1, styleId: Int = -1,
        producerId: Int = -1, releaseDate: String = "-1", seasonStatus: Int = -1,
        sort: Int = 0, page: Int = 1, pagesize: Int = 20, type: Int = 1
    ) = seasonIndexResult(SeasonIndexType.Documentary, order, null, null, area, null, null, seasonStatus, null, null, releaseDate, styleId, producerId, sort, page, pagesize, type)

    /**
     * 通过 avid 获取 seasonId
     */
    suspend fun getSeasonIdByAvid(avid: Long): Int? {
        return runCatching {
            val data = getPgcVideoPlayUrlV2(av = avid).getResponseData()
            data.playViewBusinessInfo.seasonInfo.seasonId
        }.getOrNull()
    }

    /**
     * 通过 epid 获取 aid 和 cid
     */
    suspend fun getAidCidByEpid(epid: Int): Pair<Long, Long>? {
        return runCatching {
            val data = getPgcVideoPlayUrlV2(epid = epid).getResponseData()
            data.playViewBusinessInfo.episodeInfo.aid to data.playViewBusinessInfo.episodeInfo.cid
        }.getOrNull()
    }
}
