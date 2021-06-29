package com.thoughtworks.blackhorse.utils

fun <T> String.toEnum(valueOf: (String) -> T): T = trim().uppercase().let(valueOf)

fun <T> List<T>.findByKeyOrNext(keyExtractor: (T) -> String?, key: String?, index: Int): T {
    return key?.let { findByKey(it, keyExtractor) } ?: findNext(index)
}

private fun <T> List<T>.findByKey(id: String, keyExtractor: (T) -> String?): T {
    return find { keyExtractor(it) == id } ?: throw IllegalArgumentException("No such element with id: $id")
}

private fun <T> List<T>.findNext(index: Int): T {
    if (index == size)
        throw IllegalArgumentException("You have completed, don't add invalid element")
    return get(index)
}
