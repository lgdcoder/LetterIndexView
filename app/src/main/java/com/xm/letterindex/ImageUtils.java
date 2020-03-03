package com.xm.letterindex;

import static com.xm.letterindex.Utils.getRandom;

public class ImageUtils {

    private final static String[] URLS = new String[38];

    static {
        for (int i = 1; i <= 38; i++)
            URLS[i - 1] = "http://www.helper.mashuwo.com/icon/b" + i + ".jpg";
    }

    public static String getImageUrl() {
        return URLS[getRandom(0, URLS.length)];
    }
}
