package com.example.myapplication

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.Permission
import com.hjq.permissions.XXPermissions
import kotlinx.coroutines.flow.MutableStateFlow

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        modifier = Modifier
                            .verticalScroll(rememberScrollState())
                            .fillMaxSize()
                            .padding(innerPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Spacer(modifier = Modifier.height(40.dp))
                        Button(onClick = { requestMyPermissions() }) {
                            Text(text = "申请权限")
                        }
                        val exception = exceptionFlow.collectAsState().value
                        if (exception != null) {
                            Spacer(modifier = Modifier.height(20.dp))
                            Text(
                                text = exception.stackTraceToString(),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }
            }
        }
    }

    val exceptionFlow = MutableStateFlow<Throwable?>(null)

    val context = this

    fun requestMyPermissions() {
        try {
            XXPermissions.with(context)
                .unchecked()
                .permission(Permission.POST_NOTIFICATIONS)
                .request(object : OnPermissionCallback {
                    override fun onGranted(permissions: MutableList<String>, allGranted: Boolean) {
                        Toast.makeText(context, "权限申请成功", Toast.LENGTH_SHORT).show()
                    }

                    override fun onDenied(
                        permissions: MutableList<String>,
                        doNotAskAgain: Boolean
                    ) {
                        if (doNotAskAgain) {
                            Toast.makeText(context, "权限被拒绝且不再询问", Toast.LENGTH_SHORT)
                                .show()
                        } else {
                            Toast.makeText(context, "权限申请失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                })
            exceptionFlow.value = null
        } catch (e: Throwable) {
            exceptionFlow.value = e
            Toast.makeText(context, "发生错误: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
