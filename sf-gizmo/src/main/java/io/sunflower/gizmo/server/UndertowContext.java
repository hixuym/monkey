package io.sunflower.gizmo.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.inject.Inject;
import com.google.inject.Injector;
import io.sunflower.gizmo.Cookie;
import io.sunflower.gizmo.GizmoConfiguration;
import io.sunflower.gizmo.Result;
import io.sunflower.gizmo.bodyparser.BodyParserEngineManager;
import io.sunflower.gizmo.params.ParamParsers;
import io.sunflower.gizmo.session.FlashScope;
import io.sunflower.gizmo.session.Session;
import io.sunflower.gizmo.uploads.FileItem;
import io.sunflower.gizmo.uploads.ParameterFileItem;
import io.sunflower.gizmo.utils.AbstractContext;
import io.sunflower.gizmo.utils.ResponseStreams;
import io.sunflower.gizmo.validation.Validation;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.form.FormData;
import io.undertow.server.handlers.form.FormDataParser;
import io.undertow.util.HeaderValues;
import io.undertow.util.Headers;
import io.undertow.util.HttpString;

/**
 * Created by michael on 17/9/13.
 */
public class UndertowContext extends AbstractContext {

  private final String[] STRING_ARRAY = new String[0];

  private final Map<String, Object> attributes;
  private HttpServerExchange exchange;
  private FormData formData;

  @Inject
  public UndertowContext(
      BodyParserEngineManager bodyParserEngineManager,
      FlashScope flashScope,
      GizmoConfiguration configuration,
      Session session,
      Validation validation,
      Injector injector,
      ParamParsers paramParsers) {

    super(
        bodyParserEngineManager,
        flashScope,
        configuration,
        session,
        validation,
        injector,
        paramParsers);

    this.attributes = new HashMap<>();
  }

  public void init(HttpServerExchange exchange, String contextPath) {
    this.exchange = exchange;

    //enforceCorrectEncodingOfRequest();
//        requestPath = performGetRequestPath();

    // any form data should have been eagerly parsed
    this.formData = exchange.getAttachment(FormDataParser.FORM_DATA);

    super.init(contextPath, exchange.getRelativePath());
  }

  @Override
  public String getHostname() {
    return exchange.getHostName();
  }

  @Override
  public String getScheme() {
    return exchange.getRequestScheme();
  }

  @Override
  public String getRealRemoteAddr() {
    InetSocketAddress sourceAddress = exchange.getSourceAddress();
    if (sourceAddress != null) {
      InetAddress address = sourceAddress.getAddress();
      if (address != null) {
        return address.getHostAddress();
      }
    }
    return null;
  }

  @Override
  public String getMethod() {
    return exchange.getRequestMethod().toString();
  }

  @Override
  public Object getAttribute(String name) {
    return attributes.get(name);
  }

  @Override
  public <T> T getAttribute(String name, Class<T> clazz) {
    return clazz.cast(getAttribute(name));
  }

  @Override
  public void setAttribute(String name, Object value) {
    attributes.put(name, value);
  }

  @Override
  public Map<String, Object> getAttributes() {
    return attributes;
  }

  @Override
  public String getParameter(String name) {
    // Returns the value of a request parameter as a String, or null if the
    // parameter does not exist. Request parameters are extra information sent
    // with the request. For ninja (following servlet rule), parameters are contained in the
    // query string or posted form data.
    Deque<String> queryParameterValues = exchange.getQueryParameters().get(name);

    if (queryParameterValues != null && !queryParameterValues.isEmpty()) {
      return queryParameterValues.getFirst();
    } else {
      // fallback to form data
      if (this.formData != null) {
        FormData.FormValue value = this.formData.getFirst(name);
        if (value != null) {
          return value.getValue();
        }
      }
    }

    return null;
  }

  @Override
  public List<String> getParameterValues(String name) {
    List<String> values = new ArrayList<>();

    Deque<String> queryParameterValues = exchange.getQueryParameters().get(name);

    // merge values from query parameters
    if (queryParameterValues != null) {
      values.addAll(queryParameterValues);
    }

    // merge values from form data
    if (this.formData != null) {
      Deque<FormData.FormValue> formValues = this.formData.get(name);
      if (formValues != null) {
        for (FormData.FormValue formValue : formValues) {
          values.add(formValue.getValue());
        }
      }
    }

    if (values.isEmpty()) {
      return null;
    }

    return values;
  }

  @Override
  public Map<String, String[]> getParameters() {
    // build parameter map
    Map<String, String[]> parameters = new HashMap<>();

    // merge values from query parameters
    for (Map.Entry<String, Deque<String>> entry : exchange.getQueryParameters().entrySet()) {
      parameters.put(entry.getKey(), entry.getValue().toArray(STRING_ARRAY));
    }

    // merge values from form data
    if (this.formData != null) {
      Iterator<String> it = this.formData.iterator();
      while (it.hasNext()) {
        String formName = it.next();
        Deque<FormData.FormValue> formValues = this.formData.get(formName);
        UndertowHelper.createOrMerge(parameters, formName, formValues);
      }
    }

    return parameters;
  }

  @Override
  public String getHeader(String name) {
    return exchange.getRequestHeaders().getFirst(name);
  }

  @Override
  public List<String> getHeaders(String name) {
    return exchange.getRequestHeaders().get(name);
  }

  @Override
  public Map<String, List<String>> getHeaders() {
    // build map of headers
    Map<String, List<String>> headers = new HashMap<>();

    for (HeaderValues values : exchange.getRequestHeaders()) {
      headers.put(values.getHeaderName().toString(), values);
    }

    return headers;
  }

  @Override
  public Cookie getCookie(String cookieName) {
    io.undertow.server.handlers.Cookie undertowCookie
        = exchange.getRequestCookies().get(cookieName);

    if (undertowCookie == null) {
      return null;
    }

    return UndertowCookieHelper.convertUndertowCookieToNinjaCookie(undertowCookie);
  }

  @Override
  public String getCookieValue(String cookieName) {
    io.undertow.server.handlers.Cookie undertowCookie
        = exchange.getRequestCookies().get(cookieName);

    if (undertowCookie == null) {
      return null;
    }

    return undertowCookie.getValue();
  }

  @Override
  public boolean hasCookie(String cookieName) {
    return exchange.getRequestCookies().containsKey(cookieName);
  }

  @Override
  public List<Cookie> getCookies() {
    Map<String, io.undertow.server.handlers.Cookie> undertowCookies = exchange.getRequestCookies();

    if (undertowCookies == null) {
      return Collections.emptyList();
    }

    List<Cookie> gizmoCookies = new ArrayList<>(undertowCookies.size());

    for (Map.Entry<String, io.undertow.server.handlers.Cookie> entry : undertowCookies.entrySet()) {
      Cookie ninjaCookie = UndertowCookieHelper
          .convertUndertowCookieToNinjaCookie(entry.getValue());
      gizmoCookies.add(ninjaCookie);
    }

    return gizmoCookies;
  }

  @Override
  public void addCookie(Cookie cookie) {
    io.undertow.server.handlers.Cookie undertowCookie
        = UndertowCookieHelper.convertNinjaCookieToUndertowCookie(cookie);

    exchange.getResponseCookies().put(undertowCookie.getName(), undertowCookie);
  }

  @Override
  public InputStream getInputStream() throws IOException {
    return exchange.getInputStream();
  }

  @Override
  public BufferedReader getReader() throws IOException {
    // TODO: charset issues?
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  @Override
  protected ResponseStreams finalizeHeaders(Result result, Boolean handleFlashAndSessionCookie) {
    // delegate cookie, session, and flash to parent
    super.finalizeHeaders(result, handleFlashAndSessionCookie);

    exchange.setStatusCode(result.getStatusCode());

    // copy headers
    for (Map.Entry<String, String> header : result.getHeaders().entrySet()) {
      exchange.getResponseHeaders()
          .add(new HttpString(header.getKey()), header.getValue());
    }

    // charset in use
    final String charset = Optional.ofNullable(result.getCharset()).orElse("utf-8");

    // build content-type header (but only if it does not yet exist)
    if (result.getContentType() != null) {
      String contentTypeHeader = new StringBuilder()
          .append(result.getContentType())
          .append("; charset=")
          .append(charset)
          .toString();

      exchange.getResponseHeaders().put(
          Headers.CONTENT_TYPE, contentTypeHeader);
    }

    return new ResponseStreams() {

      @Override
      public OutputStream getOutputStream() throws IOException {
        return exchange.getOutputStream();
      }

      @Override
      public Writer getWriter() throws IOException {
        return new OutputStreamWriter(exchange.getOutputStream(), charset);
      }
    };
  }

  @Override
  public String getRequestContentType() {
    return exchange.getRequestHeaders().getFirst("Content-Type");
  }

  @Override
  public boolean isMultipart() {
    // logic extracted from ServletFileUpload.isMultipartContent
    if (!"post".equalsIgnoreCase(getMethod())) {
      return false;
    }

    String contentTypeHeader = this.getRequestContentType();

    if (contentTypeHeader == null) {
      return false;
    } else if (contentTypeHeader.toLowerCase().startsWith("multipart/")) {
      return true;
    }
    return false;
  }

  /**
   private String performGetRequestPath() {
   // http://stackoverflow.com/questions/966077/java-reading-undecoded-url-from-servlet

   // this one is unencoded:
   String unencodedContextPath = httpServletRequest.getContextPath();

   // this one is unencoded, too, but may containt the context:
   String fullUnencodedUri = httpServletRequest.getRequestURI();

   String result = fullUnencodedUri.substring(unencodedContextPath
   .length());

   return result;
   }
   */

  /**
   * Get the underlying Undertow <code>HttpServerExchange</code> object.
   *
   * @return The underlying Undertow <code>HttpServerExchange</code> object.
   */
  public HttpServerExchange getExchange() {
    return this.exchange;
  }


  /**
   */
  @Override
  public FileItem getParameterAsFileItem(String name) {
    if (this.formData == null) {
      return null;
    }

    Iterator<String> it = this.formData.iterator();
    while (it.hasNext()) {
      String formName = it.next();
      if (formName.equals(name)) {
        Deque<FormData.FormValue> formValues = this.formData.get(formName);
        ParameterFileItem fileItem = UndertowHelper.getFileItem(formName, formValues);
        if (fileItem != null) {
          return fileItem;
        }
      }
    }

    // no file upload found with the given name
    return null;
  }

  /**
   */
  @Override
  public List<FileItem> getParameterAsFileItems(String name) {
    if (this.formData == null) {
      return Collections.emptyList();
    }

    List<FileItem> fileItemList = new ArrayList<FileItem>();
    Iterator<String> it = this.formData.iterator();
    while (it.hasNext()) {
      String formName = it.next();
      if (formName.equals(name)) {
        Deque<FormData.FormValue> formValues = this.formData.get(formName);
        UndertowHelper.populateFileItemList(fileItemList, formName, formValues);
      }
    }

    return fileItemList;
  }

  /**
   */
  @Override
  public Map<String, List<FileItem>> getParameterFileItems() {
    Map<String, List<FileItem>> fileItemMap = new HashMap<>();

    if (this.formData != null) {
      Iterator<String> it = this.formData.iterator();
      while (it.hasNext()) {
        String formName = it.next();
        Deque<FormData.FormValue> formValues = this.formData.get(formName);
        UndertowHelper.populateFileItemMap(fileItemMap, formName, formValues);
      }
    }

    return fileItemMap;
  }

  @Override
  public void cleanup() {
    // do nothing for right now...
  }

}
