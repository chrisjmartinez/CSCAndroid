package safemap.cscpbc.org.safemap;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    public static String LOCATION_TYPE = "LOCATION_TYPE";
    public static String LOCATIONS_FILE = "locations.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getUsersCurrentLocation();
        downloadLocations();
    }

    public void swimButtonClick(View v) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra(MapsActivity.ACTIVITY_TITLE, getResources().getString(R.string.swimLessonsTitle));
        intent.putExtra(LOCATION_TYPE, SafetyLocation.LocationType.swimLocation.ordinal());
        MainActivity.this.startActivity(intent);
    }

    public void carSeatButtonClick(View v) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra(MapsActivity.ACTIVITY_TITLE, getResources().getString(R.string.carSeatTitle));
        intent.putExtra(LOCATION_TYPE, SafetyLocation.LocationType.carSeatLocation.ordinal());
        MainActivity.this.startActivity(intent);
    }

    public void helmetButtonClick(View v) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra(MapsActivity.ACTIVITY_TITLE, getResources().getString(R.string.helmetFittingTitle));
        intent.putExtra(LOCATION_TYPE, SafetyLocation.LocationType.helmetLocation.ordinal());
        MainActivity.this.startActivity(intent);
    }

    public void pillDropButtonClick(View v) {
        Intent intent = new Intent(MainActivity.this, MapsActivity.class);
        intent.putExtra(MapsActivity.ACTIVITY_TITLE, getResources().getString(R.string.pillDropOffTitle));
        intent.putExtra(LOCATION_TYPE, SafetyLocation.LocationType.pillDropLocation.ordinal());
        MainActivity.this.startActivity(intent);
    }

    public void emailClick(View v) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/html");
        String[] addresses = {getString(R.string.eMailAddress)};
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.emailSubject));

        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void websiteClick(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.fullWebsiteURL)));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MapsActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
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

    private void getUsersCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, MapsActivity.MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        // get the user's current location
        LocationManager locationManager = (LocationManager)
                getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();

        try {
            locationManager.requestSingleUpdate(criteria, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
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

    public void downloadLocations() {
        String urlJSON = "http://pbcreads.org/" + LOCATIONS_FILE;
        DownloadLocationsTask kmlDownloader = new DownloadLocationsTask();

        kmlDownloader.execute(new String[]{ urlJSON } );
    }

    private class DownloadLocationsTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            InputStream is = getConnection(params[0]);
            FileOutputStream outputStream;

            if (is != null) {
                try {
                    outputStream = getApplicationContext().openFileOutput(LOCATIONS_FILE, Context.MODE_PRIVATE);
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return (Void)null;
        }

        protected void onProgressUpdate(Void... progress) {
        }

        protected void onPostExecute(Void result) {
        }
    }

    private InputStream getConnection(String url) {
        InputStream is = null;
        try {
            URLConnection conn = new URL(url).openConnection();
            is = conn.getInputStream();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return is;
    }

}
