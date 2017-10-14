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

package io.sunflower.example.resources;

import java.io.IOException;

import io.sunflower.gizmo.Context;
import io.sunflower.gizmo.Result;
import io.sunflower.gizmo.websocket.AbstractWebSocketResource;
import io.sunflower.gizmo.websocket.TextMessage;
import io.sunflower.gizmo.websocket.WebSocketSession;

/**
 * ApplicationWsResource
 *
 * @author michael
 * @date 17/10/14 21:08
 */
public class ApplicationWsResource extends AbstractWebSocketResource {

  @Override
  public Result handshake(Context context) {
    return super.handshake(context);
  }

  @Override
  protected void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {
    System.out.println("Received Data:" + message.getPayload());
    session.sendMessage(new TextMessage(message.getPayload() + " from server!"));
  }
}
