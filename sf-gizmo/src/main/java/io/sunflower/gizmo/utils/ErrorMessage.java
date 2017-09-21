/*
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sunflower.gizmo.utils;

import com.google.common.base.MoreObjects;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * A pojo to be renderd as Json or Xml. Used for instance to display error messages when a route is not found.
 */
public class ErrorMessage {
    private final int code;
    private final String message;
    private final String details;

    public ErrorMessage(String message) {
        this(500, message);
    }

    public ErrorMessage(int code, String message) {
        this(code, message, null);
    }

    @JsonCreator
    public ErrorMessage(@JsonProperty("code") int code, @JsonProperty("message") String message,
                        @JsonProperty("details") String details) {
        this.code = code;
        this.message = message;
        this.details = details;
    }

    @JsonProperty("code")
    public Integer getCode() {
        return code;
    }

    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @JsonProperty("details")
    public String getDetails() {
        return details;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        final ErrorMessage other = (ErrorMessage) obj;
        return Objects.equals(code, other.code)
            && Objects.equals(message, other.message)
            && Objects.equals(details, other.details);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, message, details);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this).add("code", code)
            .add("message", message).add("details", details).toString();
    }
}