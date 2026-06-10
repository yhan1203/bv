/**
 * 用户相关 API
 * 包含：用户信息、空间视频、收藏、历史记录、关注、关系、装备等
 */
package dev.aaa1115910.biliapi.http.api

import dev.aaa1115910.biliapi.http.BiliHttpApi
import dev.aaa1115910.biliapi.http.entity.BiliResponse
import dev.aaa1115910.biliapi.http.entity.BiliResponseWithoutData
import dev.aaa1115910.biliapi.http.entity.history.HistoryData
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
import io.ktor.client.statement.body
import dev.aaa1115910.biliapi.http.entity.web.NavResponseData
import dev.aaa1115910.biliapi.http.util.checkToken
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.Parameters

object UserApi {
    private val client get() = BiliHttpApi.client

    /**
     * 获取用户[uid]的详细信息
     */
    suspend fun getUserInfo(
        uid: Long,
        sessData: String = ""
    ): BiliResponse<UserInfoData> = client.get("/x/space/acc/info") {
        parameter("mid", uid)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取用户[uid]的卡片信息
     */
    suspend fun getUserCardInfo(
        uid: Long,
        photo: Boolean = false,
        sessData: String = ""
    ): BiliResponse<UserCardData> = client.get("/x/web-interface/card") {
        parameter("mid", uid)
        parameter("photo", photo)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 通过[sessData]获取用户个人信息
     */
    suspend fun getUserSelfInfo(sessData: String = ""): BiliResponse<MyInfoData> =
        client.get("/x/space/myinfo") {
            header("Cookie", "SESSDATA=$sessData;")
        }.body()

    /**
     * 获取截止至目标id[max]和目标时间[viewAt]历史记录
     */
    suspend fun getHistories(
        max: Long = 0,
        business: String = "",
        viewAt: Long = 0,
        pageSize: Int = 20,
        sessData: String = ""
    ): BiliResponse<HistoryData> = client.get("/x/web-interface/history/cursor") {
        parameter("max", max)
        parameter("business", business)
        parameter("view_at", viewAt)
        parameter("ps", pageSize)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取稍后再看列表
     */
    suspend fun getToView(sessData: String = ""): BiliResponse<ToViewData> =
        client.get("/x/v2/history/toview") {
            header("Cookie", "SESSDATA=$sessData;")
        }.body()

    /**
     * 获取收藏夹[mediaId]的元数据
     */
    suspend fun getFavoriteFolderInfo(
        mediaId: Long,
        accessKey: String? = null,
        sessData: String? = null
    ): BiliResponse<FavoriteFolderInfo> = client.get("/x/v3/fav/folder/info") {
        checkToken(accessKey, sessData)
        parameter("media_id", mediaId)
        accessKey?.let { parameter("access_key", it) }
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()

    /**
     * 获取用户[mid]的所有收藏夹信息
     */
    suspend fun getAllFavoriteFoldersInfo(
        mid: Long,
        type: Int = 0,
        rid: Long? = null,
        accessKey: String? = null,
        sessData: String? = null
    ): BiliResponse<UserFavoriteFoldersData> = client.get("/x/v3/fav/folder/created/list-all") {
        checkToken(accessKey, sessData)
        parameter("up_mid", mid)
        parameter("type", type)
        parameter("rid", rid)
        accessKey?.let { parameter("access_key", it) }
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()

    /**
     * 获取收藏夹[mediaId]的详细内容
     */
    suspend fun getFavoriteList(
        mediaId: Long,
        tid: Int = 0,
        keyword: String? = null,
        order: String? = null,
        type: Int = 0,
        pageSize: Int = 20,
        pageNumber: Int = 1,
        platform: String? = null,
        accessKey: String? = null,
        sessData: String? = null
    ): BiliResponse<FavoriteFolderInfoListData> = client.get("/x/v3/fav/resource/list") {
        checkToken(accessKey, sessData)
        parameter("media_id", mediaId)
        parameter("tid", tid)
        parameter("keyword", keyword)
        parameter("order", order)
        parameter("type", type)
        parameter("ps", pageSize)
        parameter("pn", pageNumber)
        parameter("platform", platform)
        accessKey?.let { parameter("access_key", it) }
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()

    /**
     * 获取收藏夹[mediaId]的全部内容id
     */
    suspend fun getFavoriteIdList(
        mediaId: Long,
        platform: String? = null,
        accessKey: String? = null,
        sessData: String? = null
    ): FavoriteItemIdListResponse = client.get("/x/v3/fav/resource/ids") {
        checkToken(accessKey, sessData)
        parameter("media_id", mediaId)
        parameter("platform", platform)
        accessKey?.let { parameter("access_key", it) }
        sessData?.let { header("Cookie", "SESSDATA=$it;") }
    }.body()

    /**
     * 获取用户[mid]投稿视频（Web）
     */
    suspend fun getWebUserSpaceVideos(
        mid: Long,
        order: String = "pubdate",
        tid: Int = 0,
        keyword: String? = null,
        pageNumber: Int = 1,
        pageSize: Int = 30,
        sessData: String
    ): BiliResponse<WebSpaceVideoData> = client.get("/x/space/wbi/arc/search") {
        parameter("mid", mid)
        parameter("order", order)
        parameter("tid", tid)
        keyword?.let { parameter("keyword", it) }
        parameter("pn", pageNumber)
        parameter("ps", pageSize)
        parameter("dm_img_list", "[]")
        parameter("dm_img_str", "V2ViR0wgMS4wIChPcGVuR0wgRVMgMi4wIENocm9taXVtKQ")
        parameter("dm_cover_img_str", "V2ViR0wgMS4wIChPcGVuR0wgRVMgMi4wIENocm9taXVtKQ")
        header("Cookie", "SESSDATA=$sessData;")
        header("referer", "https://space.bilibili.com")
    }.body()

    /**
     * 获取用户[mid]投稿视频（App）
     */
    suspend fun getAppUserSpaceVideos(
        mid: Long,
        lastAvid: Long,
        order: String = "pubdate",
        ts: Long,
        accessKey: String
    ): BiliResponse<AppSpaceVideoData> =
        client.get("https://app.bilibili.com/x/v2/space/archive/cursor") {
            parameter("vmid", mid)
            parameter("aid", lastAvid)
            parameter("order", order)
            parameter("ts", ts)
            parameter("access_key", accessKey)
        }.body()

    /**
     * 获取用户[mid]的关注列表
     */
    suspend fun getUserFollow(
        mid: Long,
        orderType: String? = null,
        pageSize: Int = 50,
        pageNumber: Int = 1,
        accessKey: String? = null,
        sessData: String? = null
    ): BiliResponse<UserFollowData> = client.get("/x/relation/followings") {
        checkToken(accessKey, sessData)
        parameter("vmid", mid)
        orderType?.let { parameter("order_type", orderType) }
        parameter("ps", pageSize)
        parameter("pn", pageNumber)
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
        accessKey?.let { parameter("access_key", accessKey) }
    }.body()

    /**
     * 更改与用户[mid]之间的相互关系[action]
     */
    suspend fun modifyFollow(
        mid: Long,
        action: FollowAction,
        actionSource: FollowActionSource,
        accessKey: String? = null,
        csrf: String? = null,
        sessData: String? = null
    ): BiliResponseWithoutData = client.post("/x/relation/modify") {
        checkToken(accessKey, sessData)
        setBody(
            FormDataContent(
                Parameters.build {
                    append("fid", "$mid")
                    append("act", "${action.id}")
                    append("re_src", "${actionSource.id}")
                    accessKey?.let { append("access_key", accessKey) }
                    csrf?.let { append("csrf", csrf) }
                }
            ))
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
    }.body()

    /**
     * 获取与用户[mid]的相互关系
     */
    suspend fun getRelations(
        mid: Long,
        accessKey: String? = null,
        sessData: String? = null
    ): BiliResponse<RelationData> = client.get("/x/space/wbi/acc/relation") {
        checkToken(accessKey, sessData)
        parameter("mid", mid)
        accessKey?.let { parameter("access_key", accessKey) }
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
    }.body()

    /**
     * 获取用户[mid]的关系统计（关注数，粉丝数，黑名单数）
     */
    suspend fun getRelationStat(
        mid: Long,
        accessKey: String? = null,
        sessData: String? = null
    ): BiliResponse<RelationStat> = client.get("x/relation/stat") {
        parameter("vmid", mid)
        accessKey?.let { parameter("access_key", accessKey) }
        sessData?.let { header("Cookie", "SESSDATA=$sessData;") }
    }.body()

    /**
     * 获取用户装备的装扮
     */
    suspend fun getUserEquippedGarb(
        part: EquipPart,
        sessData: String
    ): BiliResponse<Equip> = client.get("/x/garb/user/equip") {
        parameter("part", part.value)
        header("Cookie", "SESSDATA=$sessData;")
    }.body()

    /**
     * 获取导航栏用户信息（内含 wbi keys）
     */
    suspend fun getWebInterfaceNav(): BiliResponse<NavResponseData> =
        client.get("/x/web-interface/nav").body()
}
