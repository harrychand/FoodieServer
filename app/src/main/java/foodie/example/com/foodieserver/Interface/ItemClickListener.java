package foodie.example.com.foodieserver.Interface;

import android.view.View;

public interface ItemClickListener {
    void onClick(View View,int position,boolean isLongClick);

    //void inClick(View view, int adapterPosition, boolean b);
}

