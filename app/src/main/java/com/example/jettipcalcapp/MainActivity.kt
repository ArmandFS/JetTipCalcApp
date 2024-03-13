package com.example.jettipcalcapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.jettipcalcapp.components.InputField
import com.example.jettipcalcapp.ui.theme.JetTipCalcAppTheme
import com.example.jettipcalcapp.util.calculateTotalPerPerson
import com.example.jettipcalcapp.util.calculateTotalTip
import com.example.jettipcalcapp.widgets.RoundIconButton

@OptIn(ExperimentalComposeUiApi::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApp {
                TopHeader()
                MainContent()
            }
        }
    }
}
@Composable
fun MyApp(content: @Composable ()-> Unit) {
    JetTipCalcAppTheme {
        // A surface container using the 'background' color from the theme
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background) {
            content()
        }
    }
}
@Preview
@Composable
//pass in data for the topheader
fun TopHeader(totalPerPerson: Double = 0.0){
    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(15.dp)
        .height(150.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp))),
        color = Color(0xFFE9D7F7)
    )
    {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            //format into 2 decimals
            val total = "%.2f".format(totalPerPerson)
            Text(text = "Total per Person",
                 style = MaterialTheme.typography.headlineSmall)
            Text(text = "$$total",
                 style = MaterialTheme.typography.headlineSmall,
                 fontWeight = FontWeight.ExtraBold)
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent(){
    Column(modifier = Modifier.padding(all = 12.dp)) {
        BillForm{billAmt ->
            Log.d("AMT", "MainContent: ${billAmt.toInt()}")
        }
    }
}
@ExperimentalComposeUiApi
@Composable
fun BillForm(modifier: Modifier = Modifier,
             onValChange: (String) -> Unit = {}
    ){
    val totalBillState = remember {
        mutableStateOf("")
    }
    val validState = remember(totalBillState.value) {
        //creating a state of the bill state, and check if not empty.
        //this returns a boolean TRUE or FALSE
        totalBillState.value.trim().isNotEmpty()
    }
    val keyboardController = LocalSoftwareKeyboardController.current
    val sliderPositionState = remember {
        //slider value will be float
        mutableStateOf(0f)
    }
    val tipPercentage = (sliderPositionState.value)
    val splitByState = remember {
        mutableStateOf(1)
    }
    val range = IntRange(start = 1, endInclusive = 1000)
    val tipAmountState = remember {
          mutableStateOf(0.0)
    }
    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }
    TopHeader(totalPerPerson = totalPerPersonState.value)
    Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(corner = CornerSize(8.dp)),
        border = BorderStroke(width = 2.dp, color = Color.LightGray)
    ) {
        Column(modifier = Modifier.padding(6.dp),
               verticalArrangement = Arrangement.Top,
               horizontalAlignment = Alignment.Start) {
            InputField(
                valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions{
                    //enter something that is valid for the keyboard actions
                    if (!validState) return@KeyboardActions
                    onValChange(totalBillState.value.trim())
                    //Todo - onvaluechanged
                    keyboardController?.hide()
                })
            if (validState) {
                Row(modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start) {
                    Text(text = "Split",
                         modifier = Modifier.align(alignment = Alignment.CenterVertically))
                    Spacer(modifier = Modifier.width(120.dp))
                    Row (modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End){
                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            //input logic math functions here to count
                            onClick = {
                                splitByState.value =
                                    if (splitByState.value > 1) splitByState.value - 1 else 1
                                totalPerPersonState.value = calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByState.value,
                                    tipPercentage = tipPercentage.toInt())
                            })
                        Text(text = "${splitByState.value}",
                             modifier = Modifier
                                 .align(Alignment.CenterVertically)
                                 .padding(
                                     start = 9.dp,
                                     end = 9.dp
                                 )
                        )
                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                //increment range and add 1
                                if (splitByState.value < range.last){
                                    splitByState.value = splitByState.value + 1
                                    totalPerPersonState.value = calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByState.value,
                                        tipPercentage = tipPercentage.toInt())
                                }
                            })
                    }
                }
            //tip row
            Row(modifier = Modifier
                .padding(horizontal = 3.dp,
                         vertical = 12.dp)
                ) {
                Text(text = "Tip",
                     modifier = Modifier.align(alignment = Alignment.CenterVertically))
                Spacer(modifier = Modifier.width(200.dp))
                Text(text = "$ ${tipAmountState.value}",
                     modifier = Modifier.align(alignment = Alignment.CenterVertically))
            }
            Column (verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally){
                    Text(text = "$tipPercentage %")
                    Spacer(modifier = Modifier.height(14.dp))
                    //put slider mechanic
                    Slider(value = sliderPositionState.value,
                           onValueChange = {newVal ->
                               sliderPositionState.value = newVal
                               tipAmountState.value = calculateTotalTip(
                                                     totalBill = totalBillState.value.toDouble(),
                                                     tipPercentage = tipPercentage.toInt())
                               totalPerPersonState.value = calculateTotalPerPerson(
                                                           totalBill = totalBillState.value.toDouble(),
                                                           splitBy = splitByState.value,
                                                           tipPercentage = tipPercentage.toInt())
                           },
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end =  16.dp),
                        //pass integer here for steps in slider
                        //this can also work as a callback
                        steps = 5,
                        onValueChangeFinished = {
                        }
                    )
            }
            }else{
                Box(){}
            }
            }
        }
}
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    JetTipCalcAppTheme {
        MyApp {
            Text(text = "Hello again")
        }
    }
}
