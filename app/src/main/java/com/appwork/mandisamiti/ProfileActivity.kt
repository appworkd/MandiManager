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
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.text.InputType
import android.text.TextUtils.TruncateAt
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.appwork.database.MandiManagement
import com.appwork.databaseUtils.UserContract.UserEntry
import com.appwork.mandisamiti.ProfileActivity
import com.appwork.utils.*
import com.bumptech.glide.Glide
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.util.*

class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private var imgProfile: CircleImageView? = null
    private var tilTin: TextInputLayout? = null
    private var tilName: TextInputLayout? = null
    private var tilEmail: TextInputLayout? = null
    private var tilAddress: TextInputLayout? = null
    private var tilCompanyName: TextInputLayout? = null
    private var tilContactNumber: TextInputLayout? = null
    private var edtTin: TextInputEditText? = null
    private var edtName: TextInputEditText? = null
    private var edtEmail: TextInputEditText? = null
    private var edtAddress: TextInputEditText? = null
    private var edtCompanyName: TextInputEditText? = null
    private var edtContactNumber: TextInputEditText? = null
    private var btnUpdate: Button? = null
    private var tlProfile: Toolbar? = null
    private var alertDialog: MaterialAlertDialogBuilder? = null
    private var parentLayout: ConstraintLayout? = null

    //Permissions
    private val permissions = arrayOf(Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE)
    private var database: MandiManagement? = null
    private var preferences: AppPreferences? = null
    private var currentPhotoPath = ""
    private val options = arrayOf<CharSequence>(
            "Choose from gallery",
            "Capture image",
            "Cancel")
    private var imageUri: Uri? = null
    private var userId: Long = 0
    private var updateId = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        //MapView
        imgProfile = findViewById(R.id.imageViewProfile)
        tilTin = findViewById(R.id.tilTinNumber)
        tilName = findViewById(R.id.tilName)
        tilEmail = findViewById(R.id.tilEmail)
        tilAddress = findViewById(R.id.tilAddress)
        tilCompanyName = findViewById(R.id.tilCompany)
        tilContactNumber = findViewById(R.id.tilContact)
        edtTin = findViewById(R.id.edtTinNumber)
        edtName = findViewById(R.id.edtName)
        edtEmail = findViewById(R.id.edtEmail)
        edtAddress = findViewById(R.id.edtAddress)
        edtCompanyName = findViewById(R.id.edtCompany)
        edtContactNumber = findViewById(R.id.edtContact)
        tlProfile = findViewById(R.id.tl_profile)
        btnUpdate = findViewById(R.id.btnUpdate)
        parentLayout = findViewById(R.id.profile_cont)
        //init i.e. initially disable touch/edit any field
        setSupportActionBar(tlProfile)
        tlProfile!!.setNavigationOnClickListener(View.OnClickListener { view: View? -> onBackPressed() })
        database = MandiManagement(this)
        preferences = AppPreferences(this)
        userId = preferences!!.userId
        // init();
        //Listeners
        btnUpdate!!.setOnClickListener(this)
        imgProfile!!.setOnClickListener(this)
    }

    private fun init() {
        val currentUser = database!!.getUserInfo(preferences!!.userEmail)
        edtName!!.setText(currentUser.userName)
        edtCompanyName!!.setText(currentUser.userDesc)
        edtEmail!!.setText(currentUser.userEmail)
        edtAddress!!.setText(currentUser.address)
        edtContactNumber!!.setText(currentUser.contactNumber)
        if (currentUser.userTinNumber == null || currentUser.userTinNumber!!.isEmpty()) {
            edtTin!!.setText("")
        } else {
            edtTin!!.setText(currentUser.userTinNumber)
        }
        Glide.with(this)
                .load(currentUser.userImage)
                .placeholder(R.drawable.ic_user)
                .centerCrop()
                .into(imgProfile!!)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_profile, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.edit_profile) {
            enableUpdateProfile()
        } else {
            //do the logout operation
            Toast.makeText(this, "Logout successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        init()
    }

    private fun enableUpdateProfile() {
        enableEditTexts(edtName)
        enableEditTexts(edtCompanyName)
        enableEditTexts(edtAddress)
        enableEditTexts(edtEmail)
        enableEditTexts(edtContactNumber)
        enableEditTexts(edtTin)
        edtName!!.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        edtCompanyName!!.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        edtAddress!!.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
        edtAddress!!.maxLines = 2
        edtAddress!!.ellipsize = TruncateAt.END
        edtEmail!!.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
        edtContactNumber!!.inputType = InputType.TYPE_CLASS_NUMBER
        edtTin!!.inputType = InputType.TYPE_CLASS_NUMBER
        btnUpdate!!.visibility = View.VISIBLE
    }

    private fun enableEditTexts(edtText: EditText?) {
        edtText!!.isEnabled = true
        edtText.isFocusableInTouchMode = true
        edtText.isFocusable = true
        edtText.inputType = InputType.TYPE_TEXT_VARIATION_PERSON_NAME
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.btnUpdate -> updateUserProfile()
            R.id.imageViewProfile -> showImageChoose()
        }
    }

    private fun updateUserProfile() {
        val t: Thread = object : Thread() {
            override fun run() {
                super.run()
                val values = ContentValues()
                values.put(UserEntry.USER_NAME, Objects.requireNonNull(edtName!!.text).toString())
                values.put(UserEntry.USER_EMAIL, Objects.requireNonNull(edtEmail!!.text).toString())
                values.put(UserEntry.USER_DESC, Objects.requireNonNull(edtCompanyName!!.text).toString())
                values.put(UserEntry.USER_PHONE, Objects.requireNonNull(edtContactNumber!!.text).toString())
                values.put(UserEntry.USER_TIL_NUMBER, Objects.requireNonNull(edtTin!!.text).toString())
                updateId = database!!.updateUser(userId, values)
                if (updateId != -1) {
                    Handler().postDelayed({
                        UiUtils.createSnackBar(this@ProfileActivity, parentLayout, "Added")
                        finish()
                    }, 2000)
                } else {
                    UiUtils.createSnackBar(this@ProfileActivity, parentLayout, "Something went wrong")
                }
            }
        }
        t.start()
    }

    fun updateProfileImage(strImage: String?): Int {
        val contentValues = ContentValues()
        contentValues.put(UserEntry.USER_IMAGE, strImage)
        return database!!.updateProfilePic(userId, contentValues)
    }

    private fun showImageChoose() {
        val builder = MaterialAlertDialogBuilder(this)
        builder.setTitle(this.resources.getString(R.string.choose_option))
        builder.setCancelable(false)
                .setItems(options) { dialog: DialogInterface, which: Int ->
                    when (which) {
                        0 -> {
                            pickImageFromGallery()
                            dialog.dismiss()
                        }
                        1 -> {
                            if (Build.VERSION.SDK_INT >= 23) {
                                //Step 2 check for self permission
                                if (checkAndRequestPermission()) {
                                    //Permission do things
                                    dispatchTakePictureIntent()
                                }
                            } else {
                                dispatchTakePictureIntent()
                            }
                            dialog.dismiss()
                        }
                        2 -> dialog.dismiss()
                    }
                }
        builder.show()
    }

    private fun pickImageFromGallery() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
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
        val image = ImageUtils.getFile("/mandi/images/profile" + preferences!!.userId)
        // Save a file: path for use with ACTION_VIEW intents*/
        if (image != null) {
            currentPhotoPath = image.absolutePath
        }
        return image
    }

    private fun checkAndRequestPermission(): Boolean {
        val neededPermissions: MutableList<String> = ArrayList()
        for (currentPerm in permissions) {
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
                  /*  if (ActivityCompat.shouldShowRequestPermissionRationale(this, currentPerm)) {
                        if (alertDialog == null && !this.isFinishing) {
                            alertDialog = DialogUtils.showDialogBox(this,
                                    this.resources.getString(R.string.perm_msg),
                                    this.resources.getString(R.string.perm_pos_label),
                                    { dialogInterface: DialogInterface, i: Int ->
                                        dialogInterface.dismiss()
                                        checkAndRequestPermission()
                                    },
                                    this.resources.getString(R.string.perm_neg_label),
                                    { dialogInterface: DialogInterface, i: Int ->
                                        dialogInterface.dismiss()
                                        finish()
                                    },
                                    false)
                            alertDialog!!.show()
                        }
                    } else {
                        //Denied with "never ask again"
                        if (alertDialog == null && !this.isFinishing) {
                            alertDialog = DialogUtils.showDialogBox(this,
                                    this.resources.getString(R.string.perm_settings_msg),
                                    this.resources.getString(R.string.perm_settings_pos_label),
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
                            alertDialog!!.show()
                            break
                        }
                    }*/
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA_PERMISSION_CODE) {
                if (!currentPhotoPath.isEmpty()) {
                    val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                    val rotate = ImageUtils.getCorrectImageAngle(currentPhotoPath, bitmap)
                    imgProfile!!.setImageBitmap(rotate!!)
                    //update image in db
                    if (userId != -1L) {
                        val t: Thread = object : Thread() {
                            override fun run() {
                                super.run()
                                val id = updateProfileImage(currentPhotoPath).toLong()
                                if (id != -1L) {
                                    UiUtils.createSnackBar(this@ProfileActivity, parentLayout, "Added")
                                } else {
                                    UiUtils.createSnackBar(this@ProfileActivity, parentLayout, "Something went wrong")
                                }
                            }
                        }
                        t.start()
                    }
                }
            } else if (requestCode == PICK_IMAGE_REQUEST) {
                if (data != null && data.data != null) {
                    imageUri = data.data
                    val t: Thread = object : Thread() {
                        override fun run() {
                            super.run()
                            if (userId != -1L) {
                                val id = updateProfileImage(imageUri.toString()).toLong()
                                if (id != -1L) {
                                    UiUtils.createSnackBar(this@ProfileActivity, parentLayout, "Added")
                                } else {
                                    UiUtils.createSnackBar(this@ProfileActivity, parentLayout, "Something went wrong")
                                }
                            }
                        }
                    }
                    t.start()
                    Glide.with(this)
                            .load(imageUri)
                            .centerCrop()
                            .placeholder(R.drawable.ic_user)
                            .into(imgProfile!!)
                }
            }
        } else {
            UiUtils.createSnackBar(this, parentLayout, "Something went wrong")
        }
    }

    companion object {
        private const val REQUEST_PERMISSION_CODE = 100
        private const val REQUEST_CAMERA_PERMISSION_CODE = 101
        private const val PICK_IMAGE_REQUEST = 102
    }
}