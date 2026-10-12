package com.example.hw03

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

/**
 * TODO: hoàn thiện adapter.
 *  - dùng ViewHolder + tái sử dụng convertView
 *  - tô màu R.color.row_selected cho dòng có contact.id == selectedId
 *  - đặt contentDescription cho từng dòng
 *  - xử lý an toàn khi position ngoài phạm vi
 * Giữ nguyên chữ ký: var selectedId, fun submit(List<Contact>), getItem(position): Contact?
 */
class ContactAdapter(context: Context) : BaseAdapter() {
    private val inflater = LayoutInflater.from(context)
    private var items: List<Contact> = emptyList()

    var selectedId: Int = Contact.NO_ID
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    fun submit(newItems: List<Contact>) {
        items = newItems.toList()
        notifyDataSetChanged()
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Contact? = items.getOrNull(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: inflater.inflate(R.layout.item_contact, parent, false)
        val contact = items[position]
        view.findViewById<ImageView>(R.id.imgAvatar).setImageResource(contact.avatar)
        view.findViewById<TextView>(R.id.txtName).text = contact.name
        view.findViewById<TextView>(R.id.txtPhone).text = contact.phone
        return view
    }
}
