package schulzUndWitzelGbR.App.saufio;

import androidx.appcompat.app.AppCompatActivity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.example.saufio.BuildConfig;
import com.example.saufio.R;

public class Credits extends AppCompatActivity {

    ImageButton btn_insta, btn_email, btn_share;

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
        setContentView(R.layout.activity_credits);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        btn_email=(ImageButton)findViewById(R.id.imgbtn_email);
        btn_insta=(ImageButton)findViewById(R.id.imgBtn_instagram);
        btn_share=(ImageButton)findViewById(R.id.imgBtn_share);
        setzeTon();
    }

    //Button Instagra
    public void btnimg_instagram(View view) {
        Uri uri = Uri.parse("https://www.instagram.com/saufio_newdrinkinggame/");
        Intent insta = new Intent(Intent.ACTION_VIEW, uri);
        insta.setPackage("com.instagram.android");
        try {
            startActivity(insta);
        } catch (ActivityNotFoundException e){
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://www.instagram.com/saufio_newdrinkinggame/")));
        }
    }

    //Button Email-Report Btn
    public void btnimg_email(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.parse("mailto:schulz.witzel.gbr@gmail.com?subject=" + Uri.encode(getResources().getString(R.string.Bug_good)) + "&body=" + Uri.encode(""));
        intent.setData(data);
        startActivity(intent);
    }

    //BTN share
    public void btnimg_share(View view) {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share_subject));
            String shareMessage= "\n"+getResources().getString(R.string.share_Text)+"\n\n";
            shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + BuildConfig.APPLICATION_ID +"\n\n";
            shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
            startActivity(Intent.createChooser(shareIntent, getResources().getString(R.string.share_auswaehlen)));
        } catch(Exception e)
        {
            Log.i("Error",e+" Fehler Sharebutton");
        }
    }

    public void setzeTon(){
        boolean ton = Allgemein.gebeBoolean(this,Allgemein.KEY_TON);
        btn_insta.setSoundEffectsEnabled(ton);
        btn_email.setSoundEffectsEnabled(ton);
        //btn_share.setSoundEffectsEnabled(ton);
    }
}