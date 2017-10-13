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

package io.sunflower.guicey.visitors;

import com.google.inject.Binding;
import com.google.inject.spi.DefaultBindingTargetVisitor;
import com.google.inject.spi.DefaultElementVisitor;
import com.google.inject.spi.InstanceBinding;

/*
 * Specialized {@link DefaultElementVisitor} that formats a warning for any toInstance() binding.  
 * Use this with {@link InjectorBuilder#forEachElement} to identify situations where toInstance() bindings 
 * are used.  The assertion here is that toInstance bindings are an indicator of provisioning being
 * done outside of Guice and could involve static initialization that violates ordering guarantees
 * of a DI framework.  The recommendation is to replace toInstance() bindings with @Provides methods.
 */
public final class WarnOfToInstanceInjectionVisitor extends DefaultElementVisitor<String> {

  public <T> String visit(Binding<T> binding) {
    return binding.acceptTargetVisitor(new DefaultBindingTargetVisitor<T, String>() {
      public String visit(InstanceBinding<? extends T> instanceBinding) {
        return String.format("toInstance() at %s can force undesireable static initialization.  " +
                "Consider replacing with an @Provides method instead.",
            instanceBinding.getSource());
      }
    });
  }
}
