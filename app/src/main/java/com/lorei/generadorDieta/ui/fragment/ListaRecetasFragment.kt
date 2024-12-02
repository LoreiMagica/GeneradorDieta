package com.lorei.generadorDieta.ui.fragment

import ListaRecetasViewModel
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.lorei.generadorDieta.R
import com.lorei.generadorDieta.databinding.ListarecetasLayoutBinding
import com.lorei.generadorDieta.ui.adapter.ListaRecetasAdapter
import java.util.*


class ListaRecetasFragment : Fragment() {
    private lateinit var binding: ListarecetasLayoutBinding
    private lateinit var viewModel: ListaRecetasViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = ListarecetasLayoutBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[ListaRecetasViewModel::class.java]


        //Cargamos la base de datos para obtener las recetas
        val baseGuardado = requireActivity().openOrCreateDatabase("baseGuardado.db", Context.MODE_PRIVATE, null)

        //Llamamos al método para obtener estas
        viewModel.cargarRecetas(baseGuardado)

        // Se inicializa el listado de enlaces
        binding.listaRecetas.apply {
            adapter = viewModel.adapter
            layoutManager = GridLayoutManager(requireContext(), GridLayoutManager.VERTICAL)
        }

        //Seteamos la función del botón agregar receta
        binding.agregarReceta.setOnClickListener { findNavController().navigate(R.id.nav_agregar_receta) }

        //Y la de los detalles de la receta
        viewModel.adapter!!.setOnClickListener(object : ListaRecetasAdapter.OnClickListener {
            override fun onItemClick(id: Int) {

                //Pasamos el id de receta para poder obtener los detalles en la siguiente pantalla
                val bundle = Bundle()
                bundle.putInt("idReceta", id)
                //Y con esto pasamos a la siguiente pantalla
                findNavController().navigate(R.id.nav_detalle_receta, bundle)
            }
        } )

        //Le damos funcióna los filtros de nombre y categoría
        binding.buscarCategoria.onItemSelectedListener = object :
        AdapterView.OnItemSelectedListener{
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                viewModel.filterCategoria(binding.buscarCategoria.getItemAtPosition(p2).toString())
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
                TODO("Not yet implemented")
            }
        }

        binding.etReceta.addTextChangedListener {
            viewModel.filterNombre(binding.etReceta.text.toString())
        }
        return binding.root
    }
}