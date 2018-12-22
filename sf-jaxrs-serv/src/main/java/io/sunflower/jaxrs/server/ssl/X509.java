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

package io.sunflower.jaxrs.server.ssl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.InvalidNameException;
import javax.naming.ldap.LdapName;
import javax.naming.ldap.Rdn;
import javax.security.auth.x500.X500Principal;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @author michael
 */
public class X509 {

    private static final Logger LOG = LoggerFactory.getLogger(X509.class);

    /*
     * @see {@link X509Certificate#getKeyUsage()}
     */
    private static final int KEY_USAGE__KEY_CERT_SIGN = 5;

    /*
     *
     * @see {@link X509Certificate#getSubjectAlternativeNames()}
     */
    private static final int SUBJECT_ALTERNATIVE_NAMES__DNS_NAME = 2;

    public static boolean isCertSign(X509Certificate x509) {
        boolean[] key_usage = x509.getKeyUsage();
        if ((key_usage == null) || (key_usage.length <= KEY_USAGE__KEY_CERT_SIGN)) {
            return false;
        }
        return key_usage[KEY_USAGE__KEY_CERT_SIGN];
    }

    private final X509Certificate _x509;
    private final String _alias;
    private final List<String> _hosts = new ArrayList<>();
    private final List<String> _wilds = new ArrayList<>();

    public X509(String alias, X509Certificate x509)
            throws CertificateParsingException, InvalidNameException {
        _alias = alias;
        _x509 = x509;

        // Look for alternative name extensions
        boolean named = false;
        Collection<List<?>> altNames = x509.getSubjectAlternativeNames();
        if (altNames != null) {
            for (List<?> list : altNames) {
                if (((Number) list.get(0)).intValue() == SUBJECT_ALTERNATIVE_NAMES__DNS_NAME) {
                    String cn = list.get(1).toString();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Certificate SAN alias={} CN={} in {}", alias, cn, this);
                    }
                    if (cn != null) {
                        named = true;
                        addName(cn);
                    }
                }
            }
        }

        // If no names found, look up the CN from the subject
        if (!named) {
            LdapName name = new LdapName(x509.getSubjectX500Principal().getName(X500Principal.RFC2253));
            for (Rdn rdn : name.getRdns()) {
                if (rdn.getType().equalsIgnoreCase("CN")) {
                    String cn = rdn.getValue().toString();
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("Certificate CN alias={} CN={} in {}", alias, cn, this);
                    }
                    if (cn != null && cn.contains(".") && !cn.contains(" ")) {
                        addName(cn);
                    }
                }
            }
        }
    }

    protected void addName(String cn) {
        cn = StringUtils.lowerCase(cn, Locale.ENGLISH);
        if (cn.startsWith("*.")) {
            _wilds.add(cn.substring(2));
        } else {
            _hosts.add(cn);
        }
    }

    public String getAlias() {
        return _alias;
    }

    public X509Certificate getCertificate() {
        return _x509;
    }

    public Set<String> getHosts() {
        return new HashSet<>(_hosts);
    }

    public Set<String> getWilds() {
        return new HashSet<>(_wilds);
    }

    public boolean matches(String host) {
        host = StringUtils.lowerCase(host, Locale.ENGLISH);
        if (_hosts.contains(host) || _wilds.contains(host)) {
            return true;
        }

        int dot = host.indexOf('.');
        if (dot >= 0) {
            String domain = host.substring(dot + 1);
            if (_wilds.contains(domain)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return String.format("%s@%x(%s,h=%s,w=%s)",
                getClass().getSimpleName(),
                hashCode(),
                _alias,
                _hosts,
                _wilds);
    }
}
