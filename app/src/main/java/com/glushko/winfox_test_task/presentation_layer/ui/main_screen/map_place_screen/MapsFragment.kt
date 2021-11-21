package com.glushko.winfox_test_task.presentation_layer.ui.main_screen.map_place_screen

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.glushko.winfox_test_task.R
import com.glushko.winfox_test_task.business_logic_layer.domain.Menu
import com.glushko.winfox_test_task.business_logic_layer.domain.Place
import com.glushko.winfox_test_task.presentation_layer.ui.main_screen.menu_screen.FragmentDialogMenu
import com.glushko.winfox_test_task.presentation_layer.vm.ViewModelMainScreen

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class MapsFragment : Fragment() {

    private lateinit var model: ViewModelMainScreen

    private val markers: MutableList<Marker> = mutableListOf()

    private val callback = OnMapReadyCallback { googleMap ->
        model.liveDataPlace.observe(viewLifecycleOwner, Observer {
            if(it.isSuccess){
                var lastPlace: LatLng? = null
                it.places.forEach {place ->
                    val placeForMap = LatLng(place.latitide, place.longitude)
                    val marker = googleMap.addMarker(MarkerOptions().position(placeForMap).title(place.name))
                    marker.tag = place
                    markers.add(marker)
                    lastPlace = placeForMap
                }
                lastPlace?.let {
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lastPlace!!, 5F))
                }
            }
        })
        googleMap.setOnInfoWindowClickListener(callbackClickInfoWindowMarker)

    }
    private val callbackClickInfoWindowMarker = GoogleMap.OnInfoWindowClickListener{
        for(marker in markers){
            if(marker.id == it.id){
                val place = marker.tag as? Place
                println("${place?.name?:""} = ${it.title}")
                place?.let{
                    model.getMenu(place.id)
                    val dialogMenu = FragmentDialogMenu.getInstance(place.name)
                    dialogMenu.show(childFragmentManager.beginTransaction(), FragmentDialogMenu.TAG)
                }

            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        model = ViewModelProvider(requireActivity()).get(ViewModelMainScreen::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}