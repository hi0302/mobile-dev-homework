package com.example.hw03

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat

/**
 * ContactAdapter phục vụ hiển thị danh sách liên hệ trong ListView.
 *  - Dùng ViewHolder + tái sử dụng convertView
 *  - Tô màu R.color.row_selected cho dòng có contact.id == selectedId, dòng khác để trong suốt
 *  - Đặt contentDescription cho từng dòng (R.string.contact_description)
 *  - Xử lý an toàn khi position ngoài phạm vi
 * Giữ nguyên chữ ký: var selectedId, fun submit(List<Contact>), getItem(position): Contact?
 */
class ContactAdapter(private val context: Context) : BaseAdapter() {
    private val inflater = LayoutInflater.from(context)
    private var items: List<Contact> = emptyList()

    var selectedId: Int = Contact.NO_ID
        set(value) {
            if (field != value) {
                field = value
                notifyDataSetChanged()
            }
        }

    fun submit(newItems: List<Contact>) {
        items = newItems.toList()
        notifyDataSetChanged()
    }

    override fun getCount(): Int = items.size

    override fun getItem(position: Int): Contact? = items.getOrNull(position)

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        val holder: ViewHolder

        if (convertView == null) {
            view = inflater.inflate(R.layout.item_contact, parent, false)
            holder = ViewHolder(view)
            view.tag = holder
        } else {
            view = convertView
            holder = convertView.tag as ViewHolder
        }

        val contact = getItem(position)
        if (contact != null) {
            holder.imgAvatar.setImageResource(contact.avatar)
            holder.txtName.text = contact.name
            holder.txtPhone.text = contact.phone
            view.contentDescription = context.getString(R.string.contact_description, contact.name, contact.phone)

            if (contact.id == selectedId) {
                view.setBackgroundColor(ContextCompat.getColor(context, R.color.row_selected))
            } else {
                view.setBackgroundColor(Color.TRANSPARENT)
            }
        } else {
            holder.imgAvatar.setImageDrawable(null)
            holder.txtName.text = ""
            holder.txtPhone.text = ""
            view.contentDescription = null
            view.setBackgroundColor(Color.TRANSPARENT)
        }

        return view
    }

    private class ViewHolder(view: View) {
        val imgAvatar: ImageView = view.findViewById(R.id.imgAvatar)
        val txtName: TextView = view.findViewById(R.id.txtName)
        val txtPhone: TextView = view.findViewById(R.id.txtPhone)
    }
}

