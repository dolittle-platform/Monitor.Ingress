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
 * Key manager is responsible for generating and storing a unique challenge key with a state for every request.
 */
@Component
@Slf4j
public class KeyManager {

    private final HashMap<String, Boolean> _challengeKeyStore = new HashMap<>();

    public KeyManager() {
        log.info("Key Manager instantiated");
    }

    /**
     * Creates a unique key. The key is stored internally with a state of false
     * @return A unique key
     */
    public String addChallengeKeyBeforePingRequest() {
        String challengeKey = generateChallengeKey();
        log.debug("Adding challenge key: {}", challengeKey);

        _challengeKeyStore.put(challengeKey,Boolean.FALSE);
        return challengeKey;
    }

    /**
     * Checks if the key is in the internal store. If the key exists it is then removed from the internal store.
     * @param key Key to check for in the internal store.
     * @return The state of the key from the internal store
     */
    public Boolean verifyChallengeKeyAfterResponse(String key) {
        log.debug("Verifying challenge key: {}", key);
        Boolean verified = _challengeKeyStore.get(key);
        if (verified == null) {
            return false;
        }
        _challengeKeyStore.remove(key);
        return verified;
    }

    /**
     * Update the key state to true in the internal store if the key exists.
     * @param key
     * @return True if key exists in the internal store
     */
    public Boolean updateChallengeKeyWhenPingIsReceived(String key) {
        log.debug("Updating challenge key: {}", key);
        Boolean foundKey = _challengeKeyStore.get(key);
        if (foundKey == null) {
            return false;
        }
        _challengeKeyStore.put(key, Boolean.TRUE);
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
