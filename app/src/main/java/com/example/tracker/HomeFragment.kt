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
import java.util.Calendar

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
    private lateinit var tvTotalSmoked: TextView
    private lateinit var tvTotalDrunk: TextView
    private lateinit var tvAchievement: TextView
    private lateinit var tvMilestone: TextView

    private var totalSpent = 0.0
    private var totalSmoked = 0
    private var totalDrunk = 0
    private var currentStreak = 0

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
        tvTotalSmoked = view.findViewById(R.id.tvTotalSmoked)
        tvTotalDrunk = view.findViewById(R.id.tvTotalDrunk)
        tvAchievement = view.findViewById(R.id.tvAchievement)
        tvMilestone = view.findViewById(R.id.tvMilestone)

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

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                trackerViewModel.getAllRecords().collect { records ->
                    totalSpent = records.sumOf { it.moneySpent }
                    totalSmoked = records.sumOf { it.amountSmoked }
                    totalDrunk = records.sumOf { it.amountDrunk }

                    // ЛОГИКА СЧЕТЧИКА: ищем день последнего срыва
                    val relapseDays = records
                        .filter { it.amountSmoked > 0 || it.amountDrunk > 0 }
                        .map { getStartOfDay(it.date) }

                    val latestRelapseDay = if (relapseDays.isNotEmpty()) {
                        relapseDays.maxOrNull()!!
                    } else {
                        getStartOfDay(prefs.getQuitDate())
                    }

                    prefs.setLastRelapseDate(latestRelapseDay)

                    val todayStart = getStartOfDay(System.currentTimeMillis())
                    currentStreak = ((todayStart - latestRelapseDay) / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(0)

                    updateUI()
                }
            }
        }
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val cal = Calendar.getInstance()
        cal.timeInMillis = timestamp
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
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

            val expectedBudget = currentStreak * prefs.getDailyBudget()
            val savings = expectedBudget - totalSpent

            tvMoney.text = getString(R.string.money_saved, String.format("%.2f", savings))
            tvTotalSmoked.text = "Всего выкурено: $totalSmoked шт."
            tvTotalDrunk.text = "Всего выпито: $totalDrunk мл"

            if (totalSmoked == 0) tvTotalSmoked.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            else tvTotalSmoked.setTextColor(android.graphics.Color.RED)

            if (totalDrunk == 0) tvTotalDrunk.setTextColor(android.graphics.Color.parseColor("#4CAF50"))
            else tvTotalDrunk.setTextColor(android.graphics.Color.RED)

            if (currentStreak == 0) {
                tvDays.text = "Статус: Срыв (счетчик сброшен)"
                tvDays.setTextColor(android.graphics.Color.RED)
                tvAchievement.text = "🏆 Достижение: Пока нет"
                tvMilestone.text = "💡 Здоровье: Вы сбились с пути, начните заново!"
            } else {
                tvDays.text = getString(R.string.days_without_habit, currentStreak)
                tvDays.setTextColor(android.graphics.Color.parseColor("#4CAF50"))

                var achievementText = "🏆 Достижение: Пока нет"
                if (currentStreak >= 30) achievementText = "🏆 Достижение: Месяц силы! (30 дней)"
                else if (currentStreak >= 7) achievementText = "🏆 Достижение: Неделя без привычки! (7 дней)"
                else if (currentStreak >= 1) achievementText = "🏆 Достижение: Первый шаг! (1 день)"
                tvAchievement.text = achievementText

                var milestoneText = "💡 Здоровье: Начало пути"
                if (currentStreak >= 30) milestoneText = "💡 Здоровье: Легкие очищаются, функция дыхания улучшается!"
                else if (currentStreak >= 7) milestoneText = "💡 Здоровье: Вкус и запах становятся острее."
                else if (currentStreak >= 1) milestoneText = "💡 Здоровье: Уровень кислорода в крови нормализуется."
                tvMilestone.text = milestoneText
            }
        }
    }
}