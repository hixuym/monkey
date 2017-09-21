/**
 * Copyright (C) 2012-2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */

package io.sunflower.gizmo.bodyparser;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import java.io.IOException;

import io.sunflower.gizmo.ContentTypes;
import io.sunflower.gizmo.Context;
import io.sunflower.gizmo.exceptions.BadRequestException;

/**
 * Built in Xml body parser.
 *
 * @author Raphael Bauer
 * @author Thibault Meyer
 * @see io.sunflower.gizmo.bodyparser.BodyParserEngine
 */
@Singleton
public class BodyParserEngineXml implements BodyParserEngine {

    private final XmlMapper xmlMapper;

    @Inject
    public BodyParserEngineXml(XmlMapper xmlMapper) {
        this.xmlMapper = xmlMapper;
    }

    public <T> T invoke(Context context, Class<T> classOfT) {
        try {
            return xmlMapper.readValue(context.getInputStream(), classOfT);
        } catch (JsonParseException | JsonMappingException e) {
            throw new BadRequestException("Error parsing incoming Xml", e);
        } catch (IOException e) {
            throw new BadRequestException("Invalid Xml document", e);
        }
    }

    public String getContentType() {
        return ContentTypes.APPLICATION_XML;
    }

}
