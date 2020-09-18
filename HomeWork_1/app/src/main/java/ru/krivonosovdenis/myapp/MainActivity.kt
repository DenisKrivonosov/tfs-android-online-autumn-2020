package ru.krivonosovdenis.myapp

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import ru.krivonosovdenis.myapp.adapters.ContactsAdapter
import ru.krivonosovdenis.myapp.data_classes.ContactData
import ru.krivonosovdenis.myapp.extensions.RESULT_NO_CONTACTS_PERMISSIONS


class MainActivity : AppCompatActivity() {
    companion object {
        const val SECOND_ACTIVITY_REQUEST_CODE = 42
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val rvContacts = rvContacts
        rvContacts.layoutManager = LinearLayoutManager(this)
        startSecondActivityForResult()

    }

    private fun startSecondActivityForResult(){
        val intent = Intent(this@MainActivity, SecondActivity::class.java)
        startActivityForResult(intent, SECOND_ACTIVITY_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == SECOND_ACTIVITY_REQUEST_CODE){
            when(resultCode){
                RESULT_OK -> {
                    val contacts =
                        data?.getParcelableArrayListExtra<ContactData>("contacts_second_activity_result") as ArrayList<ContactData>
                    handleSuccessContactsResult(contacts)
                }
                RESULT_NO_CONTACTS_PERMISSIONS -> {
                    val builder = AlertDialog.Builder(this@MainActivity)
                    builder
                        .setTitle(getString(R.string.alert_error_title))
                        .setMessage(getString(R.string.alert_error_desc))
                        .setPositiveButton(
                            R.string.ok,
                            DialogInterface.OnClickListener { dialog, id ->
                                finish()
                            })
                    builder.create().show()
                }
            }
        }
    }

    private fun handleSuccessContactsResult(contacts: ArrayList<ContactData>){
//        contacts.forEach{
//            Log.e("name_cc", it.contactName)
//            Log.e("phone_cc", it.contactNumber)
//        }

        val adapter = ContactsAdapter(contacts)
        rvContacts.adapter = adapter
    }
}