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

package io.monkey.test.junit5;

import io.micronaut.aop.InterceptedProxy;
import io.micronaut.inject.FieldInjectionPoint;
import io.micronaut.test.annotation.MockBean;
import io.micronaut.test.transaction.TestTransactionInterceptor;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * @author Michael
 * Created at: 2019/2/18 13:21
 */
public class MonkeyJunit5Extension extends AbstractMonkeyExtension<ExtensionContext>
    implements BeforeAllCallback,
    TestTransactionInterceptor,
    AfterAllCallback,
    BeforeEachCallback,
    AfterEachCallback,
    ExecutionCondition,
    BeforeTestExecutionCallback,
    AfterTestExecutionCallback {

    @Override
    public void beforeAll(ExtensionContext extensionContext) {
        final Class<?> testClass = extensionContext.getRequiredTestClass();
        final MonkeyTest MonkeyTest = AnnotationSupport.findAnnotation(testClass, MonkeyTest.class).orElse(null);
        beforeClass(extensionContext, testClass, MonkeyTest);
    }

    @Override
    public void afterAll(ExtensionContext extensionContext) {
        afterClass(extensionContext);
    }

    @Override
    public void beforeEach(ExtensionContext extensionContext) {
        final Optional<Object> testInstance = extensionContext.getTestInstance();
        final Optional<? extends AnnotatedElement> testMethod = extensionContext.getTestMethod();
        beforeEach(extensionContext, testInstance.orElse(null), testMethod.orElse(null));
    }

    @Override
    public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext extensionContext) {
        final Optional<Object> testInstance = extensionContext.getTestInstance();
        if (testInstance.isPresent()) {

            final Class<?> requiredTestClass = extensionContext.getRequiredTestClass();
            if (applicationContext.containsBean(requiredTestClass)) {
                return ConditionEvaluationResult.enabled("Test bean active");
            } else {

                final boolean hasBeanDefinition = isTestSuiteBeanPresent(requiredTestClass);
                if (!hasBeanDefinition) {
                    throw new TestInstantiationException(MISCONFIGURED_MESSAGE);
                } else {
                    return ConditionEvaluationResult.disabled(DISABLED_MESSAGE);
                }

            }
        } else {
            final Class<?> testClass = extensionContext.getRequiredTestClass();
            if (AnnotationSupport.isAnnotated(testClass, MonkeyTest.class)) {
                return ConditionEvaluationResult.enabled("Test bean active");
            } else {
                return ConditionEvaluationResult.disabled(DISABLED_MESSAGE);
            }
        }
    }

    @Override
    protected void alignMocks(ExtensionContext context, Object instance) {
        if (specDefinition != null) {
            for (FieldInjectionPoint injectedField : specDefinition.getInjectedFields()) {
                final boolean isMock = applicationContext.resolveMetadata(injectedField.getType()).isAnnotationPresent(MockBean.class);
                if (isMock) {
                    final Field field = injectedField.getField();
                    field.setAccessible(true);
                    try {
                        final Object mock = field.get(instance);
                        if (mock instanceof InterceptedProxy) {
                            InterceptedProxy ip = (InterceptedProxy) mock;
                            final Object target = ip.interceptedTarget();
                            field.set(instance, target);
                        }
                    } catch (IllegalAccessException e) {
                        // continue
                    }
                }
            }
        }
    }

    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        commit();
        rollback();
    }

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        begin();
    }
}
