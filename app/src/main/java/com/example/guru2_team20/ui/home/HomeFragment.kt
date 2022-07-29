package com.example.guru2_team20.ui.home

import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.Rect
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
import androidx.recyclerview.widget.*
import com.example.guru2_team20.MainViewModel
import com.example.guru2_team20.R
import com.example.guru2_team20.data.model.Store
import com.example.guru2_team20.databinding.FragmentHomeBinding
import com.example.guru2_team20.databinding.ItemStoreBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch


class HomeFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var _map: GoogleMap? = null
    private val map get() = _map!!

    private var markers: List<Marker?>? = null
    private var selectedMarker: Marker? = null

    private lateinit var viewModel: MainViewModel

    private val adapter by lazy { StoreAdapter() }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root


    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        requireActivity().supportFragmentManager.setFragmentResultListener("Refresh", this) { _, _ ->
            binding.scanButton.performClick()
        }
    }

    /**
     * Map 의 카메라를 업데이트 한다.
     * @param latLng 위경도
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
     * 주소를 업데이트 한다.
     * @param latLng 위경도
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
     * 가게 정보를 업데이트 한다.
     * @param latLng 위경도
     */
    private suspend fun updateStores(latLng: LatLng) {
        if (_map == null) return

        val stores = viewModel.getStores(latLng)

        map.clear()
        selectedMarker = null
        adapter.selectedIndex = -1

        markers = stores.map {
            val options = MarkerOptions()
                .position(LatLng(it.lat, it.lng))
                // .title(it.name)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_32))

            map.addMarker(options)
        }

        adapter.submitList(stores) {
            if (stores.isNotEmpty()) {
                binding.recyclerView.scrollToPosition(0)
                selectStore(0)
                selectedMarker?.let { updateCamera(it.position) }
            }
        }

        binding.emptyView.root.isVisible = stores.isEmpty()
    }

    //UI 초기화

    private fun initUi() = with(binding) {
        scanButton.setOnClickListener {
            if (_map == null) return@setOnClickListener
            if (!it.isVisible) return@setOnClickListener

            lifecycleScope.launch {
                it.isVisible = false

                val latLng = map.cameraPosition.target
                listOf(
                    async { updateAddress(latLng) },
                    async { updateStores(latLng) }
                ).awaitAll()

                it.isVisible = true
            }
        }

        myLocationButton.setOnClickListener {
            val location = viewModel.myLocation.value ?: return@setOnClickListener
            val latLng = LatLng(location.latitude, location.longitude)

            lifecycleScope.launch {
                updateCamera(latLng)
                async { updateAddress(latLng) }
                async { updateStores(latLng) }
            }
        }

        recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = this@HomeFragment.adapter
            addItemDecoration(MarginItemDecoration((8 * resources.displayMetrics.density).toInt()))

            val snapHelper = LinearSnapHelper()
            snapHelper.attachToRecyclerView(this)

            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val layoutManager = recyclerView.layoutManager ?: return

                    val view = snapHelper.findSnapView(layoutManager) ?: return
                    val position = layoutManager.getPosition(view)

                    selectStore(position)
                    selectedMarker?.let { updateCamera(it.position) }
                }
            })
        }

        map.setOnMarkerClickListener {
            val index = markers?.indexOf(it)
            if (index != null && index >= 0) {
                binding.recyclerView.scrollToPosition(index)
                binding.recyclerView.smoothScrollBy(1, 0)
                selectStore(index)
            }

            return@setOnMarkerClickListener false
        }
    }

    //가게를 선택하는 함수 (마커 선택, RecyclerView 의 위치 설정)

    private fun selectStore(index: Int) {
        if (selectedMarker != null && selectedMarker == markers?.getOrNull(index)) return

        selectedMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_32))

        selectedMarker = markers?.getOrNull(index)
        selectedMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_40))

        adapter.selectedIndex = index
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
                    LatLng(37.617603, 127.074835)
                } else {
                    LatLng(location.latitude, location.longitude)
                }

                lifecycleScope.launch {
                    updateCamera(latLng)
                    async { updateAddress(latLng) }
                    async { updateStores(latLng) }
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


    private class StoreAdapter : ListAdapter<Store, StoreAdapter.StoreItemViewHolder>(
        object : DiffUtil.ItemCallback<Store>() {
            override fun areItemsTheSame(oldItem: Store, newItem: Store): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Store, newItem: Store): Boolean {
                return oldItem.lat == newItem.lat &&
                        oldItem.lng == newItem.lng &&
                        oldItem.name == newItem.name &&
                        oldItem.type == newItem.type &&
                        oldItem.size == newItem.size
            }

            override fun getChangePayload(oldItem: Store, newItem: Store): Any = Any()
        }
    ) {
        var selectedIndex: Int = -1
            set(value) {
                if (field != -1 && field == value) return

                val oldValue = field
                field = value

                if (oldValue != -1) {
                    notifyItemChanged(oldValue, Any())
                }

                if (value != -1) {
                    notifyItemChanged(value, Any())
                }
            }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StoreItemViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            val binding = ItemStoreBinding.inflate(inflater, parent, false)
            return StoreItemViewHolder(binding)
        }

        override fun onBindViewHolder(holder: StoreItemViewHolder, position: Int) {
            val item = getItem(position)

            with(holder.binding) {
                nameTextView.text = item.name

                var tag = ""
                val type = Store.Type.valueOf(item.type)
                val size = Store.Size.valueOf(item.size)

                if (type == Store.Type.CAFE) {
                    tag += "#카페 "
                } else if (type == Store.Type.RESTAURANT) {
                    tag += "#음식점 "
                }

                if (size == Store.Size.BOTH) {
                    tag += "#대형견 #소형견"
                } else if (size == Store.Size.BIG) {
                    tag += "#대형견"
                } else {
                    tag += "#소형견"
                }

                tagTextView.text = tag.trim()

                if (selectedIndex == position) {
                    nameTextView.setTextColor(Color.WHITE)
                    tagTextView.setTextColor(Color.WHITE)

                    contentContainer.setCardBackgroundColor(Color.parseColor("#121212"))

                } else {
                    nameTextView.setTextColor(Color.BLACK)
                    tagTextView.setTextColor(Color.BLACK)

                    contentContainer.setCardBackgroundColor(Color.WHITE)
                }
            }
        }

        class StoreItemViewHolder(val binding: ItemStoreBinding) : RecyclerView.ViewHolder(binding.root)
    }


    private class MarginItemDecoration(private val spaceSize: Int) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect, view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            with(outRect) {
                left = spaceSize
                right = spaceSize
            }
        }
    }
}