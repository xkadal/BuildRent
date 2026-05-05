package com.vlad.buildrent.service.email;

import com.vlad.buildrent.domain.Rental;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class EmailTemplateRenderer {

    private final TemplateEngine templateEngine;

    public String render(String templateName, Rental rental, String subjectHint) {
        Context ctx = new Context(new Locale("uk"));
        ctx.setVariables(Map.of(
                "rental", rental,
                "client", rental.getClient(),
                "items", rental.getItems(),
                "subject", subjectHint
        ));
        return templateEngine.process("email/" + templateName, ctx);
    }
}
