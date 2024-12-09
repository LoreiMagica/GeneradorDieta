package com.lorei.generadorDieta.ui.fragment

import DietaViewModel

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.lorei.generadorDieta.databinding.OpcionesMainLayoutBinding
import java.util.*


class OpcionesCopiaSeguridadMenuFragment : Fragment() {
    private lateinit var binding: OpcionesMainLayoutBinding
    private lateinit var viewModel: DietaViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = OpcionesMainLayoutBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(this)[DietaViewModel::class.java]







        return binding.root
    }


}