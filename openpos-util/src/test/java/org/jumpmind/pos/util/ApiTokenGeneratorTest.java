package org.jumpmind.pos.util;

import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.security.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.Function;

import static java.lang.Math.pow;
import static java.lang.Math.log;
import static org.junit.Assert.*;

public class ApiTokenGeneratorTest {
    /*
     * entropy >= 60 is generally reasonable for a "strong" password;
     * extending LENGTH increases entropy more than extending character pool
     */
    static final int MIN_ENTROPY = 59;

    @Test
    public void algorithmParametersProvideReasonableEntropy() {
        double actualEntropy = log(pow(ApiTokenGenerator.TOKEN_ALPHABET.length(), ApiTokenGenerator.TOKEN_LENGTH)) / log(2);
        assertTrue("TOKEN_ALPHABET with TOKEN_LENGTH entropy is only " + actualEntropy, actualEntropy > MIN_ENTROPY);
    }

    @Test
    public void generatesTokensOfConstantLength() {
        ApiTokenGenerator generator = new ApiTokenGenerator();
        String token;
        for (int i = 0; i < 10000; ++i) {
            token = generator.generateApiToken();
            assertEquals(ApiTokenGenerator.TOKEN_LENGTH, token.length());
        }
    }

    @Test
    public void canProvideSpecificRng() throws NoSuchAlgorithmException {
        SecureRandom rng = SecureRandom.getInstance(explicitAlgorithm());
        ApiTokenGenerator generator = new ApiTokenGenerator(rng);
        assertNotNull(generator.generateApiToken());
    }

    @Test
    public void canGenerateTokenFromCommandLine() {
        final PrintStream originalStdout = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            ApiTokenGenerator.main(new String[0]);
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            System.setOut(originalStdout);
            fail(ex.toString());
        } finally {
            System.setOut(originalStdout);
        }
        assertEquals(ApiTokenGenerator.TOKEN_LENGTH + System.lineSeparator().length(), captured.size());
    }

    @Test
    public void canGenerateTokenWithExplicitAlgorithmFromCommandLine() {
        final PrintStream originalStdout = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            ApiTokenGenerator.main(new String[] {explicitAlgorithm()});
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            System.setOut(originalStdout);
            fail(ex.toString());
        } finally {
            System.setOut(originalStdout);
        }
        assertEquals(ApiTokenGenerator.TOKEN_LENGTH + System.lineSeparator().length(), captured.size());
    }

    @Test
    public void canGenerateTokenWithExplicitAlgorithmAndProviderFromCommandLine() {
        final PrintStream originalStdout = System.out;
        ByteArrayOutputStream captured = new ByteArrayOutputStream();
        System.setOut(new PrintStream(captured));
        try {
            ApiTokenGenerator.main(explicitAlgorithmAndProvider());
        } catch (NoSuchAlgorithmException | NoSuchProviderException ex) {
            System.setOut(originalStdout);
            fail(ex.toString());
        } finally {
            System.setOut(originalStdout);
        }
        assertEquals(ApiTokenGenerator.TOKEN_LENGTH + System.lineSeparator().length(), captured.size());
    }

    @Test
    public void commandLineRejectsBadAlgorithm() throws NoSuchProviderException {
        try {
            ApiTokenGenerator.main(new String[] {"Not a SecureRandom algorithm"});
        } catch (NoSuchAlgorithmException ex) {
            /* passing test */
            return;
        }
        fail("main(String[]) did not throw NoSuchAlgorithmException");
    }

    @Test
    public void commandLineRejectsBadProvider() throws NoSuchAlgorithmException {
        try {
            ApiTokenGenerator.main(new String[] {explicitAlgorithm(), "Not a Provider name"});
        } catch (NoSuchProviderException ex) {
            /* passing test */
            return;
        }
        fail("main(String[]) did not throw NoSuchProviderException");
    }

    private String explicitAlgorithm() {
        /*
         * "Every implementation of the Java platform is required to support at least one strong SecureRandom implementation."
         * Choose a non-preferred algorithm if one is available.
         */
        return Security.getAlgorithms("SecureRandom").stream().reduce((a1, a2) -> a2).get();
    }

    private String[] explicitAlgorithmAndProvider() {
        String algorithm = explicitAlgorithm();
        /* guaranteed to exist; choose non-preferred provider if one is available */
        Provider provider = Arrays.stream(Security.getProviders(Collections.singletonMap("SecureRandom." + algorithm, ""))).reduce((p1, p2) -> p2).get();
        return new String[] {algorithm, provider.getName()};
    }
}
