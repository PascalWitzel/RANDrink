package schulzUndWitzelGbR.App.saufio;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.saufio.R;

public class sucht extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sucht);
        assert getSupportActionBar() != null;   //null check
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void btn_kennWebseite(View view) {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    //Webseite Selbsttest
                    Uri.parse("https://www.kenn-dein-limit.de/alkohol-tests/alkohol-selbsttest/")));
        } catch (ActivityNotFoundException e){
            Log.i("exceptiopn",String.valueOf(e));
    }
}

    public void btn_anrufsucht(View view) {
        //Nummer für Suchtberatung
        String phone = "0221892031";
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
        startActivity(intent);
    }
}