package org.example.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class SpaController {

    // Redireciona qualquer rota sem extensão (ex: /clientes, /pedidos) para o index.html do React.
    // Rotas da API (/api/**) têm prioridade por serem mais específicas e não chegam aqui.
    @RequestMapping(value = {"/{path:[^\\.]*}", "/{path:[^\\.]*}/**"})
    public String forward() {
        return "forward:/index.html";
    }
}
