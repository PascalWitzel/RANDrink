package schulzUndWitzelGbR.App.saufio;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;


import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.example.saufio.BuildConfig;
import com.example.saufio.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.android.billingclient.api.BillingClient.SkuType.INAPP;

public class Hauptspiel extends AppCompatActivity implements PurchasesUpdatedListener {

    //region Variablen
    //Werbung
    RewardedVideoAd ad;
    //Video
    private VideoView videoView;
    private MediaController mediaController;
    //Background
    ImageView img_bck, img_bck02, img_ende;
    //Button
    Button btn_zufall;
    //ImageButton
    ImageButton btnimg_ton, btnimg_zahl, btnimg_kopf, btn_share, imgbtn_info, imgbtn_rewardw;
    //TextView
    TextView tv_spieler1111, tv_aufgabe, tv_aufgabenzahl, tv_kopfzahl;
    //Switch
    Switch videoanaus;
    //INT
    int sicherung, zufallszahl, zufallspieler, max_shots, anzahl_kopf = 0, anzahl_zahl = 0, anzahl_aufgaben = 0, wahl, reihnfolge, runde, bgnzahl, bgnzufall, werbungzahlgebe, werbungwann, werbungmax = 22, werbungmin = 15,halbzeit;
    //double
    double prozentsatz = 1.10, anzahlrunde = 10;
    //Strings
    String spieler, aufgabetxt;
    //Boolean
    boolean folge, background, alertabspielen = false, isLoading;
    //Array
    ArrayList<String> Spieler;
    ArrayList<String> aufgabelist;
    //Aufgabe
    Aufgabe a;
    //Zufallgenerator
    Random r = new java.util.Random();
    //TextToSpeech
    TextToSpeech textspeech;
    //endregion
    Werbung w;
    //rewardedad
    RewardedAd rewardedAd;
    //decimalformat
    DecimalFormat df = new DecimalFormat("##");
    public static final String PRODUCT_ID= "entf_ad";
    private BillingClient billingClient;


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if(getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        setContentView(R.layout.activity_hauptspiel);
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadRewardedAd();
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tv_spieler1111 = (TextView) findViewById(R.id.tv_spieleranzeige);
        tv_kopfzahl = (TextView) findViewById(R.id.tv_kopfzahl);
        tv_aufgabe = (TextView) findViewById(R.id.tv_aufgabe);
        tv_aufgabenzahl = (TextView) findViewById(R.id.tv_aufgabenvorhanden);
        btnimg_ton = (ImageButton) findViewById(R.id.btn_ton);
        btnimg_zahl = (ImageButton) findViewById(R.id.btn_zahl);
        btnimg_kopf = (ImageButton) findViewById(R.id.btn_kopf);
        videoView = (VideoView) findViewById(R.id.videoBB);
        btnimg_kopf = (ImageButton) findViewById(R.id.btn_kopf);
        btnimg_zahl = (ImageButton) findViewById(R.id.btn_zahl);
        imgbtn_info = (ImageButton) findViewById(R.id.img_info);
        btn_zufall = (Button) findViewById(R.id.btn_zufall);
        btn_share = (ImageButton) findViewById(R.id.imgBtn_share);
        img_bck = (ImageView) findViewById(R.id.img_background);
        img_bck02 = (ImageView) findViewById(R.id.img_background02);
        videoanaus = (Switch) findViewById(R.id.switch_videoan);
        imgbtn_rewardw=(ImageButton)findViewById(R.id.imgbtn_rewardw);
        if (!Allgemein.gebeBoolean(getApplication(),Allgemein.KEY_ENTF_AD)){
            imgbtn_rewardw.setVisibility(View.INVISIBLE);
        }

        videoanaus.setChecked(Allgemein.gebeBoolean(this, Allgemein.KEY_VIDEO));
        videoanaus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Allgemein.setzteBoolean(getApplicationContext(), Allgemein.KEY_VIDEO);
            }
        });
        img_ende = (ImageView) findViewById(R.id.video_ende);
        Spieler = (ArrayList<String>) getIntent().getSerializableExtra("key");
        max_shots = getIntent().getIntExtra("key2", 1);
        background = true;
        bgnzahl = 0;
        bgnzufall = r.nextInt(6) + 3;
        //Aufgabe vorlesen
//        w.rewardwerbung();
        textspeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textspeech.setLanguage(Locale.GERMANY);
                }
            }
        });
        aufgabelist = (ArrayList<String>) db.aufgabeKategorie(getIntent().getStringExtra("key3"));
        halbzeit=aufgabelist.size()/2;
        folge = getIntent().getBooleanExtra("key4", false);
        Spielerauswaehlen();
        tv_aufgabenzahl.setText(getResources().getString(R.string.aufgabevorhanden) + aufgabelist.size());
        reihnfolge = r.nextInt(Spieler.size());
        setzeTonbild(Allgemein.gebeBoolean(this, Allgemein.KEY_TON));
        einblenden_k_o_z();
        //starteWerbetimer();
        videoView = findViewById(R.id.videoBB);
        videoView.setVisibility(View.INVISIBLE);
        mediaController = new MediaController(this);
        a = new Aufgabe(1, "0", "J", "0");
        buttonton();
        werbungwann = r.nextInt(werbungmax) + werbungmin;
        w = new Werbung(this, false);
        w.setAngeschaut(getIntent().getBooleanExtra("key5", false));
        ad = w.rewardwerbung();
        sicherung= getIntent().getIntExtra("key6", 2147483647);
        billingClient = BillingClient.newBuilder(this)
                .enablePendingPurchases().setListener(this).build();
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                    Purchase.PurchasesResult queryPurchase = billingClient.queryPurchases(INAPP);
                    List<Purchase> queryPurchases = queryPurchase.getPurchasesList();
                    if(queryPurchases!=null && queryPurchases.size()>0){
                        handlePurchases(queryPurchases);
                    }
                    //if purchase list is empty that means item is not purchased
                    //Or purchase is refunded or canceled
                    else{
                        Allgemein.setzteBoolean(getApplicationContext(),Allgemein.KEY_ENTF_AD);
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
            }
        });


    }
    DatabaseHandler db = new DatabaseHandler(this);

    //Zurücktaste
    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        statiskUndEnde(false);
                        break;
                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.alter_spielende_title));
        builder.setMessage(getResources().getString(R.string.alter_spielende_title)).setPositiveButton(getResources().getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getResources().getString(R.string.no), dialogClickListener).show();
    }

    @Override
    public void onResume() {
        ad.resume(this);
        super.onResume();
    }

    @Override
    public void onPause() {
//        if (textspeech != null) {
//            textspeech.stop();
//            textspeech.shutdown();
//        }
        ad.pause(this);
        super.onPause();
    }


    //region Buttons
    //Buttons Kopf u. Zahl
    public void btnimg_zahl(View view) {
        video_o_Bild(0);
    }

    public void btnimg_kopf(View view) {
        video_o_Bild(1);
    }

    //Zufallspieler
    public void btn_zufall(View view) {
        Spielerauswaehlen();
        einblenden_k_o_z();
    }

    //Spieler löschen
    public void btn_spielerloeschen(View view) {
        Spieler.add(getResources().getString(R.string.spieleradd));
        CharSequence spieler[] = Spieler.toArray(new CharSequence[Spieler.size()]);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.alter_spielerfrage));
        builder.setItems(spieler, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (Spieler.size() - 1 == which) {
                    Spieler.remove(Spieler.size() - 1);
                    Spieleradd();
                } else if (Spieler.size() > 3) {
                    Toast.makeText(getApplicationContext(), Spieler.get(which) + " " + getResources().getString(R.string.t_loschen), Toast.LENGTH_SHORT).show();
                    Spieler.remove(which);
                    Spieler.remove(Spieler.size() - 1);
                } else {
                    Spieler.remove(Spieler.size() - 1);
                    wenigSpieler();
                }
                tv_aufgabe.setText(String.valueOf(which));
            }
        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                Spieler.remove(Spieler.size() - 1);
            }
        });
        builder.show();
    }

    //Button Share
    public void btnimg_share(View view) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject));
            String shareMessage = "\n" + getResources().getString(R.string.share_Text) + "\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID + "\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_auswaehlen)));
        } catch (Exception e) {
        }
    }

    //Ton aktivieren
    public void btn_tonsetzen(View view) {
        setzeTonbild(Allgemein.gebeBoolean(this, Allgemein.KEY_TON));
        buttonton();
    }

    public void btn_info(View view) {
        String txt;
        txt = aufgabetxt.replace(getResources().getString(R.string.einzelsschluck), "$");
        for (int i = 0; i < max_shots + 1; i++) {
            txt = txt.replace((i) + " " + getResources().getString(R.string.mehrzahlschluck), "$");
        }
        Aufgabe a = db.sucheUeberTxt(txt);
        if (a.getKategorie().equals(getResources().getString(R.string.kategroie1))) {
            Allgemein.alertOK(this, getResources().getString(R.string.alter_kategorie_title1), getResources().getString(R.string.alter_kategorie_message1), getResources().getString(R.string.verstanden));
        }
        else if (a.getKategorie().equals(getResources().getString(R.string.kategorie2))) {
            Allgemein.alertOK(this, getResources().getString(R.string.alter_kategorie_title2), getResources().getString(R.string.alter_kategorie_message2), getResources().getString(R.string.verstanden));
        }
        else if (a.getKategorie().equals(getResources().getString(R.string.kategorie3))) {
            Allgemein.alertOK(this, getResources().getString(R.string.alter_kategorie_title3), getResources().getString(R.string.alter_kategorie_message3), getResources().getString(R.string.verstanden));
        }
        else if (a.getKategorie().equals(getResources().getString(R.string.kategorie4))) {
            Allgemein.alertOK(this, getResources().getString(R.string.alter_kategorie_title4), getResources().getString(R.string.alter_kategorie_message4), getResources().getString(R.string.verstanden));
        }
        
    }

    public void btn_werbung(View view) {
        purchase();
    }
    //endregion

    //Eigene Methoden
    //region Nicht zuordbar
    //Suche einen Zufälligen  Spieler
    public void Spielerauswaehlen() {
        if (aufgabelist.size() == 0) {
            AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
            alterDialog.setTitle(getResources().getString(R.string.keineaufgabe_title));
            alterDialog.setMessage(getResources().getString(R.string.keineaufgabe_message));
            alterDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Hauptspiel.this, MainActivity.class));
                }
            });
            alterDialog.create();
            alterDialog.show();
        }
        //a.setKategorie("0");
        if (folge) {
            zufallspieler = r.nextInt(Spieler.size());
            spieler = Spieler.get(zufallspieler);
        } else {
            spieler = Spieler.get(reihnfolge);
            if (reihnfolge == Spieler.size() - 1) {
                reihnfolge = 0;
                runde = runde + 1;
            } else {
                reihnfolge = reihnfolge + 1;
            }
        }
        tv_spieler1111.setText(spieler);
        tv_kopfzahl.setText(getResources().getString(R.string.entscheidung));
    }

    //ermittle welches Ergebnis
    public void kopf_o_zahl_video(int wert) {
        gebezufall();
        zufallszahl = r.nextInt(2);
        wahl = wert;
        if (zufallszahl == 0) {
            if (zufallszahl != wahl)
            tv_spieler1111.setText(spieler);
            tv_kopfzahl.setText(getResources().getString(R.string.wartenvid));
            playVideo(zufallszahl);
            Timer t = new Timer(false);
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (zufallszahl == wahl) {
                                tv_spieler1111.setText(spieler);
                                tv_kopfzahl.setText(getResources().getString(R.string.glueck_zahl));
                                anzahl_zahl = anzahl_zahl + 1;
                                btn_zufall.setVisibility(View.VISIBLE);
                                imgbtn_info.setVisibility(View.INVISIBLE);
                            } else {
                                tv_spieler1111.setText(spieler);
                                tv_kopfzahl.setText(getResources().getString(R.string.pech_kopf));
                                tv_aufgabenzahl.setText(getResources().getString(R.string.aufgabevorhanden) + aufgabelist.size());
                                tv_aufgabe.setText(aufgabetxt);
                                sprachausgabe(aufgabetxt);
                                anzahl_kopf = anzahl_kopf + 1;
                                anzahl_aufgaben = anzahl_aufgaben + 1;
                                btn_zufall.setVisibility(View.VISIBLE);
                                imgbtn_info.setVisibility(View.VISIBLE);
                                gebeWerbung_o_alert();
                            }
                        }
                    });
                }
            }, 2500);     //DELAY
        } else {
            tv_kopfzahl.setText(getResources().getString(R.string.wartenvid));
            playVideo(zufallszahl);
            if (zufallszahl != wahl) {
                aufgabetxt = Aufgabe.getAufgabe(this, aufgabelist);
                aufgabetxt = Allgemein.ersetztenSchluck(this, aufgabetxt, max_shots);
            }
            Timer t = new Timer(false);
            t.schedule(new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (zufallszahl == wahl) {
                                tv_spieler1111.setText(spieler);
                                tv_kopfzahl.setText(getResources().getString(R.string.glueck_zahl));
                                anzahl_kopf = anzahl_kopf + 1;
                                btn_zufall.setVisibility(View.VISIBLE);
                                imgbtn_info.setVisibility(View.INVISIBLE);
                            } else {
                                tv_spieler1111.setText(spieler);
                                tv_kopfzahl.setText(getResources().getString(R.string.pech_kopf));
                                tv_aufgabenzahl.setText(getResources().getString(R.string.aufgabevorhanden) + aufgabelist.size());
                                tv_aufgabe.setText(aufgabetxt);
                                sprachausgabe(aufgabetxt);
                                anzahl_zahl = anzahl_zahl + 1;
                                anzahl_aufgaben = anzahl_aufgaben + 1;
                                btn_zufall.setVisibility(View.VISIBLE);
                                imgbtn_info.setVisibility(View.VISIBLE);
                                gebeWerbung_o_alert();
                            }
                        }
                    });
                }
            }, 2500);  //DELAY
        }
        ausblenden_k_o_z();
    }

    public void video_o_Bild(int wert) {
        if (videoanaus.isChecked()) {
            kopf_o_zahl_video(wert);
        } else {
            kopf_o_zahl_bild(wert);
        }
    }

    //ermittle welches Ergebnis
    public void kopf_o_zahl_bild(int wert) {
        gebezufall();
        zufallszahl = r.nextInt(2);
        wahl = wert;
        if (zufallszahl == 0) {
            if (zufallszahl != wahl) {
                aufgabetxt = Aufgabe.getAufgabe(this, aufgabelist);
                aufgabetxt = Allgemein.ersetztenSchluck(this, aufgabetxt, max_shots);
            }
            tv_spieler1111.setText(spieler);
            tv_kopfzahl.setText(getResources().getString(R.string.wartenvid));
            if (zufallszahl == wahl) {
                tv_spieler1111.setText(spieler);
                tv_kopfzahl.setText(getResources().getString(R.string.glueck_zahl));
                img_ende.setImageResource(R.drawable.pixel_zahl);
                img_ende.setVisibility(View.VISIBLE);
                anzahl_zahl = anzahl_zahl + 1;
                btn_zufall.setVisibility(View.VISIBLE);
                imgbtn_info.setVisibility(View.INVISIBLE);
            } else {
                tv_spieler1111.setText(spieler);
                tv_kopfzahl.setText(getResources().getString(R.string.pech_kopf));
                img_ende.setImageResource(R.drawable.pixel_zahl);
                img_ende.setVisibility(View.VISIBLE);
                tv_aufgabenzahl.setText(getResources().getString(R.string.aufgabevorhanden) + aufgabelist.size());
                tv_aufgabe.setText(aufgabetxt);
                sprachausgabe(aufgabetxt);
                anzahl_kopf = anzahl_kopf + 1;
                anzahl_aufgaben = anzahl_aufgaben + 1;
                btn_zufall.setVisibility(View.VISIBLE);
                imgbtn_info.setVisibility(View.VISIBLE);
                gebeWerbung_o_alert();
            }
        } else {
            tv_kopfzahl.setText(getResources().getString(R.string.wartenvid));
            if (zufallszahl != wahl) {
                aufgabetxt = Aufgabe.getAufgabe(this, aufgabelist);
                aufgabetxt = Allgemein.ersetztenSchluck(this, aufgabetxt, max_shots);
            }
            if (zufallszahl == wahl) {
                tv_spieler1111.setText(spieler);
                tv_kopfzahl.setText(getResources().getString(R.string.glueck_zahl));
                img_ende.setImageResource(R.drawable.pixel_muenze);
                img_ende.setVisibility(View.VISIBLE);
                anzahl_kopf = anzahl_kopf + 1;
                btn_zufall.setVisibility(View.VISIBLE);
                imgbtn_info.setVisibility(View.INVISIBLE);
            } else {
                tv_spieler1111.setText(spieler);
                tv_kopfzahl.setText(getResources().getString(R.string.pech_kopf));
                img_ende.setImageResource(R.drawable.pixel_muenze);
                img_ende.setVisibility(View.VISIBLE);
                tv_aufgabenzahl.setText(getResources().getString(R.string.aufgabevorhanden) + aufgabelist.size());
                tv_aufgabe.setText(aufgabetxt);
                sprachausgabe(aufgabetxt);
                anzahl_zahl = anzahl_zahl + 1;
                anzahl_aufgaben = anzahl_aufgaben + 1;
                btn_zufall.setVisibility(View.VISIBLE);
                imgbtn_info.setVisibility(View.VISIBLE);
                gebeWerbung_o_alert();
            }
        }
        ausblenden_k_o_z();
    }

    //Alert wenn zu Wenig Spieler bei löschen
    public void wenigSpieler() {
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
        alterDialog.setTitle(getResources().getString(R.string.alter_spielerloeschen_title));
        alterDialog.setMessage(getResources().getString(R.string.alter_spieleerloeschen_message));
        alterDialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                onBackPressed();
            }
        });
        alterDialog.create();
        alterDialog.show();
    }

    //Endstatistik
    public void statiskUndEnde(boolean runde1) {
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
        alterDialog.setTitle(getResources().getString(R.string.alter_statistik_title));
        String x="";
        if (runde1) {
            x = "Aufgrund eurer Anzahl an Spielrunden ist das Spiel nun zu Ende \n\n";
        }
        x = x + getResources().getString(R.string.alter_statistik_message_gesamt) + String.valueOf(anzahl_kopf + anzahl_zahl) + "\n" +
                getResources().getString(R.string.alter_statistik_message_kopf) + String.valueOf(anzahl_kopf) + "\n" +
                getResources().getString(R.string.alter_statistik_message_zahl) + String.valueOf(anzahl_zahl) + "\n" +
                getResources().getString(R.string.alter_statistik_message_runden) + String.valueOf(anzahl_aufgaben);
        if (!folge) {
            x = x + "\n" + getResources().getString(R.string.alter_statistik_message_durchlauf) + String.valueOf(runde);
        }
        alterDialog.setMessage(x);
        alterDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Hauptspiel.this, MainActivity.class);
                startActivity(intent);
            }
        });
        alterDialog.create();
        Allgemein.setzeString(this, Allgemein.KEY_SPEICHERRUNDEN, "0");
        alterDialog.show();
    }

    //Spieler nachträglich hinzufügen
    public void Spieleradd() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.alter_spielerhinzufügen_title));
        builder.setMessage(getResources().getString(R.string.alter_spielerhinzufügen_message));
        final EditText input = new EditText(this);
        input.setSoundEffectsEnabled(Allgemein.gebeBoolean(this, Allgemein.KEY_TON));
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_DATETIME_VARIATION_NORMAL);
        builder.setView(input);
        builder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().length()<=20) {
                    Spieler.add(input.getText().toString());
                } else {
                    Toast x = Toast.makeText(getApplicationContext(), input.getText().toString() + getResources().getString(R.string.add_spieler_fehler), Toast.LENGTH_SHORT);
                    x.setGravity(Gravity.BOTTOM, 0, 0);
                    x.show();
                }
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.abbrechen), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    //endregion

    //region einblenen/ausblenden
    //Ausblenden von Kopf und Zahl Buttons -> Zufallspieler wird angezeigt
    public void ausblenden_k_o_z() {
        btnimg_zahl.setVisibility(View.INVISIBLE);
        btnimg_kopf.setVisibility(View.INVISIBLE);
        if (sicherung==anzahl_aufgaben){
            statiskUndEnde(true);
        }
    }

    //Einblenden von Kopf und Zahl -> Zufallspieler wird nicht angezeigt
    public void einblenden_k_o_z() {
        btnimg_zahl.setVisibility(View.VISIBLE);
        btnimg_kopf.setVisibility(View.VISIBLE);
        imgbtn_info.setVisibility(View.INVISIBLE);
        btn_zufall.setVisibility(View.INVISIBLE);
        videoView.setVisibility(View.GONE);
        img_ende.setVisibility(View.INVISIBLE);
        tv_aufgabe.setText("");
    }

    //endregion

    //region Video
    //Videoabspielen
    public void playVideo(int zufall) {
        Uri uri = zufallvideo(zufall);
        videoView.setVideoURI(uri);
        videoView.setVisibility(View.VISIBLE);
        videoView.start();
    }

    public Uri zufallvideo(int koz) {
        Uri uri;
        boolean zufall = r.nextBoolean();
        //Zahl
        if (koz == 0) {
            if (zufall) {
                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.zahl_auf_zahl);
            } else {
                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bier_auf_zahl);
            }
        } else {
            //Kopf
            if (koz == 1) {
                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.bier_auf_bier);
            } else {
                uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.zahl_auf_bier);
            }
        }
        return uri;
    }
    //endregion

    //region Ton-Ausgabe/Buttons
    // Text to Speech, abbrechen bzw. Ausgabe
    public void sprachausgabe(String aufgabe) {
        if (Allgemein.gebeBoolean(this, Allgemein.KEY_TON)) {
            if (textspeech != null) {
                textspeech.stop();
            }
            textspeech.speak(aufgabe, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    //Abbrechen text2speech
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(billingClient!=null){
            billingClient.endConnection();
        }
    }

    //setzeTonBild
    public void setzeTonbild(boolean b) {
        if (!b) {
            btnimg_ton.setImageResource(R.drawable.pixel_lautsprecher_an);
        } else {
            btnimg_ton.setImageResource(R.drawable.pixel_lautsprecher_aus);
        }
    }


    //Buttonston
    public void buttonton() {
        Boolean ton = Allgemein.gebeBoolean(this, Allgemein.KEY_TON);
        btnimg_ton.setSoundEffectsEnabled(ton);
        btnimg_zahl.setSoundEffectsEnabled(ton);
        btnimg_kopf.setSoundEffectsEnabled(ton);
        btn_zufall.setSoundEffectsEnabled(ton);
        btn_share.setSoundEffectsEnabled(ton);
        imgbtn_info.setSoundEffectsEnabled(ton);
        Allgemein.setzteBoolean(this, Allgemein.KEY_TON);
        ton = Allgemein.gebeBoolean(this, Allgemein.KEY_TON);
    }
    //endregion

    //region Hintergrund
    //Hitergrundwechsel Zufall ermitteln
    public void gebezufall() {
        if (bgnzufall == bgnzahl) {
            changebground();
            bgnzufall = r.nextInt(6) + 3;
            bgnzahl = 0;
        } else {
            bgnzahl = bgnzahl + 1;
        }
    }

    //Hintergrund wechsel
    public void changebground() {
        if (background) {
            img_bck02.setAlpha(0f);
            img_bck02.setVisibility(View.VISIBLE);
            img_bck02.animate()
                    .alpha(1)
                    .setDuration(2000)
                    .setListener(null);
            background = false;
        } else {
            img_bck02.animate()
                    .alpha(0f)
                    .setDuration(2000)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            img_bck02.setVisibility(View.GONE);
                        }
                    });
            background = true;
        }
    }
    //endregion

    //region Werbung
    public void gebeWerbung_o_alert() {
        if (Allgemein.gebeBoolean(getApplication(),Allgemein.KEY_ENTF_AD)==true) {
            int wahl = r.nextInt(6) + 1;
            if (wahl == 1 && alertabspielen) {
                webefreialert();
            } else {
                werbungYN_banner();
                alertabspielen = true;
            }
        }

    }

    public void webefreialert(){
        int zurueck = (int) anzahlrunde;
        anzahlrunde =anzahlrunde*prozentsatz;
        anzahlrunde = Integer.valueOf(df.format(anzahlrunde));
        AlertDialog werbungfrei = new AlertDialog.Builder(this).create();
        werbungfrei.setTitle(getResources().getString(R.string.alert_webefrei_title));
        werbungfrei.setMessage(getResources().getString(R.string.alert_werbefrei_message1)+ " " + anzahlrunde + " " + getResources().getString(R.string.alert_werbefrei_message2));
        werbungfrei.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showRewardedVideo();
            }
        });
        werbungfrei.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        werbungfrei.show();
        anzahlrunde=zurueck;
    }

    //Werbung zufall
    public void werbungYN_banner() {
        if (aufgabelist.size() == halbzeit) {
            werbungzahlgebe = werbungzahlgebe - r.nextInt(3) + 3;
            AlertDialog halbzeit = new AlertDialog.Builder(this).create();
            halbzeit.setTitle(getResources().getString(R.string.alert_halbzeit_title));
            halbzeit.setMessage(getResources().getString(R.string.alert_halbzeit_message1) + halbzeit + getResources().getString(R.string.alert_halbzeit_message2));
            halbzeit.setButton(AlertDialog.BUTTON_POSITIVE, getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    statiskUndEnde(false);
                }
            });
            halbzeit.setButton(AlertDialog.BUTTON_NEGATIVE, getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                }
            });
        }
        if (Allgemein.gebeBoolean(this, Allgemein.KEY_WERBUNG)) {
            int wert;
            try {
                wert = Integer.valueOf(Allgemein.gebeString(getApplicationContext(), Allgemein.KEY_SPEICHERRUNDEN));
            } catch (Exception e) {
                wert = 0;
            }
            if (wert == 0) {

                if (werbungzahlgebe == werbungwann) {
                    w.klick_werbung();
                    werbungmax = r.nextInt(werbungmax) + werbungmin;
                    werbungzahlgebe = 0;
                } else {
                    werbungzahlgebe++;
                    Log.i("Werbung", String.valueOf(werbungmax) + " " + String.valueOf(werbungzahlgebe) + " " + werbungwann);
                }
            } else {
                wert = wert - 1;
                Allgemein.setzeString(this, Allgemein.KEY_SPEICHERRUNDEN, String.valueOf(wert));
            }
        }
    }

    //loadwerbung

    private void loadRewardedAd() {
        if (!Allgemein.gebeBoolean(getApplication(),Allgemein.KEY_ENTF_AD)) {

            if (rewardedAd == null || !rewardedAd.isLoaded()) {
                rewardedAd = new RewardedAd(this, Werbung.AD_UNIT_ID);
                isLoading = true;
                rewardedAd.loadAd(
                        new AdRequest.Builder().build(),
                        new RewardedAdLoadCallback() {
                            @Override
                            public void onRewardedAdLoaded() {
                                // Ad successfully loaded.
                                Hauptspiel.this.isLoading = false;
                              Toast.makeText(Hauptspiel.this, "onRewardedAdLoaded", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                                // Ad failed to load.
                                Hauptspiel.this.isLoading = false;
                                Toast.makeText(Hauptspiel.this, "onRewardedAdFailedToLoad", Toast.LENGTH_SHORT)
                                      .show();
                            }
                        });
            }
        }
    }

    //Zeige Werbung
    private void showRewardedVideo() {
        if (rewardedAd.isLoaded()) {
            RewardedAdCallback adCallback =
                    new RewardedAdCallback() {
                        @Override
                        public void onRewardedAdOpened() {
                            // Ad opened.
                           Toast.makeText(Hauptspiel.this, "onRewardedAdOpened", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            // Ad closed.
                          //
                            //
                            //
                             Toast.makeText(Hauptspiel.this, "onRewardedAdClosed", Toast.LENGTH_SHORT).show();
                            // Preload the next video ad.
                        }

                        @Override
                        public void onUserEarnedReward(RewardItem rewardItem) {
                            // User earned reward.
                            anzahlrunde =anzahlrunde*prozentsatz;
                            anzahlrunde = Integer.valueOf(df.format(anzahlrunde));
                            int wert;
                            try {
                                wert = Integer.valueOf(Allgemein.gebeString(getApplicationContext(),Allgemein.KEY_SPEICHERRUNDEN));
                            } catch (Exception e){
                                wert=0;
                            }
                            wert = (int) (wert+anzahlrunde);
                            Allgemein.setzeString(getApplicationContext(),Allgemein.KEY_SPEICHERRUNDEN,String.valueOf(wert));
                            //String ersezten
                            Toast.makeText(Hauptspiel.this, "Belohnung erhalten", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRewardedAdFailedToShow(AdError adError) {
                            // Ad failed to display
                            //String ersezten
                          Toast.makeText(Hauptspiel.this, "Werbung kann nicht geladen werden", Toast.LENGTH_SHORT).show();
                        }
                    };
            rewardedAd.show(this, adCallback);
        }
        loadRewardedAd();
    }


    //initiate purchase on button click
    public void purchase() {
        //check if service is already connected
        if (billingClient.isReady()) {
            initiatePurchase();
        }
        //else reconnect service
        else{
            billingClient = BillingClient.newBuilder(this).enablePendingPurchases().setListener(this).build();
            billingClient.startConnection(new BillingClientStateListener() {
                @Override
                public void onBillingSetupFinished(BillingResult billingResult) {
                    if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                        initiatePurchase();
                    } else {
                     //   Toast.makeText(getApplicationContext(),"Error1 "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onBillingServiceDisconnected() {
                }
            });
        }
    }
    private void initiatePurchase() {
        List<String> skuList = new ArrayList<>();
        skuList.add(PRODUCT_ID);
        SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
        params.setSkusList(skuList).setType(INAPP);
        billingClient.querySkuDetailsAsync(params.build(),
                new SkuDetailsResponseListener() {
                    @Override
                    public void onSkuDetailsResponse(BillingResult billingResult,
                                                     List<SkuDetails> skuDetailsList) {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            if (skuDetailsList != null && skuDetailsList.size() > 0) {
                                BillingFlowParams flowParams = BillingFlowParams.newBuilder()
                                        .setSkuDetails(skuDetailsList.get(0))
                                        .build();
                                billingClient.launchBillingFlow(Hauptspiel.this, flowParams);
                            }
                            else{
                                //try to add item/product id "purchase" inside managed product in google play console
                              //  Toast.makeText(getApplicationContext(),"Purchase Item not Found",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                          //  Toast.makeText(getApplicationContext(),
                        //            " Error2 "+billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        //recreate();
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, @Nullable List<Purchase> purchases) {
        //if item newly purchased
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases);
        }
        //if item already purchased then check and reflect changes
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED) {
            Purchase.PurchasesResult queryAlreadyPurchasesResult = billingClient.queryPurchases(INAPP);
            List<Purchase> alreadyPurchases = queryAlreadyPurchasesResult.getPurchasesList();
            if(alreadyPurchases!=null){
                handlePurchases(alreadyPurchases);
            }
        }
        //if purchase cancelled
        else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            //Toast.makeText(getApplicationContext(),"Purchase Canceled",Toast.LENGTH_SHORT).show();
        }
        // Handle any other error msgs
        else {
            Toast.makeText(getApplicationContext(),"Error1"+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    void handlePurchases(List<Purchase>  purchases) {
        for(Purchase purchase:purchases) {
            //if item is purchased
            if (PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
            {
                if (!verifyValidSignature(purchase.getOriginalJson(), purchase.getSignature())) {
                    // Invalid purchase
                    // show error to user
                    Toast.makeText(getApplicationContext(), "Error6 : Invalid Purchase", Toast.LENGTH_SHORT).show();
                    return;
                }
                // else purchase is valid
                //if item is purchased and not acknowledged
                if (!purchase.isAcknowledged()) {
                    AcknowledgePurchaseParams acknowledgePurchaseParams =
                            AcknowledgePurchaseParams.newBuilder()
                                    .setPurchaseToken(purchase.getPurchaseToken())
                                    .build();
                    billingClient.acknowledgePurchase(acknowledgePurchaseParams, ackPurchase);
                }
                //else item is purchased and also acknowledged
                else {
                    if (!Allgemein.gebeBoolean(getApplicationContext(),Allgemein.KEY_ENTF_AD)==true){
                        Allgemein.setzteBoolean(getApplicationContext(),Allgemein.KEY_ENTF_AD);
                    }
                    // Grant entitlement to the user on item purchase
                    // restart activity
                    this.recreate();
                }
            }
            //if purchase is pending
            else if( PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.PENDING)
            {
                Toast.makeText(getApplicationContext(),
                        "Purchase is Pending. Please complete Transaction", Toast.LENGTH_SHORT).show();
            }
            //if purchase is unknown
            else if(PRODUCT_ID.equals(purchase.getSku()) && purchase.getPurchaseState() == Purchase.PurchaseState.UNSPECIFIED_STATE)
            {
                Allgemein.setzteBoolean(getApplicationContext(),Allgemein.KEY_ENTF_AD);
                //purchaseStatus.setText("Purchase Status : Not Purchased");
                //purchaseButton.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(), "Purchase Status Unknown", Toast.LENGTH_SHORT).show();
            }
        }
    }
    AcknowledgePurchaseResponseListener ackPurchase = new AcknowledgePurchaseResponseListener() {
        @Override
        public void onAcknowledgePurchaseResponse(BillingResult billingResult) {
            if(billingResult.getResponseCode()==BillingClient.BillingResponseCode.OK){
                //if purchase is acknowledged
                // Grant entitlement to the user. and restart activity
                Allgemein.setzteBoolean(getApplicationContext(),Allgemein.KEY_ENTF_AD);
                Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
                Hauptspiel.this.recreate();
            }
        }
    };

    /**
     * Verifies that the purchase was signed correctly for this developer's public key.
     * <p>Note: It's strongly recommended to perform such check on your backend since hackers can
     * replace this method with "constant true" if they decompile/rebuild your app.
     * </p>
     */
    private boolean verifyValidSignature(String signedData, String signature) {
        try {
            // To get key go to Developer Console > Select your app > Development Tools > Services & APIs.
            String base64Key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAg3ViocT0P/j71XtQyuMa8VDR+wGBHRF7cmeO42vto6JZB3tBEYCzkT0w5tRvHBPUodUt3EJucf39c4yusuJbKH3Zd2INQyXk3F90Ebg28ySTTusvJ8J3DMLd301QP0rhmPc9Qc92R4G79Ux/hVMbIVQJqM5DI23ClDt83bWNZNAxR0TyU8NT2vu6XPT94AAKerxvEHvWXAIySLXaMk7YZFVIbsLH/hyRUQPszjot+fXpBn4KAM+Jw5Y21ujaOpYd0VNs+b0SwS6/Hd0+jq0N4HNg5w7OIxUc6vH1A9LpreccxxKMcvFYi4tEVOKzyK4GDgOIqa5OtsNzOgeZ9pHuhwIDAQAB";
            return Security.verifyPurchase(base64Key, signedData, signature);
        } catch (IOException e) {
            return false;
        }
    }

}
