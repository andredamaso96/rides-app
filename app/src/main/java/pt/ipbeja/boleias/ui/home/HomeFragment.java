package pt.ipbeja.boleias.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

import me.anwarshahriar.calligrapher.Calligrapher;
import pt.ipbeja.boleias.AddTravel;
import pt.ipbeja.boleias.Model.Travel;
import pt.ipbeja.boleias.Model.TravelDatabase;
import pt.ipbeja.boleias.R;
import pt.ipbeja.boleias.Register_User;

public class HomeFragment extends Fragment {

    private TravelAdapter travelAdapter;
    private FloatingActionButton addTravel;
    //private DatabaseReference databaseUser;
    FirebaseAuth auth;
    FirebaseUser currentUser;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        addTravel = view.findViewById(R.id.add_travel_button);

        auth = FirebaseAuth.getInstance();
        currentUser = auth.getCurrentUser();

        //Initializing the recycler view and linking it to the layout
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        //Setting the layout manager needed for the recycler view
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //Initializing the recycler View
        this.travelAdapter = new TravelAdapter();
        //Setting the Adapter manager needed for the recycler view
        recyclerView.setAdapter(travelAdapter);

        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                //int position = getTraverAt(viewHolder.getAdapterPosition());
                int position = viewHolder.getAdapterPosition();
                travelAdapter.delete(position);
            }

        };
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);



        addTravel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddTravel.class);
                startActivity(intent);
            }
        });



        return view;
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }*/


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //inflate menu
        inflater.inflate(R.menu.delete, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.action_delete:
                /*List<Travel> travels = TravelDatabase
                        .getInstance(getActivity())
                        .traveldao()
                        .deleteTable(currentUser.getDisplayName());*/

                TravelDatabase.getInstance(getActivity()).traveldao().deleteTable(currentUser.getDisplayName());
                Toast.makeText(getContext(), getString(R.string.text_delete), Toast.LENGTH_LONG)
                        .show();
                return true;
                default:
                    return super.onOptionsItemSelected(item);

        }


    }

    @Override
    public void onStart() {
        super.onStart();
        //Getting all data from the local database

        List<Travel> travels = TravelDatabase
                .getInstance(getActivity())
                .traveldao()
                .getListOfTravelsUser(currentUser.getDisplayName());

        this.travelAdapter.setData(travels);


    }


    /**
     * Class Viewholder to create the views on the recycler view
     * Req 2
     */
    private class TravelViewHolder extends RecyclerView.ViewHolder {


        private Travel travel;

        TextView nameUser;
        TextView date;
        TextView hour;
        TextView numberPhone;
        TextView city_from;
        TextView city_to;



        /**
         * Thi is the method responsible for attaching the layout items to the TextView and Imageview initialized on this class
         * Req 2
         * @param itemView The item view passed thought the adapter that will contain all the data passed
         */
        TravelViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.text_date);
            hour = itemView.findViewById(R.id.text_hour);
            city_from = itemView.findViewById(R.id.text_city_from);
            city_to = itemView.findViewById(R.id.text_city_to);


            //Setting a click listener for the hole view that contains the data
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    //Calling the mathod in the adapter that initiate another activity with the info that we have on this one
                    //travelAdapter.goToDescription(position);



                }
            });







        }



        void bind(Travel travel){
            this.travel = travel;
            this.date.setText(travel.getDate());
            this.hour.setText(travel.getHour());
            this.city_from.setText(travel.getCityFromName());
            this.city_to.setText(travel.getCityToName());

        }


    }


    /**
     * This is the adapter that the recycler view requests for it to function correctly
     * Req 2
     */
    private class TravelAdapter extends RecyclerView.Adapter<TravelViewHolder>{

        private List<Travel> data;

        public void delete(int position) {
            Travel travel = data.get(position);
            TravelDatabase.getInstance(getActivity()).traveldao().delete(travel);
            data.remove(position);
            notifyItemRemoved(position);


        }


        /**
         * This is the method responsible for attacing the XML layout that we have created before to the view that will display the items
         * Req 2
         * @param viewGroup The ViewGroup into which the new View will be added after it is bound to an adapter position.
         * @param i The view type of the new View.
         * @return A new ViewHolder that holds a View of the given view type.
         */
        @NonNull
        @Override
        public TravelViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.travel_item, viewGroup, false);
            return new TravelViewHolder(view);
        }

        /**
         * Method responsible for sending the data to the viewholder
         * Req 2
         * @param postViewHolder This the ViewHolder that we have created before
         * @param i The position of the item within the adapter's data set.
         */
        @Override
        public void onBindViewHolder(@NonNull TravelViewHolder postViewHolder, int i) {
            Travel travel = data.get(i);
            postViewHolder.bind(travel);
        }

        public Travel getTraverAt(int position){

            return data.get(position);

        }

        /**
         * Getter for the size of the data
         * Req 2
         * @return the size of the data
         */
        @Override
        public int getItemCount() {

            return data == null ? 0 : data.size();
        }

        /**
         * Set that data, add the list of posts retrieved from the database and and to the list of data
         * Req 2,5
         * @param travels List of Post objects
         */
        public void setData(List<Travel> travels){
            this.data = travels;
            notifyDataSetChanged();
        }

        /**
         * Method that will start another activity passing as parameter the id of the post
         * Req 2,5
         * @param position Position in the adapter, so that we can get the right object
         */
        /*public void goToDescription(int position) {
            Travel travel = data.get(position);
            //PetDescription.start(List_Pets.this, post.getId());
        }*/
    }
}