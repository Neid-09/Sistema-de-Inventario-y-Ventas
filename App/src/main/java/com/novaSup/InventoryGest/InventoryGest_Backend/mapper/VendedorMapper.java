package com.novaSup.InventoryGest.InventoryGest_Backend.mapper;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VendedorDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.UsuarioSimplifiedDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Vendedor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VendedorMapper {

    /**
     * Convierte una entidad Vendedor a VendedorDTO
     */
    public VendedorDTO toDTO(Vendedor vendedor) {
        if (vendedor == null) {
            return null;
        }

        VendedorDTO dto = new VendedorDTO();
        dto.setIdVendedor(vendedor.getIdVendedor());
        dto.setIdUsuario(vendedor.getIdUsuario());
        dto.setObjetivoVentas(vendedor.getObjetivoVentas());
        dto.setFechaContratacion(vendedor.getFechaContratacion());

        // Mapear usuario simplificado si existe
        if (vendedor.getUsuario() != null) {
            UsuarioSimplifiedDTO usuarioDTO = new UsuarioSimplifiedDTO();
            usuarioDTO.setIdUsuario(vendedor.getUsuario().getIdUsuario());
            usuarioDTO.setNombre(vendedor.getUsuario().getNombre());
            dto.setUsuario(usuarioDTO);
        }

        return dto;
    }

    /**
     * Convierte una lista de entidades Vendedor a lista de VendedorDTO
     */
    public List<VendedorDTO> toDTOList(List<Vendedor> vendedores) {
        if (vendedores == null) {
            return null;
        }
        return vendedores.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    /**
     * Convierte un VendedorDTO a entidad Vendedor (para crear/actualizar)
     */
    public Vendedor toEntity(VendedorDTO dto) {
        if (dto == null) {
            return null;
        }

        Vendedor vendedor = new Vendedor();
        vendedor.setIdVendedor(dto.getIdVendedor());
        vendedor.setIdUsuario(dto.getIdUsuario());
        vendedor.setObjetivoVentas(dto.getObjetivoVentas());
        vendedor.setFechaContratacion(dto.getFechaContratacion());

        return vendedor;
    }
}
