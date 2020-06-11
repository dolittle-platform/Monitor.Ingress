// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.moose.pinger.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.UUID;

/**
 * Key manager is responsible for generating and storing a unique challenge key for every request.
 * The manger also updates and verifies the keys in the store.
 */
@Component
@Slf4j
public class KeyManager {

    private final HashMap<String, Boolean> _challengeList = new HashMap<>();

    public KeyManager() {
        log.info("Key Manager instantiated");
    }

    public String addChallengeKeyBeforePingRequest() {
        String challengeKey = generateChallengeKey();
        log.debug("Adding challenge key: {}", challengeKey);

        _challengeList.put(challengeKey,Boolean.FALSE);
        return challengeKey;
    }

    public Boolean verifyChallengeKeyAfterResponse(String key) {
        log.debug("Verifying challenge key: {}", key);
        Boolean verified = _challengeList.get(key);
        if (verified == null) {
            return false;
        }
        _challengeList.remove(key);
        return verified;
    }

    public Boolean updateChallengeKeyWhenPingIsReceived(String key) {
        log.debug("Updating challenge key: {}", key);
        Boolean foundKey = _challengeList.get(key);
        if (foundKey == null) {
            return false;
        }
        _challengeList.put(key, Boolean.TRUE);
        return foundKey;
    }

    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[10];
        random.nextBytes(bytes);
        String salt = Base64.encodeBase64String(bytes);
        log.debug("Generated salt: {}", salt);
        return salt;
    }

    private String generateUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    private String generateChallengeKey() {
        String uid = generateUID();
        String salt = generateSalt();
        return DigestUtils.md5Hex(salt + uid);
    }

}
