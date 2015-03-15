package ru.fizteh.fivt.students.Oktosha.filemap;

import org.json.JSONObject;
import ru.fizteh.fivt.storage.structured.ColumnFormatException;
import ru.fizteh.fivt.storage.structured.Storeable;

import org.json.JSONArray;

import java.text.ParseException;
import java.util.List;

/**
 * Created by DKolodzey on 06.03.15.
 * class which implements JSON serialize
 */
public class JSONStoreableSerializerDeserializer implements StoreableSerializerDeserializer {
    @Override
    public String serialize(List<SignatureElement> signature, Storeable value)
            throws ColumnFormatException, IndexOutOfBoundsException {
        JSONArray jsonArray = new JSONArray();
        for (int i = 0; i < signature.size(); ++i) {
            switch (signature.get(i)) {
                case BOOLEAN:
                    jsonArray.put(value.getBooleanAt(i));
                    break;
                case BYTE:
                    jsonArray.put(value.getByteAt(i));
                    break;
                case INTEGER:
                    jsonArray.put(value.getIntAt(i));
                    break;
                case LONG:
                    jsonArray.put(value.getLongAt(i));
                    break;
                case FLOAT:
                    jsonArray.put(value.getFloatAt(i));
                    break;
                case DOUBLE:
                    jsonArray.put(value.getDoubleAt(i));
                    break;
                case STRING:
                    jsonArray.put(value.getStringAt(i));
                    break;
                default:
                    throw new IllegalStateException();
            }
        }
        try {
            value.getColumnAt(signature.size());
        } catch (IndexOutOfBoundsException ignored) {
            return jsonArray.toString();
        }
        throw new ColumnFormatException("too many columns in storeable");
    }
    public Storeable deserialize(List<SignatureElement> signature, String serializedValue) throws ParseException {
        Storeable deserializedValue = new StoreableImpl(signature);
        JSONArray jsonArray = new JSONArray(serializedValue);
        if (jsonArray.length() != signature.size()) {
            throw new ParseException(serializedValue, -1);
        }
        try {
            for (int i = 0; i < signature.size(); ++i) {
                Object jsonArrayItem = jsonArray.get(i);
                if (jsonArrayItem.equals(JSONObject.NULL)) {
                    deserializedValue.setColumnAt(i, null);
                } else {
                    deserializedValue.setColumnAt(i, signature.get(i).getJavaClass().cast(jsonArrayItem));
                }
            }
        } catch (ColumnFormatException | ClassCastException | IndexOutOfBoundsException e) {
            throw new ParseException(serializedValue, -1);
        }
        return deserializedValue;
    }
}
