package com.foodstore.htmeleros.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EstadoDeserializer.class)
public enum Estado {

    PENDIENTE,
    EN_PROCESO,
    EN_CAMINO,
    ENTREGADO,

    // 🔥 NUEVO
    CANCELADO
}
