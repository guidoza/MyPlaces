package com.example.gui.myplaces;

import android.app.Activity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class BuscarSitio extends Activity implements OnMapReadyCallback {

    private String[] data;
    private String nombre;
    private String descripcion;
    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar_sitio);
        //Obtenemos los datos para encontrar el sitio
        data = getIntent().getStringArrayExtra("data");
        lat = Double.parseDouble(data[0]);
        lon = Double.parseDouble(data[1]);
        nombre = data[2];
        descripcion = data[3];
        //Instanciamos el mapa
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(GoogleMap map) {
        map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        LatLng sitio = new LatLng(lat, lon);
        map.setMyLocationEnabled(true);
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(sitio, 17));
        //Añadimos al mapa un marker en la posicion,
        //con titulo y descripcion del sitio para navegas hasta el.
        map.addMarker(new MarkerOptions()
                .title(nombre)
                .snippet(descripcion)
                .position(sitio));
    }
}