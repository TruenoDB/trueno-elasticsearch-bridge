package org.trueno.es.bridge;

import com.google.gson.Gson;
import org.trueno.es.bridge.comm.Response;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

/**
 * @author Edgardo Barsallo Yi (ebarsallo)
 */
public class TypeHelper {

    /**
     * Converts an array list, which represent a JSON object, to a response message to be send
     * via websocket connection.
     *
     * @param map the {@code ArrayList} which represent a JSON object.
     * @return the response message.
     */
    public static Response arrayList2Response (ArrayList<Map<String,Object>> map) {
        Response msg = new Response();
        msg.setResultSet(map);

        return msg;
    }

    /**
     * Returns an empty response message to be send via websocket connection.
     *
     * @return the empty response message.
     */
    public static Response getEmptyResponse () {
        Response response = new Response();
        response.setResultSet(new ArrayList<Map<String,Object>>());

        return response;
    }

    /**
     * Returns an empty set representing the JSON object.
     * @return
     */
    public static ArrayList<Map<String, Object>> emptySet () {
        return new ArrayList<Map<String,Object>>();
    }


}
