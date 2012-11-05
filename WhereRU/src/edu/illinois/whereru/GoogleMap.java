package edu.illinois.whereru;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class GoogleMap extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_google_map, menu);
        return true;
    }
}
