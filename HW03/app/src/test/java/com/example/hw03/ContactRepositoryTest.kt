package com.example.hw03

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ContactRepositoryTest {

    @Test
    fun sample_returns15ContactsWithUniqueIds() {
        val contacts = ContactRepository.sample()
        assertEquals(15, contacts.size)

        val uniqueIds = contacts.map { it.id }.toSet()
        assertEquals(15, uniqueIds.size)
        assertEquals((1..15).toList(), contacts.map { it.id })
        assertEquals(15, contacts.map { it.phone }.toSet().size)
        val avatars = listOf(R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3, R.drawable.avatar_4, R.drawable.avatar_5)
        assertEquals(List(15) { avatars[it % avatars.size] }, contacts.map { it.avatar })
    }

    @Test
    fun sample_hasValidData() {
        val contacts = ContactRepository.sample()
        for (contact in contacts) {
            assertNotNull(contact.name)
            assertNotNull(contact.phone)
            assertTrue(contact.name.isNotBlank())
            assertTrue(contact.phone.isNotBlank())
            assertTrue(contact.avatar != 0)
        }
    }
}
