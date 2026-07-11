package com.seoltangmyo.sugarcat.global.config.apns;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(ApnsProperties.class)
public class ApnsClientConfig {

    private final ApnsProperties apnsProperties;

    @Bean
    public ApnsClient apnsClient() throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        String apnsServer = apnsProperties.isProduction()
                ? ApnsClientBuilder.PRODUCTION_APNS_HOST
                : ApnsClientBuilder.DEVELOPMENT_APNS_HOST;

        // 실제 서버가 어떤 APNs 환경으로 뜨는지 확인한다.
        log.info("[APNs 설정] production={}, server={}, teamId={}, keyId={}, bundleId={}, privateKeyPath={}",
                apnsProperties.isProduction(),
                apnsServer,
                apnsProperties.getTeamId(),
                apnsProperties.getKeyId(),
                apnsProperties.getBundleId(),
                apnsProperties.getPrivateKeyPath()
        );

        ApnsSigningKey signingKey = ApnsSigningKey.loadFromPkcs8File(
                new File(apnsProperties.getPrivateKeyPath()),
                apnsProperties.getTeamId(),
                apnsProperties.getKeyId()
        );

        return new ApnsClientBuilder()
                .setApnsServer(apnsServer)
                .setSigningKey(signingKey)
                .build();
    }
}
