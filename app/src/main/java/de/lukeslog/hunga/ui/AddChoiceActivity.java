package de.lukeslog.hunga.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import de.lukeslog.hunga.R;
import de.lukeslog.hunga.model.Food;
import de.lukeslog.hunga.support.HungaConstants;

public class AddChoiceActivity extends Activity {

    public static final String TAG = HungaConstants.TAG;
    Activity ctx;
    String proposalName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_choice);
        ctx = this;
        proposalName = getIntent().getStringExtra("proposal");
        TextView chooseScan = (TextView) findViewById(R.id.choseScan);
        chooseScan.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, Scan.class);
                intent.putExtra("proposal", proposalName);
                startActivityForResult(intent, 11991);
            }
        });

        TextView chooseInput = (TextView) findViewById(R.id.chooseInput);
        chooseInput.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ctx, Scan.class);
                intent.putExtra("proposal", proposalName);
                intent.putExtra("useScan", false);
                startActivityForResult(intent, 11991);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            handleActivityResult(requestCode, data);
        }
    }

    private void handleActivityResult(int requestCode, Intent data) {
        String barcode = data.getStringExtra("barcode");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("barcode", barcode);
        setResult(RESULT_OK, returnIntent);
        this.finish();
    }
}
