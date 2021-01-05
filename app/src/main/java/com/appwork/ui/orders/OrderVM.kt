package com.appwork.ui.orders

import android.util.Log
import android.view.View
import androidx.databinding.ObservableInt
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.appwork.data.entities.OrderModel
import com.appwork.utils.Coroutines
import com.appwork.utils.doVisibilityOperation
import com.appwork.utils.hideKeyboard
import com.appwork.utils.showSnackBar
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Created by Vivek Kumar belongs to APP WORK  on 10-12-2020.
 */
class OrderVM(private val orderRepo: OrderRepo) : ViewModel() {
    var cropName: String? = null
    var labourCharge: String? = null
    var cropRate: String? = null
    var totalPieces: String? = null
    var weightPerPiece: String? = null
    var remainingWt: String? = null
    var mandiCharge: MutableLiveData<String> = MutableLiveData()
    var totalLabourCharge: MutableLiveData<String> = MutableLiveData()
    var totalWeight: MutableLiveData<String> = MutableLiveData()
    var totalPrice: MutableLiveData<String> = MutableLiveData()
    var finalCropBillAmount: MutableLiveData<String> = MutableLiveData()
    var fare: String? = null
    var prevAmount: String? = null
    var paid: String? = null
    private var showCharges = ObservableInt(View.GONE)
    private var showDone = ObservableInt(View.VISIBLE)
    private var showExtraField = ObservableInt(View.GONE)
    var orderManager: IOrderManager? = null
    var pickedDate: MutableLiveData<String> = MutableLiveData()
    var isDateActive: MutableLiveData<Boolean> = MutableLiveData(false)
    var showMenu: MutableLiveData<Boolean> = MutableLiveData(false)
    var isNavigateToBill: MutableLiveData<Boolean> = MutableLiveData(false)

    fun calculateEntityPrice(v: View) {
        if (cropRate.isNullOrEmpty()) {
            v.showSnackBar("Enter Value")
            return
        }
        if (weightPerPiece.isNullOrEmpty()) {
            v.showSnackBar("Enter Value")
            return
        }
        if (totalPieces.isNullOrEmpty()) {
            v.showSnackBar("Enter Value")
            return
        }
        if (remainingWt.isNullOrEmpty()) {
            v.showSnackBar("Enter Value")
            return
        }
        if (labourCharge.isNullOrEmpty()) {
            v.showSnackBar("Enter Value")
            return
        }
        showDone.set(View.GONE)
        v.hideKeyboard()
        val tempCropWt = getTotalCropWt(totalPieces!!, weightPerPiece!!, remainingWt!!)
        if (tempCropWt != 0) {
            val tempLC = getLCharge(labourCharge!!, totalPieces!!)
            totalLabourCharge.value = tempLC.toString()
            val tempTotal = getCropPrice(cropRate!!, tempCropWt.toFloat())
            val tempAdatCharges = getAdatCharges(tempTotal.toDouble().toInt())
            mandiCharge.value = tempAdatCharges.toString()
            val totalConFee = tempLC + tempAdatCharges
            totalWeight.value = tempCropWt.toString()
            totalPrice.value = tempTotal.toString()
            finalCropBillAmount.value = (tempTotal - totalConFee).toString()
            showCharges.set(View.VISIBLE)
        }
    }

    private fun getTotalCropWt(pieces: String, wtPerPiece: String, remainWt: String): Int {
        return pieces.toInt() * wtPerPiece.toInt() + remainWt.toInt()
    }

    private fun getCropPrice(rate: String, totalWt: Float): Float {
        return totalWt * rate.toFloat() / 100
    }

    private fun getLCharge(lCharge: String, pieces: String): Int {
        return lCharge.toInt() * pieces.toInt()
    }

    private fun getAdatCharges(totalCropPrice: Int): Int {
        return try {
            (totalCropPrice * 6 / 1000)
        } catch (e: Exception) {
            Log.e("VM", " Exception : ${e.message}")
            0
        }

    }


    fun getShowChanges(): ObservableInt {
        return showCharges
    }

    fun getShowDone(): ObservableInt {
        return showDone
    }

    fun showExtraField(): ObservableInt {
        return showExtraField
    }

    fun onCLickExtraField(v: View) {
        v.doVisibilityOperation()
        showExtraField.set(View.VISIBLE)
        showMenu.value = true
    }

    fun openCalendarDialog(v: View) {
        isDateActive.value = true
    }

    fun saveOrder() {
      val savedDate=  LocalDateTime.now()
                .atZone(ZoneId.of("Asia/Kolkata"))
                .toInstant()
                .toEpochMilli()
        Log.e("OrderDate "," Date : Epoch $savedDate")
        Coroutines.main {
            val order = OrderModel(
                    cropName,
                    totalPieces,
                    weightPerPiece,
                    remainingWt,
                    totalWeight.value.toString(),
                    totalPrice.value.toString(),
                    labourCharge,
                    totalLabourCharge.value.toString(),
                    mandiCharge.value.toString(),
                    finalCropBillAmount.toString(),
                    "",
                    "",
                    finalCropBillAmount.value.toString(),
                    savedDate,
                    "",
                    false)
            val id = orderRepo.insertOrUpdateOrder(order)
            if (id > 0L) {
                orderManager?.onSuccess(order)
            } else {
                orderManager?.onError("$id")
            }
        }
    }

    fun navigateToBillDetails(v: View) {
        isNavigateToBill.value = true
    }

    fun openCamera(v: View) {

    }

    fun getAllOrders() = orderRepo.getAllOrders()
}