package com.abhiyantrik.dentalhub

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.abhiyantrik.dentalhub.entities.Ward
import com.abhiyantrik.dentalhub.interfaces.DjangoInterface
import com.abhiyantrik.dentalhub.models.LoginResponse
import com.google.firebase.perf.metrics.AddTrace
import io.objectbox.Box
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginActivity : Activity() {
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvErrorMessage: TextView
    private lateinit var loading: ProgressBar
    private lateinit var btnLogin: Button
    private lateinit var context: Context

    private val TAG = "LoginActivity"

    @AddTrace(name = "onCreateTrace", enabled = true /* optional */)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        context = this
        setupUI()
    }

    @AddTrace(name = "setupUI", enabled = true /* optional */)
    private fun setupUI() {
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        loading = findViewById(R.id.loading)
        tvErrorMessage = findViewById(R.id.tvErrorMessage)

        if (Build.VERSION.SDK_INT>22){
            etEmail.background = getDrawable(R.drawable.auth_fields)
            etPassword.background = getDrawable(R.drawable.auth_fields)
        }else{
            etEmail.background = getDrawable(R.drawable.auth_fields_fallback)
            etPassword.background = getDrawable(R.drawable.auth_fields_fallback)
        }

        btnLogin.setOnClickListener {
            tvErrorMessage.visibility = View.GONE
            if (formIsValid()) {
                processLogin()
            }
        }
        etPassword.setOnEditorActionListener(object : TextView.OnEditorActionListener {
            override fun onEditorAction(p0: TextView?, p1: Int, p2: KeyEvent?): Boolean {
                if (p1 == EditorInfo.IME_ACTION_GO) {
                    tvErrorMessage.visibility = View.GONE
                    if (formIsValid()) {
                        processLogin()
                    }
                    return true
                }
                return false
            }

        })
    }

    @AddTrace(name = "processLogin", enabled = true /* optional */)
    private fun processLogin() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }


        Log.d(TAG, "processLogin()")
        loading.visibility = View.VISIBLE
        tvErrorMessage.visibility = View.GONE
        val email = etEmail.text.toString()
        val password = etPassword.text.toString()
        val panelService = DjangoInterface.create(this)
        val call = panelService.login(email, password)
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                Log.d(TAG, "onResponse()")
                Log.d("Resp", response.toString())
                if (null != response.body()) {
                    when (response.code()) {
                        200 -> {
                            val loginResponse = response.body() as LoginResponse
                            DentalApp.saveToPreference(
                                context,
                                Constants.PREF_AUTH_TOKEN,
                                loginResponse.token
                            )
                            DentalApp.saveToPreference(
                                context,
                                Constants.PREF_AUTH_EMAIL,
                                email
                            )
                            DentalApp.saveToPreference(
                                context,
                                Constants.PREF_AUTH_PASSWORD,
                                password
                            )
                            startActivity(Intent(context, SetupActivity::class.java))
                            finish()
                        }
                        400 -> {
                            tvErrorMessage.text = getString(R.string.error_http_400)
                            tvErrorMessage.visibility = View.VISIBLE
                            loading.visibility = View.GONE
                        }
                        404 -> {
                            tvErrorMessage.text = getString(R.string.error_http_404)
                            tvErrorMessage.visibility = View.VISIBLE
                            loading.visibility = View.GONE
                        }
                        else -> {
                            tvErrorMessage.text = getString(R.string.error_http_500)
                            tvErrorMessage.visibility = View.VISIBLE
                            loading.visibility = View.GONE
                        }
                    }
                    loading.visibility = View.GONE
                } else {
                    if (response.code() == 400) {
                        tvErrorMessage.text = getString(R.string.username_password_dont_matched)
                        tvErrorMessage.visibility = View.VISIBLE
                        loading.visibility = View.GONE
                    }

                    if(BuildConfig.DEBUG){
                        Log.d("response CODE", response.code().toString())
                        Log.d("response BODY", response.errorBody().toString())
                    }
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.d(TAG, "onFailure()")
                if (BuildConfig.DEBUG) {
                    tvErrorMessage.text = t.message.toString()
                } else {
                    tvErrorMessage.text = getString(R.string.failure_message)
                }
                tvErrorMessage.visibility = View.VISIBLE
                loading.visibility = View.GONE
            }

        })

    }

    @AddTrace(name = "formIsValid", enabled = true /* optional */)
    private fun formIsValid(): Boolean {
        tvErrorMessage.visibility = View.GONE
        var status = false
        if (etEmail.text.isBlank()) {
            status = false
            tvErrorMessage.text = getString(R.string.username_is_required)
            tvErrorMessage.visibility = View.VISIBLE
        } else if (etPassword.text.isBlank()) {
            status = false
            tvErrorMessage.text = getString(R.string.password_is_required)
            tvErrorMessage.visibility = View.VISIBLE
        } else {
            status = true
        }
        return status
    }

}