package com.lorei.generadorDieta.ui.fragment

import DietaViewModel
import android.content.Context
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lorei.generadorDieta.R
import com.lorei.generadorDieta.databinding.DietaLayoutBinding
import java.util.*


class DietaFragment : Fragment() {
    private lateinit var binding: DietaLayoutBinding
    private lateinit var viewModel: DietaViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DietaLayoutBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[DietaViewModel::class.java]


        //Seteamos los números en el calendario
        //Obtenemos la fecha de hoy
        val cal = Calendar.getInstance()
        //Sacamos el día de la fecha de hoy
        var dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        //Lo seteamos a hoy
        binding.diaLunes.text = dayOfMonth.toString()

        //Y repetimos con cada día
        cal.add(Calendar.DAY_OF_YEAR, 1)
        dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.diaMartes.text = (dayOfMonth).toString()

        cal.add(Calendar.DAY_OF_YEAR, 1)
        dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.diaMiercoles.text = (dayOfMonth).toString()

        cal.add(Calendar.DAY_OF_YEAR, 1)
        dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.diaJueves.text = (dayOfMonth).toString()

        cal.add(Calendar.DAY_OF_YEAR, 1)
        dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.diaViernes.text = (dayOfMonth).toString()

        cal.add(Calendar.DAY_OF_YEAR, 1)
        dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.diaSabado.text = (dayOfMonth).toString()

        cal.add(Calendar.DAY_OF_YEAR, 1)
        dayOfMonth = cal[Calendar.DAY_OF_MONTH]
        binding.diaDomingo.text = (dayOfMonth).toString()


        //Le damos función a los botones de días
        //Creamos un array de días en español
        val diasSemana = listOf("", getString(R.string.dia_lunes), getString(R.string.dia_martes), getString(R.string.dia_miercoles), getString(R.string.dia_jueves), getString(R.string.dia_viernes), getString(R.string.dia_sabado), getString(R.string.dia_domingo))

        //Llevamos el día a hoy
        val calHoy = Calendar.getInstance()
        calHoy.add(Calendar.DAY_OF_YEAR, -1)

        //Seteamos el texto del día de hoy, y el color a activo
        binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
        val colorPrimary = getColorFromAttr(androidx.appcompat.R.attr.colorPrimary)

        binding.fondoLunes.setBackgroundColor(colorPrimary)

        //Y repetimos con cada día
        binding.diaLunes.setOnClickListener {

            binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
            binding.fondoLunes.setBackgroundColor(colorPrimary)
            binding.fondoMartes.setBackgroundColor(0)
            binding.fondoMiercoles.setBackgroundColor(0)
            binding.fondoJueves.setBackgroundColor(0)
            binding.fondoViernes.setBackgroundColor(0)
            binding.fondoSabado.setBackgroundColor(0)
            binding.fondoDomingo.setBackgroundColor(0)
        }
        binding.diaMartes.setOnClickListener {
            val calHoy = Calendar.getInstance()
            binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
            binding.fondoLunes.setBackgroundColor(0)
            binding.fondoMartes.setBackgroundColor(colorPrimary)
            binding.fondoMiercoles.setBackgroundColor(0)
            binding.fondoJueves.setBackgroundColor(0)
            binding.fondoViernes.setBackgroundColor(0)
            binding.fondoSabado.setBackgroundColor(0)
            binding.fondoDomingo.setBackgroundColor(0)
        }
        binding.diaMiercoles.setOnClickListener {
            val calHoy = Calendar.getInstance()
            calHoy.add(Calendar.DAY_OF_YEAR, 1)
            binding.diaHoy.text = diasSemana[ calHoy.get(Calendar.DAY_OF_WEEK)]
            binding.fondoLunes.setBackgroundColor(0)
            binding.fondoMartes.setBackgroundColor(0)
            binding.fondoMiercoles.setBackgroundColor(colorPrimary)
            binding.fondoJueves.setBackgroundColor(0)
            binding.fondoViernes.setBackgroundColor(0)
            binding.fondoSabado.setBackgroundColor(0)
            binding.fondoDomingo.setBackgroundColor(0)
        }
        binding.diaJueves.setOnClickListener {
            val calHoy = Calendar.getInstance()
            calHoy.add(Calendar.DAY_OF_YEAR, 2)
            binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
            binding.fondoLunes.setBackgroundColor(0)
            binding.fondoMartes.setBackgroundColor(0)
            binding.fondoMiercoles.setBackgroundColor(0)
            binding.fondoJueves.setBackgroundColor(colorPrimary)
            binding.fondoViernes.setBackgroundColor(0)
            binding.fondoSabado.setBackgroundColor(0)
            binding.fondoDomingo.setBackgroundColor(0)
        }
        binding.diaViernes.setOnClickListener {
            val calHoy = Calendar.getInstance()
            calHoy.add(Calendar.DAY_OF_YEAR, 3)
            binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
            binding.fondoLunes.setBackgroundColor(0)
            binding.fondoMartes.setBackgroundColor(0)
            binding.fondoMiercoles.setBackgroundColor(0)
            binding.fondoJueves.setBackgroundColor(0)
            binding.fondoViernes.setBackgroundColor(colorPrimary)
            binding.fondoSabado.setBackgroundColor(0)
            binding.fondoDomingo.setBackgroundColor(0)
        }
        binding.diaSabado.setOnClickListener {
            val calHoy = Calendar.getInstance()
            calHoy.add(Calendar.DAY_OF_YEAR, 4)
            binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
            binding.fondoLunes.setBackgroundColor(0)
            binding.fondoMartes.setBackgroundColor(0)
            binding.fondoMiercoles.setBackgroundColor(0)
            binding.fondoJueves.setBackgroundColor(0)
            binding.fondoViernes.setBackgroundColor(0)
            binding.fondoSabado.setBackgroundColor(colorPrimary)
            binding.fondoDomingo.setBackgroundColor(0)
        }
        binding.diaDomingo.setOnClickListener {
            val calHoy = Calendar.getInstance()
            calHoy.add(Calendar.DAY_OF_YEAR, 5)
            binding.diaHoy.text = diasSemana[calHoy.get(Calendar.DAY_OF_WEEK)]
            binding.fondoLunes.setBackgroundColor(0)
            binding.fondoMartes.setBackgroundColor(0)
            binding.fondoMiercoles.setBackgroundColor(0)
            binding.fondoJueves.setBackgroundColor(0)
            binding.fondoViernes.setBackgroundColor(0)
            binding.fondoSabado.setBackgroundColor(0)
            binding.fondoDomingo.setBackgroundColor(colorPrimary)
        }

        binding.btGenerarDieta.setOnClickListener {
            viewModel.mostrarToast.observe(viewLifecycleOwner) { messageResId ->
                Toast.makeText(requireContext(), getString(messageResId), Toast.LENGTH_SHORT).show()
            }
            //Cargamos la base de datos para obtener las recetas
            val baseGuardado = requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null)

            //Llamamos al método para obtener estas
            viewModel.generarDieta(baseGuardado)
        }



        return binding.root
    }
    /**
     * Método para obtener un color dinámico desde un atributo del tema.
     */
    private fun getColorFromAttr(attr: Int): Int {
        val typedValue = TypedValue()
        context!!.theme.resolveAttribute(attr, typedValue, true)
        return if (typedValue.resourceId != 0) {
            ContextCompat.getColor(context!!, typedValue.resourceId)
        } else {
            typedValue.data
        }
    }

    fun  mostrarToast(message : Int) {
        Toast.makeText(context, getString(message), Toast.LENGTH_SHORT)
            .show()
    }
}