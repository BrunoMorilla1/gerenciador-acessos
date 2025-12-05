package com.seuprojeto.gerenciadordeacessos.core.seguranca;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Serviço de Criptografia de Nível Sênior (AES-256-GCM).
 * Responsabilidade Única: Criptografar e Descriptografar dados sensíveis (senhas).
 */
@Service
public class ServicoCriptografia {

    private static final String ALGORITMO = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12; // GCM exige 12 bytes para o IV
    private static final int AES_KEY_LENGTH = 32; // 256 bits

    @Value("${jwt.secret}") // Reutiliza a chave JWT para a chave de criptografia
    private String chaveSecreta;

    private SecretKeySpec getSecretKeySpec() {
        // Garante que a chave tenha 32 bytes (256 bits)
        byte[] keyBytes = chaveSecreta.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < AES_KEY_LENGTH) {
            keyBytes = Base64.getDecoder().decode(chaveSecreta);
        }
        if (keyBytes.length != AES_KEY_LENGTH) {
            throw new IllegalArgumentException("A chave secreta deve ter 32 bytes (256 bits) para AES-256.");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * Criptografa o texto usando AES-256-GCM.
     * O resultado inclui IV, Ciphertext e Tag, separados por um delimitador.
     * @param textoPlano O texto a ser criptografado (a senha).
     * @return O texto criptografado em formato Base64.
     */
    public String criptografar(String textoPlano) {
        try {
            byte[] iv = new byte[IV_LENGTH_BYTE];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKeySpec(), new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            byte[] cipherText = cipher.doFinal(textoPlano.getBytes(StandardCharsets.UTF_8));

            // Concatena IV e Ciphertext+Tag
            ByteBuffer byteBuffer = ByteBuffer.allocate(IV_LENGTH_BYTE + cipherText.length);
            byteBuffer.put(iv);
            byteBuffer.put(cipherText);

            return Base64.getEncoder().encodeToString(byteBuffer.array());
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criptografar a senha.", e);
        }
    }

    /**
     * Descriptografa o texto usando AES-256-GCM.
     * @param textoCifrado O texto criptografado em formato Base64.
     * @return O texto plano (a senha).
     */
    public String descriptografar(String textoCifrado) {
        try {
            byte[] decoded = Base64.getDecoder().decode(textoCifrado);

            // Verifica o tamanho mínimo (IV + Tag)
            if (decoded.length < IV_LENGTH_BYTE + (TAG_LENGTH_BIT / 8)) {
                throw new IllegalArgumentException("Texto cifrado inválido ou corrompido.");
            }

            // Extrai IV
            ByteBuffer byteBuffer = ByteBuffer.wrap(decoded);
            byte[] iv = new byte[IV_LENGTH_BYTE];
            byteBuffer.get(iv);

            // Extrai Ciphertext + Tag
            byte[] cipherTextWithTag = new byte[byteBuffer.remaining()];
            byteBuffer.get(cipherTextWithTag);

            Cipher cipher = Cipher.getInstance(ALGORITMO);
            cipher.init(Cipher.DECRYPT_MODE, getSecretKeySpec(), new GCMParameterSpec(TAG_LENGTH_BIT, iv));

            byte[] textoPlano = cipher.doFinal(cipherTextWithTag);

            return new String(textoPlano, StandardCharsets.UTF_8);
        } catch (Exception e) {
            // Em produção, esta exceção deve ser logada e tratada de forma genérica para o usuário.
            throw new RuntimeException("Erro ao descriptografar a senha. Chave inválida ou dado corrompido.", e);
        }
    }
}
