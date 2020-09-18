package ru.krivonosovdenis.myapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.krivonosovdenis.myapp.R
import ru.krivonosovdenis.myapp.data_classes.ContactData

class ContactsAdapter(private val mContacts: ArrayList<ContactData>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>() {

    inner class ViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val nameTextView = itemView.findViewById<TextView>(R.id.contact_name)
        val phoneTextView = itemView.findViewById<TextView>(R.id.contact_number)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.contact_layout, parent, false)
        return ViewHolder(contactView)
    }


    override fun onBindViewHolder(viewHolder: ContactsAdapter.ViewHolder, position: Int) {
        val contact: ContactData = mContacts.get(position)
        val nameTextView = viewHolder.nameTextView
        nameTextView.setText(contact.contactName)

        val phoneTextView = viewHolder.phoneTextView
        phoneTextView.setText(contact.contactNumber)
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return mContacts.size
    }
}