package ar.com.elsellotv.legalscan

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ar.com.elsellotv.legalscan.ads.AppOpenAdManager
import ar.com.elsellotv.legalscan.ui.theme.LegalScanTheme
import com.google.android.gms.ads.MobileAds
import com.google.android.ump.ConsentInformation
import com.google.android.ump.ConsentRequestParameters
import com.google.android.ump.UserMessagingPlatform
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult

class MainActivity : ComponentActivity() {

    private val scannerOptions = GmsDocumentScannerOptions.Builder()
        .setGalleryImportAllowed(true)
        .setPageLimit(20)
        .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
        .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
        .build()

    private lateinit var scanner: GmsDocumentScanner
    private val appOpenAdManager = AppOpenAdManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scanner = GmsDocumentScanning.getClient(scannerOptions)
        setUpAdsWithConsent()

        setContent {
            var uiState by remember { mutableStateOf<ScanUiState>(ScanUiState.Idle) }

            val scannerLauncher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.StartIntentSenderForResult()
            ) { activityResult ->
                if (activityResult.resultCode == RESULT_OK) {
                    val result = GmsDocumentScanningResult.fromActivityResultIntent(activityResult.data)
                    val pdf = result?.pdf
                    uiState = if (pdf != null) {
                        ScanUiState.Ready(pdf.uri, pdf.pageCount)
                    } else {
                        ScanUiState.Error(getString(R.string.scan_error))
                    }
                }
                // resultCode == RESULT_CANCELED: el usuario canceló, dejamos el estado como está.
            }

            LegalScanTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    ScanScreen(
                        state = uiState,
                        onScanClick = { launchScanner(scannerLauncher) },
                        onNewScanClick = { uiState = ScanUiState.Idle },
                    )
                }
            }
        }
    }

    private fun launchScanner(launcher: ActivityResultLauncher<IntentSenderRequest>) {
        scanner.getStartScanIntent(this)
            .addOnSuccessListener { intentSender ->
                launcher.launch(IntentSenderRequest.Builder(intentSender).build())
            }
            .addOnFailureListener {
                // El usuario canceló el permiso de cámara o Google Play services no está disponible.
            }
    }

    /**
     * Antes de pedir anuncios hay que consultar (y, si corresponde, mostrar) el formulario de
     * consentimiento de Google (UMP) — obligatorio para usuarios de UE/Reino Unido/California.
     * Recién si el usuario puede recibir anuncios se inicializa el SDK y se muestra el único
     * anuncio de la app (App Open Ad de arranque).
     */
    private fun setUpAdsWithConsent() {
        val consentInformation = UserMessagingPlatform.getConsentInformation(this)
        val params = ConsentRequestParameters.Builder().build()

        consentInformation.requestConsentInfoUpdate(
            this,
            params,
            {
                UserMessagingPlatform.loadAndShowConsentFormIfRequired(this) { formError ->
                    if (formError != null) {
                        Log.w(TAG, "Formulario de consentimiento: ${formError.message}")
                    }
                    showAppOpenAdIfAllowed(consentInformation)
                }
            },
            { requestConsentError ->
                Log.w(TAG, "No se pudo actualizar el consentimiento: ${requestConsentError.message}")
                showAppOpenAdIfAllowed(consentInformation)
            },
        )
    }

    private fun showAppOpenAdIfAllowed(consentInformation: ConsentInformation) {
        if (!consentInformation.canRequestAds()) return
        MobileAds.initialize(this) {
            appOpenAdManager.loadAndShow(this, getString(R.string.app_open_ad_unit_id))
        }
    }

    private companion object {
        const val TAG = "MainActivity"
    }
}

@Composable
private fun ScanScreen(
    state: ScanUiState,
    onScanClick: () -> Unit,
    onNewScanClick: () -> Unit,
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(text = stringResource(R.string.home_title), style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(R.string.home_subtitle),
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(32.dp))

        when (state) {
            is ScanUiState.Idle -> {
                Button(onClick = onScanClick, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.scan_button))
                }
            }

            is ScanUiState.Ready -> {
                Text(
                    text = stringResource(R.string.result_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                Spacer(Modifier.height(8.dp))
                Text(text = String.format(stringResource(R.string.result_pages), state.pageCount))
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { ShareUtils.shareViaWhatsApp(context, state.pdfUri) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.share_whatsapp))
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { ShareUtils.shareViaEmail(context, state.pdfUri) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.share_email))
                }
                Spacer(Modifier.height(12.dp))
                Button(
                    onClick = { ShareUtils.shareGeneric(context, state.pdfUri) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.share_other))
                }
                Spacer(Modifier.height(24.dp))
                Button(onClick = onNewScanClick, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.new_scan))
                }
            }

            is ScanUiState.Error -> {
                Text(text = state.message, color = MaterialTheme.colorScheme.error)
                Spacer(Modifier.height(16.dp))
                Button(onClick = onScanClick, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.scan_button))
                }
            }
        }

        Spacer(Modifier.height(32.dp))
        Text(
            text = stringResource(R.string.privacy_note),
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
