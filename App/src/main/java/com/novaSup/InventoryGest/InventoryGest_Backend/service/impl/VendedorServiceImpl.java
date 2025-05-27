package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario; // Asegúrate que la ruta sea correcta
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Vendedor;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.UsuarioRepository; // Asegúrate que la ruta sea correcta
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.VendedorRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.VendedorService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para la gestión de Vendedores.
 * La lógica de estado (activo/inactivo) y datos como nombre se delegan al Usuario asociado.
 */
@Service
@RequiredArgsConstructor // Inyección de dependencias vía constructor
public class VendedorServiceImpl implements VendedorService {

    private final VendedorRepository vendedorRepository;
    private final UsuarioRepository usuarioRepository; // Necesario para validar/actualizar estado usuario    @Override
    @Transactional(readOnly = true)
    public List<Vendedor> obtenerTodos() {
        return vendedorRepository.findAllWithUsuario();
    }    @Override
    @Transactional(readOnly = true)
    public Optional<Vendedor> obtenerPorId(Integer id) {
        return vendedorRepository.findByIdWithUsuario(id);
    }

    @Override
    @Transactional
    public Vendedor guardar(Vendedor vendedor) {
        // Validar que el Usuario asociado exista y esté asignado
        if (vendedor.getUsuario() == null || vendedor.getUsuario().getIdUsuario() == null) {
             // Si se está creando un Vendedor, se asume que el Usuario ya existe y se pasa su ID.
             // Si el objeto Usuario completo no viene en la petición, cargarlo.
             if (vendedor.getIdUsuario() != null) {
                 Usuario usuario = usuarioRepository.findById(vendedor.getIdUsuario())
                     .orElseThrow(() -> new EntityNotFoundException("Usuario no encontrado con id: " + vendedor.getIdUsuario()));
                 vendedor.setUsuario(usuario);
             } else {
                 throw new IllegalArgumentException("El Vendedor debe estar asociado a un Usuario existente.");
             }
        }

        // El estado activo/inactivo se maneja en el Usuario. No se establece aquí por defecto.
        // Si necesitas asegurar que el Usuario esté activo al crear el Vendedor, valida:
        // if (!vendedor.getUsuario().isActivo()) { throw new ... }

        return vendedorRepository.save(vendedor);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Vendedor vendedor = vendedorRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Vendedor no encontrado con id: " + id));

        Usuario usuarioAsociado = vendedor.getUsuario();
        if (usuarioAsociado == null) {
             // Esto no debería pasar si la BD está bien, pero por si acaso
             throw new IllegalStateException("El vendedor con id " + id + " no tiene un usuario asociado.");
        }

        // Lógica de negocio: En lugar de borrar, desactivar el *Usuario* asociado
        // (Asumiendo que desactivar el usuario es la acción deseada)
        // boolean tieneVentas = ventaRepository.existsByVendedorId(id); // Implementar verificación real
        boolean tieneVentas = false; // Placeholder

        if (tieneVentas) {
            if (usuarioAsociado.getEstado()) { // Solo desactivar si está activo
                 usuarioAsociado.setEstado(false); // Cambia el estado a inactivo
                 usuarioRepository.save(usuarioAsociado); // Guardar el cambio en Usuario
                 System.out.println("Usuario " + usuarioAsociado.getIdUsuario() + " (asociado al vendedor " + id + ") desactivado porque el vendedor tiene ventas.");
            }
        } else {
            // Si no tiene ventas, ¿qué hacer?
            // Opción A: Borrar solo el Vendedor (deja el Usuario intacto)
            // vendedorRepository.deleteById(id);

            // Opción B: Desactivar el Usuario igualmente
             if (usuarioAsociado.getEstado()) {
                 usuarioAsociado.setEstado(false);
                 usuarioRepository.save(usuarioAsociado);
             }

            // Opción C: Borrar Vendedor y potencialmente Usuario (¡Cuidado!)
            // vendedorRepository.deleteById(id);
            // Considerar si el Usuario debe borrarse también (podría ser usado por otros roles)
            // usuarioRepository.deleteById(usuarioAsociado.getIdUsuario());

            // Por simplicidad, vamos a desactivar el usuario como en el caso de tener ventas:
             if (usuarioAsociado.getEstado()) {
                 usuarioAsociado.setEstado(false);
                 usuarioRepository.save(usuarioAsociado);
                 System.out.println("Usuario " + usuarioAsociado.getIdUsuario() + " (asociado al vendedor " + id + ") desactivado.");
             }
             // Opcionalmente, podrías borrar el vendedor después de desactivar el usuario si ya no lo necesitas
             // vendedorRepository.deleteById(id);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Vendedor> buscarPorNombre(String nombre) {
        // Usa el método del repositorio que busca por el nombre del usuario asociado
        return vendedorRepository.findByUsuarioNombreContainingIgnoreCase(nombre);
    }    @Override
    @Transactional(readOnly = true)
    public List<Vendedor> obtenerActivos() {
        // Usa el método del repositorio que busca vendedores cuyo usuario asociado está activo
        return vendedorRepository.findActivosWithUsuario();
    }

    @Override
    @Transactional
    public Optional<Vendedor> desactivarVendedor(Integer id) {
        return vendedorRepository.findById(id).map(vendedor -> {
            Usuario usuario = vendedor.getUsuario();
            if (usuario != null && usuario.getEstado()) {
                usuario.setEstado(false); // Cambia el estado a inactivo
                usuarioRepository.save(usuario); // Guardar el cambio en Usuario
            }
            return vendedor; // Devuelve el vendedor (aunque el cambio fue en Usuario)
        });
    }

    @Override
    @Transactional
    public Optional<Vendedor> activarVendedor(Integer id) {
        return vendedorRepository.findById(id).map(vendedor -> {
            // Asumiendo que el estado se maneja en la entidad Usuario asociada
            if (vendedor.getUsuario() != null) {
                Usuario usuario = vendedor.getUsuario();
                usuario.setEstado(true); // Activa el usuario
                usuarioRepository.save(usuario); // Guarda el cambio en el usuario
                // No es necesario guardar el vendedor si no cambia nada en él
                return vendedor;
            } else {
                // Manejar caso donde el vendedor no tiene usuario asociado (podría ser un error de datos)
                // Lanzar excepción o loggear advertencia
                 throw new EntityNotFoundException("No se puede activar el vendedor ID " + id + " porque no tiene un usuario asociado.");
            }
        });
    }

    @Override
    @Transactional(readOnly = true) // Es una consulta, readOnly = true
    public Optional<Vendedor> obtenerVendedorConUsuario(Integer id) {
        // Llamar al nuevo método del repositorio que usa JOIN FETCH
        return vendedorRepository.findByIdWithUsuario(id);
    }
}
