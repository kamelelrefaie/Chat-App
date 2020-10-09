package com.example.lapitchat.view.fragment.mainCycle.menuPackage;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.lapitchat.R;
import com.example.lapitchat.helper.LoadingDialog;
import com.example.lapitchat.view.fragment.BaseFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static android.app.Activity.RESULT_OK;
import static com.example.lapitchat.helper.HelperMethods.onLoadImageFromUrl;
import static com.example.lapitchat.helper.HelperMethods.replaceFragment;

public class SettingsFragment extends BaseFragment {
    @BindView(R.id.settings_fragment_img)
    CircleImageView settingsFragmentImg;
    @BindView(R.id.settings_fragment_txt_display)
    TextView settingsFragmentTxtDisplay;
    @BindView(R.id.settings_fragment_txt_status)
    TextView settingsFragmentTxtStatus;
    @BindView(R.id.settings_fragment_btn_image)
    Button settingsFragmentBtnImage;
    @BindView(R.id.settings_fragment_btn_status)
    Button settingsFragmentBtnStatus;
    private DatabaseReference databaseReference;
    private FirebaseUser mCurrentUser;
    private Unbinder unbinder;
    private StatusFragment statusFragment;
    private Bundle bundle;
    private static final int GALLERY_PICK = 1;
    private StorageReference mStorageRef;
    private StorageReference mStorageImageRef;
    private LoadingDialog loadingDialog;
    byte[] imageByte;
    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, view);
        bundle = new Bundle();
        statusFragment = new StatusFragment();
        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
        mStorageImageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String mUId = mCurrentUser.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = snapshot.child("name").getValue().toString();
                String image = snapshot.child("image").getValue().toString();
                String status = snapshot.child("status").getValue().toString();
                String thumb_image = snapshot.child("thumb_image").getValue().toString();

                settingsFragmentTxtDisplay.setText(name);
                if (!image.equals("default"))
                    onLoadImageFromUrl(settingsFragmentImg, image, getActivity());
                settingsFragmentTxtStatus.setText(status);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        setUpActivity();
        mainActivity.setToolBar(view.GONE);
        mainActivity.setFrame(view.VISIBLE);


        return view;
    }

    @Override
    public void onBack() {
        startActivity(new Intent(getActivity(), mainActivity.getClass()));
    }

    @OnClick({R.id.settings_fragment_btn_image, R.id.settings_fragment_btn_status})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.settings_fragment_btn_image:
                // start picker to get image for cropping and then use the image in cropping activity


                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(getContext(), this);

               /* Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent, getString(R.string.select_image)),GALLERY_PICK);*/
                break;
            case R.id.settings_fragment_btn_status:
                bundle.putString("STATUS_TXT", settingsFragmentTxtStatus.getText().toString());
                statusFragment.setArguments(bundle);
                replaceFragment(getActivity().getSupportFragmentManager(), R.id.main_activity_of, statusFragment);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                loadingDialog = new LoadingDialog(getActivity());
                loadingDialog.startLoadingDialog();
                Uri resultUri = result.getUri();
                File imageFile = new File(resultUri.getPath());
                try {
                    Bitmap  compressedImageBitmap = new Compressor(getContext())
                            .setMaxHeight(200).setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(imageFile);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    imageByte= baos.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                String Current_uID = mCurrentUser.getUid();
                mStorageRef = mStorageImageRef.child(Current_uID + ".jpg");
               StorageReference bitmapPath = mStorageImageRef.child("thumbs").child(Current_uID + ".jpg");

                mStorageRef.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                        mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                databaseReference.child("image").setValue(uri.toString());


                                UploadTask uploadTask = bitmapPath.putBytes(imageByte);
                                uploadTask.addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Handle unsuccessful uploads
                                        try {

                                        }catch (Exception e){

                                        }
                                    }
                                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                     bitmapPath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                         @Override
                                         public void onSuccess(Uri uri) {
                                             databaseReference.child("thumb_image").setValue(uri.toString());
                                         }

                                     });
                                    }
                                });

                            }
                        });
                        loadingDialog.dismissDialog();
                    }
                });




            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
