package com.glushko.winfox_test_task.presentation_layer.ui.main_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.glushko.winfox_test_task.R
import com.glushko.winfox_test_task.databinding.FragmentMainScreenBinding
import com.glushko.winfox_test_task.presentation_layer.vm.ViewModelMainScreen
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainScreenFragment: Fragment() {

    private lateinit var model: ViewModelMainScreen
    private lateinit var binding: FragmentMainScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity()).get(ViewModelMainScreen::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainScreenBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.setActionBar(binding.inclToolbar.toolbar)
        binding.inclToolbar.toolbar.visibility = View.VISIBLE
        model.getPlaces()

        val bottomNavigate = view.findViewById<BottomNavigationView>(R.id.mainButtonNavigation)
        val navController =
            (childFragmentManager.findFragmentById(R.id.containerMainView) as NavHostFragment).navController
        NavigationUI.setupWithNavController(bottomNavigate, navController)

        model.liveDataPlace.observe(viewLifecycleOwner, Observer {
            binding.inclToolbar.toolbarProgressBar.visibility = View.INVISIBLE
        })
    }


}