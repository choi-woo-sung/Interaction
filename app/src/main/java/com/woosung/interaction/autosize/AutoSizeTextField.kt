package com.woosung.interaction.autosize

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFontFamilyResolver
import androidx.compose.ui.text.Paragraph
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.woosung.interaction.ext.dpToPx
import kotlin.math.ceil

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoSizableOutLinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 32.sp,
    maxLines: Int = Int.MAX_VALUE,
    minFontSize: TextUnit,
    scaleFactor: Float = 0.9f,
) {
    BoxWithConstraints(
        modifier = modifier,
    ) {
        // 폰트사이즈가 알아서 변경되어야함.
        var nFontSize = fontSize

        val calculateParagraph = @Composable {
            Paragraph(
                text = value,
                style = TextStyle(fontSize = nFontSize),
                constraints = Constraints(maxWidth = ceil(with<Density, Float>(LocalDensity.current) { (maxWidth - 32.dp).toPx() }).toInt()),
                density = LocalDensity.current,
                fontFamilyResolver = LocalFontFamilyResolver.current,
                spanStyles = listOf(),
                placeholders = listOf(),
                maxLines = maxLines,
                ellipsis = false,
            )
        }

        var intrinsics = calculateParagraph()

        with(LocalDensity.current) {
            while ((intrinsics.height.toDp() > maxHeight || intrinsics.didExceedMaxLines) && nFontSize >= minFontSize) {
                nFontSize *= scaleFactor
                intrinsics = calculateParagraph()
            }
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxSize(),
            shape = RoundedCornerShape(8.dp),
            maxLines = maxLines,
            textStyle = TextStyle(fontSize = nFontSize),

        )
    }
}

@Composable
fun AutoSizeTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    fontSize: TextUnit = 32.sp,
    maxLines: Int = Int.MAX_VALUE,
    minFontSize: TextUnit,
    scaleFactor: Float = 0.9f,

) {

    val density = LocalDensity.current
    BoxWithConstraints(
        modifier = modifier,
    ) {
        var resizedStyle by remember { mutableStateOf(fontSize) }

        // https://developer.android.com/jetpack/compose/text
        BasicTextField(
            value = value,
            maxLines = 3,
            onValueChange = onValueChange,
            textStyle = TextStyle.Default.copy(fontSize = resizedStyle),
            onTextLayout = { result ->
                if (with(density) { result.multiParagraph.height > minHeight.toPx() }) {
                    resizedStyle = resizedStyle * scaleFactor
                }
            },
        )
    }
}

@Preview
@Composable
fun DefaultPreview() {
    var text by remember { mutableStateOf("") }

    MaterialTheme() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(0.7f))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "AutoSize  TextField",
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            AutoSizableOutLinedTextField(
                value = text,
                onValueChange = { text = it },
                maxLines = 2,
                minFontSize = 10.sp,
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 20.dp)
                    .fillMaxWidth()
                    .height(150.dp),
            )
        }
    }
}

@Preview
@Composable
fun DefaultTestPreview() {
    var text by remember { mutableStateOf("") }

    MaterialTheme() {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.primary.copy(0.7f))
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "AutoSize  TextField",
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            AutoSizeTextField(
                value = text,
                onValueChange = { text = it },
                maxLines = 2,
                minFontSize = 10.sp,
                modifier = Modifier
                    .padding(top = 20.dp, bottom = 20.dp)
                    .fillMaxWidth()
                    .height(150.dp),
            )
        }
    }
}
