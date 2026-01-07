package com.kt.common;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.jupiter.api.Tag;

@Retention(RetentionPolicy.RUNTIME)
@Tag("sendEmail")
public @interface SendEmailTest {
}
