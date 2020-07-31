package pt.ipbeja.boleias;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.YearMonth;
import java.util.Arrays;
import java.util.Calendar;

import me.anwarshahriar.calligrapher.Calligrapher;
import pt.ipbeja.boleias.Model.Coordinates;
import pt.ipbeja.boleias.Model.CoordinatesTo;
import pt.ipbeja.boleias.Model.Travel;
import pt.ipbeja.boleias.Model.TravelDatabase;

public class AddTravel extends AppCompatActivity implements View.OnClickListener {

    private Button btnHour, btnDate,btnAddLocation, btnAddTravel;
    private EditText editCityFrom, editCityTo, editHour, editDate;
    private int day, month, year, hour, minutes;


    private FirebaseAuth auth;
    private FirebaseUser currentUser;
    private DatabaseReference data;

    private String numberPhone;

    private double latitudeFrom, longitudeFrom, latitudeTo, longitudeTo;

    private Coordinates coordinatesFrom;
    private CoordinatesTo coordinatesTo;

    public static void start(Context context) {
        Intent starter = new Intent(context, AddTravel.class);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_travel);

        Toolbar toolbar = findViewById(R.id.toolbar_alt);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.addTravel));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Get the Auntentication from Firebase
        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        //Get user data by auntentication ID
        //Get PhoneNumber from users table
        String userid = currentUser.getUid();
        data = FirebaseDatabase.getInstance().getReference("Users");
        data.child(userid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numberPhone = dataSnapshot.child("phone").getValue().toString();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        editCityFrom = findViewById(R.id.edtCityFrom);
        editCityTo = findViewById(R.id.edtCityTo);

        btnDate = (Button)findViewById(R.id.btnDate);
        btnHour = (Button)findViewById(R.id.btnHour);
        editDate = findViewById(R.id.edtDate);
        editHour = findViewById(R.id.edtHour);

        btnAddTravel = findViewById(R.id.btnConfirmTravel);
        btnAddLocation = findViewById(R.id.btnAddCoordinates);

        //click Listener for all buttons
        btnDate.setOnClickListener(this);
        btnHour.setOnClickListener(this);
        btnAddLocation.setOnClickListener(this);
        btnAddTravel.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        // if button is btnDate insert DATE in editText for date
        if(v == btnDate){

            final Calendar calendar= Calendar.getInstance();

            day = calendar.get(Calendar.DAY_OF_MONTH);
            month = calendar.get(Calendar.MONTH);
            year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            editDate.setText(dayOfMonth + "/" +(month + 1) + "/" + year);
                        }
                    }, day, month, year);
            datePickerDialog.show();

        }

        // if button is btnHour insert Hour in editText for hour
        if(v == btnHour){

            final Calendar calendar= Calendar.getInstance();

            hour = calendar.get(Calendar.HOUR_OF_DAY);
            minutes = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            editHour.setText(hourOfDay + ":" + minute);
                        }
                    }, hour, minutes, false);
            timePickerDialog.show();

        }



        // if button is btnAddLocation get coordinates of location FROM and TO
        if (v == btnAddLocation){

            String addressCityFrom = editCityFrom.getText().toString();
            String addressCityTo = editCityTo.getText().toString();
            GeoLocation geoLocation = new GeoLocation();
            geoLocation.getAddress(addressCityFrom, getApplicationContext(), new GeoFromHandler());
            geoLocation.getAddress(addressCityTo, getApplicationContext(), new GeoToHandler());

        }

        // if button is btnAddTravel confirm all data and call method SaveTravelTask
        if (v == btnAddTravel){

            coordinatesFrom = new Coordinates(latitudeFrom, longitudeFrom);
            coordinatesTo= new CoordinatesTo(latitudeTo, longitudeTo);

            if (coordinatesFrom.isValid() && coordinatesTo.isValid()
                    && !editCityFrom.getText().toString().isEmpty()
                        && !editCityTo.getText().toString().isEmpty()){

                if(!editDate.getText().toString().isEmpty() && !editHour.getText().toString().isEmpty()){


                    Travel travel = new Travel(
                            currentUser.getDisplayName(),
                            editDate.getText().toString(),
                            editHour.getText().toString(),
                            numberPhone,
                            editCityFrom.getText().toString(),
                            editCityTo.getText().toString(),
                            coordinatesFrom,
                            coordinatesTo);

                    new SaveTravelTask().execute(travel);
                    finish();

                }else {

                    Toast.makeText(getApplicationContext(), getString(R.string.alert_fields), Toast.LENGTH_LONG)
                            .show();

                }

            }else {


                Toast.makeText(getApplicationContext(), getString(R.string.error_locals), Toast.LENGTH_LONG)
                        .show();
            }




        }


    }


    /**
     * This method insert data into database
     */
    private class SaveTravelTask extends AsyncTask<Travel, Void, Travel> {

        @Override
        protected Travel doInBackground(Travel... travels) {
            Travel travel = travels[0];
                     .traveldao()
                    .insert(travel);

            travel.setId(id);

            return travel;

        }
    }

    /**
     * Handler for departure destination
     */
    private class GeoFromHandler extends Handler {

        @Override
        public void handleMessage(Message msg){

            String latitude;
            String longitude;
            switch (msg.what){
                case 1:
                    Bundle bundle = msg.getData();
                    //latitude = bundle.getString("latitude");
                    latitudeFrom = Double.parseDouble(bundle.getString("latitude"));
                    //longitude = bundle.getString("longitude");
                    longitudeFrom = Double.parseDouble(bundle.getString("longitude"));
                    break;
                default:
                    //latitude = null;
                    //longitude = null;
                    latitudeFrom = Double.parseDouble(null);
                    latitudeFrom = Double.parseDouble(null);


            }

        }
    }

    /**
     * Handler for arrival destination
     */
    private class GeoToHandler extends Handler {

        @Override
        public void handleMessage(Message msg){

            String latitude;
            String longitude;
            switch (msg.what){
                case 1:
                    Bundle bundle = msg.getData();
                    latitude = bundle.getString("latitude");
                    latitudeTo = Double.parseDouble(bundle.getString("latitude"));
                    longitude = bundle.getString("longitude");
                    longitudeTo = Double.parseDouble(bundle.getString("longitude"));
                    break;
                default:
                    latitude = null;
                    longitude = null;
                    latitudeTo = Double.parseDouble(null);
                    longitudeTo = Double.parseDouble(null);

            }

        }
    }
}
