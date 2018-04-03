package biz.eastservices.suara.Fragment;


import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import biz.eastservices.suara.Common.Common;
import biz.eastservices.suara.Interface.ItemClickListener;
import biz.eastservices.suara.Model.Vendor;
import biz.eastservices.suara.R;
import biz.eastservices.suara.VendorDetail;
import biz.eastservices.suara.ViewHolder.ListVendorViewHolder;

/**
 * A simple {@link Fragment} subclass.
 */
public class DeliveryFragment extends Fragment {

    private static DeliveryFragment INSTANCE=null;



    FirebaseDatabase database;
    DatabaseReference candidates;

    FirebaseRecyclerOptions<Vendor> options;
    FirebaseRecyclerAdapter<Vendor,ListVendorViewHolder> adapter;
    //View
    RecyclerView recyclerView;
    SwipeRefreshLayout swipeRefreshLayout;

    private static Location mLocation;

    public static DeliveryFragment getInstance(Location location)
    {
        if(INSTANCE == null)
            INSTANCE = new DeliveryFragment();
        mLocation = location;
        return INSTANCE;
    }




    public DeliveryFragment() {



        database = FirebaseDatabase.getInstance();
        candidates = database.getReference(Common.VENDOR_ONLINE);
        Query query = candidates.orderByChild("category").equalTo("Delivery");
        options = new FirebaseRecyclerOptions.Builder<Vendor>()
                .setQuery(query,Vendor.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Vendor, ListVendorViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ListVendorViewHolder holder, int position, @NonNull final Vendor model) {
                Location candidateLocation = new Location(LocationManager.NETWORK_PROVIDER);
                candidateLocation.setLatitude(model.getLat());
                candidateLocation.setLongitude(model.getLng());
                double distanceInKm = (mLocation.distanceTo(candidateLocation))/1000;
                if(distanceInKm <= 5) // 5km
                {
                    holder.txt_description.setText(model.getBusinessDescription());
                    holder.txt_name.setText(model.getBusinessName());
                    holder.txt_distance.setText(new StringBuilder("Distance : ").append(String.format("%.1f",distanceInKm)).append(" km").toString());

                    holder.setItemClickListener(new ItemClickListener() {
                        @Override
                        public void onClick(View view, int position) {
                            Common.selected_uid_people = adapter.getRef(position).getKey();
                            Common.currentVendor = model;
                            startActivity(new Intent(getActivity(), VendorDetail.class));

                        }
                    });
                }
                else
                    holder.hideLayout();
            }

            @Override
            public ListVendorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.layout_vendor,parent,false);
                return new ListVendorViewHolder(itemView);
            }
        };



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_vendor, container, false);
        recyclerView = (RecyclerView)view.findViewById(R.id.recycler_vendors);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_layout);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                loadData();
            }
        });
        return view;
    }

    private void loadData() {
        adapter.startListening();
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapter!=null)
            adapter.startListening();
    }

    @Override
    public void onStop() {
        if(adapter!=null)
            adapter.stopListening();
        super.onStop();
    }

}
