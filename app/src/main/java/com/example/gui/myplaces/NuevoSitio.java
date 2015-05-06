package com.example.gui.myplaces;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;


public class NuevoSitio extends ActionBarActivity {

    ImageView imagenSeleccionada;
    Button btnSelectImage;
    EditText nombreSitio;
    EditText descripcionSitio;
    Spinner spiner;
    private ArrayList<String> categorias;
    private GoogleMap mMap;
    private Location posicion;
    private String name;
    private String description;
    private String image;
    private String categoria;
    private AlertDialog alert = null;
    private boolean guardarEstado;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_sitio);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        //Nos permite conocer si debemos guardar estado o no
        guardarEstado=true;

        btnSelectImage=(Button)findViewById(R.id.nuevaFoto);
        imagenSeleccionada=(ImageView)findViewById(R.id.foto);
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        nombreSitio = (EditText) findViewById(R.id.nombreSitio);
        descripcionSitio = (EditText) findViewById(R.id.descripcionSitio);
        spiner = (Spinner) findViewById(R.id.spinner);
        //Añadimos las categorias a mostrar por el Spinner
        categorias = new ArrayList<>();
        categorias.add(0,"Ninguna");
        MySQLOpenHelper helper = new MySQLOpenHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM categories", null);
            while (cursor.moveToNext()) {
                String nuevaCategoria = cursor.getString(0);
                categorias.add(nuevaCategoria);
            }
        cursor.close();
        db.close();
        //Adaptador para el Spinner
        ArrayAdapter<String> adaptador = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                categorias);
        adaptador.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        //Mostramos las categorias en el Spinner
        spiner.setAdapter(adaptador);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nuevo_sitio, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_guardar) {
            //Si no tenemos una posicion
            if(posicion==null){
                //Activamos GPS
                ActivaGPS();
            }else{
                //Obtenemos todos los datos
                double latitud = posicion.getLatitude();
                double longitud = posicion.getLongitude();
                name = nombreSitio.getText().toString();
                description = descripcionSitio.getText().toString();
                categoria = spiner.getSelectedItem().toString();
                MySQLOpenHelper helper = new MySQLOpenHelper(this);
                SQLiteDatabase db = helper.getWritableDatabase();
                //Los añadimos a la BD
                db.execSQL("INSERT INTO myplaces (latitud, longitud, name, description, image, categoria) VALUES ('"+latitud+"', '"+longitud+"', '"+name+"', '"+description+"', '"+image+"', '"+categoria+"');");
                db.close();
                //Cerramos la actividad
                finish();
            }
            return true;
        }
        if (id == R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //Se activa cuando se pulsa sobre el boton Añadir imagen
    private void selectImage() {
        //Opciones del dialogo
        final CharSequence[] options = { "Haz una foto", "Elige de la galeria","Cancelar" };
        AlertDialog.Builder builder = new AlertDialog.Builder(NuevoSitio.this);
        builder.setTitle("Añade una imagen!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Haz una foto"))
                {   //Lanzamos el intent de la camara y esperamos una respuesta
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    //Creamos un file con la foto hecha
                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                    startActivityForResult(intent, 1);
                }
                else if (options[item].equals("Elige de la galeria"))
                {   //Lanzamos el Intent de seleccionar desde archivo y esperamos una respuesta
                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);

                }
                else if (options[item].equals("Cancelar")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            //Caso en el que hacemos una foto
            if (requestCode == 1) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                //Buscamos el file de la foto hecha anteriormente
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    //Convertimos el file en bitmap
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);
                    //Mostramos el bitmap
                    imagenSeleccionada.setImageBitmap(bitmap);
                    f.delete();
                    //Creamos una carpeta para almacenar nuestras fotos
                    File imagesFolder = new File(
                            Environment.getExternalStorageDirectory(), "Myplaces");
                    imagesFolder.mkdirs();
                    OutputStream outFile;
                    //Asignamos un nombre
                    String name = String.valueOf(System.currentTimeMillis());
                    File file = new File(imagesFolder, name+ ".jpg");
                    //Nos quedamos con el path, es lo que guardaremos en la BD
                    image = file.getAbsolutePath();
                    //La guardamos
                    try {
                        outFile = new FileOutputStream(file);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outFile);
                        outFile.flush();
                        outFile.close();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            //Caso en el que seleccionamos una foto
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor c = getContentResolver().query(selectedImage,filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                //Guardamos su path
                image = picturePath;
                c.close();
                //Generamos el bitmap para mostrarlo
                Bitmap bitmap = (BitmapFactory.decodeFile(picturePath));
                imagenSeleccionada.setImageBitmap(bitmap);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        recoverState();
        //Comprobamos que los Google Play Services estan disponibles
        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (available != ConnectionResult.SUCCESS) {
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(available, this, 0);
            if (dialog != null) {
                MyErrorDialog errorDialog = new MyErrorDialog();
                errorDialog.setDialog(dialog);
                errorDialog.show(getSupportFragmentManager(), "errorDialog");
            }
        }
        //Comprobamos si el GPS esta activado
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            ActivaGPS();
        }
        //Instanciamos el mapa
        setUpMapIfNeeded();
    }
    @Override
    protected void onPause() {
        super.onPause();
        saveState();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(alert != null){
            alert.dismiss ();
        }
    }
    private void setUpMapIfNeeded() {
        if (mMap == null) {
            //Instanciamos el objeto mMap a partir del MapFragment definido bajo el Id "map"
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if (mMap != null) {
                //Atributos del mapa
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                mMap.setMyLocationEnabled(true);
                CameraUpdate ZoomCam = CameraUpdateFactory.zoomTo(17);
                mMap.animateCamera(ZoomCam);
                //Listener para cambios de posicion
                mMap.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {

                    public void onMyLocationChange(Location pos) {
                        double lat = pos.getLatitude();
                        double lon = pos.getLongitude();
                        //Movemos la camara a la posicion
                        CameraUpdate cam = CameraUpdateFactory.newLatLng(new LatLng(
                                lat, lon));
                        mMap.animateCamera(cam);
                        //Actualizamos posicion, son las coordenadas que guardaremos
                        posicion = pos;

                    }
                });
            }
        }
    }

    private void ActivaGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                .setCancelable(false)
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //Dialogo que nos lleva a activar el GPS
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        //Cerrar dialogo
                        dialog.cancel();
                    }
                });
        alert = builder.create();
        alert.show();
    }
    public void saveState(){
        //Guardaremos las preferencias si debemos hacerlo
        if(!guardarEstado) return;
            SharedPreferences preferences = getSharedPreferences("Preferencias", Activity.MODE_PRIVATE);
            if (preferences == null) return;
            SharedPreferences.Editor preferencesEditor = preferences.edit();
            if (preferencesEditor == null) return;
            preferencesEditor.putString("namePlace", ((EditText) findViewById(R.id.nombreSitio)).getText().toString());
            preferencesEditor.putString("descriptionPlace", ((EditText) findViewById(R.id.descripcionSitio)).getText().toString());
            preferencesEditor.putInt("spinner", ((Spinner) findViewById(R.id.spinner)).getSelectedItemPosition());
            preferencesEditor.commit();

    }
    public void recoverState(){
        //Recuperaremos las preferencias si debemos hacerlo
        if(!guardarEstado)return;
        SharedPreferences preferences = getSharedPreferences("Preferencias", MODE_PRIVATE);
        if (preferences==null) return;
        ((EditText) findViewById(R.id.nombreSitio)).setText(preferences.getString("namePlace",""));
        ((EditText) findViewById(R.id.descripcionSitio)).setText(preferences.getString("descriptionPlace",""));
        ((Spinner) findViewById(R.id.spinner)).setSelection(preferences.getInt("spinner", 0));
    }
    public void clearState(){
        SharedPreferences preferences = getSharedPreferences("Preferencias", MODE_PRIVATE);
        //Borramos el estado
        preferences.edit().remove("namePlace");
        preferences.edit().remove("descriptionPlace");
        preferences.edit().remove("spinner");
        preferences.edit().clear().commit();
        //Cuando borramos el estado, no queremos que se recupere inmediatamente
        guardarEstado = false;
    }
    @Override
    public void finish(){
        //Sobreescribimos el metodo finish para que borre el estado
        super.finish();
        clearState();

    }




}


