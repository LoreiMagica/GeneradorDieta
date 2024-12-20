package com.lorei.generadorDieta.ui.fragment

import DietaViewModel
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat.recreate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.navOptions
import com.lorei.generadorDieta.R
import com.lorei.generadorDieta.databinding.OpcionesMainLayoutBinding
import java.util.*


class OpcionesMenuFragment : Fragment() {
    private lateinit var binding: OpcionesMainLayoutBinding
    private lateinit var viewModel: DietaViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OpcionesMainLayoutBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[DietaViewModel::class.java]

        //Se crean los arrays con los idiomas de la app y sus id
        val idiomas = requireContext().resources.getStringArray(R.array.spinner_idiomas)
        val idiomasCod = listOf("es", "en")

        val diasSemana = listOf(getString(R.string.dia_lunes), getString(R.string.dia_domingo))


        // Cargar el idioma guardado antes de setContentView
        val sharedPref = requireActivity().getSharedPreferences("Settings", Context.MODE_PRIVATE)
        val savedLanguage = sharedPref.getString("Language", "en")

        //Y también el día
        var savedPrimerDía = sharedPref.getInt("primerSemana", 0)

        // Desactivar el listener temporalmente
        binding.opcionSwitchTema.setOnCheckedChangeListener(null)

        //Comprobamos el tema actual del usuario
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        binding.opcionSwitchTema.isChecked = currentNightMode == Configuration.UI_MODE_NIGHT_YES


        //Si se pulsa el switch, cambia el tema
        binding.opcionSwitchTema.setOnCheckedChangeListener { _, isChecked ->
            val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK

            if (isChecked && currentNightMode != Configuration.UI_MODE_NIGHT_YES) {
                applyTheme(true, sharedPref)
            } else if (!isChecked && currentNightMode != Configuration.UI_MODE_NIGHT_NO) {
                applyTheme(false, sharedPref)
            }
            // Reiniciar la actividad para aplicar el cambio de tema
            requireActivity().recreate()
        }


        //Seteamos los idiomas al spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, idiomas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.opcionSpinnerIdioma.adapter = adapter

        // Establecer el idioma seleccionado previamente en el Spinner
        binding.opcionSpinnerIdioma.setSelection(idiomasCod.indexOf(savedLanguage))

        // Manejar el cambio de idioma
        binding.opcionSpinnerIdioma.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedLanguageCode = idiomasCod[position]
                if (selectedLanguageCode != savedLanguage) {
                    setLocaleWithPreference(selectedLanguageCode, sharedPref)
                    requireActivity().recreate()  // Reiniciar la actividad para aplicar el cambio
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada si no se selecciona nada
            }
        }

        //Seteamos los días  al spinner de días
        val adapterDias = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, diasSemana)
        adapterDias.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.opcionSpinnerPrimerDiaSemana.adapter = adapterDias

        // Establecer el día seleccionado previamente en el Spinner
        binding.opcionSpinnerPrimerDiaSemana.setSelection(savedPrimerDía)

        // Manejar el cambio de día
        binding.opcionSpinnerPrimerDiaSemana.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (position != savedPrimerDía) {
                    val editor = sharedPref.edit()
                    editor.putInt("primerSemana", position)
                    editor.apply()
                    savedPrimerDía = position
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // No hacer nada si no se selecciona nada
            }
        }

        binding.optionBackup.setOnClickListener {
            findNavController().navigate(R.id.nav_opciones_backup)
        }
        return binding.root
    }

    // Función para aplicar el idioma y guardar la preferencia
    private fun setLocaleWithPreference(languageCode: String, prefs: SharedPreferences) {
        //Se guardar el nuevo idioma en sharedpreferences
        val editor = prefs.edit()
        editor.putString("Language", languageCode)
        editor.apply()
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = resources.configuration
        config.setLocale(locale)
        resources.updateConfiguration(config, resources.displayMetrics)    }

    //Función para aplicar el tema
    private fun applyTheme(isDarkMode: Boolean, sharedPref: SharedPreferences) {
        //Se guarda la preferencia de tema en sharedpreferences
        val editor = sharedPref.edit()
        editor.putBoolean("isDarkMode", isDarkMode)
        editor.apply()

        // Aplicar el nuevo modo solo si es diferente del actual
        AppCompatDelegate.setDefaultNightMode(
            if (isDarkMode) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )

        // Recrear la actividad para aplicar el nuevo tema
        requireActivity().recreate()
    }
}