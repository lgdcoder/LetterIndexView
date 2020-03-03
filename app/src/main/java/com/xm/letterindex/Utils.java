package com.xm.letterindex;

import android.text.TextUtils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;

import java.util.Random;

public class Utils {
    /**
     * 使用PinYin4j.jar将汉字转换为拼音
     */
    private static HanyuPinyinOutputFormat format;

    public static String chineneToSpell(String chineseStr) {
        try {
            if (!TextUtils.isEmpty(chineseStr)) {
                if (format == null) {
                    format = new HanyuPinyinOutputFormat();
                    format.setCaseType(HanyuPinyinCaseType.LOWERCASE);
                    format.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
                    format.setVCharType(HanyuPinyinVCharType.WITH_V);
                }
                return PinyinHelper.toHanYuPinyinString(chineseStr, format, "", false);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return chineseStr;
        }
        return "";
    }

    public static int getRandom(int min, int max) {
        if (max > min) {
            Random random = new Random();
            return random.nextInt(max - min) + min;
        }
        return 0;
    }
}
