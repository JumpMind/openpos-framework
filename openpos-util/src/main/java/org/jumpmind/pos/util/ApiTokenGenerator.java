package org.jumpmind.pos.util;

import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.stream.Collectors;

/**
 * Generates secure tokens for accessing the JMC APIs.
 *
 * <p>Tokens are 20-character alphanumeric, which are relatively compact and have no need for escaping in any data interchange format.
 * Token entropy is &gt;100 bits ({@code log2(pow(62, 20))}), more than sufficient to qualify as "strong."</p>
 */
public class ApiTokenGenerator {
    static final String TOKEN_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

    static final int TOKEN_LENGTH = 20;

    private final SecureRandom rng;

    /**
     * Command-line API token generation.
     *
     * @param args algorithm and provider names (both optional)
     * @throws NoSuchAlgorithmException if the algorithm name is not recognized
     * @throws NoSuchProviderException if the provider name is not recognized
     */
    public static void main(final String[] args) throws NoSuchAlgorithmException, NoSuchProviderException {
        SecureRandom srand = null;
        switch (args.length) {
            case 0:
                /* use the platform default implementation (common case) */
                srand = new SecureRandom();
                break;
            case 1:
                srand = SecureRandom.getInstance(/* algorithm */args[0]);
                break;
            case 2:
                srand = SecureRandom.getInstance(/* algorithm */args[0], /* provider */args[1]);
                break;
            default:
                System.err.printf("USAGE: java %s [algorithm[ provider]]\n", ApiTokenGenerator.class.getName());
                System.exit(1);
        }

        System.out.println(new ApiTokenGenerator(srand).generateApiToken());
    }

    public ApiTokenGenerator() {
        this(new SecureRandom());
    }

    public ApiTokenGenerator(final SecureRandom rng) {
        this.rng = rng;
    }

    public String generateApiToken() {
        return rng.ints(TOKEN_LENGTH, 0, TOKEN_ALPHABET.length()).mapToObj(TOKEN_ALPHABET::charAt).map(Object::toString).collect(Collectors.joining());
    }
}
