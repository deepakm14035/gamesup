package com.iiitd.purusharth.projectx;

import java.util.HashMap;
import java.util.Map;

//Stores activity images for easy access

public class ActivityImages {

    public static Map<String, Integer> hashMap = new HashMap<String, Integer>() {{
        put("cricket", R.drawable.cricket);
        put("football", R.drawable.football);
        put("tennis", R.drawable.tennis);
        put("basketball", R.drawable.basketball);
        put("karate", R.drawable.karate);
    }};

    public static Integer getImageResource(String activity) {
        Integer res = hashMap.get(activity);
        if (res == null) {
            return R.drawable.ic_fire;
        } else {
            return res;
        }
    }
}
