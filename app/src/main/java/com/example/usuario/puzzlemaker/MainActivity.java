package com.example.usuario.puzzlemaker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity {

    private static final int PICK_FOTO = 1;

    private Button fotoButton, borrarButton, jugarButton;
    private Uri imageUri;
    private ImageView fotoImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fotoButton = (Button) findViewById(R.id.fotoButton);
        borrarButton = (Button) findViewById(R.id.borrarButton);
        jugarButton = (Button) findViewById(R.id.jugarButton);
        fotoImageView = (ImageView) findViewById(R.id.fotoImageView);
        final Drawable imageBkg = fotoImageView.getBackground();

        // Crea accion para el boton de subir foto
        fotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galeriaIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(galeriaIntent, PICK_FOTO);
            }
        });

        // Crea accion para el boton de borrar foto
        borrarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fotoImageView.setImageDrawable(null);
                fotoImageView.setBackground(imageBkg);
            }
        });

        // Crea accion para el boton de jugar
        jugarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent juegoPuzleIntent = new Intent(getApplicationContext(), JuegoPuzleActivity.class);
                juegoPuzleIntent.putExtra("foto",imageUri);
                startActivity(juegoPuzleIntent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK && requestCode == PICK_FOTO) {
            imageUri = data.getData();
            fotoImageView.setBackground(null);
            fotoImageView.setImageURI(imageUri);
        }
    }
}
