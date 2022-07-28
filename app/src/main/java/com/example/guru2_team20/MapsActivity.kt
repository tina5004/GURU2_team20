package com.example.guru2_team20

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.example.guru2_team20.databinding.ActivityMapsBinding
import com.google.android.gms.maps.model.*

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Add a marker in Sydney and move the camera
        val seoul = LatLng(37.566,126.9779)

        //마커 아이콘 만들기
        // val descriptor = getDescriptorFromDrawable(R.drawable.marker)

        //마커
        val marker = MarkerOptions()
            .position(seoul)
            .title("Marker is Seoul")
            //.icon(descriptor)

        mMap.addMarker(marker)

        //카메라의 위치
        val cameraOption = CameraPosition.Builder()
            .target(seoul)
            .zoom(15f)
            .build()

        val camera = CameraUpdateFactory.newCameraPosition((cameraOption))

        mMap.moveCamera(camera)


    }

    fun getDescriptorFromDrawable(drawableID : Int) : BitmapDescriptor {
        var bitmapDrawable: BitmapDrawable
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            bitmapDrawable = getDrawable(drawableID) as BitmapDrawable
        }
        else{
            bitmapDrawable = resources.getDrawable(drawableID)as BitmapDrawable
        }

        //마커 크기변환
        val scaledBitmap= Bitmap.createScaledBitmap(bitmapDrawable.bitmap, 100,100,false)
        return BitmapDescriptorFactory.fromBitmap(scaledBitmap)
    }
}