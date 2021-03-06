package com.example.audacia.sample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

//import android.location.LocationListener;

public class MapsActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {
    public static final String TAG = "MapsActivity";
    public static final int THUMBNAIL = 1;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private Button picButton; //takes user to camera

    static final int REQUEST_IMAGE_CAPTURE_CODE = 1;

    private GoogleApiClient mGoogleApiClient;
    public Location mLastLocation;
    public double mLatitude;
    public double mLongitude;
    public LocationRequest mLocationRequest;
    public Location mCurrentLocation;
    public boolean mRequestingLocationUpdates = true;
    private File HW2dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/camera");
    private String HW2name = "Homework2.csv";
    public File Homework2 = new File(HW2dir, HW2name); // The Homework2.csv file is stored in DCIM/camera folder

    private TextView displaylatitude;
    private TextView displaylongitude;
    private ImageView mImageView;

    public EditText mEditTitle;
    public EditText mEditSnippet;
    public View MarkerInfoEditView;
    public String mTitle = "Edit title";
    public String mSnippet = "Edit Snippet";
    public Marker mMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maps);
        setUpMapIfNeeded();

        picButton = (Button) findViewById(R.id.photobutton);
        displaylatitude = (TextView) findViewById(R.id.displaylatitude);
        displaylongitude = (TextView) findViewById(R.id.displaylongitude);


        picButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        if (!Homework2.exists()) {
            try {
                Homework2.createNewFile();
                BufferedWriter bfw = new BufferedWriter(new FileWriter(Homework2, true));
                bfw.write("TimeStamp,Latitude,Longitude\n");
                bfw.close();
            } catch (IOException e) {
                Log.d("MapsActivity", "Homework2.csv file failed to create");
                e.printStackTrace();
            }
        }

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                Dialog MarkerInfo = MarkerInfoDialog(marker);

                MarkerInfo.show();

                marker.hideInfoWindow();

                return false;
            }
        });

        mMap.setMyLocationEnabled(true);
        buildGoogleApiClient();
        createLocationRequest();

    }

    private Dialog MarkerInfoDialog(Marker marker) {
        mMarker = marker;

        mTitle = mMarker.getTitle();
        mSnippet = mMarker.getSnippet();

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        builder.setTitle("Marker Info editor")
                .setCancelable(true)
                .setView(inflater.inflate(R.layout.edit_markerinfo, null))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Dialog f = (Dialog) dialog;

                        mEditTitle = (EditText) f.findViewById(R.id.title);
                        mEditSnippet = (EditText) f.findViewById(R.id.snippet);

                        mTitle = mEditTitle.getText().toString();
                        mSnippet = mEditSnippet.getText().toString();

                        mMarker.setTitle(mTitle);
                        mMarker.setSnippet(mSnippet);

                        mMarker.showInfoWindow();
                        dialog.dismiss();

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        return builder.create();
    }

    //Start camera activity to take a photo
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File tempdir = null;
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            /*
            try {
                tempdir = createImageFile();
            } catch (IOException e){
                e.printStackTrace();
            }
            if ( tempdir != null  ){
                //takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(tempdir));
            }
            */
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE_CODE);
        }
    }

    @Override
    //do Writing CSV file for homework 2
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //super.onActivityResult(requestCode,resultCode,data);

        if (requestCode == REQUEST_IMAGE_CAPTURE_CODE) {
            if (resultCode == RESULT_OK) {
                // photo is captured, save timestamp and the last available coordinate data to the CSV file
                if (mCurrentLocation != null) {
                    double latitude = mCurrentLocation.getLatitude();
                    double longitude = mCurrentLocation.getLongitude();

                    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                    String mRowInput = timeStamp + "," + latitude + "," + longitude + "\n";

                    try {
                        BufferedWriter bfw1 = new BufferedWriter(new FileWriter(Homework2, true));
                        bfw1.write(mRowInput);
                        bfw1.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    Bundle extras = data.getExtras();
                    if (extras.keySet().contains("data")) {

                        try {
                            Bitmap imageBitmap = (Bitmap) extras.get("data");

                            //save imageBitmap to storage
                            String imageFileName = "JPEG_" + timeStamp + "_.jpg";
                            File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera");

                            File image = new File(storageDir, imageFileName);
                            FileOutputStream out = new FileOutputStream(image);
                            imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                            out.flush();
                            out.close();

                            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                                    .icon(BitmapDescriptorFactory.fromBitmap(imageBitmap))
                            );
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, make some advices for the user
            }
        }
    }
/*
    private void drawLine() {
        PolylineOptions rectOptions = new PolylineOptions();
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera");
        Log.v("drawLine", "파일 리스트 전에");
        File[] fileList = storageDir.listFiles();
        Log.v("drawLine", "파일 리스트 후에");
        for(int i = 0; i < fileList.length; i++) {
            JPGFileFilter jpgFileFilter = new JPGFileFilter();
            if(!jpgFileFilter.accept(storageDir, fileList[i].getName())) {
                continue;
            }
            try {
                Log.v("drawLine", "포문 들어와서");
                Log.v("drawLine", fileList[i].getName());
                ExifInterface exif = new ExifInterface(storageDir.getAbsolutePath() + File.separator + fileList[i].getName());
                LatLng loc = new LatLng(Double.parseDouble(exif.getAttribute(TAG_GPS_LATITUDE)),
                        Double.parseDouble(exif.getAttribute(TAG_GPS_LONGITUDE)));
                rectOptions.add(loc);
            } catch(IOException e) {
                Toast.makeText(getApplicationContext(), "sibal", Toast.LENGTH_SHORT).show();
            }
        }
        Polyline polyline = mMap.addPolyline(rectOptions);
    }
*/
    //Connect with the GoogleApiClient
    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    //Create locationrequest
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(5000);
        mLocationRequest.setFastestInterval(2000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    //Get last known location & start location updates
    @Override
    public void onConnected(Bundle bundle) {
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            mLatitude = mLastLocation.getLatitude();
            mLongitude = mLastLocation.getLongitude();
        }

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

    }

    //startLocationUpdates in onResume() and onConnected()
    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        mRequestingLocationUpdates = false;
    }

    //stopLocationUpdates in onPause()
    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        mRequestingLocationUpdates = true;
    }

    @Override
    public void onLocationChanged(Location location) {

        mCurrentLocation = location;

        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        displaylatitude.setText(String.format("%.5f", latitude));
        displaylongitude.setText(String.format("%.5f", longitude));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions()
                .position(new LatLng(20, 20))
                .title("EECS397/600"));

        InputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(Homework2)); // get Homework2 file
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String currentLine = reader.readLine();
            if (reader.readLine() != null)
                currentLine = reader.readLine(); // Skip to 2nd line because first line is header info
            while (currentLine != null) // Loop through all lines in CSV file if it exists
            {
                String[] values = currentLine.split(","); // Split line by commas, giving three values: TIMESTAP, LAT, LONG
                String filepath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM + "/Camera").getAbsolutePath() + "/JPEG_" + values[0] + "_.jpg"; // Get filepath of current JPG
                Log.d("MarkerCreate", "Filepath for next marker is " + filepath);
                mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(Double.parseDouble(values[1]), Double.parseDouble(values[2]))) // Get Latitude and Longitude values
                        .icon(BitmapDescriptorFactory.fromPath(filepath)) // filepath
                );
                Log.d("MarkerCreate", "Created Marker with timestamp" + values[0] + "and LatLng" + values[1] + "," + values[2]);
                currentLine = reader.readLine(); // Go to next line
            }
        } catch (Exception FileNotFoundException) {
            Log.e("FileOpen", "File Not Found Exception while creating markers");
        }
    }

    //must implement abstract method onConnectionFailed()
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    //must implement abstract method onConnectionSuspended()
    @Override
    public void onConnectionSuspended(int i) {

    }
}
/*
class JPGFileFilter implements FilenameFilter
{
    @Override
    public boolean accept(File file, String s) {
        return s.toLowerCase().endsWith(".jpg");
    }
}
*/