package com.example.mapas281124

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.commit
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private val LOCATION_CODE=1000
    private val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permisos ->
        if (
            permisos[Manifest.permission.ACCESS_FINE_LOCATION] == true

            ||
            permisos[Manifest.permission.ACCESS_COARSE_LOCATION] == true
            ){
            gestionarLocalizacion()
        }else{
            Toast.makeText(this,"El usuario denegó los permisos de localizacion",Toast.LENGTH_SHORT).show()
        }
    }
    private lateinit var mapa: GoogleMap
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        iniciarFragment()
    }

    private fun iniciarFragment() {
        val fragment = SupportMapFragment()
        fragment.getMapAsync(this)
        supportFragmentManager.commit {
            setReorderingAllowed(true)
            add(R.id.fm_mapa, fragment)
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        mapa = p0//llamar al mapa
        mapa.uiSettings.isZoomControlsEnabled = true//activar controles de zoom
        mapa.uiSettings.isZoomGesturesEnabled = true//activar gestos de zoom
       // mapa.mapType = GoogleMap.MAP_TYPE_SATELLITE //cambiar tipo de mapa a mapa vista satelite
        ponerMarcador(LatLng(-68.8787, 99.3810))
        gestionarLocalizacion()
        //------
        ponerRuta()
    }

    private fun ponerRuta() {
        val coordenada1=LatLng(36.85072552692251, -2.465253404260371)
        val coordenada2=LatLng(36.84865216760904, -2.461868456501225)
        val coordenada3=LatLng(36.849261732488564, -2.460688284517258)
        val coordenada4=LatLng(36.8499056337729, -2.461814812320136)
        val coordenada5=LatLng(36.850617609260766, -2.464627291607413)
        //añadimos los parametros de la ruta
        val polylineOptions= PolylineOptions()
            .add(coordenada1,coordenada2,coordenada3,coordenada4,coordenada5,coordenada1)
        //añadir la ruta al mapa
        val polyline=mapa.addPolyline(polylineOptions)

    }

    private fun gestionarLocalizacion() {
        if (!::mapa.isInitialized) return //comprobar que el mapa esta inicializado
        if (//revisar que el permiso de localizacion precisa este activado
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
            &&
            //revisar que el permiso de localizacion no precisa este activado
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            mapa.isMyLocationEnabled = true
            mapa.uiSettings.isMyLocationButtonEnabled = true
        } else {
            pedirPermisos()
        }

    }

    private fun pedirPermisos() {
        if (
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            ||
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        ) {
            mostrarExplicacion()
        } else {
            escogerPermisos()
        }
    }

    private fun escogerPermisos() {
        locationPermissionRequest.launch(
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    private fun mostrarExplicacion() {
        AlertDialog.Builder(this)
            .setTitle("Permiso requerido")
            .setMessage("Por favor, habilita los permisos en la configuración de la aplicación.")
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton("Aceptar") { dialog, _ ->
                startActivity(Intent(Settings.ACTION_APPLICATION_SETTINGS))
                dialog.dismiss()
            }
            .create()
            .dismiss()
    }

    private fun ponerMarcador(coordenadas: LatLng) {
        val marker= MarkerOptions().position(coordenadas).title("Ubicacion random ")//datos del marcador que vamos a añadir
        mapa.addMarker(marker)//añadimos la marca al mapa
        mostrarAnimacion(coordenadas,12f)
    }

    private fun mostrarAnimacion(coordenadas: LatLng, zoom: Float) {
        mapa.animateCamera(
            CameraUpdateFactory.newLatLngZoom(coordenadas,zoom),
            4500,
            null
        )
    }

    override fun onRestart() {
        super.onRestart()
        gestionarLocalizacion()
    }
}