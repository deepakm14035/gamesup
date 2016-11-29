package com.iiitd.purusharth.projectx;

import java.io.Serializable;

/**
 * Created by Purusharth on 29-11-2016.
 */

public class ExtendedEventActivity extends EventActivity implements Serializable {
    public int num_joined;

    public int getNum_joined() {
        return num_joined;
    }

    public void setNum_joined(int num_joined) {
        this.num_joined = num_joined;
    }
}
