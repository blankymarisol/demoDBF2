package model;

import java.math.BigDecimal;

public class CompraDetalle {
    private int idDetalle;
    private int cantidadDetalle;
    private BigDecimal precioUnitarioDetalle;
    private int idCompra;
    private int idProducto;

    // Constructor vacío
    public CompraDetalle() {}

    // Constructor con parámetros
    public CompraDetalle(int idDetalle, int cantidadDetalle, BigDecimal precioUnitarioDetalle,
                         int idCompra, int idProducto) {
        this.idDetalle = idDetalle;
        this.cantidadDetalle = cantidadDetalle;
        this.precioUnitarioDetalle = precioUnitarioDetalle;
        this.idCompra = idCompra;
        this.idProducto = idProducto;
    }

    // Getters y Setters
    public int getIdDetalle() {
        return idDetalle;
    }

    public void setIdDetalle(int idDetalle) {
        this.idDetalle = idDetalle;
    }

    public int getCantidadDetalle() {
        return cantidadDetalle;
    }

    public void setCantidadDetalle(int cantidadDetalle) {
        this.cantidadDetalle = cantidadDetalle;
    }

    public BigDecimal getPrecioUnitarioDetalle() {
        return precioUnitarioDetalle;
    }

    public void setPrecioUnitarioDetalle(BigDecimal precioUnitarioDetalle) {
        this.precioUnitarioDetalle = precioUnitarioDetalle;
    }

    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    @Override
    public String toString() {
        return "CompraDetalle{" +
                "idDetalle=" + idDetalle +
                ", cantidadDetalle=" + cantidadDetalle +
                ", precioUnitarioDetalle=" + precioUnitarioDetalle +
                ", idCompra=" + idCompra +
                ", idProducto=" + idProducto +
                '}';
    }
}