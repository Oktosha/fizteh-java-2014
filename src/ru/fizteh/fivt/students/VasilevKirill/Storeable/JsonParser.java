package ru.fizteh.fivt.students.VasilevKirill.Storeable;

import ru.fizteh.fivt.storage.structured.Storeable;

import java.util.List;

/**
 * Created by Kirill on 16.11.2014.
 */
public class JsonParser {
    public static String dataToJsonString(String key, List<Object> data) throws IllegalArgumentException {
        if (key == null || data == null) {
            throw new IllegalArgumentException("JsonParser: illegal arguments");
        }
        String newValue = "";
        if (data.size() == 1) {
            newValue = objectToString(data.get(0));
        } else {
            newValue = "[";
            for (int i = 0; i < data.size(); ++i) {
                newValue += objectToString(data.get(i));
                if (i != data.size() - 1) {
                    newValue += ", ";
                }
            }
            newValue += "]";
        }
        return newValue;
    }

    private static String objectToString(Object data) {
        String result = "";
        if (data instanceof String) {
            result = "\"" + data + "\"";
        } else {
            result = data.toString();
        }
        return result;
    }

    public static Storeable stringToStoreable(String value, Class[] typeList) {
        if (value == null || (value.charAt(0) != '[' && value.charAt(value.length() - 1) != ']')) {
            throw new IllegalArgumentException("JsonParser: illegal argument");
        }
        Storeable result = new MyStorable(typeList);
        String[] resultArray = value.substring(1, value.length() - 1).split("\\s*,\\s*");
        if (resultArray.length != typeList.length) {
            throw new IllegalStateException("JsonParser: incorrect string");
        }
        for (int i = 0; i < resultArray.length; ++i) {
            if (resultArray[i].contains("\"")) {
                resultArray[i] = resultArray[i].substring(1, resultArray[i].length() - 1);
                result.setColumnAt(i, resultArray[i]);
            } else {
                result.setColumnAt(i, typeList[i].cast(resultArray[i]));
            }
        }
        return result;
    }
}
