package br.ueg.progweb2.arquitetura.converters;

import br.ueg.progweb2.arquitetura.interfaces.IConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class BooleanConverter implements IConverter {
    private static final Logger LOG =
            LoggerFactory.getLogger(BooleanConverter.class);
    private List<String> trueValues = List.of("verdadeiro", "sim", "ativo", "true", "1");
    @Override
    public Object converter(String value) {
        if(Objects.nonNull(value)){
            AtomicReference<Boolean> returnValue = new AtomicReference<>(Boolean.FALSE);
            try {
                trueValues.forEach(s -> {
                    if (s.equalsIgnoreCase(value)){
                        returnValue.set(Boolean.TRUE);
                    }
                });
                return returnValue.get();
            }catch (Exception e){
                LOG.error("Erro ao Converter valor(%s) para Boolean",value);
            }
        }
        return null;
    }
}
