package com.seuprojeto.gerenciadordeacessos.dominio.acesso.modelo;

import com.seuprojeto.gerenciadordeacessos.dominio.BaseAuditoria;
import com.seuprojeto.gerenciadordeacessos.dominio.usuario.modelo.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/**
 * Entidade para armazenar as credenciais (login, senha, url).
 */
@Entity
@Table(name = "entradas_acesso")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EntradaAcesso extends BaseAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(length = 255)
    private String descricao;

    @Column(nullable = false, length = 255)
    private String url;

    @Column(nullable = false, length = 100)
    private String login;

    @Column(name = "senha_criptografada", nullable = false, length = 512)
    private String senhaCriptografada;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_visibilidade", nullable = false)
    private TipoVisibilidade tipoVisibilidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proprietario_id", nullable = false)
    private Usuario proprietario;

    @Column(name = "data_expiracao")
    private LocalDate dataExpiracao;

    public enum TipoVisibilidade {
        PESSOAL,
        COMPARTILHADA
    }
}
