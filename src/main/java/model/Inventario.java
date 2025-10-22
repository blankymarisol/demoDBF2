// Clase modelo Inventario
package model;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class Inventario {
    private int idInventario;
    private int cantidadInventario;
    private BigDecimal precioVentaInventario;
    private Timestamp fechaActualizacionInventario;
    private int idBodega;
    private int idProducto;

    public Inventario() {}

    public Inventario(int idInventario, int cantidadInventario, BigDecimal precioVentaInventario,
                      Timestamp fechaActualizacionInventario, int idBodega, int idProducto) {
        this.idInventario = idInventario;
        this.cantidadInventario = cantidadInventario;
        this.precioVentaInventario = precioVentaInventario;
        this.fechaActualizacionInventario = fechaActualizacionInventario;
        this.idBodega = idBodega;
        this.idProducto = idProducto;
    }

    // Getters y Setters
    public int getIdInventario() { return idInventario; }
    public void setIdInventario(int idInventario) { this.idInventario = idInventario; }

    public int getCantidadInventario() { return cantidadInventario; }
    public void setCantidadInventario(int cantidadInventario) { this.cantidadInventario = cantidadInventario; }

    public BigDecimal getPrecioVentaInventario() { return precioVentaInventario; }
    public void setPrecioVentaInventario(BigDecimal precioVentaInventario) {
        this.precioVentaInventario = precioVentaInventario;
    }

    public Timestamp getFechaActualizacionInventario() { return fechaActualizacionInventario; }
    public void setFechaActualizacionInventario(Timestamp fechaActualizacionInventario) {
        this.fechaActualizacionInventario = fechaActualizacionInventario;
    }

    public int getIdBodega() { return idBodega; }
    public void setIdBodega(int idBodega) { this.idBodega = idBodega; }

    public int getIdProducto() { return idProducto; }
    public void setIdProducto(int idProducto) { this.idProducto = idProducto; }

    @Override
    public String toString() {
        return "Inventario{" +
                "idInventario=" + idInventario +
                ", cantidadInventario=" + cantidadInventario +
                ", precioVentaInventario=" + precioVentaInventario +
                ", idBodega=" + idBodega +
                ", idProducto=" + idProducto +
                '}';
    }
}