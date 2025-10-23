package model;

import java.sql.Timestamp;

public class MovimientoInventario {
    private int idMovimiento;
    private String tipoMovimiento;
    private int cantidadMovimiento;
    private Timestamp fechaMovimiento;
    private String observacionMovimiento;
    private String referenciaMovimiento;
    private int idInventario;
    private int idUsuario;

    // Constructor vacío
    public MovimientoInventario() {}

    // Constructor con parámetros
    public MovimientoInventario(int idMovimiento, String tipoMovimiento, int cantidadMovimiento,
                                Timestamp fechaMovimiento, String observacionMovimiento,
                                String referenciaMovimiento, int idInventario, int idUsuario) {
        this.idMovimiento = idMovimiento;
        this.tipoMovimiento = tipoMovimiento;
        this.cantidadMovimiento = cantidadMovimiento;
        this.fechaMovimiento = fechaMovimiento;
        this.observacionMovimiento = observacionMovimiento;
        this.referenciaMovimiento = referenciaMovimiento;
        this.idInventario = idInventario;
        this.idUsuario = idUsuario;
    }

    // Getters y Setters
    public int getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(int idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public String getTipoMovimiento() {
        return tipoMovimiento;
    }

    public void setTipoMovimiento(String tipoMovimiento) {
        this.tipoMovimiento = tipoMovimiento;
    }

    public int getCantidadMovimiento() {
        return cantidadMovimiento;
    }

    public void setCantidadMovimiento(int cantidadMovimiento) {
        this.cantidadMovimiento = cantidadMovimiento;
    }

    public Timestamp getFechaMovimiento() {
        return fechaMovimiento;
    }

    public void setFechaMovimiento(Timestamp fechaMovimiento) {
        this.fechaMovimiento = fechaMovimiento;
    }

    public String getObservacionMovimiento() {
        return observacionMovimiento;
    }

    public void setObservacionMovimiento(String observacionMovimiento) {
        this.observacionMovimiento = observacionMovimiento;
    }

    public String getReferenciaMovimiento() {
        return referenciaMovimiento;
    }

    public void setReferenciaMovimiento(String referenciaMovimiento) {
        this.referenciaMovimiento = referenciaMovimiento;
    }

    public int getIdInventario() {
        return idInventario;
    }

    public void setIdInventario(int idInventario) {
        this.idInventario = idInventario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    @Override
    public String toString() {
        return "MovimientoInventario{" +
                "idMovimiento=" + idMovimiento +
                ", tipoMovimiento='" + tipoMovimiento + '\'' +
                ", cantidadMovimiento=" + cantidadMovimiento +
                ", fechaMovimiento=" + fechaMovimiento +
                ", observacionMovimiento='" + observacionMovimiento + '\'' +
                ", referenciaMovimiento='" + referenciaMovimiento + '\'' +
                ", idInventario=" + idInventario +
                ", idUsuario=" + idUsuario +
                '}';
    }
}