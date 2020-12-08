package com.appwork.mandisamiti

import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.appwork.mandisamiti.MainActivity
import com.appwork.utils.AESUtils
import java.util.*

class MainActivity : AppCompatActivity() {
    var appPermissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_COARSE_LOCATION)
    private var textView: TextView? = null
    private var edtText: EditText? = null
    private var isEncrypted = false
    var encrypted: String? = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.button)
        edtText = findViewById(R.id.editText)
        textView = findViewById(R.id.textView)
        button.text = "Encrypt"
        button.setOnClickListener { /*buildNotification();*/
            val str = edtText!!.text.toString().trim { it <= ' ' }
            if (!isEncrypted) {
                if (!str.isEmpty()) {
                    try {
                        encrypted = AESUtils.encrypt(str)
                        Log.d("TEST", "encrypted:$encrypted")
                        isEncrypted = true
                        edtText!!.text.clear()
                        button.text = "Decrypt"
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                } else {
                    Toast.makeText(this@MainActivity, "Enter some text", Toast.LENGTH_SHORT).show()
                }
            } else {
                var decrypted: String? = ""
                try {
                    decrypted = AESUtils.decrypt(encrypted)
                    Log.d("TEST", "decrypted:$decrypted")
                    textView!!.text = decrypted
                    button.visibility = View.GONE
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
        //CheckPermission
        //Step 1
        //Check the platform if grater than lollipop(23)
        if (Build.VERSION.SDK_INT >= 23) {
            //Step 2 check for self permission
            if (checkAndRequestPermission()) {
                //Permission do things
            }
        }
    }

    private fun checkAndRequestPermission(): Boolean {
        val neededPermissions: MutableList<String> = ArrayList()
        for (currentPerm in appPermissions) {
            if (ContextCompat.checkSelfPermission(this, currentPerm) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(currentPerm)
            }
        }
        if (!neededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    neededPermissions.toTypedArray(),
                    REQUEST_PERMISSION_CODE)
            return false
        }
        return true
    }

    private fun buildNotification() {
        val notificationBuilder = NotificationCompat.Builder(this, "0")
        notificationBuilder
                .setContentTitle(this.resources.getString(R.string.app_name))
                .setContentText("Any Description regarding notification")
                .setAutoCancel(true)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setBadgeIconType(NotificationCompat.EXTRA_LARGE_ICON_BIG.toInt())
        val notification = notificationBuilder.build()
        val notificationManager = this.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                    "0",
                    "Normal Notification",
                    NotificationManager.IMPORTANCE_DEFAULT
            )
            channel.setShowBadge(true)
            notificationManager.createNotificationChannel(channel)
            notificationManager.notify(0, notification)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.i("Main Activity", "onResume called")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            val grantResultMap: MutableMap<String, Int> = HashMap()
            var deniCount = 0
            for (i in grantResults.indices) {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                    grantResultMap[permissions[i]] = grantResults[i]
                    deniCount++
                }
            }
            if (deniCount == 0) {
                Toast.makeText(this, "All permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                for ((currentPerm, permResult) in grantResultMap) {
                 /*   if (ActivityCompat.shouldShowRequestPermissionRationale(this, currentPerm)) {
                        showDialogBox(
                                "This app needs permission for work without problems",
                                "Yes, Grant Permissions",
                                { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                    checkAndRequestPermission()
                                },
                                "No, Exit app",
                                { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                    finish()
                                },
                                false)
                    } else {
                        //Denied with "never ask again"
                        showDialogBox(
                                "You have denied some permission. Allow all permissions at [Settings] > [Permissions]",
                                "Go to Settings",
                                { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", packageName, null))
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    startActivity(intent)
                                    finish()
                                },
                                "No, Exit App",
                                { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                    finish()
                                },
                                false)
                        break
                    }*/
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun showDialogBox(
            msg: String?,
            positiveLabel: String?,
            positiveClick: DialogInterface.OnClickListener?,
            negativeLabel: String?,
            negativeListener: DialogInterface.OnClickListener?,
            isCancelable: Boolean): AlertDialog {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(this.resources.getString(R.string.app_name))
                .setCancelable(isCancelable)
                .setPositiveButton(positiveLabel, positiveClick)
                .setNegativeButton(negativeLabel, negativeListener)
                .setMessage(msg)
        val dialog = builder.create()
        dialog.show()
        return dialog
    }

    companion object {
        const val REQUEST_PERMISSION_CODE = 101
    }
}