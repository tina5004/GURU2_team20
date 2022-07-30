package com.example.guru2_team20.ui.report

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.guru2_team20.MainViewModel
import com.example.guru2_team20.R
import com.example.guru2_team20.databinding.FragmentReportMapBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class ReportMapFragment : Fragment(), OnMapReadyCallback {

    companion object {
        const val TAG = "ReportMapFragment"
    }

    private var _binding: FragmentReportMapBinding? = null
    private val binding get() = _binding!!

    private var _map: GoogleMap? = null
    private val map get() = _map!!

    private lateinit var viewModel: MainViewModel


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentReportMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /**
      Map의 카메라 업데이트
      @param latLng 위경도
     */
    private fun updateCamera(latLng: LatLng) {
        if (_map == null) return

        //카메라의 위치
        val cameraOption = CameraPosition.Builder()
            .target(latLng)
            .zoom(15f)
            .build()

        val camera = CameraUpdateFactory.newCameraPosition((cameraOption))

        map.moveCamera(camera)
    }

    /**
      주소 업데이트
      @param latLng 위경도
     */
    private suspend fun updateAddress(latLng: LatLng) {
        val address = viewModel.getAddress(latLng)

        if (address == null) {
            binding.addressTextView.text = "알 수 없는 위치"
        } else {
            binding.addressTextView.text = address.getAddressLine(0).removePrefix(address.countryName).trim()
        }
    }

    /**
      마커 업데이트
      @param latLng 위경도
     */
    private suspend fun updateMarkers(latLng: LatLng) {
        if (_map == null) return

        map.clear()

        val stores = viewModel.getStores(latLng)
        stores.forEach {
            val marker = MarkerOptions()
                .position(LatLng(it.lat, it.lng))
                .title(it.name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_24))

            map.addMarker(marker)
        }
    }

    //UI 초기화

    private fun initUi() = with(binding) {
        toolbar.setNavigationOnClickListener { requireActivity().onBackPressed() }

        myLocationButton.setOnClickListener {
            val location = viewModel.myLocation.value ?: return@setOnClickListener
            val latLng = LatLng(location.latitude, location.longitude)

            lifecycleScope.launch {
                updateCamera(latLng)
                async { updateAddress(latLng) }
                async { updateMarkers(latLng) }
            }
        }

        map.setOnCameraIdleListener {
            val latLng = map.cameraPosition.target

            lifecycleScope.launch {
                async { updateAddress(latLng) }
                async { updateMarkers(latLng) }
            }
        }

        doneButton.setOnClickListener {
            if (addressTextView.text.isBlank()) return@setOnClickListener
            if (_map == null) return@setOnClickListener

            if (requireActivity().supportFragmentManager.findFragmentByTag(ReportFragment.TAG) == null) {
                val fragment = ReportFragment.newInstance(map.cameraPosition.target, addressTextView.text.toString())

                requireActivity().supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container_view, fragment, ReportFragment.TAG)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    //Google map ready callback

    override fun onMapReady(map: GoogleMap) {
        _map = map
        map.uiSettings.isMyLocationButtonEnabled = false

        initUi()

        viewModel.myLocation.observe(viewLifecycleOwner, object : Observer<Location> {
            @SuppressLint("MissingPermission")
            override fun onChanged(location: Location?) {
                val latLng: LatLng = if (location == null) {
                    LatLng(37.617603, 127.074835) //태릉입구로 기본 위치 설정
                } else {
                    LatLng(location.latitude, location.longitude)
                }

                lifecycleScope.launch {
                    updateCamera(latLng)
                    async { updateAddress(latLng) }
                    async { updateMarkers(latLng) }
                }

                if (location != null) {
                    try {
                        map.isMyLocationEnabled = true
                        binding.myLocationButton.isVisible = true
                    } catch (ignore: Exception) {
                    }

                    viewModel.myLocation.removeObserver(this)
                }
            }
        })
    }
}