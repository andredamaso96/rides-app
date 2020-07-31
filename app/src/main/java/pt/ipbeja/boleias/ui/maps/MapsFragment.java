package pt.ipbeja.boleias.ui.maps;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

import pt.ipbeja.boleias.Model.Travel;
import pt.ipbeja.boleias.Model.TravelDatabase;
import pt.ipbeja.boleias.R;


public class MapsFragment extends Fragment implements OnMapReadyCallback{

    private GoogleMap mMap;



    public MapsFragment(){

    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View mView = inflater.inflate(R.layout.fragment_map, container, false);

        return mView;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(final GoogleMap googleMap) {



        mMap = googleMap;
        //mMap.addMarker(place1);
        //mMap.addMarker(place2);

        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            List<Travel> data;
            List<Travel> travels = TravelDatabase.getInstance(getContext()).traveldao().getListOfTravels();
            @Override
            public void onMapLoaded() {
                //New boundary builder, so that every marker added to it, it will create a boundary around so that they will all stay inside it
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                boolean validBounds = false;
                int i = 0;

                for (Travel travel : travels) {
                    i++;

                    //If they're coordinates a correct
                    if(travel.getFrom().isValid() && travel.getTo().isValid()) {
                        validBounds = true;


                        LatLng latLngFrom = new LatLng(travel.getFrom().getLatitude(), travel.getFrom().getLongitude());
                        LatLng latLngTo = new LatLng(travel.getTo().getLatitudeTo(), travel.getTo().getLongitudeTo());

                        Marker markerFrom = mMap.addMarker(
                                new MarkerOptions()
                                        .position(latLngFrom)
                                        .title(travel.getCityFromName())
                        );

                        Marker markerTo = mMap.addMarker(
                                new MarkerOptions()
                                        .position(latLngTo)
                                        .title(travel.getCityToName())
                        );

                        Polyline polyline1 = googleMap.addPolyline(new PolylineOptions()
                                .clickable(true)
                                .add(latLngFrom,latLngTo));


                        markerFrom.setTag(travel);
                        markerTo.setTag(travel);
                        polyline1.setTag(travel);


                        builder.include(latLngFrom);
                        builder.include(latLngTo);
                    }
                }
                //If the are bounds i will set the camera to move accordingly to the bounds created
                if(validBounds) googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 200));


            }
        });
        //when click on line of travel
        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener()
        {
            @Override
            public void onPolylineClick(Polyline polyline)
            {
                //do something with polyline
                Travel travel = (Travel) polyline.getTag();
                //TravelDescription.start(getContext(), travel.getId());


                AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());


                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View description_layout = inflater.inflate(R.layout.layout_travel_description, null);


                dialog.setTitle(R.string.description_title);

                dialog.setView(description_layout);

                final TextView userName = description_layout.findViewById(R.id.view_username);
                final TextView userPhone = description_layout.findViewById(R.id.view_userPhone);
                final TextView cityFrom = description_layout.findViewById(R.id.view_city_from);
                final TextView cityTo = description_layout.findViewById(R.id.view_city_to);
                final TextView hour = description_layout.findViewById(R.id.view_hour);
                final TextView date = description_layout.findViewById(R.id.view_date);

                userName.setText(travel.getNameUser());
                userPhone.setText(travel.getNumberPhone());
                cityFrom.setText(travel.getCityFromName());
                cityTo.setText(travel.getCityToName());
                hour.setText(travel.getHour());
                date.setText(travel.getDate());

                dialog.show();


            }
        });


    }



}