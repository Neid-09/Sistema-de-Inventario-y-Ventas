-- Ya integrado 
select * from comisiones;

select * from facturas;
select * from detalle_impuestos_factura;
select * from impuestos_aplicables;
select * from tasas_impuestos;
select * from tipos_impuestos;
select * from promociones;

SELECT * FROM venta;

-- SET FOREIGN_KEY_CHECKS = 0;
-- SET FOREIGN_KEY_CHECKS = 1;
-- DROP TABLE IF EXISTS clientes;

CREATE TABLE clientes (
    id_cliente INT(11) NOT NULL AUTO_INCREMENT,
    cedula VARCHAR(20) NULL,
    nombre VARCHAR(100) NOT NULL,
    celular VARCHAR(15) NULL,
    correo VARCHAR(100) NULL, 
    direccion VARCHAR(255) NULL,

    -- Datos para facturación
    requiere_factura_default TINYINT(1) DEFAULT 0,
    razon_social_fiscal VARCHAR(255) NULL,
    rfc_fiscal VARCHAR(20) NULL, 
    direccion_fiscal TEXT NULL, -- Opción 1: Dirección fiscal como un solo campo de texto
    correo_fiscal VARCHAR(100) NULL,
    uso_cfdi_default VARCHAR(10) NULL, 

    -- Sistema de Lealtad y Crédito
    total_comprado DECIMAL(12,2) DEFAULT 0.00,
    puntos_fidelidad INT(11) DEFAULT 0,
    ultima_compra TIMESTAMP NULL,
    limite_credito DECIMAL(10,2) DEFAULT 0.00,

    -- Otros campos comunes
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    activo TINYINT(1) DEFAULT 1,

    PRIMARY KEY (id_cliente),
    UNIQUE KEY uq_cliente_correo (correo) -- Ejemplo: si el correo de contacto general debe ser único. Descomenta si es necesario.
);