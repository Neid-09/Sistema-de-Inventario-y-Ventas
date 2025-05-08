-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: adppd2
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.32-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `auditoria`
--

DROP TABLE IF EXISTS `auditoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria` (
  `id_auditoria` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) DEFAULT NULL,
  `accion` varchar(100) NOT NULL,
  `tabla_afectada` varchar(100) NOT NULL,
  `id_registro_afectado` int(11) DEFAULT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `datos_anteriores` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`datos_anteriores`)),
  `datos_nuevos` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`datos_nuevos`)),
  PRIMARY KEY (`id_auditoria`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `auditoria_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditoria`
--

LOCK TABLES `auditoria` WRITE;
/*!40000 ALTER TABLE `auditoria` DISABLE KEYS */;
INSERT INTO `auditoria` VALUES (1,NULL,'ELIMINAR','productos',1,'2025-04-20 00:37:57','{\"idProducto\":1,\"codigo\":\"test1\",\"nombre\":\"test1\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(2,NULL,'CREAR','productos',2,'2025-04-20 00:38:46',NULL,'{\"idProducto\":2,\"codigo\":\"test1\",\"nombre\":\"test1\",\"descripcion\":\"111\",\"precioCosto\":1,\"precioVenta\":12,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(3,NULL,'ELIMINAR','productos',2,'2025-04-20 00:40:10','{\"idProducto\":2,\"codigo\":\"test1\",\"nombre\":\"test1\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(4,NULL,'CREAR','productos',3,'2025-04-20 00:47:05',NULL,'{\"idProducto\":3,\"codigo\":\"test1\",\"nombre\":\"test1\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(5,NULL,'CREAR','productos',4,'2025-04-20 00:47:20',NULL,'{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":0,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(6,NULL,'CREAR','categorias',7,'2025-04-20 00:48:05',NULL,'{\"idCategoria\":7,\"nombre\":\"new test\",\"duracionGarantia\":0,\"descripcion\":\"test\",\"estado\":true}'),(7,NULL,'ACTUALIZAR','productos',3,'2025-04-20 00:48:55','{\"idProducto\":3,\"codigo\":\"test1\",\"nombre\":\"test1\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":3,\"codigo\":\"test1\",\"nombre\":\"test1\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":null,\"estado\":true}'),(8,NULL,'CREAR','productos',5,'2025-04-20 00:52:39',NULL,'{\"idProducto\":5,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(9,NULL,'ELIMINAR','productos',5,'2025-04-20 00:54:27','{\"idProducto\":5,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(10,NULL,'CREAR','productos',6,'2025-04-20 00:54:46',NULL,'{\"idProducto\":6,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(11,NULL,'ELIMINAR','productos',6,'2025-04-20 00:54:51','{\"idProducto\":6,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(12,NULL,'ACTUALIZAR','productos',4,'2025-04-26 02:57:49','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":12,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":12,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(13,NULL,'ACTUALIZAR','productos',4,'2025-04-26 02:58:07','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":12,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":12,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(14,NULL,'ACTUALIZAR','productos',4,'2025-04-26 02:59:10','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":12,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":12,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(15,NULL,'CREAR','productos',7,'2025-04-26 03:10:35',NULL,'{\"idProducto\":7,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":1,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":true}'),(16,NULL,'CREAR','productos',8,'2025-05-01 09:47:29',NULL,'{\"idProducto\":8,\"codigo\":\"test4\",\"nombre\":\"test4\",\"descripcion\":\"123\",\"precioCosto\":123,\"precioVenta\":124,\"stock\":0,\"stockMinimo\":5,\"stockMaximo\":50,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":1,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(17,NULL,'CREAR','productos',9,'2025-05-02 21:26:52',NULL,'{\"idProducto\":9,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"delete\",\"precioCosto\":1,\"precioVenta\":2,\"stock\":0,\"stockMinimo\":11,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(18,NULL,'ELIMINAR','productos',9,'2025-05-02 21:26:58','{\"idProducto\":9,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(19,NULL,'ACTUALIZAR','productos',7,'2025-05-02 21:27:22','{\"idProducto\":7,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":23,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":7,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":23,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":1,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(20,NULL,'CREAR','productos',10,'2025-05-02 21:28:57',NULL,'{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":0,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":true}'),(21,NULL,'DESACTIVAR','productos',10,'2025-05-03 00:59:01','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(22,NULL,'DESACTIVAR','productos',10,'2025-05-03 01:06:28','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(23,NULL,'DESACTIVAR','productos',10,'2025-05-03 01:06:33','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(24,NULL,'DESACTIVAR','productos',10,'2025-05-03 01:06:37','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(25,NULL,'CREAR','productos',11,'2025-05-03 01:09:59',NULL,'{\"idProducto\":11,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"test\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(26,NULL,'ELIMINAR','productos',11,'2025-05-03 01:15:13','{\"idProducto\":11,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(27,NULL,'CREAR','productos',12,'2025-05-03 01:15:39',NULL,'{\"idProducto\":12,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"test6\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":12,\"stockMaximo\":13,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(28,NULL,'ELIMINAR','productos',12,'2025-05-03 01:28:02','{\"idProducto\":12,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(29,NULL,'DESACTIVAR','productos',10,'2025-05-03 01:28:05','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(30,NULL,'DESACTIVAR','productos',10,'2025-05-03 01:36:17','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(31,NULL,'DESACTIVAR','productos',10,'2025-05-03 02:05:21','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(32,NULL,'ACTUALIZAR','productos',10,'2025-05-03 02:22:32','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":200,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":true}'),(33,NULL,'CREAR','productos',13,'2025-05-03 02:38:46',NULL,'{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(34,NULL,'ACTUALIZAR','productos',13,'2025-05-03 02:39:04','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":null,\"estado\":true}'),(35,NULL,'ACTUALIZAR','productos',13,'2025-05-03 02:50:26','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":2,\"estado\":true}'),(36,NULL,'ACTUALIZAR','productos',13,'2025-05-03 02:50:40','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":null,\"estado\":true}'),(37,NULL,'ACTUALIZAR','productos',13,'2025-05-03 02:51:24','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":2222,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":2222,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":false}'),(38,NULL,'ACTUALIZAR','productos',13,'2025-05-03 02:52:47','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":2222,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":222,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(39,NULL,'CREAR','categorias',8,'2025-05-03 02:53:52',NULL,'{\"idCategoria\":8,\"nombre\":\"test\",\"duracionGarantia\":0,\"descripcion\":\"test\",\"estado\":true}'),(40,NULL,'ACTUALIZAR','productos',7,'2025-05-03 03:06:49','{\"idProducto\":7,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":23,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":7,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":13,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":1,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":true}'),(41,NULL,'ACTUALIZAR','productos',13,'2025-05-03 03:14:59','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":222,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":222,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":false}'),(42,NULL,'ACTUALIZAR','productos',13,'2025-05-03 03:17:06','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":222,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":622,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(43,NULL,'ACTUALIZAR','productos',13,'2025-05-03 22:02:31','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":622,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":622,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":false}'),(44,NULL,'ACTUALIZAR','productos',13,'2025-05-03 23:44:03','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":622,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":622,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(45,NULL,'CREAR','productos',14,'2025-05-05 04:59:32',NULL,'{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(46,NULL,'ACTUALIZAR','productos',14,'2025-05-05 04:59:39','{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":null,\"estado\":true}'),(47,NULL,'CREAR','productos',15,'2025-05-05 05:00:07',NULL,'{\"idProducto\":15,\"codigo\":\"testd\",\"nombre\":\"testd\",\"descripcion\":\"123\",\"precioCosto\":1,\"precioVenta\":12,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(48,NULL,'ELIMINAR','productos',15,'2025-05-05 05:00:11','{\"idProducto\":15,\"codigo\":\"testd\",\"nombre\":\"testd\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(49,NULL,'ACTUALIZAR','productos',14,'2025-05-05 05:00:15','{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":1,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":1,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":false}'),(50,NULL,'ACTUALIZAR','productos',14,'2025-05-05 05:00:29','{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":1,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":1,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(51,NULL,'CREAR','productos',16,'2025-05-08 04:45:56',NULL,'{\"idProducto\":16,\"codigo\":\"PROD-001\",\"nombre\":\"Panela\",\"descripcion\":\"De caña\",\"precioCosto\":2000,\"precioVenta\":3000,\"stock\":0,\"stockMinimo\":15,\"stockMaximo\":30,\"categoria\":{\"idCategoria\":4,\"nombre\":\"Perecederos1\",\"duracionGarantia\":1,\"descripcion\":\"Se acaban\",\"estado\":true},\"idCategoria\":4,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":true}');
/*!40000 ALTER TABLE `auditoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auditoria_caja`
--

DROP TABLE IF EXISTS `auditoria_caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria_caja` (
  `id_auditoria` int(11) NOT NULL AUTO_INCREMENT,
  `id_caja` int(11) DEFAULT NULL,
  `dinero_esperado` decimal(10,2) NOT NULL,
  `dinero_real` decimal(10,2) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `motivo` varchar(255) DEFAULT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_auditoria`),
  KEY `id_caja` (`id_caja`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `auditoria_caja_ibfk_1` FOREIGN KEY (`id_caja`) REFERENCES `caja` (`id_caja`),
  CONSTRAINT `auditoria_caja_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditoria_caja`
--

LOCK TABLES `auditoria_caja` WRITE;
/*!40000 ALTER TABLE `auditoria_caja` DISABLE KEYS */;
/*!40000 ALTER TABLE `auditoria_caja` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auditoria_stock`
--

DROP TABLE IF EXISTS `auditoria_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria_stock` (
  `id_auditoria` int(11) NOT NULL AUTO_INCREMENT,
  `id_producto` int(11) DEFAULT NULL,
  `stock_esperado` int(11) NOT NULL,
  `stock_real` int(11) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `motivo` varchar(255) DEFAULT NULL,
  `id_usuario` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_auditoria`),
  KEY `id_producto` (`id_producto`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `auditoria_stock_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `auditoria_stock_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditoria_stock`
--

LOCK TABLES `auditoria_stock` WRITE;
/*!40000 ALTER TABLE `auditoria_stock` DISABLE KEYS */;
/*!40000 ALTER TABLE `auditoria_stock` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `caja`
--

DROP TABLE IF EXISTS `caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `caja` (
  `id_caja` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) DEFAULT NULL,
  `fecha_apertura` timestamp NOT NULL DEFAULT current_timestamp(),
  `fecha_cierre` timestamp NULL DEFAULT NULL,
  `dinero_inicial` decimal(10,2) NOT NULL,
  `dinero_total` decimal(10,2) NOT NULL DEFAULT 0.00,
  `estado` varchar(50) NOT NULL,
  PRIMARY KEY (`id_caja`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `caja_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `caja`
--

LOCK TABLES `caja` WRITE;
/*!40000 ALTER TABLE `caja` DISABLE KEYS */;
/*!40000 ALTER TABLE `caja` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categorias`
--

DROP TABLE IF EXISTS `categorias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias` (
  `id_categoria` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `duracion_garantia` int(11) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categorias`
--

LOCK TABLES `categorias` WRITE;
/*!40000 ALTER TABLE `categorias` DISABLE KEYS */;
INSERT INTO `categorias` VALUES (1,'Bebidas alcoholicas',1,'Licores',1),(2,'Bebidas Energisantes',1,'Bebidas Energisantes',1),(4,'Perecederos1',1,'Se acaban',1),(5,'Bebidas lacteas',1,'Leche, yogurts...',1),(6,'Bebidas',1,'Licores',1),(8,'test',1,'test',0);
/*!40000 ALTER TABLE `categorias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clientes` (
  `id_cliente` int(11) NOT NULL AUTO_INCREMENT,
  `cedula` varchar(20) DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `celular` varchar(15) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `requiere_factura_default` tinyint(1) DEFAULT 0,
  `razon_social_fiscal` varchar(255) DEFAULT NULL,
  `rfc_fiscal` varchar(20) DEFAULT NULL,
  `direccion_fiscal` text DEFAULT NULL,
  `correo_fiscal` varchar(100) DEFAULT NULL,
  `uso_cfdi_default` varchar(10) DEFAULT NULL,
  `total_comprado` decimal(12,2) DEFAULT 0.00,
  `puntos_fidelidad` int(11) DEFAULT 0,
  `ultima_compra` timestamp NULL DEFAULT NULL,
  `limite_credito` decimal(10,2) DEFAULT 0.00,
  `fecha_registro` timestamp NOT NULL DEFAULT current_timestamp(),
  `fecha_actualizacion` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `activo` tinyint(1) DEFAULT 1,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `uq_cliente_correo` (`correo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comisiones`
--

DROP TABLE IF EXISTS `comisiones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comisiones` (
  `id_comision` int(11) NOT NULL AUTO_INCREMENT,
  `id_vendedor` int(11) DEFAULT NULL,
  `id_venta` int(11) DEFAULT NULL,
  `base_comisionable` decimal(10,2) NOT NULL,
  `porcentaje_comision` decimal(5,2) NOT NULL,
  `monto_comision` decimal(10,2) NOT NULL,
  `fecha_calculo` timestamp NOT NULL DEFAULT current_timestamp(),
  `estado` varchar(50) NOT NULL,
  PRIMARY KEY (`id_comision`),
  KEY `id_vendedor` (`id_vendedor`),
  KEY `id_venta` (`id_venta`),
  CONSTRAINT `comisiones_ibfk_1` FOREIGN KEY (`id_vendedor`) REFERENCES `vendedor` (`id_vendedor`),
  CONSTRAINT `comisiones_ibfk_2` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`)
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comisiones`
--

LOCK TABLES `comisiones` WRITE;
/*!40000 ALTER TABLE `comisiones` DISABLE KEYS */;
INSERT INTO `comisiones` VALUES (1,1,8,39.00,5.00,1.95,'2025-05-05 02:28:04','PENDIENTE'),(2,1,1,39.00,5.00,1.95,'2025-05-05 02:48:55','PENDIENTE'),(3,1,3,39.00,5.00,1.95,'2025-05-05 03:58:51','PENDIENTE'),(4,1,4,39.00,5.00,1.95,'2025-05-05 04:06:44','PENDIENTE'),(5,1,5,39.00,5.00,1.95,'2025-05-05 04:13:54','PENDIENTE'),(6,1,6,39.00,5.00,1.95,'2025-05-05 04:24:44','PENDIENTE'),(7,1,7,39.00,5.00,1.95,'2025-05-05 04:54:03','PENDIENTE'),(8,1,8,78.00,5.00,3.90,'2025-05-08 02:05:12','PENDIENTE'),(9,1,9,52.00,5.00,2.60,'2025-05-08 02:07:55','PENDIENTE'),(10,1,10,39.00,5.00,1.95,'2025-05-08 02:28:09','PENDIENTE'),(11,1,11,13.00,5.00,0.65,'2025-05-08 03:17:54','PENDIENTE'),(12,1,12,104.00,5.00,5.20,'2025-05-08 03:55:06','PENDIENTE'),(13,1,13,13.00,5.00,0.65,'2025-05-08 04:41:54','PENDIENTE'),(14,1,1,39.00,5.00,1.95,'2025-05-08 07:02:38','PENDIENTE');
/*!40000 ALTER TABLE `comisiones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuracion_empresa`
--

DROP TABLE IF EXISTS `configuracion_empresa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuracion_empresa` (
  `id_configuracion` int(11) NOT NULL AUTO_INCREMENT,
  `razon_social_emisor` varchar(255) NOT NULL,
  `nombre_comercial_emisor` varchar(255) DEFAULT NULL,
  `identificacion_fiscal_emisor` varchar(20) NOT NULL,
  `domicilio_calle` varchar(150) DEFAULT NULL,
  `domicilio_numero_exterior` varchar(20) DEFAULT NULL,
  `domicilio_numero_interior` varchar(20) DEFAULT NULL,
  `domicilio_barrio_colonia` varchar(100) DEFAULT NULL,
  `domicilio_localidad` varchar(100) DEFAULT NULL,
  `domicilio_municipio` varchar(100) DEFAULT NULL,
  `domicilio_estado_provincia` varchar(100) DEFAULT NULL,
  `domicilio_pais` varchar(50) DEFAULT NULL,
  `domicilio_codigo_postal` varchar(10) DEFAULT NULL,
  `regimen_fiscal_emisor` varchar(255) DEFAULT NULL,
  `telefono_contacto` varchar(50) DEFAULT NULL,
  `email_contacto` varchar(100) DEFAULT NULL,
  `pagina_web` varchar(100) DEFAULT NULL,
  `email_facturacion` varchar(100) DEFAULT NULL,
  `logo_url` varchar(255) DEFAULT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp(),
  `fecha_actualizacion` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id_configuracion`),
  UNIQUE KEY `identificacion_fiscal_emisor` (`identificacion_fiscal_emisor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuracion_empresa`
--

LOCK TABLES `configuracion_empresa` WRITE;
/*!40000 ALTER TABLE `configuracion_empresa` DISABLE KEYS */;
/*!40000 ALTER TABLE `configuracion_empresa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuraciones`
--

DROP TABLE IF EXISTS `configuraciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuraciones` (
  `id_config` int(11) NOT NULL AUTO_INCREMENT,
  `clave` varchar(100) NOT NULL,
  `valor` text NOT NULL,
  `descripcion` text DEFAULT NULL,
  PRIMARY KEY (`id_config`),
  UNIQUE KEY `clave` (`clave`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuraciones`
--

LOCK TABLES `configuraciones` WRITE;
/*!40000 ALTER TABLE `configuraciones` DISABLE KEYS */;
INSERT INTO `configuraciones` VALUES (1,'QUINMA','Quinma Corporation','Nombre oficial de la empresa (actualizado)'),(3,'IVA','16','Impuesto'),(6,'NOMBRE DE EMPRESA','EMPRESA1','Este es el nombre de la empresa'),(7,'Ganancia','0.4','Es la ganancia que se optine por producto');
/*!40000 ALTER TABLE `configuraciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `creditos`
--

DROP TABLE IF EXISTS `creditos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `creditos` (
  `id_credito` int(11) NOT NULL AUTO_INCREMENT,
  `id_cliente` int(11) DEFAULT NULL,
  `id_venta` int(11) DEFAULT NULL,
  `monto` decimal(10,2) NOT NULL,
  `saldo_pendiente` decimal(10,2) NOT NULL,
  `fecha_inicio` timestamp NOT NULL DEFAULT current_timestamp(),
  `fecha_vencimiento` timestamp NULL DEFAULT NULL,
  `estado` varchar(50) NOT NULL,
  PRIMARY KEY (`id_credito`),
  KEY `id_cliente` (`id_cliente`),
  KEY `id_venta` (`id_venta`),
  CONSTRAINT `creditos_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`),
  CONSTRAINT `creditos_ibfk_2` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `creditos`
--

LOCK TABLES `creditos` WRITE;
/*!40000 ALTER TABLE `creditos` DISABLE KEYS */;
/*!40000 ALTER TABLE `creditos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_impuestos_factura`
--

DROP TABLE IF EXISTS `detalle_impuestos_factura`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_impuestos_factura` (
  `id_detalle` int(11) NOT NULL AUTO_INCREMENT,
  `id_factura` int(11) DEFAULT NULL,
  `id_tipo_impuesto` int(11) DEFAULT NULL,
  `base_imponible` decimal(10,2) NOT NULL,
  `tasa_aplicada` decimal(5,2) NOT NULL,
  `monto_impuesto` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `id_factura` (`id_factura`),
  KEY `id_tipo_impuesto` (`id_tipo_impuesto`),
  CONSTRAINT `detalle_impuestos_factura_ibfk_1` FOREIGN KEY (`id_factura`) REFERENCES `facturas` (`id_factura`),
  CONSTRAINT `detalle_impuestos_factura_ibfk_2` FOREIGN KEY (`id_tipo_impuesto`) REFERENCES `tipos_impuestos` (`id_tipo_impuesto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_impuestos_factura`
--

LOCK TABLES `detalle_impuestos_factura` WRITE;
/*!40000 ALTER TABLE `detalle_impuestos_factura` DISABLE KEYS */;
/*!40000 ALTER TABLE `detalle_impuestos_factura` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_venta`
--

DROP TABLE IF EXISTS `detalle_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_venta` (
  `id_detalle_venta` int(11) NOT NULL AUTO_INCREMENT,
  `id_venta` int(11) NOT NULL,
  `id_producto` int(11) NOT NULL,
  `id_lote` int(11) DEFAULT NULL,
  `cantidad` int(11) NOT NULL,
  `precio_unitario_original` decimal(10,2) DEFAULT NULL,
  `id_promocion_aplicada` int(11) DEFAULT NULL,
  `precio_unitario_final` decimal(10,2) NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  `costo_unitario_producto` decimal(10,2) DEFAULT 0.00,
  `ganancia_detalle` decimal(12,2) DEFAULT 0.00,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id_detalle_venta`),
  KEY `id_venta` (`id_venta`),
  KEY `id_producto` (`id_producto`),
  KEY `id_lote` (`id_lote`),
  KEY `id_promocion_aplicada` (`id_promocion_aplicada`),
  CONSTRAINT `detalle_venta_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`),
  CONSTRAINT `detalle_venta_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `detalle_venta_ibfk_3` FOREIGN KEY (`id_lote`) REFERENCES `lotes` (`id_lote`),
  CONSTRAINT `detalle_venta_ibfk_4` FOREIGN KEY (`id_promocion_aplicada`) REFERENCES `promociones` (`id_promocion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_venta`
--

LOCK TABLES `detalle_venta` WRITE;
/*!40000 ALTER TABLE `detalle_venta` DISABLE KEYS */;
/*!40000 ALTER TABLE `detalle_venta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `devoluciones`
--

DROP TABLE IF EXISTS `devoluciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `devoluciones` (
  `id_devolucion` int(11) NOT NULL AUTO_INCREMENT,
  `id_venta` int(11) DEFAULT NULL,
  `id_producto` int(11) DEFAULT NULL,
  `cantidad` int(11) NOT NULL,
  `motivo` text DEFAULT NULL,
  `fecha_devolucion` timestamp NOT NULL DEFAULT current_timestamp(),
  `estado` varchar(50) NOT NULL,
  `id_detalle` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_devolucion`),
  KEY `id_venta` (`id_venta`),
  KEY `id_producto` (`id_producto`),
  KEY `id_detalle` (`id_detalle`),
  CONSTRAINT `devoluciones_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`),
  CONSTRAINT `devoluciones_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `devoluciones_ibfk_3` FOREIGN KEY (`id_detalle`) REFERENCES `detalle_venta` (`id_detalle_venta`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `devoluciones`
--

LOCK TABLES `devoluciones` WRITE;
/*!40000 ALTER TABLE `devoluciones` DISABLE KEYS */;
/*!40000 ALTER TABLE `devoluciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `entradas_productos`
--

DROP TABLE IF EXISTS `entradas_productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `entradas_productos` (
  `id_entrada` int(11) NOT NULL AUTO_INCREMENT,
  `id_producto` int(11) NOT NULL,
  `id_proveedor` int(11) DEFAULT NULL,
  `cantidad` int(11) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `tipo_movimiento` varchar(50) NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `motivo` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id_entrada`),
  KEY `id_producto` (`id_producto`),
  KEY `id_proveedor` (`id_proveedor`),
  CONSTRAINT `entradas_productos_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `entradas_productos_ibfk_2` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedores` (`id_proveedor`)
) ENGINE=InnoDB AUTO_INCREMENT=67 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entradas_productos`
--

LOCK TABLES `entradas_productos` WRITE;
/*!40000 ALTER TABLE `entradas_productos` DISABLE KEYS */;
INSERT INTO `entradas_productos` VALUES (1,4,1,12,'2025-04-20 00:47:32','ENTRADA',12.00,'Creación de lote: LOT-test2-20254'),(2,3,2,100,'2025-04-20 00:49:10','ENTRADA',12.00,'Creación de lote: LOT-test1-20254'),(3,7,2,11,'2025-04-26 03:10:48','ENTRADA',12.00,'Creación de lote: LOT-test3-20254'),(4,8,1,1001,'2025-05-01 09:47:37','ENTRADA',123.00,'Creación de lote: LOT-test4-20254'),(5,8,1,10,'2025-05-01 10:12:47','ENTRADA',123.00,'Creación de lote: LOT-test4-20255'),(6,8,1,100,'2025-05-01 10:23:08','ENTRADA',123.00,'Creación de lote: LOT-test4-20255'),(7,7,2,12,'2025-05-01 10:23:56','ENTRADA',12.00,'Creación de lote: LOT-test3-20255'),(8,10,2,11,'2025-05-02 21:29:05','ENTRADA',12.00,'Creación de lote: LOT-test5-20255'),(9,10,2,100,'2025-05-03 02:21:40','ENTRADA',12.00,'Creación de lote: LOT-test5-20255'),(10,10,2,100,'2025-05-03 02:21:59','ENTRADA',12.00,'Creación de lote: LOT-test5-20255'),(11,4,1,1,'2025-05-03 02:22:20','ENTRADA',12.00,'Creación de lote: LOT-test2-20255'),(12,13,1,2222,'2025-05-03 02:50:48','ENTRADA',12.00,'Creación de lote: LOT-test6-20255'),(13,13,1,12,'2025-05-03 02:56:02','ENTRADA',12.00,'Creación de lote: test'),(14,13,1,200,'2025-05-03 03:16:22','ENTRADA',12.00,'Creación de lote: LOT006'),(15,13,1,200,'2025-05-03 03:16:42','ENTRADA',12.00,'Creación de lote: LOT006'),(16,13,1,1,'2025-05-03 23:45:29','ENTRADA',12.00,'Creación de lote: LOT-test6-20255'),(31,3,NULL,2,'2025-05-05 02:28:04','SALIDA',13.00,'Venta VTA-00123'),(32,4,NULL,1,'2025-05-05 02:28:04','SALIDA',13.00,'Venta VTA-00123'),(45,3,NULL,2,'2025-05-05 02:48:55','SALIDA',13.00,'Venta VTA-00124'),(46,4,NULL,1,'2025-05-05 02:48:55','SALIDA',13.00,'Venta VTA-00124'),(47,3,NULL,2,'2025-05-05 04:06:44','SALIDA',13.00,'Venta VTA-00126'),(48,4,NULL,1,'2025-05-05 04:06:44','SALIDA',13.00,'Venta VTA-00126'),(49,3,NULL,2,'2025-05-05 04:13:54','SALIDA',13.00,'Venta VTA-00127'),(50,4,NULL,1,'2025-05-05 04:13:54','SALIDA',13.00,'Venta VTA-00127'),(51,3,2,2,'2025-05-05 04:24:44','SALIDA',13.00,'Venta VTA-00128'),(52,4,1,1,'2025-05-05 04:24:44','SALIDA',13.00,'Venta VTA-00128'),(53,3,2,2,'2025-05-05 04:54:03','SALIDA',13.00,'Venta VTA-00129'),(54,4,1,1,'2025-05-05 04:54:03','SALIDA',13.00,'Venta VTA-00129'),(55,14,1,1,'2025-05-05 04:59:47','ENTRADA',12.00,'Creación de lote: LOT-test7-20255'),(56,3,2,6,'2025-05-08 02:05:11','SALIDA',13.00,'Venta VTA-1746651911650'),(57,3,2,4,'2025-05-08 02:07:55','SALIDA',13.00,'Venta VTA-1746652075626'),(58,3,2,3,'2025-05-08 02:28:09','SALIDA',13.00,'Venta VTA-1746653289745'),(59,3,2,1,'2025-05-08 03:17:54','SALIDA',13.00,'Venta VTA-1746656274740'),(60,3,2,8,'2025-05-08 03:55:06','SALIDA',13.00,'Venta VTA-1746658506634'),(61,3,2,1,'2025-05-08 04:41:54','SALIDA',13.00,'Venta VTA-1746661314011'),(62,16,2,25,'2025-05-08 04:46:06','ENTRADA',2000.00,'Creación de lote: LOT-PROD-001-20255'),(65,3,2,2,'2025-05-08 07:02:38','SALIDA',13.00,'Venta VTA-00129'),(66,4,1,1,'2025-05-08 07:02:38','SALIDA',13.00,'Venta VTA-00129');
/*!40000 ALTER TABLE `entradas_productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facturas`
--

DROP TABLE IF EXISTS `facturas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facturas` (
  `id_factura` int(11) NOT NULL AUTO_INCREMENT,
  `id_venta` int(11) DEFAULT NULL,
  `numero_factura` varchar(50) NOT NULL,
  `fecha_emision` timestamp NOT NULL DEFAULT current_timestamp(),
  `subtotal` decimal(10,2) NOT NULL,
  `total_impuestos` decimal(10,2) NOT NULL,
  `total_con_impuestos` decimal(10,2) NOT NULL,
  `datos_fiscales` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin DEFAULT NULL CHECK (json_valid(`datos_fiscales`)),
  `estado` varchar(50) NOT NULL DEFAULT 'EMITIDA',
  PRIMARY KEY (`id_factura`),
  UNIQUE KEY `numero_factura` (`numero_factura`),
  KEY `id_venta` (`id_venta`),
  CONSTRAINT `facturas_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facturas`
--

LOCK TABLES `facturas` WRITE;
/*!40000 ALTER TABLE `facturas` DISABLE KEYS */;
/*!40000 ALTER TABLE `facturas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `garantias`
--

DROP TABLE IF EXISTS `garantias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `garantias` (
  `id_garantia` int(11) NOT NULL AUTO_INCREMENT,
  `id_venta` int(11) DEFAULT NULL,
  `id_producto` int(11) DEFAULT NULL,
  `id_cliente` int(11) DEFAULT NULL,
  `numero_ticket` varchar(50) DEFAULT NULL,
  `id_motivo` int(11) DEFAULT NULL,
  `fecha_reclamo` timestamp NOT NULL DEFAULT current_timestamp(),
  `fecha_resolucion` timestamp NULL DEFAULT NULL,
  `estado` varchar(50) NOT NULL,
  `descripcion` text DEFAULT NULL,
  PRIMARY KEY (`id_garantia`),
  UNIQUE KEY `numero_ticket` (`numero_ticket`),
  KEY `id_venta` (`id_venta`),
  KEY `id_producto` (`id_producto`),
  KEY `id_cliente` (`id_cliente`),
  KEY `id_motivo` (`id_motivo`),
  CONSTRAINT `garantias_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`),
  CONSTRAINT `garantias_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `garantias_ibfk_3` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`),
  CONSTRAINT `garantias_ibfk_4` FOREIGN KEY (`id_motivo`) REFERENCES `motivos_reclamo` (`id_motivo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `garantias`
--

LOCK TABLES `garantias` WRITE;
/*!40000 ALTER TABLE `garantias` DISABLE KEYS */;
/*!40000 ALTER TABLE `garantias` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `impuestos_aplicables`
--

DROP TABLE IF EXISTS `impuestos_aplicables`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `impuestos_aplicables` (
  `id_impuesto_aplicable` int(11) NOT NULL AUTO_INCREMENT,
  `id_tasa` int(11) DEFAULT NULL,
  `id_producto` int(11) DEFAULT NULL,
  `id_categoria` int(11) DEFAULT NULL,
  `aplica` tinyint(1) NOT NULL DEFAULT 1,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date DEFAULT NULL,
  PRIMARY KEY (`id_impuesto_aplicable`),
  UNIQUE KEY `uk_impuesto_aplicable` (`id_tasa`,`id_producto`,`id_categoria`,`fecha_inicio`),
  KEY `id_producto` (`id_producto`),
  KEY `id_categoria` (`id_categoria`),
  CONSTRAINT `impuestos_aplicables_ibfk_1` FOREIGN KEY (`id_tasa`) REFERENCES `tasas_impuestos` (`id_tasa`),
  CONSTRAINT `impuestos_aplicables_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `impuestos_aplicables_ibfk_3` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id_categoria`),
  CONSTRAINT `chk_producto_o_categoria` CHECK (`id_producto` is null or `id_categoria` is null)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `impuestos_aplicables`
--

LOCK TABLES `impuestos_aplicables` WRITE;
/*!40000 ALTER TABLE `impuestos_aplicables` DISABLE KEYS */;
/*!40000 ALTER TABLE `impuestos_aplicables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lotes`
--

DROP TABLE IF EXISTS `lotes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lotes` (
  `id_lote` int(11) NOT NULL AUTO_INCREMENT,
  `id_entrada` int(11) DEFAULT NULL,
  `id_producto` int(11) DEFAULT NULL,
  `numero_lote` varchar(50) NOT NULL,
  `fecha_entrada` date DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `cantidad` int(11) NOT NULL,
  `activo` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id_lote`),
  KEY `id_entrada` (`id_entrada`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `lotes_ibfk_1` FOREIGN KEY (`id_entrada`) REFERENCES `entradas_productos` (`id_entrada`),
  CONSTRAINT `lotes_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lotes`
--

LOCK TABLES `lotes` WRITE;
/*!40000 ALTER TABLE `lotes` DISABLE KEYS */;
INSERT INTO `lotes` VALUES (1,1,4,'LOT-test2-20254','2025-04-19','2025-05-09',1,1),(2,2,3,'LOT-test1-20254','2025-04-17','2025-05-09',45,1),(3,3,7,'LOT-test3-20254','2025-04-25',NULL,1,1),(4,4,8,'LOT-test4-20254','2025-04-30',NULL,1,1),(5,5,8,'LOT-test4-20255','2025-05-01',NULL,10,1),(6,6,8,'LOT-test4-20255','2025-04-30','2025-05-29',100,1),(7,7,7,'LOT-test3-20255','2025-05-01',NULL,12,1),(8,8,10,'LOT-test5-20255','2025-05-02',NULL,11,0),(9,9,10,'LOT-test5-20255','2025-05-02',NULL,100,1),(10,10,10,'LOT-test5-20255','2025-05-02',NULL,100,1),(11,11,4,'LOT-test2-20255','2025-05-02',NULL,0,0),(12,12,13,'LOT-test6-20255','2025-05-02',NULL,110,1),(13,13,13,'test','2025-05-01','2025-06-06',12,0),(14,14,13,'LOT006','2025-05-02','2025-12-30',200,1),(15,15,13,'LOT006','2025-05-02','2025-12-30',200,1),(16,16,13,'LOT-test6-20255','2025-05-03',NULL,1,1),(17,55,14,'LOT-test7-20255','2025-05-04',NULL,1,1),(18,62,16,'LOT-PROD-001-20255','2025-05-07',NULL,25,1);
/*!40000 ALTER TABLE `lotes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metodos_pago`
--

DROP TABLE IF EXISTS `metodos_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `metodos_pago` (
  `id_metodo_pago` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(50) NOT NULL,
  PRIMARY KEY (`id_metodo_pago`),
  UNIQUE KEY `descripcion` (`descripcion`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `metodos_pago`
--

LOCK TABLES `metodos_pago` WRITE;
/*!40000 ALTER TABLE `metodos_pago` DISABLE KEYS */;
/*!40000 ALTER TABLE `metodos_pago` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `motivos_reclamo`
--

DROP TABLE IF EXISTS `motivos_reclamo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `motivos_reclamo` (
  `id_motivo` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) NOT NULL,
  `es_valido` tinyint(1) NOT NULL,
  PRIMARY KEY (`id_motivo`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `motivos_reclamo`
--

LOCK TABLES `motivos_reclamo` WRITE;
/*!40000 ALTER TABLE `motivos_reclamo` DISABLE KEYS */;
/*!40000 ALTER TABLE `motivos_reclamo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificaciones`
--

DROP TABLE IF EXISTS `notificaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notificaciones` (
  `id_notificacion` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) DEFAULT NULL,
  `titulo` varchar(255) DEFAULT NULL,
  `mensaje` text NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `leida` tinyint(1) DEFAULT 0,
  `tipo` varchar(50) NOT NULL,
  `id_referencia` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_notificacion`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `notificaciones_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=134 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificaciones`
--

LOCK TABLES `notificaciones` WRITE;
/*!40000 ALTER TABLE `notificaciones` DISABLE KEYS */;
INSERT INTO `notificaciones` VALUES (3,28,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',3),(4,29,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',3),(5,30,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',3),(6,31,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',3),(7,33,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',3),(10,28,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 1011 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',8),(11,29,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 1011 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',8),(12,30,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 1011 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',8),(13,31,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 1011 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',8),(14,33,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 1011 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',8),(17,28,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',1),(18,29,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',1),(19,30,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',1),(20,31,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',1),(21,33,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',1),(24,28,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',2),(25,29,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',2),(26,30,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',2),(27,31,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',2),(28,33,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',2),(31,28,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',1),(32,29,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',1),(33,30,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',1),(34,31,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',1),(35,33,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',1),(38,28,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',2),(39,29,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',2),(40,30,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',2),(41,31,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',2),(42,33,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',2),(45,28,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',3),(46,29,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',3),(47,30,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',3),(48,31,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',3),(49,33,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',3),(52,28,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',7),(53,29,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',7),(54,30,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',7),(55,31,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',7),(56,33,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',7),(59,28,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',8),(60,29,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',8),(61,30,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',8),(62,31,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',8),(63,33,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',8),(66,28,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',1),(67,29,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',1),(68,30,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',1),(69,31,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',1),(70,33,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',1),(73,28,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',2),(74,29,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',2),(75,30,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',2),(76,31,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',2),(77,33,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',2),(80,28,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',3),(81,29,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',3),(82,30,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',3),(83,31,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',3),(84,33,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',3),(87,28,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',7),(88,29,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',7),(89,30,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',7),(90,31,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',7),(91,33,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',7),(94,28,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',8),(95,29,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',8),(96,30,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',8),(97,31,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',8),(98,33,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',8),(101,28,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',3),(102,29,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',3),(103,30,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',3),(104,31,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',3),(105,33,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',3),(108,28,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',7),(109,29,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',7),(110,30,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',7),(111,31,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',7),(112,33,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',7),(115,28,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',8),(116,29,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',8),(117,30,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',8),(118,31,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',8),(119,33,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',8),(122,28,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',1),(123,29,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',1),(124,30,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',1),(125,31,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',1),(126,33,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',1),(129,28,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',2),(130,29,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',2),(131,30,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',2),(132,31,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',2),(133,33,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',2);
/*!40000 ALTER TABLE `notificaciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pagos_credito`
--

DROP TABLE IF EXISTS `pagos_credito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagos_credito` (
  `id_pago` int(11) NOT NULL AUTO_INCREMENT,
  `id_credito` int(11) DEFAULT NULL,
  `monto_pagado` decimal(10,2) NOT NULL,
  `fecha_pago` timestamp NOT NULL DEFAULT current_timestamp(),
  `metodo_pago` varchar(50) NOT NULL,
  PRIMARY KEY (`id_pago`),
  KEY `id_credito` (`id_credito`),
  CONSTRAINT `pagos_credito_ibfk_1` FOREIGN KEY (`id_credito`) REFERENCES `creditos` (`id_credito`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pagos_credito`
--

LOCK TABLES `pagos_credito` WRITE;
/*!40000 ALTER TABLE `pagos_credito` DISABLE KEYS */;
/*!40000 ALTER TABLE `pagos_credito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pagos_venta`
--

DROP TABLE IF EXISTS `pagos_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagos_venta` (
  `id_pago` int(11) NOT NULL AUTO_INCREMENT,
  `id_venta` int(11) DEFAULT NULL,
  `id_metodo_pago` int(11) DEFAULT NULL,
  `monto` decimal(10,2) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id_pago`),
  KEY `id_venta` (`id_venta`),
  KEY `id_metodo_pago` (`id_metodo_pago`),
  CONSTRAINT `pagos_venta_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`),
  CONSTRAINT `pagos_venta_ibfk_2` FOREIGN KEY (`id_metodo_pago`) REFERENCES `metodos_pago` (`id_metodo_pago`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pagos_venta`
--

LOCK TABLES `pagos_venta` WRITE;
/*!40000 ALTER TABLE `pagos_venta` DISABLE KEYS */;
/*!40000 ALTER TABLE `pagos_venta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permisos`
--

DROP TABLE IF EXISTS `permisos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permisos` (
  `id_permiso` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  PRIMARY KEY (`id_permiso`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permisos`
--

LOCK TABLES `permisos` WRITE;
/*!40000 ALTER TABLE `permisos` DISABLE KEYS */;
INSERT INTO `permisos` VALUES (1,'ver_usuarios','Permite ver la lista de usuarios del sistema'),(2,'crear_usuario','Permite registrar un nuevo usuario'),(3,'editar_usuario','Permite modificar los datos de un usuario'),(4,'eliminar_usuario','Permite eliminar usuarios del sistema'),(5,'asignar_roles','Permite asignar roles a los usuarios'),(6,'gestionar_roles_permisos','Permite administrar permisos asociados a los roles'),(7,'ver_productos','Permite consultar los productos disponibles en el inventario'),(8,'crear_producto','Permite agregar un nuevo producto al inventario'),(9,'editar_producto','Permite modificar la información de un producto existente'),(10,'eliminar_producto','Permite eliminar productos del inventario'),(11,'ver_categorias','Permite consultar las categorías de productos'),(12,'gestionar_impuestos','Permite gestionar los impuestos aplicables a los productos'),(13,'registrar_entrada_producto','Permite registrar el ingreso de productos al inventario'),(14,'ver_entradas_productos','Permite consultar el historial de entradas de productos'),(15,'ver_proveedores','Permite ver la lista de proveedores'),(16,'gestionar_proveedores','Permite agregar, editar o eliminar proveedores'),(17,'crear_venta','Permite registrar una nueva venta'),(18,'ver_ventas','Permite consultar el historial de ventas'),(19,'generar_factura','Permite generar facturas de ventas'),(20,'ver_facturas','Permite consultar las facturas generadas'),(21,'gestionar_metodos_pago','Permite agregar o modificar métodos de pago'),(22,'registrar_pago_venta','Permite registrar pagos realizados por ventas'),(23,'ver_promociones','Permite consultar promociones disponibles'),(24,'crear_promocion','Permite crear nuevas promociones'),(25,'ver_recompensas','Permite consultar recompensas otorgadas a clientes'),(26,'registrar_devolucion','Permite registrar la devolución de productos'),(27,'ver_devoluciones','Permite ver el historial de devoluciones'),(28,'otorgar_credito','Permite asignar créditos a clientes'),(29,'ver_creditos','Permite consultar los créditos otorgados'),(30,'registrar_pago_credito','Permite registrar pagos de créditos'),(31,'gestionar_garantias','Permite registrar y consultar garantías'),(32,'ver_clientes','Permite consultar la lista de clientes'),(33,'gestionar_clientes','Permite agregar o editar información de clientes'),(34,'ver_vendedores','Permite consultar la lista de vendedores'),(35,'ver_auditorias','Permite consultar las auditorías generales del sistema'),(36,'auditar_stock','Permite auditar el stock de productos'),(37,'gestionar_caja','Permite abrir, cerrar y gestionar la caja'),(38,'ver_auditoria_caja','Permite ver auditorías realizadas a la caja'),(39,'ver_comisiones','Permite consultar las comisiones generadas por ventas'),(40,'gestionar_configuracion','Permite cambiar las configuraciones generales del sistema'),(41,'ver_notificaciones','Permite consultar las notificaciones del sistema'),(42,'gestionar_sesiones','Permite ver o gestionar sesiones activas'),(43,'ver_servicios','Permite ver los servicios ofrecidos'),(44,'gestionar_servicios','Permite crear o editar servicios'),(46,'gestionar_usuarios','Acceso a todo el tema relacionado con users'),(47,'acces_mod_clientes','Permite acceder al modulo de clientes'),(50,'activar_usuario','Activa usuario que se eliminaro(false -> true)');
/*!40000 ALTER TABLE `permisos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permisos_usuario`
--

DROP TABLE IF EXISTS `permisos_usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `permisos_usuario` (
  `id_usuario` int(11) NOT NULL,
  `id_permiso` int(11) NOT NULL,
  PRIMARY KEY (`id_usuario`,`id_permiso`),
  KEY `id_permiso` (`id_permiso`),
  CONSTRAINT `permisos_usuario_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `permisos_usuario_ibfk_2` FOREIGN KEY (`id_permiso`) REFERENCES `permisos` (`id_permiso`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permisos_usuario`
--

LOCK TABLES `permisos_usuario` WRITE;
/*!40000 ALTER TABLE `permisos_usuario` DISABLE KEYS */;
/*!40000 ALTER TABLE `permisos_usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `productos`
--

DROP TABLE IF EXISTS `productos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `productos` (
  `id_producto` int(11) NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) DEFAULT NULL,
  `nombre` varchar(100) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `precio_costo` decimal(10,2) NOT NULL,
  `precio_venta` decimal(10,2) NOT NULL,
  `stock` int(11) NOT NULL DEFAULT 0,
  `stock_minimo` int(11) DEFAULT NULL,
  `stock_maximo` int(11) DEFAULT NULL,
  `id_categoria` int(11) DEFAULT NULL,
  `id_proveedor` int(11) DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `codigo` (`codigo`),
  KEY `id_categoria` (`id_categoria`),
  KEY `id_proveedor` (`id_proveedor`),
  CONSTRAINT `productos_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id_categoria`),
  CONSTRAINT `productos_ibfk_2` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedores` (`id_proveedor`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `productos`
--

LOCK TABLES `productos` WRITE;
/*!40000 ALTER TABLE `productos` DISABLE KEYS */;
INSERT INTO `productos` VALUES (3,'test1','test1','123',12.00,13.00,45,1,11,NULL,2,1),(4,'test2','test2','123',12.00,13.00,1,1,11,NULL,1,1),(7,'test3','test3','123',12.00,13.00,13,1,11,1,2,1),(8,'test4','test4','123',123.00,124.00,111,5,50,1,1,1),(10,'test5','test5','123',12.00,13.00,200,1,10,NULL,2,1),(13,'test6','test6','123',12.00,13.00,511,1,11,NULL,1,1),(14,'test7','test7','123',12.00,13.00,1,1,12,NULL,1,1),(16,'PROD-001','Panela','De caña',2000.00,3000.00,25,15,30,4,2,1);
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promociones`
--

DROP TABLE IF EXISTS `promociones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promociones` (
  `id_promocion` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) NOT NULL,
  `tipo_promocion` varchar(50) NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `id_categoria` int(11) DEFAULT NULL,
  `id_producto` int(11) DEFAULT NULL,
  `activo` tinyint(1) DEFAULT 1,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp(),
  `fecha_actualizacion` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id_promocion`),
  KEY `id_categoria` (`id_categoria`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `promociones_ibfk_1` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id_categoria`),
  CONSTRAINT `promociones_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `promociones`
--

LOCK TABLES `promociones` WRITE;
/*!40000 ALTER TABLE `promociones` DISABLE KEYS */;
/*!40000 ALTER TABLE `promociones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proveedores`
--

DROP TABLE IF EXISTS `proveedores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedores` (
  `id_proveedor` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `contacto` varchar(100) DEFAULT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `correo` varchar(100) DEFAULT NULL,
  `direccion` varchar(150) DEFAULT NULL,
  PRIMARY KEY (`id_proveedor`),
  UNIQUE KEY `uk_proveedores_correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedores`
--

LOCK TABLES `proveedores` WRITE;
/*!40000 ALTER TABLE `proveedores` DISABLE KEYS */;
INSERT INTO `proveedores` VALUES (1,'test','test','3134567785','testemail@gmail.com','ev'),(2,'test2','test2','3186406689','test2@gmail.com','av');
/*!40000 ALTER TABLE `proveedores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recompensas`
--

DROP TABLE IF EXISTS `recompensas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `recompensas` (
  `id_recompensa` int(11) NOT NULL AUTO_INCREMENT,
  `id_cliente` int(11) DEFAULT NULL,
  `puntos_usados` int(11) NOT NULL,
  `descripcion_recompensa` varchar(255) NOT NULL,
  `fecha_reclamo` timestamp NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`id_recompensa`),
  KEY `id_cliente` (`id_cliente`),
  CONSTRAINT `recompensas_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recompensas`
--

LOCK TABLES `recompensas` WRITE;
/*!40000 ALTER TABLE `recompensas` DISABLE KEYS */;
INSERT INTO `recompensas` VALUES (1,2,75,'Bono de $5','2025-05-02 06:22:05');
/*!40000 ALTER TABLE `recompensas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id_rol` int(11) NOT NULL AUTO_INCREMENT,
  `rol` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `nombre` (`rol`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'Administrador'),(9,'MROL_PRUEBA12'),(2,'Vendedor');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles_permisos`
--

DROP TABLE IF EXISTS `roles_permisos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles_permisos` (
  `id_rol` int(11) NOT NULL,
  `id_permiso` int(11) NOT NULL,
  PRIMARY KEY (`id_rol`,`id_permiso`),
  KEY `id_permiso` (`id_permiso`),
  CONSTRAINT `roles_permisos_ibfk_1` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`),
  CONSTRAINT `roles_permisos_ibfk_2` FOREIGN KEY (`id_permiso`) REFERENCES `permisos` (`id_permiso`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles_permisos`
--

LOCK TABLES `roles_permisos` WRITE;
/*!40000 ALTER TABLE `roles_permisos` DISABLE KEYS */;
INSERT INTO `roles_permisos` VALUES (1,1),(1,2),(1,3),(1,4),(1,5),(1,6),(1,7),(1,8),(1,9),(1,10),(1,11),(1,12),(1,13),(1,14),(1,15),(1,16),(1,17),(1,18),(1,19),(1,20),(1,21),(1,22),(1,23),(1,24),(1,25),(1,26),(1,27),(1,28),(1,29),(1,30),(1,31),(1,32),(1,33),(1,34),(1,35),(1,36),(1,37),(1,38),(1,39),(1,40),(1,41),(1,42),(1,43),(1,44),(1,46),(1,47),(1,50),(2,7),(2,17),(2,18),(2,19),(2,20),(2,22),(2,23),(2,25),(2,26),(2,27),(2,28),(2,29),(2,30),(2,32),(2,33),(2,41),(9,4),(9,5),(9,6),(9,7),(9,8),(9,9),(9,10),(9,11),(9,12),(9,13),(9,14),(9,15),(9,16),(9,17),(9,18),(9,19),(9,20),(9,21),(9,22),(9,23),(9,24),(9,25),(9,26),(9,27),(9,28),(9,29),(9,30),(9,31),(9,32),(9,33),(9,34),(9,35),(9,36),(9,37),(9,38),(9,39),(9,40),(9,41),(9,42),(9,43),(9,44),(9,46),(9,47);
/*!40000 ALTER TABLE `roles_permisos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicios`
--

DROP TABLE IF EXISTS `servicios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicios` (
  `id_servicio` int(11) NOT NULL AUTO_INCREMENT,
  `id_garantia` int(11) DEFAULT NULL,
  `descripcion` text NOT NULL,
  `costo` decimal(10,2) DEFAULT NULL,
  `fecha_inicio` timestamp NOT NULL DEFAULT current_timestamp(),
  `fecha_fin` timestamp NULL DEFAULT NULL,
  `estado` varchar(50) NOT NULL,
  PRIMARY KEY (`id_servicio`),
  KEY `id_garantia` (`id_garantia`),
  CONSTRAINT `servicios_ibfk_1` FOREIGN KEY (`id_garantia`) REFERENCES `garantias` (`id_garantia`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servicios`
--

LOCK TABLES `servicios` WRITE;
/*!40000 ALTER TABLE `servicios` DISABLE KEYS */;
/*!40000 ALTER TABLE `servicios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sesiones`
--

DROP TABLE IF EXISTS `sesiones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sesiones` (
  `id_sesion` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) DEFAULT NULL,
  `fecha_login` timestamp NOT NULL DEFAULT current_timestamp(),
  `fecha_logout` timestamp NULL DEFAULT NULL,
  `ip_address` varchar(45) DEFAULT NULL,
  `dispositivo` varchar(100) DEFAULT NULL,
  `estado` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_sesion`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `sesiones_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sesiones`
--

LOCK TABLES `sesiones` WRITE;
/*!40000 ALTER TABLE `sesiones` DISABLE KEYS */;
/*!40000 ALTER TABLE `sesiones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tasas_impuestos`
--

DROP TABLE IF EXISTS `tasas_impuestos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tasas_impuestos` (
  `id_tasa` int(11) NOT NULL AUTO_INCREMENT,
  `id_tipo_impuesto` int(11) DEFAULT NULL,
  `tasa` decimal(5,2) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date DEFAULT NULL,
  `descripcion` text DEFAULT NULL,
  PRIMARY KEY (`id_tasa`),
  UNIQUE KEY `uk_tasa_impuesto_fecha` (`id_tipo_impuesto`,`fecha_inicio`),
  CONSTRAINT `tasas_impuestos_ibfk_1` FOREIGN KEY (`id_tipo_impuesto`) REFERENCES `tipos_impuestos` (`id_tipo_impuesto`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tasas_impuestos`
--

LOCK TABLES `tasas_impuestos` WRITE;
/*!40000 ALTER TABLE `tasas_impuestos` DISABLE KEYS */;
/*!40000 ALTER TABLE `tasas_impuestos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipos_impuestos`
--

DROP TABLE IF EXISTS `tipos_impuestos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipos_impuestos` (
  `id_tipo_impuesto` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `es_porcentual` tinyint(1) NOT NULL DEFAULT 1,
  `activo` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_tipo_impuesto`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipos_impuestos`
--

LOCK TABLES `tipos_impuestos` WRITE;
/*!40000 ALTER TABLE `tipos_impuestos` DISABLE KEYS */;
/*!40000 ALTER TABLE `tipos_impuestos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id_usuario` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) NOT NULL,
  `correo` varchar(100) NOT NULL,
  `telefono` varchar(20) DEFAULT NULL,
  `contraseña` varchar(255) NOT NULL,
  `id_rol` int(11) NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `correo` (`correo`),
  KEY `id_rol` (`id_rol`),
  CONSTRAINT `usuarios_ibfk_1` FOREIGN KEY (`id_rol`) REFERENCES `roles` (`id_rol`)
) ENGINE=InnoDB AUTO_INCREMENT=36 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (24,'Admin','admin','1234567890','$2a$10$017NV/XpgjRQ0jxxbJaw6eO.P2J1BgTYo85xYz7uPrzxXS5l01XSm',1,1),(27,'Neider','neider','312','$2a$10$v0.lXmfN1iJbfXq8iunZaeHCN..FfsDCB4IO2rLszr8ZRF00fA/7y',1,1),(28,'Andres','andres','33131','$2a$10$DlL9REAu4Se0t28jLGuoYelAxqcN72qkYckIhVo6gDlzTogZiM4be',1,1),(29,'Leonel','leonel','3131313','$2a$10$ADoHT6KFAdEoB1FaCUB.U.4Fzi/45lWsxbEQ5eUAf9XyOVe8jdHQy',1,1),(30,'Daniel','daniel','31313131','$2a$10$n5K8RmKAvqUAH2Nw5oeXX.dUhAgJzhjvYPSR/UAiFQy5gBXU7w1L2',1,1),(31,'Vendedor','vendedor','313','$2a$10$0zm.eHCfzngIYyzkOVMUounwteDrLGQldDeGLq53LIYYaZBJWhvxW',2,1),(33,'testUpdapte','testUpdate','5555555555','$2a$10$e92kA4sc5HJBqv2Je/x3EuVv2LNXGDgEId2v6LIvsD3VNoFJVGX1C',9,1),(35,'test','test','123','$2a$10$Jjt74NbKQ8dmUD2XmlG2W.NGB9bg7oUR.1ipXNTEeRxXiE.17RGxa',1,1);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vendedor`
--

DROP TABLE IF EXISTS `vendedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendedor` (
  `id_vendedor` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuario` int(11) DEFAULT NULL,
  `objetivo_ventas` decimal(10,2) DEFAULT NULL,
  `fecha_contratacion` date DEFAULT NULL,
  PRIMARY KEY (`id_vendedor`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `vendedor_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendedor`
--

LOCK TABLES `vendedor` WRITE;
/*!40000 ALTER TABLE `vendedor` DISABLE KEYS */;
INSERT INTO `vendedor` VALUES (1,31,1000000.00,'2025-04-19');
/*!40000 ALTER TABLE `vendedor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ventas`
--

DROP TABLE IF EXISTS `ventas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ventas` (
  `id_venta` int(11) NOT NULL AUTO_INCREMENT,
  `id_cliente` int(11) DEFAULT NULL,
  `id_vendedor` int(11) NOT NULL,
  `numero_venta` varchar(50) DEFAULT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `requiere_factura` tinyint(1) DEFAULT 0,
  `aplicar_impuestos` tinyint(1) DEFAULT 0,
  `tipo_pago` varchar(50) DEFAULT NULL,
  `subtotal` decimal(12,2) DEFAULT 0.00,
  `total_impuestos` decimal(12,2) DEFAULT 0.00,
  `total_con_impuestos` decimal(12,2) DEFAULT 0.00,
  `estado_venta` varchar(50) DEFAULT 'PENDIENTE',
  `observaciones` text DEFAULT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT current_timestamp(),
  `fecha_actualizacion` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  PRIMARY KEY (`id_venta`),
  UNIQUE KEY `numero_venta` (`numero_venta`),
  KEY `id_cliente` (`id_cliente`),
  KEY `id_vendedor` (`id_vendedor`),
  CONSTRAINT `ventas_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`),
  CONSTRAINT `ventas_ibfk_2` FOREIGN KEY (`id_vendedor`) REFERENCES `vendedores` (`id_vendedor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ventas`
--

LOCK TABLES `ventas` WRITE;
/*!40000 ALTER TABLE `ventas` DISABLE KEYS */;
/*!40000 ALTER TABLE `ventas` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-08 12:26:51
