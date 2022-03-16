package schulzUndWitzelGbR.App.saufio;

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
import static com.android.billingclient.api.BillingClient.SkuType.INAPP;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.saufio.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements PurchasesUpdatedListener {
    public static int minvalueshots=1;
    public static int maxvalueshots=5;
    private BillingClient billingClient;
    //public static final String PURCHASE_KEY= "hure";
    public static final String PRODUCT_ID= "sport";
    Button btn_start, btn_add;
    //EditText
    ImageButton btn_info_rundenbasis, btn_info_zufall;
    EditText tp_spieler, et_sucht;
    //TextView
    TextView tv_spieler1;
    //ArrayListe
    ArrayList<String> Spieler = new ArrayList<>();
    String[] kategorie = new ArrayList<>().toArray(new String[0]);
    //Strings
    String Spielertxt;
    //int
    int numberpicker = 1;
    //boolean
    boolean rewardw;
    //edittextplan
    //checkbox
    CheckBox cb_kinder;
    //erstellen Objekte
    DatabaseHandler db = new DatabaseHandler(this);
    ListView lv_kategorie;
    Switch swch_zufall;

    //Options Menü (3 Punkte)




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Menü welcher Button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.Einstellungen:
                startActivity(new Intent(MainActivity.this, Einstellungen.class));
                return true;
            case R.id.Credits:
                startActivity(new Intent(MainActivity.this, Credits.class));
                return true;
            case R.id.Aufgabe:
                startActivity(new Intent(MainActivity.this, ac_aufgaben.class));
                return true;
            //case R.id.Shop:
              //  startActivity(new Intent(MainActivity.this, Shop.class));
            case R.id.Sucht:
                startActivity(new Intent(MainActivity.this, sucht.class));
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Beenden der App mit Zurücktaste
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.finishAndRemoveTask();
        }
        this.finishAffinity();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkNight();
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if(getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        DatabaseHandler d = new DatabaseHandler(this);
        if (db.getLastID() == 0) {
            Aufgabe.getAddGameaufgabe(this);
            Intent i = new Intent(MainActivity.this, MainActivity.class);
        }

        kategorie=db.getKategorien().toArray(new String[0]);
        Allgemein.checkInternetPermission(this);
        btn_start=(Button)findViewById(R.id.btn_zumSpiel);
        btn_add=(Button)findViewById(R.id.btn_add);
        et_sucht=(EditText)findViewById(R.id.etn_sucht);
        et_sucht.setVisibility(View.INVISIBLE);
        tp_spieler = (EditText) findViewById(R.id.tp_spieler);
        tv_spieler1 = (TextView) findViewById(R.id.tv_Spieler);
        lv_kategorie = (ListView) findViewById(R.id.lv_kategorie);
        Spielertxt = getResources().getString(R.string.spielernamen);
        swch_zufall = (Switch)findViewById(R.id.swch_zufall);
        cb_kinder = (CheckBox)findViewById(R.id.cb_kinder);
        btn_info_rundenbasis = (ImageButton) findViewById(R.id.rundenbasis_info);
        btn_info_zufall = (ImageButton) findViewById(R.id.zufall_info);
        //erstellen Numberpicker
        NumberPicker np = findViewById(R.id.numberPicker);
        np.setMinValue(minvalueshots);
        np.setMaxValue(maxvalueshots);
        np.setOnValueChangedListener(onValueChangeListener);
        //ListView erstell
        lv_kategorie.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        if (Allgemein.gebeBoolean(getApplication(),Allgemein.KEY_ENTF_AD)==false){
            //btn_add.setVisibility(View.INVISIBLE);
        }

        cb_kinder.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    et_sucht.setVisibility(View.VISIBLE);
                    btn_info_rundenbasis.setVisibility(View.INVISIBLE);
                    Log.i("Cbox",String.valueOf(isChecked));
                }
                else {
                    et_sucht.setVisibility(View.INVISIBLE);
                    btn_info_rundenbasis.setVisibility(View.VISIBLE);
                    Log.i("Cbox",String.valueOf(isChecked));
                }
            }
        });

                // Todo Pop-Up für Rundenbasis erstellen und Pop-Up Zufall Button erstellen


        final String[] kategroie = getResources().getStringArray(R.array.regeln);
        lv_kategorie.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CheckedTextView v = (CheckedTextView) view;
                String user = (String) lv_kategorie.getItemAtPosition(position);
            }
        });
        final Context c = this;
        lv_kategorie.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                List<String> aufgabeKategorie = db.aufgabeKategorie("'"+kategorie[pos]+"'");
                    Allgemein.alertOK(c,kategorie[pos],kategroie[pos]+"\n\n"+getResources().getString(R.string.regeln_rundenzahl) +aufgabeKategorie.size(), getResources().getString(R.string.ok));
                return true;
            }
        });
        buttonton();

        //Kategorien anzeigen lassen
        initListViewKategorie();
        //todo Appname im String anpassen
        Allgemein.alertOK(this,getResources().getString(R.string.alert_bewusstes_saufen_title),getResources().getString(R.string.alert_bewusstes_saufen_message),getResources().getString(R.string.verstanden));

        // Establish connection to billing client
        //check purchase status from google play store cache
        //to check if item already Purchased previously or refunded
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
                        //setze App buy auf bestätigt
                        Allgemein.setzteBoolean(getApplicationContext(),Allgemein.KAT_BUY_1);
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
            }
        });

    }


    //Button zum Spiel
    public void btn_zumSpiel(View view) {
        if (Spieler.size() >= 2) {
            Intent i = new Intent(MainActivity.this, Hauptspiel.class);
            i.putExtra("key", Spieler);
            i.putExtra("key2", numberpicker);
            i.putExtra("key4",swch_zufall.isChecked());
            i.putExtra("key5",true);
            int rundeanzahlx;
            if (cb_kinder.isChecked()) {
                try {
                    rundeanzahlx = Integer.parseInt(et_sucht.getText().toString());
                } catch (Exception e){
                    rundeanzahlx = 2147483647;
                }
            } else{
                rundeanzahlx = 2147483645;
            }
        i.putExtra("key6",rundeanzahlx);
            String s = gebeKategorie();//.substring(0,gebeKategorie().length()-1);
            if (s !="0"){
                i.putExtra("key3",s);
                if(s.matches(".*"+getResources().getString(R.string.kategroie1)+".*")){
                    alertRegeln(i,this, getResources().getString(R.string.alter_kategorie_title1), getResources().getString(R.string.alter_kategorie_message1), getResources().getString(R.string.verstanden));
                } else {
                startActivity(i);
                }
            }
            else {
                Allgemein.alertOK(this,getResources().getString(R.string.alter_kategorie_title),getResources().getString(R.string.alter_kategorie_message),getResources().getString(R.string.ok));
            }
        }
        //wenn zu wenig Spieler
        else {
            Allgemein.alertOK(this, getResources().getString(R.string.alter_spieler_title), getResources().getString(R.string.alter_spieler_message), getResources().getString(R.string.ok));
        }
    }

    //Spieler hinzufügen
    public void btn_add(View view) throws InterruptedException {
        if (tp_spieler.getText().length() != 0) {
            String spielerstring = tp_spieler.getText().toString();
            if (spielerstring.length()>=20){
                Toast x = Toast.makeText(getApplicationContext(), tp_spieler.getText().toString() + getResources().getString(R.string.add_spieler_fehler_laenge), Toast.LENGTH_SHORT);
                x.setGravity(Gravity.BOTTOM, 0, 0);
                x.show();
            }
            if (Spieler.contains(spielerstring)){
                Toast x = Toast.makeText(getApplicationContext(), tp_spieler.getText().toString() + getResources().getString(R.string.add_spieler_fehler), Toast.LENGTH_SHORT);
                x.setGravity(Gravity.BOTTOM, 0, 0);
                x.show();
            } else {
                Spieler.add(spielerstring);
                Spielertxt = Spielertxt + " " + tp_spieler.getText().toString() + ",";
                tv_spieler1.setText(Spielertxt.toString().substring(0, Spielertxt.length() - 1));
                tp_spieler.setText("");
                // Verstecke Keybord
                InputMethodManager keybord = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                keybord.hideSoftInputFromWindow(tp_spieler.getWindowToken(), 0);
                Toast x = Toast.makeText(getApplicationContext(), tp_spieler.getText().toString() + " " + getResources().getString(R.string.t_add), Toast.LENGTH_SHORT);
                tp_spieler.setText("");
                x.setGravity(Gravity.BOTTOM, 0, 0);
                x.show();
            }
        } else {
            Allgemein.alertOK(this, getResources().getString(R.string.alter_spielername_title), getResources().getString(R.string.alter_spielername_message), getResources().getString(R.string.ok));
        }
    }

    //Numberpiocker Change
    NumberPicker.OnValueChangeListener onValueChangeListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker numberPicker, int i, int i1) {
            numberpicker = numberPicker.getValue();
        }
    };



    //Listview(Kategorien) initialsieren
    private void initListViewKategorie() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_checked, kategorie);
        lv_kategorie.setAdapter(arrayAdapter);
        for (int i = 0; i < kategorie.length; i++) {
            lv_kategorie.setItemChecked(i, false);
        }
    }

    //Übergabe für next Activity
    private String gebeKategorie(){
    SparseBooleanArray sp = lv_kategorie.getCheckedItemPositions();
    StringBuilder sb= new StringBuilder();
        for(int i=0;i<sp.size();i++){
        if(sp.valueAt(i)==true){
            sb.append("'");
            String s = ((CheckedTextView) lv_kategorie.getChildAt(i)).getText().toString();
            sb = sb.append(s+"',");
            }
        }
        if (sb.length()==0){
            return "0";
        }
        else{
            return sb.toString().substring(0, sb.length() - 1);
        }
    }

    //setzte Ton
    private void buttonton(){
        Boolean ton = Allgemein.gebeBoolean(this,Allgemein.KEY_TON);
        btn_start.setSoundEffectsEnabled(ton);
        btn_add.setSoundEffectsEnabled(ton);
        tp_spieler.setSoundEffectsEnabled(ton);
        lv_kategorie.setSoundEffectsEnabled(ton);
        swch_zufall.setSoundEffectsEnabled(ton);
    }

    //Alert Regeln
    private void alertRegeln(final Intent y, Context c, String title, String message, String button) {
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(c);
        alterDialog.setTitle(title);
        alterDialog.setMessage(message);
        alterDialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                startActivity(y);
            }
        });
        alterDialog.create();
        alterDialog.show();
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
                        Toast.makeText(getApplicationContext(),"Error1 "+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
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
                                billingClient.launchBillingFlow(MainActivity.this, flowParams);
                            }
                            else{
                                //try to add item/product id "purchase" inside managed product in google play console
                                Toast.makeText(getApplicationContext(),"Purchase Item not Found",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    " Error2 "+billingResult.getDebugMessage(), Toast.LENGTH_SHORT).show();
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
                    if (Allgemein.gebeBoolean(this,Allgemein.KAT_BUY_1)){
                        Allgemein.setzteBoolean(getApplicationContext(),Allgemein.KAT_BUY_1);
                    }
                    // Grant entitlement to the user on item purchase
                    // restart activity
                    //this.recreate();
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
                Allgemein.setzteBoolean(getApplicationContext(),Allgemein.KAT_BUY_1);
                Toast.makeText(getApplicationContext(), "Item Purchased", Toast.LENGTH_SHORT).show();
                MainActivity.this.recreate();
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(billingClient!=null){
            billingClient.endConnection();
        }
    }

    //NightMode
    public void checkNight(){
        Allgemein.checkNightModeActivated(getApplicationContext());
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}