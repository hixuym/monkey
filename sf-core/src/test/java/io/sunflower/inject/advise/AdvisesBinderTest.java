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

package io.sunflower.inject.advise;

import com.google.inject.*;
import com.google.inject.name.Names;
import org.junit.Assert;
import org.junit.Test;

import javax.inject.Named;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.UnaryOperator;

public class AdvisesBinderTest {

    static class AdviseList implements UnaryOperator<List<String>> {

        @Override
        public List<String> apply(List<String> t) {
            t.add("a");
            return t;
        }
    }

    @Test
    public void adviseWithAdvice() {
        TypeLiteral<List<String>> LIST_TYPE_LITERAL = new TypeLiteral<List<String>>() {
        };

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(AdvisableAnnotatedMethodScanner.asModule());

                AdvisesBinder.bind(binder(), LIST_TYPE_LITERAL).toInstance(new ArrayList<>());
                AdvisesBinder.bindAdvice(binder(), LIST_TYPE_LITERAL, 0).to(AdviseList.class);
            }
        });

        List<String> list = injector.getInstance(Key.get(LIST_TYPE_LITERAL));
        Assert.assertEquals(Arrays.asList("a"), list);
    }

    @Test
    public void provisionWithoutAdviseDoesntBlowUp() {
        TypeLiteral<List<String>> LIST_TYPE_LITERAL = new TypeLiteral<List<String>>() {
        };

        Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(AdvisableAnnotatedMethodScanner.asModule());

                AdvisesBinder.bindAdvice(binder(), LIST_TYPE_LITERAL, 0).to(AdviseList.class);
            }
        });
    }

    @Test
    public void adviseWithoutQualifier() {
        TypeLiteral<List<String>> LIST_TYPE_LITERAL = new TypeLiteral<List<String>>() {
        };

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(AdvisableAnnotatedMethodScanner.asModule());

                AdvisesBinder.bind(binder(), LIST_TYPE_LITERAL).toInstance(new ArrayList<>());
                AdvisesBinder.bindAdvice(binder(), LIST_TYPE_LITERAL, 0).to(AdviseList.class);
            }

            @Advises
            UnaryOperator<List<String>> advise() {
                return list -> {
                    list.add("b");
                    return list;
                };
            }

        });

        List<String> list = injector.getInstance(Key.get(LIST_TYPE_LITERAL));
        Assert.assertEquals(Arrays.asList("a", "b"), list);
    }

    @Test
    public void adviseWithQualifier() {
        TypeLiteral<List<String>> LIST_TYPE_LITERAL = new TypeLiteral<List<String>>() {
        };

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(AdvisableAnnotatedMethodScanner.asModule());

                AdvisesBinder.bind(binder(), LIST_TYPE_LITERAL, Names.named("test"))
                        .toInstance(new ArrayList<>());
                AdvisesBinder.bindAdvice(binder(), LIST_TYPE_LITERAL, Names.named("test"), 0)
                        .to(AdviseList.class);
            }

            @Advises
            @Named("test")
            UnaryOperator<List<String>> advise() {
                return list -> {
                    list.add("b");
                    return list;
                };
            }

        });

        List<String> list = injector.getInstance(Key.get(LIST_TYPE_LITERAL, Names.named("test")));
        Assert.assertEquals(Arrays.asList("a", "b"), list);
    }

    @Test
    public void adviseNoBleedingBetweenQualifiers() {
        TypeLiteral<List<String>> LIST_TYPE_LITERAL = new TypeLiteral<List<String>>() {
        };

        Injector injector = Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {
                install(AdvisableAnnotatedMethodScanner.asModule());

                AdvisesBinder.bind(binder(), LIST_TYPE_LITERAL, Names.named("test"))
                        .toInstance(new ArrayList<>());
                AdvisesBinder.bindAdvice(binder(), LIST_TYPE_LITERAL, Names.named("test"), 0)
                        .to(AdviseList.class);

                AdvisesBinder.bind(binder(), LIST_TYPE_LITERAL).toInstance(new ArrayList<>());
                AdvisesBinder.bindAdvice(binder(), LIST_TYPE_LITERAL, 0).to(AdviseList.class);
            }

            @Advises
            @Named("test")
            UnaryOperator<List<String>> adviseQualified() {
                return list -> {
                    list.add("qualified");
                    return list;
                };
            }

            @Advises
            UnaryOperator<List<String>> adviseNotQualified() {
                return list -> {
                    list.add("not qualified");
                    return list;
                };
            }

        });

        List<String> list;
        list = injector.getInstance(Key.get(LIST_TYPE_LITERAL, Names.named("test")));
        Assert.assertEquals(Arrays.asList("a", "qualified"), list);

        list = injector.getInstance(Key.get(LIST_TYPE_LITERAL));
        Assert.assertEquals(Arrays.asList("a", "not qualified"), list);
    }
}
