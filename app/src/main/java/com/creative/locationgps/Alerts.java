package com.creative.locationgps;

import android.content.Context;
import android.content.DialogInterface;

import androidx.appcompat.app.AlertDialog;

public class Alerts
{
    public static AlertDialog crearDialogoPermisos(Context context, String mensaje, String positivo, String negativo,
                                                   DialogInterface.OnClickListener listenerPositivo,
                                                   DialogInterface.OnClickListener listenerNegativo)
    {
        AlertDialog dialog = new AlertDialog
                .Builder(context)
                .setMessage(mensaje)
                .setPositiveButton(positivo,listenerPositivo)
                .setNegativeButton(negativo, listenerNegativo)
                .create();

        return dialog;
    }
}
