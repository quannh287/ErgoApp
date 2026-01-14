package com.example.ergoguard

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform