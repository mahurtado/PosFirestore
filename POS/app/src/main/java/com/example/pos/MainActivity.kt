package com.example.pos

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pos.ui.theme.POSTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // setting up the individual tabs
            val loginTab = TabBarItem(title = "Login", selectedIcon = Icons.Filled.Face, unselectedIcon = Icons.Outlined.Face)
            val paymentTab = TabBarItem(title = "Payment", selectedIcon = Icons.Filled.ShoppingCart, unselectedIcon = Icons.Outlined.ShoppingCart)
            val historyTab = TabBarItem(title = "History", selectedIcon = Icons.Filled.Search, unselectedIcon = Icons.Outlined.Search)
            val settingsTab = TabBarItem(title = "Settings", selectedIcon = Icons.Filled.Settings, unselectedIcon = Icons.Outlined.Settings)

            val authManager: AuthManager = AuthManager(this)
            val firestore = FirestoreManager(this)

            // creating a list of all the tabs
            val tabBarItems = listOf(loginTab, paymentTab, historyTab, settingsTab)

            // creating our navController
            val navController = rememberNavController()

            val paymentViewModel: PaymentViewModel by viewModels()
            val historyViewModel: HistoryViewModel by viewModels()
            val settingsViewModel: SettingsViewModel by viewModels()
            val loginViewModel: LoginViewModel by viewModels()

            POSTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = { TabView(tabBarItems, navController)},
                        topBar = {
                            TopAppBar(
                                title = { Text("POS Device", color = Color.White) },
                                colors = TopAppBarDefaults.mediumTopAppBarColors(
                                    containerColor = Color(24,61,92)
                                    //containerColor = MaterialTheme.colorScheme.primaryContainer
                                )
                            )}
                    ){
                        NavHost(navController = navController, startDestination = loginTab.title) {
                            composable(loginTab.title) {
                                LoginView(loginViewModel, settingsViewModel, authManager, firestore)
                            }
                            composable(paymentTab.title) {
                                PaymentView(paymentViewModel, settingsViewModel, firestore)
                            }
                            composable(historyTab.title) {
                                HistoryView(historyViewModel, settingsViewModel, firestore)
                            }
                            composable(settingsTab.title) {
                                SettingsView(settingsViewModel, firestore)
                            }
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun PaymentView(paymentViewModel: PaymentViewModel, settingsViewModel: SettingsViewModel, firestore: FirestoreManager) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        SimpleOutlinedTextField(
            value = paymentViewModel.cardNumberValue,
            label = "Card Number",
            onChange = paymentViewModel::setCardNumber,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        SimpleOutlinedTextField(
            value = paymentViewModel.amountValue,
            label = "Amount",
            onChange = paymentViewModel::setAmount,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        if(paymentViewModel.isDoneValue) {
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    firestore.addTransaction(paymentViewModel, settingsViewModel)
                }
            ) {
                Text("Pay", color = Color.White)
            }
        }
        if(!paymentViewModel.isDoneValue){
            Spacer(modifier = Modifier.height(20.dp))
            Text(paymentViewModel.resultMsgValue)
            Spacer(modifier = Modifier.height(20.dp))
            Button(
                onClick = {
                    paymentViewModel.setDone(true)
                }
            ) {
                Text("New payment", color = Color.White)
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun HistoryView(historyViewModel: HistoryViewModel, settingsViewModel: SettingsViewModel, firestore: FirestoreManager) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        Spacer(modifier = Modifier.height(70.dp))
        // Filter type (Last transactions / Date range)
        Row (Modifier
            .selectableGroup()
            .fillMaxWidth()
            .defaultMinSize(minHeight = 80.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RadioButton(
                selected = (historyViewModel.isLastModeValue),
                onClick = { historyViewModel.setLastMode(true) }
            )
            Text(
                text = "Last #",
            )
            RadioButton(
                selected = (!historyViewModel.isLastModeValue),
                onClick = { historyViewModel.setLastMode(false) }
            )
            Text(
                text = "Date",
            )
            Spacer(modifier = Modifier.width(20.dp))
            Button(
                onClick = {
                    firestore.searchTransactions(historyViewModel, settingsViewModel)
                    //historyViewModel.setTransactionList(getTransactionList(10))
                },
            ) {
                Text(text = "Search")
            }
        }
        // Filter fields
        Row (Modifier
            .selectableGroup()
            .defaultMinSize(minHeight = 100.dp)
            .padding(horizontal = 16.dp)
            .weight(1f),
            verticalAlignment = Alignment.Top,
            horizontalArrangement = Arrangement.Center
        ) {
            if(historyViewModel.isLastModeValue) {
                SimpleOutlinedTextField(
                    value = historyViewModel.lastTransactionsValue.toString(),
                    label = "Number of Transactions",
                    onChange = historyViewModel::setLastTransactions,
                    modifier = Modifier.padding(horizontal = 24.dp)
                )
            }
            else if(!historyViewModel.isLastModeValue){
                DatePickerRangeComponent(historyViewModel)
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
        TransactionList(historyViewModel)
        Spacer(modifier = Modifier.height(70.dp))
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SettingsView(settingsViewModel: SettingsViewModel, firestore: FirestoreManager) {
    val context = LocalContext.current
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ){
            Text("Connected", fontSize = 20.sp)
            Switch(
                checked = settingsViewModel.connectedValue,
                onCheckedChange = {
                    settingsViewModel.setConnected(it)
                    firestore.setConnected(settingsViewModel.connectedValue, context)
                },
                modifier = Modifier.padding(16.dp),
            )
        }

        Text("Merchant name", color = Color.Red, fontSize = 14.sp)
        Text(settingsViewModel.merchantNameValue, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))

        Text("Merchant Id", color = Color.Red, fontSize = 14.sp)
        Text(settingsViewModel.merchantIdValue, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))

        Text("Last updated", color = Color.Red, fontSize = 14.sp)
        Text(settingsViewModel.lastUpdated, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))

        Text("Config status", color = Color.Red, fontSize = 14.sp)
        Text(settingsViewModel.configMsgValue, fontSize = 20.sp)
        Spacer(modifier = Modifier.height(20.dp))
    /*
        SimpleOutlinedTextField(
            value = settingsViewModel.merchantNameValue,
            label = "Merchant name",
            onChange = settingsViewModel::setMerchantName,
            enabled = false,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        SimpleOutlinedTextField(
            value = settingsViewModel.merchantIdValue,
            label = "Merchant Id",
            onChange = settingsViewModel::setMerchantId,
            enabled = false,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        SimpleOutlinedTextField(
            value = settingsViewModel.lastUpdated,
            label = "Last updated",
            onChange = settingsViewModel::setLastUpdatedValue,
            enabled = false,
            modifier = Modifier.padding(horizontal = 24.dp)
        )

        Spacer(modifier = Modifier.height(20.dp))

        SimpleOutlinedTextField(
            value = settingsViewModel.configMsgValue,
            label = "Config status",
            onChange = settingsViewModel::setConfigMsg,
            enabled = false,
            modifier = Modifier.padding(horizontal = 24.dp)
        )
*/
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun LoginView(loggingViewModel: LoginViewModel, settingsViewModel: SettingsViewModel, auth: AuthManager, firestore: FirestoreManager) {

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    if(!loggingViewModel.userLoggedValue){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            TextField(
                label = { Text(text = "email") },
                value = email,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                onValueChange = { email = it })
            Spacer(modifier = Modifier.height(10.dp))
            TextField(
                label = { Text(text = "password") },
                value = password,
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                onValueChange = { password = it })
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            emailPassSignIn(email, password, auth, context)
                            if(auth.getCurrentUser() != null) {
                                firestore.loadSettings(settingsViewModel)
                                loggingViewModel.setUserLogged(true)
                            }
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Login")
                }
            }
        }
    }
    else if(loggingViewModel.userLoggedValue){
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(text = "Current user: ${auth.getCurrentUser()!!.email}")
            Spacer(modifier = Modifier.height(20.dp))
            Box(modifier = Modifier.padding(40.dp, 0.dp, 40.dp, 0.dp)) {
                Button(
                    onClick = {
                        scope.launch {
                            auth.signOut()
                            loggingViewModel.setUserLogged(false)
                        }
                    },
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Logout")
                }
            }
        }
    }

}

private suspend fun emailPassSignIn(email: String, password: String, auth: AuthManager, context: Context) {
    if(email.isNotEmpty() && password.isNotEmpty()) {
        when (val result = auth.signInWithEmailAndPassword(email, password)) {
            is AuthRes.Success -> {
                // Load profile

                /*navigation.navigate(Routes.Home.route) {
                    popUpTo(Routes.Login.route) {
                        inclusive = true
                    }
                }*/
            }

            is AuthRes.Error -> {
                Toast.makeText(context, "Error SignUp: ${result.errorMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    } else {
        Toast.makeText(context, "Please enter email and password", Toast.LENGTH_SHORT).show()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionList(historyViewModel: HistoryViewModel) {
    val transactions: List<Transaction> = historyViewModel.transactionListValue
    LazyColumn(modifier = Modifier
        .verticalScroll(rememberScrollState())
        .background(Color.LightGray)
        .padding(20.dp)
        .heightIn(0.dp, 470.dp),
        verticalArrangement = Arrangement.Top, ) {
        items(transactions) { transaction ->
            TransactionRow(transaction)
            HorizontalDivider(thickness = 1.dp, color = Color.Black)
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransactionRow(transaction: Transaction) {
    Row(){
        Column {
            Text(DateUtils().dateToTimeString(DateUtils().convertMillisToLocalDate(transaction.date.time)), color = Color.Blue)
            Text("Amount: ${transaction.amount} €", color = Color.Red)
            Text("Card: ${transaction.cardNumber}", color = Color.Black)
        }
    }
}