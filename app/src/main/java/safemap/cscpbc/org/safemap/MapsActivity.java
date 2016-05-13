package safemap.cscpbc.org.safemap;

import android.*;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity implements GoogleMap.OnInfoWindowClickListener, OnMapReadyCallback {

    public static String ACTIVITY_TITLE = "ACTIVITY_TITLE";
    public final static int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    private GoogleMap mMap;
    private SafetyLocation.LocationType locationType = SafetyLocation.LocationType.swimLocation;
    private ArrayList<SafetyLocation> locations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String title = extras.getString(ACTIVITY_TITLE);
            setTitle(title);

            int type = extras.getInt(MainActivity.LOCATION_TYPE);
            locationType = SafetyLocation.LocationType.values()[type];
        }

        locations = loadLocations(locationType);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    protected ArrayList<SafetyLocation> loadLocations(SafetyLocation.LocationType locationType) {
        ArrayList<SafetyLocation> locations = null;
        String jsonFile;

        switch (locationType) {
            case swimLocation:
                jsonFile = "swim_locations.json";
                break;

            case carSeatLocation:
                jsonFile = "car_locations.json";
                break;

            case helmetLocation:
                jsonFile = "helmet_locations.json";
                break;

            case pillDropLocation:
            default:
                jsonFile = "pill_locations.json";
                break;
        }

        JSONObject json = loadJSONFromAsset(jsonFile);

        if (json != null) {
            locations = SafetyLocation.locationsFromJSON(json);
        }

        return locations;
    }

    protected float markerHue(SafetyLocation.LocationType locationType) {
        float hue = BitmapDescriptorFactory.HUE_RED;

        switch (locationType) {
            case swimLocation:
                hue = BitmapDescriptorFactory.HUE_BLUE;
                break;

            case carSeatLocation:
                hue = BitmapDescriptorFactory.HUE_ORANGE;
                break;

            case helmetLocation:
                hue = BitmapDescriptorFactory.HUE_GREEN;
                break;

            case pillDropLocation:
            default:
                hue = BitmapDescriptorFactory.HUE_RED;
                break;
        }

        return hue;
    }

    protected String markerTitle(SafetyLocation.LocationType locationType) {
        String title = getResources().getString(R.string.swimLessonsTitle);

        switch (locationType) {
            case swimLocation:
                title = getResources().getString(R.string.swimLessonsTitle);
                break;

            case carSeatLocation:
                title = getResources().getString(R.string.carSeatTitle);
                break;

            case helmetLocation:
                title = getResources().getString(R.string.helmetFittingTitle);
                break;

            case pillDropLocation:
            default:
                title = getResources().getString(R.string.pillDropOffTitle);
                break;
        }

        return title;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        /*
        LatLng cscPalmBeach = new LatLng(26.551748, -80.071720);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(cscPalmBeach)
                .title("CSC Palm Beach")
                .snippet("2300 High Ridge Rd, Boynton Beach, FL 33426")
                .icon(BitmapDescriptorFactory.defaultMarker(markerHue(locationType)));
        CameraPosition cameraPosition = new CameraPosition.Builder().target(markerOptions.getPosition()).zoom(18).build();
        mMap.addMarker(markerOptions);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        */

        if (locations != null && locations.size() > 0) {
            // use the first location as a zoom focal point
            SafetyLocation zoom = locations.get(0);
            LatLng position = new LatLng(zoom.latitude, zoom.longitude);

            for (SafetyLocation location : locations) {
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(new LatLng(location.latitude, location.longitude))
                        .title(location.name)
                        .snippet(location.address)
                        .icon(BitmapDescriptorFactory.defaultMarker(markerHue(locationType)));
                mMap.addMarker(markerOptions);
            }

            // get the user's current location
            LocationManager locationManager = (LocationManager)
                    getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();

            Location location = locationManager.getLastKnownLocation(locationManager
                    .getBestProvider(criteria, false));

            if (location != null) {
                position = new LatLng(location.getLatitude(), location.getLongitude());
            } else {
                try {
                    locationManager.requestSingleUpdate(criteria, new LocationListener() {
                        @Override
                        public void onLocationChanged(Location location) {
                            LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
                            CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(10).build();
                            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                        }

                        @Override
                        public void onStatusChanged(String provider, int status, Bundle extras) {

                        }

                        @Override
                        public void onProviderEnabled(String provider) {

                        }

                        @Override
                        public void onProviderDisabled(String provider) {

                        }
                    }, null);
                } catch ( SecurityException e ) {
                    e.printStackTrace();
                }
            }

            CameraPosition cameraPosition = new CameraPosition.Builder().target(position).zoom(10).build();
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }

        mMap.setOnInfoWindowClickListener(this);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        Intent intent = new Intent(MapsActivity.this, MapDetailActivity.class);
        intent.putExtra(MapDetailActivity.DETAIL_SUBJECT, markerTitle(locationType));
        intent.putExtra(MapDetailActivity.DETAIL_NAME, marker.getTitle());
        intent.putExtra(MapDetailActivity.DETAIL_ADDRESS, marker.getSnippet());
        intent.putExtra(MapDetailActivity.DETAIL_LAT, marker.getPosition().latitude);
        intent.putExtra(MapDetailActivity.DETAIL_LNG, marker.getPosition().longitude);
        MapsActivity.this.startActivity(intent);
    }

    // helper functions
    protected JSONObject loadJSONFromAsset(String jsonFile) {
        String jsonString = null;
        JSONObject json = null;
        try {
            InputStream is = getAssets().open(jsonFile);

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            jsonString = new String(buffer, "UTF-8");
            json = new JSONObject(jsonString);

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }
}
