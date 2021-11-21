package com.glushko.winfox_test_task.presentation_layer.ui.main_screen.menu_screen

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.glushko.winfox_test_task.R
import com.glushko.winfox_test_task.databinding.FragmentMenuBinding
import com.glushko.winfox_test_task.presentation_layer.ui.main_screen.menu_screen.adapter.AdapterMenu
import com.glushko.winfox_test_task.presentation_layer.vm.ViewModelMainScreen
class FragmentDialogMenu: DialogFragment() {

    companion object{
        const val TAG = "FragmentDialogMenu"
        private const val EXTRA_TITLE = "name place"
        private const val EXTRA_ID = "id place"

        fun getInstance(id:String ,title: String): FragmentDialogMenu{
            return FragmentDialogMenu().apply {
                arguments = bundleOf(EXTRA_TITLE to title, EXTRA_ID to id)
            }
        }
    }

    lateinit var binding: FragmentMenuBinding
    lateinit var model: ViewModelMainScreen

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(this).get(ViewModelMainScreen::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        var namePlace = ""
        var idPlace = ""
        arguments?.let {
            namePlace = it.getString(EXTRA_TITLE)?:""
            idPlace = it.getString(EXTRA_ID)?:""
        }
        dialog?.setTitle("${getString(R.string.menu_title)} $namePlace")
        model.getMenu(idPlace)
        binding = FragmentMenuBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recyclerMenu.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        model.liveDataMenu.observe(viewLifecycleOwner, Observer {
            binding.menuProgressBar.visibility = View.INVISIBLE
            if(it.isSuccess){
                binding.recyclerMenu.adapter = AdapterMenu(it.menu)
            }else{
                binding.menuTextErr.visibility = View.VISIBLE
            }
        })

    }


}