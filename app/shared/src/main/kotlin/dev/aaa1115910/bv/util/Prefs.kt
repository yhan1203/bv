@file:Suppress("SpellCheckingInspection")

package dev.aaa1115910.bv.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import de.schnettler.datastore.manager.PreferenceRequest
import dev.aaa1115910.biliapi.entity.ApiType
import dev.aaa1115910.biliapi.http.util.generateBuvid
import dev.aaa1115910.bv.BVApp
import dev.aaa1115910.bv.BuildConfig
import dev.aaa1115910.bv.entity.PlayerType
import dev.aaa1115910.bv.entity.ThemeType
import dev.aaa1115910.bv.player.entity.Audio
import dev.aaa1115910.bv.player.entity.DanmakuType
import dev.aaa1115910.bv.player.entity.PlayMode
import dev.aaa1115910.bv.player.entity.Resolution
import dev.aaa1115910.bv.player.entity.VideoCodec
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.runBlocking
import java.util.Date
import java.util.UUID
import kotlin.math.roundToInt

object Prefs {
    private val dsm = BVApp.dataStoreManager
    val logger = KotlinLogging.logger { }

    @Deprecated("Use suspend getIsLogin()/setIsLogin() instead", ReplaceWith("getIsLogin()"))
    var isLogin: Boolean
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefIsLoginRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefIsLoginKey, value) }
    suspend fun getIsLogin(): Boolean = dsm.getPreferenceFlow(PrefKeys.prefIsLoginRequest).first()
    suspend fun setIsLogin(value: Boolean) = dsm.editPreference(PrefKeys.prefIsLoginKey, value)

    @Deprecated("Use suspend getUid()/setUid() instead", ReplaceWith("getUid()"))
    var uid: Long
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefUidRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefUidKey, value) }
    suspend fun getUid(): Long = dsm.getPreferenceFlow(PrefKeys.prefUidRequest).first()
    suspend fun setUid(value: Long) = dsm.editPreference(PrefKeys.prefUidKey, value)

    @Deprecated("Use suspend getSid()/setSid() instead", ReplaceWith("getSid()"))
    var sid: String
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefSidRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefSidKey, value) }
    suspend fun getSid(): String = dsm.getPreferenceFlow(PrefKeys.prefSidRequest).first()
    suspend fun setSid(value: String) = dsm.editPreference(PrefKeys.prefSidKey, value)

    @Deprecated("Use suspend getSessData()/setSessData() instead", ReplaceWith("getSessData()"))
    var sessData: String
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefSessDataRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefSessDataKey, value) }
    suspend fun getSessData(): String = dsm.getPreferenceFlow(PrefKeys.prefSessDataRequest).first()
    suspend fun setSessData(value: String) = dsm.editPreference(PrefKeys.prefSessDataKey, value)

    @Deprecated("Use suspend getBiliJct()/setBiliJct() instead", ReplaceWith("getBiliJct()"))
    var biliJct: String
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefBiliJctRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefBiliJctKey, value) }
    suspend fun getBiliJct(): String = dsm.getPreferenceFlow(PrefKeys.prefBiliJctRequest).first()
    suspend fun setBiliJct(value: String) = dsm.editPreference(PrefKeys.prefBiliJctKey, value)

    @Deprecated("Use suspend getUidCkMd5()/setUidCkMd5() instead", ReplaceWith("getUidCkMd5()"))
    var uidCkMd5: String
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefUidCkMd5Request).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefUidCkMd5Key, value) }
    suspend fun getUidCkMd5(): String = dsm.getPreferenceFlow(PrefKeys.prefUidCkMd5Request).first()
    suspend fun setUidCkMd5(value: String) = dsm.editPreference(PrefKeys.prefUidCkMd5Key, value)

    @Deprecated(
        "Use suspend getTokenExpiredData()/setTokenExpiredData() instead",
        ReplaceWith("getTokenExpiredData()")
    )
    var tokenExpiredData: Date
        get() = Date(runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefTokenExpiredDateRequest).first()
        })
        set(value) = runBlocking {
            dsm.editPreference(PrefKeys.prefTokenExpiredDateKey, value.time)
        }
    suspend fun getTokenExpiredData(): Date =
        Date(dsm.getPreferenceFlow(PrefKeys.prefTokenExpiredDateRequest).first())
    suspend fun setTokenExpiredData(value: Date) =
        dsm.editPreference(PrefKeys.prefTokenExpiredDateKey, value.time)

    @Deprecated(
        "Use suspend getDefaultQuality()/setDefaultQuality() instead",
        ReplaceWith("getDefaultQuality()")
    )
    var defaultQuality: Resolution
        get() = runBlocking {
            Resolution.fromCode(dsm.getPreferenceFlow(PrefKeys.prefDefaultQualityRequest).first())
                ?: Resolution.R1080P
        }
        set(value) = runBlocking {
            dsm.editPreference(PrefKeys.prefDefaultQualityKey, value.code)
        }
    suspend fun getDefaultQuality(): Resolution =
        Resolution.fromCode(dsm.getPreferenceFlow(PrefKeys.prefDefaultQualityRequest).first())
            ?: Resolution.R1080P
    suspend fun setDefaultQuality(value: Resolution) =
        dsm.editPreference(PrefKeys.prefDefaultQualityKey, value.code)

    @Deprecated(
        "Use suspend getDefaultPlaySpeed()/setDefaultPlaySpeed() instead",
        ReplaceWith("getDefaultPlaySpeed()")
    )
    var defaultPlaySpeed: Float
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefDefaultPlaySpeedRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefDefaultPlaySpeedKey, value) }
    suspend fun getDefaultPlaySpeed(): Float =
        dsm.getPreferenceFlow(PrefKeys.prefDefaultPlaySpeedRequest).first()
    suspend fun setDefaultPlaySpeed(value: Float) =
        dsm.editPreference(PrefKeys.prefDefaultPlaySpeedKey, value)

    @Deprecated(
        "Use suspend getDefaultAudio()/setDefaultAudio() instead",
        ReplaceWith("getDefaultAudio()")
    )
    var defaultAudio: Audio
        get() = runBlocking {
            Audio.fromCode(dsm.getPreferenceFlow(PrefKeys.prefDefaultAudioRequest).first())
                ?: Audio.A192K
        }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefDefaultAudioKey, value.code) }
    suspend fun getDefaultAudio(): Audio =
        Audio.fromCode(dsm.getPreferenceFlow(PrefKeys.prefDefaultAudioRequest).first())
            ?: Audio.A192K
    suspend fun setDefaultAudio(value: Audio) =
        dsm.editPreference(PrefKeys.prefDefaultAudioKey, value.code)

    @Deprecated(
        "Use suspend getDefaultDanmakuSize()/setDefaultDanmakuSize() instead",
        ReplaceWith("getDefaultDanmakuSize()")
    )
    var defaultDanmakuSize: Int
        get() = runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuSizeRequest).first()
        }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefDefaultDanmakuSizeKey, value) }
    suspend fun getDefaultDanmakuSize(): Int =
        dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuSizeRequest).first()
    suspend fun setDefaultDanmakuSize(value: Int) =
        dsm.editPreference(PrefKeys.prefDefaultDanmakuSizeKey, value)

    @Deprecated(
        "Use suspend getDefaultDanmakuScale()/setDefaultDanmakuScale() instead",
        ReplaceWith("getDefaultDanmakuScale()")
    )
    var defaultDanmakuScale: Float
        get() = runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuScaleRequest).first()
        }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefDefaultDanmakuScaleKey, value) }
    suspend fun getDefaultDanmakuScale(): Float =
        dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuScaleRequest).first()
    suspend fun setDefaultDanmakuScale(value: Float) =
        dsm.editPreference(PrefKeys.prefDefaultDanmakuScaleKey, value)

    @Deprecated(
        "Use suspend getDefaultDanmakuTransparency()/setDefaultDanmakuTransparency() instead",
        ReplaceWith("getDefaultDanmakuTransparency()")
    )
    var defaultDanmakuTransparency: Int
        get() = runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuTransparencyRequest).first()
        }
        set(value) = runBlocking {
            dsm.editPreference(PrefKeys.prefDefaultDanmakuTransparencyKey, value)
        }
    suspend fun getDefaultDanmakuTransparency(): Int =
        dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuTransparencyRequest).first()
    suspend fun setDefaultDanmakuTransparency(value: Int) =
        dsm.editPreference(PrefKeys.prefDefaultDanmakuTransparencyKey, value)

    @Deprecated(
        "Use suspend getDefaultDanmakuOpacity()/setDefaultDanmakuOpacity() instead",
        ReplaceWith("getDefaultDanmakuOpacity()")
    )
    var defaultDanmakuOpacity: Float
        get() = runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuOpacityRequest).first()
        }
        set(value) = runBlocking {
            dsm.editPreference(PrefKeys.prefDefaultDanmakuOpacityKey, value)
        }
    suspend fun getDefaultDanmakuOpacity(): Float =
        dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuOpacityRequest).first()
    suspend fun setDefaultDanmakuOpacity(value: Float) =
        dsm.editPreference(PrefKeys.prefDefaultDanmakuOpacityKey, value)

    @Deprecated(
        "Use suspend getDefaultDanmakuEnabled()/setDefaultDanmakuEnabled() instead",
        ReplaceWith("getDefaultDanmakuEnabled()")
    )
    var defaultDanmakuEnabled: Boolean
        get() = runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuEnabledRequest).first()
        }
        set(value) = runBlocking {
            dsm.editPreference(PrefKeys.prefDefaultDanmakuEnabledKey, value)
        }
    suspend fun getDefaultDanmakuEnabled(): Boolean =
        dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuEnabledRequest).first()
    suspend fun setDefaultDanmakuEnabled(value: Boolean) =
        dsm.editPreference(PrefKeys.prefDefaultDanmakuEnabledKey, value)

    @Deprecated(
        "Use suspend getDefaultDanmakuTypes()/setDefaultDanmakuTypes() instead",
        ReplaceWith("getDefaultDanmakuTypes()")
    )
    var defaultDanmakuTypes: List<DanmakuType>
        get() = runBlocking {
            val danmakuTypeIdsString =
                dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuTypesRequest).first()
            if (danmakuTypeIdsString == "") {
                emptyList()
            } else {
                danmakuTypeIdsString.split(",").map { DanmakuType.entries[it.toInt()] }
            }
        }
        set(value) = runBlocking {
            dsm.editPreference(
                PrefKeys.prefDefaultDanmakuTypesKey,
                value.map { it.ordinal }.joinToString(",")
            )
        }

    suspend fun getDefaultDanmakuTypes(): List<DanmakuType> {
        val danmakuTypeIdsString =
            dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuTypesRequest).first()
        return if (danmakuTypeIdsString == "") {
            emptyList()
        } else {
            danmakuTypeIdsString.split(",").map { DanmakuType.entries[it.toInt()] }
        }
    }

    suspend fun setDefaultDanmakuTypes(value: List<DanmakuType>) =
        dsm.editPreference(
            PrefKeys.prefDefaultDanmakuTypesKey,
            value.map { it.ordinal }.joinToString(",")
        )

    @Deprecated(
        "Use suspend getDefaultDanmakuArea()/setDefaultDanmakuArea() instead",
        ReplaceWith("getDefaultDanmakuArea()")
    )
    var defaultDanmakuArea: Float
        get() = runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuAreaRequest).first()
        }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefDefaultDanmakuAreaKey, value) }
    suspend fun getDefaultDanmakuArea(): Float =
        dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuAreaRequest).first()
    suspend fun setDefaultDanmakuArea(value: Float) =
        dsm.editPreference(PrefKeys.prefDefaultDanmakuAreaKey, value)

    @Deprecated(
        "Use suspend getDefaultVideoCodec()/setDefaultVideoCodec() instead",
        ReplaceWith("getDefaultVideoCodec()")
    )
    var defaultVideoCodec: dev.aaa1115910.bv.player.entity.VideoCodec
        get() = dev.aaa1115910.bv.player.entity.VideoCodec.Companion.fromCode(
            runBlocking { dsm.getPreferenceFlow(PrefKeys.prefDefaultVideoCodecRequest).first() }
        )
        set(value) = runBlocking {
            dsm.editPreference(PrefKeys.prefDefaultVideoCodecKey, value.ordinal)
        }
    suspend fun getDefaultVideoCodec(): dev.aaa1115910.bv.player.entity.VideoCodec =
        dev.aaa1115910.bv.player.entity.VideoCodec.Companion.fromCode(
            dsm.getPreferenceFlow(PrefKeys.prefDefaultVideoCodecRequest).first()
        )
    suspend fun setDefaultVideoCodec(value: dev.aaa1115910.bv.player.entity.VideoCodec) =
        dsm.editPreference(PrefKeys.prefDefaultVideoCodecKey, value.ordinal)

    @Deprecated(
        "Use suspend getEnableFirebaseCollection()/setEnableFirebaseCollection() instead",
        ReplaceWith("getEnableFirebaseCollection()")
    )
    var enableFirebaseCollection: Boolean
        get() = runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefEnabledFirebaseCollectionRequest).first()
        }
        set(value) = runBlocking {
            dsm.editPreference(PrefKeys.prefEnabledFirebaseCollectionKey, value)
        }
    suspend fun getEnableFirebaseCollection(): Boolean =
        dsm.getPreferenceFlow(PrefKeys.prefEnabledFirebaseCollectionRequest).first()
    suspend fun setEnableFirebaseCollection(value: Boolean) =
        dsm.editPreference(PrefKeys.prefEnabledFirebaseCollectionKey, value)

    @Deprecated(
        "Use suspend getIncognitoMode()/setIncognitoMode() instead",
        ReplaceWith("getIncognitoMode()")
    )
    var incognitoMode: Boolean
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefIncognitoModeRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefIncognitoModeKey, value) }
    suspend fun getIncognitoMode(): Boolean =
        dsm.getPreferenceFlow(PrefKeys.prefIncognitoModeRequest).first()
    suspend fun setIncognitoMode(value: Boolean) =
        dsm.editPreference(PrefKeys.prefIncognitoModeKey, value)

    @Deprecated(
        "Use suspend getDefaultSubtitleFontSize()/setDefaultSubtitleFontSize() instead",
        ReplaceWith("getDefaultSubtitleFontSize()")
    )
    var defaultSubtitleFontSize: TextUnit
        get() = runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefDefaultSubtitleFontSizeRequest).first().sp
        }
        set(value) = runBlocking {
            dsm.editPreference(PrefKeys.prefDefaultSubtitleFontSizeKey, value.value.roundToInt())
        }
    suspend fun getDefaultSubtitleFontSize(): TextUnit =
        dsm.getPreferenceFlow(PrefKeys.prefDefaultSubtitleFontSizeRequest).first().sp
    suspend fun setDefaultSubtitleFontSize(value: TextUnit) =
        dsm.editPreference(PrefKeys.prefDefaultSubtitleFontSizeKey, value.value.roundToInt())

    @Deprecated(
        "Use suspend getDefaultSubtitleBackgroundOpacity()/setDefaultSubtitleBackgroundOpacity() instead",
        ReplaceWith("getDefaultSubtitleBackgroundOpacity()")
    )
    var defaultSubtitleBackgroundOpacity: Float
        get() = runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefDefaultSubtitleBackgroundOpacityRequest).first()
        }
        set(value) = runBlocking {
            dsm.editPreference(PrefKeys.prefDefaultSubtitleBackgroundOpacityKey, value)
        }
    suspend fun getDefaultSubtitleBackgroundOpacity(): Float =
        dsm.getPreferenceFlow(PrefKeys.prefDefaultSubtitleBackgroundOpacityRequest).first()
    suspend fun setDefaultSubtitleBackgroundOpacity(value: Float) =
        dsm.editPreference(PrefKeys.prefDefaultSubtitleBackgroundOpacityKey, value)

    @Deprecated(
        "Use suspend getDefaultSubtitleBottomPadding()/setDefaultSubtitleBottomPadding() instead",
        ReplaceWith("getDefaultSubtitleBottomPadding()")
    )
    var defaultSubtitleBottomPadding: Dp
        get() = runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefDefaultSubtitleBottomPaddingRequest).first().dp
        }
        set(value) = runBlocking {
            dsm.editPreference(
                PrefKeys.prefDefaultSubtitleBottomPaddingKey, value.value.roundToInt()
            )
        }
    suspend fun getDefaultSubtitleBottomPadding(): Dp =
        dsm.getPreferenceFlow(PrefKeys.prefDefaultSubtitleBottomPaddingRequest).first().dp
    suspend fun setDefaultSubtitleBottomPadding(value: Dp) =
        dsm.editPreference(PrefKeys.prefDefaultSubtitleBottomPaddingKey, value.value.roundToInt())

    @Deprecated(
        "Use suspend getShowFps()/setShowFps() instead",
        ReplaceWith("getShowFps()")
    )
    var showFps: Boolean
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefShowFpsRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefShowFpsKey, value) }
    suspend fun getShowFps(): Boolean =
        dsm.getPreferenceFlow(PrefKeys.prefShowFpsRequest).first()
    suspend fun setShowFps(value: Boolean) =
        dsm.editPreference(PrefKeys.prefShowFpsKey, value)

    @Deprecated(
        "Use suspend getBuvid()/setBuvid() instead",
        ReplaceWith("getBuvid()")
    )
    var buvid: String
        get() = runBlocking {
            val id = dsm.getPreferenceFlow(PrefKeys.prefBuvidRequest).first()
            if (id != "") {
                id
            } else {
                val randomBuvid = generateBuvid()
                buvid3 = randomBuvid
                randomBuvid
            }
        }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefBuvidKey, value) }
    suspend fun getBuvid(): String {
        val id = dsm.getPreferenceFlow(PrefKeys.prefBuvidRequest).first()
        return if (id != "") {
            id
        } else {
            val randomBuvid = generateBuvid()
            setBuvid3(randomBuvid)
            randomBuvid
        }
    }
    suspend fun setBuvid(value: String) = dsm.editPreference(PrefKeys.prefBuvidKey, value)

    @Deprecated(
        "Use suspend getBuvid3()/setBuvid3() instead",
        ReplaceWith("getBuvid3()")
    )
    var buvid3: String
        get() = runBlocking {
            val id = dsm.getPreferenceFlow(PrefKeys.prefBuvid3Request).first()
            if (id != "") {
                id
            } else {
                //random buvid3
                val randomBuvid3 = "${UUID.randomUUID()}${(0..9).random()}infoc"
                buvid3 = randomBuvid3
                randomBuvid3
            }
        }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefBuvid3Key, value) }
    suspend fun getBuvid3(): String {
        val id = dsm.getPreferenceFlow(PrefKeys.prefBuvid3Request).first()
        return if (id != "") {
            id
        } else {
            //random buvid3
            val randomBuvid3 = "${UUID.randomUUID()}${(0..9).random()}infoc"
            setBuvid3(randomBuvid3)
            randomBuvid3
        }
    }
    suspend fun setBuvid3(value: String) = dsm.editPreference(PrefKeys.prefBuvid3Key, value)

    @Deprecated(
        "Use suspend getPlayerType()/setPlayerType() instead",
        ReplaceWith("getPlayerType()")
    )
    var playerType: PlayerType
        get() = runBlocking {
            runCatching {
                PlayerType.entries[dsm.getPreferenceFlow(PrefKeys.prefPlayerTypeRequest).first()]
            }.getOrDefault(PlayerType.Media3)
        }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefPlayerTypeKey, value.ordinal) }
    suspend fun getPlayerType(): PlayerType {
        return runCatching {
            PlayerType.entries[dsm.getPreferenceFlow(PrefKeys.prefPlayerTypeRequest).first()]
        }.getOrDefault(PlayerType.Media3)
    }
    suspend fun setPlayerType(value: PlayerType) =
        dsm.editPreference(PrefKeys.prefPlayerTypeKey, value.ordinal)

    val densityFlow: Flow<Float> get() = dsm.getPreferenceFlow(PrefKeys.prefDensityRequest)

    @Deprecated(
        "Use suspend getDensity()/setDensity() instead",
        ReplaceWith("getDensity()")
    )
    var density: Float
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefDensityRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefDensityKey, value) }
    suspend fun getDensity(): Float =
        dsm.getPreferenceFlow(PrefKeys.prefDensityRequest).first()
    suspend fun setDensity(value: Float) =
        dsm.editPreference(PrefKeys.prefDensityKey, value)

    @Deprecated(
        "Use suspend getUpdateAlpha()/setUpdateAlpha() instead",
        ReplaceWith("getUpdateAlpha()")
    )
    var updateAlpha: Boolean
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefAlphaRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefAlphaKey, value) }
    suspend fun getUpdateAlpha(): Boolean =
        dsm.getPreferenceFlow(PrefKeys.prefAlphaRequest).first()
    suspend fun setUpdateAlpha(value: Boolean) =
        dsm.editPreference(PrefKeys.prefAlphaKey, value)

    @Deprecated(
        "Use suspend getAccessToken()/setAccessToken() instead",
        ReplaceWith("getAccessToken()")
    )
    var accessToken: String
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefAccessTokenRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefAccessTokenKey, value) }
    suspend fun getAccessToken(): String =
        dsm.getPreferenceFlow(PrefKeys.prefAccessTokenRequest).first()
    suspend fun setAccessToken(value: String) =
        dsm.editPreference(PrefKeys.prefAccessTokenKey, value)

    @Deprecated(
        "Use suspend getRefreshToken()/setRefreshToken() instead",
        ReplaceWith("getRefreshToken()")
    )
    var refreshToken: String
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefRefreshTokenRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefRefreshTokenKey, value) }
    suspend fun getRefreshToken(): String =
        dsm.getPreferenceFlow(PrefKeys.prefRefreshTokenRequest).first()
    suspend fun setRefreshToken(value: String) =
        dsm.editPreference(PrefKeys.prefRefreshTokenKey, value)

    @Deprecated(
        "Use suspend getApiType()/setApiType() instead",
        ReplaceWith("getApiType()")
    )
    var apiType: ApiType
        get() = runBlocking {
            ApiType.entries[dsm.getPreferenceFlow(PrefKeys.prefApiTypeRequest).first()]
        }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefApiTypeKey, value.ordinal) }
    suspend fun getApiType(): ApiType =
        ApiType.entries[dsm.getPreferenceFlow(PrefKeys.prefApiTypeRequest).first()]
    suspend fun setApiType(value: ApiType) =
        dsm.editPreference(PrefKeys.prefApiTypeKey, value.ordinal)

    @Deprecated(
        "Use suspend getEnableProxy()/setEnableProxy() instead",
        ReplaceWith("getEnableProxy()")
    )
    var enableProxy: Boolean
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefEnabelProxyRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefEnableProxyKey, value) }
    suspend fun getEnableProxy(): Boolean =
        dsm.getPreferenceFlow(PrefKeys.prefEnabelProxyRequest).first()
    suspend fun setEnableProxy(value: Boolean) =
        dsm.editPreference(PrefKeys.prefEnableProxyKey, value)

    @Deprecated(
        "Use suspend getProxyHttpServer()/setProxyHttpServer() instead",
        ReplaceWith("getProxyHttpServer()")
    )
    var proxyHttpServer: String
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefProxyHttpServerRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefProxyHttpServerKey, value) }
    suspend fun getProxyHttpServer(): String =
        dsm.getPreferenceFlow(PrefKeys.prefProxyHttpServerRequest).first()
    suspend fun setProxyHttpServer(value: String) =
        dsm.editPreference(PrefKeys.prefProxyHttpServerKey, value)

    @Deprecated(
        "Use suspend getProxyGRPCServer()/setProxyGRPCServer() instead",
        ReplaceWith("getProxyGRPCServer()")
    )
    var proxyGRPCServer: String
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefProxyGRPCServerRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefProxyGRPCServerKey, value) }
    suspend fun getProxyGRPCServer(): String =
        dsm.getPreferenceFlow(PrefKeys.prefProxyGRPCServerRequest).first()
    suspend fun setProxyGRPCServer(value: String) =
        dsm.editPreference(PrefKeys.prefProxyGRPCServerKey, value)

    @Deprecated(
        "Use suspend getLastVersionCode()/setLastVersionCode() instead",
        ReplaceWith("getLastVersionCode()")
    )
    var lastVersionCode: Int
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefLastVersionCodeRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefLastVersionCodeKey, value) }
    suspend fun getLastVersionCode(): Int =
        dsm.getPreferenceFlow(PrefKeys.prefLastVersionCodeRequest).first()
    suspend fun setLastVersionCode(value: Int) =
        dsm.editPreference(PrefKeys.prefLastVersionCodeKey, value)

    @Deprecated(
        "Use suspend getShowedRemoteControllerPanelDemo()/setShowedRemoteControllerPanelDemo() instead",
        ReplaceWith("getShowedRemoteControllerPanelDemo()")
    )
    var showedRemoteControllerPanelDemo: Boolean
        get() = runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefShowedRemoteControllerPanelDemoRequest).first()
        }
        set(value) = runBlocking {
            dsm.editPreference(PrefKeys.prefShowedRemoteControllerPanelDemoKey, value)
        }
    suspend fun getShowedRemoteControllerPanelDemo(): Boolean =
        dsm.getPreferenceFlow(PrefKeys.prefShowedRemoteControllerPanelDemoRequest).first()
    suspend fun setShowedRemoteControllerPanelDemo(value: Boolean) =
        dsm.editPreference(PrefKeys.prefShowedRemoteControllerPanelDemoKey, value)

    @Deprecated(
        "Use suspend getPreferOfficialCdn()/setPreferOfficialCdn() instead",
        ReplaceWith("getPreferOfficialCdn()")
    )
    var preferOfficialCdn: Boolean
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefPreferOfficialCdnRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefPreferOfficialCdn, value) }
    suspend fun getPreferOfficialCdn(): Boolean =
        dsm.getPreferenceFlow(PrefKeys.prefPreferOfficialCdnRequest).first()
    suspend fun setPreferOfficialCdn(value: Boolean) =
        dsm.editPreference(PrefKeys.prefPreferOfficialCdn, value)

    @Deprecated(
        "Use suspend getDefaultDanmakuMask()/setDefaultDanmakuMask() instead",
        ReplaceWith("getDefaultDanmakuMask()")
    )
    var defaultDanmakuMask: Boolean
        get() = runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuMaskRequest).first()
        }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefDefaultDanmakuMask, value) }
    suspend fun getDefaultDanmakuMask(): Boolean =
        dsm.getPreferenceFlow(PrefKeys.prefDefaultDanmakuMaskRequest).first()
    suspend fun setDefaultDanmakuMask(value: Boolean) =
        dsm.editPreference(PrefKeys.prefDefaultDanmakuMask, value)

    @Deprecated(
        "Use suspend getEnableFfmpegAudioRenderer()/setEnableFfmpegAudioRenderer() instead",
        ReplaceWith("getEnableFfmpegAudioRenderer()")
    )
    var enableFfmpegAudioRenderer: Boolean
        get() = runBlocking {
            dsm.getPreferenceFlow(PrefKeys.prefEnableFfmpegEndererRequest).first()
        }
        set(value) = runBlocking {
            dsm.editPreference(
                PrefKeys.prefEnableFfmpegAudioRenderer,
                value
            )
        }
    suspend fun getEnableFfmpegAudioRenderer(): Boolean =
        dsm.getPreferenceFlow(PrefKeys.prefEnableFfmpegEndererRequest).first()
    suspend fun setEnableFfmpegAudioRenderer(value: Boolean) =
        dsm.editPreference(PrefKeys.prefEnableFfmpegAudioRenderer, value)

    @Deprecated(
        "Use suspend getBlacklistUser()/setBlacklistUser() instead",
        ReplaceWith("getBlacklistUser()")
    )
    var blacklistUser: Boolean
        get() = runBlocking { dsm.getPreferenceFlow(PrefKeys.prefBlacklistUserRequest).first() }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefBlacklistUserKey, value) }
    suspend fun getBlacklistUser(): Boolean =
        dsm.getPreferenceFlow(PrefKeys.prefBlacklistUserRequest).first()
    suspend fun setBlacklistUser(value: Boolean) =
        dsm.editPreference(PrefKeys.prefBlacklistUserKey, value)

    @Deprecated(
        "Use suspend getThemeType()/setThemeType() instead",
        ReplaceWith("getThemeType()")
    )
    var themeType: ThemeType
        get() = runBlocking {
            ThemeType.entries[dsm.getPreferenceFlow(PrefKeys.prefThemeTypeRequest).first()]
        }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefThemeTypeKey, value.ordinal) }
    suspend fun getThemeType(): ThemeType =
        ThemeType.entries[dsm.getPreferenceFlow(PrefKeys.prefThemeTypeRequest).first()]
    suspend fun setThemeType(value: ThemeType) =
        dsm.editPreference(PrefKeys.prefThemeTypeKey, value.ordinal)

    val themeTypeFlow: Flow<ThemeType>
        get() = dsm.getPreferenceFlow(PrefKeys.prefThemeTypeRequest)
            .transform { ordinal -> emit(ThemeType.entries[ordinal]) }

    @Deprecated(
        "Use suspend getDefaultPlayMode()/setDefaultPlayMode() instead",
        ReplaceWith("getDefaultPlayMode()")
    )
    var defaultPlayMode: PlayMode
        get() = runBlocking {
            PlayMode.entries[dsm.getPreferenceFlow(PrefKeys.prefPlayModeRequest).first()]
        }
        set(value) = runBlocking { dsm.editPreference(PrefKeys.prefPlayModeKey, value.ordinal) }
    suspend fun getDefaultPlayMode(): PlayMode =
        PlayMode.entries[dsm.getPreferenceFlow(PrefKeys.prefPlayModeRequest).first()]
    suspend fun setDefaultPlayMode(value: PlayMode) =
        dsm.editPreference(PrefKeys.prefPlayModeKey, value.ordinal)
}

object PrefKeys {
    val prefIsLoginKey = booleanPreferencesKey("il")
    val prefUidKey = longPreferencesKey("uid")
    val prefSidKey = stringPreferencesKey("sid")
    val prefSessDataKey = stringPreferencesKey("sd")
    val prefBiliJctKey = stringPreferencesKey("bj")
    val prefUidCkMd5Key = stringPreferencesKey("ucm")
    val prefTokenExpiredDateKey = longPreferencesKey("ted")
    val prefDefaultQualityKey = intPreferencesKey("dq")
    val prefDefaultAudioKey = intPreferencesKey("da")
    val prefDefaultPlaySpeedKey = floatPreferencesKey("dps")
    val prefDefaultDanmakuSizeKey = intPreferencesKey("dds")
    val prefDefaultDanmakuScaleKey = floatPreferencesKey("dds2")
    val prefDefaultDanmakuTransparencyKey = intPreferencesKey("ddt")
    val prefDefaultDanmakuOpacityKey = floatPreferencesKey("ddo")
    val prefDefaultDanmakuEnabledKey = booleanPreferencesKey("dde")
    val prefDefaultDanmakuTypesKey = stringPreferencesKey("ddts")
    val prefDefaultDanmakuAreaKey = floatPreferencesKey("dda")
    val prefDefaultVideoCodecKey = intPreferencesKey("dvc")
    val prefEnabledFirebaseCollectionKey = booleanPreferencesKey("efc")
    val prefIncognitoModeKey = booleanPreferencesKey("im")
    val prefDefaultSubtitleFontSizeKey = intPreferencesKey("dsfs")
    val prefDefaultSubtitleBackgroundOpacityKey = floatPreferencesKey("dsbo")
    val prefDefaultSubtitleBottomPaddingKey = intPreferencesKey("dsbp")
    val prefShowFpsKey = booleanPreferencesKey("sf")
    val prefBuvidKey = stringPreferencesKey("random_buvid")
    val prefBuvid3Key = stringPreferencesKey("random_buvid3")
    val prefPlayerTypeKey = intPreferencesKey("pt")
    val prefDensityKey = floatPreferencesKey("density")
    val prefAlphaKey = booleanPreferencesKey("alpha")
    val prefAccessTokenKey = stringPreferencesKey("access_token")
    val prefRefreshTokenKey = stringPreferencesKey("refresh_token")
    val prefApiTypeKey = intPreferencesKey("api_type")
    val prefEnableProxyKey = booleanPreferencesKey("enable_proxy")
    val prefProxyHttpServerKey = stringPreferencesKey("proxy_http_server")
    val prefProxyGRPCServerKey = stringPreferencesKey("proxy_grpc_server")
    val prefLastVersionCodeKey = intPreferencesKey("last_version_code")
    val prefShowedRemoteControllerPanelDemoKey = booleanPreferencesKey("showed_rcpd")
    val prefPreferOfficialCdn = booleanPreferencesKey("prefer_official_cdn")
    val prefDefaultDanmakuMask = booleanPreferencesKey("prefer_enable_webmark")
    val prefEnableFfmpegAudioRenderer = booleanPreferencesKey("enable_ffmpeg_audio_renderer")
    val prefBlacklistUserKey = booleanPreferencesKey("blacklist_user")
    val prefThemeTypeKey = intPreferencesKey("theme_type")
    val prefPlayModeKey = intPreferencesKey("play_mode")

    val prefIsLoginRequest = PreferenceRequest(prefIsLoginKey, false)
    val prefUidRequest = PreferenceRequest(prefUidKey, 0)
    val prefSidRequest = PreferenceRequest(prefSidKey, "")
    val prefSessDataRequest = PreferenceRequest(prefSessDataKey, "")
    val prefBiliJctRequest = PreferenceRequest(prefBiliJctKey, "")
    val prefUidCkMd5Request = PreferenceRequest(prefUidCkMd5Key, "")
    val prefTokenExpiredDateRequest = PreferenceRequest(prefTokenExpiredDateKey, 0)
    val prefDefaultPlaySpeedRequest = PreferenceRequest(prefDefaultPlaySpeedKey, 1f)
    val prefDefaultQualityRequest = PreferenceRequest(prefDefaultQualityKey, Resolution.R1080P.code)
    val prefDefaultAudioRequest = PreferenceRequest(prefDefaultAudioKey, Audio.A192K.code)
    val prefDefaultDanmakuSizeRequest = PreferenceRequest(prefDefaultDanmakuSizeKey, 6)
    val prefDefaultDanmakuScaleRequest = PreferenceRequest(prefDefaultDanmakuScaleKey, 1f)
    val prefDefaultDanmakuTransparencyRequest =
        PreferenceRequest(prefDefaultDanmakuTransparencyKey, 0)
    val prefDefaultDanmakuOpacityRequest = PreferenceRequest(prefDefaultDanmakuOpacityKey, 1f)
    val prefDefaultDanmakuEnabledRequest = PreferenceRequest(prefDefaultDanmakuEnabledKey, true)
    val prefDefaultDanmakuTypesRequest =
        PreferenceRequest(prefDefaultDanmakuTypesKey, "0,1,2,3")
    val prefDefaultDanmakuAreaRequest = PreferenceRequest(prefDefaultDanmakuAreaKey, 1f)
    val prefDefaultVideoCodecRequest =
        PreferenceRequest(prefDefaultVideoCodecKey, VideoCodec.AVC.ordinal)
    val prefEnabledFirebaseCollectionRequest =
        PreferenceRequest(prefEnabledFirebaseCollectionKey, true)
    val prefIncognitoModeRequest = PreferenceRequest(prefIncognitoModeKey, false)
    val prefDefaultSubtitleFontSizeRequest = PreferenceRequest(prefDefaultSubtitleFontSizeKey, 24)
    val prefDefaultSubtitleBackgroundOpacityRequest =
        PreferenceRequest(prefDefaultSubtitleBackgroundOpacityKey, 0.4f)
    val prefDefaultSubtitleBottomPaddingRequest =
        PreferenceRequest(prefDefaultSubtitleBottomPaddingKey, 12)
    val prefShowFpsRequest = PreferenceRequest(prefShowFpsKey, false)
    val prefBuvidRequest = PreferenceRequest(prefBuvidKey, "")
    val prefBuvid3Request = PreferenceRequest(prefBuvid3Key, "")
    val prefPlayerTypeRequest = PreferenceRequest(prefPlayerTypeKey, PlayerType.Media3.ordinal)
    val prefDensityRequest =
        PreferenceRequest(
            prefDensityKey,
            runCatching { BVApp.context.resources.displayMetrics.widthPixels / 960f }
                .getOrDefault(2f)
        )

    @Suppress("KotlinConstantConditions")
    val prefAlphaRequest = PreferenceRequest(prefAlphaKey, BuildConfig.BUILD_TYPE == "alpha")
    val prefAccessTokenRequest = PreferenceRequest(prefAccessTokenKey, "")
    val prefRefreshTokenRequest = PreferenceRequest(prefRefreshTokenKey, "")
    val prefApiTypeRequest = PreferenceRequest(prefApiTypeKey, 0)
    val prefEnabelProxyRequest = PreferenceRequest(prefEnableProxyKey, false)
    val prefProxyHttpServerRequest = PreferenceRequest(prefProxyHttpServerKey, "")
    val prefProxyGRPCServerRequest = PreferenceRequest(prefProxyGRPCServerKey, "")
    val prefLastVersionCodeRequest = PreferenceRequest(prefLastVersionCodeKey, 0)
    val prefShowedRemoteControllerPanelDemoRequest =
        PreferenceRequest(prefShowedRemoteControllerPanelDemoKey, false)
    val prefPreferOfficialCdnRequest = PreferenceRequest(prefPreferOfficialCdn, false)
    val prefDefaultDanmakuMaskRequest = PreferenceRequest(prefDefaultDanmakuMask, false)
    val prefEnableFfmpegEndererRequest = PreferenceRequest(prefEnableFfmpegAudioRenderer, false)
    val prefBlacklistUserRequest = PreferenceRequest(prefBlacklistUserKey, false)
    val prefThemeTypeRequest = PreferenceRequest(prefThemeTypeKey, ThemeType.Auto.ordinal)
    val prefPlayModeRequest = PreferenceRequest(prefPlayModeKey, PlayMode.Sequential.ordinal)
}