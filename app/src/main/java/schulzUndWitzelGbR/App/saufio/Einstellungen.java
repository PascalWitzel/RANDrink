package schulzUndWitzelGbR.App.saufio;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.saufio.R;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdCallback;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.android.billingclient.api.BillingClient.SkuType.INAPP;

public class Einstellungen extends AppCompatActivity implements PurchasesUpdatedListener {

    ImageButton ton, btn_rewardw;
    Spinner spin_sprache;
    TextView tv,tv2;
    public Switch aSwitch, videoan;
    Button  btn50, btn_save;
    private RewardedAd rewardedAd;
    boolean isLoading;
    int laden;
    int geschaut=0;
    private BillingClient billingClient;
    //public static final String PURCHASE_KEY= "hure";
    public static final String PRODUCT_ID= "entf_ad";


    @Override
    public void onBackPressed() {
        startActivity(new Intent(Einstellungen.this, MainActivity.class));
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_einstellungen);
        //Log.i("Inappbuy",String.valueOf(Allgemein.gebeBoolean(getApplication(),Allgemein.KEY_ENTF_AD)));
        if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
        if(getResources().getBoolean(R.bool.landscape_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        loadRewardedAd();



        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ton = (ImageButton) findViewById(R.id.btn_ton);
        spin_sprache=(Spinner)findViewById(R.id.spin_sprache);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.sprachen_lang));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spin_sprache.setAdapter(adapter);
        videoan = (Switch)findViewById(R.id.switch_Videoanaus);
        videoan.setChecked(Allgemein.gebeBoolean(this,Allgemein.KEY_VIDEO));
        videoan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Allgemein.setzteBoolean(getApplicationContext(),Allgemein.KEY_VIDEO);
            }});
        aSwitch = findViewById(R.id.switch1);
        aSwitch.setChecked(!Allgemein.gebeBoolean(this,Allgemein.KEY_ISNIGHTMODE));
        aSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(Allgemein.gebeBoolean(getApplicationContext(),Allgemein.KEY_ISNIGHTMODE)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
                Allgemein.setzteBoolean(getApplicationContext(), Allgemein.KEY_ISNIGHTMODE);
                finish();
                startActivity(new Intent(Einstellungen.this,Einstellungen.class));
                overridePendingTransition(0,0);
            }
        });

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
                        Allgemein.setzteBoolean(getApplicationContext(),Allgemein.KAT_BUY_1);
                    }
                }
            }

            @Override
            public void onBillingServiceDisconnected() {
            }
        });
        btn_rewardw = (ImageButton) findViewById(R.id.imgbtn_rewardw);
        btn50 = (Button)findViewById(R.id.btn_50);
        tv2 = (TextView)findViewById(R.id.textView6);
        tv = (TextView)findViewById(R.id.textView7);
        btn_save=(Button)findViewById(R.id.btn_sprache_save);
        if (Allgemein.gebeBoolean(getApplicationContext(),Allgemein.KEY_ENTF_AD)){
            btn_rewardw.setVisibility(View.VISIBLE);
            btn50.setVisibility(View.VISIBLE);
        } else {
            btn_rewardw.setVisibility(View.INVISIBLE);
            btn50.setVisibility(View.INVISIBLE);
            tv.setText("Du hast dich von der lästigen Werbung befreit, die nächste Runde geht auf uns");
            tv2.setVisibility(View.INVISIBLE);
        }
        setzeTonbild();

    }



    //setze TonBild
    public void setzeTonbild() {
        if (Allgemein.gebeBoolean(this,Allgemein.KEY_TON)) {
            ton.setImageResource(R.drawable.pixel_lautsprecher_an);
            btn50.setSoundEffectsEnabled(true);
            btn_rewardw.setSoundEffectsEnabled(true);
            spin_sprache.setSoundEffectsEnabled(true);
            btn_save.setSoundEffectsEnabled(true);
            ton.setSoundEffectsEnabled(true);
            aSwitch.setSoundEffectsEnabled(true);
            videoan.setSoundEffectsEnabled(true);

        } else {
            ton.setImageResource(R.drawable.pixel_lautsprecher_aus);
            btn50.setSoundEffectsEnabled(false);
            btn_rewardw.setSoundEffectsEnabled(false);
            spin_sprache.setSoundEffectsEnabled(false);
            btn_save.setSoundEffectsEnabled(false);
            ton.setSoundEffectsEnabled(false);
            aSwitch.setSoundEffectsEnabled(false);
            videoan.setSoundEffectsEnabled(false);
        }
    }

    public void btn_einstellung_ton(View view) {
        Allgemein.setzteBoolean(this,Allgemein.KEY_TON);
        setzeTonbild();
    }

    public void btn_werbung(View view) {
        Log.i("IN-App-Buy", "kauf abschließen");
        purchase();
    }

    public void btn_sprache_save(View view) {
        AlertDialog.Builder alterDialog = new AlertDialog.Builder(this);
        alterDialog.setTitle(getResources().getString(R.string.alert_title));
        alterDialog.setMessage(getResources().getString(R.string.alert_message));
        alterDialog.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Allgemein.setzeString(getApplicationContext(),Allgemein.KEY_SPRACHE,gebekurz(spin_sprache.getSelectedItem().toString()));
                DatabaseHandler db = new DatabaseHandler(getApplicationContext());
                db.deleteTabeleLeeren();
                startActivity(new Intent(Einstellungen.this, MainActivity.class));
            }
        });
        alterDialog.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alterDialog.create();
        alterDialog.show();
    }

    public void btn_ladesammeln(View view) {
        showRewardedVideo();
    }

    private String gebekurz(String lang){
        List<String> sprachauswahlkurz = Allgemein.gebeSprachenKurz();
        List<String> sprachauswahllang = Arrays.asList(getResources().getStringArray(R.array.sprachen_lang));
        String kurz ="";
        for (int i =0;i<sprachauswahllang.size();i++){
            if (lang==sprachauswahllang.get(i)){
                kurz=sprachauswahlkurz.get(i);
                break;
            } else {
                kurz="en";
            }
        }
        return kurz;
    }

    //loadwerbung
    private void loadRewardedAd() {
        if (rewardedAd == null || !rewardedAd.isLoaded()) {
            rewardedAd = new RewardedAd(this, Werbung.AD_UNIT_ID);
            isLoading = true;
            rewardedAd.loadAd(
                    new AdRequest.Builder().build(),
                    new RewardedAdLoadCallback() {
                        @Override
                        public void onRewardedAdLoaded() {
                            // Ad successfully loaded.
                            Einstellungen.this.isLoading = false;
                        //    Toast.makeText(Einstellungen.this, "onRewardedAdLoaded", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRewardedAdFailedToLoad(LoadAdError loadAdError) {
                            // Ad failed to load.
                            Einstellungen.this.isLoading = false;
                            //Toast.makeText(Einstellungen.this, "onRewardedAdFailedToLoad", Toast.LENGTH_SHORT)
                           //         .show();
                        }
                    });
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
                            //Toast.makeText(Einstellungen.this, "onRewardedAdOpened", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onRewardedAdClosed() {
                            geschaut=0;
                            laden=0;
                        }

                        @Override
                        public void onUserEarnedReward(RewardItem rewardItem) {
                            // User earned reward.
                            geschaut++;
                            Log.i("Werbung",String.valueOf(geschaut));
                            if (laden==geschaut&&geschaut==3){
                                werbespeicher(90);
                            } else if (laden==geschaut&&geschaut==2) {
                                werbespeicher(50);
                            } else if (laden==geschaut&&geschaut==1){
                                werbespeicher(20);
                            }   else {
                                showRewardedVideo();
                            }
                        }
                        @Override
                        public void onRewardedAdFailedToShow(AdError adError) {
                        }
                    };
            rewardedAd.show(this, adCallback);
        }
        loadRewardedAd();
    }

    public void btn_ladesammeln20(View view) {
        laden=1;
        showRewardedVideo();
    }

    public void btn_ladesammeln50(View view) {
        laden=2;
        showRewardedVideo();
    }


    public void btn_ladesammeln90(View view) {
        laden=3;
        showRewardedVideo();
    }

    public void werbespeicher(int anzahlrunde){
        int wert;
        try {
            wert = Integer.valueOf(Allgemein.gebeString(getApplicationContext(),Allgemein.KEY_SPEICHERRUNDEN));
        } catch (Exception e){
            wert=0;
        }
        wert = wert+anzahlrunde;
        Allgemein.setzeString(getApplicationContext(),Allgemein.KEY_SPEICHERRUNDEN,String.valueOf(wert));
        Log.i("Speicherround",String.valueOf(wert));
        laden=0;
        geschaut=0;
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
                                billingClient.launchBillingFlow(Einstellungen.this, flowParams);
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
            //Toast.makeText(getApplicationContext(),"Purchase Canceled",Toast.LENGTH_SHORT).show();
        }
        // Handle any other error msgs
        else {
        //    Toast.makeText(getApplicationContext(),"Error1"+billingResult.getDebugMessage(),Toast.LENGTH_SHORT).show();
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
                //    Toast.makeText(getApplicationContext(), "Error6 : Invalid Purchase", Toast.LENGTH_SHORT).show();
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
                Einstellungen.this.recreate();
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
}