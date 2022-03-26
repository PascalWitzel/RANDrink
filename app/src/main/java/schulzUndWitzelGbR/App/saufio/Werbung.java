package schulzUndWitzelGbR.App.saufio;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;



public class Werbung {

    //TODO Strings add mode anpassen

    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd interstitialAd;
    Context c;
    public boolean angeschaut = true;
    public int minuten = 0;


    //:todo String auf unsere Werbung anpassen
    public static String interstalize = "ca-app-pub-3594566801106885/3731320663";
    public static String unitid = "ca-app-pub-3594566801106885/3731320663";
    //REWARDCODE
    public static  String AD_UNIT_ID = "ca-app-pub-3594566801106885/3731320663";

    public Werbung(Context c, boolean angeschaut) {
        this.c = c;
        this.angeschaut = angeschaut;
    }

    public void setAngeschaut(boolean hier) {
        this.angeschaut = hier;
    }

    public boolean getAngeschaut() {
        return angeschaut;
    }

    public void klick_werbung() {
        if (Allgemein.gebeBoolean(c, Allgemein.KEY_WERBUNG)) {
            MobileAds.initialize(c, interstalize);
            interstitialAd = new InterstitialAd(c);
            interstitialAd.setAdUnitId(unitid);
            AdRequest request = new AdRequest.Builder().build();
            interstitialAd.loadAd(request);
            interstitialAd.setAdListener(new AdListener() {
                public void onAdLoaded() {
                    if (interstitialAd.isLoaded()) {
                        interstitialAd.show();
                    }
                }
            });
        }
    }

    public RewardedVideoAd rewardwerbung() {
        if (Allgemein.gebeBoolean(c, Allgemein.KEY_WERBUNG)) {
            MobileAds.initialize(c, interstalize);
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(c);
            mRewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                @Override
                public void onRewardedVideoAdLoaded() {

                }

                @Override
                public void onRewardedVideoAdOpened() {
                    minuten = minuten + 1;
                }

                @Override
                public void onRewardedVideoStarted() {
                    minuten = minuten + 1;
                }

                @Override
                public void onRewardedVideoAdClosed() {
                    angeschaut = false;
                }

                @Override
                public void onRewarded(RewardItem rewardItem) {
                    angeschaut = true;
                    minuten = minuten + 5;
                }

                @Override
                public void onRewardedVideoAdLeftApplication() {
                    angeschaut = false;
                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int i) {
                    angeschaut = false;
                }

                @Override
                public void onRewardedVideoCompleted() {

                }

            });
            //todo string anpassen werbung
            mRewardedVideoAd.loadAd("ca-app-pub-3940256099942544/5224354917", new AdRequest.Builder().build());
            mRewardedVideoAd.show();
            mRewardedVideoAd.show();
            return mRewardedVideoAd;
        }
        else {
            return null;
        }
    }
}