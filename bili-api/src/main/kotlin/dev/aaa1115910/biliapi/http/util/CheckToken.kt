/**
 * 通用工具函数 — 验证 accessKey 或 sessData 至少一个不为空
 */
package dev.aaa1115910.biliapi.http.util

internal fun checkToken(accessKey: String?, sessData: String?) {
    require(accessKey != null || sessData != null) { "accessKey and sessData cannot be null at the same time" }
}
