package com.example.billsplit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billsplit.databinding.ActivityMainBinding
import androidx.recyclerview.widget.DividerItemDecoration
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDate(): String {
    val currentDate = Date()
    val dateFormat = SimpleDateFormat("MM/dd/yyyy", Locale.getDefault())
    return dateFormat.format(currentDate)
}
data class ReceiptItem(
    val restaurantName: String,
    val billTotal: Double,
    val taxTotal: Double,
    val tipPercentage: Double,
    val splitAmount: Int,
    val myPortion: Double,
    val date: String
)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var tipPercentage = 0.0
    private var numberOfPeople = 1
    private val numberFormat = NumberFormat.getCurrencyInstance()
    private val pastReceiptsList = mutableListOf<ReceiptItem>()
    private lateinit var pastReceiptsAdapter: PastReceiptsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.etRestaurantName.addTextChangedListener(textWatcher)
        binding.etBillTotal.addTextChangedListener(textWatcher)
        binding.etTaxTotal.addTextChangedListener(textWatcher)
        binding.etSplitAmount.addTextChangedListener(textWatcher)

        binding.seekBarTipPercentage.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                updateTipPercentage(progress / 100.0)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        updateTipPercentage(0.15) // Set initial tip percentage to 15%

        val button = findViewById<Button>(R.id.btnCalculate)

        button.setOnClickListener {
            calculateTip()
        }

        pastReceiptsAdapter = PastReceiptsAdapter(pastReceiptsList)
        binding.rvPastReceipts.adapter = pastReceiptsAdapter
        binding.rvPastReceipts.layoutManager = LinearLayoutManager(this)

        val dividerItemDecoration = DividerItemDecoration(this, LinearLayoutManager.VERTICAL)
        binding.rvPastReceipts.addItemDecoration(dividerItemDecoration)
    }

    private fun updateTipPercentage(newTipPercentage: Double) {
        tipPercentage = newTipPercentage
        binding.tvTipPercentage.text = String.format("%.0f%%", tipPercentage * 100)
        binding.seekBarTipPercentage.progress = (tipPercentage * 100).toInt()
    }

    private fun calculateTip() {
        val baseAmount = binding.etBillTotal.text.toString().toDoubleOrNull() ?: 0.0
        val taxAmount = binding.etTaxTotal.text.toString().toDoubleOrNull() ?: 0.0
        val numberOfPeople = binding.etSplitAmount.text.toString().toIntOrNull() ?: 0
        val everythingTotal = baseAmount + taxAmount
        val tipAmount = everythingTotal * tipPercentage
        val totalAmountForAll = everythingTotal + tipAmount
        val totalAmountPerPerson = String.format("%.2f", totalAmountForAll / numberOfPeople).toDoubleOrNull() ?: 0.00

        val result = "Base Amount: $baseAmount\n" +
                "Tax Amount: $taxAmount\n" +
                "Tip Amount: $tipAmount\n" +
                "Total Amount for All: $totalAmountForAll\n" +
                "Total Amount Per Person: $totalAmountPerPerson"

        // Show the result dialog
        showResultDialog(baseAmount, taxAmount, tipAmount, totalAmountForAll, totalAmountPerPerson)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun showResultDialog(baseAmount: Double, taxAmount: Double, tipAmount: Double, totalAmountForAll: Double, totalAmountPerPerson: Double) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_result, null)
        val tvBillTotal = dialogView.findViewById<TextView>(R.id.tv_bill_total)
        val tvTaxAmount = dialogView.findViewById<TextView>(R.id.tv_tax_total)
        val tvTipAmount = dialogView.findViewById<TextView>(R.id.tv_tip_total)
        val tvAllAmount = dialogView.findViewById<TextView>(R.id.tv_all_amount)
        val tvMyPortion = dialogView.findViewById<TextView>(R.id.tv_my_portion)

        tvBillTotal.text = "$baseAmount"
        tvTaxAmount.text = "$taxAmount"
        tvTipAmount.text = "$tipAmount"
        tvAllAmount.text = "$totalAmountForAll"
        tvMyPortion.text = "$totalAmountPerPerson"


        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setPositiveButton("OK") { _, _ ->
                val restaurantName = binding.etRestaurantName.text.toString()
                val billTotal = binding.etBillTotal.text.toString().toDoubleOrNull() ?: 0.0
                val taxTotal = binding.etTaxTotal.text.toString().toDoubleOrNull() ?: 0.0
                val tipPercentage = tipPercentage
                val splitAmount = numberOfPeople
                val myPortion = totalAmountPerPerson
                val date = getCurrentDate() // You'll need to implement this function to get the current date

                val receiptItem = ReceiptItem(
                    restaurantName,
                    billTotal,
                    taxTotal,
                    tipPercentage,
                    splitAmount,
                    myPortion,
                    date
                )

                pastReceiptsList.add(receiptItem)
                // Update the RecyclerView or display the past receipts list
                pastReceiptsAdapter.notifyDataSetChanged()
            }

        val dialog = builder.create()
        dialog.show()
    }

}