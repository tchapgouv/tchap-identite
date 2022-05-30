package org.beta.tchap.identite.utils;

import com.google.common.hash.Hashing;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import static org.beta.tchap.identite.utils.Constants.TCHAP_LOG_SENSITIVE_DATA;

public class LoggingUtils {

    Boolean logSensitiveData;
    public static String HIDDEN_DATA = "******";

    LoggingUtils(){
        String sensitive_data_env = Environment.getenv(TCHAP_LOG_SENSITIVE_DATA);
        this.logSensitiveData = sensitive_data_env != null && "true".equals(sensitive_data_env.toLowerCase(Locale.ROOT));
    }

    public String logOrHide(String data) {
        if (logSensitiveData) {
            return data;
        }else{
            return HIDDEN_DATA;
        }
    }

    public String logOrHash(String data) {
        if (logSensitiveData) {
            return data;
        }
    return Hashing.sha256()
            .hashString(data, StandardCharsets.UTF_8)
            .toString();
    }
}
