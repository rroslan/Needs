package biz.eastservices.suara;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.squareup.picasso.Picasso;

import java.util.UUID;

import biz.eastservices.suara.Common.Common;
import biz.eastservices.suara.Model.Vendor;
import de.hdodenhof.circleimageview.CircleImageView;

public class VendorSettings extends AppCompatActivity {

    Button btnSave;
    MaterialEditText edtName, edtDescription;
    RadioButton rdiWorking, rdiStaticLocation, rdiAny, rdiServices, rdiTransports, rdiSell, rdiRent;

    CircleImageView avatar;

    int defaultRadioSelect = 0;
    private Uri filePath;

    FirebaseStorage storage;
    StorageReference storageReference;

    Vendor vendor = new Vendor();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor_settings);

        //Init FireStorage
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        btnSave = (Button) findViewById(R.id.btn_save);
        edtName = (MaterialEditText) findViewById(R.id.edt_name);
        edtDescription = (MaterialEditText) findViewById(R.id.edt_description);
        rdiAny = (RadioButton) findViewById(R.id.rdi_any);
        rdiRent = (RadioButton) findViewById(R.id.rdi_rent);
        rdiSell = (RadioButton) findViewById(R.id.rdi_sell);
        rdiServices = (RadioButton) findViewById(R.id.rdi_services);
        rdiStaticLocation= (RadioButton) findViewById(R.id.rdi_static_location);
        rdiTransports= (RadioButton) findViewById(R.id.rdi_transport);
        rdiWorking= (RadioButton) findViewById(R.id.rdi_working);
        avatar = (CircleImageView)findViewById(R.id.profile_image);
        avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), Common.PICK_IMAGE_REQUEST);
            }
        });

        rdiAny.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    defaultRadioSelect = 0;
            }
        });
        rdiServices.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    defaultRadioSelect = 1;
            }
        });
        rdiTransports.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    defaultRadioSelect = 2;
            }
        });
        rdiSell.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    defaultRadioSelect = 3;
            }
        });
        rdiRent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    defaultRadioSelect = 4;
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveInformation();
            }
        });

        loadInformation();
    }

    private void loadInformation() {
        if(!Common.isDebug)
        {
            FirebaseDatabase.getInstance()
                    .getReference(Common.USER_TABLE_VENDOR)
                    .child(FirebaseAuth.getInstance().getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if(dataSnapshot.exists())
                            {
                                Vendor user = dataSnapshot.getValue(Vendor.class);

                                if(!user.getAvatarUrl().isEmpty() && user.getAvatarUrl() != null)
                                    Picasso.with(getBaseContext())
                                            .load(vendor.getAvatarUrl())
                                            .into(avatar);

                                edtName.setText(user.getBusinessName());
                                edtDescription.setText(user.getBusinessDescription());
                                if(user.isWorking())
                                    rdiWorking.setChecked(true);

                                if(user.isStaticLocation())
                                    rdiStaticLocation.setChecked(true);

                                if(user.getCategory() != null) {
                                    if (Common.convertCategoryToType(user.getCategory()) == 0)
                                        rdiAny.setChecked(true);
                                    else if (Common.convertCategoryToType(user.getCategory()) == 1)
                                        rdiServices.setChecked(true);
                                    else if (Common.convertCategoryToType(user.getCategory()) == 2)
                                        rdiTransports.setChecked(true);
                                    else if (Common.convertCategoryToType(user.getCategory()) == 3)
                                        rdiSell.setChecked(true);
                                    else if (Common.convertCategoryToType(user.getCategory()) == 4)
                                        rdiRent.setChecked(true);
                                }
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
        else
        {
            FirebaseDatabase.getInstance()
                    .getReference(Common.USER_TABLE_VENDOR)
                    .child("c5f7ddd0-58c9-4920-849e-8f1fe8f0f096")
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                           if(dataSnapshot.exists())
                           {
                               Vendor user = dataSnapshot.getValue(Vendor.class);

                               if(user.getAvatarUrl() != null)
                                   Picasso.with(getBaseContext())
                                           .load(vendor.getAvatarUrl())
                                           .into(avatar);

                               edtName.setText(user.getBusinessName());
                               edtDescription.setText(user.getBusinessDescription());
                               if(user.isWorking())
                                   rdiWorking.setChecked(true);
                               else
                                   rdiStaticLocation.setChecked(true);

                               if(user.getCategory() != null) {
                                   if (Common.convertCategoryToType(user.getCategory()) == 0)
                                       rdiAny.setChecked(true);
                                   else if (Common.convertCategoryToType(user.getCategory()) == 1)
                                       rdiServices.setChecked(true);
                                   else if (Common.convertCategoryToType(user.getCategory()) == 2)
                                       rdiTransports.setChecked(true);
                                   else if (Common.convertCategoryToType(user.getCategory()) == 3)
                                       rdiSell.setChecked(true);
                                   else if (Common.convertCategoryToType(user.getCategory()) == 4)
                                       rdiRent.setChecked(true);
                               }
                           }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private void saveInformation() {

        String name = edtName.getText().toString();
        String description = edtDescription.getText().toString();
        if(name.isEmpty() || name == null)
        {
            Toast.makeText(this, "Please enter name !", Toast.LENGTH_SHORT).show();
            return;
        }

        if(description.isEmpty() || description == null)
        {
            Toast.makeText(this, "Please enter description !", Toast.LENGTH_SHORT).show();
            return;
        }

        vendor.setBusinessName(name);
        vendor.setBusinessDescription(description);
        vendor.setWorking(rdiWorking.isChecked());
        vendor.setStaticLocation(rdiStaticLocation.isChecked());
        vendor.setCategory(Common.convertTypeToCategory(defaultRadioSelect));

       if(!Common.isDebug)
       {
           FirebaseDatabase.getInstance().getReference(Common.USER_TABLE_VENDOR)
                   .child(FirebaseAuth.getInstance().getUid())
                   .setValue(vendor)
                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           Toast.makeText(VendorSettings.this, "Updated !", Toast.LENGTH_SHORT).show();
                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(VendorSettings.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });
       }
       else
       {
           FirebaseDatabase.getInstance().getReference(Common.USER_TABLE_VENDOR)
                   .child("c5f7ddd0-58c9-4920-849e-8f1fe8f0f096")
                   .setValue(vendor)
                   .addOnSuccessListener(new OnSuccessListener<Void>() {
                       @Override
                       public void onSuccess(Void aVoid) {
                           Toast.makeText(VendorSettings.this, "Updated !", Toast.LENGTH_SHORT).show();
                           finish();
                       }
                   })
                   .addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           Toast.makeText(VendorSettings.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                       }
                   });
       }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Common.PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            filePath = data.getData();
            avatar.setImageURI(data.getData());

            uploadImageAndGetUrl();
        }
    }

    private void uploadImageAndGetUrl() {
        if (filePath != null) {
            final ProgressDialog progressDialog = new ProgressDialog(VendorSettings.this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            String imageName = UUID.randomUUID().toString();
            final StorageReference ref = storageReference.child("images/" + imageName);
            ref.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            progressDialog.dismiss();
                            Toast.makeText(VendorSettings.this, "Uploaded", Toast.LENGTH_SHORT).show();
                            ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    vendor.setAvatarUrl(uri.toString());


                                }
                            });

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(VendorSettings.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }


}
