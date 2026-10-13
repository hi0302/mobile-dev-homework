package com.example.hw03

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PaginatorTest {
    @Test
    fun fifteenItemsHaveThreePagesAndSecondPageContainsSixToTen() {
        val paginator = Paginator((1..15).toList(), 5)
        assertEquals(3, paginator.pageCount)
        assertEquals((1..5).toList(), paginator.itemsOf(0))
        assertEquals((6..10).toList(), paginator.itemsOf(1))
        assertEquals((11..15).toList(), paginator.itemsOf(2))
    }

    @Test
    fun lastPageContainsOnlyRemainingItems() {
        val paginator = Paginator((1..16).toList(), 5)
        assertEquals(4, paginator.pageCount)
        assertEquals(listOf(16), paginator.itemsOf(3))
    }

    @Test
    fun emptyListHasNoPagesAndClampsToZero() {
        val paginator = Paginator(emptyList<Int>(), 5)
        assertEquals(0, paginator.pageCount)
        for (page in listOf(Int.MIN_VALUE, -1, 0, 1, Int.MAX_VALUE)) {
            assertTrue(paginator.itemsOf(page).isEmpty())
            assertEquals(0, paginator.clampPage(page))
        }
    }

    @Test
    fun nonexistentPagesAreEmpty() {
        val paginator = Paginator((1..15).toList(), 5)
        for (page in listOf(Int.MIN_VALUE, -1, 3, Int.MAX_VALUE)) {
            assertTrue(paginator.itemsOf(page).isEmpty())
        }
    }

    @Test
    fun clampPageKeepsValidPagesAndClampsBothEnds() {
        val paginator = Paginator((1..15).toList(), 5)
        assertEquals(0, paginator.clampPage(Int.MIN_VALUE))
        assertEquals(0, paginator.clampPage(0))
        assertEquals(1, paginator.clampPage(1))
        assertEquals(2, paginator.clampPage(2))
        assertEquals(2, paginator.clampPage(Int.MAX_VALUE))
    }

    @Test(expected = IllegalArgumentException::class)
    fun zeroPageSizeIsRejected() {
        Paginator(listOf(1), 0)
    }

    @Test(expected = IllegalArgumentException::class)
    fun negativePageSizeIsRejectedEvenForEmptyList() {
        Paginator(emptyList<Int>(), -1)
    }

    @Test
    fun sourceMutationsDoNotChangePages() {
        val source = (1..6).toMutableList()
        val paginator = Paginator(source, 5)
        source[0] = 99
        source.clear()
        assertEquals(2, paginator.pageCount)
        assertEquals((1..5).toList(), paginator.itemsOf(0))
        assertEquals(listOf(6), paginator.itemsOf(1))
    }

    @Test
    fun maximumPageSizeDoesNotOverflow() {
        val paginator = Paginator(listOf(1, 2), Int.MAX_VALUE)
        assertEquals(1, paginator.pageCount)
        assertEquals(listOf(1, 2), paginator.itemsOf(0))
    }

    @Test
    fun singleItemPagesKeepAllItemsInOrder() {
        val paginator = Paginator(listOf("a", "b", "c"), 1)
        assertEquals(3, paginator.pageCount)
        assertEquals(listOf("a", "b", "c"), (0..2).flatMap(paginator::itemsOf))
    }
}
