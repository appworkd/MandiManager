package com.appwork.mandisamiti

import android.Manifest
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.appwork.database.MandiManagement
import com.appwork.databaseUtils.EntryContract.EntryColumns
import com.appwork.fragments.DatePickerFragment
import com.appwork.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import java.io.File
import java.text.DateFormat
import java.util.*

class CalculateActivity : AppCompatActivity(), View.OnClickListener, OnDateSetListener {
    val TAG = this.javaClass.simpleName
    private var tilComName: TextInputLayout? = null
    private var tilComRate: TextInputLayout? = null
    private var tilPieceCount: TextInputLayout? = null
    private var tilPieceWt: TextInputLayout? = null
    private var tilWtRemain: TextInputLayout? = null
    private var tilTotalWt: TextInputLayout? = null
    private var tilTotalPrice: TextInputLayout? = null
    private var tilLabourCharge: TextInputLayout? = null
    private var tilAdatCharge: TextInputLayout? = null
    private var tilTotalPayableAmount: TextInputLayout? = null
    private var tilTotalLbCharges: TextInputLayout? = null
    private var tilFare: TextInputLayout? = null
    private var tilPreviousAmount: TextInputLayout? = null
    private var tilPaid: TextInputLayout? = null

    //Edit Text
    private var edtComName: EditText? = null
    private var edtComRate: EditText? = null
    private var edtPieceCount: EditText? = null
    private var edtPieceWt: EditText? = null
    private var edtWtRemain: EditText? = null
    private var edtTotalWt: EditText? = null
    private var edtTotalPrice: EditText? = null
    private val edtLabourCharge: EditText? = null
    private var edtAdatCharge: EditText? = null
    private var edtTotalPayableAmount: EditText? = null
    private var edtTotalLbCharges: EditText? = null
    private var edtFare: EditText? = null
    private var edtPreviousAmount: EditText? = null
    private var edtPaid: EditText? = null
    private var imgBtnCapture: ImageButton? = null
    private var imgCaptured: ImageView? = null
    private var calView: LinearLayout? = null
    private var txtPickedDate: TextView? = null
    private var txtAddMore: TextView? = null

    //Parent Layouts
    private var contExtra: RelativeLayout? = null

    //AlertDialogPermissions;
    private var dialog: MaterialAlertDialogBuilder? = null

    //Validation
    private var validations: ValidationUtils? = null

    //Permissions
    var appPermissions = arrayOf(Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private var currentPhotoPath = ""

    //Parent View
    private var contParentWeight: ConstraintLayout? = null
    private var contParentCharges: ConstraintLayout? = null
    private var contParentExtra: ConstraintLayout? = null
    private var contParentFinalAmount: ConstraintLayout? = null
    private var calculateParent: ConstraintLayout? = null

    //boolean
    private var hideMenu = false
    private var totalCommodityBill = 0
    private val lCharges = arrayOf("8", "9", "10", "12", "15")
    private var labourChargesAdapter: ArrayAdapter<String>? = null
    private var autoCompleteLCharges: AutoCompleteTextView? = null
    private var btnCal: Button? = null

    //Strings
    private val payableAmount = 0
    private var isGave = false
    private var isCommodityValid = false
    private var isRateValid = false
    private var isPiecesValid = false
    private var isRemainWeightValid = false
    private var isWtPerPiece = false
    private var scrollCont: ScrollView? = null
    private var database: MandiManagement? = null

    //Prefs
    private var preferences: AppPreferences? = null
    private var entryId: Long = -1
    private var userId: Long = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_calculator)
        //Mapping View
        tilComName = findViewById(R.id.tilComName)
        tilComRate = findViewById(R.id.tilComRate)
        tilPieceCount = findViewById(R.id.tilPieces)
        tilPieceWt = findViewById(R.id.tilWt)
        tilWtRemain = findViewById(R.id.tilRemainWt)
        tilTotalWt = findViewById(R.id.tilTotalWt)
        tilTotalPrice = findViewById(R.id.tilGrandTotal)
        tilLabourCharge = findViewById(R.id.tilLabourCharges)
        tilAdatCharge = findViewById(R.id.tilMandiCharges)
        tilTotalPayableAmount = findViewById(R.id.tilAmountToPay)
        tilTotalLbCharges = findViewById(R.id.totalLbCharge)
        tilFare = findViewById(R.id.tilFare)
        tilPreviousAmount = findViewById(R.id.tilPreviousAmount)
        tilPaid = findViewById(R.id.tilAmountPaid)
        edtComName = findViewById(R.id.edtComName)
        edtComRate = findViewById(R.id.edtComRate)
        edtPieceCount = findViewById(R.id.edtPieces)
        edtPieceWt = findViewById(R.id.edtWt)
        edtWtRemain = findViewById(R.id.edtRemainWt)
        edtTotalWt = findViewById(R.id.edtTotalWt)
        edtTotalPrice = findViewById(R.id.edtGrandTotal)
        // edtLabourCharge = findViewById(R.id.edtLabourCharges);
        edtAdatCharge = findViewById(R.id.edtMandiCharges)
        edtTotalPayableAmount = findViewById(R.id.edtAmountToPay)
        edtTotalLbCharges = findViewById(R.id.edtTotalLbCharge)
        edtFare = findViewById(R.id.edtFare)
        edtPreviousAmount = findViewById(R.id.edtPreviousAmount)
        edtPaid = findViewById(R.id.edtAmountPaid)
        imgBtnCapture = findViewById(R.id.btnCamera)
        imgCaptured = findViewById(R.id.imgCapture)
        txtPickedDate = findViewById(R.id.selected_date)
        txtAddMore = findViewById(R.id.txtMoreFieldCalculation)
        autoCompleteLCharges = findViewById(R.id.txt_labour_charges)
        calView = findViewById(R.id.container_cal_entry)
        contExtra = findViewById(R.id.cont_extra)
        btnCal = findViewById(R.id.btn_done)
        scrollCont = findViewById(R.id.scroll_entry)
        contParentWeight = findViewById(R.id.cont_wt)
        contParentCharges = findViewById(R.id.cont_charges)
        contParentExtra = findViewById(R.id.cont_extra_fields)
        contParentFinalAmount = findViewById(R.id.contFinalAmount)
        calculateParent = findViewById(R.id.calculateContainer)
        validations = ValidationUtils(this)
        database = MandiManagement(this)
        preferences = AppPreferences(this)
        UiUtils.checkKeyBoard(this, calculateParent)
        init()
        //Get Intents
        if (intent.hasExtra(StringValues.GAVE)) {
            isGave = intent.getBooleanExtra(StringValues.GAVE, true)
        }
        if (intent.hasExtra(StringValues.USER_ID)) {
            userId = intent.getLongExtra(StringValues.USER_ID, -1)
        }
        //setListener
        calView!!.setOnClickListener(this)
        imgBtnCapture!!.setOnClickListener(this)
        txtAddMore!!.setOnClickListener(this)
        btnCal!!.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
    }

    private fun init() {
        txtPickedDate!!.setText(DateUtils.currentDate)
        labourChargesAdapter = ArrayAdapter(this, R.layout.item_text, lCharges)
        autoCompleteLCharges!!.setAdapter(labourChargesAdapter)
        autoCompleteLCharges!!.setSelection(0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_calculate, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val save = menu.findItem(R.id.save_cal)
        if (hideMenu) {
            save.isVisible = true
        } else {
            save.isVisible = false
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save_cal) {
            //Todo save data remotely/locally
            //validateAllEntries();
            val fareCharges = edtFare!!.text.toString().trim { it <= ' ' }
            val prevCharges = edtPreviousAmount!!.text.toString().trim { it <= ' ' }
            calculatePayableAmount(fareCharges, prevCharges)
            /*Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();*/
        }
        return super.onOptionsItemSelected(item)
    }

    private fun calculatePayableAmount(fareCharges: String, prevCharges: String) {
        var fareCharges = fareCharges
        var prevCharges = prevCharges
        if (fareCharges.isEmpty()) {
            fareCharges = "0"
        }
        if (prevCharges.isEmpty()) {
            prevCharges = "0"
        }
        val extraAmount = fareCharges.toInt() + prevCharges.toInt()
        if (extraAmount != 0) {
            val payableAmount = totalCommodityBill - extraAmount
            enableDisableExtraFields()
            edtPaid!!.setText(payableAmount.toString())
            //updateEntryData
            updateEntry(fareCharges, prevCharges, payableAmount.toString())
            Log.d("Calc", "Extra Amount : $extraAmount  payableAmount : $payableAmount  totalCommodityBill : $totalCommodityBill")
        }
        startActivity(Intent(this, BillDetails::class.java)
                .putExtra(EntryColumns.ENTRY_ID, entryId)
                .putExtra(StringValues.USER_ID, userId))
        finish()
    }

    private fun updateEntry(fareCharges: String, prevCharges: String, payableAmount: String) {
        val updateThread: Thread = object : Thread() {
            override fun run() {
                super.run()
                val cv = ContentValues()
                cv.put(EntryColumns.ENTRY_FARE, fareCharges)
                cv.put(EntryColumns.ENTRY_PREV_AMOUNT, prevCharges)
                cv.put(EntryColumns.ENTRY_GRANT_TOTAL, payableAmount)
                val updateId = database!!.updateEntry(entryId, cv)
                Log.e("Cal", "updateId : $updateId")
            }
        }
        updateThread.start()
    }

    private fun enableDisableExtraFields() {
        edtFare!!.isFocusable = false
        edtFare!!.isEnabled = false
        edtPreviousAmount!!.isFocusable = false
        edtPreviousAmount!!.isEnabled = false
        contParentFinalAmount!!.visibility = View.VISIBLE
        edtPaid!!.isFocusable = false
        edtPaid!!.isEnabled = false
        hideMenu = true
        invalidateOptionsMenu()
    }

    private fun validateAllEntries() {
        val commodityRate = edtComRate!!.text.toString().trim { it <= ' ' }
        val piecesCount = edtPieceCount!!.text.toString().trim { it <= ' ' }
        var wt = edtPieceWt!!.text.toString().trim { it <= ' ' }
        val remainWt = edtWtRemain!!.text.toString().trim { it <= ' ' }
        val lbCharge = autoCompleteLCharges!!.text.toString().trim { it <= ' ' }
        if (wt.isEmpty()) {
            wt = "0"
        }
        isCommodityValid = validations!!.validateName(tilComName, edtComName)
        isRateValid = validations!!.validateName(tilComRate, edtComRate)
        isPiecesValid = validations!!.validateName(tilPieceCount, edtPieceCount)
        isRemainWeightValid = validations!!.validateName(tilWtRemain, edtWtRemain)
        isWtPerPiece = validations!!.validateName(tilPieceWt, edtPieceWt)
        if (isCommodityValid or isRateValid or isPiecesValid or isRemainWeightValid or isWtPerPiece) {
            hideFilledTextInputField()
            if (btnCal!!.visibility == View.VISIBLE) {
                btnCal!!.visibility = View.GONE
            }
            doCalculation(commodityRate, piecesCount, wt, remainWt, lbCharge)
        }
    }

    private fun doCalculation(commodityRate: String, piecesCount: String, wt: String, remainWt: String, lbCharge: String) {
        edtWtRemain!!.clearFocus()
        UiUtils.hideSoftKeyboard(calculateParent, this)
        var pieceCountValue = piecesCount.toFloat()
        val wtValue = wt.toFloat()
        val remainWtValue = remainWt.toFloat()
        val totalWt = pieceCountValue * wtValue + remainWtValue
        edtTotalWt!!.setText(totalWt.toString())
        val grandTotalAmount = totalWt * commodityRate.toFloat() / 100
        val grandTotalComplete = Math.ceil(grandTotalAmount.toDouble()).toInt()
        edtTotalPrice!!.setText(grandTotalComplete.toString())
        val adatCharge = grandTotalComplete * 6 / 1000
        edtAdatCharge!!.setText(adatCharge.toString())
        if (remainWtValue > 0) {
            pieceCountValue++
        }
        val totalLabourCharge = lbCharge.toInt() * pieceCountValue.toInt()
        edtTotalLbCharges!!.setText(totalLabourCharge.toString())
        var totalAmountToPay = (grandTotalAmount - (totalLabourCharge + adatCharge)).toInt()
        val modAmount = totalAmountToPay % 10
        totalAmountToPay = if (modAmount < 5) {
            totalAmountToPay - modAmount
        } else {
            val temp = 10 - modAmount
            totalAmountToPay + temp
        }
        Log.d("Calculate", "modAmount : $modAmount")
        totalCommodityBill = totalAmountToPay
        if (contParentCharges!!.visibility == View.GONE) {
            contParentCharges!!.visibility = View.VISIBLE
            edtTotalPayableAmount!!.setText(totalAmountToPay.toString())
        }
        if (contParentCharges!!.visibility == View.VISIBLE) {
            scrollToBottom()
            val t1: Thread = object : Thread() {
                override fun run() {
                    super.run()
                    insertEntryDataInDb()
                }
            }
            t1.start()
        }
        hideMenu = true
        invalidateOptionsMenu()
    }

    private fun insertEntryDataInDb() {
        val cv = ContentValues()
        cv.put(EntryColumns.ENTRY_NAME, edtComName!!.text.toString())
        cv.put(EntryColumns.ENTRY_USER_ID, userId)
        cv.put(EntryColumns.ORG_NAME, "")
        cv.put(EntryColumns.ENTRY_NOP, edtPieceCount!!.text.toString())
        cv.put(EntryColumns.ENTRY_PIECE_WT, edtPieceWt!!.text.toString())
        cv.put(EntryColumns.ENTRY_REMAIN_WT, edtWtRemain!!.text.toString())
        cv.put(EntryColumns.ENTRY_TOTAL_WT, edtTotalWt!!.text.toString())
        cv.put(EntryColumns.ENTRY_TOTAL_AMOUNT, edtTotalPrice!!.text.toString())
        cv.put(EntryColumns.ENTRY_LB_RATE, autoCompleteLCharges!!.text.toString())
        cv.put(EntryColumns.ENTRY_TOTAL_LB_CHARGE, edtTotalLbCharges!!.text.toString())
        cv.put(EntryColumns.ENTRY_ADAT_CHARGE, edtAdatCharge!!.text.toString())
        cv.put(EntryColumns.ENTRY_COMMODITY_PRICE, totalCommodityBill)
        cv.put(EntryColumns.ENTRY_FARE, "0")
        cv.put(EntryColumns.ENTRY_PREV_AMOUNT, "0")
        cv.put(EntryColumns.ENTRY_GRANT_TOTAL, totalCommodityBill)
        cv.put(EntryColumns.ENTRY_ENTRY_DATE, DateUtils.currentDateInMillis)
        cv.put(EntryColumns.ENTRY_ENTRY_ATTACHMENT, currentPhotoPath)
        cv.put(EntryColumns.ENTRY_ENTRY_IS_GIVEN, "true")
        entryId = database!!.insertEntry(cv)
    }

    override fun onClick(view: View) {
        val viewId = view.id
        when (viewId) {
            R.id.container_cal_entry -> showDatePickerDialog(view)
            R.id.btnCamera ->                 //Step 1
                //Check the platform if grater than lollipop(23)
                checkSelfPermission()
            R.id.txtMoreFieldCalculation -> hideShowExtraField()
            R.id.btn_done -> validateAllEntries()
        }
    }

    private fun hideFilledTextInputField() {
        hideTextInput(edtComRate)
        hideTextInput(edtPieceCount)
        hideTextInput(edtPieceWt)
        hideTextInput(edtWtRemain)
    }

    private fun hideTextInput(editText: EditText?) {
        editText!!.isFocusable = false
        editText.isEnabled = false
    }

    private fun showTextInput(editText: EditText) {
        editText.isFocusable = true
        editText.isEnabled = true
    }

    private fun hideShowExtraField() {
        txtAddMore!!.visibility = View.GONE
        contParentExtra!!.visibility = View.VISIBLE
        scrollToBottom()
    }

    private fun scrollToBottom() {
        Handler().postDelayed({
            scrollCont!!.isFillViewport = true
            scrollCont!!.fullScroll(View.FOCUS_DOWN)
        }, 1000)
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
                    TransactionLogActivity.Companion.REQUEST_PERMISSION_CODE)
            return false
        }
        return true
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            photoFile = createImageFile()
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(this,
                        StringValues.FILE_AUTHORITY,
                        photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                startActivityForResult(takePictureIntent, TransactionLogActivity.Companion.REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    private fun createImageFile(): File? {
        // Create an image file name
        val image = ImageUtils.getFile("/mandi/images/order")
        // Save a file: path for use with ACTION_VIEW intents*/
        if (image != null) {
            currentPhotoPath = image.absolutePath
        }
        return image
    }

    fun showDatePickerDialog(v: View?) {
        val newFragment: DialogFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == TransactionLogActivity.Companion.REQUEST_PERMISSION_CODE) {
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
                       /* if (dialog == null && !this.isFinishing) {
                            dialog = DialogUtils.showDialogBox(this,
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
                            dialog!!.show()
                        }*/
                    } else {
                        //Denied with "never ask again"
                      /*  if (dialog == null && !this.isFinishing) {
                            dialog = DialogUtils.showDialogBox(this,
                                    "You have denied some permission. Allow all permissions at [Settings] > [Permissions]",
                                    "Go to Settings",
                                    { dialogInterface, i ->
                                        dialogInterface.dismiss()
                                        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                                Uri.fromParts("package", packageName, null))
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        startActivity(intent)
                                    },
                                    "No, Exit",
                                    { dialogInterface, i ->
                                        dialogInterface.dismiss()
                                        finish()
                                    },
                                    false)
                            dialog!!.show()
                            break
                        }*/
                    }
                }
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == TransactionLogActivity.Companion.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (!currentPhotoPath.isEmpty()) {
                val bitmap = BitmapFactory.decodeFile(currentPhotoPath)
                val rotate = ImageUtils.getCorrectImageAngle(currentPhotoPath, bitmap)
                imgCaptured!!.visibility = View.VISIBLE
                imgCaptured!!.setImageBitmap(rotate)
            }
        }
    }

    override fun onDateSet(datePicker: DatePicker, year: Int, month: Int, day: Int) {
        val calendar = Calendar.getInstance()
        calendar[year, month] = day
        val currentDate = calendar.time
        val selectedDateStr = DateFormat.getDateInstance().format(currentDate)
        txtPickedDate!!.text = selectedDateStr
    }
}