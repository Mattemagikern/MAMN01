package mamn01.projekt;

import android.app.ListActivity;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class RatingActivity extends ListActivity {

    String[] values = { "1. Nille\t\t50 Hugpoint", "2. Hanna\t\t30 Hugpoint", "3. Hanna\t\t10 Hugpoint", "4. Måns\t\t-10 Hugpoint" };
    //String[] values = {};


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);


       // Instantiate the RequestQueue.
        String url = "http://shapeapp.se/mamn01/?action=getHugboard";
        final ArrayList<String> hugboard = new ArrayList<String>();
        JsonArrayRequest jsObjRequest = new JsonArrayRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
                @Override
                public void onResponse(JSONArray response) {
                    try {
                        for(int i = 0; i < response.length(); i++){
                            JSONObject o = (JSONObject) response.get(i);
                            hugboard.add(String.valueOf(i +1) + ". " + o.getString("name") + "\t\t" + o.getString("hugpoints") + "points");
                        }
                        setListAdapter(new ArrayAdapter<String>(RatingActivity.this,
                                android.R.layout.simple_list_item_1, hugboard));
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

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }

}
