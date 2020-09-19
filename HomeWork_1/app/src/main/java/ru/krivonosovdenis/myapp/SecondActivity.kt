package ru.krivonosovdenis.myapp

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import ru.krivonosovdenis.myapp.data_classes.ContactData
import ru.krivonosovdenis.myapp.extensions.RESULT_NO_CONTACTS_PERMISSIONS
import ru.krivonosovdenis.myapp.services.AsyncService
import ru.krivonosovdenis.myapp.services.AsyncService.Companion.CONTACTS_EVENT_NAME
import ru.krivonosovdenis.myapp.services.AsyncService.Companion.CONTACTS_SERVICE_RESULT



class SecondActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_READ_CONTACTS = 999
        const val SECOND_ACTIVITY_CONTACTS_DATA = "contacts_second_activity_result"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_CONTACTS)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startContactsService(this)
        } else {
            requestPermission();
        }
    }

    override fun onStart() {
        super.onStart()
        LocalBroadcastManager.getInstance(this).registerReceiver(
            contactsReceiver,
            IntentFilter(CONTACTS_EVENT_NAME)
        )
    }

    override fun onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(contactsReceiver)
        super.onStop()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_READ_CONTACTS -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    startContactsService(this)

                } else {
                    val resultIntent = Intent()
                    setResult(RESULT_NO_CONTACTS_PERMISSIONS, resultIntent)
                    finish()
                }
                return
            }
        }
    }

    private fun startContactsService(context: Context) {
        context.startService(Intent(context, AsyncService::class.java))
    }

    private val contactsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val contacts =
                intent.getParcelableArrayListExtra<ContactData>(CONTACTS_SERVICE_RESULT)
            val resultIntent = Intent()
            resultIntent.putParcelableArrayListExtra(SECOND_ACTIVITY_CONTACTS_DATA, contacts)
            setResult(RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun requestPermission() {
        //Все кейсы не обрабатывал. Допустил, что юзер сразу дает нам доступ к контактам
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.READ_CONTACTS),
            REQUEST_READ_CONTACTS
        )
    }


}