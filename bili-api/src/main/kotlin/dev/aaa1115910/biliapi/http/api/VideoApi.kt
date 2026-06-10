/**
 * 视频相关 API
 * 包含：视频播放、信息、推荐、收藏、标签、弹幕等
 */
package dev.aaa1115910.biliapi.http.api

import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.entity.BiliResponse
import dev.aaa1115910.biliapi.http.entity.BiliResponseWithoutData
import dev.aaa1115910.biliapi.http.entity.danmaku.DanmakuData
import dev.aaa1115910.biliapi.http.entity.danmaku.DanmakuResponse
import dev.aaa1115910.biliapi.http.entity.home.RcmdIndexData
import dev.aaa1115910.biliapi.http.entity.home.RcmdTopData
import dev.aaa1115910.biliapi.http.entity.region.RegionBanner
import dev.aaa1115910.biliapi.http.entity.region.RegionFeedRcmd
import dev.aaa1115910.biliapi.http.entity.reply.CommentData
import dev.aaa1115910.biliapi.http.entity.reply.CommentReplyData
import dev.aaa1115910.biliapi.http.entity.video.AddCoin
import dev.aaa1115910.biliapi.http.entity.video.CheckSentCoin
import dev.aaa1115910.biliapi.http.entity.video.CheckVideoFavoured
import dev.aaa1115910.biliapi.http.entity.video.PlayUrlData
import dev.aaa1115910.biliapi.http.entity.video.PlayUrlV2Data
import dev.aaa1115910.biliapi.http.entity.video.PopularVideoData
import dev.aaa1115910.biliapi.http.entity.video.RelatedVideosResponse
import dev.aaa1115910.biliapi.http.entity.video.SetVideoFavorite
import dev.aaa1115910.biliapi.http.entity.video.Tag
import dev.aaa1115910.biliapi.http.entity.video.TagDetail
import dev.aaa1115910.biliapi.http.entity.video.TagTopVideosResponse
import dev.aaa1115910.biliapi.http.entity.video.VideoDetail
import dev.aaa1115910.biliapi.http.entity.video.VideoInfo
import dev.aaa1115910.biliapi.http.entity.video.VideoMoreInfo
import dev.aaa1115910.biliapi.http.entity.video.VideoShot
import dev.aaa1115910.biliapi.http.util.checkToken
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.call.body
import io.ktor.client.statement.bodyAsChannel
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readRawBytes
import io.ktor.http.Parameters
import io.ktor.utils.io.InternalAPI
import io.ktor.utils.io.jvm.javaio.toInputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.nodes.Document
import javax.xml.parsers.DocumentBuilderFactory

object VideoApi {
    private val client get() = BiliHttpApi.client

    /**
     * 获取热门视频列表
     */
    suspend fun getPopularVideoData(
        pageNumber: Int = 1,
        pageSize: Int = 20,
        sessData: String = ""
    ): BiliResponse<PopularVideoData> = client.get("/x/web-interface/popular") {
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取视频详细信息
     */
    suspend fun getVideoInfo(
        av: Int? = null,
        bv: String? = null,
        sessData: String? = null
    ): BiliResponse<VideoInfo> = client.get("/x/web-interface/view") {
        parameter("aid", av)
        parameter("bvid", bv)
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
    }.body()

    /**
     * 获取视频超详细信息
     */
    suspend fun getVideoDetail(
        av: Long? = null,
        bv: String? = null,
        sessData: String? = null
    ): BiliResponse<VideoDetail> = client.get("/x/web-interface/view/detail") {
        parameter("aid", av)
        parameter("bvid", bv)
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
    }.body()

    /**
     * 获取视频流
     */
    suspend fun getVideoPlayUrl(
        av: Long? = null,
        bv: String? = null,
        cid: Long,
        qn: Int? = null,
        fnval: Int? = null,
        fnver: Int? = null,
        fourk: Int? = 0,
        session: String? = null,
        otype: String = "json",
        type: String = "",
        platform: String = "oc",
        sessData: String? = null,
        dedeUserID: Long? = null
    ): BiliResponse<PlayUrlData> = client.get("/x/player/playurl") {
        require(av != null || bv != null) { "av and bv cannot be null at the same time" }
        parameter("avid", av)
        parameter("bvid", bv)
        parameter("cid", cid)
        parameter("qn", qn)
        parameter("fnval", fnval)
        parameter("fnver", fnver)
        parameter("fourk", fourk)
        parameter("session", session)
        parameter("otype", otype)
        parameter("type", type)
        parameter("platform", platform)
        sessData?.let { header("Cookie", "SESSDATA=$sessData;DedeUserID=$dedeUserID") }
    }.body()

    /**
     * 通过[cid]获取视频弹幕
     */
    suspend fun getDanmakuXml(
        cid: Long,
        sessData: String = ""
    ): DanmakuResponse {
        val xmlChannel = client.get("/x/v1/dm/list.so") {
            parameter("oid", cid)
            header("Cookie", "SESSDATA=$sessData;")
        }.bodyAsChannel()

        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val doc = withContext(Dispatchers.IO) {
            dBuilder.parse(xmlChannel.toInputStream())
        }
        doc.documentElement.normalize()

        val chatServer = doc.getElementsByTagName("chatserver").item(0).textContent
        val chatId = doc.getElementsByTagName("chatid").item(0).textContent.toLong()
        val maxLimit = doc.getElementsByTagName("maxlimit").item(0).textContent.toInt()
        val state = doc.getElementsByTagName("state").item(0).textContent.toInt()
        val realName = doc.getElementsByTagName("real_name").item(0).textContent.toInt()
        val source = runCatching {
            doc.getElementsByTagName("source").item(0).textContent
        }.getOrDefault("")

        val data = mutableListOf<DanmakuData>()
        val danmakuNodes = doc.getElementsByTagName("d")

        for (i in 0 until danmakuNodes.length) {
            val danmakuNode = danmakuNodes.item(i)
            val p = danmakuNode.attributes.item(0).textContent
            val text = danmakuNode.textContent
            data.add(DanmakuData.fromString(p, text))
        }

        return DanmakuResponse(chatServer, chatId, maxLimit, state, realName, source, data)
    }

    /**
     * 获取与视频[avid]或[bvid]有关的相关推荐视频
     */
    suspend fun getRelatedVideos(
        avid: Long? = null,
        bvid: String? = null
    ): RelatedVideosResponse = client.get("/x/web-interface/archive/related") {
        require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
        parameter("aid", avid)
        parameter("bvid", bvid)
    }.body()

    /**
     * 获取视频[avid]的[cid]视频更多信息，例如播放进度
     */
    suspend fun getVideoMoreInfo(
        avid: Long,
        cid: Long,
        sessData: String,
        buvid3: String
    ): BiliResponse<VideoMoreInfo> = client.get("/x/player/wbi/v2") {
        parameter("aid", avid)
        parameter("cid", cid)
        header("Cookie", "buvid3=$buvid3; SESSDATA=$sessData;")
    }.body()

    /**
     * 为视频[avid]或[bvid]点赞或取消赞
     */
    suspend fun sendVideoLike(
        avid: Long? = null,
        bvid: String? = null,
        like: Boolean = true,
        csrf: String,
        sessData: String
    ): Pair<Boolean, String> {
        val response = client.post("/x/web-interface/archive/like") {
            require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
            setBody(
                FormDataContent(
                    Parameters.build {
                        avid?.let { append("aid", "$it") }
                        bvid?.let { append("bvid", it) }
                        append("like", "${if (like) 1 else 2}")
                        append("csrf", csrf)
                    }
                ))
            header("Cookie", "SESSDATA=$sessData;")
        }.body<BiliResponseWithoutData>()
        return Pair(response.code == 0, response.message)
    }

    /**
     * 检查视频[avid]或[bvid]是否已点赞
     */
    suspend fun checkVideoLiked(
        avid: Long? = null,
        bvid: String? = null,
        sessData: String
    ): Boolean {
        val response = client.get("/x/web-interface/archive/has/like") {
            require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
            avid?.let { parameter("aid", it) }
            bvid?.let { parameter("bvid", it) }
            header("Cookie", "SESSDATA=$sessData;")
        }.body<BiliResponse<Int>>()
        return runCatching {
            response.getResponseData() == 1
        }.getOrDefault(false)
    }

    /**
     * 为视频[avid]或[bvid]投币
     */
    suspend fun sendVideoCoin(
        avid: Long? = null,
        bvid: String? = null,
        multiply: Int = 1,
        like: Boolean = false,
        csrf: String,
        sessData: String
    ): Pair<Boolean, String> {
        require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
        val response = client.post("/x/web-interface/coin/add") {
            setBody(
                FormDataContent(
                    Parameters.build {
                        avid?.let { append("aid", "$it") }
                        bvid?.let { append("bvid", it) }
                        append("multiply", "$multiply")
                        append("select_like", "${if (like) 1 else 0}")
                        append("csrf", csrf)
                    }
                ))
            header("Cookie", "SESSDATA=$sessData;")
        }.body<BiliResponse<AddCoin>>()
        return Pair(response.code == 0, response.message)
    }

    /**
     * 检查视频[avid]或[bvid]是否已投币
     */
    suspend fun checkVideoSentCoin(
        avid: Long? = null,
        bvid: String? = null,
        sessData: String
    ): Boolean {
        val response = client.get("/x/web-interface/archive/coins") {
            require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
            avid?.let { parameter("aid", it) }
            bvid?.let { parameter("bvid", it) }
            header("Cookie", "SESSDATA=$sessData;")
        }.body<BiliResponse<CheckSentCoin>>()
        return runCatching {
            response.getResponseData().multiply != 0
        }.getOrDefault(false)
    }

    /**
     * 为视频[avid]添加到[addMediaIds]或从[delMediaIds]移除
     */
    suspend fun setVideoToFavorite(
        avid: Long,
        type: Int = 2,
        addMediaIds: List<Long> = listOf(),
        delMediaIds: List<Long> = listOf(),
        accessKey: String? = null,
        csrf: String? = null,
        sessData: String? = null
    ) {
        checkToken(accessKey, sessData)
        val response = client.post("/x/v3/fav/resource/deal") {
            require(addMediaIds.isNotEmpty() || delMediaIds.isNotEmpty()) {
                "addMediaIds and delMediaIds cannot be empty at the same time"
            }
            setBody(
                FormDataContent(
                    Parameters.build {
                        append("rid", "$avid")
                        append("type", "$type")
                        append("add_media_ids", addMediaIds.joinToString(separator = ","))
                        append("del_media_ids", delMediaIds.joinToString(separator = ","))
                        csrf?.let { append("csrf", it) }
                        accessKey?.let { append("access_key", it) }
                    }
                ))
            sessData?.let { header("Cookie", "SESSDATA=$it;") }
        }.body<BiliResponse<SetVideoFavorite>>()
        check(response.code == 0) { response.message }
    }

    /**
     * 检查视频[avid]是否已收藏
     */
    suspend fun checkVideoFavoured(
        avid: Long,
        accessKey: String? = null,
        sessData: String? = null
    ): Boolean {
        checkToken(accessKey, sessData)
        val response = client.get("/x/v2/fav/video/favoured") {
            parameter("aid", avid)
            accessKey?.let { parameter("access_key", it) }
            sessData?.let { header("Cookie", "SESSDATA=$it;") }
        }.body<BiliResponse<CheckVideoFavoured>>()
        return runCatching {
            response.getResponseData().favoured
        }.getOrDefault(false)
    }

    /**
     * 获取视频[avid]/[bvid]的视频标签[Tag]
     */
    suspend fun getVideoTags(
        avid: Long? = null,
        bvid: String? = null,
        sessData: String = ""
    ): BiliResponse<List<Tag>> = client.get("/x/tag/archive/tags") {
        require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
        avid?.let { parameter("aid", it) }
        bvid?.let { parameter("bvid", it) }
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取视频标签[tagId]的详细信息
     */
    suspend fun getTagDetail(
        tagId: Int,
        pageNumber: Int,
        pageSize: Int
    ): BiliResponse<TagDetail> = client.get("/x/tag/detail") {
        parameter("tag_id", tagId)
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
    }.body()

    /**
     * 获取视频标签[tagId]的最热门的视频列表
     */
    suspend fun getTagTopVideos(
        tagId: Int,
        pageNumber: Int,
        pageSize: Int
    ): TagTopVideosResponse = client.get("/x/web-interface/tag/top") {
        parameter("tid", tagId)
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
    }.body()

    /**
     * 获取首页视频推荐列表（Web）
     */
    suspend fun getFeedRcmd(
        freshType: Int = 4,
        pageSize: Int = 30,
        idx: Int = 1,
        sessData: String? = null
    ): BiliResponse<RcmdTopData> = client.get("/x/web-interface/wbi/index/top/feed/rcmd") {
        parameter("fresh_type", freshType)
        parameter("ps", pageSize)
        parameter("fresh_idx", idx)
        parameter("fresh_idx_1h", idx)
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()

    /**
     * 获取首页视频推荐列表（App）
     */
    suspend fun getFeedIndex(
        idx: Int = 0,
        accessKey: String? = null,
    ): BiliResponse<RcmdIndexData> =
        client.get("https://app.bilibili.com/x/v2/feed/index") {
            parameter("idx", idx)
            accessKey?.let { parameter("access_key", it) }
        }.body()

    /**
     * 发送播放心跳（Web）
     */
    suspend fun sendHeartbeat(
        avid: Long? = null,
        bvid: String? = null,
        cid: Long? = null,
        epid: Int? = null,
        sid: Int? = null,
        mid: Long? = null,
        playedTime: Int? = null,
        realtime: Int? = null,
        startTs: Long? = null,
        type: Int? = null,
        subType: Int? = null,
        dt: Int? = null,
        playType: Int? = null,
        csrf: String? = null,
        sessData: String
    ): String = client.post("/x/click-interface/web/heartbeat") {
        require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
        setBody(
            FormDataContent(
                Parameters.build {
                    avid?.let { append("aid", "$it") }
                    bvid?.let { append("bvid", it) }
                    cid?.let { append("cid", "$it") }
                    epid?.let { append("epid", "$it") }
                    sid?.let { append("sid", "$it") }
                    mid?.let { append("mid", "$it") }
                    playedTime?.let { append("played_time", "$it") }
                    realtime?.let { append("realtime", "$it") }
                    startTs?.let { append("start_ts", "$it") }
                    type?.let { append("type", "$it") }
                    subType?.let { append("sub_type", "$it") }
                    dt?.let { append("dt", "$it") }
                    playType?.let { append("play_type", "$it") }
                    csrf?.let { append("csrf", it) }
                }
            ))
        header("Cookie", "SESSDATA=$sessData;")
    }.bodyAsText()

    /**
     * 发送播放心跳（App）
     */
    suspend fun sendHeartbeat(
        avid: Long? = null,
        bvid: String? = null,
        cid: Long? = null,
        epid: Int? = null,
        sid: Int? = null,
        mid: Long? = null,
        playedTime: Int? = null,
        realtime: Int? = null,
        startTs: Long? = null,
        type: Int? = null,
        subType: Int? = null,
        dt: Int? = null,
        playType: Int? = null,
        accessKey: String? = null
    ): String = client.post("/x/v2/history/report") {
        require(avid != null || bvid != null) { "avid and bvid cannot be null at the same time" }
        setBody(
            FormDataContent(
                Parameters.build {
                    avid?.let { append("aid", "$it") }
                    bvid?.let { append("bvid", it) }
                    cid?.let { append("cid", "$it") }
                    epid?.let { append("epid", "$it") }
                    sid?.let { append("sid", "$it") }
                    mid?.let { append("mid", "$it") }
                    playedTime?.let { append("progress", "$it") }
                    realtime?.let { append("realtime", "$it") }
                    startTs?.let { append("start_ts", "$it") }
                    type?.let { append("type", "$it") }
                    subType?.let { append("sub_type", "$it") }
                    dt?.let { append("dt", "$it") }
                    playType?.let { append("play_type", "$it") }
                    accessKey?.let { append("access_key", it) }
                }
            ))
    }.bodyAsText()

    /**
     * 获取视频截图
     */
    suspend fun getWebVideoShot(
        aid: Long? = null,
        bvid: String? = null,
        cid: Long? = null,
        needJsonArrayIndex: Boolean = false
    ): BiliResponse<VideoShot> = client.get("/x/player/videoshot") {
        require(aid != null || bvid != null) { "av and bv cannot be null at the same time" }
        aid?.let { parameter("aid", it) }
        bvid?.let { parameter("bvid", it) }
        cid?.let { parameter("cid", it) }
        parameter("index", if (needJsonArrayIndex) 1 else 0)
    }.body()

    /**
     * 获取视频截图（App）
     */
    suspend fun getAppVideoShot(
        aid: Long,
        cid: Long
    ): BiliResponse<VideoShot> = client.get("https://app.bilibili.com/x/v2/view/video/shot") {
        parameter("aid", aid)
        parameter("cid", cid)
        parameter("ts", 0)
    }.body()

    /**
     * 通用下载
     */
    suspend fun download(url: String): ByteArray {
        return client.get(url).readRawBytes()
    }

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

    /**
     * 获取评论
     */
    suspend fun getComments(
        type: Long,
        oid: Long,
        mode: Int = 3,
        paginationStr: String = """{"offset":""}""",
        sessData: String? = null,
        buvid3: String? = null
    ): BiliResponse<CommentData> =
        client.get("/x/v2/reply/wbi/main") {
            parameter("type", type)
            parameter("oid", oid)
            parameter("mode", mode)
            parameter("pagination_str", paginationStr)
            sessData?.let { header("Cookie", "SESSDATA=$sessData;buvid3=$buvid3;") }
        }.body()

    /**
     * 获取评论回复
     */
    suspend fun getCommentReplies(
        oid: Long,
        type: Long,
        root: Long,
        pageSize: Int = 10,
        pageNumber: Int = 1
    ): BiliResponse<CommentReplyData> = client.get("/x/v2/reply/reply") {
        parameter("oid", oid)
        parameter("type", type)
        parameter("root", root)
        parameter("ps", pageSize)
        parameter("pn", pageNumber)
    }.body()
}
