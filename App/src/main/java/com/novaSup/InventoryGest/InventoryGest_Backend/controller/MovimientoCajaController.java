package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Caja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.MovimientoCaja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.CajaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.MovimientoCajaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.UsuarioService;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.CajaResponseDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.MovimientoCajaResponseDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.UsuarioSimplifiedDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.RegistrarMovimientoManualRequestDTO;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/movimientos-caja")
public class MovimientoCajaController {

    private final MovimientoCajaService movimientoCajaService;
    private final CajaService cajaService;
    private final UsuarioService usuarioService;

    public MovimientoCajaController(MovimientoCajaService movimientoCajaService, CajaService cajaService, UsuarioService usuarioService) {
        this.movimientoCajaService = movimientoCajaService;
        this.cajaService = cajaService;
        this.usuarioService = usuarioService;
    }

    // Helper method to map MovimientoCaja to MovimientoCajaResponseDTO
    private MovimientoCajaResponseDTO mapMovimientoCajaToMovimientoCajaResponseDTO(MovimientoCaja movimiento) {
        if (movimiento == null) {
            return null;
        }
        MovimientoCajaResponseDTO dto = new MovimientoCajaResponseDTO();
        dto.setIdMovimiento(movimiento.getIdMovimiento());

        // Map Caja, using the existing CajaResponseDTO mapping logic
        if (movimiento.getCaja() != null) {
             Caja caja = movimiento.getCaja();
             CajaResponseDTO cajaDto = new CajaResponseDTO();
             cajaDto.setIdCaja(caja.getIdCaja());
             if(caja.getUsuario() != null) {
                 cajaDto.setUsuario(new UsuarioSimplifiedDTO(caja.getUsuario().getIdUsuario(), caja.getUsuario().getNombre()));
             }
             if (caja.getFechaApertura() != null) {
                 // Assuming getFechaApertura returns Timestamp, convert to LocalDateTime
                 if (caja.getFechaApertura() instanceof java.sql.Timestamp) {
                     cajaDto.setFechaApertura(((java.sql.Timestamp) caja.getFechaApertura()).toLocalDateTime());
                 } else { /* handle other date types if necessary */ }
             }
             if (caja.getFechaCierre() != null) {
                 // Assuming getFechaCierre returns Timestamp, convert to LocalDateTime
                 if (caja.getFechaCierre() instanceof java.sql.Timestamp) {
                      cajaDto.setFechaCierre(((java.sql.Timestamp) caja.getFechaCierre()).toLocalDateTime());
                 } else { /* handle other date types if necessary */ }
             }
             cajaDto.setDineroInicial(caja.getDineroInicial());
             cajaDto.setDineroTotal(caja.getDineroTotal());
             cajaDto.setEstado(caja.getEstado());
             dto.setCaja(cajaDto);
        }

        dto.setTipoMovimiento(movimiento.getTipoMovimiento());
        dto.setDescripcion(movimiento.getDescripcion());
        dto.setMonto(movimiento.getMonto());

        // Convert Timestamp to LocalDateTime for the movement's date
        if (movimiento.getFecha() != null) {
             if (movimiento.getFecha() instanceof java.sql.Timestamp) {
                 dto.setFecha(((java.sql.Timestamp) movimiento.getFecha()).toLocalDateTime());
             } else { /* handle other date types if necessary */ }
        }

        // Map Usuario who performed the movement
        if (movimiento.getUsuario() != null) {
            dto.setUsuario(new UsuarioSimplifiedDTO(movimiento.getUsuario().getIdUsuario(), movimiento.getUsuario().getNombre()));
        }

        // Assuming venta is not needed in the response DTO for now based on user's example

        return dto;
    }

    @GetMapping("/caja/{idCaja}")
    public ResponseEntity<List<MovimientoCajaResponseDTO>> getMovimientosByCaja(@PathVariable Integer idCaja) {
        try {
            Caja caja = cajaService.getCajaById(idCaja);

            List<MovimientoCaja> movimientos = movimientoCajaService.getMovimientosByCaja(caja);
            List<MovimientoCajaResponseDTO> movimientoDTOs = movimientos.stream()
                                                                  .map(this::mapMovimientoCajaToMovimientoCajaResponseDTO)
                                                                  .collect(Collectors.toList());

            return new ResponseEntity<>(movimientoDTOs, HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping
    public ResponseEntity<MovimientoCajaResponseDTO> registrarMovimientoManual(@RequestBody RegistrarMovimientoManualRequestDTO requestDTO) {
        try {
            // Get data from the request DTO
            Integer idCaja = requestDTO.getIdCaja();
            String tipoMovimiento = requestDTO.getTipoMovimiento();
            String descripcion = requestDTO.getDescripcion();
            BigDecimal monto = requestDTO.getMonto();
            Integer idUsuario = requestDTO.getIdUsuario();

            Caja caja = cajaService.getCajaById(idCaja);

            Usuario usuario = usuarioService.obtenerUsuarioPorId(idUsuario);
            if (usuario == null) {
                throw new RuntimeException("Usuario no encontrado con id: " + idUsuario);
            }

            // Call service method with data from DTO
            MovimientoCaja nuevoMovimiento = movimientoCajaService.registrarMovimiento(caja, tipoMovimiento, descripcion, monto, usuario);
            return new ResponseEntity<>(mapMovimientoCajaToMovimientoCajaResponseDTO(nuevoMovimiento), HttpStatus.CREATED);
        } catch (RuntimeException e) {
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    // Note: No create endpoint here as movements are created by other services (like VentaService)
    // The POST / endpoint above is for manual adjustments/non-venta movements.
} 