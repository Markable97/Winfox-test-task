package com.glushko.winfox_test_task.presentation_layer.ui.main_screen.list_places_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.glushko.winfox_test_task.databinding.FragmentListPlacesBinding
import com.glushko.winfox_test_task.presentation_layer.ui.main_screen.list_places_screen.adapter.AdapterPlaces
import com.glushko.winfox_test_task.presentation_layer.vm.ViewModelMainScreen

class ListPlacesFragment: Fragment() {

    private lateinit var binding: FragmentListPlacesBinding
    private lateinit var model: ViewModelMainScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity()).get(ViewModelMainScreen::class.java)

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentListPlacesBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model.liveDataPlace.observe(viewLifecycleOwner, Observer {
            println(it)
            val adapter = AdapterPlaces(it.places)
            binding.recyclerPlaces.layoutManager = GridLayoutManager(requireActivity(), 4)
            binding.recyclerPlaces.adapter = adapter
        })
    }


}