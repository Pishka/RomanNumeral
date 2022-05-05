package com.consulner.api;

import java.util.*;

public class RomanNumeralConvertor {
    private static TreeMap<Integer , String> numDict;
    public static String convert(String number)
    {
        String result = "";
        //convert String num to Int
        var num = toInt(number);
        //initializing of dictionary(once)
        if(numDict == null)
        {
            initializeDictionary();
        }
        for(var mapKeyValue : numDict.entrySet())
        {
            //decrease num with the largest possible value from dictionary
            while(num>=mapKeyValue.getKey())
            {
                num-=mapKeyValue.getKey();
                result+=mapKeyValue.getValue();
            }
        }
        return result;
    }

    private static void initializeDictionary() {
        numDict = new TreeMap<Integer , String>(Collections.reverseOrder());
        numDict.put(1000 , "M");
        numDict.put(900 , "CM");
        numDict.put(500 , "D");
        numDict.put(400 , "CD");
        numDict.put(100 , "C");
        numDict.put(90 , "XC");
        numDict.put(50 , "L");
        numDict.put(40 , "XL");
        numDict.put(10 , "X");
        numDict.put(9 , "IX");
        numDict.put(5 , "V");
        numDict.put(4 , "IV");
        numDict.put(1 , "I");
    }

    public static int toInt(String number)
    {
        int result = 0;
        for(int i=0;i<number.length();++i)
        {
            result*=10;
            result+=(number.charAt(i)-'0');
        }
        return result;
    }
}
