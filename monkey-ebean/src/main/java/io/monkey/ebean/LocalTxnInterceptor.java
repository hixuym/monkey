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

package io.monkey.ebean;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.TxScope;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author michael
 * created on 16/12/7
 */
public class LocalTxnInterceptor implements MethodInterceptor {

    private static class Internal {
    }

    @Override
    public Object invoke(MethodInvocation invocation) {

        Transactional transactional = readTransactionMetadata(invocation);

        TxScope txScope = new TxScope();
        txScope.setType(transactional.type());
        txScope.setIsolation(transactional.isolation());
        txScope.setBatch(transactional.batch());
        txScope.setBatchOnCascade(transactional.batchOnCascade());
        txScope.setBatchSize(transactional.batchSize());
        txScope.setServerName(transactional.serverName());
        txScope.setFlushOnQuery(transactional.flushOnQuery());
        txScope.setProfileId(transactional.profileId());
        txScope.setLabel(transactional.label());
        txScope.setSkipCache(transactional.skipCache());
        txScope.setReadOnly(transactional.readOnly());

        txScope.setNoRollbackFor(transactional.noRollbackFor());
        txScope.setRollbackFor(transactional.rollbackFor());


        if (!transactional.getGeneratedKeys()) {
            txScope.setSkipGeneratedKeys();
        }

        EbeanServer ebeanServer = Ebean.getServer(txScope.getServerName());

        return ebeanServer.executeCall(txScope, () -> {
            try {
                return invocation.proceed();
            } catch (Throwable throwable) {
                throw new Exception(throwable);
            }
        });

    }

    private Transactional readTransactionMetadata(MethodInvocation methodInvocation) {
        Transactional transactional;

        Method method = methodInvocation.getMethod();
        Class<?> targetClass = methodInvocation.getThis().getClass();

        transactional = method.getAnnotation(Transactional.class);
        if (null == transactional) {
            // If none on method, try the class.
            transactional = targetClass.getAnnotation(Transactional.class);
        }

        if (null == transactional) {
            // If there is no transactional annotation present, use the default
            transactional = Internal.class.getAnnotation(Transactional.class);
        }

        return transactional;
    }

}