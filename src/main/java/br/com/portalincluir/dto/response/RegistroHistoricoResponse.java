package br.com.portalincluir.dto.response;

import java.beans.PropertyDescriptor;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/** Somente valores do proprio registro; nao mistura nomes atuais com revisoes antigas. */
public record RegistroHistoricoResponse(Map<String, Object> campos) {
    public static RegistroHistoricoResponse de(Object entidade) {
        BeanWrapper bean = new BeanWrapperImpl(entidade);
        Map<String, Object> campos = new LinkedHashMap<>();
        for (PropertyDescriptor propriedade : bean.getPropertyDescriptors()) {
            Class<?> tipo = propriedade.getPropertyType();
            if (tipo != null && (tipo.isPrimitive() || tipo.isEnum() || tipo == String.class
                    || Number.class.isAssignableFrom(tipo) || tipo == Boolean.class
                    || tipo.getPackageName().equals("java.time"))) {
                campos.put(propriedade.getName(), bean.getPropertyValue(propriedade.getName()));
            }
        }
        return new RegistroHistoricoResponse(java.util.Collections.unmodifiableMap(campos));
    }
}
