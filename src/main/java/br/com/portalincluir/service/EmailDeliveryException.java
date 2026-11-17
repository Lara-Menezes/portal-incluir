package br.com.portalincluir.service;

public class EmailDeliveryException extends RuntimeException {
    public EmailDeliveryException(Throwable cause) {
        super("Falha no envio do e-mail pelo servidor SMTP.", cause);
    }
}
