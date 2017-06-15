package com.eje_c.simple360videoplayer

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder

import org.meganekkovr.GearVRActivity

class MainActivity : GearVRActivity() {

    private val conn = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {}

        override fun onServiceDisconnected(name: ComponentName) {}
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Start PlayerService to init ExoPlayer
        bindService(Intent(this, PlayerService::class.java), conn, BIND_AUTO_CREATE)
    }

    override fun onDestroy() {

        // Stop PlayerService
        unbindService(conn)

        super.onDestroy()
    }
}
