package menghu.chat.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Facebook
import androidx.compose.material.icons.filled.Person
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import menghu.chat.core.ui.component.OutlineTextField
import menghu.chat.core.ui.component.PrimaryButton
import menghu.chat.core.ui.theme.AppColors
import menghu.chat.feature.auth.viewmodel.LoginViewModel

/**
 * 登录页 Screen
 * - 顶部：渐变橙色装饰背景（含圆形/椭圆装饰）
 * - 中部：白色圆角卡片，承载 Email / Password / Log in / 三方登录
 * - 底部：跳转到注册页入口
 * 仅负责渲染与事件回调，业务逻辑通过 LoginViewModel 管理
 *
 * @param onLoginSuccess 登录成功时回调
 * @param onNavigateToRegister 点击注册入口时回调
 */
@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    // 使用生命周期感知的 StateFlow 读取，避免后台做无用重组
    val email by viewModel.email.collectAsStateWithLifecycle()
    val password by viewModel.password.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val loginSuccess by viewModel.loginSuccess.collectAsStateWithLifecycle()

    // 登录成功后回调上层执行跳转
    LaunchedEffect(loginSuccess) {
        if (loginSuccess) {
            onLoginSuccess()
            viewModel.resetLoginSuccess()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
    ) {
        // 顶部渐变橙色装饰背景
        AuthHeaderDecor()

        // 主体内容：白色圆角卡片
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "欢迎回来",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "登录你的梦狐账号",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
            Spacer(modifier = Modifier.height(32.dp))

            // 白色卡片表单
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // 邮箱
                    LabeledField("Email") {
                        OutlineTextField(
                            value = email,
                            onValueChange = { viewModel.updateEmail(it) },
                            placeholder = "请输入邮箱地址"
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))

                    // 密码
                    LabeledField("Password") {
                        OutlineTextField(
                            value = password,
                            onValueChange = { viewModel.updatePassword(it) },
                            placeholder = "请输入密码",
                            isPassword = true
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))

                    // 错误提示
                    if (!errorMessage.isNullOrEmpty()) {
                        Text(
                            text = errorMessage.orEmpty(),
                            color = AppColors.Error,
                            fontSize = 12.sp,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // 忘记密码（右对齐）
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Text(
                            text = "Forgot password?",
                            color = AppColors.OrangePrimary,
                            fontSize = 13.sp,
                            modifier = Modifier.clickable { /* TODO: 忘记密码路由 */ }
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))

                    // 登录按钮
                    PrimaryButton(
                        text = "Log in",
                        onClick = { viewModel.login() },
                        isLoading = isLoading
                    )
                    Spacer(modifier = Modifier.height(20.dp))

                    // 中部辅助文字：Login with existing account?
                    Text(
                        text = "Login with existing account?",
                        color = AppColors.TextSecondary,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    // 三方登录按钮：Google / Apple / Facebook
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        SocialIconButton(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Email,
                                    contentDescription = "Google",
                                    tint = Color(0xFF4285F4),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = "Google",
                            modifier = Modifier.weight(1f)
                        )
                        SocialIconButton(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Apple",
                                    tint = Color.Black,
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = "Apple",
                            modifier = Modifier.weight(1f)
                        )
                        SocialIconButton(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Facebook,
                                    contentDescription = "Facebook",
                                    tint = Color(0xFF1877F2),
                                    modifier = Modifier.size(20.dp)
                                )
                            },
                            label = "Facebook",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 底部：没有账号？Sign up
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account? ",
                    color = AppColors.TextSecondary,
                    fontSize = 14.sp
                )
                Text(
                    text = "Sign up",
                    color = AppColors.OrangePrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { onNavigateToRegister() }
                )
            }
        }
    }
}
