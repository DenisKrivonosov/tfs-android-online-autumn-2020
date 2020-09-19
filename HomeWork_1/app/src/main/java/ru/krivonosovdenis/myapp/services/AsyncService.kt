package ru.krivonosovdenis.myapp.services

import android.app.IntentService
import android.content.Intent
import android.database.Cursor
import android.provider.ContactsContract
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ru.krivonosovdenis.myapp.data_classes.ContactData


class AsyncService : IntentService(AsyncService::class.java.simpleName) {

    companion object {
        const val CONTACTS_EVENT_NAME = "contacts_success_event"
        const val CONTACTS_SERVICE_RESULT = "contacts_service_result"

    }

    val contactsResult = ArrayList<ContactData>()


    override fun onHandleIntent(intent: Intent?) {
        val cursor: Cursor? =
            contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null)
        cursor?.let {
            while (cursor.moveToNext()) {
                val id: String = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.Contacts._ID)
                )
                val name: String = cursor.getString(
                    cursor.getColumnIndex(
                        ContactsContract.Contacts.DISPLAY_NAME
                    )
                )
                val phoneColumnId = cursor.getInt(
                    cursor.getColumnIndex(
                        ContactsContract.Contacts.HAS_PHONE_NUMBER
                    )
                )
                if (phoneColumnId > 0) {
                    val phoneCursor: Cursor? = contentResolver.query(
                        ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?",
                        arrayOf(id),
                        null
                    )
                    if (phoneCursor != null) {
                        //забираем только 1 номер
                        if (phoneCursor.moveToNext()) {
                            val phoneNo = phoneCursor.getString(
                                phoneCursor.getColumnIndex(
                                    ContactsContract.CommonDataKinds.Phone.NUMBER
                                )
                            )
                            val contact = ContactData(name, phoneNo)
                            contactsResult.add(contact)
                        }
                    }
                    phoneCursor?.close()
                }
            }
        }
        cursor?.close()

        Intent().also { intent ->
            intent.setAction(CONTACTS_EVENT_NAME)
            intent.putParcelableArrayListExtra(CONTACTS_SERVICE_RESULT, contactsResult)
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent)

        }
    }
}