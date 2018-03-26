package biz.eastservices.suara;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kyleduo.switchbutton.SwitchButton;

import java.util.HashMap;
import java.util.Map;

import biz.eastservices.suara.Common.Common;
import biz.eastservices.suara.Fragment.DeliveryFragment;
import biz.eastservices.suara.Fragment.RentFragment;
import biz.eastservices.suara.Fragment.SellFragment;
import biz.eastservices.suara.Fragment.ServiceFragment;
import biz.eastservices.suara.Fragment.TransportFragment;
import biz.eastservices.suara.Model.Vendor;
import dmax.dialog.SpotsDialog;

public class EmployerActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private static final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7172;
    private static int UPDATE_INTERVAL = 5000; // SEC
    private static int FATEST_INTERVAL = 3000; // SEC
    private static int DISPLACEMENT = 10; // METERS


    FirebaseDatabase database;
    DatabaseReference user_tbl, currentUserRef;
    BottomNavigationView bottomNavigationView;

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;

    int match_people = 0;

    SwitchButton mSwitchShowSecure;

    DatabaseReference vendorLocation;
    GeoFire geoFire;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getResources().getString(R.string.deliveries_string));
        setSupportActionBar(toolbar);

        //Request Runtime permission
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //Run-time request permission
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION
                }, MY_PERMISSION_REQUEST_CODE);
            } else {
                if (checkPlayServices()) {
                    buildGoogleApiClient();
                    createLocationRequest();
                }
            }
        } else {
            if (checkPlayServices()) {
                buildGoogleApiClient();
                createLocationRequest();
            }
        }

        //Init View
        bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.bottom_nav_employer);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.action_deliveries:
                        selectedFragment = new DeliveryFragment();
                        toolbar.setTitle(getResources().getString(R.string.deliveries_string));
                        break;

                    case R.id.action_services:
                        selectedFragment = ServiceFragment.getInstance(mLastLocation);
                        toolbar.setTitle(getResources().getString(R.string.services_string));
                        break;
                    case R.id.action_transports:
                        selectedFragment = TransportFragment.getInstance(mLastLocation);
                        toolbar.setTitle(getResources().getString(R.string.transports_string));
                        break;
                    case R.id.action_sell:
                        selectedFragment = SellFragment.getInstance(mLastLocation);
                        toolbar.setTitle(getResources().getString(R.string.buy_string));
                        break;
                    case R.id.action_rent:
                        selectedFragment = RentFragment.getInstance(mLastLocation);
                        toolbar.setTitle(getResources().getString(R.string.rent_string));
                        break;
                }
                if (selectedFragment != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame_layout, selectedFragment);
                    transaction.commit();
                }
                return true;
            }
        });


        //Init Firebase
        database = FirebaseDatabase.getInstance();
        user_tbl = database.getReference(Common.USER_TABLE_VENDOR);
        currentUserRef = database.getReference(Common.VENDOR_ONLINE);
        vendorLocation = database.getReference(Common.VENDOR_LOCATION);



        //Set
        setEnableOnlineTool();





    }

    private void setEnableOnlineTool() {


        final AlertDialog waitingDialog = new SpotsDialog(this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait...");
        waitingDialog.setCanceledOnTouchOutside(false);

        if (Common.isDebug) {
            user_tbl.child("c5f7ddd0-58c9-4920-849e-8f1fe8f0f096")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                mSwitchShowSecure.setEnabled(true);

                                waitingDialog.dismiss();

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            waitingDialog.dismiss();
                        }
                    });
        } else {
            user_tbl.child(FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists())
                                mSwitchShowSecure.setEnabled(true);

                            waitingDialog.dismiss();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            waitingDialog.dismiss();

                        }
                    });
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (checkPlayServices()) {
                        buildGoogleApiClient();
                        createLocationRequest();
                    }
                }
                break;
        }
    }

    private synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FATEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(getApplicationContext(), "This device is not supported", Toast.LENGTH_LONG).show();
                finish();
            }
            return false;
        }
        return true;
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        updateLocation(mLastLocation);
    }

    private void updateLocation(Location location) {



        final Map<String, Object> update_location = new HashMap<>();
        update_location.put("lat", mLastLocation.getLatitude());
        update_location.put("lng", mLastLocation.getLongitude());
        if (!Common.isDebug) {



            user_tbl.child(FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {

                                user_tbl.child(FirebaseAuth.getInstance().getUid())
                                        .updateChildren(update_location)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EmployerActivity.this, "Error update location", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

        } else {



            user_tbl.child("c5f7ddd0-58c9-4920-849e-8f1fe8f0f096")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {



                                user_tbl.child("c5f7ddd0-58c9-4920-849e-8f1fe8f0f096")
                                        .updateChildren(update_location)
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(EmployerActivity.this, "Error update location", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);

        bottomNavigationView.setSelectedItemId(R.id.action_deliveries);

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_employer, menu);




        mSwitchShowSecure = (SwitchButton) menu.findItem(R.id.show_online).getActionView().findViewById(R.id.switch_button);
        mSwitchShowSecure.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(EmployerActivity.this, "Online Vendor Current Location", Toast.LENGTH_SHORT).show();
                    setAvailableVendor();



                } else {
                    Toast.makeText(EmployerActivity.this, "Offline Vendor", Toast.LENGTH_SHORT).show();
                    removeAvailableVendor();


                }
            }
        });

        //Set Online
        if(Common.isDebug)
        {
            FirebaseDatabase.getInstance().getReference(Common.VENDOR_ONLINE)
                    .child("c5f7ddd0-58c9-4920-849e-8f1fe8f0f096")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                                mSwitchShowSecure.setChecked(true);
                            else
                                mSwitchShowSecure.setChecked(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
        else
        {
            FirebaseDatabase.getInstance().getReference(Common.VENDOR_ONLINE)
                    .child(FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                                mSwitchShowSecure.setChecked(true);
                            else
                                mSwitchShowSecure.setChecked(false);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }

        return true;
    }

    private void removeAvailableVendor() {
        if (Common.isDebug) {
            currentUserRef.child("c5f7ddd0-58c9-4920-849e-8f1fe8f0f096")
                    .removeValue()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EmployerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            currentUserRef.child(FirebaseAuth.getInstance().getUid())
                    .removeValue()
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(EmployerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setAvailableVendor() {

        final AlertDialog waitingDialog = new SpotsDialog(this);
        waitingDialog.show();
        waitingDialog.setMessage("Please wait...");
        waitingDialog.setCanceledOnTouchOutside(false);


        if (Common.isDebug) {
            user_tbl.child("c5f7ddd0-58c9-4920-849e-8f1fe8f0f096")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Vendor user = dataSnapshot.getValue(Vendor.class);

                                currentUserRef.child("c5f7ddd0-58c9-4920-849e-8f1fe8f0f096")
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                waitingDialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                waitingDialog.dismiss();
                                                Toast.makeText(EmployerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            waitingDialog.dismiss();
                        }
                    });
        } else {
            user_tbl.child(FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                Vendor user = dataSnapshot.getValue(Vendor.class);

                                currentUserRef.child(FirebaseAuth.getInstance().getUid())
                                        .setValue(user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {

                                                waitingDialog.dismiss();
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                waitingDialog.dismiss();
                                                Toast.makeText(EmployerActivity.this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                        }


                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            waitingDialog.dismiss();
                        }
                    });
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_setting) {

            startActivityForResult(new Intent(EmployerActivity.this, VendorSettings.class), Common.RESULT_CODE);

        } else if (item.getItemId() == R.id.action_copy) {
            if (mLastLocation != null) {
                String locationString = new StringBuilder(String.valueOf(mLastLocation.getLatitude()))
                        .append(",")
                        .append(mLastLocation.getLongitude())
                        .toString();
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                //ClipData clip = ClipData.newPlainText("location", locationString);
                clipboard.setText(locationString);

                Toast.makeText(this, "" + clipboard.getText(), Toast.LENGTH_SHORT).show();
            } else
                Toast.makeText(this, "No location to copy ! Please enable INTERNET", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case Common.RESULT_CODE:
                setEnableOnlineTool();
                setAvailableVendor();
                if (mLastLocation != null)
                    updateLocation(mLastLocation);
                break;
            default:
                break;
        }
    }


}
