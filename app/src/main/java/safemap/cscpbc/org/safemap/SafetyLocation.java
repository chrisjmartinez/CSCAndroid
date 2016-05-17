package safemap.cscpbc.org.safemap;

import android.util.Xml;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


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
                    location.type = SafetyLocation.LocationType.values()[type - 1];
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

    // We don't use namespaces
    private static final String ns = null;

    public static ArrayList<SafetyLocation> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readLocations(parser);
        } finally {
            in.close();
        }
    }

    private static ArrayList<SafetyLocation> readLocations(XmlPullParser parser) throws XmlPullParserException, IOException {
        ArrayList<SafetyLocation> locations = new ArrayList<SafetyLocation>();

        parser.require(XmlPullParser.START_TAG, ns, "locations");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            // Starts by looking for the location tag
            if (name.equals("location")) {
                locations.add(readLocation(parser));
            } else {
                skip(parser);
            }
        }
        return locations;
    }

    // Parses the contents of an entry. If it encounters a name, address, or description tag, hands them off
    // to their respective "read" methods for processing. Otherwise, skips the tag.
    private static SafetyLocation readLocation(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "location");
        String name = null;
        String address = null;
        String description = null;
        LocationType locationType;
        double latitude;
        double longitude;

        SafetyLocation safetyLocation = new SafetyLocation();

        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String node = parser.getName();
            if (node.equals("name")) {
                name = readName(parser);
                safetyLocation.name = name;
            } else if (node.equals("address")) {
                address = readAddress(parser);
                safetyLocation.address = address;
            } else if (node.equals("latitude")) {
                latitude = readLatitude(parser);
                safetyLocation.latitude = latitude;
            } else if (node.equals("longitude")) {
                longitude = readLongitude(parser);
                safetyLocation.longitude = longitude;
            } else if (node.equals("description")) {
                description = readDescription(parser);
                safetyLocation.description = description;
            } else if (node.equals("locationType")) {
                locationType = readLocationType(parser);
                safetyLocation.type = locationType;
            } else {
                skip(parser);
            }
        }
        return safetyLocation;
    }

    private static String readName(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "name");
        String name = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "name");
        return name;
    }

    private static String readAddress(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "address");
        String address = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "address");
        return address;
    }

    private static String readDescription(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "description");
        String description = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "description");
        return description;
    }

    private static double readLatitude(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "latitude");
        double latitude = readDouble(parser);
        parser.require(XmlPullParser.END_TAG, ns, "latitude");
        return latitude;
    }

    private static double readLongitude(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "longitude");
        double longitude = readDouble(parser);
        parser.require(XmlPullParser.END_TAG, ns, "longitude");
        return longitude;
    }

    private static LocationType readLocationType(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "locationType");
        int locType = readInteger(parser);
        parser.require(XmlPullParser.END_TAG, ns, "locationType");
        return LocationType.values()[locType - 1];
    }

    private static String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private static int readInteger(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        int value = 0;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();

            try {
                value = Integer.parseInt(result);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }

            parser.nextTag();
        }
        return value;
    }

    private static double readDouble(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        double value = 0.0;
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();

            try {
                value = Double.parseDouble(result);
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }

            parser.nextTag();
        }
        return value;
    }

    private static void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
