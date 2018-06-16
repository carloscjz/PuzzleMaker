package com.example.usuario.puzzlemaker;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class JuegoPuzleActivity extends Activity {

    private static boolean isWon = false;

    private static final int COLUMNS = 3;
    private static final int DIMENSIONS = COLUMNS * COLUMNS;

    private static String[] winningOrder;
    private static String[] tileList;
    private static Bitmap[] piezas;
    private static GestureDetectGridView mGridView;
    private static int mColumnWidth, mColumnHeight;

    public static final String UP = "up";
    public static final String DOWN = "down";
    public static final String RIGHT = "right";
    public static final String LEFT = "left";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_juego_puzle);
        //ImageView image = (ImageView) findViewById(R.id.imageView);

        init();

        Uri imageUri = (Uri) getIntent().getExtras().get("foto");
        try {
            InputStream inputStream = getContentResolver().openInputStream(imageUri);

            //Obtenemos el mapa de bits correspondiente al Uri
            Bitmap imageBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);

            //Particion de la imagen
            piezas = creaPiezas(imageBitmap);

            mezclar();

            setDimensions();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setDimensions(){
        ViewTreeObserver vto = mGridView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mGridView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int displayWidth = mGridView.getMeasuredWidth();
                int displayHeight = mGridView.getMeasuredHeight();

                int statusHeight = getStatusBarHeight(getApplicationContext());
                int requiredHeight = displayHeight - statusHeight;

                mColumnWidth = displayWidth/COLUMNS;
                mColumnHeight = requiredHeight/COLUMNS;

                display(getApplicationContext());
            }
        });
    }

    private int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen",
                "android");

        if (resourceId>0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }

        return result;
    }

    /**
     * Crea las piezas a partir de una imagen
     * @param imageBitmap
     * @return
     */
    private Bitmap[] creaPiezas (Bitmap imageBitmap){
        Bitmap[] piezas = new Bitmap[DIMENSIONS];
        int contador = 0;
        int anchoPieza = imageBitmap.getWidth()/COLUMNS;
        int altoPieza = imageBitmap.getHeight()/COLUMNS;

        for (int i=0; i<COLUMNS; i++){
            for (int j=0; j<COLUMNS; j++) {
                piezas[contador] = Bitmap.createBitmap(imageBitmap, anchoPieza* j,
                        altoPieza*i, anchoPieza-1, altoPieza-1);
                contador++;
            }
        }
        return piezas;
    }

    private static void display(Context context) {
        ArrayList<Button> buttons = new ArrayList<>();
        Button button;

        isWon = checkPuzzle(context);

        for (int i=0; i<tileList.length; i++) {
            button = new Button(context);

            button.setBackground(new BitmapDrawable(context.getResources(), piezas[Integer.parseInt(tileList[i])]));

            buttons.add(button);
        }

        mGridView.setAdapter(new CustomAdapter(buttons, mColumnWidth, mColumnHeight));

        if(isWon) Toast.makeText(context, "Puzzle completed", Toast.LENGTH_SHORT).show();
    }

    public static boolean isCompleted(){
        return isWon;
    }

    /**
     * Mezcla las posiciones de la lista
     */
    private void mezclar(){
        int index;
        String temp;
        Random random = new Random();

        for (int i=tileList.length-1; i>=0; i--) {
            index = random.nextInt(tileList.length-1);
            temp = tileList[index];
            tileList[index] = tileList[i];
            tileList[i] = temp;
        }
    }

    /**
     * Crea la lista de casillas.
     */
    private void init() {
        mGridView = (GestureDetectGridView) findViewById(R.id.grid);
        mGridView.setNumColumns(COLUMNS);

        tileList = new String[DIMENSIONS];
        winningOrder = new String[DIMENSIONS];
        for (int i=0; i<DIMENSIONS; i++) {
            tileList[i] = String.valueOf(i);
            winningOrder[i] = String.valueOf(i);
        }
    }

    public static void swap(Context context, int position, int swap) {
        String newPosition = tileList[position + swap];
        tileList[position + swap] = tileList[position];
        tileList[position] = newPosition;
        display(context);
    }

    public static void moveTiles(Context context, String direction, int position) {

        //Pieza de esquina de arriba-izquierda
        if (position==0) {
            if (direction.equals("right")) swap(context, position, 1);
            else if (direction.equals("down")) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            //Piezas centrales de arriba
        }else if (position>0 && position<COLUMNS-1) {
            if (direction.equals("left")) swap(context, position, -1);
            else if (direction.equals("down")) swap(context, position, COLUMNS);
            else if (direction.equals("right")) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

          //Pieza de esquina de arriba-derecha
        }else if (position==COLUMNS-1) {
            if (direction.equals("left")) swap(context, position, -1);
            else if (direction.equals("down")) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

          //Piezas centrales izquierda
        }else if (position>COLUMNS-1 && position<DIMENSIONS-COLUMNS && position%COLUMNS==0) {
            if (direction.equals("up")) swap(context, position, -COLUMNS);
            else if (direction.equals("right")) swap(context, position, 1);
            else if (direction.equals("down")) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            //Piezas centrales derecha
        }else if (position>COLUMNS-1 && position<DIMENSIONS-COLUMNS && (position-(COLUMNS-1))%COLUMNS==0) {
            if (direction.equals("up")) swap(context, position, -COLUMNS);
            else if (direction.equals("left")) swap(context, position, -1);
            else if (direction.equals("down")) swap(context, position, COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            //Pieza de esquina abajo-izquierda
        }else if (position==DIMENSIONS-COLUMNS) {
            if (direction.equals("right")) swap(context, position, 1);
            else if (direction.equals("up")) swap(context, position, -COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            //Pieza de esquina abajo-derecha
        }else if (position==DIMENSIONS-1) {
            if (direction.equals("left")) swap(context, position, -1);
            else if (direction.equals("up")) swap(context, position, -COLUMNS);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            //Piezas centrales de abajo
        }else if (position>DIMENSIONS-COLUMNS && position<DIMENSIONS-1) {
            if (direction.equals("left")) swap(context, position, -1);
            else if (direction.equals("up")) swap(context, position, -COLUMNS);
            else if (direction.equals("right")) swap(context, position, 1);
            else Toast.makeText(context, "Invalid move", Toast.LENGTH_SHORT).show();

            //Piezas centrales
        }else {
            if (direction.equals("left")) swap(context, position, -1);
            else if (direction.equals("down")) swap(context, position, COLUMNS);
            else if (direction.equals("right")) swap(context, position, 1);
            else swap(context, position, -COLUMNS);
        }


    }

    /**
     * Comprueba si se ha ganado la partida
     */
    private static boolean checkPuzzle(Context context){
        boolean win = true;

        for (int i=0; i<tileList.length; i++){
            if (!tileList[i].equals(winningOrder[i])){
                win = false;
                break;
            }
        }

        return win;
    }


}
