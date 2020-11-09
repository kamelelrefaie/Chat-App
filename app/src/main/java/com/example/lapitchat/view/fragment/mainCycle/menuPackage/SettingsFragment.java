package com.example.lapitchat.view.fragment.mainCycle.menuPackage;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.example.lapitchat.R;
import com.example.lapitchat.helper.LoadingDialog;
import com.example.lapitchat.helper.cropper.GlideApp;
import com.example.lapitchat.helper.cropper.ImagePickerActivity;
import com.example.lapitchat.view.activity.MainActivity;
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
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

import static com.example.lapitchat.helper.HelperMethods.onLoadImageFromUrlOff;
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
    private StorageReference mStorageRef;
    private StorageReference mStorageImageRef;

    private LoadingDialog loadingDialog;

    private static final int GALLERY_PICK = 1;
    byte[] imageByte;

    private StatusFragment statusFragment;
    private Bundle bundle;
    private Unbinder unbinder;
    public static final int REQUEST_IMAGE = 100;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        unbinder = ButterKnife.bind(this, view);
        // get info from this fragment and send it to another one through bundle
        bundle = new Bundle();
        statusFragment = new StatusFragment();

        mStorageRef = FirebaseStorage.getInstance().getReference().child("profile_images");
        mStorageImageRef = FirebaseStorage.getInstance().getReference().child("profile_images");

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String mUId = mCurrentUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(mUId);
        // adding firebase offline features
        databaseReference.keepSynced(true);

        //set values
        getValues(databaseReference);

        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over
        ImagePickerActivity.clearCache(getActivity());


        return view;
    }

    private void loadProfile(String url) {
        Log.d("TAG", "Image cache path: " + url);

        GlideApp.with(this).load(url)
                .into(settingsFragmentImg);
        settingsFragmentImg.setColorFilter(ContextCompat.getColor(getActivity(), android.R.color.transparent));
    }




    /**
     * this method used to set values
     *
     * @param databaseReference get database ref from current fragment
     */
    private void getValues(DatabaseReference databaseReference) {
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {

                    //getting values from database
                    String name = snapshot.child("name").getValue().toString();
                    String image = snapshot.child("image").getValue().toString();
                    String status = snapshot.child("status").getValue().toString();

                    // set values
                    settingsFragmentTxtDisplay.setText(name);

                    if (!image.equals("default")) {
                        onLoadImageFromUrlOff(settingsFragmentImg, image, getActivity());
                    }

                    settingsFragmentTxtStatus.setText(status);
                } catch (Exception e) {

                }

            }//in data changed

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                try {

                } catch (Exception e) {

                }
            }
        });
    }

    // change image and status

    @OnClick({R.id.settings_fragment_btn_image, R.id.settings_fragment_btn_status})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.settings_fragment_btn_image:
                // start picker to get image for cropping and then use the image in cropping activity

                Dexter.withActivity(getActivity())
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showImagePickerOptions();
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();


//                CropImage.activity()
//                        .setGuidelines(CropImageView.Guidelines.ON)
//                        .setAspectRatio(1, 1)
//                        .start(getContext(), this);
                break;

            case R.id.settings_fragment_btn_status:

                // using bundle here to send information through fragments
                bundle.putString("STATUS_TXT", settingsFragmentTxtStatus.getText().toString());
                statusFragment.setArguments(bundle);

                // go to status fragment
                replaceFragment(getActivity().getSupportFragmentManager(), R.id.menu_container_activity_frame, statusFragment);
                break;
        }
    }
    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(getActivity(), new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {

                // get cropping image uri
                Uri resultUri = data.getParcelableExtra("path");

                File imageFile = new File(resultUri.getPath());
                try {
                    Bitmap compressedImageBitmap = new Compressor(getContext())
                            .setMaxHeight(200).setMaxHeight(200)
                            .setQuality(75)
                            .compressToBitmap(imageFile);

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    compressedImageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    imageByte = baos.toByteArray();

                } catch (IOException e) {
                    e.printStackTrace();
                }


                String Current_uID = mCurrentUser.getUid();
                mStorageRef = mStorageImageRef.child(Current_uID + ".jpg");
                StorageReference bitmapPath = mStorageImageRef.child("thumbs").child(Current_uID + ".jpg");
                // upload profile photo and thumb
                uploadingPhoto(mStorageRef, resultUri, bitmapPath);

            }
        }
    }

    //    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Toast.makeText(getActivity(), "on result", Toast.LENGTH_SHORT).show();
//
//    }
    private void uploadingPhoto(StorageReference mStorageRef, Uri resultUri, StorageReference bitmapPath) {

        mStorageRef.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                mStorageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        databaseReference.child("image").setValue(uri.toString());
                        // start uploading thumb
                        UploadTask uploadTask = bitmapPath.putBytes(imageByte);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                                try {

                                } catch (Exception e) {

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

            }
        });
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }
    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getActivity().getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    @Override
    public void onBack() {
        startActivity(new Intent(getActivity(),MainActivity.class));
        getActivity().finish();
    }
}
