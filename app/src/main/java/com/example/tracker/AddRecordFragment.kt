package com.example.tracker

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.NavHostFragment
import com.example.tracker.data.DayRecord
import com.example.tracker.utils.PreferencesManager
import com.example.tracker.viewmodel.TrackerViewModel
import kotlinx.coroutines.launch
import java.io.File
import java.util.Calendar

class AddRecordFragment : Fragment() {

    private val viewModel: TrackerViewModel by viewModels()
    private var recordIdToEdit: Int = 0

    private var currentPhotoUri: Uri? = null
    private var currentPhotoPath: String? = null

    // САМЫЙ ПРОСТОЙ И БЕЗОПАСНЫЙ ВЫБОР ФОТО (доступ только к 1 файлу)
    private val pickImage = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            currentPhotoUri = uri
            currentPhotoPath = uri.toString()
            view?.findViewById<ImageView>(R.id.ivPhoto)?.setImageURI(uri)
        }
    }

    // КАМЕРА
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoUri?.let { uri ->
                currentPhotoPath = uri.toString()
                view?.findViewById<ImageView>(R.id.ivPhoto)?.setImageURI(uri)
            }
        }
    }

    private val requestCameraPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) openCamera()
        else Toast.makeText(requireContext(), "Разрешение на камеру отклонено", Toast.LENGTH_SHORT).show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_record, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvCravingLabel = view.findViewById<TextView>(R.id.tvCravingLabel)
        val sbCravingLevel = view.findViewById<SeekBar>(R.id.sbCravingLevel)
        val tvWellBeingLabel = view.findViewById<TextView>(R.id.tvWellBeingLabel)
        val sbWellBeing = view.findViewById<SeekBar>(R.id.sbWellBeing)
        val tvSleepLabel = view.findViewById<TextView>(R.id.tvSleepLabel)
        val sbSleepQuality = view.findViewById<SeekBar>(R.id.sbSleepQuality)

        val etMoneySpent = view.findViewById<EditText>(R.id.etMoneySpent)
        val etMood = view.findViewById<EditText>(R.id.etMood)
        val cbPhysicalActivity = view.findViewById<CheckBox>(R.id.cbPhysicalActivity)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        val ivPhoto = view.findViewById<ImageView>(R.id.ivPhoto)
        val btnPickGallery = view.findViewById<Button>(R.id.btnPickGallery)
        val btnTakePhoto = view.findViewById<Button>(R.id.btnTakePhoto)

        val layoutSmoking = view.findViewById<LinearLayout>(R.id.layoutSmoking)
        val layoutAlcohol = view.findViewById<LinearLayout>(R.id.layoutAlcohol)
        val etSmokedAmount = view.findViewById<EditText>(R.id.etSmokedAmount)
        val etAlcoholAmount = view.findViewById<EditText>(R.id.etAlcoholAmount)

        val prefs = PreferencesManager(requireContext())

        when (prefs.getAddictionType()) {
            "Курение" -> layoutSmoking.isVisible = true
            "Алкоголь" -> layoutAlcohol.isVisible = true
            "Оба" -> {
                layoutSmoking.isVisible = true
                layoutAlcohol.isVisible = true
            }
        }

        sbCravingLevel.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvCravingLabel.text = getString(R.string.craving_label_format, progress + 1)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sbWellBeing.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvWellBeingLabel.text = getString(R.string.well_being_label_format, progress + 1)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        sbSleepQuality.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tvSleepLabel.text = getString(R.string.sleep_label_format, progress + 1)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // КНОПКА ГАЛЕРЕИ (СРАЗУ ОТКРЫВАЕТ СИСТЕМНЫЙ ВЫБОР)
        btnPickGallery.setOnClickListener {
            pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        btnTakePhoto.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openCamera()
            } else {
                requestCameraPermission.launch(Manifest.permission.CAMERA)
            }
        }

        recordIdToEdit = arguments?.getInt("recordId", 0) ?: 0

        if (recordIdToEdit > 0) {
            viewLifecycleOwner.lifecycleScope.launch {
                val existingRecord = viewModel.getRecordById(recordIdToEdit)
                existingRecord?.let { r ->
                    sbCravingLevel.progress = r.cravingLevel - 1
                    sbWellBeing.progress = r.wellBeing - 1
                    sbSleepQuality.progress = r.sleepQuality - 1
                    etMoneySpent.setText(r.moneySpent.toString())
                    etMood.setText(r.mood)
                    cbPhysicalActivity.isChecked = r.physicalActivity
                    etNotes.setText(r.notes)

                    if (layoutSmoking.isVisible) etSmokedAmount.setText(r.amountSmoked.toString())
                    if (layoutAlcohol.isVisible) etAlcoholAmount.setText(r.amountDrunk.toString())

                    currentPhotoPath = r.photoPath
                    if (r.photoPath != null) {
                        ivPhoto.setImageURI(r.photoPath.toUri())
                    }
                }
            }
        }

        btnSave.setOnClickListener {
            val cravingLevel = sbCravingLevel.progress + 1
            val wellBeing = sbWellBeing.progress + 1
            val sleepQuality = sbSleepQuality.progress + 1
            val moneySpent = etMoneySpent.text.toString().toDoubleOrNull() ?: 0.0

            val smokedVal = etSmokedAmount.text.toString()
            val alcoholVal = etAlcoholAmount.text.toString()

            val amountSmoked = if (prefs.getAddictionType() == "Курение" || prefs.getAddictionType() == "Оба") smokedVal.toIntOrNull() ?: 0 else 0
            val amountDrunk = if (prefs.getAddictionType() == "Алкоголь" || prefs.getAddictionType() == "Оба") alcoholVal.toIntOrNull() ?: 0 else 0

            var consumedText = "Не указано"
            when (prefs.getAddictionType()) {
                "Курение" -> if (smokedVal.isNotEmpty()) consumedText = "$smokedVal шт."
                "Алкоголь" -> if (alcoholVal.isNotEmpty()) consumedText = "$alcoholVal мл"
                "Оба" -> {
                    val parts = mutableListOf<String>()
                    if (smokedVal.isNotEmpty()) parts.add("$smokedVal шт. сигарет")
                    if (alcoholVal.isNotEmpty()) parts.add("$alcoholVal мл алкоголя")
                    if (parts.isNotEmpty()) consumedText = parts.joinToString(", ")
                }
            }

            val record = DayRecord(
                id = recordIdToEdit,
                date = if (recordIdToEdit > 0) System.currentTimeMillis() else Calendar.getInstance().timeInMillis,
                cravingLevel = cravingLevel,
                consumedAmount = consumedText,
                amountSmoked = amountSmoked,
                amountDrunk = amountDrunk,
                moneySpent = moneySpent,
                wellBeing = wellBeing,
                mood = if (etMood.text.isNotEmpty()) etMood.text.toString() else "Не указано",
                sleepQuality = sleepQuality,
                physicalActivity = cbPhysicalActivity.isChecked,
                notes = etNotes.text.toString(),
                photoPath = currentPhotoPath
            )

            if (recordIdToEdit > 0) {
                viewModel.updateRecord(record)
                Toast.makeText(requireContext(), "Запись обновлена!", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.insertRecord(record)
                Toast.makeText(requireContext(), "Запись сохранена!", Toast.LENGTH_SHORT).show()
            }

            NavHostFragment.findNavController(this).popBackStack()
        }
    }

    private fun openCamera() {
        val photoFile = File(requireContext().cacheDir, "temp_photo_${System.currentTimeMillis()}.jpg")
        val photoUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            photoFile
        )
        currentPhotoUri = photoUri
        takePicture.launch(photoUri)
    }
}