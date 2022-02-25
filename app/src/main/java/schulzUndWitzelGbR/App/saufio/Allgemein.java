package schulzUndWitzelGbR.App.saufio;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.saufio.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

public class Allgemein {

    static String KEY_TON = "KEY_TON";
    static String KEY_WERBUNG="KEY_WERBUNG";
    static String KEY_SPRACHE="KEY_SPRACHE";
    static String KEY_VIDEO="KEY_VIDEO";

    //In-App-Käufe
    static String KEY_ENTF_AD="KEY_ENTF_AD";
    static String KAT_BUY_1="KAT_BUY_1";


    static String KEY_SPEICHERRUNDEN;

    public static final String KEY_MyPREFERENCES = "nightModePrefs";
    public static final String KEY_ISNIGHTMODE = "isNightMode";
    static SharedPreferences prefs;
    static SharedPreferences.Editor prefsedit;


    public static void ueberallsetzen(Context c, Resources res){
        setzeSprache(c,res);
    }

    //Sprache auswählen
    public static void setzeSprache (Context c,Resources res){
        String locale;
        List<String> sprachauswahl = gebeSprachenKurz();
        String getSprache=gebeString(c,KEY_SPRACHE);
        if (getSprache=="x"){
            locale=Locale.getDefault().getLanguage();
            for (int i=0;i<sprachauswahl.size();i++){
                String x = sprachauswahl.get(i);
                if (locale.toLowerCase().equals(x)){
                    locale=x;
                    break;
                } else {
                    //standardwert
                    locale="en";
                }
            }
        } else {
            locale=getSprache;
        }
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration config = res.getConfiguration();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(new Locale(locale.toLowerCase()));
        } else {
            config.locale = new Locale(locale.toLowerCase());
        }
        res.updateConfiguration(config, dm);
    }

    public static  List<String> gebeSprachenKurz() {
        List<String> sprachauswahl = new ArrayList<>();
        //todo: Sprache adden
        sprachauswahl.add("de");
        sprachauswahl.add("en");
        sprachauswahl.add("es");
        sprachauswahl.add("fr");
        return sprachauswahl;
    }


    //Alert OK ohne weiteres auszuführen
    public static void alertOK(Context c, String title, String message, String button) {
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(c);
        alterDialog.setTitle(title);
        alterDialog.setMessage(message);
        alterDialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alterDialog.create();
        alterDialog.show();
    }

    //SharedPreferencces
    //i dont know what gebeBooleans means
    public static boolean gebeBoolean(Context c, String key){
        final SharedPreferences prefs;
        prefs = c.getSharedPreferences("Speicher", MODE_PRIVATE);
        return prefs.getBoolean(key,true);
    }

    //i dont know what gebeBooleans means maybe the opposite
    public static boolean setzteBoolean(Context c, String key) {
        prefs = c.getSharedPreferences("Speicher", MODE_PRIVATE);
        prefsedit = prefs.edit();
        if (prefs.getBoolean(key, true)) {
            prefsedit.putBoolean(key, false);
            prefsedit.commit();
        } else {
            prefsedit.putBoolean(key, true);
            prefsedit.commit();
        }
        return prefs.getBoolean(key, true);

        //prefsedit.apply();
    }

    public static String gebeString(Context c, String key){
        final SharedPreferences prefs;
        prefs=c.getSharedPreferences("Speicher",MODE_PRIVATE);
        return prefs.getString(key,"x");
    }

    public static String setzeString(Context c, String key, String wert){
        prefs=c.getSharedPreferences("Speicher", MODE_PRIVATE);
        prefsedit=prefs.edit();
        prefsedit.putString(key,wert);
        prefsedit.commit();
        return prefs.getString(key,"x");
    }

    //ersezten Schlucke
    public static String ersetztenSchluck(Context c, String aufgabe, int max_shots){
        int rdm = new Random().nextInt(max_shots)+1;
        String schlueck;
        if (rdm==1){
            schlueck=c.getResources().getString(R.string.einzelsschluck);
        }
        else {
            schlueck=rdm+" "+c.getResources().getString(R.string.mehrzahlschluck);
        }
        aufgabe=aufgabe.replace("$",schlueck);
        return aufgabe;
    }

    public static boolean checkInternetPermission(Context c){
        String permission = Manifest.permission.INTERNET;
        int res = c.checkCallingOrSelfPermission(permission);
        Log.i("Permission", String.valueOf((res == PackageManager.PERMISSION_GRANTED)));
        return (res == PackageManager.PERMISSION_GRANTED);
    }


    public static void checkNightModeActivated(Context c) {
        //getBool
        if(gebeBoolean(c ,KEY_ISNIGHTMODE)) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }
    }
}
