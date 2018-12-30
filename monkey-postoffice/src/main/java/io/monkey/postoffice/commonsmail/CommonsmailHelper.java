/**
 * Copyright (C) 2012-2017 the original author or authors. <p> Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance with the License. You
 * may obtain a copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless
 * required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.monkey.postoffice.commonsmail;

import io.monkey.postoffice.Mail;
import io.monkey.postoffice.common.Tuple;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import javax.mail.internet.AddressException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * @author michael
 */
public interface CommonsmailHelper {

    void doPopulateMultipartMailWithContent(MultiPartEmail multiPartEmail, Mail mail)
            throws AddressException, EmailException;

    /**
     * Creates a MultiPartEmail. Selects the correct implementation regarding html (MultiPartEmail)
     * and/or txt content or both.
     * <p>
     * Populates the mutlipart email accordingly with the txt / html content.
     */
    MultiPartEmail createMultiPartEmailWithContent(Mail mail) throws EmailException;

    void doSetServerParameter(MultiPartEmail multiPartEmail, String smtpHost,
                              Integer smtpPort, Boolean smtpSsl, Optional<String> smtpUser, Optional<String> smtpPassword,
                              Boolean smtpDebug);

    List<Tuple<String, String>> createListOfAddresses(Collection<String> emails)
            throws AddressException;

    Tuple<String, String> createValidEmailFromString(String email) throws AddressException;

}
