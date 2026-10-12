package com.example.hw03

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat

class MainActivity : AppCompatActivity() {
    private val contacts = ContactRepository.sample()
    private val paginator = Paginator(contacts, PAGE_SIZE)

    private lateinit var txtChoose: TextView
    private lateinit var listContacts: ListView
    private lateinit var adapter: ContactAdapter
    private lateinit var pageButtons: PageButtonController
    private lateinit var state: SelectionState

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        configureWindow()

        txtChoose = findViewById(R.id.txtChoose)
        listContacts = findViewById(R.id.listContacts)
        adapter = ContactAdapter(this)
        listContacts.adapter = adapter

        pageButtons = PageButtonController(
            listOf<Button>(
                findViewById(R.id.btnPage1),
                findViewById(R.id.btnPage2),
                findViewById(R.id.btnPage3),
            )
        ) { page -> if (page != state.page) showPage(page) }

        listContacts.setOnItemClickListener { _, _, position, _ ->
            adapter.getItem(position)?.let { select(it) }
        }

        state = SelectionState.restore(
            savedInstanceState, paginator.pageCount, contacts.map { it.id }.toSet()
        )
        showPage(state.page)
        updateChooseText()
    }

    private fun showPage(page: Int) {
        val target = paginator.clampPage(page)
        state.page = target
        adapter.submit(paginator.itemsOf(target))
        adapter.selectedId = state.selectedId
        listContacts.setSelection(0)
        pageButtons.render(paginator.pageCount, target)
    }

    private fun select(contact: Contact) {
        state.selectedId = contact.id
        adapter.selectedId = contact.id
        updateChooseText()
    }

    private fun updateChooseText() {
        val name = contacts.firstOrNull { it.id == state.selectedId }?.name.orEmpty()
        txtChoose.text = getString(R.string.you_choose, name)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        state.saveTo(outState)
        super.onSaveInstanceState(outState)
    }

    private fun configureWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val light = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK !=
            Configuration.UI_MODE_NIGHT_YES
        WindowInsetsControllerCompat(window, window.decorView).apply {
            isAppearanceLightStatusBars = light
            isAppearanceLightNavigationBars = light
        }
        val root = findViewById<View>(R.id.mainRoot)
        ViewCompat.setOnApplyWindowInsetsListener(root) { view, insets ->
            val bars = insets.getInsets(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.displayCutout()
            )
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom)
            insets
        }
        ViewCompat.requestApplyInsets(root)
    }

    private companion object {
        const val PAGE_SIZE = 5
    }
}
