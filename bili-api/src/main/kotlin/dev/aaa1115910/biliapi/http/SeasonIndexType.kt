/**
 * 番剧索引类型枚举
 */
package dev.aaa1115910.biliapi.http

enum class SeasonIndexType(val id: Int) {
    Anime(1), Movie(2), Documentary(3), Guochuang(4), Tv(5), Variety(7);

    companion object {
        fun fromId(id: Int) = entries.first { it.id == id }
    }
}
