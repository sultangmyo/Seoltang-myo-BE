package com.seoltangmyo.sugarcat.global.config.apns;

import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

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
