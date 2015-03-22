package ru.fizteh.fivt.students.Oktosha.database.storeable;

import org.json.JSONException;
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
        JSONArray jsonArray;
        try {
            jsonArray = new JSONArray(serializedValue);
        } catch (JSONException e) {
            throw new ParseException(e.getMessage(), -1);
        }
        if (jsonArray.length() != signature.size()) {
            throw new ParseException("expected " + signature.size() + " columns; got " + jsonArray.length(), -1);
        }
        for (int i = 0; i < signature.size(); ++i) {
            Object jsonArrayItem;
            try {
                jsonArrayItem = jsonArray.get(i);
            } catch (JSONException e) {
                throw new IllegalStateException("JSON fail in deserializer", e);
            }
            try {
                if (jsonArrayItem.equals(JSONObject.NULL)) {
                    deserializedValue.setColumnAt(i, null);
                } else if (jsonArrayItem instanceof Number) {
                    Number asNumber = (Number) jsonArrayItem;
                    switch (signature.get(i)) {
                        case DOUBLE:
                            deserializedValue.setColumnAt(i, asNumber.doubleValue());
                            break;
                        case FLOAT:
                            deserializedValue.setColumnAt(i, asNumber.floatValue());
                            break;
                        case LONG:
                            deserializedValue.setColumnAt(i, asNumber.longValue());
                            break;
                        case INTEGER:
                            deserializedValue.setColumnAt(i, asNumber.intValue());
                            break;
                        case BYTE:
                            deserializedValue.setColumnAt(i, asNumber.byteValue());
                            break;
                        case STRING:
                        case BOOLEAN:
                            throw new ParseException("unable to set value \"" + jsonArrayItem.toString()
                                + "\" at column #" + i
                                + "; expected column type: " + signature.get(i).getName()
                                + ";", i);
                        default:
                            throw new IllegalStateException();
                    }
                } else {
                    deserializedValue.setColumnAt(i, jsonArrayItem);
                }
            } catch (ColumnFormatException | ClassCastException | IndexOutOfBoundsException e) {
                throw new ParseException("unable to set value \"" + jsonArrayItem.toString()
                                         + "\" at column #" + i
                                         + "; expected column type: " + signature.get(i).getName()
                                         + ";" + e.getMessage(), i);
            }
        }
        return deserializedValue;
    }
}
