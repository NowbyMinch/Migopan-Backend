package com.migopan.api.util;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

public final class SequenciaDias {

    private SequenciaDias() {
    }

    public record Resultado(int sequencia, LocalDate ultimoDia) {
    }

    public static Resultado calcular(List<LocalDate> diasComAtividade) {
        List<LocalDate> dias = diasComAtividade.stream()
                .distinct()
                .sorted(Comparator.reverseOrder())
                .toList();

        if (dias.isEmpty()) {
            return new Resultado(0, null);
        }

        LocalDate esperado = dias.get(0);
        int total = 0;
        for (LocalDate dia : dias) {
            if (!dia.equals(esperado)) {
                break; 
            }
            total++;
            esperado = esperado.minusDays(1);
        }
        return new Resultado(total, dias.get(0));
    }

    public static int efetiva(Integer armazenada, LocalDate ultimaAtividade, LocalDate hoje) {
        if (armazenada == null || ultimaAtividade == null) {
            return 0;
        }
        return ultimaAtividade.isBefore(hoje.minusDays(1)) ? 0 : armazenada;
    }
}
