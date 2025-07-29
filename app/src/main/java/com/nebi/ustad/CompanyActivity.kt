package com.nebi.ustad

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class CompanyActivity : AppCompatActivity() {

    private lateinit var autoCompleteCompany: AutoCompleteTextView
    private lateinit var btnForward: Button
    private lateinit var btnExit: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_company)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.activity_company)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initComponents()
        registerEventHandlers()
        setupCompanyList()
        setupAutoCompleteListener()

    }

    private fun initComponents() {
        autoCompleteCompany = findViewById(R.id.autoCompleteCompany)
        btnForward = findViewById(R.id.btnForward)
        btnExit = findViewById(R.id.btnExit)
    }

    private fun registerEventHandlers() {
        btnForward.setOnClickListener {
            val selectedCompany = autoCompleteCompany.text.toString()
            if (selectedCompany.isNotEmpty()) {
                Toast.makeText(this, "Seçilen şirket: $selectedCompany", Toast.LENGTH_SHORT).show()
                val intent = Intent(this@CompanyActivity, ErrorActivity::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Lütfen bir şirket seçin", Toast.LENGTH_SHORT).show()
            }
        }

        btnExit.setOnClickListener {
            val intent = Intent(this, PhoneActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    private fun setupCompanyList() {
        val companies = arrayOf(
            "ABC Teknoloji",
            "XYZ Holding",
            "123 İnşaat"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, companies)
        autoCompleteCompany.setAdapter(adapter)
        autoCompleteCompany.threshold = 1
    }

    private fun setupAutoCompleteListener() {
        autoCompleteCompany.setOnItemClickListener { _, _, _, _ ->
            enableButtons()
        }
    }

    private fun enableButtons() {
        btnForward.isEnabled = true
        btnExit.isEnabled = true
        btnForward.alpha = 1.0f
        btnExit.alpha = 1.0f
    }

}