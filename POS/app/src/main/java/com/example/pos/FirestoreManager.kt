package com.example.pos

import android.content.Context
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.text.isDigitsOnly
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import java.util.Date

class FirestoreManager(context: Context) {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = AuthManager(context)
    var userId = auth.getCurrentUser()?.uid

    @RequiresApi(Build.VERSION_CODES.O)
    fun loadSettings(settingsViewModel: SettingsViewModel) {
        val email = auth.getCurrentUser()?.email
        if (email != null) {
            val docRef = firestore.collection("merchants").document(email)
            docRef.get().addOnSuccessListener { document ->
                val settings = Settings(
                    email,
                    document.getString("merchantName")!!,
                    document.getString("merchantId")!!,
                    document.getDate("lastUpdated")!!,
                    document.getString("configStatus")!!
                )
                settingsViewModel.loadSettings(settings)
            }
        }
    }

    fun setConnected(connected : Boolean, context: Context){
        if(connected){
            firestore.enableNetwork().addOnCompleteListener {
                Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show()
            }
        }
        else{
            firestore.disableNetwork().addOnCompleteListener {
                Toast.makeText(context, "Disconnected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addTransaction(paymentViewModel: PaymentViewModel, settingsViewModel: SettingsViewModel){
        if(auth.getCurrentUser() != null && transactionValid(paymentViewModel)){
            val docRef = firestore.collection("tx").document()
            val trans = hashMapOf(
                "merchantId" to settingsViewModel.merchantIdValue,
                "cardNumber" to paymentViewModel.cardNumberValue,
                "amount" to paymentViewModel.amountValue.toDoubleOrNull(),
                "date" to Date()
            )
            docRef.set(trans)
                .addOnSuccessListener {
                    Log.d("POS", "Doc written: ${trans.toString()}")
                    paymentViewModel.setResultMsg("Transaction done: ${docRef.id}")
                    paymentViewModel.setDone(false)
                }
                .addOnFailureListener {
                    e -> Log.w("POS", "Error writing document", e)
                    paymentViewModel.setResultMsg("Error in transaction")
                    paymentViewModel.setDone(false)
                }
            if(!settingsViewModel.connectedValue){
                Log.d("POS", "Doc written (disconnected): ${trans.toString()}")
                paymentViewModel.setResultMsg("Transaction done: ${docRef.id}")
                paymentViewModel.setDone(false)
            }
        }
        else{
            val docRef = firestore.collection("errors").document()
            val trans = hashMapOf(
                "merchantId" to settingsViewModel.merchantIdValue,
                "cardNumber" to paymentViewModel.cardNumberValue,
                "amount" to paymentViewModel.amountValue,
                "date" to Date()
            )
            //Log.d("tx", trans.toString())
            docRef.set(trans)
                .addOnSuccessListener {
                    paymentViewModel.setResultMsg("Invalid transaction: ${docRef.id}")
                    paymentViewModel.setDone(false)
                }
                .addOnFailureListener {
                    e -> Log.w("POS", "Error writing document", e)
                    paymentViewModel.setResultMsg("Invalid transaction. Error saving")
                    paymentViewModel.setDone(false)
                }
            if(!settingsViewModel.connectedValue){
                Log.d("POS", "Doc written - Invalid transaction (disconnected): ${trans.toString()}")
                paymentViewModel.setResultMsg("Invalid transaction: ${docRef.id}")
                paymentViewModel.setDone(false)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun searchTransactions(historyViewModel: HistoryViewModel, settingsViewModel: SettingsViewModel){
        if(auth.getCurrentUser() != null){
            var result : MutableList<Transaction> = mutableListOf<Transaction>()
            if(historyViewModel.isLastModeValue) {
                firestore
                    .collection("tx")
                    .whereEqualTo("merchantId", settingsViewModel.merchantIdValue)
                    .orderBy("date", Query.Direction.DESCENDING)
                    .limit(historyViewModel.lastTransactionsValue.toLong())
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Log.d("POS", "${document.id} => ${document.data}")
                            val t = document.toObject(Transaction::class.java)
                            t?.id = document.id
                            Log.d("POS", "transaction ==> ${t.toString()}")
                            result.add(t)
                        }
                        historyViewModel.setTransactionList(result)
                    }
                    .addOnFailureListener { exception ->
                        Log.w("POS", "Error getting documents: ", exception)
                    }
            }
            else {
                firestore
                    .collection("tx")
                    .whereEqualTo("merchantId", settingsViewModel.merchantIdValue)
                    .whereGreaterThanOrEqualTo("date", Date(historyViewModel.fromDateValue))
                    .whereLessThanOrEqualTo("date", Date(historyViewModel.toDateValue))
                    .orderBy("date", Query.Direction.DESCENDING)
                    .limit(50)
                    .get()
                    .addOnSuccessListener { documents ->
                        for (document in documents) {
                            Log.d("POS", "${document.id} => ${document.data}")
                            val t = document.toObject(Transaction::class.java)
                            t?.id = document.id
                            Log.d("POS", "transaction ==> ${t.toString()}")
                            result.add(t)
                        }
                        historyViewModel.setTransactionList(result)
                    }
                    .addOnFailureListener { exception ->
                        Log.w("POS", "Error getting documents: ", exception)
                    }
            }

        }
    }

    private fun transactionValid(paymentViewModel: PaymentViewModel) : Boolean {
        if(paymentViewModel.amountValue.trim() == "" || paymentViewModel.amountValue.toDoubleOrNull() == null){
            return false
        }
        else if(paymentViewModel.cardNumberValue.length != 16 || !paymentViewModel.cardNumberValue.isDigitsOnly()){
            return false
        }
        return true
    }
}