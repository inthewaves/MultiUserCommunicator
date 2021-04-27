package com.example.multiusercommunicator.ui.login

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.text.Editable
import android.text.TextWatcher
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.multiusercommunicator.R
import com.example.multiusercommunicator.databinding.ActivityLoginBinding
import com.example.multiusercommunicator.service.ILoginService
import com.example.multiusercommunicator.service.LoginService

class LoginActivity : AppCompatActivity() {

    private var service: ILoginService? = null

    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val serviceIntent = Intent()
        serviceIntent.component = ComponentName(this, LoginService::class.java)

        bindService(
            serviceIntent,
            object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    check(service != null)
                    val localImplementation = service.queryLocalInterface(
                        "com.example.multiusercommunicator.service.ILoginService"
                    )
                    this@LoginActivity.service = if (localImplementation is ILoginService) {
                        localImplementation
                    } else {
                        error("missing local implementation")
                    }

                    // localImplementation.addListenerOrRemoveIfNull()
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                }
            } ,
            Context.BIND_NOT_FOREGROUND or Context.BIND_AUTO_CREATE
        )

        binding.password.apply {
            setOnEditorActionListener { _, actionId, _ ->
                when (actionId) {
                    EditorInfo.IME_ACTION_DONE ->
                        login(binding.username.text.toString(), binding.password.text.toString())
                }
                false
            }

            binding.login.setOnClickListener {
                login(binding.username.text.toString(), binding.password.text.toString())
            }
        }


    }


    private fun login(username: String, password: String) {
        binding.loading.isVisible = true

        service?.let { service ->
            val isLoginSuccessful = service.login(username, password)
            if (isLoginSuccessful) {
                binding.currentLoggedInUserTextView.text = "ME!"
            } else {
                showLoginFailed(R.string.login_failed_service_returned_failure)
            }
        } ?: run {
            showLoginFailed(R.string.login_failed_missing_service)
        }

        binding.loading.isVisible = false
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        binding.loading.isVisible = false
        Toast.makeText(applicationContext, errorString, Toast.LENGTH_SHORT).show()
    }
}

/**
 * Extension function to simplify setting an afterTextChanged action to EditText components.
 */
fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }

        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
    })
}