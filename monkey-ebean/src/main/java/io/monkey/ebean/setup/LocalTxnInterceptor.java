/*
 * Copyright 2018-2023 Monkey, Inc
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.monkey.ebean.setup;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.TxScope;
import io.monkey.ebean.Txn;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.lang.reflect.Method;

/**
 * @author michael
 * created on 16/12/7
 */
class LocalTxnInterceptor implements MethodInterceptor {

    private static class Internal {
    }

    @Override
    public Object invoke(MethodInvocation invocation) {

        Txn txn = readTransactionMetadata(invocation);

        TxScope txScope = new TxScope();
        txScope.setType(txn.type());
        txScope.setIsolation(txn.isolation());
        txScope.setBatch(txn.batch());
        txScope.setBatchOnCascade(txn.batchOnCascade());
        txScope.setBatchSize(txn.batchSize());
        txScope.setServerName(txn.dbName());
        txScope.setFlushOnQuery(txn.flushOnQuery());
        txScope.setProfileId(txn.profileId());
        txScope.setLabel(txn.label());
        txScope.setSkipCache(txn.skipCache());
        txScope.setReadOnly(txn.readOnly());

        txScope.setNoRollbackFor(txn.noRollbackFor());
        txScope.setRollbackFor(txn.rollbackFor());


        if (!txn.getGeneratedKeys()) {
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

    private Txn readTransactionMetadata(MethodInvocation methodInvocation) {
        Txn txn;

        Method method = methodInvocation.getMethod();
        Class<?> targetClass = methodInvocation.getThis().getClass();

        txn = method.getAnnotation(Txn.class);
        if (null == txn) {
            // If none on method, try the class.
            txn = targetClass.getAnnotation(Txn.class);
        }

        if (null == txn) {
            // If there is no transactional annotation present, use the default
            txn = Internal.class.getAnnotation(Txn.class);
        }

        return txn;
    }

}
