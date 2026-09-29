package ar.com.elsellotv.legalscan.ads

import android.app.Activity
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd

/**
 * El único anuncio de la app: se intenta cargar y mostrar una vez al arrancar
 * y nunca más durante ese proceso, se haya podido mostrar o no.
 */
class AppOpenAdManager {

    private var hasShownThisSession = false

    fun loadAndShow(activity: Activity, adUnitId: String) {
        if (hasShownThisSession) return

        AppOpenAd.load(
            activity,
            adUnitId,
            AdRequest.Builder().build(),
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    if (hasShownThisSession || activity.isFinishing) return
                    ad.fullScreenContentCallback = object : FullScreenContentCallback() {
                        override fun onAdDismissedFullScreenContent() {
                            hasShownThisSession = true
                        }

                        override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                            hasShownThisSession = true
                        }
                    }
                    hasShownThisSession = true
                    ad.show(activity)
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.w(TAG, "No se pudo cargar el anuncio de apertura: ${loadAdError.message}")
                }
            },
        )
    }

    private companion object {
        const val TAG = "AppOpenAdManager"
    }
}
