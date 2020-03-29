package com.example.socialapp;


import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTaskScheduler;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import static android.app.Activity.RESULT_OK;
import static com.google.firebase.storage.FirebaseStorage.getInstance;


/**
 * A simple {@link Fragment} subclass.
 */
public class profileFragment extends Fragment {
    //firebase
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    //storage
    StorageReference storageReference;// khai bao
    //path where image of user profile and cover will be stored
    String storagepath = "User_Profile_Cover_Img/";//ok


    ImageView avatarIv,coverIv;
    TextView nameTv, phoneTv, EmailTv;
    FloatingActionButton floatingActionButton;
    ProgressDialog pd;
    //permission constant
    private static final int CAMERA_REQUEST_CODE = 100;
    private static final int STORAGE_REQUEST_CODE = 200;
    private static final int IMAGE_PICK_GALLERY_CODE = 300;
    private static final int IMAGE_PICK_CAMERA_CODE = 400;
    //arrays of permission to be requested
    String camerapermission[];
    String storagepermission[];

    //uri of picked image
    Uri image_uri;
    // for checking profile or cover photo
    String profileorcover;

    public profileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);


        //init firebase
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        storageReference = getInstance().getReference();
        //init arrays of permission
        camerapermission = new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        storagepermission = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        //init view

        avatarIv = view.findViewById(R.id.avatarIV);
        coverIv = view.findViewById(R.id.coverIv);
        nameTv = view.findViewById(R.id.nameTv);
        phoneTv = view.findViewById(R.id.PhoneTv);
        EmailTv = view.findViewById(R.id.EmailTv);
        floatingActionButton = view.findViewById(R.id.editBt);
        //init progress dialog
        pd = new ProgressDialog(getActivity());

//       /* we have to get info of currently signed in user.
//       we can get ist using user's email or uid i'm gonna
//       retrieve user detail using email
        /*
        by using orderByChild query we will show the detail from a node whose
        key named email has value equal to currently signed in email ,
        it will search all nodes
        where the key matches is will get its detail
        */
//
        Query query = databaseReference.orderByChild("email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //check until required data get
                for (DataSnapshot ds :dataSnapshot.getChildren()){

                    //get data
                    String image = ""+ds.child("image").getValue();
                    String name = ""+ds.child("name").getValue();
                    String phone = ""+ds.child("phone").getValue();
                    String email = ""+ds.child("email").getValue();
                    String cover = ""+ds.child("cover").getValue();


                    //set data
                    nameTv.setText(name);
                    phoneTv.setText(phone);
                    EmailTv.setText(email);

                    try {
                        //if image is received  then set

                        Picasso.get().load(image).into(avatarIv);

                    }

                    catch (Exception e){
                        //if there is any exception white getting image then set default
                        Picasso.get().load(R.drawable.ic_default_image_white).into(avatarIv);
                        Log.e("bbb","avatarIV loi cmnr");
                    }

                    try {
                        //if image is received  then set
                        Picasso.get().load(cover).into(coverIv);

                    }

                    catch (Exception e){
                        //if there is any exception white getting image then set default
                        Picasso.get().load(R.drawable.ic_default_image_white).into(coverIv);
                        Log.e("bbb","cover loi cmnr");
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEditprofileDialog();

            }
        });

        return view;
    }
    private boolean checkstoragepermission(){
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                 ==(PackageManager.PERMISSION_GRANTED);
        return  result;
    }

    private void requeststoragepermissions(){
        //request runtime storage permission
        requestPermissions(storagepermission,STORAGE_REQUEST_CODE);
    }

    private boolean checkcamerapermission(){
        //check if storage permission is enabled or not
        //return true if enabled
        //return false if not enabled
        boolean result = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.CAMERA)
                ==(PackageManager.PERMISSION_GRANTED);

        boolean result1 = ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)
                ==(PackageManager.PERMISSION_GRANTED);
        return  result && result1;
    }

    private void requecameragepermissions(){
        //request runtime storage permission
        requestPermissions(camerapermission,CAMERA_REQUEST_CODE);
    }

    private void showEditprofileDialog()    {
        //show dialog edit name , edit phone , edit cover photo ,
        // edit image
        //options to show in dialog
        String option[] = {"Edit Profile Picture","Edit Name","Edit Phone Number","Edit Cover Photo"};
        //alert dialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("Choose Action");
        //set items to dialog
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if(which == 0)
                {
                    //edit profile clicked
                    pd.setMessage("Update profile picture");
                    showImagePicDialog();

                }
                else if (which == 1)
                {
                    //edit name clicked
                    pd.setMessage("Update your name ");
                    profileorcover = "image"; // changing profile picture , make sure to assign same value
                    showNamephoneUpdatedialog("name");
                }
                else if (which == 2)
                {
                    //edit phone clicked
                    pd.setMessage("Update your Number phone");//changing cover photo , make sure to assign same value
                    profileorcover = "cover";
                    showNamephoneUpdatedialog("phone");

                }

                else if (which == 3)
                {
                    //edit cover photo clicked
                    pd.setMessage("Update cover photo");//changing cover photo , make sure to assign same value
                    showImagePicDialog();

                }
            }

            private void showNamephoneUpdatedialog(final String key ) {
                    // paremater "key" will constant value
                //eather 'name' which is key in user's database is used to update user's name
                // or  'phone' which is key in user's database is used to update user's phone

                //custom dialog
                AlertDialog.Builder builder1 = new AlertDialog.Builder(getActivity());
                builder.setTitle("Udate"+key);// update name or phone
                //set layout of dialog
                LinearLayout linearLayout = new LinearLayout(getActivity());
                linearLayout.setOrientation(LinearLayout.VERTICAL);
                linearLayout.setPadding(10,10,10,10);
                //add edit text
                final EditText editText = new EditText(getActivity());
                editText.setHint("Enter" + key);//edit name and phone
                linearLayout.addView(editText);


                builder.setView(linearLayout);
                //add button in dialog to update
                builder.setPositiveButton("update", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //input text from edit text
                        String value = editText.getText().toString().trim();
                        //validate if user entered  something or not
                        if(!TextUtils.isEmpty(value))
                        {
                            pd.show();

                            HashMap<String, Object> result = new HashMap<>();
                            result.put(key,value);
                            databaseReference.child(user.getUid()).updateChildren(result)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            pd.dismiss();
                                            Toast.makeText(getActivity(), "Updated...", Toast.LENGTH_SHORT).show();

                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getActivity(),""+e.getMessage(), Toast.LENGTH_SHORT).show();

                                }
                            });

                        }
                        else
                            {}

                    }
                });
                //add button in dialog to cancel
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                //create and show dialog
                builder.create().show();




            }
        });
        //create and show dialog
        builder.create().show();
    }

    private void showImagePicDialog() {
        //show  dialog containing option camera and gallery to pick  the image

        String option[] = {"CAMERA","GALLERY"};
        //alert dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        //set title
        builder.setTitle("Pick Image From");
        //set items to dialog
        builder.setItems(option, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //handle dialog item clicks
                if(which == 0)
                {
                    //camera clicked

                    if(!checkcamerapermission()){
                        requecameragepermissions();
                    }
                    else {
                        pickfromcamera();
                    }
                }
                else if (which == 1)
                {
                    //gallery clicked
                    if(!checkstoragepermission()){
                        requeststoragepermissions();
                    }
                    else {
                        pickfromgallery();
                    }
                }
            }
        });
        //create and show dialog
        builder.create().show();

        //for picking image from
        //* camera [camera and storage permission required
        // * gallery {storage permission required
    }

    @Override
    public void onRequestPermissionsResult(int requestCode
            , @NonNull String[] permissions, @NonNull int[] grantResults) {
        // this method called when is user press Allow  or deny from permission request dialog
        //*here we will handle  permission case  (allowed or deny)
        switch(requestCode){
            case  CAMERA_REQUEST_CODE:
                {
                    if(grantResults.length >0)
                    {
                        boolean cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                        boolean writestorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                        if (cameraAccepted && writestorageAccepted){
                            //permission enabled
                            pickfromcamera();

                            
                        }
                        else
                            Toast.makeText(getActivity(), "Please enable Camera and storage permission", Toast.LENGTH_SHORT).show();
                    }

            }break;
        //pick from gallery, first check if camera and storage permission  allowed or not
            case  STORAGE_REQUEST_CODE:{
                if(grantResults.length >0)
                {
                    boolean writestorageAccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (writestorageAccepted){
                        //permission enabled
                        pickfromgallery();
                    }
                    else
                        Toast.makeText(getActivity(), "Please enable storage permission", Toast.LENGTH_SHORT).show();
                }

            }break;
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //this method will be called after picking image from camera or gallery
        if(resultCode == RESULT_OK )
        {
            if(requestCode == IMAGE_PICK_GALLERY_CODE)
            {
                //image picked from gallery,get uri of image
                image_uri = data.getData();
                uploadProfileCoverphoto(image_uri);

            }
            if(requestCode ==  IMAGE_PICK_CAMERA_CODE)
            {
                //image picked from camera ,get uri of image

                uploadProfileCoverphoto(image_uri);
                
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void uploadProfileCoverphoto(final Uri uri) {
        // show progress
        pd.show();
        /*Instead of creating separate function for profile Picture and cover photo
        *i'm doing work for both in same function
        * to add check ill add a string variable and assign it value "image" when user click
        * "edit profile pic", and assign it value "cover" when user click "edit cover photo"
        * here :image is the key in each user  containing url of user's profile user
        *       cover is the key in each user  containing url of user's cover photo user
        * */

        // the parameter "image_uri" contains the  uri of image picked either  from camera or gallery
        //* we will use UID of the currently signed in user as name of the image so there will be only one image
        //* profile and one image for cover for each user
        //path and name of image for cover for each user
        String filePathAndName =  storagepath+ "" +profileorcover+ "_" + user.getUid();

        StorageReference storageReference2nd = storageReference.child(filePathAndName);
        storageReference2nd.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //image is uploaded  to storage , now get it url and store in user's database
                Task<Uri> uriTask = taskSnapshot.getStorage().getDownloadUrl();
                while(!uriTask.isSuccessful());
                Uri downloadUri = uriTask.getResult();
                //check ig image  is uploaded or not and url is received
                if(uriTask.isSuccessful()){
                    //image uploaded
                    //add/update url in user's database
                    HashMap<String,Object> results = new HashMap<>();
                    //first parameter is profileorcoverphoto that has value "image" or "cover" which
                    //are key in user's database where url of image will be saved in one of them
                    //second parameter contains the url of  image stored in firebase storage , this url
                    //wil be saved  as value against key "image" or "cover"

                    results.put(profileorcover,downloadUri.toString());

                    databaseReference.child(user.getUid()).updateChildren(results)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    //url in database of user is added successful
                                    // dismiss progress bar
                                    pd.dismiss();
                                    Toast.makeText(getActivity(), "Image Updated",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            //error  adding url in database of user
                            //dismiss progress bar
                            pd.dismiss();
                            Toast.makeText(getActivity(), "Error Updating Image ...",
                                    Toast.LENGTH_SHORT).show();
                            

                        }
                    });
                }
                else
                    {
                        pd.dismiss();
                        Toast.makeText(getActivity(), "Some error occured", Toast.LENGTH_SHORT).show();
                    }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //there were some error(s), get and show error message ,dismiss progress dialog
                pd.dismiss();
                Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

    }

    private void pickfromcamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE,"Temp PICTURE");
        values.put(MediaStore.Images.Media.DESCRIPTION,"Temp Description");
        // put image uri
        image_uri  = getActivity().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values);
        //intent to start camera
        Intent cameraintent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraintent.putExtra(MediaStore.EXTRA_OUTPUT,image_uri);
        startActivityForResult(cameraintent,IMAGE_PICK_CAMERA_CODE);
    }

    private void pickfromgallery() {
        //pick from gallery
        Intent galleryintent = new Intent(Intent.ACTION_PICK);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent,IMAGE_PICK_GALLERY_CODE);
    }
}
