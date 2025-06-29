-- Script SQL para la base de datos hotel_nova_db

-- Eliminar la base de datos si ya existe (para empezar limpio en pruebas)
DROP DATABASE IF EXISTS hotel_nova_db;

-- Crear la base de datos
CREATE DATABASE hotel_nova_db;

-- Seleccionar la base de datos
USE hotel_nova_db;

-- Tabla HUESPEDES
CREATE TABLE HUESPEDES (
    id_huesped INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    dni VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100),
    telefono VARCHAR(20)
);

-- Tabla HABITACIONES
CREATE TABLE HABITACIONES (
    id_habitacion INT PRIMARY KEY AUTO_INCREMENT,
    numero_habitacion INT UNIQUE NOT NULL,
    tipo VARCHAR(50) NOT NULL, -- Ej: 'Simple', 'Doble', 'Suite'
    precio_por_noche DECIMAL(10, 2) NOT NULL,
    estado VARCHAR(50) NOT NULL -- Ej: 'Disponible', 'Ocupada', 'En Limpieza', 'Mantenimiento'
);

-- Tabla RESERVAS
CREATE TABLE RESERVAS (
    id_reserva INT PRIMARY KEY AUTO_INCREMENT,
    id_huesped INT NOT NULL,
    id_habitacion INT, -- Puede ser NULL hasta que se asigne una habitación
    fecha_checkin DATE NOT NULL,
    fecha_checkout DATE NOT NULL,
    cantidad_huespedes INT NOT NULL,
    estado_reserva VARCHAR(50) NOT NULL, -- Ej: 'Confirmada', 'Check-in', 'Check-out', 'Cancelada'
    fecha_creacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_huesped) REFERENCES HUESPEDES(id_huesped),
    FOREIGN KEY (id_habitacion) REFERENCES HABITACIONES(id_habitacion)
);

-- Tabla SERVICIOS_ADICIONALES (si quieres implementarlos en el futuro)
CREATE TABLE SERVICIOS_ADICIONALES (
    id_servicio INT PRIMARY KEY AUTO_INCREMENT,
    nombre_servicio VARCHAR(100) NOT NULL,
    precio_servicio DECIMAL(10, 2) NOT NULL
);

-- Tabla FACTURAS (si quieres implementarlos en el futuro)
CREATE TABLE FACTURAS (
    id_factura INT PRIMARY KEY AUTO_INCREMENT,
    id_reserva INT NOT NULL,
    id_huesped INT NOT NULL,
    fecha_emision DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_factura DECIMAL(10, 2) NOT NULL,
    estado_pago VARCHAR(50) NOT NULL, -- Ej: 'Pendiente', 'Pagada', 'Cancelada'
    FOREIGN KEY (id_reserva) REFERENCES RESERVAS(id_reserva),
    FOREIGN KEY (id_huesped) REFERENCES HUESPEDES(id_huesped)
);

-- Tabla DETALLE_FACTURA (si quieres implementarlos en el futuro)
CREATE TABLE DETALLE_FACTURA (
    id_detalle INT PRIMARY KEY AUTO_INCREMENT,
    id_factura INT NOT NULL,
    id_servicio INT NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10, 2) NOT NULL,
    FOREIGN KEY (id_factura) REFERENCES FACTURAS(id_factura),
    FOREIGN KEY (id_servicio) REFERENCES SERVICIOS_ADICIONALES(id_servicio)
);
