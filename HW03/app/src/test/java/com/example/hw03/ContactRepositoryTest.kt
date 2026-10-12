package com.example.hw03

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class ContactRepositoryTest {

    @Test
    fun sample_returns15ContactsWithUniqueIds() {
        val contacts = ContactRepository.sample()
        assertEquals(15, contacts.size)

        val uniqueIds = contacts.map { it.id }.toSet()
        assertEquals(15, uniqueIds.size)
        assertEquals((1..15).toList(), contacts.map { it.id })
    }

    @Test
    fun sample_hasValidData() {
        val contacts = ContactRepository.sample()
        for (contact in contacts) {
            assertNotNull(contact.name)
            assertNotNull(contact.phone)
            assert(contact.name.isNotBlank())
            assert(contact.phone.isNotBlank())
            assert(contact.avatar != 0)
        }
    }
}
