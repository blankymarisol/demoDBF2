package model;

import java.math.BigDecimal;

public class Producto {
    private int idProducto;
    private String skuProducto;
    private String nombreProducto;
    private String descripcionProducto;
    private BigDecimal costoUnitarioProducto;
    private BigDecimal descuentoProducto;
    private String imagenURLProducto;
    private String estadoProducto;
    private int stockProducto;
    private int idCategoria;
    private int idTipoProducto;
    private int idUnidadMedida;

    // Constructor vacío
    public Producto() {}

    // Constructor completo
    public Producto(int idProducto, String skuProducto, String nombreProducto,
                    String descripcionProducto, BigDecimal costoUnitarioProducto,
                    BigDecimal descuentoProducto, String imagenURLProducto,
                    String estadoProducto, int stockProducto, int idCategoria,
                    int idTipoProducto, int idUnidadMedida) {
        this.idProducto = idProducto;
        this.skuProducto = skuProducto;
        this.nombreProducto = nombreProducto;
        this.descripcionProducto = descripcionProducto;
        this.costoUnitarioProducto = costoUnitarioProducto;
        this.descuentoProducto = descuentoProducto;
        this.imagenURLProducto = imagenURLProducto;
        this.estadoProducto = estadoProducto;
        this.stockProducto = stockProducto;
        this.idCategoria = idCategoria;
        this.idTipoProducto = idTipoProducto;
        this.idUnidadMedida = idUnidadMedida;
    }

    // Getters y Setters
    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    public String getSkuProducto() { return skuProducto; }
    public void setSkuProducto(String skuProducto) { this.skuProducto = skuProducto; }

    public String getNombreProducto() { return nombreProducto; }
    public void setNombreProducto(String nombreProducto) { this.nombreProducto = nombreProducto; }

    public String getDescripcionProducto() { return descripcionProducto; }
    public void setDescripcionProducto(String descripcionProducto) { this.descripcionProducto = descripcionProducto; }

    public BigDecimal getCostoUnitarioProducto() { return costoUnitarioProducto; }
    public void setCostoUnitarioProducto(BigDecimal costoUnitarioProducto) { this.costoUnitarioProducto = costoUnitarioProducto; }

    public BigDecimal getDescuentoProducto() { return descuentoProducto; }
    public void setDescuentoProducto(BigDecimal descuentoProducto) { this.descuentoProducto = descuentoProducto; }

    public String getImagenURLProducto() { return imagenURLProducto; }
    public void setImagenURLProducto(String imagenURLProducto) { this.imagenURLProducto = imagenURLProducto; }

    public String getEstadoProducto() { return estadoProducto; }
    public void setEstadoProducto(String estadoProducto) { this.estadoProducto = estadoProducto; }

    public int getStockProducto() { return stockProducto; }
    public void setStockProducto(int stockProducto) { this.stockProducto = stockProducto; }

    public int getIdCategoria() { return idCategoria; }
    public void setIdCategoria(int idCategoria) { this.idCategoria = idCategoria; }

    public int getIdTipoProducto() { return idTipoProducto; }
    public void setIdTipoProducto(int idTipoProducto) { this.idTipoProducto = idTipoProducto; }

    public int getIdUnidadMedida() { return idUnidadMedida; }
    public void setIdUnidadMedida(int idUnidadMedida) { this.idUnidadMedida = idUnidadMedida; }

    @Override
    public String toString() {
        return "Producto{" +
                "idProducto=" + idProducto +
                ", skuProducto='" + skuProducto + '\'' +
                ", nombreProducto='" + nombreProducto + '\'' +
                ", costoUnitarioProducto=" + costoUnitarioProducto +
                ", stockProducto=" + stockProducto +
                ", estadoProducto='" + estadoProducto + '\'' +
                '}';
    }
}