package com.ancda.rtsppusher.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ancda.rtsppusher.ui.SplashActivity

class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION) {
            val mainIntent = Intent(context, SplashActivity::class.java)
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(mainIntent)
        }
    }

    companion object {
        const val ACTION: String = "android.intent.action.BOOT_COMPLETED"
    }
}
