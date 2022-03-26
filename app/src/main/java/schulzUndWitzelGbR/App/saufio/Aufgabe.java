package schulzUndWitzelGbR.App.saufio;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saufio.R;

import java.util.ArrayList;
import java.util.Random;


public class Aufgabe extends AppCompatActivity{


    int id;
    String aufgabe;
    String aenderung;
    String kategorie;

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
        setContentView(R.layout.activity_ac_aufgaben);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public Aufgabe(){}
    public Aufgabe(int id, String aufgabe, String aenderung,String kategorie){
        this.id=id;
        this.aufgabe=aufgabe;
        this.aenderung=aenderung;
        this.kategorie=kategorie;
    }

    //Getter und Setter ID
    public int getId(){return this.id;}
    public void setId(int id){this.id=id;}

    //Getter und Setter aufgabe
    public String getAufgabe(){return this.aufgabe;}
    public void  setAufgabe(String aufgabe){this.aufgabe=aufgabe;
    }

    //Getter und Setter aenderung
    public String getAenderung(){return this.aenderung;}
    public void setAenderung(String aenderung){this.aenderung=aenderung;}

    //Getter und Setter Kategorie
    public String getKategorie(){return this.kategorie;}
    public void setKategorie(String kategorie){this.kategorie=kategorie;}

    //RDM aufgabetxt
    public static String getAufgabe(Context c,ArrayList<String> aufgabelist){
        String aufgabe;
        int rdm = new Random().nextInt(aufgabelist.size());
        aufgabe = aufgabelist.get(rdm);
        aufgabelist.remove(rdm);
        return aufgabe;
    }

    //funkt net - muss noch gemacht werden
    public static String gebekatsuch(Context c, String aufgabe){
        DatabaseHandler d = new DatabaseHandler(c);
        Aufgabe a = d.sucheUeberTxt(aufgabe);
        return a.getKategorie();
    }

    //Adde  Aufgaben
    public static void getAddGameaufgabe(Context c) {
        DatabaseHandler d = new DatabaseHandler(c);
        String[] aufgabe;
        Aufgabe a = new Aufgabe(1,"","J","");
        //Kategoire ich hab noch nie
        aufgabe = c.getResources().getStringArray(R.array.kategorie1_aufgaben);
        a.setKategorie(c.getResources().getString(R.string.kategroie1));

        for (int i=0; i<aufgabe.length;i++){
            a.setAufgabe(aufgabe[i]);
            d.addAufgabe(a);
        }

        //Hardocre Aufgabe 103 = 90x Aufgabe 104 10 x
        aufgabe=c.getResources().getStringArray(R.array.kategorie2_aufgaben);
        a.setKategorie(c.getResources().getString(R.string.kategorie2));
        a.setAufgabe(aufgabe[0]);
        for (int i=0;i<=90;i++) {
            d.addAufgabe(a);
        }
        a.setAufgabe(aufgabe[1]);
        for (int i=0;i<=10;i++) {
            d.addAufgabe(a);
        }


        //Kategorie gamer
        a.setKategorie(c.getResources().getString(R.string.kategorie3));
        aufgabe=c.getResources().getStringArray(R.array.kategorie3_aufgaben);
        for (int i=0; i<aufgabe.length;i++){
            a.setAufgabe(aufgabe[i]);
            d.addAufgabe(a);
        }

        a.setKategorie(c.getResources().getString(R.string.kategorie4));
        aufgabe=c.getResources().getStringArray(R.array.kategorie4_aufgaben);
        for (int i=0; i<aufgabe.length;i++){
            a.setAufgabe(aufgabe[i]);
            d.addAufgabe(a);
        }

        //Kategorie test
        a.setKategorie("0");
        a.setAufgabe("0");
        d.addAufgabe(a);


    }
}











