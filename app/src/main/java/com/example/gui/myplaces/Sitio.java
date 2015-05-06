package com.example.gui.myplaces;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.File;


public class Sitio extends ActionBarActivity {

    private AlertDialog alert = null;
    private double latitud;
    private double longitud;
    String[] datos;
    private String img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sitio);

        ImageView imagen =(ImageView) findViewById(R.id.imageView);
        TextView tvName =(TextView) findViewById(R.id.textView);
        TextView tvDescription =(TextView) findViewById(R.id.textView3);
        TextView tvCategoria = (TextView) findViewById(R.id.textView5);
        //Obtenemos la informacion del sitio
        datos = getIntent().getStringArrayExtra("data");
        latitud = Double.parseDouble(datos[0]);
        longitud = Double.parseDouble(datos[1]);
        img = datos[4];
        tvName.setText(datos[2]);
        tvDescription.setText(datos[3]);
        //CAategoria en blanco si es Ninguna
        if(datos[5].equals("Ninguna")){
            tvCategoria.setText("");
        }else tvCategoria.setText(datos[5]);
        //Si hay foto del sitio
        if(!datos[4].equals("null")) {
            //Creamos el bitmap a partir de path
            File image = new File(datos[4]);
            if(image.exists()){
                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath());
                //Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, ((int) convertDpToPixel(140, this)), ((int) convertDpToPixel(110, this)), true);
                //Lo mostramos
                imagen.setImageBitmap(bitmap);

            }
        //Si no hay foto, ponemos una por defecto
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.ic_places);
            imagen.setImageBitmap(bitmap);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //Comprobamos la disponibilidad de los Google Play Services
        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (available != ConnectionResult.SUCCESS) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(available, this, 0);
            if (dialog != null) {
                MyErrorDialog errorDialog = new MyErrorDialog();
                errorDialog.setDialog(dialog);
                errorDialog.show(getSupportFragmentManager(), "errorDialog");
            }
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
        if(id == R.id.action_compartir){
            //Enviaremos un email
            sendEmail();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //Se activa al pulsar sobre el boton del street view
    public void street(View v){
        //Activamos GPS
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            ActivaGPS();
        }
        Uri gmmIntentUri = Uri.parse("google.streetview:cbll="+latitud+","+longitud+"&cbp=0,30,0,0,-15");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        //Vemos nuestro sitio con el Street View de Google
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }
    //Podremos obtener una ruta hasta nuestro sitio
    public void irA(View v){
        //Activamos GPS
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            ActivaGPS();
        }
        //Utilizamos la interfaz nuestra propia BuscarSitio
        Intent i = new Intent(this, BuscarSitio.class);
        i.putExtra("data", datos);
        startActivity(i);

        //Utilizamos google maps directamente
        /**
        Uri gmmIntentUri = Uri.parse("google.navigation:q="+latitud+", "+longitud+"");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }**/
    }
    //Metodo para activar el GPS
    private void ActivaGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }

   //Metodo para compartir nuestros sitios
    protected void sendEmail() {
        //Creamos un intent
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/image");
        //emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{Email});
        //Asunto y cuerpo
        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Mira donde he estado!");
        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "En "+datos[2]+"!"+"\n\nEnviado desde la aplicación MyPlaces!");
        //Si tenemos una imagen
        if(!img.equals("null")) {
            File image = new File(img);
            if(image.exists()){
                String ruta = image.getAbsolutePath();
                //Adjuntamos la imagen
                emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + ruta));
            }
        }
        try {
            //Lanzamos el intent y veremos que aplicaciones instaladas en el dispositivo pueden atender la peticion
            startActivity(Intent.createChooser(emailIntent, "Compartir con:"));
        } catch (android.content.ActivityNotFoundException ex) {
            //Si no puede ser atendida, generaremos un mensaje de error
            Toast.makeText(Sitio.this,
                    "No se ha encontrado una aplicación para compartir tu sitio.", Toast.LENGTH_LONG).show();
        }
    }
}
