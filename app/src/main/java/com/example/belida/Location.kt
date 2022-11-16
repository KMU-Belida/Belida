package com.example.belida

import android.Manifest
import android.content.pm.PackageManager
import android.location.*
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.location.*

class Location : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.location)

        val locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0f,locationListener())

    }

    internal inner class locationListener : LocationListener{

        override fun onLocationChanged(p0: Location) {
            val geocoder : Geocoder = Geocoder(this@Location)
            var list = geocoder.getFromLocation(p0.latitude,p0.longitude,1) as List<Address>
            textLocation.text = "위도 : "+p0.latitude+"\n경도 : "+p0.longitude+"\n주소 : "+list[0].getAddressLine(0)

        }
    }

}
