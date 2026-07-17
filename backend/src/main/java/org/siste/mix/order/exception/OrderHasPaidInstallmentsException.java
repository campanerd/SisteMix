package org.siste.mix.order.exception;

public class OrderHasPaidInstallmentsException extends RuntimeException {

    public OrderHasPaidInstallmentsException() {
        super("Não é possível alterar valor ou data do pedido: existem parcelas pagas.");
    }
}
