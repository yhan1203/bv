package dev.aaa1115910.bv.entity.proxy

enum class ProxyArea {
    MainLand, HongKong, TaiWan;

    companion object {
        fun checkProxyArea(title: String): ProxyArea {
            return MainLand
        }
    }
}
