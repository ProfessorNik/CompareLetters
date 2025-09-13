package com.github.professornik.compareletters

interface Cache<K, V>: (K, (K) -> V) -> V {
    fun get(key: K): V?
    fun set(key: K, value: V)

    override fun invoke(key: K, invokeOriginal: (K) -> V): V {
       return get(key) ?: invokeOriginal(key).apply { set(key, this) }
    }
}

fun <K,V> emptyCache(): Cache<K, V> {
    return object : Cache<K, V> {
        override fun get(key: K): V? = null

        override fun set(key: K, value: V) = Unit
    }
}

fun <K, V> mapCache(): Cache<K, V> {
    return object : Cache<K, V> {
        val map: MutableMap<K, V> = mutableMapOf()

        override fun get(key: K): V? {
            return map[key]
        }

        override fun set(key: K, value: V) {
            map.put(key, value)
        }
    }
}
