package foodie.example.com.foodieserver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.RemoteMessage;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;
import java.util.Map;

import foodie.example.com.foodieserver.Common.Common;
import foodie.example.com.foodieserver.Interface.ItemClickListener;
import foodie.example.com.foodieserver.Model.MyResponse;
import foodie.example.com.foodieserver.Model.Notification;
import foodie.example.com.foodieserver.Model.Request;
import foodie.example.com.foodieserver.Model.Sender;
import foodie.example.com.foodieserver.Model.Token;
import foodie.example.com.foodieserver.Remote.APIService;
import foodie.example.com.foodieserver.ViewHolder.MenuViewHolder;
import foodie.example.com.foodieserver.ViewHolder.OrderViewHolder;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderStatus extends AppCompatActivity {
    // view in activity
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    //Firebase
    FirebaseDatabase database;
    DatabaseReference requests;
    FirebaseRecyclerAdapter<Request,OrderViewHolder> adapter;

    MaterialSpinner spinner;

    APIService mService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        // find view in activity
        recyclerView = findViewById(R.id.listOrders);
        recyclerView.setHasFixedSize(true);
        layoutManager=new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        // initial Firebase
        database=FirebaseDatabase.getInstance();
        requests=database.getReference("Restaurants").child(Common.currentUser.getRestaurantId()).child("Requests");

        mService = Common.getFCMClient();
        // load Order from firebase
        loadOrders();
    }
    /* this method that get data From Firebase json In Adapter => FirebaseRecyclerAdapter*/
    private void loadOrders() {
        FirebaseRecyclerOptions<Request> options = new FirebaseRecyclerOptions.Builder<Request>()
                .setQuery(requests,Request.class)
                .build();

        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull OrderViewHolder viewHolder, final int position, @NonNull final Request model) {
                // get data From Model and set In View Holder and By adapter set View Holder in RecyclerView
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAddress.setText(model.getAddress());

                viewHolder.btnEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdateDialog(adapter.getRef(position).getKey(),
                                adapter.getItem(position));
                    }
                });

                viewHolder.btnRemove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteOrder(adapter.getRef(position).getKey());
                    }
                });

                viewHolder.btnDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent orderDetail = new Intent(OrderStatus.this,OrderDetail.class);
                        Common.currentRequest = model;
                        orderDetail.putExtra("OrderId",adapter.getRef(position).getKey());
                        startActivity(orderDetail);
                    }
                });
            }

            @Override
            public OrderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.order_layout,parent,false);
                return new OrderViewHolder(itemView);
            }
        };
        adapter.startListening();
        adapter.notifyDataSetChanged(); // get data when refresh
        recyclerView.setAdapter(adapter); // set up adapter

    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    /*this delete to delete Order
     * @param key => used to delete item */
    private void deleteOrder(String key) {
        requests.child(key).removeValue(); // delete item from firebase json Requests
        adapter.notifyDataSetChanged();
    }

    /*this method create dialog to update status  an save it in Firebase
     * @param  key=> to determine item that will update
     *          item => to change item and save it
     *          */
    private void showUpdateDialog(String key, final Request item) {
        final String localKey=key;
        // create Dialog
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Update Status");
        alertDialog.setMessage("Please choose status");
        // get Inflater
        LayoutInflater inflater=this.getLayoutInflater();
        // create View
        final View view=inflater.inflate(R.layout.update_order_layout,null);
        // find View in this layout
        final MaterialSpinner spinner=view.findViewById(R.id.status_spinner);
        spinner.setItems("Placed","On My Way","Shipped"); // set item of spinner
        // set View in dialog
        alertDialog.setView(view);
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // change status in item
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                requests.child(localKey).setValue(item); // update item in Requests Json
                adapter.notifyDataSetChanged();

                if (spinner.getSelectedIndex() == 2){
                    updateRewardCash();
                }

                sendOrderStatusToUser(localKey,item);

            }
        });
        alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        //show dialog
        alertDialog.show();
    }

    /* this method to move to this Activity*/
    public static Intent newIntent(Context context){
        Intent intent=new Intent(context,OrderStatus.class);
        return intent;
    }

    private void sendOrderStatusToUser(final String key, final Request item){
        DatabaseReference tokens = database.getReference("Tokens");
        tokens.orderByKey().equalTo(item.getPhone())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapShot:dataSnapshot.getChildren()){
                            Token token = postSnapShot.getValue(Token.class);
                            Notification notification= new Notification("UTP","Your order "+key+" was updated by "+Common.currentUser.getName());
                            Sender content = new Sender(token.getToken(),notification);

                            mService.sendNotification(content)
                                    .enqueue(new Callback<MyResponse>() {
                                        @Override
                                        public void onResponse(Call<MyResponse> call, Response<MyResponse> response) {
                                            if(response.body().success == 1){
                                                Toast.makeText(OrderStatus.this, "Order was updated", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(OrderStatus.this, "Order was updated but failed to send notification", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<MyResponse> call, Throwable t) {
                                            Log.e("ERROR",t.getMessage());
                                        }
                                    });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    private void updateRewardCash(){
        double rewardCash = Common.currentUser.getRewardCash() + 1.0;
        Map<String,Object> rewardUpdate = new HashMap<>();
        rewardUpdate.put("rewardCash",rewardCash);

        DatabaseReference user = FirebaseDatabase.getInstance().getReference("User");
        user.child(Common.currentUser.getPhone())
                .updateChildren(rewardUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(OrderStatus.this, "Reward updated !!",Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(OrderStatus.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
