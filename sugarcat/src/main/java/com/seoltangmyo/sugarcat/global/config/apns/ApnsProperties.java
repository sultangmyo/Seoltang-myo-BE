package com.seoltangmyo.sugarcat.global.config.apns;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "apple.apns")
public class ApnsProperties {

    private String bundleId;
    private String keyId;
    private String teamId;
    private String privateKeyPath;
    private boolean production;

}
