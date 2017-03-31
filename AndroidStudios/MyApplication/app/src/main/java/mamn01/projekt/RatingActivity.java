package mamn01.projekt;

import android.app.Activity;
import android.app.ListActivity;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.provider.Contacts;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;
import android.widget.Toolbar;

public class RatingActivity extends ListActivity {

    String[] values = { "1. Nille\t\t50 Hugpoint", "2. Hanna\t\t30 Hugpoint", "3. Hanna\t\t10 Hugpoint", "4. Måns\t\t-10 Hugpoint" };
    //String[] values = {};


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);

        // parameters are (Context, layout for the row, and the array of data)
        setListAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, values));
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id)
    {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(this, item + " selected", Toast.LENGTH_LONG).show();
    }

    public void finishRatingActivity(View v){
        finish();
    }


}
