package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.sql.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VendedorDTO {
    private Integer idVendedor;
    private Integer idUsuario;
    private UsuarioSimplifiedDTO usuario;
    private BigDecimal objetivoVentas;
    private Date fechaContratacion;
}
