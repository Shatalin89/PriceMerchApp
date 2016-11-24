package request;

import org.json.JSONObject;

public class JSONRequest {

    JSONObject json;
    String URL_ID;
    String typeRequest;

    public JSONObject getJson() {
        return json;
    }

    public void setJson(JSONObject json) {
        this.json = json;
    }

    public String getURL_ID() {
        return URL_ID;
    }

    public void setURL_ID(String URL_ID) {
        this.URL_ID = URL_ID;
    }


    public String getTypeRequest() {
        return typeRequest;
    }

    public void setTypeRequest(String typeRequest) {
        this.typeRequest = typeRequest;
    }
}
