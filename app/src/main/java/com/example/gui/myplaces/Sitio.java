package com.example.gui.myplaces;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;


public class Sitio extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitio);

        ImageView imagen =(ImageView) findViewById(R.id.imageView);
        TextView tvName =(TextView) findViewById(R.id.textView);
        TextView tvDescription =(TextView) findViewById(R.id.textView3);

        String[] datos;
        datos = getIntent().getStringArrayExtra("data");

        tvName.setText(datos[2]);
        tvDescription.setText(datos[3]);

        if(!datos[4].equals("null")) {
            File image = new File(datos[4]);
            if(image.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                //Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, ((int) convertDpToPixel(140, this)), ((int) convertDpToPixel(110, this)), true);
                imagen.setImageBitmap(bitmap);

            }
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_places);
            imagen.setImageBitmap(bitmap);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_sitio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
