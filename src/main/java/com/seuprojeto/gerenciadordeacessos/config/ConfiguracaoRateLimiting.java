package com.seuprojeto.gerenciadordeacessos.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.BucketConfiguration;
import io.github.bucket4j.Refill;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Configuração do Rate Limiting (Limitação de Taxa) usando Bucket4j.
 * Responsabilidade Única: Definir a política de limitação e gerenciar os buckets por IP.
 */
@Configuration
public class ConfiguracaoRateLimiting {

    // Cache para armazenar os buckets por IP
    private final Map<String, Bucket> cacheBuckets = new ConcurrentHashMap<>();

    // Política de Limitação: 10 requisições a cada 1 minuto
    private final BucketConfiguration configuracaoBucket = BucketConfiguration.builder()
            .addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1))))
            .build();

    /**
     * Resolve o Bucket para um determinado IP. Se não existir, cria um novo.
     */
    public Bucket resolverBucket(String ip) {
        return cacheBuckets.computeIfAbsent(ip, this::criarNovoBucket);
    }

    private Bucket criarNovoBucket(String ip) {
        return Bucket.builder().addLimit(Bandwidth.classic(10, Refill.intervally(10, Duration.ofMinutes(1)))).build();
    }
}
