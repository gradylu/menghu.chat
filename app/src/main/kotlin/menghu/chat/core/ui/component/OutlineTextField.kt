package menghu.chat.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import menghu.chat.core.ui.theme.AppColors

/**
 * 带边框的圆角输入框
 * 支持普通文本与密码显示切换
 *
 * @param value 当前文本值
 * @param onValueChange 值变化回调
 * @param label 顶部标签文本
 * @param placeholder 占位符文本
 * @param isPassword 是否为密码类型
 * @param modifier 自定义修饰符
 */
@Composable
fun OutlineTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String? = null,
    placeholder: String? = null,
    isPassword: Boolean = false,
    modifier: Modifier = Modifier
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = label?.let {
            { Text(text = it, color = AppColors.TextSecondary) }
        },
        placeholder = placeholder?.let {
            { Text(text = it, color = AppColors.TextHint) }
        },
        visualTransformation = if (isPassword && !passwordVisible) {
            PasswordVisualTransformation()
        } else {
            VisualTransformation.None
        },
        trailingIcon = if (isPassword) {
            {
                val icon = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.Visibility
                val description = if (passwordVisible) "隐藏密码" else "显示密码"
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = description,
                        tint = AppColors.TextSecondary
                    )
                }
            }
        } else null,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        shape = RoundedCornerShape(12.dp),
        textStyle = TextStyle(fontSize = 14.sp, color = AppColors.TextPrimary),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = AppColors.OrangePrimary,
            unfocusedBorderColor = AppColors.Border,
            cursorColor = AppColors.OrangePrimary,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White
        ),
        singleLine = true
    )
}
