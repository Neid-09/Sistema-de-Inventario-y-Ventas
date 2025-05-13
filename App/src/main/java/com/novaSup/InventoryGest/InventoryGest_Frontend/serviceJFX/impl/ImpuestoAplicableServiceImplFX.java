package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CategoriaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ImpuestoAplicableFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.TasaImpuestoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.TipoImpuestoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.CategoriaDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.ImpuestoAplicableDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.ProductoDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.TasaImpuestoDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.TipoImpuestoDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IImpuestoAplicableServiceFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;

import java.util.stream.Collectors;

public class ImpuestoAplicableServiceImplFX implements IImpuestoAplicableServiceFX {

    private final String baseUrl;
    private final ObjectMapper objectMapper;

    public ImpuestoAplicableServiceImplFX() {
        this.baseUrl = ApiConfig.getBaseUrl() + "/api/impuestos-aplicables";
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    private ImpuestoAplicableFX convertirDTOaFX(ImpuestoAplicableDTO dto) {
        if (dto == null) return null;

        TasaImpuestoFX tasaFX = convertirTasaImpuestoDTOaFX(dto.getTasaImpuesto());
        ProductoFX productoFX = convertirProductoDTOaFX(dto.getProducto());
        CategoriaFX categoriaFX = convertirCategoriaDTOaFX(dto.getCategoria());

        return new ImpuestoAplicableFX(
            dto.getIdImpuestoAplicable(),
            tasaFX,
            tasaFX != null ? tasaFX.getIdTasa() : null,
            productoFX,
            productoFX != null ? productoFX.getIdProducto() : null,
            categoriaFX,
            categoriaFX != null ? categoriaFX.getIdCategoria() : null,
            dto.getAplica(),
            dto.getFechaInicio(),
            dto.getFechaFin()
        );
    }

    private ImpuestoAplicableDTO convertirFXaDTO(ImpuestoAplicableFX fx) {
        if (fx == null) return null;

        TasaImpuestoDTO tasaDTO = convertirTasaImpuestoFXaDTO(fx.getTasaImpuesto());
        ProductoDTO productoDTO = convertirProductoFXaDTO(fx.getProducto());
        CategoriaDTO categoriaDTO = convertirCategoriaFXaDTO(fx.getCategoria());

        if (fx.getProducto() == null || fx.getProducto().getIdProducto() == null || fx.getProducto().getIdProducto() == 0) {
             productoDTO = null;
        }
         if (fx.getCategoria() == null || fx.getCategoria().getIdCategoria() == null || fx.getCategoria().getIdCategoria() == 0) {
             categoriaDTO = null;
        }

        ImpuestoAplicableDTO dto = new ImpuestoAplicableDTO(
            fx.getIdImpuestoAplicable() == 0 ? null : fx.getIdImpuestoAplicable(),
            tasaDTO,
            productoDTO,
            categoriaDTO,
            fx.isAplica(),
            fx.getFechaInicio(),
            fx.getFechaFin()
        );
        return dto;
    }

    private TasaImpuestoFX convertirTasaImpuestoDTOaFX(TasaImpuestoDTO dto) {
        if (dto == null) return null;
        TipoImpuestoFX tipoFX = convertirTipoImpuestoDTOaFX(dto.getTipoImpuesto());
        return new TasaImpuestoFX(
            dto.getIdTasa(),
            tipoFX,
            tipoFX != null ? tipoFX.getIdTipoImpuesto() : null,
            dto.getTasa(),
            dto.getFechaInicio(),
            dto.getFechaFin(),
            dto.getDescripcion()
        );
    }

     private TasaImpuestoDTO convertirTasaImpuestoFXaDTO(TasaImpuestoFX fx) {
        if (fx == null) return null;
        TipoImpuestoDTO tipoDTO = convertirTipoImpuestoFXaDTO(fx.getTipoImpuesto());

        if (fx.getTipoImpuesto() != null && tipoDTO != null) {
            tipoDTO = new TipoImpuestoDTO();
             tipoDTO.setIdTipoImpuesto(fx.getTipoImpuestoId());
         }

        return new TasaImpuestoDTO(
            fx.getIdTasa() == 0 ? null : fx.getIdTasa(),
            tipoDTO,
            fx.getTasa(),
            fx.getFechaInicio(),
            fx.getFechaFin(),
            fx.getDescripcion()
        );
    }

    private TipoImpuestoFX convertirTipoImpuestoDTOaFX(TipoImpuestoDTO dto) {
        if (dto == null) return null;
        return new TipoImpuestoFX(
            dto.getIdTipoImpuesto(),
            dto.getNombre(),
            dto.getDescripcion(),
            dto.getEsPorcentual(),
            dto.getActivo()
        );
    }

     private TipoImpuestoDTO convertirTipoImpuestoFXaDTO(TipoImpuestoFX fx) {
        if (fx == null) return null;
        return new TipoImpuestoDTO(
            fx.getIdTipoImpuesto(),
            fx.getNombre(),
            fx.getDescripcion(),
            fx.isEsPorcentual(),
            fx.isActivo()
        );
    }


    private ProductoFX convertirProductoDTOaFX(ProductoDTO dto) {
        if (dto == null) return null;
        return new ProductoFX(
            dto.getIdProducto(),
            dto.getCodigo(),
            dto.getNombre(),
            dto.getDescripcion(),
            dto.getPrecioCosto(),
            dto.getPrecioVenta(),
            dto.getStock(),
            dto.getStockMinimo(),
            dto.getStockMaximo(),
            dto.getCategoria() != null ? dto.getCategoria().getNombre() : null,
            dto.getIdCategoria(),
            dto.getProveedor() != null ? dto.getProveedor().getNombre() : null,
            dto.getIdProveedor(),
            dto.getEstado()
        );
    }

     private ProductoDTO convertirProductoFXaDTO(ProductoFX fx) {
        if (fx == null) return null;
         ProductoDTO dto = new ProductoDTO();
         dto.setIdProducto(fx.getIdProducto() == 0 ? null : fx.getIdProducto());
         dto.setCodigo(fx.getCodigo());
         dto.setNombre(fx.getNombre());
         dto.setDescripcion(fx.getDescripcion());
         dto.setPrecioCosto(fx.getPrecioCosto());
         dto.setPrecioVenta(fx.getPrecioVenta());
         dto.setStock(fx.getStock());
         dto.setStockMinimo(fx.getStockMinimo());
         dto.setStockMaximo(fx.getStockMaximo());
         dto.setIdCategoria(fx.getIdCategoria());
         dto.setIdProveedor(fx.getIdProveedor());
         dto.setEstado(fx.getEstado());
         return dto;
    }

    private CategoriaFX convertirCategoriaDTOaFX(CategoriaDTO dto) {
        if (dto == null) return null;
        return new CategoriaFX(
            dto.getIdCategoria(),
            dto.getNombre(),
            dto.getDescripcion(),
            dto.getEstado(),
            dto.getDuracionGarantia()
        );
    }

     private CategoriaDTO convertirCategoriaFXaDTO(CategoriaFX fx) {
        if (fx == null) return null;
         CategoriaDTO dto = new CategoriaDTO();
         dto.setIdCategoria(fx.getIdCategoria() == 0 ? null : fx.getIdCategoria());
         dto.setNombre(fx.getNombre());
         dto.setDescripcion(fx.getDescripcion());
         dto.setEstado(fx.getEstado());
         dto.setDuracionGarantia(fx.getDuracionGarantia());
        return dto;
    }

    @Override
    public ObservableList<ImpuestoAplicableFX> listarImpuestosAplicables() throws Exception {
        String jsonResponse = HttpClient.get(baseUrl);
        List<ImpuestoAplicableDTO> listaDTO = objectMapper.readValue(jsonResponse, new TypeReference<List<ImpuestoAplicableDTO>>() {});
        List<ImpuestoAplicableFX> listaFX = listaDTO.stream()
                                                  .map(this::convertirDTOaFX)
                                                  .collect(Collectors.toList());
        return FXCollections.observableArrayList(listaFX);
    }

    @Override
    public ImpuestoAplicableFX obtenerImpuestoAplicablePorId(int id) throws Exception {
        String url = baseUrl + "/" + id;
        String jsonResponse = HttpClient.get(url);
        ImpuestoAplicableDTO dto = objectMapper.readValue(jsonResponse, ImpuestoAplicableDTO.class);
        return convertirDTOaFX(dto);
    }

    @Override
    public ObservableList<ImpuestoAplicableFX> obtenerImpuestosPorProductoYFecha(int idProducto, String fecha) throws Exception {
        String url = baseUrl + "/por-producto/" + idProducto + "?fecha=" + fecha;
        String jsonResponse = HttpClient.get(url);
        List<ImpuestoAplicableDTO> listaDTO = objectMapper.readValue(jsonResponse, new TypeReference<List<ImpuestoAplicableDTO>>() {});
        List<ImpuestoAplicableFX> listaFX = listaDTO.stream()
                                                  .map(this::convertirDTOaFX)
                                                  .collect(Collectors.toList());
        return FXCollections.observableArrayList(listaFX);
    }

    @Override
    public ObservableList<ImpuestoAplicableFX> obtenerImpuestosPorCategoriaYFecha(int idCategoria, String fecha) throws Exception {
        String url = baseUrl + "/por-categoria/" + idCategoria + "?fecha=" + fecha;
        String jsonResponse = HttpClient.get(url);
        List<ImpuestoAplicableDTO> listaDTO = objectMapper.readValue(jsonResponse, new TypeReference<List<ImpuestoAplicableDTO>>() {});
        List<ImpuestoAplicableFX> listaFX = listaDTO.stream()
                                                  .map(this::convertirDTOaFX)
                                                  .collect(Collectors.toList());
        return FXCollections.observableArrayList(listaFX);
    }

    @Override
    public ImpuestoAplicableFX crearImpuestoAplicable(ImpuestoAplicableFX impuestoAplicable) throws Exception {
        ImpuestoAplicableDTO dtoRequest = convertirFXaDTO(impuestoAplicable);
        String jsonRequest = objectMapper.writeValueAsString(dtoRequest);
        String jsonResponse = HttpClient.post(baseUrl, jsonRequest);
        ImpuestoAplicableDTO dtoResponse = objectMapper.readValue(jsonResponse, ImpuestoAplicableDTO.class);
        return convertirDTOaFX(dtoResponse);
    }

    @Override
    public ImpuestoAplicableFX actualizarImpuestoAplicable(int id, ImpuestoAplicableFX impuestoAplicable) throws Exception {
        String url = baseUrl + "/" + id;
        ImpuestoAplicableDTO dtoRequest = convertirFXaDTO(impuestoAplicable);
        dtoRequest.setIdImpuestoAplicable(id);
        String jsonRequest = objectMapper.writeValueAsString(dtoRequest);
        String jsonResponse = HttpClient.put(url, jsonRequest);
        ImpuestoAplicableDTO dtoResponse = objectMapper.readValue(jsonResponse, ImpuestoAplicableDTO.class);
        return convertirDTOaFX(dtoResponse);
    }

    @Override
    public ImpuestoAplicableFX cambiarAplicabilidadImpuesto(int id) throws Exception {
        String url = baseUrl + "/" + id + "/cambiar-aplicabilidad";
        String jsonResponse = HttpClient.put(url, "{}");
        ImpuestoAplicableDTO dtoResponse = objectMapper.readValue(jsonResponse, ImpuestoAplicableDTO.class);
        return convertirDTOaFX(dtoResponse);
    }
} 