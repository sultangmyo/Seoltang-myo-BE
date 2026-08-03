package com.seoltangmyo.sugarcat.domain.auth.dto;

// 애플 공개키 하나를 나타내는 dto
public record ApplePublicKey(
        // 키 타입입니다. 보통 RSA입니다.
        String kty,

        // 키 식별자입니다. identityToken header의 kid와 매칭합니다.
        String kid,

        // 키 용도입니다. 보통 sig입니다.
        String use,

        // 알고리즘입니다. 보통 RS256입니다.
        String alg,

        // RSA 공개키의 modulus입니다.
        String n,

        // RSA 공개키의 exponent입니다.
        String e
) {
}
