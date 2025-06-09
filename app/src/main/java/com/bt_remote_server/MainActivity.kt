package com.bt_remote_server

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bt_remote_server.databinding.ActivityMainBinding
import com.bt_remote_server.server.BluetoothServerService
import com.bt_remote_server.server.BluetoothServerService.Companion.serverLogsState
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import com.bt_remote_server.server.ServerUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var serverStatusTextView: TextView
    private lateinit var connectionStatusTextView: TextView
    private lateinit var startServerButton: Button
    private lateinit var stopServerButton: Button
    private lateinit var serverLogsTextView: TextView

    private var bluetoothServerService: BluetoothServerService? = null

    companion object {
        private const val REQUEST_BLUETOOTH_CONNECT = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMain.toolbar)

//        binding.appBarMain.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null)
//                .setAnchorView(R.id.fab).show()
//        }
//        val drawerLayout: DrawerLayout = binding.drawerLayout
//        val navView: NavigationView = binding.navView
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
        serverStatusTextView = findViewById(R.id.server_status_text_view)
        connectionStatusTextView = findViewById(R.id.connection_status_text_view)
        startServerButton = findViewById(R.id.start_server_button)
        stopServerButton = findViewById(R.id.stop_server_button)
        serverLogsTextView = findViewById(R.id.server_logs_text_view)

        requestNotificationPermissionIfNeeded()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_ADVERTISE), REQUEST_BLUETOOTH_CONNECT
            )
        } else {
//            ServerUtils.startServer(this)
        }

        startServerButton.setOnClickListener {
//            ServerUtils.startServer(this)
        }

        stopServerButton.setOnClickListener {
            stopServer()
        }

        val bluetoothStateReceiver = BluetoothStateReceiver()
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothStateReceiver, filter)

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
//        appBarConfiguration = AppBarConfiguration(
//            setOf(
//                R.id.nav_home, R.id.nav_gallery, R.id.nav_slideshow
//            ), drawerLayout
//        )
//        setupActionBarWithNavController(navController, appBarConfiguration)
//        navView.setupWithNavController(navController)

        // Initial update of server logs
        updateServerLogs()

        // Observe server running state
        lifecycleScope.launch {
            BluetoothServerService.isServerRunning.collectLatest { isRunning ->
                serverStatusTextView.text = "Server status: ${if (isRunning) "Running" else "Stopped"}"
            }
        }

        lifecycleScope.launch {
            BluetoothServerService.isConnected.collectLatest { connected ->
                connectionStatusTextView.text = "Last connection: ${
                        BluetoothServerService.lastConnectionTime.value}"
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_main)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                // Handle the Settings menu item click
                val intent = Intent(this, ButtonConfigActivity::class.java)
                intent.putExtra("button_index", 0) // for Button 1
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun stopServer() {
        ServerUtils.stopServer(this)
        updateServerLogs()
    }

    private fun updateServerLogs() {
        // Replace this with actual implementation to get logs from serverLogsState
        serverLogsTextView.text = serverLogsState.toString()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_BLUETOOTH_CONNECT) {
            if ((grantResults.isNotEmpty() &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED
                    )) {
                ServerUtils.startServer(this)
            } else {
                // Permission denied
                serverStatusTextView.text = "Permission denied: Cannot start server"
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (!isGranted) {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}