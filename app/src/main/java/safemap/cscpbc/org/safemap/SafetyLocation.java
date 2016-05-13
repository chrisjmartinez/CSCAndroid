package safemap.cscpbc.org.safemap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Created by chrismartinez on 5/13/16.
 */
public class SafetyLocation {

    public enum LocationType {
        swimLocation,
        carSeatLocation,
        helmetLocation,
        pillDropLocation
    }

    public String name;
    public String address;
    public String description;
    public double latitude;
    public double longitude;
    public LocationType type;


    public SafetyLocation() {

    }

    public static SafetyLocation locationFromJSON(JSONObject json) {
        /*
        "name":"CSC Palm Beach",
      "address":"2300 High Ridge Rd, Boynton Beach, FL 33426",
      "latitude":"26.551748",
      "longitude":"-80.071720",
      "description":"CSC Palm Beach",
      "location_type":"1"
         */
        SafetyLocation location = new SafetyLocation();

        if (null != json) {
            try {
                if (json.has("name")) {
                    location.name = json.getString("name");
                }

                if (json.has("address")) {
                    location.address = json.getString("address");
                }

                if (json.has("latitude")) {
                    location.latitude = json.getDouble("latitude");
                }

                if (json.has("longitude")) {
                    location.longitude = json.getDouble("longitude");
                }

                if (json.has("description")) {
                    location.description = json.getString("description");
                }

                if (json.has("location_type")) {
                    int type = json.getInt("location_type");
                    location.type = SafetyLocation.LocationType.values()[type];
                }

            } catch (JSONException ex) {
                ex.printStackTrace();
            }
        }

        return location;
    }

    public static ArrayList<SafetyLocation> locationsFromJSON(JSONObject json) {
        ArrayList<SafetyLocation> locations = new ArrayList<SafetyLocation>();
        JSONArray jsonLocations;
        try {
            jsonLocations = json.getJSONArray("locations");

            if (null != jsonLocations) {
                for (int index = 0; index < jsonLocations.length(); index++) {
                    JSONObject obj = jsonLocations.getJSONObject(index);

                    SafetyLocation location = SafetyLocation.locationFromJSON(obj);

                    if (location != null) {
                        locations.add(location);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return locations;
    }
}
