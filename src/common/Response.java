package common;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class Response implements Serializable {
    private final boolean success;
    private final String message;
    private final Map<String, Double> totals;

    public Response(boolean success, String message) {
        this(success, message, Collections.emptyMap());
    }

    public Response(boolean success, String message, Map<String, Double> totals) {
        this.success = success;
        this.message = message;
        this.totals = totals == null
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new LinkedHashMap<>(totals));
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public Map<String, Double> getTotals() {
        return totals;
    }
}
