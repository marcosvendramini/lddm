package com.android.lddm;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.location.Location;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleMap mMap;
    private BDControl mBancoDeDados;
    private GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    private Bitmap imagem;

    private final int  MY_PERMISSIONS_REQUEST_LOCATION= 1;
    private final int  MY_PERMISSIONS_REQUEST_CAMERA= 2;
    private Button btnCamera;
    private Button btnDados;
    protected static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //Banco de Dados
        mBancoDeDados = new BDControl(getApplicationContext());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        btnCamera = (Button) findViewById(R.id.btnCamera);
        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //abrir camera
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                    OpenCamera();
                } else {
                    requestPermissions( new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CAMERA);
                }

            }
        });

        btnDados = (Button)findViewById(R.id.btnDados);
        btnDados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = mBancoDeDados.getReadableDatabase();
                String[] data = {DataSource.Dados._ID,
                        DataSource.Dados.COLUMN_NAME_LOGITUDE ,
                        DataSource.Dados.COLUMN_NAME_LATITUDE,
                        DataSource.Dados.COLUMN_NAME_DATA,
                        DataSource.Dados.COLUMN_NAME_FOTO, };

                Cursor c = db.query(DataSource.Dados.TABLE_NAME, data,null,null,null,null,null);

                //Ir para o ultimo para ver a quantidade
                c.moveToLast();

                Long item = c.getLong(c.getColumnIndexOrThrow(DataSource.Dados._ID));

                Toast.makeText(getApplicationContext(),"TESTE:"+ item,Toast.LENGTH_LONG).show();
            }
        });


    }

    public void OpenCamera(){
        Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(i, 123);

    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {


        mMap = googleMap;

        //pedir permissão em casao de android 6
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        } else {
            requestPermissions( new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
        }

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                LatLng local = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(local).title("Marker in Sydney"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(local));
            }else{
                Log.e("ERRO","Location null ");
                Toast.makeText(this,"LOcation fail",Toast.LENGTH_SHORT).show();
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
        Log.i(TAG, "Connection suspended");
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                    if (permissions.length == 1 &&
                            permissions[0] == Manifest.permission.ACCESS_FINE_LOCATION &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        mMap.setMyLocationEnabled(true);
                    }
                }else{
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
            case MY_PERMISSIONS_REQUEST_CAMERA:
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                    if (permissions.length == 1 &&
                            permissions[0] == Manifest.permission.CAMERA &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        OpenCamera();
                    }
                }else{
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }

    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 123) {
            if (resultCode == Activity.RESULT_OK) {
                //usuário tirou a foto
                if (data != null) {
                    Bundle bundle = data.getExtras();
                    imagem = (Bitmap) bundle.get("data");
                    SalvarBD();
                } else {
                    //usuário não tirou a foto
                }
            }
        }
    }

    public void SalvarBD(){

        //pegar hora
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date hora = Calendar.getInstance().getTime();
        String dataFormatada = sdf.format(hora);

        //salvar banco de dados
        SQLiteDatabase db = mBancoDeDados.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DataSource.Dados.COLUMN_NAME_LATITUDE,mLastLocation.getLatitude());
        values.put(DataSource.Dados.COLUMN_NAME_LOGITUDE,mLastLocation.getLongitude());
        values.put(DataSource.Dados.COLUMN_NAME_DATA,dataFormatada );
        values.put(DataSource.Dados.COLUMN_NAME_FOTO, getBytes(imagem) );

        long newRowId = db.insert(DataSource.Dados.TABLE_NAME, null, values);
    }

    //COnverter Bitmap em Array de Bytes
    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,0,stream);
        return stream.toByteArray();
    }

    // convert from byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }
}
