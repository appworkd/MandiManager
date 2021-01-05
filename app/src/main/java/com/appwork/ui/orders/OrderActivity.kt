package com.appwork.ui.orders

import android.Manifest
import android.app.DatePickerDialog.OnDateSetListener
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import com.appwork.data.entities.OrderModel
import com.appwork.database.MandiManagement
import com.appwork.databaseUtils.EntryContract.EntryColumns
import com.appwork.fragments.DatePickerFragment
import com.appwork.mandisamiti.BillDetails
import com.appwork.mandisamiti.R
import com.appwork.mandisamiti.TransactionLogActivity
import com.appwork.mandisamiti.databinding.ActivityOrderBinding
import com.appwork.utils.*
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import kotlinx.android.synthetic.main.activity_order.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance
import java.io.File
import java.text.DateFormat
import java.time.LocalDate
import java.util.*
import kotlin.math.ceil

class OrderActivity : AppCompatActivity(), View.OnClickListener,
        OnDateSetListener,
        KodeinAware, IOrderManager {
    val TAG = this.javaClass.simpleName
    private var tilComName: TextInputLayout? = null
    private var tilComRate: TextInputLayout? = null
    private var tilPieceCount: TextInputLayout? = null
    private var tilPieceWt: TextInputLayout? = null
    private var tilWtRemain: TextInputLayout? = null

    //Edit Text
    private var edtComName: EditText? = null

    //private var edtComRate: EditText? = null
    private var edtPieceCount: EditText? = null
    private var edtPieceWt: EditText? = null
    private var edtWtRemain: EditText? = null

    private var edtTotalPrice: EditText? = null
    private var edtAdatCharge: EditText? = null
    private var edtTotalPayableAmount: EditText? = null
    private var edtTotalLbCharges: EditText? = null
    private var edtFare: EditText? = null
    private var edtPreviousAmount: EditText? = null
    private var edtPaid: EditText? = null
    private var imgCaptured: ImageView? = null

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

    private var orderBinding: ActivityOrderBinding? = null
    private var orderViewModel: OrderVM? = null
    private var totalWt: LiveData<String>? = null
    private val orderFactory: OrderVMFactory by instance()
    override val kodein by kodein()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderBinding = DataBindingUtil.setContentView(this, R.layout.activity_order)
        orderBinding?.lifecycleOwner = this
        orderViewModel = ViewModelProvider(this, orderFactory).get(OrderVM::class.java)
        orderBinding!!.orderVM = orderViewModel
        orderViewModel!!.orderManager = this
        init()
        orderViewModel!!.isDateActive.observe(this, Observer {
            if (it) {
                showDatePickerDialog()
            }
        })
        orderViewModel!!.showMenu.observe(this, Observer {
            if (it) {
                invalidateOptionsMenu()
            }
        })
        setSupportActionBar(tlEntry)
        tlEntry.setNavigationOnClickListener {
            onBackPressed()
        }
        orderViewModel!!.getAllOrders().observe(this, Observer {
            it.size
        })
        /*totalWt = orderViewModel?.totalWeight
        totalWt?.observe(this, Observer {
            edtRemainWt.clearFocus()
            hideFilledTextInputField()
            if (it.isNotEmpty()) {
                cont_charges.visibility = View.VISIBLE
            }
        })
        txtMoreFieldCalculation.setOnClickListener { hideShowExtraField() }*/

        //Get Intents
        /*if (intent.hasExtra(StringValues.GAVE)) {
            isGave = intent.getBooleanExtra(StringValues.GAVE, true)
        }
        if (intent.hasExtra(StringValues.USER_ID)) {
            userId = intent.getLongExtra(StringValues.USER_ID, -1)
        }
        //setListener
        calView!!.setOnClickListener(this)
        imgBtnCapture!!.setOnClickListener(this)
        txtAddMore!!.setOnClickListener(this)
        btnCal!!.setOnClickListener(this)*/
    }


    private fun init() {
        orderViewModel!!.pickedDate.value = LocalDate.now().toString()
        labourChargesAdapter = ArrayAdapter(this, R.layout.item_text, lCharges)
        txt_labour_charges!!.setAdapter(labourChargesAdapter)
        txt_labour_charges!!.setSelection(0)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater=menuInflater
        inflater.inflate(R.menu.menu_calculate, menu)
        menu.findItem(R.id.save_cal).isVisible = false
        return  super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val save = menu.findItem(R.id.save_cal)
        save.isVisible = orderViewModel!!.showMenu.value!!
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.save_cal -> {
                orderViewModel!!.saveOrder()
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
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
        val grandTotalComplete = ceil(grandTotalAmount.toDouble()).toInt()
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

        when (view.id) {
            R.id.container_cal_entry -> showDatePickerDialog()
            R.id.btnCamera ->                 //Step 1
                //Check the platform if grater than lollipop(23)
                checkSelfPermission()
            R.id.txtMoreFieldCalculation -> hideShowExtraField()
            R.id.btn_done -> validateAllEntries()
        }
    }

    private fun hideFilledTextInputField() {
        edtComRate?.disableOperation()
        edtPieces?.disableOperation()
        edtWt?.disableOperation()
        edtRemainWt?.disableOperation()
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
        txtMoreFieldCalculation!!.doVisibilityOperation()
        cont_extra_fields!!.doVisibilityOperation()
        scrollToBottom()
    }

    private fun scrollToBottom() {
        Handler().postDelayed({
            scroll_entry!!.isFillViewport = true
            scroll_entry!!.fullScroll(View.FOCUS_DOWN)
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
                    TransactionLogActivity.REQUEST_PERMISSION_CODE)
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
                startActivityForResult(takePictureIntent, TransactionLogActivity.REQUEST_IMAGE_CAPTURE)
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

    fun showDatePickerDialog() {
        val newFragment: DialogFragment = DatePickerFragment()
        newFragment.show(supportFragmentManager, "datePicker")
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == TransactionLogActivity.REQUEST_PERMISSION_CODE) {
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
        if (requestCode == TransactionLogActivity.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            if (currentPhotoPath.isNotEmpty()) {
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
        orderViewModel!!.pickedDate.value = selectedDateStr
    }

    override fun onError(error: String) {
        Log.e("Order", "Error : $error")
    }

    override fun onSuccess(order: OrderModel) {
        Log.e("Order", "${order.entryName}")
        finish()
    }


}