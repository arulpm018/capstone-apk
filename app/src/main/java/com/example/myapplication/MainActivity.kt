package com.example.myapplication

import RelatedDocument
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.google.android.material.chip.Chip
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.util.Log
import android.widget.Button

class MainActivity : AppCompatActivity() {

    // Variabel untuk view binding, adapter, ViewModel, dan SharedPreferences
    private lateinit var binding: ActivityMainBinding
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var documentAdapter: DocumentAdapter
    private val selectedDocuments = mutableSetOf<RelatedDocument>()
    private val viewModel: ChatViewModel by viewModels()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Mengatur layout binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Inisialisasi SharedPreferences
        sharedPreferences = getSharedPreferences("UserPrefs", MODE_PRIVATE)

        // Mengatur tombol keluar
        setupSignOutButton()

        // Mengatur toolbar, RecyclerView, UI interaksi, dan observer ViewModel
        setupToolbar()
        setupRecyclerViews()
        setupUI()
        observeViewModel()
    }

    // Mengatur tombol keluar (sign-out)
    private fun setupSignOutButton() {
        val signOutButton = findViewById<Button>(R.id.signOutButton)
        signOutButton.setOnClickListener {
            // Menghapus data pengguna dari SharedPreferences
            sharedPreferences.edit().clear().apply()

            // Berpindah ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish() // Mengakhiri aktivitas saat ini
        }
    }

    // Mengatur toolbar dan tombol sidebar
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        binding.sidebarButton.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START) // Membuka sidebar
        }
    }

    // Mengatur RecyclerView untuk chat dan dokumen
    private fun setupRecyclerViews() {
        // Mengatur adapter untuk chat
        chatAdapter = ChatAdapter()
        binding.chatRecyclerView.apply {
            adapter = chatAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        // Mengatur adapter untuk dokumen
        documentAdapter = DocumentAdapter { document, isChecked ->
            if (isChecked) {
                // Menambah dokumen yang dipilih
                selectedDocuments.add(document)
                addDocumentChip(document)
            } else {
                // Menghapus dokumen yang dipilih
                selectedDocuments.remove(document)
                removeDocumentChip(document)
            }
        }
        binding.documentsRecyclerView.apply {
            adapter = documentAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
    }

    // Menambah chip untuk dokumen yang dipilih
    @SuppressLint("NotifyDataSetChanged")
    private fun addDocumentChip(document: RelatedDocument) {
        val chip = Chip(this).apply {
            text = document.judul
            isCloseIconVisible = true
            setOnCloseIconClickListener {
                // Menghapus dokumen dari daftar pilihan
                selectedDocuments.remove(document)
                binding.selectedDocumentsGroup.removeView(this)
                documentAdapter.notifyDataSetChanged()
            }
        }
        binding.selectedDocumentsGroup.addView(chip)
    }

    // Menghapus chip dokumen
    private fun removeDocumentChip(document: RelatedDocument) {
        val chipCount = binding.selectedDocumentsGroup.childCount
        for (i in 0 until chipCount) {
            val chip = binding.selectedDocumentsGroup.getChildAt(i) as? Chip
            if (chip?.text == document.judul) {
                binding.selectedDocumentsGroup.removeView(chip)
                break
            }
        }
    }

    // Mengatur tombol interaksi UI
    private fun setupUI() {
        binding.sendButton.setOnClickListener {
            val message = binding.messageInput.text.toString()
            if (message.isNotEmpty() && selectedDocuments.isNotEmpty()) {
                // Menampilkan pesan pengguna di RecyclerView
                val spannableMessage = SpannableStringBuilder("You: $message")
                chatAdapter.addMessage(spannableMessage, true)

                // Menggabungkan konteks dari dokumen yang dipilih
                val combinedContext = selectedDocuments.joinToString("\n\n") { it.abstrak }
                viewModel.sendMessage(message, combinedContext)

                // Membersihkan input pesan
                binding.messageInput.text?.clear()
            } else if (selectedDocuments.isEmpty()) {
                Toast.makeText(this, "Please select at least one document", Toast.LENGTH_SHORT).show()
            }
        }

        binding.searchButton.setOnClickListener {
            val query = binding.searchInput.text.toString()
            if (query.isNotEmpty()) {
                viewModel.searchRelatedDocuments(query) // Memulai pencarian dokumen
            }
        }
    }


    // Memformat respons untuk chat
    private fun formatResponse(response: String): Spannable {
        val spannable = SpannableStringBuilder()
        val lines = response.lines()

        for (line in lines) {
            val trimmedLine = line.trim()
            val start = spannable.length

            // Bolding lines that start with '#' or '&'
            if (trimmedLine.startsWith("#") || trimmedLine.startsWith("*")) {
                spannable.append(trimmedLine.removePrefix("#").removePrefix("*") + "\n")
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    start,
                    spannable.length,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            } else {
                spannable.append(trimmedLine + "\n")
            }
        }
        return spannable
    }


    // Mengamati perubahan pada ViewModel
    private fun observeViewModel() {
        viewModel.relatedDocuments.observe(this) { documents ->
            Log.d("DOCUMENT_ADAPTER", "Documents: $documents")
            documentAdapter.submitList(documents)
        }

        viewModel.chatResponse.observe(this) { responseText ->
            responseText?.let {
                val formattedResponse = formatResponse(it)
                chatAdapter.addMessage(formattedResponse, false)
            }
        }

        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.isVisible = isLoading
        }

        viewModel.error.observe(this) { error ->
            if (error.isNotEmpty()) {
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
            }
        }
    }
}
