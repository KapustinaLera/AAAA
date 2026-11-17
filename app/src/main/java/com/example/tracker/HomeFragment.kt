package com.example.tracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.tracker.utils.PreferencesManager
import com.example.tracker.viewmodel.QuoteState
import com.example.tracker.viewmodel.QuoteViewModel
import com.example.tracker.viewmodel.TrackerViewModel
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var prefs: PreferencesManager
    private val quoteViewModel: QuoteViewModel by viewModels()
    private val trackerViewModel: TrackerViewModel by viewModels()

    private lateinit var tvGreeting: TextView
    private lateinit var layoutNameInput: LinearLayout
    private lateinit var layoutStats: LinearLayout
    private lateinit var etUserName: EditText
    private lateinit var tvDays: TextView
    private lateinit var tvMoney: TextView
    private lateinit var tvTotalSmoked: TextView // <-- НОВОЕ
    private lateinit var tvTotalDrunk: TextView  // <-- НОВОЕ

    private var totalSpent = 0.0
    private var totalSmoked = 0 // <-- НОВОЕ
    private var totalDrunk = 0  // <-- НОВОЕ

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        prefs = PreferencesManager(requireContext())

        tvGreeting = view.findViewById(R.id.tvGreeting)
        layoutNameInput = view.findViewById(R.id.layoutNameInput)
        layoutStats = view.findViewById(R.id.layoutStats)
        etUserName = view.findViewById(R.id.etUserName)
        tvDays = view.findViewById(R.id.tvDaysWithout)
        tvMoney = view.findViewById(R.id.tvMoneySaved)

        // Находим новые элементы
        tvTotalSmoked = view.findViewById(R.id.tvTotalSmoked)
        tvTotalDrunk = view.findViewById(R.id.tvTotalDrunk)

        val btnSaveName = view.findViewById<Button>(R.id.btnSaveName)
        val progressBar = view.findViewById<ProgressBar>(R.id.progressBarQuote)
        val tvQuoteText = view.findViewById<TextView>(R.id.tvQuoteText)
        val tvQuoteAuthor = view.findViewById<TextView>(R.id.tvQuoteAuthor)
        val tvError = view.findViewById<TextView>(R.id.tvError)
        val btnRefreshQuote = view.findViewById<Button>(R.id.btnRefreshQuote)

        btnSaveName.setOnClickListener {
            val newName = etUserName.text.toString().trim()
            if (newName.isNotEmpty()) {
                prefs.setUserName(newName)
                updateUI()
                Toast.makeText(requireContext(), getString(R.string.greeting_intro, newName), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), getString(R.string.error_enter_name), Toast.LENGTH_SHORT).show()
            }
        }

        btnRefreshQuote.setOnClickListener { quoteViewModel.fetchQuote() }

        // Подписка на цитаты
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                quoteViewModel.quoteState.collect { state ->
                    when (state) {
                        is QuoteState.Loading -> {
                            progressBar.visibility = View.VISIBLE
                            tvQuoteText.visibility = View.GONE
                            tvQuoteAuthor.visibility = View.GONE
                            tvError.visibility = View.GONE
                            btnRefreshQuote.visibility = View.GONE
                        }
                        is QuoteState.Success -> {
                            progressBar.visibility = View.GONE
                            tvQuoteText.visibility = View.VISIBLE
                            tvQuoteAuthor.visibility = View.VISIBLE
                            tvError.visibility = View.GONE
                            btnRefreshQuote.visibility = View.VISIBLE
                            tvQuoteText.text = getString(R.string.quote_format, state.quote.quoteText)
                            tvQuoteAuthor.text = getString(R.string.quote_author_format, state.quote.quoteAuthor)
                        }
                        is QuoteState.Error -> {
                            progressBar.visibility = View.GONE
                            tvQuoteText.visibility = View.GONE
                            tvQuoteAuthor.visibility = View.GONE
                            tvError.visibility = View.VISIBLE
                            btnRefreshQuote.visibility = View.VISIBLE
                            tvError.text = state.message
                        }
                    }
                }
            }
        }

        // ПОДПИСКА НА ЗАПИСИ
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                trackerViewModel.getAllRecords().collect { records ->
                    totalSpent = records.sumOf { it.moneySpent }
                    totalSmoked = records.sumOf { it.amountSmoked } // <-- СУММИРУЕМ
                    totalDrunk = records.sumOf { it.amountDrunk }   // <-- СУММИРУЕМ
                    updateUI()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::prefs.isInitialized) updateUI()
    }

    private fun updateUI() {
        val name = prefs.getUserName()
        if (name.isEmpty()) {
            tvGreeting.text = "Привет!"
            layoutNameInput.visibility = View.VISIBLE
            layoutStats.visibility = View.GONE
        } else {
            tvGreeting.text = getString(R.string.greeting_hello, name)
            layoutNameInput.visibility = View.GONE
            layoutStats.visibility = View.VISIBLE

            val days = ((System.currentTimeMillis() - prefs.getQuitDate()) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)
            val expectedBudget = days * prefs.getDailyBudget()
            val savings = expectedBudget - totalSpent

            tvDays.text = getString(R.string.days_without_habit, days)
            tvMoney.text = getString(R.string.money_saved, String.format("%.2f", savings))

            // ВЫВОДИМ ЗДОРОВЬЕ
            tvTotalSmoked.text = "Всего выкурено: $totalSmoked шт."
            tvTotalDrunk.text = "Всего выпито: $totalDrunk мл"

            // Красим в зеленый, если 0, и в красный, если есть срывы
            if (totalSmoked == 0) tvTotalSmoked.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            else tvTotalSmoked.setTextColor(android.graphics.Color.RED)

            if (totalDrunk == 0) tvTotalDrunk.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            else tvTotalDrunk.setTextColor(android.graphics.Color.RED)
        }
    }
}