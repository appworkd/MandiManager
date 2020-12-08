package com.appwork.mandisamiti

import android.Manifest
import android.app.AlertDialog
import android.app.DatePickerDialog.OnDateSetListener
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
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.appwork.database.MandiManagement
import com.appwork.databaseUtils.ClientContract.ClientValues
import com.appwork.databaseUtils.TransactionContract.TransactionColumns
import com.appwork.fragments.DatePickerFragment
import com.appwork.mandisamiti.TransactionLogActivity
import com.appwork.utils.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.text.DateFormat
import java.util.*

class TransactionLogActivity : AppCompatActivity(), View.OnClickListener, OnDateSetListener {
    val TAG = this.javaClass.simpleName
    var appPermissions = arrayOf(Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var tlTrans: Toolbar? = null
    private var edtAmount: TextInputEditText? = null
    private var tilAmount: TextInputLayout? = null
    private var tilOtherInfo: TextInputLayout? = null
    private var edtOtherInfo: TextInputEditText? = null
    private var imgCamera: ImageButton? = null
    private var contCal: LinearLayout? = null
    private var imgSelected: ImageView? = null
    private var selectedDate: TextView? = null
    private var currentPhotoPath: String? = null
    private var btnSave: Button? = null
    private var imgCont: CardView? = null
    private var btnClose: ImageButton? = null

    //Validation
    private var validationUtils: ValidationUtils? = null
    private var database: MandiManagement? = null
    private var isGiven = false
    private var strIsGiven = "false"
    private var transId: Long = -1
    private var userId: Long = 0
    private var isGave = ""
    private var clientAmountTotal = ""
    private var parentLayout: ConstraintLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_log)
        //Map
        tlTrans = findViewById(R.id.tl_trans_log)
        tilAmount = findViewById(R.id.til_amount)
        edtAmount = findViewById(R.id.edt_amount)
        tilOtherInfo = findViewById(R.id.til_trans_details)
        edtOtherInfo = findViewById(R.id.edt_trans_detail)
        imgCamera = findViewById(R.id.btnCamera)
        contCal = findViewById(R.id.container_cal_entry)
        selectedDate = findViewById(R.id.selected_date)
        btnSave = findViewById(R.id.btnSave)
        imgSelected = findViewById(R.id.img_selected)
        imgCont = findViewById(R.id.imgCont)
        btnClose = findViewById(R.id.btn_cancel)
        parentLayout = findViewById(R.id.parent_transaction)
        //Set current Date to text
        val calendar = Calendar.getInstance()
        val today = calendar.time
        val currentDate = DateFormat.getDateInstance().format(today)
        selectedDate!!.setText(currentDate)
        validationUtils = ValidationUtils(this)
        database = MandiManagement(this)
        //Intent
        if (intent.hasExtra(StringValues.GAVE)) {
            isGiven = intent.getBooleanExtra(StringValues.GAVE, false)
        }
        if (intent.hasExtra(StringValues.USER_ID)) {
            userId = intent.getLongExtra(StringValues.USER_ID, -1)
        }
        strIsGiven = if (isGiven) {
            "true"
        } else {
            "false"
        }
        //SetListener
        imgCamera!!.setOnClickListener(this)
        btnSave!!.setOnClickListener(this)
        btnClose!!.setOnClickListener(this)
        contCal!!.setOnClickListener(this)
        tlTrans!!.setNavigationOnClickListener(View.OnClickListener { view: View? -> onBackPressed() })
    }

    override fun onResume() {
        super.onResume()
        val cursor = database!!.getClientAmount(userId)
        while (cursor!!.moveToNext()) {
            isGave = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_IS_GAVE))
            clientAmountTotal = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_AMOUNT))
        }
    }

    override fun onClick(view: View) {
        if (view === imgCamera) {
            //CheckPermission
            //Step 1
            //Check the platform if grater than lollipop(23)
            if (Build.VERSION.SDK_INT >= 23) {
                //Step 2 check for self permission
                if (checkAndRequestPermission()) {
                    //Permission do things
                    dispatchTakePictureIntent()
                }
            }
        } else if (view === btnSave) {
            if (validationUtils!!.validateName(tilAmount, edtAmount)) {
                insertData()
                updateClientAmount(isGave, clientAmountTotal)
                finish()
            }
        } else if (view === btnClose) {
            if (imgCont!!.visibility == View.VISIBLE) {
                imgCont!!.visibility = View.GONE
            }
        } else if (view === contCal) {
            showDatePickerDialog(contCal)
        }
    }

    private fun updateClientAmount(clientFlag: String, amount: String) {
        val currentAmount = Objects.requireNonNull(edtAmount!!.text).toString()
        val currentAmountInt = currentAmount.toInt()
        var updatedAmount = 0
        var tempFlag = false
        val amountInt = amount.toInt()
        if (!isGiven) {
            if (clientFlag.equals("true", ignoreCase = true)) {
                updatedAmount = currentAmountInt + amountInt
                tempFlag = true
            } else {
                if (amountInt > currentAmountInt) {
                    updatedAmount = amountInt - currentAmountInt
                    tempFlag = true
                } else {
                    updatedAmount = currentAmountInt - amountInt
                }
            }
        } else {
            if (clientFlag.equals("true", ignoreCase = true)) {
                if (amountInt > currentAmountInt) {
                    updatedAmount = amountInt - currentAmountInt
                    tempFlag = true
                } else {
                    updatedAmount = currentAmountInt - amountInt
                    tempFlag = false
                }
            } else {
                updatedAmount = amountInt + currentAmountInt
                tempFlag = false
            }
        }
        insertUpdatedData(updatedAmount, tempFlag)
    }

    private fun insertUpdatedData(updatedAmount: Int, tempFlag: Boolean) {
        Thread {
            val contentValues = ContentValues()
            contentValues.put(ClientValues.CLIENT_AMOUNT, updatedAmount)
            if (tempFlag) {
                contentValues.put(ClientValues.CLIENT_IS_GAVE, "true")
            } else {
                contentValues.put(ClientValues.CLIENT_IS_GAVE, "false")
            }
            val updateId = database!!.updateClientAmount(userId, contentValues)
            if (updateId != -1) {
                UiUtils.createSnackBar(this@TransactionLogActivity, parentLayout, "Updated")
            }
        }
    }

    private fun insertData() {
        val t: Thread = object : Thread() {
            override fun run() {
                super.run()
                val values = ContentValues()
                values.put(TransactionColumns.TRAN_DATE, DateUtils.currentDateInMillis)
                values.put(TransactionColumns.TRAN_USER_ID, userId)
                values.put(TransactionColumns.TRAN_REASON, Objects.requireNonNull(edtOtherInfo!!.text).toString().trim { it <= ' ' })
                values.put(TransactionColumns.TRAN_AMOUNT, Objects.requireNonNull(edtAmount!!.text).toString().trim { it <= ' ' })
                values.put(TransactionColumns.TRAN_ATTACHMENT, currentPhotoPath)
                values.put(TransactionColumns.TRAN_IS_GIVEN, strIsGiven)
                transId = database!!.insertTransaction(values)
                if (transId != -1L) {
                    if (!isFinishing) {
                        Toast.makeText(this@TransactionLogActivity, "Amount added", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        t.start()
    }

    fun showDatePickerDialog(v: View?) {
        val newFragment: DialogFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
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
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun createImageFile(): File? {
        // Create an image file name
        val image = ImageUtils.getFile("/mandi/images/entry")
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
                for ((currentPerm) in grantResultMap) {
                  /*  if (ActivityCompat.shouldShowRequestPermissionRationale(this, currentPerm)) {
                        showDialogBox(
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
                    } else {
                        //Denied with "never ask again"
                        showDialogBox(
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
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (!currentPhotoPath!!.isEmpty()) {
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                val rotate = ImageUtils.getCorrectImageAngle(currentPhotoPath, bitmap)
                imgCont!!.visibility = View.VISIBLE
                imgSelected!!.setImageBitmap(rotate)
            }
        }
    }

    fun showDialogBox(
            msg: String?,
            positiveLabel: String?,
            positiveClick: DialogInterface.OnClickListener?,
            negativeLabel: String?,
            negativeListener: DialogInterface.OnClickListener?,
            isCancelable: Boolean) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(this.resources.getString(R.string.app_name))
                .setCancelable(isCancelable)
                .setPositiveButton(positiveLabel, positiveClick)
                .setNegativeButton(negativeLabel, negativeListener)
                .setMessage(msg)
        val dialog = builder.create()
        dialog.show()
    }

    override fun onDateSet(view: DatePicker, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar[year, month] = day
        val currentDate = calendar.time
        val selectedDateStr = DateFormat.getDateInstance().format(currentDate)
        selectedDate!!.text = selectedDateStr
    }

    companion object {
        const val REQUEST_PERMISSION_CODE = 100
        const val REQUEST_IMAGE_CAPTURE = 101
    }
}