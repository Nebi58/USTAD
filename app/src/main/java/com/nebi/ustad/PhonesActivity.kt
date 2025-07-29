package com.nebi.ustad

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class PhonesActivity : AppCompatActivity() {

    private lateinit var edtPhoneNumber: EditText
    private lateinit var btnVerificationCode: Button
    private lateinit var textInputLayoutPhone: TextInputLayout
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_phones)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_phones)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
        registerEventHandlers()

    }

    private fun initComponents() {
        edtPhoneNumber = findViewById(R.id.edtPhoneNumber)
        btnVerificationCode = findViewById(R.id.btnVerificationCode)
        textInputLayoutPhone = findViewById(R.id.textInputLayoutPhone)
        mAuth = FirebaseAuth.getInstance()
    }

    private fun registerEventHandlers() {
        verify()
    }

    private fun verify() {
        btnVerificationCode.setOnClickListener {
            val phoneNumber = edtPhoneNumber.text.toString().trim()
            if (!isTurkishPhone(phoneNumber)) {
                textInputLayoutPhone.error = "Kayıtlı telefon numarası bulunamadı"
                textInputLayoutPhone.setBoxStrokeColor(Color.parseColor("#E21849"))
                textInputLayoutPhone.setErrorTextColor(ColorStateList.valueOf(Color.parseColor("#E21849")))
                return@setOnClickListener
            } else {
                val fullPhoneNumber = "+90$phoneNumber"

                val options = PhoneAuthOptions.newBuilder(mAuth)
                    .setPhoneNumber(fullPhoneNumber)
                    .setTimeout(60L, TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                            Log.d("PhonesActivity", "onVerificationCompleted: $credential")
                        }

                        override fun onVerificationFailed(e: FirebaseException) {
                            Log.e("PhonesActivity", "onVerificationFailed", e)
                            Toast.makeText(this@PhonesActivity, "Doğrulama başarısız: ${e.message}", Toast.LENGTH_LONG
                            ).show()
                        }

                        override fun onCodeSent(
                            verificationId: String,
                            token: PhoneAuthProvider.ForceResendingToken
                        ) {
                            Log.d("PhonesActivity", "onCodeSent: $verificationId")
                            Toast.makeText(this@PhonesActivity, "Kod gönderildi!", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@PhonesActivity, VerifyActivity::class.java)
                            intent.putExtra("verificationId", verificationId)
                            intent.putExtra("phoneNumber", fullPhoneNumber)
                            startActivity(intent)
                        }
                    })
                    .build()

                PhoneAuthProvider.verifyPhoneNumber(options)
                mAuth.setLanguageCode("tr")
            }
        }
    }

    private fun isTurkishPhone(phoneNumber: String): Boolean {
        return phoneNumber.matches(Regex("^5\\d{9}$"))
    }
}