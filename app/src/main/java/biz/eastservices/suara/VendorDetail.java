package biz.eastservices.suara;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.stepstone.apprating.AppRatingDialog;
import com.stepstone.apprating.listener.RatingDialogListener;

import java.util.Arrays;

import biz.eastservices.suara.Common.Common;
import biz.eastservices.suara.Model.Rating;
import biz.eastservices.suara.Model.Vendor;
import de.hdodenhof.circleimageview.CircleImageView;

public class VendorDetail extends AppCompatActivity implements RatingDialogListener {

    Button btnWhatsApp, btnWaze, btnRating, btnCall;
    RatingBar ratingBar;
    TextView txt_name, txt_description,txt_website;
    CircleImageView circleImageView;

    String uri = "", whatAppUri = "";
    String smsNumber;

    ScrollView rootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_detail);

        rootLayout = (ScrollView) findViewById(R.id.rootLayout);

        ratingBar = (RatingBar) findViewById(R.id.ratingBar);

        circleImageView = (CircleImageView) findViewById(R.id.profile_image);

        txt_description = (TextView) findViewById(R.id.txt_description);
        txt_name = (TextView) findViewById(R.id.txt_name);
        txt_website = (TextView) findViewById(R.id.txt_website);
        txt_website.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = txt_website.getText().toString();
                if (!url.startsWith("http://") && !url.startsWith("https://"))
                    url = "http://" + url;
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(browserIntent);
            }
        });

        btnRating = (Button) findViewById(R.id.btn_rating);
        btnWaze = (Button) findViewById(R.id.btn_waze);
        btnWhatsApp = (Button) findViewById(R.id.btn_whats_app);
        btnCall = (Button) findViewById(R.id.btn_call);


        btnCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_CALL);

                intent.setData(Uri.parse("tel:" + Common.currentVendor.getPhone()));
                if (ActivityCompat.checkSelfPermission(VendorDetail.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                   return;
                }
                startActivity(intent);
    }
});

        btnRating.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();
            }
        });

        loadDetail(Common.selected_uid_people);

        btnWaze.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(uri)));
                }
                catch (ActivityNotFoundException ex)
                {
                    Snackbar.make(rootLayout,"Please install WazeApp",Snackbar.LENGTH_SHORT)
                            .show();
                    // Toast.makeText(CandidateDetail.this, "Please install WazeApp", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnWhatsApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{

                    startActivity(new Intent(android.content.Intent.ACTION_VIEW, Uri.parse(whatAppUri)));
                }
                catch (ActivityNotFoundException  ex)
                {
                    Snackbar.make(rootLayout,"Please install Whatapp",Snackbar.LENGTH_SHORT)
                            .show();
                    //Toast.makeText(CandidateDetail.this, "Please install WhatApp", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadDetail(String uid) {
        FirebaseDatabase.getInstance()
                .getReference(Common.USER_TABLE_VENDOR)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Vendor vendor = dataSnapshot.getValue(Vendor.class);

                        Picasso.with(getBaseContext())
                                .load(vendor.getAvatarUrl())
                                .error(R.drawable.ic_terrain_black_24dp)
                                .placeholder(R.drawable.ic_terrain_black_24dp)
                                .into(circleImageView);

                        txt_description.setText(vendor.getBusinessDescription());
                        txt_name.setText(vendor.getBusinessName());
                        txt_website.setText(vendor.getWebsite());
                        txt_website.setMovementMethod(LinkMovementMethod.getInstance());

                        uri = "waze://?ll="+vendor.getLat()+", "+vendor.getLng()+"&navigate=yes";
                        whatAppUri="https://api.whatsapp.com/send?phone="+Common.currentVendor.getPhone();
                        smsNumber = FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

        getRatingOfPeople(uid);
    }

    private void showRatingDialog() {
        new AppRatingDialog.Builder()
                .setPositiveButtonText("Submit")
                .setNegativeButtonText("Cancel")
                .setNoteDescriptions(Arrays.asList("Very Bad","Not Good","Quite Ok","Very Good","Excellent"))
                .setDefaultRating(1)
                .setTitle("Rate this person")
                .setDescription("Please select some stars and give your feedback")
                .setTitleTextColor(R.color.colorPrimary)
                .setDescriptionTextColor(R.color.colorPrimary)
                .setHint("Please write your comment here...")
                .setHintTextColor(R.color.colorAccent)
                .setCommentTextColor(android.R.color.white)
                .setCommentBackgroundColor(R.color.colorPrimaryDark)
                .setWindowAnimation(R.style.RatingDialogFadeAnim)
                .create(VendorDetail.this)
                .show();
    }

    @Override
    public void onPositiveButtonClicked(int i, String s) {
        final Rating rating = new Rating(Float.valueOf(String.valueOf(i)),
                s);
        FirebaseDatabase.getInstance()
                .getReference(Common.USER_RATING)
                .child(Common.selected_uid_people)
                .child(FirebaseAuth.getInstance().getUid())
                .setValue(rating)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(VendorDetail.this, "Rating Succeed", Toast.LENGTH_SHORT).show();
                        loadDetail(Common.selected_uid_people);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(VendorDetail.this, "Rating Failed", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onNegativeButtonClicked() {

    }

    @Override
    public void onNeutralButtonClicked() {

    }


    private void getRatingOfPeople(String uid) {

        FirebaseDatabase
                .getInstance()
                .getReference(Common.USER_RATING)
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    int count=0,sum=0;
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for(DataSnapshot postSnapshot:dataSnapshot.getChildren())
                        {
                            Rating item = postSnapshot.getValue(Rating.class);
                            sum+=item.getRatingValue();
                            count++;
                        }
                        if(count != 0)
                        {
                            float average = sum/count;
                            ratingBar.setRating(average);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



    }
}
