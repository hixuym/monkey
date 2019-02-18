/*
 * Copyright 2017-2019 original authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.monkey.ebean.setup;

import io.ebean.Ebean;
import io.ebean.EbeanServer;
import io.ebean.TxScope;
import io.ebean.annotation.PersistBatch;
import io.ebean.annotation.TxIsolation;
import io.ebean.annotation.TxType;
import io.micronaut.aop.MethodInterceptor;
import io.micronaut.aop.MethodInvocationContext;
import io.micronaut.core.annotation.AnnotationMetadata;
import io.monkey.ebean.Txn;

import javax.inject.Singleton;

/**
 * @author Michael
 * Created at: 2019/2/18 22:28
 */
@Singleton
public class LocalTxnInterceptor implements MethodInterceptor<Object, Object> {

    @Override
    public Object intercept(MethodInvocationContext<Object, Object> context) {

        AnnotationMetadata am = context.getExecutableMethod();

        TxScope txScope = new TxScope();
        am.getValue(Txn.class, "type", TxType.class).ifPresent(txScope::setType);
        am.getValue(Txn.class, "batch", PersistBatch.class).ifPresent(txScope::setBatch);
        am.getValue(Txn.class, "batchOnCascade", PersistBatch.class).ifPresent(txScope::setBatchOnCascade);
        am.getValue(Txn.class, "batchSize", int.class).ifPresent(txScope::setBatchSize);
        am.getValue(Txn.class, "isolation", TxIsolation.class).ifPresent(txScope::setIsolation);
        am.getValue(Txn.class, "flushOnQuery", boolean.class).ifPresent(txScope::setFlushOnQuery);
        am.getValue(Txn.class, "readOnly", boolean.class).ifPresent(txScope::setReadOnly);
        am.getValue(Txn.class, "skipCache", boolean.class).ifPresent(txScope::setSkipCache);
        am.getValue(Txn.class, "label", String.class).ifPresent(txScope::setLabel);
        am.getValue(Txn.class, "profileId", int.class).ifPresent(txScope::setProfileId);
        am.getValue(Txn.class, "rollbackFor", Class[].class).ifPresent(txScope::setRollbackFor);
        am.getValue(Txn.class, "noRollbackFor", Class[].class).ifPresent(txScope::setNoRollbackFor);

        am.getValue(Txn.class, "dbName", String.class).ifPresent(txScope::setServerName);
        am.getValue(Txn.class, "getGeneratedKeys", boolean.class).ifPresent(b -> {
            if (!b) {
                txScope.setSkipGeneratedKeys();
            }
        });

        EbeanServer ebeanServer = Ebean.getServer(txScope.getServerName());

        return ebeanServer.executeCall(txScope, () -> {
            try {
                return context.proceed();
            } catch (Throwable throwable) {
                throw new Exception(throwable);
            }
        });
    }

}
