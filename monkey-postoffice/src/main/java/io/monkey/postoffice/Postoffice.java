/**
 * Copyright (C) 2012-2017 the original author or authors. <p> Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance with the License. You
 * may obtain a copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless
 * required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.monkey.postoffice;

/**
 * Simply takes a Mail and sends it.
 * <p>
 * Can be used for instance with the implementation that uses Apache CommonsMail. Or the Mockmailer.
 * Or your own implementation.
 *
 * @author rbauer
 */
public interface Postoffice {

    /**
     * send mail
     *
     * @param mail
     * @throws Exception
     */
    void send(Mail mail) throws Exception;

}
