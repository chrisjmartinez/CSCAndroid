package safemap.cscpbc.org.safemap;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Locale;

public class MapDetailActivity extends AppCompatActivity {

    public static String DETAIL_SUBJECT = "DETAIL_SUBJECT";
    public static String DETAIL_NAME = "DETAIL_NAME";
    public static String DETAIL_ADDRESS = "DETAIL_ADDRESS";
    public static String DETAIL_PHONE = "DETAIL_PHONE";
    public static String DETAIL_EXTRAS = "DETAIL_EXTRAS";
    public static String DETAIL_LAT = "DETAIL_LAT";
    public static String DETAIL_LNG = "DETAIL_LNG";

    private String address;
    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_detail);

        setTitle("");

        TextView textSubject = (TextView)findViewById(R.id.detailSubject);
        TextView textName = (TextView)findViewById(R.id.detailName);
        TextView textAddress = (TextView)findViewById(R.id.detailAddress);
        TextView textPhone = (TextView)findViewById(R.id.detailPhone);
        TextView textExtras = (TextView)findViewById(R.id.detailExtras);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String subject = extras.getString(DETAIL_SUBJECT);
            String name = extras.getString(DETAIL_NAME);
            address = extras.getString(DETAIL_ADDRESS);
            String phone = extras.getString(DETAIL_PHONE);
            String extra = extras.getString(DETAIL_EXTRAS);

            textSubject.setText(subject);
            textName.setText(name);
            textAddress.setText(address);
            textPhone.setText(phone);
            Linkify.addLinks(textPhone, Linkify.PHONE_NUMBERS);
            textExtras.setText(extra);

            lat = extras.getDouble(DETAIL_LAT);
            lng = extras.getDouble(DETAIL_LNG);
        }
    }

    public void addressClick(View v) {
        Uri gmmIntentUri = Uri.parse(String.format(Locale.ENGLISH, "geo:%f,%f?q=%s", lat, lng, address));
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast warning = Toast.makeText(this, R.string.no_map_activity, Toast.LENGTH_LONG);
            warning.show();
        }
    }
}
