package com.iiitd.purusharth.projectx;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Purusharth on 27-11-2016.
 */

public class VenueLocation {
    public static Map<String, String> hashMap = new HashMap<String, String>() {{
        put("iiit delhi", "28.547126,77.273158");
        put("nsit", "28.609027,77.035058");
        put("dtu", "28.749967,77.117674");
    }};

    public static String getVenueLocation(String venue) {
        String res = hashMap.get(venue);
        if (res == null) {
            return null;
        } else {
            return res;
        }
    }
}
