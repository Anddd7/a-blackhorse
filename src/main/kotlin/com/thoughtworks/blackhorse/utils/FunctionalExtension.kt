package com.thoughtworks.blackhorse.utils

fun <T> String.toEnum(valueOf: (String) -> T): T = trim().uppercase().let(valueOf)
