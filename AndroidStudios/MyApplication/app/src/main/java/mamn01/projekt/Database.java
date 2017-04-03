package mamn01.projekt;

/**
 * Created by mattemagikern on 2017-04-02.
 */

public class Database {

    private String Hugger;
    private String currentPos; //behöver inte uppdatera så länge vi inte flyttar på oss, sparar cpu i databasen.

    public Database(){

    }
    public void UpdatePos(){
        /*
        if(currentPos[0] != bar.latitude() && currentPos[1] != foo.longitude()){
            currentPos[0]= bar.latitude();
            currentpos[1] = foo.longitude();
            UpdateServer(lat,lng);

        }
        */
    }
    public String[] getTopTen(){
        return null; //TODO: Implement.
    }
    public String[] getUsersInArea(String pos){
        return null; //TODO: Implement.
    }
    public String getPosHugger(String HuggerId){
        return null; //TODO: Implement.
    }
    private class pos{
        private String[] pos;
        public pos(String latitude,String longitude){
            pos = new String[2];
            pos[0]= latitude;
            pos[1] = longitude;
        }
        public String[] getPos(){
            return pos;
        }

    }

}
