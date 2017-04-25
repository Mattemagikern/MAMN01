package mamn01.projekt;

import android.app.ListActivity;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HugboardActivity extends ListActivity {

    private boolean isPaused;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
    }

    @Override
    protected void onResume(){
        super.onResume();
        isPaused = false;
        fetchHugBoard();
    }

    /*
      *  Fetch from the backend the list of hugboard.
      *  Add the response to the ListAdapter for updating the ListView.
     */
    private void fetchHugBoard() {
        // Instantiate the RequestQueue.
        String deviceId = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        String url = "http://shapeapp.se/mamn01/?action=getHugboard&device=" + deviceId;
        final ArrayList<String> hugboard = new ArrayList<String>();
        JsonObjectRequest jsObjRequest = new JsonObjectRequest
            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        if(isPaused)
                            return;
                        String dataStr = (String) response.get("data");
                        JSONArray data = new JSONArray(dataStr);
                        for(int i = 0; i < data.length(); i++){
                            JSONObject o = (JSONObject) data.get(i);
                            hugboard.add(String.valueOf(i +1) + ". " + o.getString("name") + "\t\t" + o.getString("hugpoints") + "points");
                        }
                        setListAdapter(new ArrayAdapter<String>(HugboardActivity.this,
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
    protected void onListItemClick(ListView l, View v, int position, long id) {
        //String item = (String) getListAdapter().getItem(position);
        //Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();;
    }
    @Override
    protected void onPause(){
        super.onPause();
        isPaused = true;
    }
}
