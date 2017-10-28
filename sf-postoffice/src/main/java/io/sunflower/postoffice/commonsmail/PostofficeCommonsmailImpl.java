/**
 * Copyright (C) 2012-2017 the original author or authors. <p> Licensed under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except in compliance with the License. You
 * may obtain a copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless
 * required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package io.sunflower.postoffice.commonsmail;

import io.sunflower.postoffice.Mail;
import io.sunflower.postoffice.Postoffice;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.MultiPartEmail;

import javax.mail.internet.AddressException;
import java.util.Optional;

public class PostofficeCommonsmailImpl implements Postoffice {

    private final CommonsmailHelper commonsmailHelper;

    private final String smtpHost;
    private final int smtpPort;
    private final boolean smtpSsl;
    private final Optional<String> smtpUser;
    private final Optional<String> smtpPassword;
    private final boolean smtpDebug;


    // May be used for test
    public PostofficeCommonsmailImpl(String smtpHost,
                                     int smtpPort, boolean smtpSsl, Optional<String> smtpUser, Optional<String> smtpPassword,
                                     boolean smtpDebug) {
        this.commonsmailHelper = new CommonsmailHelperImpl();
        this.smtpHost = smtpHost;
        this.smtpPort = smtpPort;
        this.smtpSsl = smtpSsl;
        this.smtpUser = smtpUser;
        this.smtpPassword = smtpPassword;
        this.smtpDebug = smtpDebug;
    }

    @Override
    public void send(Mail mail) throws EmailException, AddressException {

        // create a correct multipart email based on html / txt content:
        MultiPartEmail multiPartEmail = commonsmailHelper.createMultiPartEmailWithContent(mail);

        // fill the from, to, bcc, css and all other fields:
        commonsmailHelper.doPopulateMultipartMailWithContent(multiPartEmail, mail);

        // set server parameters so we can send the MultiPartEmail:
        commonsmailHelper.doSetServerParameter(multiPartEmail, smtpHost, smtpPort, smtpSsl,
                smtpUser, smtpPassword, smtpDebug);

        // And send it:
        multiPartEmail.send();
    }

}
