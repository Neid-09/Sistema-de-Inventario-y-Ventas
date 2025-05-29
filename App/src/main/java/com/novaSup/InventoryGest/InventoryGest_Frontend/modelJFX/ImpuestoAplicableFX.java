package com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

// Si usas LocalDate para las fechas, necesitarás este import
// import java.time.LocalDate;

public class ImpuestoAplicableFX {

    private final IntegerProperty idImpuestoAplicable;
    private final ObjectProperty<TasaImpuestoFX> tasaImpuesto; // Referencia al objeto TasaImpuestoFX
    private final IntegerProperty idTasa; // Puede ser útil si a veces solo tienes el ID
    private final ObjectProperty<ProductoFX> producto; // Referencia a ProductoFX, puede ser null
    private final IntegerProperty idProducto; // Puede ser útil, puede ser null
    private final ObjectProperty<CategoriaFX> categoria; // Referencia a CategoriaFX, puede ser null
    private final IntegerProperty idCategoria; // Puede ser útil, puede ser null
    private final BooleanProperty aplica;
    private final StringProperty fechaInicio; // Considerar ObjectProperty<LocalDate>
    private final StringProperty fechaFin;    // Considerar ObjectProperty<LocalDate>, puede ser null

    public ImpuestoAplicableFX(Integer idImpuestoAplicable, TasaImpuestoFX tasaImpuesto, Integer idTasa,
                               ProductoFX producto, Integer idProducto, CategoriaFX categoria, Integer idCategoria,
                               Boolean aplica, String fechaInicio, String fechaFin) {
        this.idImpuestoAplicable = new SimpleIntegerProperty(idImpuestoAplicable == null ? 0 : idImpuestoAplicable);
        this.tasaImpuesto = new SimpleObjectProperty<>(tasaImpuesto);
        this.idTasa = new SimpleIntegerProperty(idTasa == null ? (tasaImpuesto != null ? tasaImpuesto.getIdTasa() : 0) : idTasa);
        this.producto = new SimpleObjectProperty<>(producto);
        this.idProducto = new SimpleIntegerProperty(idProducto == null ? (producto != null ? producto.getIdProducto() : 0) : idProducto);
        this.categoria = new SimpleObjectProperty<>(categoria);
        this.idCategoria = new SimpleIntegerProperty(idCategoria == null ? (categoria != null ? categoria.getIdCategoria() : 0) : idCategoria);
        this.aplica = new SimpleBooleanProperty(aplica != null && aplica);
        this.fechaInicio = new SimpleStringProperty(fechaInicio);
        this.fechaFin = new SimpleStringProperty(fechaFin);
    }

    public ImpuestoAplicableFX() {
        this.idImpuestoAplicable = new SimpleIntegerProperty();
        this.tasaImpuesto = new SimpleObjectProperty<>();
        this.idTasa = new SimpleIntegerProperty();
        this.producto = new SimpleObjectProperty<>();
        this.idProducto = new SimpleIntegerProperty();
        this.categoria = new SimpleObjectProperty<>();
        this.idCategoria = new SimpleIntegerProperty();
        this.aplica = new SimpleBooleanProperty();
        this.fechaInicio = new SimpleStringProperty();
        this.fechaFin = new SimpleStringProperty();
    }

    // idImpuestoAplicable
    public int getIdImpuestoAplicable() {
        return idImpuestoAplicable.get();
    }
    public void setIdImpuestoAplicable(int idImpuestoAplicable) {
        this.idImpuestoAplicable.set(idImpuestoAplicable);
    }
    public IntegerProperty idImpuestoAplicableProperty() {
        return idImpuestoAplicable;
    }

    // tasaImpuesto
    public TasaImpuestoFX getTasaImpuesto() {
        return tasaImpuesto.get();
    }
    public void setTasaImpuesto(TasaImpuestoFX tasaImpuesto) {
        this.tasaImpuesto.set(tasaImpuesto);
        if (tasaImpuesto != null) {
            this.setIdTasa(tasaImpuesto.getIdTasa());
        }
    }
    public ObjectProperty<TasaImpuestoFX> tasaImpuestoProperty() {
        return tasaImpuesto;
    }

    // idTasa
    public int getIdTasa() {
        return idTasa.get();
    }
    public void setIdTasa(int idTasa) {
        this.idTasa.set(idTasa);
    }
    public IntegerProperty idTasaProperty() {
        return idTasa;
    }

    // producto
    public ProductoFX getProducto() {
        return producto.get();
    }
    public void setProducto(ProductoFX producto) {
        this.producto.set(producto);
        if (producto != null) {
            this.setIdProducto(producto.getIdProducto());
        }
    }
    public ObjectProperty<ProductoFX> productoProperty() {
        return producto;
    }

    // idProducto
    public int getIdProducto() {
        return idProducto.get();
    }
    public void setIdProducto(int idProducto) {
        this.idProducto.set(idProducto);
    }
    public IntegerProperty idProductoProperty() {
        return idProducto;
    }

    // categoria
    public CategoriaFX getCategoria() {
        return categoria.get();
    }
    public void setCategoria(CategoriaFX categoria) {
        this.categoria.set(categoria);
        if (categoria != null) {
            this.setIdCategoria(categoria.getIdCategoria());
        }
    }
    public ObjectProperty<CategoriaFX> categoriaProperty() {
        return categoria;
    }

    // idCategoria
    public int getIdCategoria() {
        return idCategoria.get();
    }
    public void setIdCategoria(int idCategoria) {
        this.idCategoria.set(idCategoria);
    }
    public IntegerProperty idCategoriaProperty() {
        return idCategoria;
    }

    // aplica
    public boolean isAplica() {
        return aplica.get();
    }
    public void setAplica(boolean aplica) {
        this.aplica.set(aplica);
    }
    public BooleanProperty aplicaProperty() {
        return aplica;
    }

    // fechaInicio
    public String getFechaInicio() {
        return fechaInicio.get();
    }
    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio.set(fechaInicio);
    }
    public StringProperty fechaInicioProperty() {
        return fechaInicio;
    }

    // fechaFin
    public String getFechaFin() {
        return fechaFin.get();
    }
    public void setFechaFin(String fechaFin) {
        this.fechaFin.set(fechaFin);
    }
    public StringProperty fechaFinProperty() {
        return fechaFin;
    }

    @Override
    public String toString() {
        String aplicableA = "N/A";
        if (getProducto() != null) {
            aplicableA = "Producto: " + getProducto().getNombre();
        } else if (getCategoria() != null) {
            aplicableA = "Categoría: " + getCategoria().getNombre();
        }
        return (getTasaImpuesto() != null ? getTasaImpuesto().toString() : "Tasa N/A") + " -> " + aplicableA;
    }
} 