package com.eventcommerce.common.util;

import java.util.UUID;

public class IdGenerator {

    public static String generateEventId() {
        return UUID.randomUUID().toString();
    }

}
