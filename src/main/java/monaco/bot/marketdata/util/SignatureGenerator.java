package monaco.bot.marketdata.util;


import lombok.experimental.UtilityClass;
import org.apache.commons.codec.digest.HmacUtils;

@UtilityClass
public class SignatureGenerator {
    private static final String ALGORITHM = "HmacSHA256";

    public static String generateSignature(String secretKey, String params)  {
        return new HmacUtils(ALGORITHM, secretKey).hmacHex(params);
    }
}
