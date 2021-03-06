package com.appwork.ui.client

import android.Manifest
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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.lifecycle.Observer
import com.appwork.data.DataPref
import com.appwork.data.entities.ClientModel
import com.appwork.mandisamiti.R
import com.appwork.mandisamiti.databinding.ActivityAddClientActivityBinding
import com.appwork.utils.*
import com.appwork.utils.StringValues.FILE_AUTHORITY
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.android.synthetic.main.activity_add_client_activity.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.util.*

class AddClientActivity : AppCompatActivity(),
        View.OnClickListener, KodeinAware,
        IAddClientManager {
    private var photoFile: File? = null
    override val kodein by kodein()
    private val factory: ClientVMFactory by instance()
    private var imgClient: CircleImageView? = null
    private val prefs: DataPref by instance()

    //Classes
    private var currentPhotoPath = ""
    private val appPermissions = arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var clientId: Long = -1
    private var dialog: MaterialAlertDialogBuilder? = null
    private var clientVM: ClientListVM? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val clientDataBinding: ActivityAddClientActivityBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_add_client_activity)
        clientVM = ViewModelProvider(this, factory).get(ClientListVM::class.java)
        clientDataBinding.clientVM = clientVM
        clientVM!!.addClientListener = this
        prefs.userId.asLiveData().observe(this, Observer {
            it?.let {
                clientVM!!.parentId = it
            }
        })
    }

    override fun onClick(v: View) {
        /*if (v === btnClientSave) {
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
        }*/
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
            photoFile = createImageFile()
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this,
                        FILE_AUTHORITY,
                        photoFile!!)
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
        if (neededPermissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(this,
                    neededPermissions.toTypedArray(),
                    REQUEST_PERMISSION_CODE)
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CAMERA_PERMISSION_CODE && resultCode == RESULT_OK) {
            if (photoFile != null) {
                val temp = currentPhotoPath
                /*val bitmap = BitmapFactory.decodeFile(temp)
                val rotate = ImageUtils.getCorrectImageAngle(temp, bitmap)
                iv_client!!.setImageBitmap(rotate!!)*/
//                    clientVM!!.clientImage.set(temp)
                clientVM!!.choosedImage.value = temp
                //clientVM!!.choose("")
                //clientVM!!.choose(currentPhotoPath)
            }
        } else {
            UiUtils.createSnackBar(this, client_parent, "Something went wrong")
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
                        if (dialog == null && !this.isFinishing) {
                            dialog = this.createDialog(getString(R.string.app_name),
                                    "This app needs permission for work without problems",
                                    "Yes, Grant Permissions",
                                    "No, Exit app",
                                    false,
                                    DialogInterface.OnClickListener { dialog, _ ->
                                        dialog.dismiss()
                                        checkAndRequestPermission()
                                    },
                                    DialogInterface.OnClickListener { dialog, _ ->
                                        dialog.dismiss()
                                        finish()
                                    }
                            )
                        }
                    } else {
                        //Denied with "never ask again"
                        if (dialog == null && !this.isFinishing) {
                            dialog = this.createDialog(getString(R.string.app_name),
                                    "You have denied some permission. Allow all permissions at [Settings] > [Permissions]",
                                    "Go to Settings",
                                    "No, Exit app",
                                    false,
                                    DialogInterface.OnClickListener { dialog, _ ->
                                        dialog.dismiss()
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", packageName, null))
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    },
                                    DialogInterface.OnClickListener { dialog, _ ->
                                        dialog.dismiss()
                                        finish()
                                    }
                            )
                            break
                        }
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

    override fun onError(errorCode: Int) {
        when (errorCode) {
            1 -> {
                til_client_name?.apply {
                    this.error = this@AddClientActivity.getString(R.string.enter_name)
                    this.isErrorEnabled = true
                }
            }
            2 -> {
                til_client_number?.apply {
                    this.error = this@AddClientActivity.getString(R.string.enter_number)
                    this.isErrorEnabled = true
                }
            }
            3 -> {
                til_client_address?.apply {
                    this.error = this@AddClientActivity.getString(R.string.enter_address)
                    this.isErrorEnabled = true
                }
            }
            6 -> {
                showToast("Same Number exist")
            }
        }
    }

    override fun onSuccess(client: ClientModel) {
        finish()
    }

    override fun moveToAddClient() {
    }

    override fun capturePhoto() {
        checkSelfPermission()
    }
}