package com.creative.locationgps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity
{

    private FusedLocationProviderClient mCliente;

    private static final String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.INTERNET
    };
    private static final int REQUEST_PERMISSION_CODE = 20;
    private static final int REQUEST_SETTINGS = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCliente = LocationServices.getFusedLocationProviderClient(this);
        checkAndRequirePermissions();
    }

    private boolean checkAndRequirePermissions() {
        //Permisos requeridos que no han sido aceptados
        List<String> permisosRequeridos = new ArrayList<>();
        for (String permiso : PERMISSIONS)
        {
            //Revisa si el permiso ha sido aceptado de lo contrario lo agrega a la lista
            //De los permisos requeridos
            if (ContextCompat.checkSelfPermission(this, permiso) != PackageManager.PERMISSION_GRANTED)
                permisosRequeridos.add(permiso);
        }

        //Si existe al menos un permiso que no ha sido aceptado el metodo regresa falso
        //se lanza una solicitud para que el usuario acepte los permisos
        //si la lista esta vacia entonces todos los permisos fueron aceptados y regresa true
        if (!permisosRequeridos.isEmpty())
        {
            ActivityCompat.requestPermissions(this, permisosRequeridos.toArray(new String[permisosRequeridos.size()]), REQUEST_PERMISSION_CODE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        //Valida que la peticion haya sido para pedir los permisos (numero 20)
        if (requestCode == REQUEST_PERMISSION_CODE)
        {
            //Se guardan los resultados de los permisos denegados
            HashMap<String, Integer> permisos = new HashMap<>();
            //Cuenta los permisos denegados
            int permisosDenegados = 0;
            for (int i = 0; i < grantResults.length; i++)
            {
                if (grantResults[i] == PackageManager.PERMISSION_DENIED)
                {
                    permisos.put(permissions[i], grantResults[i]);
                    permisosDenegados++;
                }
            }

            //Si existen permisos denegados
            if (permisosDenegados != 0)
            {
                for (Map.Entry<String, Integer> permiso : permisos.entrySet())
                {
                    if (ActivityCompat.shouldShowRequestPermissionRationale(this, permiso.getKey()))
                    {
                        //Se valida nuevamente que acepeten los permisos
                        AlertDialog alert = Alerts.crearDialogoPermisos(this, "Para que la aplicación funcione sin problemas debes aceptar todos los permisos",
                                "Si, aceptar permisos", "No, Cerrar aplicacion",
                                (dialogInterface, i) ->
                                {
                                    dialogInterface.dismiss();
                                    checkAndRequirePermissions();
                                },
                                (dialogInterface, i) ->
                                {
                                    dialogInterface.dismiss();
                                    this.finish();
                                });
                        alert.show();
                    }
                    else
                    {
                        //Abre ventana de Ajustes para aceptar manuelamente los permisos
                        AlertDialog alert = Alerts.crearDialogoPermisos(this, "Has rechazado algunos permisos. permite todos los permisos en [Ajustes] > [Permisos]",
                                "Ir a ajustes", "No, Cerrar aplicacion",
                                (dialogInterface, i) ->
                                {
                                    dialogInterface.dismiss();
                                    Intent ajustes = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                            Uri.fromParts("package", getPackageName(), null));
                                    ajustes.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivityForResult(ajustes, REQUEST_SETTINGS);
                                },
                                (dialogInterface, i) ->
                                {
                                    dialogInterface.dismiss();
                                    this.finish();
                                });
                        alert.show();
                    }
                }
            }
            else
            {
                //TODO agregar location aqui
                mCliente.getLastLocation().addOnSuccessListener(MainActivity.this, location ->
                {
                    Log.wtf("Location",location.toString());
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_SETTINGS) {
            if (resultCode == RESULT_OK) {

            } else {
                checkAndRequirePermissions();
            }
        }
    }
}
