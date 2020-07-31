package pt.ipbeja.boleias;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


class GeoLocation {

    /**
     * This method find the latitude and longitude from the city picked by variable locationAddress
     * @param locationAddress location to get latitude and longitude data
     * @param context
     * @param handler
     */
    public static void getAddress(final String locationAddress, final Context context, final Handler handler){


        Thread thread = new Thread(){

            @Override
            public void run(){

                //Geocoder find the data from the location
                Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                String latitude = null;
                String longitude = null;
                try{

                    List addressList = geocoder.getFromLocationName(locationAddress, 1);
                    if (addressList != null && addressList.size() > 0){

                        Address address = (Address) addressList.get(0);

                        StringBuilder latitudeBuilder = new StringBuilder();
                        latitudeBuilder.append(address.getLatitude());
                        latitude = latitudeBuilder.toString();

                        StringBuilder longitudeBuilder = new StringBuilder();
                        longitudeBuilder.append(address.getLongitude());
                        longitude = longitudeBuilder.toString();

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }finally {
                    Message message = Message.obtain();
                    message.setTarget(handler);
                    if (latitude!=null && longitude!=null){
                        message.what = 1;
                        Bundle bundle= new Bundle();
                        bundle.putString("latitude", latitude);
                        bundle.putString("longitude", longitude);
                        message.setData(bundle);

                    }
                    message.sendToTarget();
                }

            }


        };
        thread.start();

    }


}
