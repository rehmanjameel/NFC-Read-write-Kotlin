package org.codebase.nfcreader

import android.app.PendingIntent
import android.content.Intent
import android.content.IntentFilter
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.NfcA
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import java.lang.Exception

@RequiresApi(Build.VERSION_CODES.M)

class MainActivity : AppCompatActivity() {
    private var nfcAdapter : NfcAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)

        var tagFromIntent: Tag? = intent?.getParcelableExtra(NfcAdapter.EXTRA_TAG)
        val nfcA = NfcA.get(tagFromIntent)

        val atqa : ByteArray = nfcA.atqa
        val sak: Short = nfcA.sak

        nfcA.connect()

        val isConnected = nfcA.isConnected

        if (isConnected) {
            val receiveData: ByteArray = nfcA.transceive(atqa)
            Log.d("RData", receiveData.toString())
        } else {
            Log.d("RData", "Not connected")
        }
    }

    private fun enableForeGroundDispatcher(activity: AppCompatActivity, nfcAdapter: NfcAdapter?) {
        val intent = Intent(activity.applicationContext, activity.javaClass)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP

        val pendingIntent = PendingIntent.getActivity(activity.applicationContext, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val filters = arrayOfNulls<IntentFilter>(1)
        val techList = arrayOf<Array<String>>()

        filters[0] = IntentFilter()
        with(filters[0]) {
            this?.addAction(NfcAdapter.ACTION_NDEF_DISCOVERED)
            this?.addCategory(Intent.CATEGORY_DEFAULT)
            try {
                Log.d("RData", "value")

                this?.addDataType("text/plain")
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        nfcAdapter?.enableForegroundDispatch(activity, pendingIntent, filters, techList)
    }

    override fun onResume() {
        super.onResume()
        enableForeGroundDispatcher(this, this.nfcAdapter)
    }
}