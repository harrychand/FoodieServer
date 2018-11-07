package foodie.example.com.foodieserver.Common;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;

import foodie.example.com.foodieserver.Model.User;

public class Common {
    public static User currentUser;
    public static com.example.foodie.foodie.Model.Request currentRequest;
    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";
    public static final int PICK_IMAGE_REQUEST = 71;
    public static final String baseUrl="https://maps.googleapis.com";

    /* this method to convert status value(0,1,2) to String that appear in status */
    public static String convertCodeToStatus(String status){
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return  "On My way";
        else
            return "Shipped";
    }
    /*
    public static IGeoCoordinates getGeoCodeService(){
        return  RetrofitClient.getClient(baseUrl).create(IGeoCoordinates.class);
    }
    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight){
        Bitmap scaledBitmap= Bitmap.createBitmap(newWidth,newHeight,Bitmap.Config.ARGB_8888);
        float scaleX=newWidth/(float) bitmap.getWidth();
        float scaleY=newHeight/(float)bitmap.getHeight();

        float pivotX=0,pivotY=0;
        Matrix scaledMatrix= new Matrix();
        scaledMatrix.setScale(scaleX,scaleY,pivotX,pivotY);
        Canvas canvas=new Canvas(scaledBitmap);
        canvas.setMatrix(scaledMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }*/
}

