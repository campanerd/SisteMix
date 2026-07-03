package org.siste.mix.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    // Redireciona qualquer rota sem extensão (ex: /clients, /orders) para o index.html do React.
    // Rotas da API (/api/**) têm prioridade por serem mais específicas e não chegam aqui.
    // Os caminhos do Swagger/springdoc são excluídos para não serem sobrescritos pelo forward.
    @RequestMapping(value = {
            "/{path:(?!swagger-ui|v3|api-docs|webjars)[^\\.]*}",
            "/{path:(?!swagger-ui|v3|api-docs|webjars)[^\\.]*}/**"
    })
    public String forward() {
        return "forward:/index.html";
    }
}
