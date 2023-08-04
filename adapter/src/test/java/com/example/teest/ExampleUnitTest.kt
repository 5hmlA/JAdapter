package com.example.teest

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test() {
        mutableListOf<tti>(ttia(),ttia(),ttib(),ttia(),ttib()).groupBy {
            it.javaClass
        }.forEach { t, u ->
            println("$t,,,,,,$u")
        }
    }
}

interface tti

class ttia:tti
class ttib:tti