package com.example.driveohioia.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import timber.log.Timber

@Composable
fun DriveScreen(){
    ConstraintLayoutTracking()
}

@Composable
fun ConstraintLayoutTracking() {
    val context = LocalContext.current
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            val (startButton) = createRefs()
            val (timeValue) = createRefs()
            val bottomGuideline = createGuidelineFromBottom(350.dp)


            Text(
                "PH:PH:PH", // Will replace with some string resource
                Modifier.constrainAs(timeValue) {
                    bottom.linkTo(bottomGuideline)
                    centerHorizontallyTo(parent)
                },
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp

            )

            Button(
                onClick = {
                    Timber.i("Button Pressed") //Logs for testing
                    //TODO: Starts Drive Activity or equivalent
                },
                modifier = Modifier.constrainAs(startButton) {
                    centerHorizontallyTo(parent)
                    top.linkTo(bottomGuideline, margin = 100.dp)
                }

            ) { Text("Start") }


        }
    }
}



@Preview
@Composable
fun TrackingPreview(){
    MaterialTheme{
        ConstraintLayoutTracking()
    }
}

