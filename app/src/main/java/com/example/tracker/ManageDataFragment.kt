package com.example.tracker

import android.app.AlertDialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tracker.adapter.*
import com.example.tracker.data.Achievement
import com.example.tracker.data.HealthMilestone
import com.example.tracker.data.User
import com.example.tracker.viewmodel.TrackerViewModel
import kotlinx.coroutines.launch

class ManageDataFragment : Fragment() {
    private val viewModel: TrackerViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_manage_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- ПОЛЬЗОВАТЕЛИ ---
        val userAdapter = UserAdapter(
            onEdit = { showUserDialog(it) },
            onDelete = { viewModel.deleteUser(it) }
        )
        val rvUsers = view.findViewById<RecyclerView>(R.id.rvUsers)
        rvUsers.layoutManager = LinearLayoutManager(requireContext())
        rvUsers.adapter = userAdapter
        view.findViewById<Button>(R.id.btnAddUser).setOnClickListener { showUserDialog(null) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllUsersFlow().collect { userAdapter.submitList(it) }
        }

        // --- ВЕХИ ---
        val milestoneAdapter = MilestoneAdapter(
            onEdit = { showMilestoneDialog(it) },
            onDelete = { viewModel.deleteMilestone(it) }
        )
        val rvMilestones = view.findViewById<RecyclerView>(R.id.rvMilestones)
        rvMilestones.layoutManager = LinearLayoutManager(requireContext())
        rvMilestones.adapter = milestoneAdapter
        view.findViewById<Button>(R.id.btnAddMilestone).setOnClickListener { showMilestoneDialog(null) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllMilestones().collect { milestoneAdapter.submitList(it) }
        }

        // --- ДОСТИЖЕНИЯ ---
        val achAdapter = AchievementAdapter(
            onEdit = { showAchievementDialog(it) },
            onDelete = { viewModel.deleteAchievement(it) }
        )
        val rvAchievements = view.findViewById<RecyclerView>(R.id.rvAchievements)
        rvAchievements.layoutManager = LinearLayoutManager(requireContext())
        rvAchievements.adapter = achAdapter
        view.findViewById<Button>(R.id.btnAddAchievement).setOnClickListener { showAchievementDialog(null) }

        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.getAllAchievements().collect { achAdapter.submitList(it) }
        }
    }

    // ДИАЛОГ ПОЛЬЗОВАТЕЛЯ (С выпадающим списком)
    private fun showUserDialog(existingUser: User?) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(if (existingUser == null) "Добавить пользователя" else "Изменить")

        val layout = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(32, 16, 32, 16)
        }

        val etName = EditText(requireContext()).apply { hint = "Имя"; setText(existingUser?.name) }

        // Выпадающий список для типа зависимости
        val spinnerType = Spinner(requireContext())
        val types = arrayOf("Курение", "Алкоголь", "Оба")
        val adapterSpinner = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, types)
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerType.adapter = adapterSpinner
        // Устанавливаем текущее значение при редактировании
        if (existingUser != null) {
            val pos = types.indexOf(existingUser.addictionType)
            if (pos >= 0) spinnerType.setSelection(pos)
        }

        val etBudget = EditText(requireContext()).apply {
            hint = "Бюджет в день";
            inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            setText(existingUser?.dailyBudget?.toString())
        }

        layout.addView(TextView(requireContext()).apply { text = "Имя:" })
        layout.addView(etName)
        layout.addView(TextView(requireContext()).apply { text = "Тип зависимости:"; setPadding(0, 16, 0, 0) })
        layout.addView(spinnerType)
        layout.addView(TextView(requireContext()).apply { text = "Бюджет в день:"; setPadding(0, 16, 0, 0) })
        layout.addView(etBudget)

        builder.setView(layout)
        builder.setPositiveButton("Сохранить") { _, _ ->
            val user = User(
                id = existingUser?.id ?: 0,
                name = etName.text.toString(),
                birthDate = existingUser?.birthDate ?: System.currentTimeMillis(),
                gender = existingUser?.gender ?: "Не указан",
                quitStartDate = existingUser?.quitStartDate ?: System.currentTimeMillis(),
                addictionType = spinnerType.selectedItem.toString(), // Берем из списка
                dailyBudget = etBudget.text.toString().toDoubleOrNull() ?: 300.0,
                targetDays = existingUser?.targetDays ?: 30
            )
            if (existingUser == null) viewModel.insertUser(user) else viewModel.updateUser(user)
        }
        builder.show()
    }

    // ДИАЛОГ ВЕХИ
    private fun showMilestoneDialog(existing: HealthMilestone?) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(if (existing == null) "Добавить веху" else "Изменить")

        val layout = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL; setPadding(32, 16, 32, 16) }
        val etDays = EditText(requireContext()).apply { hint = "Кол-во дней (например: 7)"; inputType = android.text.InputType.TYPE_CLASS_NUMBER; setText(existing?.daysCount?.toString()) }
        val etDesc = EditText(requireContext()).apply { hint = "Описание (например: Вкус возвращается)"; setText(existing?.description) }
        layout.addView(etDays); layout.addView(etDesc)

        builder.setView(layout)
        builder.setPositiveButton("Сохранить") { _, _ ->
            val days = etDays.text.toString().toIntOrNull() ?: 0
            val item = HealthMilestone(
                id = existing?.id ?: 0,
                daysCount = days,
                description = etDesc.text.toString(),
                icon = existing?.icon ?: "default"
            )
            if (existing == null) viewModel.insertMilestone(item) else viewModel.updateMilestone(item)
        }
        builder.show()
    }

    // ДИАЛОГ ДОСТИЖЕНИЯ
    private fun showAchievementDialog(existing: Achievement?) {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle(if (existing == null) "Добавить достижение" else "Изменить")

        val layout = LinearLayout(requireContext()).apply { orientation = LinearLayout.VERTICAL; setPadding(32, 16, 32, 16) }
        val etType = EditText(requireContext()).apply { hint = "Название (например, 7 дней)"; setText(existing?.type) }
        layout.addView(etType)

        builder.setView(layout)
        builder.setPositiveButton("Сохранить") { _, _ ->
            val item = Achievement(
                id = existing?.id ?: 0,
                type = etType.text.toString(),
                achievedDate = System.currentTimeMillis()
            )
            if (existing == null) viewModel.insertAchievement(item) else viewModel.updateAchievement(item)
        }
        builder.show()
    }
}