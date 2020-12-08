package com.appwork.mandisamiti

import android.Manifest
import android.content.ContentValues
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.appwork.database.MandiManagement
import com.appwork.databaseUtils.ClientContract.ClientValues
import com.appwork.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.util.*

class AddClientActivity : AppCompatActivity(), View.OnClickListener {
    private var tilClientName: TextInputLayout? = null
    private var tilClientNumber: TextInputLayout? = null
    private var tilClientAddress: TextInputLayout? = null
    private var edtClientName: EditText? = null
    private var edtClientNumber: EditText? = null
    private var edtClientAddress: EditText? = null
    private var imgClient: CircleImageView? = null
    private var btnClientSave: Button? = null
    private var parentClient: ConstraintLayout? = null

    //Classes
    private var validations: ValidationUtils? = null
    private var database: MandiManagement? = null
    private var preferences: AppPreferences? = null
    private var currentPhotoPath = ""
    private val appPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var clientId: Long = -1
    private var dialog: MaterialAlertDialogBuilder? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_client_activity)
        //Map
        tilClientAddress = findViewById(R.id.til_client_address)
        tilClientNumber = findViewById(R.id.til_client_number)
        tilClientName = findViewById(R.id.til_client_name)
        edtClientAddress = findViewById(R.id.edt_client_address)
        edtClientName = findViewById(R.id.edt_client_name)
        edtClientNumber = findViewById(R.id.edt_client_number)
        imgClient = findViewById(R.id.iv_client)
        btnClientSave = findViewById(R.id.btn_client_save)
        parentClient = findViewById(R.id.client_parent)
        //CLasses
        database = MandiManagement(this)
        validations = ValidationUtils(this)
        preferences = AppPreferences(this)

        //listener
        btnClientSave!!.setOnClickListener(this)
        imgClient!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v === btnClientSave) {
            if (validations!!.validateName(tilClientName, edtClientName)
                    && validations!!.validateNumber(tilClientNumber, edtClientNumber)
                    && validations!!.validateName(tilClientAddress, edtClientAddress)) {
                if (!database!!.isClientExist(edtClientNumber!!.text.toString().trim { it <= ' ' })) {
                    insertClient()
                } else {
                    UiUtils.createSnackBar(this, parentClient, "User already exist")
                }
            } else {
                UiUtils.createSnackBar(this, parentClient, "Field cn't be empty")
            }
        }
        if (v === imgClient) {
            checkSelfPermission()
        }
    }

    private fun checkSelfPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            //Step 2 check for self permission
            if (checkAndRequestPermission()) {
                //Permission do things
                dispatchTakePictureIntent()
            }
        } else {
            dispatchTakePictureIntent()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            val photoFile: File?
            photoFile = createImageFile()
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this,
                        StringValues.FILE_AUTHORITY,
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, REQUEST_CAMERA_PERMISSION_CODE)
            }
        }
    }

    private fun createImageFile(): File? {
        // Create an image file name
        val image = ImageUtils.getFile("/mandi/images/client")
        // Save a file: path for use with ACTION_VIEW intents*/
        if (image != null) {
            currentPhotoPath = image.absolutePath
        }
        return image
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

    fun insertClient() {
        var clientNumber = edtClientNumber!!.text.toString()
        if (!clientNumber.contains("+91")) {
            clientNumber = "+91$clientNumber"
        }
        val values = ContentValues()
        values.put(ClientValues.CLIENT_NAME, edtClientName!!.text.toString())
        values.put(ClientValues.CLIENT_PARENT_ID, preferences!!.userId)
        values.put(ClientValues.CLIENT_ADDRESS, edtClientAddress!!.text.toString())
        values.put(ClientValues.CLIENT_NUMBER, clientNumber)
        values.put(ClientValues.CLIENT_AMOUNT, "0")
        values.put(ClientValues.CLIENT_IMAGE, currentPhotoPath)
        clientId = database!!.insertClientData(values)
        if (clientId != -1L) {
            UiUtils.createSnackBar(this, parentClient, "Added")
            finish()
        } else {
            UiUtils.createSnackBar(this, parentClient, "Something went wrong")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE && resultCode == RESULT_OK) {
            if (!currentPhotoPath.isEmpty()) {
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                val rotate = ImageUtils.getCorrectImageAngle(currentPhotoPath, bitmap)
                imgClient!!.setImageBitmap(rotate!!)
                //update image in db
                if (clientId != -1L) {
                    val cv = ContentValues()
                    cv.put(ClientValues.CLIENT_IMAGE, currentPhotoPath)
                    val id = database!!.updateClientImage(clientId, cv)
                    if (id != -1L) {
                        UiUtils.createSnackBar(this, parentClient, "Added")
                    } else {
                        UiUtils.createSnackBar(this, parentClient, "Something went wrong")
                    }
                }
            }
        } else {
            UiUtils.createSnackBar(this, parentClient, "Something went wrong")
        }
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
                dispatchTakePictureIntent()
            } else {
                for ((currentPerm) in grantResultMap) {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, currentPerm)) {
                      /*  if (dialog == null && !this.isFinishing) {
                            dialog = DialogUtils.showDialogBox(this,
                                    "This app needs permission for work without problems",
                                    "Yes, Grant Permissions",
                                    { dialogInterface: DialogInterface, i: Int ->
                                        dialogInterface.dismiss()
                                        checkAndRequestPermission()
                                    },
                                    "No, Exit app",
                                    { dialogInterface: DialogInterface, i: Int ->
                                        dialogInterface.dismiss()
                                        finish()
                                    },
                                    false)
                        }*/
                    } else {
                        //Denied with "never ask again"
                        /*if (dialog == null && !this.isFinishing) {
                            DialogUtils.showDialogBox(this,
                                    "You have denied some permission. Allow all permissions at [Settings] > [Permissions]",
                                    "Go to Settings",
                                    { dialogInterface: DialogInterface, i: Int ->
                                        dialogInterface.dismiss()
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", packageName, null))
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    },
                                    "No, Exit",
                                    { dialogInterface: DialogInterface, i: Int ->
                                        dialogInterface.dismiss()
                                        finish()
                                    },
                                    false)
                            break
                        }*/
                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    companion object {
        private const val REQUEST_CAMERA_PERMISSION_CODE = 100
        private const val REQUEST_PERMISSION_CODE = 101
    }
}