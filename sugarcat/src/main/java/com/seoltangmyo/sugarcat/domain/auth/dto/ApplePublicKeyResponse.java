package com.seoltangmyo.sugarcat.domain.auth.dto;

import java.util.List;

public record ApplePublicKeyResponse (
        List<ApplePublicKey> keys
){
}