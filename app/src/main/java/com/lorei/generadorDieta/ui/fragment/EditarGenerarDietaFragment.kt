package com.lorei.generadorDieta.ui.fragment

import EditarGenerarDietaViewModel
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.gson.Gson
import com.lorei.generadorDieta.R
import com.lorei.generadorDieta.databinding.EditarGenerarDietaLayoutBinding


class EditarGenerarDietaFragment : Fragment() {
    private lateinit var binding: EditarGenerarDietaLayoutBinding
    private lateinit var viewModel: EditarGenerarDietaViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = EditarGenerarDietaLayoutBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[EditarGenerarDietaViewModel::class.java]
        viewModel.cargarRecetas(requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null))

        //Creamos todas las variables y arrays necesarios para operar
        val tipo = resources.getStringArray(R.array.dieta_tipo)

        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE)
        var dieta = sharedPref!!.getInt("dieta", 0)

        val nombresArray = viewModel.data.map { it.nombre.toString() }.toTypedArray()
        val idsArray = viewModel.data.map { it.id!!.toInt() }.toTypedArray()
        val selectedItems = mutableMapOf<Int, String>()
        var  nombreSeleccionado = ""
        var idSeleccionado = 0

        //Llenamos la lista de comidas, con las comidas del usuario
        val adapterFav = ArrayAdapter(requireActivity(), android.R.layout.simple_dropdown_item_1line, nombresArray)
        binding.etFav.setAdapter(adapterFav)

        //Y cargamos el listado de dietas
        val adapter = ArrayAdapter<String>(requireActivity(), android.R.layout.simple_dropdown_item_1line, tipo)
        binding.listaDietas.setAdapter(adapter)
        //Seteamos la posición guardada de la dieta
        binding.listaDietas.setSelection(dieta)

        //Seteamos el listener del cambio de posición
        binding.listaDietas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View,
                position: Int,
                id: Long
            ) {
                //Cuando se elija dieta, actualizamos su variable
                dieta = position
            }

            override fun onNothingSelected(parent: AdapterView<*>) {

            }
        }
        //Y cuando elijamos una comida, igual
        binding.etFav.setOnItemClickListener { parent, _, position, _ ->
            nombreSeleccionado = parent.getItemAtPosition(position) as String
            idSeleccionado = viewModel.data.firstOrNull { it.nombre == nombreSeleccionado }?.id!!
        }

        //Al pulsar el botón, comprueba si existe y lo agrega o no
        binding.botonAgregarFav.setOnClickListener {

            val inputText = binding.etFav.text.toString().trim()

            if (inputText.isNotEmpty()) {
                // Agregar el chip solo si no existe ya, tanto al array como al chipgroup
                if (!isChipAlreadyAdded(inputText, binding.listaFav)) {
                    selectedItems[idSeleccionado] = nombreSeleccionado
                    addChipToGroup(inputText, binding.listaFav, selectedItems, idSeleccionado)

                } else {
                    //En caso de ya existir, se avisa al usuario
                    Toast.makeText(activity, (getString(R.string.avisoRepe) + " " + inputText), Toast.LENGTH_SHORT)
                        .show()
                }
                    binding.etFav.text?.clear()
                }
            }

        //Damos función al botón de agregar configuración
        binding.btAgregar.setOnClickListener {
            //Llamamos a la función y le pasamos la base de datos
            agregarReceta(requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null), dieta, selectedItems)

            }

        return binding.root
    }

    //Función para comprobar si existe esa categoría
private fun isChipAlreadyAdded(text: String, chipGroup: ChipGroup): Boolean {
    for (i in 0 until chipGroup.childCount) {
        val chip = chipGroup.getChildAt(i) as Chip
        if (chip.text == text) {
            return true
        }
    }
    return false
}
    //Función para añadir categorías
    private fun addChipToGroup(text: String, chipGroup: ChipGroup, selectedItems: MutableMap<Int, String>, idSeleccionado : Int) {
        val chip = Chip(activity)
        chip.text = text
        chip.isCloseIconVisible = true // Permite eliminar el chip
        chip.setOnCloseIconClickListener {
            chipGroup.removeView(chip) // Eliminar el chip
            selectedItems.remove(idSeleccionado) // Eliminar del mapa
        }
        chipGroup.addView(chip)
    }

    //Esta función agrega finalmente la receta
    private fun agregarReceta(db : SQLiteDatabase, dieta:Int, selectedItems: MutableMap<Int, String>) {


        //Guardamos la dieta en sharedpreferences hasta que lo cambie el usuario
        val sharedPref = activity?.getPreferences(Context.MODE_PRIVATE) ?: return
        with (sharedPref.edit()) {
            putInt("dieta", dieta)
            apply()
        }


        //Y las comidas en la base de datos, en su propia tabla
        val baseGuardado = db

        baseGuardado.execSQL("DROP TABLE if exists dietaFavs ")

        //Creamos la tabla en caso de no existir
        baseGuardado.execSQL("CREATE TABLE if not exists dietaFavs (numero integer primary key AUTOINCREMENT, comida VARCHAR(500))")

        //CDreamos la variable donde almacenaremos la receta a agregar
        val registro = ContentValues()

        //Comprobamos qué comidas han sido agregadas para guardar el registro
        for (item in selectedItems.keys) {
            for (i in viewModel.data){
                if (i.id == (item)) {
                    //Almacenamos todas las recetas de las comidas para guardarlas juntas
                    val gson = Gson()
                    val json = gson.toJson(i)
                    registro.put("comida", json)
                    baseGuardado.insert("dietaFavs", null, registro)
                }
            }
        }

        //y cerramos la conexión
        baseGuardado.close()
        findNavController().navigate(R.id.nav_dieta)

    }
}