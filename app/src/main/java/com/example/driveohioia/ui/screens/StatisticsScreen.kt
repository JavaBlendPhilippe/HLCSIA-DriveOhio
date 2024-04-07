package com.example.driveohioia.ui.screens

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun StatisticsScreen(){
    ConstraintLayoutStatistics()
}

@Composable
fun ConstraintLayoutStatistics () {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        ConstraintLayout(modifier = Modifier.fillMaxSize()) {
            // References for the Constraint Layout
            val (distanceTextInfo, distanceTextValue) = createRefs()
            val (totalTimeInfo, totalTimeValue) = createRefs()
            val (safetyScoreInfo, safetyScoreValue) = createRefs()
            val (avgSpeedInfo, avgSpeedValue) = createRefs()
            val startGuideline = createGuidelineFromStart(0.3f)
            val endGuideline = createGuidelineFromEnd(0.3f)
            val topGuideline = createGuidelineFromTop(200.dp)
            val bottomGuideline = createGuidelineFromBottom(400.dp)

            Text(
                "Total Distance",
                Modifier.constrainAs(distanceTextInfo) {
                    start.linkTo(startGuideline, 75.dp)
                    end.linkTo(parent.end, 1.dp)
                    bottom.linkTo(topGuideline)
                },
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 20.sp,
            )

            Text(
                text = "{PH} Miles", //Will Replace with string resource
                Modifier.constrainAs(distanceTextValue) {
                    start.linkTo(startGuideline, 75.dp)
                    end.linkTo(parent.end, 1.dp)
                    bottom.linkTo(topGuideline)
                    top.linkTo(parent.top,50.dp)
                },
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold

            )

            Text(
                "Total Time",
                Modifier.constrainAs(totalTimeInfo) {
                    start.linkTo(parent.start, 75.dp)
                    end.linkTo(startGuideline, 1.dp)
                    bottom.linkTo(topGuideline)
                },
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 20.sp,
            )

            Text(
                text = "PH:PH:PH", //Will Replace with string resource
                Modifier.constrainAs(totalTimeValue) {
                    start.linkTo(parent.start, 75.dp)
                    end.linkTo(startGuideline, 1.dp)
                    bottom.linkTo(topGuideline)
                    top.linkTo(parent.top,50.dp)
                },
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold

            )

            Text(
                "SafetyScore",
                Modifier.constrainAs(safetyScoreInfo) {
                    start.linkTo(parent.start, 75.dp)
                    end.linkTo(startGuideline, 1.dp)
                    top.linkTo(bottomGuideline,15.dp)
                },
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 20.sp,
            )

            Text(
                text = "PH", //Will Replace with string resource
                Modifier.constrainAs(safetyScoreValue) {
                    start.linkTo(parent.start, 75.dp)
                    end.linkTo(startGuideline, 1.dp)
                    bottom.linkTo(bottomGuideline)
                    top.linkTo(topGuideline, 20.dp)
                },
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold

            )

            Text(
                "Average Speed",
                Modifier.constrainAs(avgSpeedInfo) {
                    start.linkTo(startGuideline, 75.dp)
                    end.linkTo(parent.end, 1.dp)
                    top.linkTo(bottomGuideline,15.dp)
                },
                color = MaterialTheme.colorScheme.secondary,
                fontSize = 20.sp,
            )

            Text(
                text = "{PH} MPH", //Will Replace with string resource
                Modifier.constrainAs(avgSpeedValue) {
                    start.linkTo(startGuideline, 75.dp)
                    end.linkTo(parent.end, 1.dp)
                    bottom.linkTo(bottomGuideline)
                    top.linkTo(topGuideline, 25.dp)
                },
                color = MaterialTheme.colorScheme.primary,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold

            )

        }

    }
}


@Preview
@Composable
fun StatisticsPreview(){
    MaterialTheme{
        ConstraintLayoutStatistics()
    }


}


