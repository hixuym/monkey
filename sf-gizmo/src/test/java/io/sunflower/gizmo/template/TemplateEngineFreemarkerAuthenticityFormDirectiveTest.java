package io.sunflower.gizmo.template;

import com.google.common.collect.Maps;

import freemarker.core.Environment;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.StringWriter;
import java.util.Map;

import io.sunflower.gizmo.Context;
import io.sunflower.gizmo.session.Session;
import io.sunflower.gizmo.template.directives.TemplateEngineFreemarkerAuthenticityFormDirective;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * @author svenkubiak
 */
@RunWith(MockitoJUnitRunner.class)
public class TemplateEngineFreemarkerAuthenticityFormDirectiveTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Mock
    Context context;

    @Mock
    Session session;

    StringWriter stringWriter = new StringWriter();

    Environment environment = new Environment(Mockito.mock(Template.class), null, stringWriter);

    TemplateEngineFreemarkerAuthenticityFormDirective templateEngineFreemarkerAuthenticityFormDirective;

    Map<String, String> parameters = Maps.newHashMap();

    @Before
    public void before() {
        when(context.getSession()).thenReturn(session);
        when(session.getAuthenticityToken()).thenReturn("12345");

        templateEngineFreemarkerAuthenticityFormDirective = new TemplateEngineFreemarkerAuthenticityFormDirective(context);
    }

    @Test
    public void testThatAuthenticityTokenIsNotCalledInConstructor() throws Exception {
        Mockito.verify(session, Mockito.never()).getAuthenticityToken();
    }

    @Test
    public void testThatItWorks() throws Exception {
        TemplateModel[] loopVars = new TemplateModel[0];

        templateEngineFreemarkerAuthenticityFormDirective.execute(environment, parameters, loopVars, null);

        assertThat(
            stringWriter.toString(),
            equalTo("<input type=\"hidden\" value=\"12345\" name=\"authenticityToken\" />"));
        Mockito.verify(session).getAuthenticityToken();
    }


    @Test
    public void testThatParamsThrowException() throws Exception {
        thrown.expect(TemplateException.class);
        parameters.put("foo", "bar");

        templateEngineFreemarkerAuthenticityFormDirective.execute(null, parameters, null, null);
    }

    @Test
    public void testThatLoopVarsThrowException() throws Exception {
        TemplateModel[] loopVars = new TemplateModel[1];
        thrown.expect(TemplateException.class);
        loopVars[0] = new TemplateModel() {
        };

        templateEngineFreemarkerAuthenticityFormDirective.execute(null, parameters, loopVars, null);
    }
}