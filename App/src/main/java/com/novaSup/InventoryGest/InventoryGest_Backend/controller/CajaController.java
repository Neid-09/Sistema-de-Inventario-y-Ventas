package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Caja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.CajaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.UsuarioService;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.AbrirCajaRequestDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.CerrarCajaRequestDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.CajaResponseDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.UsuarioSimplifiedDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/cajas")
public class CajaController {

    private final CajaService cajaService;
    private final UsuarioService usuarioService;

    public CajaController(CajaService cajaService, UsuarioService usuarioService) {
        this.cajaService = cajaService;
        this.usuarioService = usuarioService;
    }

    private CajaResponseDTO mapCajaToCajaResponseDTO(Caja caja) {
        if (caja == null) {
            return null;
        }
        CajaResponseDTO dto = new CajaResponseDTO();
        dto.setIdCaja(caja.getIdCaja());
        if (caja.getUsuario() != null) {
            dto.setUsuario(new UsuarioSimplifiedDTO(caja.getUsuario().getIdUsuario(), caja.getUsuario().getNombre()));
        }
        if (caja.getFechaApertura() != null) {
            dto.setFechaApertura(caja.getFechaApertura().toLocalDateTime());
        }
        if (caja.getFechaCierre() != null) {
            dto.setFechaCierre(caja.getFechaCierre().toLocalDateTime());
        }
        dto.setDineroInicial(caja.getDineroInicial());
        dto.setDineroTotal(caja.getDineroTotal());
        dto.setEstado(caja.getEstado());
        return dto;
    }

    @PostMapping("/abrir")
    public ResponseEntity<CajaResponseDTO> abrirCaja(@RequestBody AbrirCajaRequestDTO request) {
        try {
            Integer idUsuario = request.getIdUsuario();
            if (idUsuario == null) {
                throw new RuntimeException("idUsuario es obligatorio.");
            }
            Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado con id: " + idUsuario);
            }

            BigDecimal dineroInicial = request.getDineroInicial();
            boolean heredarSaldoAnterior = Boolean.TRUE.equals(request.getHeredarSaldoAnterior());
            String justificacionManual = request.getJustificacionManual();

            if (heredarSaldoAnterior) {
                if (dineroInicial != null || (justificacionManual != null && !justificacionManual.trim().isEmpty())) {
                    throw new RuntimeException("No se puede heredar saldo y proporcionar dinero inicial o justificación manual en el cuerpo.");
                }
            } else {
                if (dineroInicial == null) {
                    throw new RuntimeException("Debe heredar saldo (heredarSaldoAnterior=true) o proporcionar un dinero inicial (dineroInicial) en el cuerpo.");
                }
            }

            Caja caja = cajaService.abrirCaja(usuario, dineroInicial, heredarSaldoAnterior, justificacionManual);
            return new ResponseEntity<>(mapCajaToCajaResponseDTO(caja), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/cerrar/{idCaja}")
    public ResponseEntity<CajaResponseDTO> cerrarCaja(@PathVariable Integer idCaja, @RequestBody CerrarCajaRequestDTO request) {
        try {
            BigDecimal dineroReal = request.getDineroReal();
            if (dineroReal == null) {
                throw new RuntimeException("dineroReal es obligatorio en el cuerpo de la solicitud.");
            }

            Caja caja = cajaService.cerrarCaja(idCaja, dineroReal);
            return new ResponseEntity<>(mapCajaToCajaResponseDTO(caja), HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/abierta/{idUsuario}")
    public ResponseEntity<CajaResponseDTO> getCajaAbiertaByUsuario(@PathVariable Integer idUsuario) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado con id: " + idUsuario);
            }

            Caja caja = cajaService.getCajaAbiertaByUsuario(usuario);
            if (caja != null) {
                return new ResponseEntity<>(mapCajaToCajaResponseDTO(caja), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public ResponseEntity<List<CajaResponseDTO>> getAllCajas(){
        try {
            List<Caja> cajas = cajaService.getAllCajas();
            List<CajaResponseDTO> cajaDTOs = cajas.stream()
                                                .map(this::mapCajaToCajaResponseDTO)
                                                .collect(Collectors.toList());
            return new ResponseEntity<>(cajaDTOs, HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{idCaja}")
    public ResponseEntity<CajaResponseDTO> getCajaById(@PathVariable Integer idCaja) {
        try {
            Caja caja = cajaService.getCajaById(idCaja);
            return new ResponseEntity<>(mapCajaToCajaResponseDTO(caja), HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

} 