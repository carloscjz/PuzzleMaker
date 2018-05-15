package com.example.usuario.puzzlemaker;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

public class JuegoPuzleActivity extends Activity {

    ImageView imagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_puzle);

        imagen = (ImageView) findViewById(R.id.imageView);
        Uri imageUri = (Uri) getIntent().getExtras().get("foto");
        imagen.setImageURI(imageUri);
    }
}
