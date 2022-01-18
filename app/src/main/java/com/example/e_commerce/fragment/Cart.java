package com.example.e_commerce.fragment;


import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.telephony.gsm.GsmCellLocation;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.Adabter.CartAdapter;
import com.example.e_commerce.Database.MyDatabase;
import com.example.e_commerce.Model.ProductModel;
import com.example.e_commerce.R;
import com.google.gson.Gson;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class Cart extends Fragment {

    private ListView cart_products;
    private CartAdapter adapter;
    private ArrayList<ProductModel> data = new ArrayList<>();

    private MyDatabase database;
    private SharedPreferences sharedPreferences;


    TextView orignal_price,delivery_cost,total_cost;

    double cost=0;

    int PERMISSION_ID = 44;

    public Cart() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);


        cart_products = view.findViewById(R.id.cart_product);
        database = new MyDatabase(getContext());

        orignal_price=view.findViewById(R.id.order_price);
        delivery_cost=view.findViewById(R.id.delivery_cost);
        total_cost=view.findViewById(R.id.total_cost);


        getProductsids();

        return view;
    }

    private void getProductsids() {
        sharedPreferences = this.getActivity().getSharedPreferences("cart", Context.MODE_PRIVATE);
        String ids = sharedPreferences.getString("lastorder", null);
        if (ids != null) {
            Gson gson = new Gson();
            ArrayList id = gson.fromJson(ids, ArrayList.class);
            getCartProduct(id);


            adapter = new CartAdapter(getContext(), data);
            adapter.setTotal_cost(cost);
            cart_products.setAdapter(adapter);


            orignal_price.setText(String.valueOf(adapter.getTotal_cost()) + " $");
            delivery_cost.setText("20.0 $");
            total_cost.setText(cost + 20.0 + "$");
        }


    }

    private void getCartProduct(ArrayList<Integer> ids) {

        data.clear();
        for (int i = 0; i < ids.size(); i++) {
            Cursor cursor = database.getProductbyId(String.valueOf(ids.get(i)));
            if (cursor != null) {
                ProductModel productModel = new ProductModel(Integer.parseInt(cursor.getString(4)),
                        Integer.parseInt(cursor.getString(5)),
                        cursor.getString(1), cursor.getBlob(2),
                        Double.parseDouble(cursor.getString(3)));
                productModel.setPro_id(Integer.parseInt(cursor.getString(0)));
                data.add(productModel);
                cost+=Double.parseDouble(cursor.getString(3));
            }
        }

    }






    private boolean checkPermissions(){
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            return true;
        }
        return false;
    }

    private void requestPermissions(){
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                PERMISSION_ID
        );
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
        );
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_ID) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
            }
        }

}
}
