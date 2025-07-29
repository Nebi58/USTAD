package com.nebi.ustad

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthProvider

class VerifyActivity : AppCompatActivity() {

    private lateinit var edtCode1: EditText
    private lateinit var edtCode2: EditText
    private lateinit var edtCode3: EditText
    private lateinit var edtCode4: EditText
    private lateinit var edtCode5: EditText
    private lateinit var edtCode6: EditText
    private lateinit var txtInput1: TextInputLayout
    private lateinit var txtInput2: TextInputLayout
    private lateinit var txtInput3: TextInputLayout
    private lateinit var txtInput4: TextInputLayout
    private lateinit var txtInput5: TextInputLayout
    private lateinit var txtInput6: TextInputLayout
    private lateinit var btnVerification: Button
    private lateinit var btnExit: Button
    private lateinit var txtDescription: TextView

    private lateinit var verificationId: String
    private lateinit var phoneNumber: String
    private lateinit var mAuth: FirebaseAuth
    private var countDownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_verify)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_verify)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initComponents()
        fromPhoneActivity()
        registerEventHandlers()
        updateDescriptionText()
        startCountDownTimer()
        setupTextWatchers()
    }


    private fun initComponents() {
        edtCode1 = findViewById(R.id.edtCode1)
        edtCode2 = findViewById(R.id.edtCode2)
        edtCode3 = findViewById(R.id.edtCode3)
        edtCode4 = findViewById(R.id.edtCode4)
        edtCode5 = findViewById(R.id.edtCode5)
        edtCode6 = findViewById(R.id.edtCode6)

        txtInput1 = findViewById(R.id.textInputLayout1)
        txtInput2 = findViewById(R.id.textInputLayout2)
        txtInput3 = findViewById(R.id.textInputLayout3)
        txtInput4 = findViewById(R.id.textInputLayout4)
        txtInput5 = findViewById(R.id.textInputLayout5)
        txtInput6 = findViewById(R.id.textInputLayout6)

        btnVerification = findViewById(R.id.btnVerification)
        btnExit = findViewById(R.id.btnExit)
        txtDescription = findViewById(R.id.txtDescription)

        mAuth = FirebaseAuth.getInstance()
    }

    private fun fromPhoneActivity() {
        verificationId = intent.getStringExtra("verificationId") ?: ""
        phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
    }

    private fun registerEventHandlers() {
        btnVerification.setOnClickListener {
            val code = getFullCode()
            if (code.length == 6) {
                val intent = Intent(this, CompanyActivity::class.java)
                startActivity(intent)
            } else {
                val errorColor = ColorStateList.valueOf(Color.parseColor("#E21849"))
                listOf(txtInput1, txtInput2, txtInput3, txtInput4, txtInput5, txtInput6).forEach {
                    it.setErrorTextColor(errorColor)
                }
            }
        }

        btnExit.setOnClickListener {
            val intent = Intent(this, PhonesActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun updateDescriptionText() {
        if (phoneNumber.isNotEmpty()) {
            val formattedPhone = formatPhoneNumber(phoneNumber)
            txtDescription.text =
                "Lütfen $formattedPhone numaralı cep telefonunuza gönderilen doğrulama şifresini girin."
        }
    }

    private fun formatPhoneNumber(phone: String): String {
        return if (phone.startsWith("+90") && phone.length == 13) {
            val number = phone.substring(3)
            "+90 ${number.substring(0, 3)} ${number.substring(3, 6)} ${number.substring(6, 8)} ${number.substring(8)}"
        } else phone
    }

    private fun startCountDownTimer() {
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val seconds = (millisUntilFinished / 1000).toInt()
                val timeText = String.format("%02d:%02d", seconds / 60, seconds % 60)
                val baseText =
                    "Lütfen ${formatPhoneNumber(phoneNumber)} numaralı cep telefonunuza gönderilen doğrulama şifresini girin. $timeText"
                txtDescription.text = baseText
            }

            override fun onFinish() {
                val baseText =
                    "Lütfen ${formatPhoneNumber(phoneNumber)} numaralı cep telefonunuza gönderilen doğrulama şifresini girin. 00:00"
                txtDescription.text = baseText
            }
        }.start()
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }

    private fun setupTextWatchers() {
        edtCode1.addTextChangedListener(CodeTextWatcher(edtCode1, edtCode2, null))
        edtCode2.addTextChangedListener(CodeTextWatcher(edtCode2, edtCode3, edtCode1))
        edtCode3.addTextChangedListener(CodeTextWatcher(edtCode3, edtCode4, edtCode2))
        edtCode4.addTextChangedListener(CodeTextWatcher(edtCode4, edtCode5, edtCode3))
        edtCode5.addTextChangedListener(CodeTextWatcher(edtCode5, edtCode6, edtCode4))
        edtCode6.addTextChangedListener(CodeTextWatcher(edtCode6, null, edtCode5))

        setupButtonControlWatcher()
    }

    private fun setupButtonControlWatcher() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                checkAndEnableButtons()
            }
        }

        listOf(edtCode1, edtCode2, edtCode3, edtCode4, edtCode5, edtCode6).forEach {
            it.addTextChangedListener(watcher)
        }
    }

    private fun checkAndEnableButtons() {
        val fullCode = getFullCode()
        val enabled = fullCode.length == 6
        btnVerification.isEnabled = enabled
        btnExit.isEnabled = enabled
        btnVerification.alpha = if (enabled) 1.0f else 0.5f
        btnExit.alpha = if (enabled) 1.0f else 0.5f
    }

    private fun getFullCode(): String {
        return edtCode1.text.toString() +
                edtCode2.text.toString() +
                edtCode3.text.toString() +
                edtCode4.text.toString() +
                edtCode5.text.toString() +
                edtCode6.text.toString()
    }

    private fun verifyCode(code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Doğrulama başarılı!", Toast.LENGTH_SHORT).show()
                // Giriş sonrası yönlendirme yapılabilir
            } else {
                Toast.makeText(this, "Kod yanlış veya doğrulama başarısız!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }
}