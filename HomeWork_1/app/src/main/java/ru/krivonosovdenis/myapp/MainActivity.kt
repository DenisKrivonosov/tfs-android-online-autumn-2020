package ru.krivonosovdenis.myapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.krivonosovdenis.myapp.SecondActivity.Companion.SECOND_ACTIVITY_CONTACTS_DATA
import ru.krivonosovdenis.myapp.adapters.ContactsAdapter
import ru.krivonosovdenis.myapp.data_classes.ContactData
import ru.krivonosovdenis.myapp.extensions.RESULT_NO_CONTACTS_PERMISSIONS


class MainActivity : AppCompatActivity() {
    companion object {
        private const val SECOND_ACTIVITY_REQUEST_CODE = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvContacts.layoutManager = LinearLayoutManager(this)
        startSecondActivityForResult()
    }

    private fun startSecondActivityForResult() {
        val intent = Intent(this, SecondActivity::class.java)
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SECOND_ACTIVITY_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val contacts =
                        data?.getParcelableArrayListExtra<ContactData>(SECOND_ACTIVITY_CONTACTS_DATA) as ArrayList<ContactData>
                    handleSuccessContactsResult(contacts)
                }
                RESULT_NO_CONTACTS_PERMISSIONS -> {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.alert_error_title))
                        .setMessage(getString(R.string.alert_error_desc))
                        .setCancelable(false)
                        .setPositiveButton(
                            R.string.ok,
                            DialogInterface.OnClickListener { dialog, id ->
                                //Логика запроса пермишенов реализована во второй активити
                                //Чтоб не тянуть её сюда - переоткрываю ее после показа диалога
                                startSecondActivityForResult()
                            })
                        .create().show()
                }
            }
        }
    }

    private fun handleSuccessContactsResult(contacts: ArrayList<ContactData>) {
        rvContacts.adapter = ContactsAdapter(contacts)
    }
}