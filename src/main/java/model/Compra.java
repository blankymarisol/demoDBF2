package model;

import java.math.BigDecimal;
import java.sql.Date;

public class Compra {
    private int idCompra;
    private Date fechaCompra;
    private BigDecimal totalCompra;
    private String estadoCompra;
    private int idProveedor;

    // Constructor vacío
    public Compra() {}

    // Constructor con parámetros
    public Compra(int idCompra, Date fechaCompra, BigDecimal totalCompra,
                  String estadoCompra, int idProveedor) {
        this.idCompra = idCompra;
        this.fechaCompra = fechaCompra;
        this.totalCompra = totalCompra;
        this.estadoCompra = estadoCompra;
        this.idProveedor = idProveedor;
    }

    // Getters y Setters
    public int getIdCompra() {
        return idCompra;
    }

    public void setIdCompra(int idCompra) {
        this.idCompra = idCompra;
    }

    public Date getFechaCompra() {
        return fechaCompra;
    }

    public void setFechaCompra(Date fechaCompra) {
        this.fechaCompra = fechaCompra;
    }

    public BigDecimal getTotalCompra() {
        return totalCompra;
    }

    public void setTotalCompra(BigDecimal totalCompra) {
        this.totalCompra = totalCompra;
    }

    public String getEstadoCompra() {
        return estadoCompra;
    }

    public void setEstadoCompra(String estadoCompra) {
        this.estadoCompra = estadoCompra;
    }

    public int getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    @Override
    public String toString() {
        return "Compra{" +
                "idCompra=" + idCompra +
                ", fechaCompra=" + fechaCompra +
                ", totalCompra=" + totalCompra +
                ", estadoCompra='" + estadoCompra + '\'' +
                ", idProveedor=" + idProveedor +
                '}';
    }
}