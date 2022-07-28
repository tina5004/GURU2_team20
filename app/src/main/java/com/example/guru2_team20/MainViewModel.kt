package com.example.guru2_team20

import android.content.Context
import android.location.Location
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.guru2_team20.data.model.Store
import com.example.guru2_team20.data.repository.StoreRepository
import com.google.android.gms.maps.model.LatLng


class MainViewModel(appContext: Context) : ViewModel() {

    private val repository by lazy { StoreRepository.getInstance(appContext) }
    val myLocation: MutableLiveData<Location> = MutableLiveData(null)


    suspend fun getAddress(latLng: LatLng) = repository.getAddress(latLng)

    fun addStore(latLng: LatLng, name: String, type: Store.Type, size: Store.Size) =
        repository.addStore(latLng, name, type, size)

    suspend fun getStores(center: LatLng) = repository.getStores(center, 2000)
}