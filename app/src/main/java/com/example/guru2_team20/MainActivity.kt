package com.example.guru2_team20

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.guru2_team20.databinding.ActivityMainBinding
import com.example.guru2_team20.ui.home.HomeFragment
import com.example.guru2_team20.ui.report.ReportMapFragment
import com.google.android.gms.location.*
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY

class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private lateinit var viewModel: MainViewModel

    //region 현재 위치 관련 변수
    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(this)
    }

    private val locationRequest by lazy {
        LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = PRIORITY_HIGH_ACCURACY
        }
    }

    private val locationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                viewModel.myLocation.value = locationResult.locations.lastOrNull()
            }
        }
    }

    private var requestingLocationUpdates = false

    private val permissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
        if (it.values.none { it == false }) {
            startLocationUpdates()
        }
    }
    //endregion


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this, MainViewModelFactory(applicationContext))
            .get(MainViewModel::class.java)

        initUi()

        if (!checkLocationPermissions()) {
            permissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        }
    }

    /**
     * UI 초기화 함수
     */
    private fun initUi() = with(binding) {
        viewPager.apply {
            adapter = ViewPagerAdapter(this@MainActivity)
            isUserInputEnabled = false
        }

        bottomNavigation.setOnItemSelectedListener {
            val itemId = it.itemId

            if (itemId == R.id.tab2) {
                if (supportFragmentManager.findFragmentByTag(ReportMapFragment.TAG) == null) {
                    supportFragmentManager.beginTransaction()
                        .add(R.id.fragment_container_view, ReportMapFragment(), ReportMapFragment.TAG)
                        .addToBackStack("Home")
                        .commit()
                }
            }

            return@setOnItemSelectedListener false
        }
    }

    /**
     * 위치 권한 부여 여부를 검사하는 함수
     * @return true: 권한 있음, false: 권한 없음
     */
    private fun checkLocationPermissions(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    /**
     * 현재 위치 업데이트를 시작하는 함수
     */
    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        if (checkLocationPermissions() && !requestingLocationUpdates) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper())
            requestingLocationUpdates = true
        }
    }

    /**
     * 현재 위치 업데이트를 종료하는 함수
     */
    private fun stopLocationUpdates() {
        if (requestingLocationUpdates) {
            fusedLocationClient.removeLocationUpdates(locationCallback)
            requestingLocationUpdates = false
        }
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }


    private class ViewPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
        override fun getItemCount() = 1

        override fun createFragment(position: Int): Fragment {
            return HomeFragment()
        }
    }
}