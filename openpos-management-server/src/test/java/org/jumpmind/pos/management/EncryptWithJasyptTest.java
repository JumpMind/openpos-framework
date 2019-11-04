package org.jumpmind.pos.management;

import static org.assertj.core.api.Assertions.assertThat;

import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.junit.Test;

public class EncryptWithJasyptTest implements EncryptionTestConstants {
    
    @Test
    public void encryptKeystorePasswordTest() {
        StandardPBEStringEncryptor passwordEncryptor = new StandardPBEStringEncryptor();
        passwordEncryptor.setPassword(ENCRYPTOR_PASSWORD);
        passwordEncryptor.setAlgorithm(ENCYPTION_ALGORITHM);
        
        String encryptedPassword = passwordEncryptor.encrypt(TEST_KEYSTORE_PASSWORD);
        System.out.println("Encrypted password value: " + encryptedPassword);
        assertThat(encryptedPassword).isNotEqualTo(TEST_KEYSTORE_PASSWORD);
        assertThat(passwordEncryptor.decrypt(encryptedPassword)).isEqualTo(TEST_KEYSTORE_PASSWORD);
    }

}
