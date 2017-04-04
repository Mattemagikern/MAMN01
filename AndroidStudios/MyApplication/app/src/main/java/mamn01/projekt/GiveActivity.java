package mamn01.projekt;

import android.app.ListActivity;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/1.
 */

public class GiveActivity extends ListActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give);
        fetchNearbyWanters();

        /**


         String [] give_example =  {
         "User 1, 1 km",
         "User 2, 2 km",
         "User 3, 3 km"
         };
        ListView list_view = (ListView) findViewById(R.id.give_list);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.content_give, give_example());
        list_view.setAdapter(adapter);
         */
    }


    private void fetchNearbyWanters() {
        // Instantiate the RequestQueue.
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String lat = String.valueOf(55.704660);
        String lng = String.valueOf(13.221007);
        String url = "http://shapeapp.se/mamn01/?action=getNearbyWanters&device=" + deviceId + "&lat=" + lat + "&lng=" + lng;
        final ArrayList<String> wanters = new ArrayList<String>();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
                (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray data = (JSONArray) response.get("data");
                            for(int i = 0; i < data.length(); i++){
                                JSONObject o = (JSONObject) data.get(i);
                                wanters.add(String.valueOf(i +1) + ". " + o.getString("name") + " är " + o.getString("distance") + " km bort");
                            }
                            setListAdapter(new ArrayAdapter<String>(GiveActivity.this,
                                    android.R.layout.simple_list_item_1, wanters));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error: " + error.getMessage());
                    }
                });
        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsObjRequest);
    }
}