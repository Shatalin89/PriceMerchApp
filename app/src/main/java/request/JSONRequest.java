package request;

import org.json.JSONObject;

public class JSONRequest {
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

    JSONObject json;
    String URL_ID;
}
