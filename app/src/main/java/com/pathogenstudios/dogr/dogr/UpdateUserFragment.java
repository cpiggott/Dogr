package com.pathogenstudios.dogr.dogr;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
/**
 * Created by HaydenKinney on 2/21/15.
 */
/**
 * A placeholder fragment containing a simple view.
 */
public class UpdateUserFragment extends Fragment {
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private static final String ARG_SECTION_NUMBER = "4";
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_CROP_PICTURE = 2;
    static final int REQUEST_IMAGE_SELECT = 3;


    String currentPhotoPath;
    Uri    outputUri;
    ImageButton userImage;
    EditText  userBio;
    Button    updateInformation;
    Button    cropPhoto;
    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static UpdateUserFragment newInstance(int sectionNumber) {
        UpdateUserFragment fragment = new UpdateUserFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_SECTION_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    public UpdateUserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_update_user, container, false);
        userImage = (ImageButton) rootView.findViewById(R.id.imageButtonUpdateUser);
        userBio = (EditText)rootView.findViewById(R.id.editTextUpdateUser);
        updateInformation = (Button)rootView.findViewById(R.id.buttonUpdateUser);
        cropPhoto = (Button)rootView.findViewById(R.id.buttonCropPhoto);
        userImage.setImageResource(R.drawable.ic_action_person);
        cropPhoto.setEnabled(false);
        final ParseUser currentUser = ParseUser.getCurrentUser();
        ParseFile profilePic  = (ParseFile) currentUser.get("userProfileImage");
        try {
            profilePic.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    loadImageFromParse(bytes, userImage);

                }
            });
        } catch (Exception e) {

        }

        String userBioText = (String)currentUser.get("userBio");
        userBio.setText(userBioText);

        updateInformation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storeBio(currentUser);
//                storePicture();
            }
        });

        cropPhoto.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                cropPhoto();
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                Toast.makeText(getActivity(), "picSuccess", Toast.LENGTH_LONG).show();
                cameraOrGallery();
            }
        });

        return rootView;

    }

    private void cropPhoto() {
        try{

            Intent intent = new Intent("com.android.camera.action.CROP");
            // this will open all images in the Galery
            intent.setDataAndType(Uri.fromFile(new File(currentPhotoPath)), "image/*");

//            // this defines the aspect ration
            intent.putExtra("aspectX", 1000);
            intent.putExtra("aspectY", 1000);
            intent.putExtra("scale", "true");
            intent.putExtra("scaleUpIfNeeded", true);
            intent.putExtra("return-data", false);
            try{
                outputUri = Uri.fromFile(createImageFile());
            } catch (Exception e) {
                Log.d("WHY", "DOINEEDTHIS");
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
            startActivityForResult(intent, REQUEST_CROP_PICTURE);

        } catch(ActivityNotFoundException anfe) {
            Toast.makeText(this.getActivity(), "Whoops - your device can't support cropping!", Toast.LENGTH_LONG).show();
        }
    }

    private void storeBio(ParseUser currentUser) {
        currentUser.put("userBio", userBio.getText().toString());
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("STOREBIO: ", "Finished Saving.");
                storePicture();
            }
        });
    }

    private void storePicture() {
        try{
            Bitmap bitmap = ((BitmapDrawable)userImage.getDrawable()).getBitmap();
                    //MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), outputUri);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] bytearray = stream.toByteArray();

            final ParseUser currentUser = ParseUser.getCurrentUser();
            if(bytearray != null) {
                final ParseFile file = new ParseFile("userProfPic.jpg", bytearray);
                file.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        Log.d("UPDATEUSERSFRAG", "Finished saving parse File");
                        currentUser.put("userProfileImage", file);
                        currentUser.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                Log.d("UPDATEUSERFRAG: ", "Saved currentUser");
                            }
                        });

                    }
                });
            }
        } catch (Exception ex) {
            Log.d("UPDATEUSERFRAGMENT: Received exception: ", ex.toString());
        }
    }

    private void cameraOrGallery() {
        CharSequence options[] = new CharSequence[] { "Take A Photo", "Choose From Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose Photo Selection Method");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int picked) {
                switch (picked) {
                    case 0:
                        TakeNewPhoto();
                        break;
                    case 1:
                        SelectGalleryPhoto();
                        break;
                }
            }
        });
        builder.show();
    }

    private void TakeNewPhoto() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent
                .resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
                currentPhotoPath = photoFile.getAbsolutePath();

            } catch (IOException EX) {
                // Error
                Toast.makeText(this.getActivity(), "Error Creating Image File", Toast.LENGTH_SHORT).show();
                // Anything better to do with this error?
            }
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

            }
        }
    }

    private void SelectGalleryPhoto() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickPhoto , REQUEST_IMAGE_SELECT);
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM
                        + "/Camera"); // Add the pictures to the default
        // DCIM/Camera folder.
        File image = File.createTempFile(imageFileName, /* prefix */
                ".jpg", /* suffix */
                storageDir /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        // currentPhotoPath[nextPhoto] = "file:" + image.getAbsolutePath();
        return image;
    }

    private void loadImageFromParse(byte[] thumbnail, final ImageButton img) {
        if(thumbnail != null) {
            Bitmap bmp = BitmapFactory.decodeByteArray(thumbnail, 0, thumbnail.length);
            img.setImageBitmap(bmp);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE
                && resultCode == Activity.RESULT_OK) {
            userImage.setImageBitmap(renderBitmap());
            cropPhoto.setEnabled(true);
        } else if (requestCode == REQUEST_IMAGE_SELECT
                && resultCode == Activity.RESULT_OK) {
            Uri selectedImage = intent.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(
                    selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            currentPhotoPath = cursor.getString(columnIndex);

            cursor.close();
            userImage.setImageBitmap(renderBitmap());
            cropPhoto.setEnabled(true);

        } else if (requestCode == REQUEST_CROP_PICTURE && resultCode == Activity.RESULT_OK ) {
            userImage.setImageURI(outputUri);
        }
    }

    private Bitmap renderBitmap() {
        // Get the dimensions of the View
        int targetW = userImage.getWidth();
        int targetH = userImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetH, photoH / targetW);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath,
                bmOptions);
//        if (bitmap.getWidth() > bitmap.getHeight()) {
//            Matrix matrix = new Matrix();
//            matrix.postRotate(90);
//            Bitmap rotatedBitmap = Bitmap.createBitmap(bitmap, 0, 0,
//                    bmOptions.outWidth, bmOptions.outHeight, matrix, true);
//            bitmap = rotatedBitmap;
////			rotatedBitmap.recycle();
//        }
        return( bitmap );

    }
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        ((MainActivity) activity).onSectionAttached(
                getArguments().getInt(ARG_SECTION_NUMBER));
    }
}
