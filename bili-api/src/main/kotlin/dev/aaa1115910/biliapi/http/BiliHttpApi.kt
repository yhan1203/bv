/**
 * Bilibili HTTP API 委派入口
 *
 * 所有方法已按业务领域拆分到 api/ 子目录下对应的文件中：
 * - VideoApi — 视频相关（播放、信息、推荐、收藏、标签、弹幕、评论）
 * - UserApi — 用户相关（个人信息、收藏、关注、历史记录等）
 * - SearchApi — 搜索
 * - PgcApi — 番剧/影视(PGC)
 * - RegionApi — 分区/UGC
 * - DynamicApi — 动态
 *
 * 本文件保留所有原有方法签名并委派到对应的 API 对象，以保证向后兼容。
 */
@file:Suppress("unused", "SpellCheckingInspection")

package dev.aaa1115910.biliapi.http

import com.tfowl.ktor.client.plugins.JsoupPlugin
import dev.aaa1115910.biliapi.http.api.DynamicApi
import dev.aaa1115910.biliapi.http.api.PgcApi
import dev.aaa1115910.biliapi.http.api.RegionApi
import dev.aaa1115910.biliapi.http.api.SearchApi
import dev.aaa1115910.biliapi.http.api.UserApi
import dev.aaa1115910.biliapi.http.api.VideoApi
import dev.aaa1115910.biliapi.http.entity.BiliResponse
import dev.aaa1115910.biliapi.http.entity.BiliResponseWithoutData
import dev.aaa1115910.biliapi.http.entity.danmaku.DanmakuResponse
import dev.aaa1115910.biliapi.http.entity.dynamic.DynamicData
import dev.aaa1115910.biliapi.http.entity.dynamic.DynamicDetailData
import dev.aaa1115910.biliapi.http.entity.history.HistoryData
import dev.aaa1115910.biliapi.http.entity.home.RcmdIndexData
import dev.aaa1115910.biliapi.http.entity.home.RcmdTopData
import dev.aaa1115910.biliapi.http.entity.index.IndexResultData
import dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedData
import dev.aaa1115910.biliapi.http.entity.pgc.PgcFeedV3Data
import dev.aaa1115910.biliapi.http.entity.pgc.PgcWebInitialStateData
import dev.aaa1115910.biliapi.http.entity.region.RegionBanner
import dev.aaa1115910.biliapi.http.entity.region.RegionDynamic
import dev.aaa1115910.biliapi.http.entity.region.RegionDynamicList
import dev.aaa1115910.biliapi.http.entity.region.RegionFeedRcmd
import dev.aaa1115910.biliapi.http.entity.region.RegionLocs
import dev.aaa1115910.biliapi.http.entity.reply.CommentData
import dev.aaa1115910.biliapi.http.entity.reply.CommentReplyData
import dev.aaa1115910.biliapi.http.entity.search.AppSearchSquareData
import dev.aaa1115910.biliapi.http.entity.search.KeywordSuggest
import dev.aaa1115910.biliapi.http.entity.search.SearchResultData
import dev.aaa1115910.biliapi.http.entity.search.SearchTendingData
import dev.aaa1115910.biliapi.http.entity.search.WebSearchSquareData
import dev.aaa1115910.biliapi.http.entity.season.AppSeasonData
import dev.aaa1115910.biliapi.http.entity.season.FollowingSeasonAppData
import dev.aaa1115910.biliapi.http.entity.season.FollowingSeasonWebData
import dev.aaa1115910.biliapi.http.entity.season.SeasonFollowData
import dev.aaa1115910.biliapi.http.entity.season.WebSeasonData
import dev.aaa1115910.biliapi.http.entity.toview.ToViewData
import dev.aaa1115910.biliapi.http.entity.user.AppSpaceVideoData
import dev.aaa1115910.biliapi.http.entity.user.FollowAction
import dev.aaa1115910.biliapi.http.entity.user.FollowActionSource
import dev.aaa1115910.biliapi.http.entity.user.MyInfoData
import dev.aaa1115910.biliapi.http.entity.user.RelationData
import dev.aaa1115910.biliapi.http.entity.user.RelationStat
import dev.aaa1115910.biliapi.http.entity.user.UserCardData
import dev.aaa1115910.biliapi.http.entity.user.UserFollowData
import dev.aaa1115910.biliapi.http.entity.user.UserInfoData
import dev.aaa1115910.biliapi.http.entity.user.WebSpaceVideoData
import dev.aaa1115910.biliapi.http.entity.user.favorite.FavoriteFolderInfo
import dev.aaa1115910.biliapi.http.entity.user.favorite.FavoriteFolderInfoListData
import dev.aaa1115910.biliapi.http.entity.user.favorite.FavoriteItemIdListResponse
import dev.aaa1115910.biliapi.http.entity.user.favorite.UserFavoriteFoldersData
import dev.aaa1115910.biliapi.http.entity.user.garb.Equip
import dev.aaa1115910.biliapi.http.entity.user.garb.EquipPart
import dev.aaa1115910.biliapi.http.entity.video.AddCoin
import dev.aaa1115910.biliapi.http.entity.video.PlayUrlData
import dev.aaa1115910.biliapi.http.entity.video.PlayUrlV2Data
import dev.aaa1115910.biliapi.http.entity.video.PopularVideoData
import dev.aaa1115910.biliapi.http.entity.video.RelatedVideosResponse
import dev.aaa1115910.biliapi.http.entity.video.Tag
import dev.aaa1115910.biliapi.http.entity.video.TagDetail
import dev.aaa1115910.biliapi.http.entity.video.TagTopVideosResponse
import dev.aaa1115910.biliapi.http.entity.video.Timeline
import dev.aaa1115910.biliapi.http.entity.video.TimelineAppData
import dev.aaa1115910.biliapi.http.entity.video.VideoDetail
import dev.aaa1115910.biliapi.http.entity.video.VideoInfo
import dev.aaa1115910.biliapi.http.entity.video.VideoMoreInfo
import dev.aaa1115910.biliapi.http.entity.video.VideoShot
import dev.aaa1115910.biliapi.http.entity.web.NavResponseData
import dev.aaa1115910.biliapi.http.plugins.BiliUserAgent
import dev.aaa1115910.biliapi.http.util.checkToken
import dev.aaa1115910.biliapi.http.util.encApiSign
import dev.aaa1115910.biliapi.entity.pgc.PgcType
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.HttpRequestRetry
import io.ktor.client.plugins.compression.ContentEncoding
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import io.ktor.utils.io.InternalAPI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@Suppress("SpellCheckingInspection")
object BiliHttpApi {
    internal var endPoint: String = "api.bilibili.com"
    internal lateinit var client: HttpClient

    internal val json = Json {
        coerceInputValues = true
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    var wbiImgKey: String? = null
    var wbiSubKey: String? = null
    private var wbiLastRefreshDate = 0L

    init {
        createClient()
        CoroutineScope(Dispatchers.IO).launch {
            updateWbi()
        }
    }

    private fun createClient() {
        client = HttpClient(OkHttp) {
            BiliUserAgent()
            install(ContentNegotiation) {
                json(json)
            }
            install(ContentEncoding) {
                deflate(1.0F)
                gzip(0.9F)
            }
            install(HttpRequestRetry) {
                retryOnException(maxRetries = 2)
            }
            install(JsoupPlugin)
            defaultRequest {
                url {
                    host = endPoint
                    protocol = URLProtocol.HTTPS
                }
            }
        }.apply {
            encApiSign()
        }
    }

    // ==================== 基础设施 ====================

    /**
     * 更新 wbi keys
     */
    suspend fun updateWbi() {
        val needToUpdate =
            wbiImgKey == null || wbiSubKey == null || System.currentTimeMillis() - wbiLastRefreshDate < 2 * 60 * 60 * 1000L
        if (!needToUpdate) {
            println("Skip update wbi keys")
            return
        }

        println("Updating wbi keys...")
        runCatching {
            val wbiData = getWebInterfaceNav().data!!.wbiImg
            wbiImgKey = wbiData.getImgKey()
            wbiSubKey = wbiData.getSubKey()
            wbiLastRefreshDate = System.currentTimeMillis()
        }.onSuccess {
            println("Update wbi data success")
        }.onFailure {
            println("Update wbi data failed: ${it.stackTraceToString()}")
        }
    }

    /** 通用文件下载 */
    suspend fun download(url: String): ByteArray = VideoApi.download(url)

    // ==================== 视频 ====================

    suspend fun getPopularVideoData(
        pageNumber: Int = 1, pageSize: Int = 20, sessData: String = ""
    ): BiliResponse<PopularVideoData> = VideoApi.getPopularVideoData(pageNumber, pageSize, sessData)

    suspend fun getVideoInfo(
        av: Int? = null, bv: String? = null, sessData: String? = null
    ): BiliResponse<VideoInfo> = VideoApi.getVideoInfo(av, bv, sessData)

    suspend fun getVideoDetail(
        av: Long? = null, bv: String? = null, sessData: String? = null
    ): BiliResponse<VideoDetail> = VideoApi.getVideoDetail(av, bv, sessData)

    suspend fun getVideoPlayUrl(
        av: Long? = null, bv: String? = null, cid: Long, qn: Int? = null, fnval: Int? = null,
        fnver: Int? = null, fourk: Int? = 0, session: String? = null, otype: String = "json",
        type: String = "", platform: String = "oc", sessData: String? = null,
        dedeUserID: Long? = null
    ): BiliResponse<PlayUrlData> = VideoApi.getVideoPlayUrl(av, bv, cid, qn, fnval, fnver, fourk, session, otype, type, platform, sessData, dedeUserID)

    suspend fun getDanmakuXml(cid: Long, sessData: String = ""): DanmakuResponse =
        VideoApi.getDanmakuXml(cid, sessData)

    suspend fun getRelatedVideos(avid: Long? = null, bvid: String? = null): RelatedVideosResponse =
        VideoApi.getRelatedVideos(avid, bvid)

    suspend fun getVideoMoreInfo(avid: Long, cid: Long, sessData: String, buvid3: String): BiliResponse<VideoMoreInfo> =
        VideoApi.getVideoMoreInfo(avid, cid, sessData, buvid3)

    suspend fun sendVideoLike(
        avid: Long? = null, bvid: String? = null, like: Boolean = true, csrf: String, sessData: String
    ): Pair<Boolean, String> = VideoApi.sendVideoLike(avid, bvid, like, csrf, sessData)

    suspend fun checkVideoLiked(avid: Long? = null, bvid: String? = null, sessData: String): Boolean =
        VideoApi.checkVideoLiked(avid, bvid, sessData)

    suspend fun sendVideoCoin(
        avid: Long? = null, bvid: String? = null, multiply: Int = 1, like: Boolean = false,
        csrf: String, sessData: String
    ): Pair<Boolean, String> = VideoApi.sendVideoCoin(avid, bvid, multiply, like, csrf, sessData)

    suspend fun checkVideoSentCoin(avid: Long? = null, bvid: String? = null, sessData: String): Boolean =
        VideoApi.checkVideoSentCoin(avid, bvid, sessData)

    suspend fun setVideoToFavorite(
        avid: Long, type: Int = 2, addMediaIds: List<Long> = listOf(), delMediaIds: List<Long> = listOf(),
        accessKey: String? = null, csrf: String? = null, sessData: String? = null
    ) = VideoApi.setVideoToFavorite(avid, type, addMediaIds, delMediaIds, accessKey, csrf, sessData)

    suspend fun checkVideoFavoured(avid: Long, accessKey: String? = null, sessData: String? = null): Boolean =
        VideoApi.checkVideoFavoured(avid, accessKey, sessData)

    suspend fun getVideoTags(avid: Long? = null, bvid: String? = null, sessData: String = ""): BiliResponse<List<Tag>> =
        VideoApi.getVideoTags(avid, bvid, sessData)

    suspend fun getTagDetail(tagId: Int, pageNumber: Int, pageSize: Int): BiliResponse<TagDetail> =
        VideoApi.getTagDetail(tagId, pageNumber, pageSize)

    suspend fun getTagTopVideos(tagId: Int, pageNumber: Int, pageSize: Int): TagTopVideosResponse =
        VideoApi.getTagTopVideos(tagId, pageNumber, pageSize)

    suspend fun getFeedRcmd(freshType: Int = 4, pageSize: Int = 30, idx: Int = 1, sessData: String? = null): BiliResponse<RcmdTopData> =
        VideoApi.getFeedRcmd(freshType, pageSize, idx, sessData)

    suspend fun getFeedIndex(idx: Int = 0, accessKey: String? = null): BiliResponse<RcmdIndexData> =
        VideoApi.getFeedIndex(idx, accessKey)

    suspend fun sendHeartbeat(
        avid: Long? = null, bvid: String? = null, cid: Long? = null, epid: Int? = null,
        sid: Int? = null, mid: Long? = null, playedTime: Int? = null, realtime: Int? = null,
        startTs: Long? = null, type: Int? = null, subType: Int? = null, dt: Int? = null,
        playType: Int? = null, csrf: String? = null, sessData: String
    ): String = VideoApi.sendHeartbeat(avid, bvid, cid, epid, sid, mid, playedTime, realtime, startTs, type, subType, dt, playType, csrf, sessData)

    suspend fun sendHeartbeat(
        avid: Long? = null, bvid: String? = null, cid: Long? = null, epid: Int? = null,
        sid: Int? = null, mid: Long? = null, playedTime: Int? = null, realtime: Int? = null,
        startTs: Long? = null, type: Int? = null, subType: Int? = null, dt: Int? = null,
        playType: Int? = null, accessKey: String? = null
    ): String = VideoApi.sendHeartbeat(avid, bvid, cid, epid, sid, mid, playedTime, realtime, startTs, type, subType, dt, playType, accessKey)

    suspend fun getWebVideoShot(aid: Long? = null, bvid: String? = null, cid: Long? = null, needJsonArrayIndex: Boolean = false): BiliResponse<VideoShot> =
        VideoApi.getWebVideoShot(aid, bvid, cid, needJsonArrayIndex)

    suspend fun getAppVideoShot(aid: Long, cid: Long): BiliResponse<VideoShot> =
        VideoApi.getAppVideoShot(aid, cid)

    suspend fun getRegionBanner(regionId: Int): BiliResponse<RegionBanner> =
        RegionApi.getRegionBanner(regionId)

    suspend fun getRegionFeedRcmd(
        displayId: Int, requestCnt: Int = 15, fromRegion: Int, device: String = "web",
        plat: Int = 30, sessData: String? = null
    ): BiliResponse<RegionFeedRcmd> = RegionApi.getRegionFeedRcmd(displayId, requestCnt, fromRegion, device, plat, sessData)

    suspend fun getComments(
        type: Long, oid: Long, mode: Int = 3, paginationStr: String = """{"offset":""}""",
        sessData: String? = null, buvid3: String? = null
    ): BiliResponse<CommentData> = VideoApi.getComments(type, oid, mode, paginationStr, sessData, buvid3)

    suspend fun getCommentReplies(oid: Long, type: Long, root: Long, pageSize: Int = 10, pageNumber: Int = 1): BiliResponse<CommentReplyData> =
        VideoApi.getCommentReplies(oid, type, root, pageSize, pageNumber)

    // ==================== 用户 ====================

    suspend fun getUserInfo(uid: Long, sessData: String = ""): BiliResponse<UserInfoData> =
        UserApi.getUserInfo(uid, sessData)

    suspend fun getUserCardInfo(uid: Long, photo: Boolean = false, sessData: String = ""): BiliResponse<UserCardData> =
        UserApi.getUserCardInfo(uid, photo, sessData)

    suspend fun getUserSelfInfo(sessData: String = ""): BiliResponse<MyInfoData> =
        UserApi.getUserSelfInfo(sessData)

    suspend fun getHistories(max: Long = 0, business: String = "", viewAt: Long = 0, pageSize: Int = 20, sessData: String = ""): BiliResponse<HistoryData> =
        UserApi.getHistories(max, business, viewAt, pageSize, sessData)

    suspend fun getToView(sessData: String = ""): BiliResponse<ToViewData> =
        UserApi.getToView(sessData)

    suspend fun getFavoriteFolderInfo(mediaId: Long, accessKey: String? = null, sessData: String? = null): BiliResponse<FavoriteFolderInfo> =
        UserApi.getFavoriteFolderInfo(mediaId, accessKey, sessData)

    suspend fun getAllFavoriteFoldersInfo(mid: Long, type: Int = 0, rid: Long? = null, accessKey: String? = null, sessData: String? = null): BiliResponse<UserFavoriteFoldersData> =
        UserApi.getAllFavoriteFoldersInfo(mid, type, rid, accessKey, sessData)

    suspend fun getFavoriteList(
        mediaId: Long, tid: Int = 0, keyword: String? = null, order: String? = null, type: Int = 0,
        pageSize: Int = 20, pageNumber: Int = 1, platform: String? = null,
        accessKey: String? = null, sessData: String? = null
    ): BiliResponse<FavoriteFolderInfoListData> = UserApi.getFavoriteList(mediaId, tid, keyword, order, type, pageSize, pageNumber, platform, accessKey, sessData)

    suspend fun getFavoriteIdList(mediaId: Long, platform: String? = null, accessKey: String? = null, sessData: String? = null): FavoriteItemIdListResponse =
        UserApi.getFavoriteIdList(mediaId, platform, accessKey, sessData)

    suspend fun getWebUserSpaceVideos(mid: Long, order: String = "pubdate", tid: Int = 0, keyword: String? = null, pageNumber: Int = 1, pageSize: Int = 30, sessData: String): BiliResponse<WebSpaceVideoData> =
        UserApi.getWebUserSpaceVideos(mid, order, tid, keyword, pageNumber, pageSize, sessData)

    suspend fun getAppUserSpaceVideos(mid: Long, lastAvid: Long, order: String = "pubdate", ts: Long, accessKey: String): BiliResponse<AppSpaceVideoData> =
        UserApi.getAppUserSpaceVideos(mid, lastAvid, order, ts, accessKey)

    suspend fun getUserFollow(mid: Long, orderType: String? = null, pageSize: Int = 50, pageNumber: Int = 1, accessKey: String? = null, sessData: String? = null): BiliResponse<UserFollowData> =
        UserApi.getUserFollow(mid, orderType, pageSize, pageNumber, accessKey, sessData)

    suspend fun modifyFollow(mid: Long, action: FollowAction, actionSource: FollowActionSource, accessKey: String? = null, csrf: String? = null, sessData: String? = null): BiliResponseWithoutData =
        UserApi.modifyFollow(mid, action, actionSource, accessKey, csrf, sessData)

    suspend fun getRelations(mid: Long, accessKey: String? = null, sessData: String? = null): BiliResponse<RelationData> =
        UserApi.getRelations(mid, accessKey, sessData)

    suspend fun getRelationStat(mid: Long, accessKey: String? = null, sessData: String? = null): BiliResponse<RelationStat> =
        UserApi.getRelationStat(mid, accessKey, sessData)

    suspend fun getUserEquippedGarb(part: EquipPart, sessData: String): BiliResponse<Equip> =
        UserApi.getUserEquippedGarb(part, sessData)

    suspend fun getWebInterfaceNav(): BiliResponse<NavResponseData> =
        UserApi.getWebInterfaceNav()

    // ==================== 搜索 ====================

    suspend fun getWebSearchSquare(limit: Int = 10, platform: String? = null): BiliResponse<WebSearchSquareData> =
        SearchApi.getWebSearchSquare(limit, platform)

    suspend fun getAppSearchSquare(limit: Int = 10, platform: String? = null): BiliResponse<List<AppSearchSquareData>> =
        SearchApi.getAppSearchSquare(limit, platform)

    suspend fun getSearchTrendRank(limit: Int = 10): BiliResponse<SearchTendingData> =
        SearchApi.getSearchTrendRank(limit)

    @OptIn(InternalAPI::class)
    suspend fun getKeywordSuggest(term: String, mainVer: String = "v1", highlight: String? = null, buvid: String): KeywordSuggest =
        SearchApi.getKeywordSuggest(term, mainVer, highlight, buvid)

    suspend fun searchAll(keyword: String, page: Int = 1, tid: Int? = null, order: String? = null, duration: Int? = null, buvid3: String? = null): BiliResponse<SearchResultData> =
        SearchApi.searchAll(keyword, page, tid, order, duration, buvid3)

    suspend fun searchType(keyword: String, type: String, page: Int = 1, tid: Int? = null, order: String? = null, duration: Int? = null, buvid3: String? = null): BiliResponse<SearchResultData> =
        SearchApi.searchType(keyword, type, page, tid, order, duration, buvid3)

    // ==================== PGC ====================

    suspend fun getPgcVideoPlayUrl(
        av: Long? = null, bv: String? = null, epid: Int? = null, cid: Long? = null,
        qn: Int? = null, fnval: Int? = null, fnver: Int? = null, fourk: Int? = null,
        session: String? = null, supportMultiAudio: Boolean? = null, drmTechType: Int? = null,
        fromClient: String? = null, sessData: String? = null, dedeUserID: Long? = null
    ): BiliResponse<PlayUrlData> = PgcApi.getPgcVideoPlayUrl(av, bv, epid, cid, qn, fnval, fnver, fourk, session, supportMultiAudio, drmTechType, fromClient, sessData, dedeUserID)

    suspend fun getPgcVideoPlayUrlV2(
        av: Long? = null, bv: String? = null, epid: Int? = null, cid: Long? = null,
        qn: Int? = null, fnval: Int? = null, fnver: Int? = null, fourk: Int? = null,
        session: String? = null, supportMultiAudio: Boolean? = null, drmTechType: Int? = null,
        fromClient: String? = null, sessData: String? = null
    ): BiliResponse<PlayUrlV2Data> = PgcApi.getPgcVideoPlayUrlV2(av, bv, epid, cid, qn, fnval, fnver, fourk, session, supportMultiAudio, drmTechType, fromClient, sessData)

    suspend fun getWebSeasonInfo(seasonId: Int? = null, epId: Int? = null, sessData: String = ""): BiliResponse<WebSeasonData> =
        PgcApi.getWebSeasonInfo(seasonId, epId, sessData)

    suspend fun getAppSeasonInfo(
        seasonId: Int? = null, epId: Int? = null, mobiApp: String, adExtra: String? = null,
        autoPlay: Int? = null, build: Int? = null, cLocale: String? = null, channel: String? = null,
        disableRcmd: Int? = null, fromAv: String? = null, fromSpmid: String? = null,
        isShowAllSeries: Int? = null, platform: String? = null, sLocale: String? = null,
        spmid: String? = null, statistics: String? = null, trackPath: String? = null,
        trackid: String? = null, ts: Int? = null, accessKey: String? = ""
    ): BiliResponse<AppSeasonData> = PgcApi.getAppSeasonInfo(seasonId, epId, mobiApp, adExtra, autoPlay, build, cLocale, channel, disableRcmd, fromAv, fromSpmid, isShowAllSeries, platform, sLocale, spmid, statistics, trackPath, trackid, ts, accessKey)

    suspend fun addSeasonFollow(seasonId: Int, csrf: String, sessData: String): BiliResponse<SeasonFollowData> =
        PgcApi.addSeasonFollow(seasonId, csrf, sessData)

    suspend fun addSeasonFollow(seasonId: Int, accessKey: String): BiliResponse<SeasonFollowData> =
        PgcApi.addSeasonFollow(seasonId, accessKey)

    suspend fun delSeasonFollow(seasonId: Int, csrf: String, sessData: String): BiliResponse<SeasonFollowData> =
        PgcApi.delSeasonFollow(seasonId, csrf, sessData)

    suspend fun delSeasonFollow(seasonId: Int, accessKey: String): BiliResponse<SeasonFollowData> =
        PgcApi.delSeasonFollow(seasonId, accessKey)

    suspend fun getSeasonUserStatus(seasonId: Int, sessData: String): BiliResponse<WebSeasonData.UserStatus> =
        PgcApi.getSeasonUserStatus(seasonId, sessData)

    suspend fun getTimeline(type: Int, before: Int, after: Int): BiliResponse<List<Timeline>> =
        PgcApi.getTimeline(type, before, after)

    suspend fun getTimeline(filterType: Int): BiliResponse<TimelineAppData> =
        PgcApi.getTimeline(filterType)

    suspend fun getPgcWebInitialStateData(pgcType: PgcType): PgcWebInitialStateData =
        PgcApi.getPgcWebInitialStateData(pgcType)

    suspend fun getPgcFeedV3(name: String = "anime", cursor: Int = 0): BiliResponse<PgcFeedV3Data> =
        PgcApi.getPgcFeedV3(name, cursor)

    suspend fun getPgcFeed(name: String = "movie", cursor: Int = 0): BiliResponse<PgcFeedData> =
        PgcApi.getPgcFeed(name, cursor)

    suspend fun getFollowingSeasons(
        type: Int, status: Int, pageNumber: Int = 1, pageSize: Int = 15, mid: Long, sessData: String? = ""
    ): BiliResponse<FollowingSeasonWebData> = PgcApi.getFollowingSeasons(type, status, pageNumber, pageSize, mid, sessData)

    suspend fun getFollowingSeasons(
        type: String, status: Int, pageNumber: Int = 1, pageSize: Int = 15, build: Int, accessKey: String
    ): BiliResponse<FollowingSeasonAppData> = PgcApi.getFollowingSeasons(type, status, pageNumber, pageSize, build, accessKey)

    suspend fun seasonIndexAnimeResult(
        order: Int = 0, seasonVersion: Int = -1, spokenLanguageType: Int = -1, area: Int = -1,
        isFinish: Int = -1, copyright: Int = -1, seasonStatus: Int = -1, seasonMonth: Int = -1,
        year: String = "-1", styleId: Int = -1, sort: Int = 0, page: Int = 1, pagesize: Int = 20, type: Int = 1
    ) = PgcApi.seasonIndexAnimeResult(order, seasonVersion, spokenLanguageType, area, isFinish, copyright, seasonStatus, seasonMonth, year, styleId, sort, page, pagesize, type)

    suspend fun seasonIndexGuochuangResult(
        order: Int = 0, seasonVersion: Int = -1, isFinish: Int = -1, copyright: Int = -1,
        seasonStatus: Int = -1, year: String = "-1", styleId: Int = -1, sort: Int = 0,
        page: Int = 1, pagesize: Int = 20, type: Int = 1
    ) = PgcApi.seasonIndexGuochuangResult(order, seasonVersion, isFinish, copyright, seasonStatus, year, styleId, sort, page, pagesize, type)

    suspend fun seasonIndexVarietyResult(
        order: Int = 0, seasonStatus: Int = -1, styleId: Int = -1, sort: Int = 0,
        page: Int = 1, pagesize: Int = 20, type: Int = 1
    ) = PgcApi.seasonIndexVarietyResult(order, seasonStatus, styleId, sort, page, pagesize, type)

    suspend fun seasonIndexMovieResult(
        order: Int = 0, area: Int = -1, styleId: Int = -1, releaseDate: String = "-1",
        seasonStatus: Int = -1, sort: Int = 0, page: Int = 1, pagesize: Int = 20, type: Int = 1
    ) = PgcApi.seasonIndexMovieResult(order, area, styleId, releaseDate, seasonStatus, sort, page, pagesize, type)

    suspend fun seasonIndexTvResult(
        order: Int = 0, area: Int = -1, styleId: Int = -1, releaseDate: String = "-1",
        seasonStatus: Int = -1, sort: Int = 0, page: Int = 1, pagesize: Int = 20, type: Int = 1
    ) = PgcApi.seasonIndexTvResult(order, area, styleId, releaseDate, seasonStatus, sort, page, pagesize, type)

    suspend fun seasonIndexDocumentaryResult(
        order: Int = 0, area: Int = -1, styleId: Int = -1, producerId: Int = -1,
        releaseDate: String = "-1", seasonStatus: Int = -1, sort: Int = 0, page: Int = 1,
        pagesize: Int = 20, type: Int = 1
    ) = PgcApi.seasonIndexDocumentaryResult(order, area, styleId, producerId, releaseDate, seasonStatus, sort, page, pagesize, type)

    suspend fun getSeasonIdByAvid(avid: Long): Int? = PgcApi.getSeasonIdByAvid(avid)

    suspend fun getAidCidByEpid(epid: Int): Pair<Long, Long>? = PgcApi.getAidCidByEpid(epid)

    // ==================== 分区 ====================

    suspend fun getRegionDynamic(rid: Int, accessKey: String): BiliResponse<RegionDynamic> =
        RegionApi.getRegionDynamic(rid, accessKey)

    suspend fun getRegionDynamicList(rid: Int, ctime: Long = 0, accessKey: String): BiliResponse<RegionDynamicList> =
        RegionApi.getRegionDynamicList(rid, ctime, accessKey)

    suspend fun getLocs(ids: List<Int>, sessData: String? = null): RegionLocs =
        RegionApi.getLocs(ids, sessData)

    // ==================== 动态 ====================

    suspend fun getDynamicList(
        timezoneOffset: Int = -480, type: String = "all", page: Int = 1,
        offset: String? = null, sessData: String = ""
    ): BiliResponse<DynamicData> = DynamicApi.getDynamicList(timezoneOffset, type, page, offset, sessData)

    suspend fun getDynamicDetail(
        timezoneOffset: Int = -480, id: String, features: String? = null, sessData: String = ""
    ): BiliResponse<DynamicDetailData> = DynamicApi.getDynamicDetail(timezoneOffset, id, features, sessData)
}
