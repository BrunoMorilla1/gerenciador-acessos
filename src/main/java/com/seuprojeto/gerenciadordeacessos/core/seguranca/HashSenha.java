package com.seuprojeto.gerenciadordeacessos.core.seguranca;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Serviço de Hash de Senha.
 * Responsabilidade Única: Gerenciar o hash e a verificação de senhas de usuário.
 */
@Service
@RequiredArgsConstructor
public class HashSenha {

    private final PasswordEncoder passwordEncoder;

    /**
     * Gera o hash BCrypt de uma senha.
     * @param senha Senha em texto plano.
     * @return Hash BCrypt.
     */
    public String gerarHash(String senha) {
        return passwordEncoder.encode(senha);
    }

    /**
     * Verifica se a senha em texto plano corresponde ao hash.
     * @param senha Senha em texto plano.
     * @param hash Hash BCrypt.
     * @return true se corresponder, false caso contrário.
     */
    public boolean verificar(String senha, String hash) {
        return passwordEncoder.matches(senha, hash);
    }
}
