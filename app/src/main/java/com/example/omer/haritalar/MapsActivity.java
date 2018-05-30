package com.example.omer.haritalar;

import android.Manifest;
import android.app.AlertDialog;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private final static String TAG = "Main Activity";
    private GoogleMap mMap;
    private final static int REQUEST_lOCATION = 90;
    MarkerOptions trbzonMarker;
    Marker mMarker;
    Marker markerTrabzon, markerAnkara, markerIzmir, markerIstanbul;
    List<Marker> markerList;
    Marker clickedMarker;
    String jsonVeri;
    List<MarkerBilgileri> markerBilgileriList;
    SupportMapFragment mapFragment;
    private LatLngBounds ADELAIDE = new LatLngBounds(
            new LatLng(35.9025, 25.90902), new LatLng(42.02683, 44.5742));
    AutoCompleteTextView etLocation;
    private PlaceAutocompleteAdapter mAdapter;
    GeoDataClient mGeoDataClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        markerList = new ArrayList<>();
        markerBilgileriList = new ArrayList<>();

        mGeoDataClient = Places.getGeoDataClient(this, null);
        etLocation = findViewById(R.id.et_location);
        mAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient, ADELAIDE, null);
        etLocation.setAdapter(mAdapter);


        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ZoomControls zoom = (ZoomControls) findViewById(R.id.zoom);
        zoom.setOnZoomOutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomOut());
            }
        });
        zoom.setOnZoomInClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.animateCamera(CameraUpdateFactory.zoomIn());
            }
        });

        final Button btn_MapType = (Button) findViewById(R.id.btn_Sat);
        btn_MapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    btn_MapType.setText("NORMAL");
                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    btn_MapType.setText("UYDU");
                }
            }
        });

        Button btnGo = (Button) findViewById(R.id.btn_Go);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String location = etLocation.getText().toString();
                if (mMarker != null) {
                    mMarker.remove();
                }
                if (location != null && !location.equals("")) {
                    List<Address> adressList = null;
                    Geocoder geocoder = new Geocoder(MapsActivity.this);
                    try {
                        adressList = geocoder.getFromLocationName(location, 1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Address address = adressList.get(0);

                    MarkerOptions mMarkerOp = new MarkerOptions()
                            .title(address.getCountryName())
                            .snippet(address.getAddressLine(0))
                            .position(new LatLng(address.getLatitude(), address.getLongitude()
                            ));
                    Log.d(TAG, mMarkerOp.getSnippet());
                    mMarker = mMap.addMarker(mMarkerOp);
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 9));
                }
            }
        });

    }

    //Map Ready
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng ankara = new LatLng(39.925533, 32.866287);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ankara, 5));
        mMap.setLatLngBoundsForCameraTarget(ADELAIDE);
        //izin kontrolü ve yer belirleme tuşu
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_lOCATION);
            }
        }

        MarkerBilgileri markerBilgileriTra = new MarkerBilgileri("Trabzon Çarşıbaşı", "Memleket", 41.083332, 39.383331);
        MarkerBilgileri markerBilgileriAnk = new MarkerBilgileri("Ankara", "Başkent", 39.9334, 32.8597);
        MarkerBilgileri markerBilgileriIzm = new MarkerBilgileri("İzmir", "Ege", 38.423734, 27.142826000000014);
        MarkerBilgileri markerBilgileriIst = new MarkerBilgileri("İstanbul", "Marmara", 41.0082, 28.9784);
        markerBilgileriList.add(markerBilgileriTra);
        markerBilgileriList.add(markerBilgileriAnk);
        markerBilgileriList.add(markerBilgileriIzm);
        markerBilgileriList.add(markerBilgileriIst);

        for (MarkerBilgileri markerBilgileri : markerBilgileriList) {
            Marker marker =
                    mMap.addMarker(new MarkerOptions()
                            .title(markerBilgileri.getIsim())
                            .snippet(markerBilgileri.getIcerik())
                            .position(new LatLng(markerBilgileri.getX(), markerBilgileri.getY()))
                            .draggable(true));
            markerList.add(marker);
        }


        mMap.setMinZoomPreference(5);
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Log.d(TAG, "infoclick" + marker.getSnippet());
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle(marker.getTitle());
                builder.setMessage(marker.getSnippet());
                builder.show();
            }
        });
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
//                try {
//                    List<Address> adressList = null;
//                    Geocoder geocoder = new Geocoder(MapsActivity.this);
//                    adressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
//                    Address address = adressList.get(0);
//                    MarkerOptions mMarkerOp = new MarkerOptions()
//                            .title(address.getCountryName())
//                            .snippet(address.getAddressLine(0))
//                            .position(new LatLng(latLng.latitude, latLng.longitude
//                            ));
//                    Log.d(TAG, mMarkerOp.getSnippet());
//                    mMarker = mMap.addMarker(mMarkerOp);
//                } catch (IOException e) {
//                    Log.d(TAG, e.toString());
                //-----------------//
//            }
            }
        });

        //for İmplement markerClick listener
        mMap.setOnMarkerClickListener(this);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

            }
        });

        mMap.setOnMarkerDragListener(new MyMarkerLongClickListener(markerList) {
            @Override
            public void onLongClickListener(Marker marker) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle(marker.getTitle());
                builder.setMessage(marker.getSnippet());
                builder.show();
            }
        });

    }

    @Override
    //izinler
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_lOCATION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    mMap.setMyLocationEnabled(true);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Kullanıcı konum iznini vermedi", Toast.LENGTH_SHORT).show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //onSaveInstanceState
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        if (mMarker != null) {
            savedInstanceState.putDouble("markerLtng", mMarker.getPosition().latitude);
            savedInstanceState.putDouble("markerLong", mMarker.getPosition().longitude);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    //onRestoreInstanceState
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    //Marker Click
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

}
