// Copyright (c) Dolittle. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package io.dolittle.monitor.ingress.ping.component;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.UUID;

@Component
@Slf4j
public class KeyManager {

    private String HASH_SALT = "";
    private HashMap<String, String> challengeList = new HashMap<>();

    public KeyManager() {
        generateSalt();
        log.info("Key Manager instantiated");
    }

    @Scheduled(cron = "0 1 1 1/1 * ?")
    public void generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[10];
        random.nextBytes(bytes);
        HASH_SALT = Base64.encodeBase64String(bytes);
        log.debug("Generated salt: {}", HASH_SALT);
    }

    private String generateUID() {
        UUID uuid = UUID.randomUUID();
        return uuid.toString();
    }

    public String hashKeyWithSalt(String key) {
        return DigestUtils.md5Hex(HASH_SALT + key);
    }

    public String addChallengeKey(String host) {
        String uid = generateUID();
        String hashKeyWithSalt = hashKeyWithSalt(uid);
        challengeList.put(host, hashKeyWithSalt);
        return uid;
    }

    public Boolean verifyResponseKey(String host, String responseKey) {
        log.debug("Verifying responsekey: {} from host: {}", responseKey, host);
        String key = challengeList.get(host);
        if (key == null || key.isEmpty()) {
            return false;
        }
        challengeList.remove(host);
        log.debug("Keys to verify -  Key: {} - Response-Key: {}", key, responseKey);
        return key.equals(responseKey);
    }
}
