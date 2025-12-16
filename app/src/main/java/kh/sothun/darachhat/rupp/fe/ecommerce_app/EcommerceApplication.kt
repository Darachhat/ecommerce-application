package kh.sothun.darachhat.rupp.fe.ecommerce_app

import android.app.Application
import com.google.firebase.database.FirebaseDatabase

class EcommerceApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Enable Disk Persistence for offline support and faster subsequent loads
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)

        // Initialize App Check with Debug Provider
        val firebaseAppCheck = com.google.firebase.appcheck.FirebaseAppCheck.getInstance()
        firebaseAppCheck.installAppCheckProviderFactory(
            com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory.getInstance()
        )
    }
}
