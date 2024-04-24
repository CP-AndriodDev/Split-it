package com.example.billsplit

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.billsplit.databinding.ActivityMainBinding
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.JsonHttpResponseHandler
import cz.msebera.android.httpclient.Header
import org.json.JSONObject
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
//       Displays API data
        setupRecyclerView()
        binding.searchButton.setOnClickListener {
            val query = binding.searchEditText.text.toString().trim()
            if (query.isNotEmpty()) {
                fetchBusinessData(query)
            } else {
                Toast.makeText(this, "Please enter a search term.", Toast.LENGTH_SHORT).show()
            }
        }
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
        showResultDialog(baseAmount, taxAmount, tipAmount, totalAmountForAll, totalAmountPerPerson, numberOfPeople)
    }

    private val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        override fun afterTextChanged(s: Editable?) {
        }
    }

    private fun showResultDialog(baseAmount: Double, taxAmount: Double, tipAmount: Double, totalAmountForAll: Double, totalAmountPerPerson: Double, numberOfPeople: Int) {
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
                val tipPercentage = tipPercentage * 100
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
//    Displays API - List of YELP places
    private fun setupRecyclerView() {
        val adapter = BusinessAdapter(listOf()) { business ->
            // Set the clicked business's name to the EditText
            binding.etRestaurantName.setText(business.name)
            Toast.makeText(this, "Selected: ${business.name}", Toast.LENGTH_SHORT).show()
        }
        binding.businessesRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            this.adapter = adapter
        }
    }


    // Add a function to make an API call and search for businesses
    private fun fetchBusinessData(query: String) {
        val client = AsyncHttpClient()
        val url = "https://api.yelp.com/v3/businesses/search?term=$query&location=NewYork&sort_by=best_match&limit=20"

        client.addHeader("Authorization", "Bearer eRsgA3eC8-4ZRukxJNTGeCaaitEbF5MJlp_3JOehSoygLGC4hHERJLAP17OsXmbbLwCzOLtf9MN9WX2UZSuLVUNyc9bm2soZvxyW3bVYwPObpgA71i1XwsjI-vgmZnYx")
        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Array<Header>?, response: JSONObject) {
                val businesses = BusinessParser.parse(response)
                (binding.businessesRecyclerView.adapter as BusinessAdapter).updateBusinesses(businesses)
                Toast.makeText(this@MainActivity, "Data fetched successfully", Toast.LENGTH_SHORT).show()
            }

            override fun onFailure(
                statusCode: Int,
                headers: Array<Header>?,
                throwable: Throwable?,
                errorResponse: JSONObject?
            ) {
                // Proper error handling depending on status code and error response
                Log.e("MainActivity", "Error fetching data: ${errorResponse.toString()}")
                Toast.makeText(this@MainActivity, "Error fetching data: ${throwable?.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


}
class BusinessAdapter(
    private var businesses: List<Business>,
    private val onClick: (Business) -> Unit
) : RecyclerView.Adapter<BusinessAdapter.BusinessViewHolder>() {

    // Provide a reference to the views for each data item
    class BusinessViewHolder(
        private val view: View,
        private val onClick: (Business) -> Unit
    ) : RecyclerView.ViewHolder(view) {
        private val nameTextView: TextView = view.findViewById(R.id.businessNameTextView)
        private var currentBusiness: Business? = null

        init {
            view.setOnClickListener {
                currentBusiness?.let { business ->
                    onClick(business)
                }
            }
        }

        // Replace the contents of a view (invoked by the layout manager)
        fun bind(business: Business) {
            currentBusiness = business
            nameTextView.text = business.name
            itemView.setOnClickListener {
                onClick(business) // Trigger the onClick action passed to the adapter
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BusinessViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_business, parent, false)
        return BusinessViewHolder(view, onClick)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: BusinessViewHolder, position: Int) {
        val business = businesses[position]
        holder.bind(business)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = businesses.size

    // Update the list of businesses and notify the adapter about changes
    fun updateBusinesses(newBusinesses: List<Business>) {
        // Calculate the difference between the old and the new data set
        val diffCallback = object : DiffUtil.Callback() {
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return businesses[oldItemPosition].id == newBusinesses[newItemPosition].id
            }

            override fun getOldListSize(): Int = businesses.size

            override fun getNewListSize(): Int = newBusinesses.size

            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
                return businesses[oldItemPosition] == newBusinesses[newItemPosition]
            }
        }
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        this.businesses = newBusinesses
        diffResult.dispatchUpdatesTo(this)
    }
}


data class Business(val id: String, val name: String)

object BusinessParser {
    fun parse(json: JSONObject): List<Business> {
        val businesses = mutableListOf<Business>()
        val jsonArray = json.getJSONArray("businesses")
        for (i in 0 until jsonArray.length()) {
            val jsonObject = jsonArray.getJSONObject(i)
            val id = jsonObject.getString("id")
            val name = jsonObject.getString("name")
            businesses.add(Business(id, name))
        }
        return businesses
    }
}

