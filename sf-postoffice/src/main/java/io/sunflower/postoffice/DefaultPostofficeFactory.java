/*
 * Copyright (C) 2017. the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sunflower.postoffice;

import java.util.Optional;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import io.sunflower.postoffice.commonsmail.PostofficeCommonsmailImpl;

@JsonTypeName("commons-mail")
public class DefaultPostofficeFactory implements PostofficeFactory {

  @NotNull
  private String smtpHost;
  @NotNull
  private int smtpPort;
  private boolean smtpSsl;
  private Optional<String> smtpUser;
  private Optional<String> smtpPassword;
  private boolean smtpDebug = false;

  @Override
  public Postoffice build() {
    return new PostofficeCommonsmailImpl(
        getSmtpHost(),
        getSmtpPort(),
        isSmtpSsl(),
        getSmtpUser(),
        getSmtpPassword(),
        isSmtpDebug()
    );
  }

  @JsonProperty
  public String getSmtpHost() {
    return smtpHost;
  }

  @JsonProperty
  public void setSmtpHost(String smtpHost) {
    this.smtpHost = smtpHost;
  }

  @JsonProperty
  public int getSmtpPort() {
    return smtpPort;
  }

  @JsonProperty
  public void setSmtpPort(int smtpPort) {
    this.smtpPort = smtpPort;
  }

  @JsonProperty
  public boolean isSmtpSsl() {
    return smtpSsl;
  }

  @JsonProperty
  public void setSmtpSsl(boolean smtpSsl) {
    this.smtpSsl = smtpSsl;
  }

  @JsonProperty
  public Optional<String> getSmtpUser() {
    return smtpUser;
  }

  @JsonProperty
  public void setSmtpUser(Optional<String> smtpUser) {
    this.smtpUser = smtpUser;
  }

  @JsonProperty
  public Optional<String> getSmtpPassword() {
    return smtpPassword;
  }

  @JsonProperty
  public void setSmtpPassword(Optional<String> smtpPassword) {
    this.smtpPassword = smtpPassword;
  }

  @JsonProperty
  public boolean isSmtpDebug() {
    return smtpDebug;
  }

  @JsonProperty
  public void setSmtpDebug(boolean smtpDebug) {
    this.smtpDebug = smtpDebug;
  }
}
