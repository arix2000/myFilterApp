package com.k.myfilterapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;

public class GalleryActivity extends AppCompatActivity
{

    public static final String EXTRA_IMAGE_URI = "com.k.myfilterapp.EXTRA_IMAGE_URI";
    public static final int PICK_IMAGE_RESULT = 1;
    private static final String TAG = "TAG_FOR_THIS";
    private ImageView inputImage;
    private ImageButton continueButton;
    private ButtonShape continueButtonShape;
    private Uri imageUri;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        inputImage = findViewById(R.id.photo_to_filter);
        continueButton = findViewById(R.id.btn_continue);
        continueButtonShape = new ButtonShape();

        initChoosePhotoButtonListenerAndShape();
    }

    private void initChoosePhotoButtonListenerAndShape()
    {
        Button choosePhotoButton = findViewById(R.id.btn_choose_photo);
        choosePhotoButton.setBackground(new ShapeDrawable(new ButtonShape()));
        choosePhotoButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                choosePhoto();
            }
        });
    }

    private void choosePhoto()
    {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_RESULT);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        setContinueButtonState();
    }

    private void setContinueButtonState()
    {
        if (inputImage.getDrawable() == null) {
            continueButtonShape.setColour(Color.parseColor("#3E3E3E")); //it isn't clickable
            continueButton.setBackground(new ShapeDrawable(continueButtonShape));
        } else {
            continueButtonShape.setColour(Color.parseColor("#FB9F2C")); // it clickable
            continueButton.setBackground(new ShapeDrawable(continueButtonShape));
            initContinueButtonListenerAndShape();
        }
    }

    private void initContinueButtonListenerAndShape()
    {
        continueButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                openSendToMainScreenActivity();
            }
        });
    }

    private void openSendToMainScreenActivity()
    {
        Intent intent = new Intent(this, LoadingActivity.class);
        intent.putExtra(EXTRA_IMAGE_URI,imageUri);
        startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null) {
            imageUri = data.getData();
            inputImage.setImageURI(imageUri);
        }

    }

}