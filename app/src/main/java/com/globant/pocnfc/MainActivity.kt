package com.globant.pocnfc

import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.tech.Ndef
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.globant.pocnfc.ui.theme.PoCNFCTheme
import java.io.IOException


class MainActivity : ComponentActivity(), NfcAdapter.ReaderCallback {
    private lateinit var mNfcAdapter: NfcAdapter
    private var fullURL = ""

    private val action = { url: String ->
        fullURL = "https://www.$url.com"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this)

        setContent {
            PoCNFCTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Content(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White),
                        action = action
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val options = Bundle()

        // We add some millis to allow a correct reading
        options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 250)

        // We enable reader mode to detect the Tag.
        mNfcAdapter.enableReaderMode(
            this,
            this,
            NfcAdapter.FLAG_READER_NFC_A or
                    NfcAdapter.FLAG_READER_NFC_B or
                    NfcAdapter.FLAG_READER_NFC_F or
                    NfcAdapter.FLAG_READER_NFC_V or
                    NfcAdapter.FLAG_READER_NFC_BARCODE,
            options
        )
    }

    override fun onPause() {
        super.onPause()
        // We disable reader mode to ensure we only detect tags with the app in foreground.
        mNfcAdapter.disableReaderMode(this);
    }

    override fun onTagDiscovered(tag: Tag?) {
        val ndef = Ndef.get(tag)

        val record = NdefRecord.createUri(fullURL)
        val ndefMsg = NdefMessage(record)

//       if (ndef != null) {
//            try {
//                ndef.connect()
//                ndef.writeNdefMessage(ndefMsg)
//
//                runOnUiThread {
//                    Toast.makeText(this, "Tag recorded. URI: $fullURL", Toast.LENGTH_SHORT).show()
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            } finally {
//                try {
//                    ndef.close()
//                } catch (e: IOException) {
//                    e.printStackTrace()
//                }
//            }
//        } else {
//            Log.d("onTagDiscovered", "Error getting ndef.")
//        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Content(modifier: Modifier = Modifier, action: (String) -> Unit) {
    var input by rememberSaveable {
        mutableStateOf("")
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Image(
            modifier = Modifier.size(128.dp),
            painter = painterResource(id = R.drawable.nfc),
            contentDescription = ""
        )
        TextField(value = input,
            onValueChange = {
                input = it
                action(it)
            },
            label = { Text("Introduce URL") })
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Content(action = { _ -> })
}