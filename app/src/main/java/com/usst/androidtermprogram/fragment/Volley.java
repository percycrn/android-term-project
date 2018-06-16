package com.usst.androidtermprogram.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonArrayRequest;
import com.usst.androidtermprogram.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Volley extends Fragment {

    private RequestQueue requestQueue;
    private ImageView imageView;
    private TextView textView;
    private Button btv_string;
    private Button btv_img;

    public Volley() {
        // required empty public constructor
    }

    private void init() {
        // todo
        requestQueue = com.android.volley.toolbox.Volley.newRequestQueue(getActivity());
        imageView = getActivity().findViewById(R.id.image_view);
        textView = getActivity().findViewById(R.id.string_view);
        btv_string = getActivity().findViewById(R.id.btv_string);
        btv_img = getActivity().findViewById(R.id.btv_img);
    }

    private void jsonRequest() {
        textView.setVisibility(View.VISIBLE);
        imageView.setVisibility(View.GONE);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET,
                "http://www.crncrn.cc:8080/json", new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                StringBuilder string = new StringBuilder();
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        string.append("name: ").append(jsonObject.getString("name"))
                                .append("\n")
                                .append("address: ").append(jsonObject.getString("address"))
                                .append("\n")
                                .append("email: ").append(jsonObject.getString("email"))
                                .append("\n")
                                .append("\n");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                textView.setText(string.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        });
        requestQueue.add(jsonArrayRequest);
    }

    private void imageRequest() {
        textView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
        String url = "http://www.qqzhi.com/uploadpic/2014-05-14/051120936.jpg";
        ImageRequest imageRequest = new ImageRequest(
                url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap bitmap) {
                        imageView.setImageBitmap(bitmap);
                    }
                }, 0, 0, ImageView.ScaleType.CENTER, Bitmap.Config.RGB_565,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        System.out.println(volleyError.getMessage());
                        Toast.makeText(getActivity(), volleyError.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(imageRequest);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_volley, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init();
        btv_string.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                jsonRequest();
            }
        });

        btv_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imageRequest();
            }
        });
    }
}
