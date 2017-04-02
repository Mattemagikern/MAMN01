package mamn01.projekt;

import android.app.ListActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

/**
 * Created by Administrator on 2017/4/1.
 */

public class GiveActivity extends ListActivity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView list_view = (ListView) findViewById(R.id.give_list);

        ArrayAdapter adapter = new ArrayAdapter<String>(this, R.layout.content_give, give_example());
        list_view.setAdapter(adapter);
    }

    public String[] give_example (){
        String [] example_list = null;
        String [] example_name_list = {
                "User 1",
                "User 2",
                "User 3"
        };

        String [] example_distance_list = {
                "1 km",
                "2 km",
                "3 km"
        };

        for (int i = 0; i < example_distance_list.length; i++){
            example_list[i] = example_name_list[i] + "  " + example_distance_list;
        }

        return example_list;
    }
}
