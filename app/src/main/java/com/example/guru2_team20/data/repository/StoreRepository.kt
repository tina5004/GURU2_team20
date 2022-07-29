package com.example.guru2_team20.data.repository

import android.content.Context
import android.location.Geocoder
import android.widget.Button
import com.example.guru2_team20.data.model.Store
import com.firebase.geofire.GeoFireUtils
import com.firebase.geofire.GeoLocation
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.math.max


class StoreRepository private constructor(appContext: Context) {



    companion object {
        private var instance: StoreRepository? = null

        fun getInstance(appContext: Context): StoreRepository {
            if (instance == null) {
                instance = StoreRepository(appContext)
            }

            return instance!!
        }
    }


    private val geocoder by lazy { Geocoder(appContext, Locale.KOREAN) }
    private val auth by lazy { Firebase.auth }
    private val db by lazy { Firebase.firestore }


    /**
      주소를 반환하는 함수
      @param latLng 위경도
      @return Address 객체 또는 null
     */
    suspend fun getAddress(latLng: LatLng) = withContext(Dispatchers.IO) {
        try {
            val list = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 10)
            return@withContext list.firstOrNull()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return@withContext null
    }

    /**
      중앙 지점으로부터 반경 n 미터 이내의 가게 정보를 가져오는 함수
      @param center 중앙 지점
      @param radius 반경 n 미터
     */
    suspend fun getStores(center: LatLng, radius: Int) = withContext(Dispatchers.IO) {
        val center = GeoLocation(center.latitude, center.longitude)
        val radius = max(radius, 2000).toDouble()

        val bounds = GeoFireUtils.getGeoHashQueryBounds(center, radius)
        val tasks = arrayListOf<Task<QuerySnapshot>>()
        bounds.forEach {
            val query = db.collection("stores")
                .orderBy("geohash")
                .startAfter(it.startHash)
                .endAt(it.endHash)

            tasks.add(query.get())
        }

        Tasks.await(Tasks.whenAllComplete(tasks))

        val results = arrayListOf<Store>()

        for (task in tasks) {
            val snapshot = task.result ?: continue

            for (doc in snapshot.documents) {
                val lat = doc.getDouble("lat") ?: continue
                val lng = doc.getDouble("lng") ?: continue

                val location = GeoLocation(lat, lng)
                val distance = GeoFireUtils.getDistanceBetween(location, center)

                if (distance <= radius) {
                    doc.toObject(Store::class.java)?.let { results.add(it) }
                }
            }
        }

        results.sortBy { GeoFireUtils.getDistanceBetween(GeoLocation(it.lat, it.lng), center) }
        return@withContext results
    }

    //가게를 DB 에 추가하는 함수

    fun addStore(latLng: LatLng, name: String, type: Store.Type, size: Store.Size) {
        val lat = latLng.latitude
        val lng = latLng.longitude

        val store = Store(
            "",
            lat,
            lng,
            GeoFireUtils.getGeoHashForLocation(GeoLocation(lat, lng)),
            name,
            type.name,
            size.name,
            auth.uid!!
        )

        db.collection("stores").document().set(store)
    }

}