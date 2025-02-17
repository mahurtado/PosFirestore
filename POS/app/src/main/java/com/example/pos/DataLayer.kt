package com.example.pos

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import java.util.Date

class PaymentViewModel : ViewModel() {

    var cardNumberValue by mutableStateOf("")
        private set
    var amountValue  by mutableStateOf("")
        private set
    var resultMsgValue  by mutableStateOf("Transaction message")
        private set
    var isDoneValue  by mutableStateOf(true)
        private set

    fun setCardNumber(value: String){
        cardNumberValue = value
    }
    fun setAmount(value: String){
        amountValue = value
    }
    fun setResultMsg(value: String){
        resultMsgValue = value
    }
    fun setDone(value: Boolean){
        isDoneValue = value
    }

}

@RequiresApi(Build.VERSION_CODES.O)
class HistoryViewModel : ViewModel() {

    var transactionListValue by mutableStateOf(emptyList<Transaction>())
        private set
    var isLastModeValue by mutableStateOf(true)
        private set
    var lastTransactionsValue by mutableIntStateOf(10)
        private set
    var fromDateValue by mutableLongStateOf(Date().time)
        private set
    var toDateValue by mutableStateOf(Date().time)
        private set

    fun setTransactionList(list: List<Transaction>){
        transactionListValue = list
    }
    fun setLastTransactions(value: String){
        lastTransactionsValue = value.toInt()
    }
    fun setFromDate(value: Long){
        fromDateValue = value
    }
    fun setToDate(value: Long){
        toDateValue = value
    }
    fun setLastMode(value: Boolean){
        isLastModeValue = value
    }

}

@RequiresApi(Build.VERSION_CODES.O)
class SettingsViewModel : ViewModel() {

    var merchantIdValue by mutableStateOf("")
        private set
    var merchantNameValue  by mutableStateOf("")
        private set
    var configMsgValue  by mutableStateOf("")
        private set
    var lastUpdated  by mutableStateOf(DateUtils().dateToTimeString(DateUtils().convertMillisToLocalDate(Date().time)))
        private set
    var connectedValue  by mutableStateOf(true)
        private set

    fun setMerchantId(value: String){
        merchantIdValue = value
    }
    fun setMerchantName(value: String){
        merchantNameValue = value
    }
    fun setLastUpdatedValue(value: String){
        lastUpdated = value
    }
    fun setConfigMsg(value: String){
        configMsgValue = value
    }
    fun setConnected(value: Boolean){
        connectedValue = value
    }

    public fun loadSettings(settings: Settings) {
        merchantIdValue = settings.merchantId
        merchantNameValue = settings.merchantName
        configMsgValue = settings.configStatus
        lastUpdated =
            DateUtils().dateToTimeString(DateUtils().convertMillisToLocalDate(settings.lastUpdated.time))
        connectedValue = settings.connected
    }
}

class LoginViewModel : ViewModel() {
    var userLoggedValue by mutableStateOf(false)
        private set
    fun setUserLogged(value: Boolean){
        userLoggedValue = value
    }
}

data class Transaction (
    var id: String? = null,
    val merchantId: String = "",
    val cardNumber: String = "",
    val amount: Double = 0.0,
    val date: Date = Date()
){
    constructor (cardNumber: String,  merchantId: String, amount: Double,  date: Date) : this()
}

data class TransactionError (
    val merchantId: String = "",
    val cardNumber: String = "",
    val amount: String = "",
    val date: Date = Date()
)

data class Settings (
    val email: String? = "",
    val merchantName: String = "",
    val merchantId: String = "",
    val lastUpdated: Date = Date(),
    val configStatus: String = "",
    val connected: Boolean = true,
)

fun getTransactionList(size : Int) : List<Transaction> {
    var result : MutableList<Transaction> = mutableListOf<Transaction>()
    result.add(Transaction ("111122223333444", "33533", 20.95, Date()))
    result.add(Transaction ("211122223333444", "33533", 100.95, Date()))
    result.add(Transaction ("311122223333444", "33533", 40.00, Date()))
    result.add(Transaction ("411122223333444", "33533", 12.95, Date()))
    result.add(Transaction ("511122223333444", "33533", 66.95, Date()))
    result.add(Transaction ("211122223333444", "33533", 100.95, Date()))
    result.add(Transaction ("311122223333444", "33533", 40.00, Date()))
    result.add(Transaction ("411122223333444", "33533", 12.95, Date()))
    result.add(Transaction ("511122223333444", "33533", 66.95, Date()))
    result.add(Transaction ("211122223333444", "33533", 100.95, Date()))
    result.add(Transaction ("311122223333444", "33533", 40.00, Date()))
    result.add(Transaction ("411122223333444", "33533", 12.95, Date()))
    result.add(Transaction ("511122223333444", "33533", 66.95, Date()))
    result.add(Transaction ("211122223333444", "33533", 100.95, Date()))
    result.add(Transaction ("311122223333444", "33533", 40.00, Date()))
    result.add(Transaction ("411122223333444", "33533", 12.95, Date()))
    result.add(Transaction ("511122223333444", "33533", 66.95, Date()))
    return result.toList()
}