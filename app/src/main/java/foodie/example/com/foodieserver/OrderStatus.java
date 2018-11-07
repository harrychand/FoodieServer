package foodie.example.com.foodieserver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.example.foodie.foodie.Model.Request;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import foodie.example.com.foodieserver.Common.Common;
import foodie.example.com.foodieserver.Interface.ItemClickListener;
import foodie.example.com.foodieserver.ViewHolder.OrderViewHolder;

public class OrderStatus extends AppCompatActivity {
    // view in activity
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    //Firebase
    FirebaseDatabase database;
    DatabaseReference requests;
    FirebaseRecyclerAdapter<com.example.foodie.foodie.Model.Request,OrderViewHolder> adapter;

    MaterialSpinner spinner;

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
        // load Order from firebase
        loadOrders();
    }
    /* this method that get data From Firebase json In Adapter => FirebaseRecyclerAdapter*/
    private void loadOrders() {
        adapter=new FirebaseRecyclerAdapter<com.example.foodie.foodie.Model.Request, OrderViewHolder>(
                Request.class,
                R.layout.order_layout,
                OrderViewHolder.class,
                requests) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, int position) {
                // get data From Model and set In View Holder and By adapter set View Holder in RecyclerView
                viewHolder.txtOrderId.setText(adapter.getRef(position).getKey());
                viewHolder.txtOrderStatus.setText(Common.convertCodeToStatus(model.getStatus()));
                viewHolder.txtOrderPhone.setText(model.getPhone());
                viewHolder.txtOrderAddress.setText(model.getAddress());
                // action View holder
                viewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View v, int position, boolean isLongClick) {
                        // move to google map
                        Common.currentRequest=model; // set currentRequest = model to get data im map from currentRequest
                        //startActivity(Order.newIntent(OrderStatus.this));
                    }
                });
            }
        };
        adapter.notifyDataSetChanged(); // get data when refresh
        recyclerView.setAdapter(adapter); // set up adapter
    }

    /*show action when select option of context menu
     * @param item of MenuItem
     * */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getTitle().equals(Common.UPDATE)){
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        else if (item.getTitle().equals(Common.DELETE)){
            deleteOrder(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }
    /*this delete to delete Order
     * @param key => used to delete item */
    private void deleteOrder(String key) {
        requests.child(key).removeValue(); // delete item from firebase json Requests
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
}
