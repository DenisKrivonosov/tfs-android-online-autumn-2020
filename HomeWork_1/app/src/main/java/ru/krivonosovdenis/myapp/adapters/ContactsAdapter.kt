package ru.krivonosovdenis.myapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.contact_layout.view.*
import ru.krivonosovdenis.myapp.R
import ru.krivonosovdenis.myapp.data_classes.ContactData

class ContactsAdapter(private val contacts: ArrayList<ContactData>) : RecyclerView.Adapter<ContactsAdapter.ViewHolder>(){

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
        val nameTextView = containerView.contact_name
        val phoneTextView = containerView.contact_number
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactsAdapter.ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val contactView = inflater.inflate(R.layout.contact_layout, parent, false)
        return ViewHolder(contactView)
    }


    override fun onBindViewHolder(viewHolder: ContactsAdapter.ViewHolder, position: Int) {
        val contact: ContactData = contacts.get(position)
        val nameTextView = viewHolder.nameTextView
        nameTextView.setText(contact.contactName)

        val phoneTextView = viewHolder.phoneTextView
        phoneTextView.setText(contact.contactNumber)
    }

    // Returns the total count of items in the list
    override fun getItemCount(): Int {
        return contacts.size
    }
}