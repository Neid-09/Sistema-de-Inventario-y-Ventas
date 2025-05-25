package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaCaja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Caja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.AuditoriaCajaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.CajaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.UsuarioService;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.AuditoriaCajaResponseDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.CajaResponseDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.UsuarioSimplifiedDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.RegistrarAuditoriaRequestDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.math.BigDecimal;


@RestController
@RequestMapping("/api/auditorias-caja")
public class AuditoriaCajaController {

    private final AuditoriaCajaService auditoriaCajaService;
    private final CajaService cajaService;
    private final UsuarioService usuarioService;

    public AuditoriaCajaController(AuditoriaCajaService auditoriaCajaService, CajaService cajaService, UsuarioService usuarioService) {
        this.auditoriaCajaService = auditoriaCajaService;
        this.cajaService = cajaService;
        this.usuarioService = usuarioService;
    }

    private AuditoriaCajaResponseDTO mapAuditoriaCajaToAuditoriaCajaResponseDTO(AuditoriaCaja auditoria) {
        if (auditoria == null) {
            return null;
        }
        AuditoriaCajaResponseDTO dto = new AuditoriaCajaResponseDTO();
        dto.setIdAuditoria(auditoria.getIdAuditoria());

        if (auditoria.getCaja() != null) {
             Caja caja = auditoria.getCaja();
             CajaResponseDTO cajaDto = new CajaResponseDTO();
             cajaDto.setIdCaja(caja.getIdCaja());
             if(caja.getUsuario() != null) {
                 cajaDto.setUsuario(new UsuarioSimplifiedDTO(caja.getUsuario().getIdUsuario(), caja.getUsuario().getNombre()));
             }
             if (caja.getFechaApertura() != null) {
                 if (caja.getFechaApertura() instanceof java.sql.Timestamp) {
                     cajaDto.setFechaApertura(((java.sql.Timestamp) caja.getFechaApertura()).toLocalDateTime());
                 }
             }
             if (caja.getFechaCierre() != null) {
                 if (caja.getFechaCierre() instanceof java.sql.Timestamp) {
                      cajaDto.setFechaCierre(((java.sql.Timestamp) caja.getFechaCierre()).toLocalDateTime());
                 }
             }
             cajaDto.setDineroInicial(caja.getDineroInicial());
             cajaDto.setDineroTotal(caja.getDineroTotal());
             cajaDto.setEstado(caja.getEstado());
             dto.setCaja(cajaDto);
        }

        dto.setDineroEsperado(auditoria.getDineroEsperado());
        dto.setDineroReal(auditoria.getDineroReal());

        if (auditoria.getFecha() != null) {
             if (auditoria.getFecha() instanceof java.sql.Timestamp) {
                 dto.setFecha(((java.sql.Timestamp) auditoria.getFecha()).toLocalDateTime());
             }
        }

        dto.setMotivo(auditoria.getMotivo());

        if (auditoria.getUsuario() != null) {
            dto.setUsuario(new UsuarioSimplifiedDTO(auditoria.getUsuario().getIdUsuario(), auditoria.getUsuario().getNombre()));
        }

        return dto;
    }

    @GetMapping("/caja/{idCaja}")
    public ResponseEntity<List<AuditoriaCajaResponseDTO>> getAuditoriasByCaja(@PathVariable Integer idCaja) {
        try {
            Caja caja = cajaService.getCajaById(idCaja);

            List<AuditoriaCaja> auditorias = auditoriaCajaService.getAuditoriasByCaja(caja);
            List<AuditoriaCajaResponseDTO> auditoriaDTOs = auditorias.stream()
                                                                 .map(this::mapAuditoriaCajaToAuditoriaCajaResponseDTO)
                                                                 .collect(Collectors.toList());

            return new ResponseEntity<>(auditoriaDTOs, HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<List<AuditoriaCajaResponseDTO>> getAllAuditorias() {
        try {
            List<AuditoriaCaja> auditorias = auditoriaCajaService.getAllAuditorias();
             List<AuditoriaCajaResponseDTO> auditoriaDTOs = auditorias.stream()
                                                                 .map(this::mapAuditoriaCajaToAuditoriaCajaResponseDTO)
                                                                 .collect(Collectors.toList());
            return new ResponseEntity<>(auditoriaDTOs, HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<AuditoriaCajaResponseDTO> registrarAuditoria(@RequestBody RegistrarAuditoriaRequestDTO requestDTO) {
        try {
            Integer idCaja = requestDTO.getIdCaja();
            BigDecimal dineroEsperado = requestDTO.getDineroEsperado();
            BigDecimal dineroReal = requestDTO.getDineroReal();
            String motivo = requestDTO.getMotivo();
            Integer idUsuario = requestDTO.getIdUsuario();

            Caja caja = cajaService.getCajaById(idCaja);
            Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);

            AuditoriaCaja nuevaAuditoria = auditoriaCajaService.registrarAuditoria(caja, dineroEsperado, dineroReal, motivo, usuario);

            return new ResponseEntity<>(mapAuditoriaCajaToAuditoriaCajaResponseDTO(nuevaAuditoria), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }
} 