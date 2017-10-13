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

package io.sunflower.gizmo.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.google.inject.Injector;
import io.sunflower.gizmo.Cookie;
import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.gizmo.bodyparser.BodyParserEngineManager;
import io.sunflower.gizmo.params.ParamParsers;
import io.sunflower.gizmo.session.FlashScope;
import io.sunflower.gizmo.session.Session;
import io.sunflower.gizmo.uploads.FileItem;
import io.sunflower.gizmo.validation.Validation;

/**
 * Used for mocking an AbstractContext in unit tests.
 */
public class AbstractContextImpl extends AbstractContext {

  public AbstractContextImpl(BodyParserEngineManager bodyParserEngineManager, FlashScope flashScope,
      GizmoConfiguration configuration, Session session, Validation validation, Injector injector,
      ParamParsers paramParsers) {
    super(bodyParserEngineManager, flashScope, configuration, session, validation, injector,
        paramParsers);
  }

  @Override
  protected String getRealRemoteAddr() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getRequestContentType() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getHostname() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getScheme() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Cookie getCookie(String cookieName) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean hasCookie(String cookieName) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<Cookie> getCookies() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getParameter(String name) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<String> getParameterValues(String name) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Map<String, String[]> getParameters() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getHeader(String name) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<String> getHeaders(String name) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Map<String, List<String>> getHeaders() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public InputStream getInputStream() throws IOException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public BufferedReader getReader() throws IOException {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public boolean isMultipart() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public String getMethod() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Object getAttribute(String name) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void setAttribute(String name, Object value) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Map<String, Object> getAttributes() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void addCookie(Cookie cookie) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public FileItem getParameterAsFileItem(String name) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public List<FileItem> getParameterAsFileItems(String name) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public Map<String, List<FileItem>> getParameterFileItems() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  @Override
  public void cleanup() {
    throw new UnsupportedOperationException("Not supported yet.");
  }

}
