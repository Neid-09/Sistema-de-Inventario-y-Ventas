-- MySQL dump 10.13  Distrib 8.0.41, for Win64 (x86_64)
--
-- Host: localhost    Database: appd2
-- ------------------------------------------------------
-- Server version	9.2.0

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
  `id_auditoria` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int DEFAULT NULL,
  `accion` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `tabla_afectada` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `id_registro_afectado` int DEFAULT NULL,
  `fecha` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `datos_anteriores` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `datos_nuevos` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  PRIMARY KEY (`id_auditoria`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `auditoria_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`),
  CONSTRAINT `auditoria_chk_1` CHECK (json_valid(`datos_anteriores`)),
  CONSTRAINT `auditoria_chk_2` CHECK (json_valid(`datos_nuevos`))
) ENGINE=InnoDB AUTO_INCREMENT=62 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditoria`
--

LOCK TABLES `auditoria` WRITE;
/*!40000 ALTER TABLE `auditoria` DISABLE KEYS */;
INSERT INTO `auditoria` VALUES (1,NULL,'ELIMINAR','productos',1,'2025-04-20 00:37:57','{\"idProducto\":1,\"codigo\":\"test1\",\"nombre\":\"test1\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(2,NULL,'CREAR','productos',2,'2025-04-20 00:38:46',NULL,'{\"idProducto\":2,\"codigo\":\"test1\",\"nombre\":\"test1\",\"descripcion\":\"111\",\"precioCosto\":1,\"precioVenta\":12,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(3,NULL,'ELIMINAR','productos',2,'2025-04-20 00:40:10','{\"idProducto\":2,\"codigo\":\"test1\",\"nombre\":\"test1\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(4,NULL,'CREAR','productos',3,'2025-04-20 00:47:05',NULL,'{\"idProducto\":3,\"codigo\":\"test1\",\"nombre\":\"test1\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(5,NULL,'CREAR','productos',4,'2025-04-20 00:47:20',NULL,'{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":0,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(6,NULL,'CREAR','categorias',7,'2025-04-20 00:48:05',NULL,'{\"idCategoria\":7,\"nombre\":\"new test\",\"duracionGarantia\":0,\"descripcion\":\"test\",\"estado\":true}'),(7,NULL,'ACTUALIZAR','productos',3,'2025-04-20 00:48:55','{\"idProducto\":3,\"codigo\":\"test1\",\"nombre\":\"test1\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":3,\"codigo\":\"test1\",\"nombre\":\"test1\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":null,\"estado\":true}'),(8,NULL,'CREAR','productos',5,'2025-04-20 00:52:39',NULL,'{\"idProducto\":5,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(9,NULL,'ELIMINAR','productos',5,'2025-04-20 00:54:27','{\"idProducto\":5,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(10,NULL,'CREAR','productos',6,'2025-04-20 00:54:46',NULL,'{\"idProducto\":6,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(11,NULL,'ELIMINAR','productos',6,'2025-04-20 00:54:51','{\"idProducto\":6,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(12,NULL,'ACTUALIZAR','productos',4,'2025-04-26 02:57:49','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":12,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":12,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(13,NULL,'ACTUALIZAR','productos',4,'2025-04-26 02:58:07','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":12,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":12,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(14,NULL,'ACTUALIZAR','productos',4,'2025-04-26 02:59:10','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":12,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":12,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(15,NULL,'CREAR','productos',7,'2025-04-26 03:10:35',NULL,'{\"idProducto\":7,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":1,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":true}'),(16,NULL,'CREAR','productos',8,'2025-05-01 09:47:29',NULL,'{\"idProducto\":8,\"codigo\":\"test4\",\"nombre\":\"test4\",\"descripcion\":\"123\",\"precioCosto\":123,\"precioVenta\":124,\"stock\":0,\"stockMinimo\":5,\"stockMaximo\":50,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":1,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(17,NULL,'CREAR','productos',9,'2025-05-02 21:26:52',NULL,'{\"idProducto\":9,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"delete\",\"precioCosto\":1,\"precioVenta\":2,\"stock\":0,\"stockMinimo\":11,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(18,NULL,'ELIMINAR','productos',9,'2025-05-02 21:26:58','{\"idProducto\":9,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(19,NULL,'ACTUALIZAR','productos',7,'2025-05-02 21:27:22','{\"idProducto\":7,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":23,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":7,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":23,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":1,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(20,NULL,'CREAR','productos',10,'2025-05-02 21:28:57',NULL,'{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":0,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":true}'),(21,NULL,'DESACTIVAR','productos',10,'2025-05-03 00:59:01','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(22,NULL,'DESACTIVAR','productos',10,'2025-05-03 01:06:28','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(23,NULL,'DESACTIVAR','productos',10,'2025-05-03 01:06:33','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(24,NULL,'DESACTIVAR','productos',10,'2025-05-03 01:06:37','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(25,NULL,'CREAR','productos',11,'2025-05-03 01:09:59',NULL,'{\"idProducto\":11,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"test\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(26,NULL,'ELIMINAR','productos',11,'2025-05-03 01:15:13','{\"idProducto\":11,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(27,NULL,'CREAR','productos',12,'2025-05-03 01:15:39',NULL,'{\"idProducto\":12,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"test6\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":12,\"stockMaximo\":13,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(28,NULL,'ELIMINAR','productos',12,'2025-05-03 01:28:02','{\"idProducto\":12,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(29,NULL,'DESACTIVAR','productos',10,'2025-05-03 01:28:05','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(30,NULL,'DESACTIVAR','productos',10,'2025-05-03 01:36:17','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(31,NULL,'DESACTIVAR','productos',10,'2025-05-03 02:05:21','{\"idProducto\":10,\"codigo\":null,\"nombre\":null,\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":false}'),(32,NULL,'ACTUALIZAR','productos',10,'2025-05-03 02:22:32','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":10,\"codigo\":\"test5\",\"nombre\":\"test5\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":200,\"stockMinimo\":1,\"stockMaximo\":10,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":true}'),(33,NULL,'CREAR','productos',13,'2025-05-03 02:38:46',NULL,'{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(34,NULL,'ACTUALIZAR','productos',13,'2025-05-03 02:39:04','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":null,\"estado\":true}'),(35,NULL,'ACTUALIZAR','productos',13,'2025-05-03 02:50:26','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":2,\"estado\":true}'),(36,NULL,'ACTUALIZAR','productos',13,'2025-05-03 02:50:40','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":null,\"estado\":true}'),(37,NULL,'ACTUALIZAR','productos',13,'2025-05-03 02:51:24','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":2222,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":2222,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":false}'),(38,NULL,'ACTUALIZAR','productos',13,'2025-05-03 02:52:47','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":2222,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":222,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(39,NULL,'CREAR','categorias',8,'2025-05-03 02:53:52',NULL,'{\"idCategoria\":8,\"nombre\":\"test\",\"duracionGarantia\":0,\"descripcion\":\"test\",\"estado\":true}'),(40,NULL,'ACTUALIZAR','productos',7,'2025-05-03 03:06:49','{\"idProducto\":7,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":23,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":7,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":13,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":1,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":true}'),(41,NULL,'ACTUALIZAR','productos',13,'2025-05-03 03:14:59','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":222,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":222,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":false}'),(42,NULL,'ACTUALIZAR','productos',13,'2025-05-03 03:17:06','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":222,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":622,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(43,NULL,'ACTUALIZAR','productos',13,'2025-05-03 22:02:31','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":622,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":622,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":false}'),(44,NULL,'ACTUALIZAR','productos',13,'2025-05-03 23:44:03','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":622,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":622,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(45,NULL,'CREAR','productos',14,'2025-05-05 04:59:32',NULL,'{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":\"123\",\"precioCosto\":12,\"precioVenta\":13,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(46,NULL,'ACTUALIZAR','productos',14,'2025-05-05 04:59:39','{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":null,\"estado\":true}'),(47,NULL,'CREAR','productos',15,'2025-05-05 05:00:07',NULL,'{\"idProducto\":15,\"codigo\":\"testd\",\"nombre\":\"testd\",\"descripcion\":\"123\",\"precioCosto\":1,\"precioVenta\":12,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":0,\"proveedor\":null,\"idProveedor\":0,\"estado\":true}'),(48,NULL,'ELIMINAR','productos',15,'2025-05-05 05:00:11','{\"idProducto\":15,\"codigo\":\"testd\",\"nombre\":\"testd\",\"descripcion\":null,\"precioCosto\":null,\"precioVenta\":null,\"stock\":null,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}',NULL),(49,NULL,'ACTUALIZAR','productos',14,'2025-05-05 05:00:15','{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":1,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":1,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":false}'),(50,NULL,'ACTUALIZAR','productos',14,'2025-05-05 05:00:29','{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":1,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":false}','{\"idProducto\":14,\"codigo\":\"test7\",\"nombre\":\"test7\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":1,\"stockMinimo\":1,\"stockMaximo\":12,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(51,NULL,'CREAR','productos',16,'2025-05-08 04:45:56',NULL,'{\"idProducto\":16,\"codigo\":\"PROD-001\",\"nombre\":\"Panela\",\"descripcion\":\"De caña\",\"precioCosto\":2000,\"precioVenta\":3000,\"stock\":0,\"stockMinimo\":15,\"stockMaximo\":30,\"categoria\":{\"idCategoria\":4,\"nombre\":\"Perecederos1\",\"duracionGarantia\":1,\"descripcion\":\"Se acaban\",\"estado\":true},\"idCategoria\":4,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":true}'),(52,27,'ACTUALIZAR','productos',4,'2025-05-15 06:14:42','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":22,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":4,\"codigo\":\"test2\",\"nombre\":\"test2\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":22,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(53,27,'ACTUALIZAR','productos',7,'2025-05-15 06:15:00','{\"idProducto\":7,\"codigo\":\"test3\",\"nombre\":\"test3\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":6,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":7,\"codigo\":\"PROD-002\",\"nombre\":\"test3\",\"descripcion\":\"123\",\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":6,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":1,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":2,\"estado\":true}'),(54,27,'ACTUALIZAR','productos',7,'2025-05-15 06:15:26','{\"idProducto\":7,\"codigo\":\"PROD-002\",\"nombre\":\"test3\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":6,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":7,\"codigo\":\"PROD-002\",\"nombre\":\"Cerveza\",\"descripcion\":\"123\",\"precioCosto\":3000,\"precioVenta\":4000,\"stock\":6,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":1,\"proveedor\":null,\"idProveedor\":2,\"estado\":true}'),(55,27,'CREAR','tipos_impuestos',1,'2025-05-17 21:40:51',NULL,'{\"idTipoImpuesto\":1,\"nombre\":\"IVA\",\"descripcion\":\"Impuesto\",\"esPorcentual\":true,\"activo\":true}'),(56,27,'CREAR','tasas_impuestos',1,'2025-05-17 21:41:19',NULL,'{\"idTasa\":1,\"tipoImpuestoId\":1,\"tipoImpuesto\":{\"idTipoImpuesto\":1,\"nombre\":\"IVA\",\"descripcion\":\"Impuesto\",\"esPorcentual\":true,\"activo\":true},\"tasa\":16.0,\"fechaInicio\":\"2025-05-17T00:00:00.000+00:00\",\"fechaFin\":\"2026-05-16T00:00:00.000+00:00\",\"descripcion\":\"Iva\"}'),(57,27,'CREAR','impuestos_aplicables',1,'2025-05-17 22:38:06',NULL,'{\"idImpuestoAplicable\":1,\"tasaImpuesto\":{\"idTasa\":1,\"tipoImpuestoId\":1,\"tipoImpuesto\":{\"idTipoImpuesto\":1,\"nombre\":\"IVA\",\"descripcion\":\"Impuesto\",\"esPorcentual\":true,\"activo\":true},\"tasa\":16.00,\"fechaInicio\":\"2025-05-16\",\"fechaFin\":\"2026-05-15\",\"descripcion\":\"Iva\"},\"idTasa\":null,\"producto\":null,\"idProducto\":null,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":null,\"aplica\":true,\"fechaInicio\":\"2025-05-17T00:00:00.000+00:00\",\"fechaFin\":null}'),(58,27,'ACTUALIZAR','impuestos_aplicables',1,'2025-05-17 22:38:33','{\"idImpuestoAplicable\":1,\"tasaImpuesto\":{\"idTasa\":1,\"tipoImpuestoId\":1,\"tipoImpuesto\":{\"idTipoImpuesto\":1,\"nombre\":\"IVA\",\"descripcion\":\"Impuesto\",\"esPorcentual\":true,\"activo\":true},\"tasa\":16.00,\"fechaInicio\":\"2025-05-16\",\"fechaFin\":\"2026-05-15\",\"descripcion\":\"Iva\"},\"idTasa\":1,\"producto\":null,\"idProducto\":null,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":1,\"aplica\":true,\"fechaInicio\":\"2025-05-16\",\"fechaFin\":null}','{\"idImpuestoAplicable\":1,\"tasaImpuesto\":{\"idTasa\":1,\"tipoImpuestoId\":1,\"tipoImpuesto\":{\"idTipoImpuesto\":1,\"nombre\":\"IVA\",\"descripcion\":\"Impuesto\",\"esPorcentual\":true,\"activo\":true},\"tasa\":16.00,\"fechaInicio\":\"2025-05-16\",\"fechaFin\":\"2026-05-15\",\"descripcion\":\"Iva\"},\"idTasa\":1,\"producto\":null,\"idProducto\":null,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":1,\"aplica\":false,\"fechaInicio\":\"2025-05-15\",\"fechaFin\":null}'),(59,27,'ACTUALIZAR','productos',13,'2025-05-18 00:14:12','{\"idProducto\":13,\"codigo\":\"test6\",\"nombre\":\"test6\",\"descripcion\":null,\"precioCosto\":12.00,\"precioVenta\":13.00,\"stock\":510,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":13,\"codigo\":\"PROD-003\",\"nombre\":\"Cereales\",\"descripcion\":\"123\",\"precioCosto\":5000,\"precioVenta\":7000,\"stock\":510,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":{\"idProveedor\":1,\"nombre\":\"test\",\"contacto\":\"test\",\"telefono\":\"3134567785\",\"correo\":\"testemail@gmail.com\",\"direccion\":\"ev\"},\"idProveedor\":1,\"estado\":true}'),(60,27,'ACTUALIZAR','impuestos_aplicables',1,'2025-05-19 03:35:39','{\"idImpuestoAplicable\":1,\"tasaImpuesto\":{\"idTasa\":1,\"tipoImpuestoId\":1,\"tipoImpuesto\":{\"idTipoImpuesto\":1,\"nombre\":\"IVA\",\"descripcion\":\"Impuesto\",\"esPorcentual\":true,\"activo\":true},\"tasa\":16.00,\"fechaInicio\":\"2025-05-16\",\"fechaFin\":\"2026-05-15\",\"descripcion\":\"Iva\"},\"idTasa\":1,\"producto\":null,\"idProducto\":null,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":1,\"aplica\":false,\"fechaInicio\":\"2025-05-15\",\"fechaFin\":null}','{\"idImpuestoAplicable\":1,\"tasaImpuesto\":{\"idTasa\":1,\"tipoImpuestoId\":1,\"tipoImpuesto\":{\"idTipoImpuesto\":1,\"nombre\":\"IVA\",\"descripcion\":\"Impuesto\",\"esPorcentual\":true,\"activo\":true},\"tasa\":16.00,\"fechaInicio\":\"2025-05-16\",\"fechaFin\":\"2026-05-15\",\"descripcion\":\"Iva\"},\"idTasa\":1,\"producto\":{\"idProducto\":7,\"codigo\":\"PROD-002\",\"nombre\":\"Cerveza\",\"descripcion\":\"123\",\"precioCosto\":3000.00,\"precioVenta\":4000.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true},\"idProducto\":null,\"categoria\":null,\"idCategoria\":1,\"aplica\":true,\"fechaInicio\":\"2025-05-14\",\"fechaFin\":null}'),(61,27,'ACTUALIZAR','productos',7,'2025-05-19 03:36:30','{\"idProducto\":7,\"codigo\":\"PROD-002\",\"nombre\":\"Cerveza\",\"descripcion\":null,\"precioCosto\":3000.00,\"precioVenta\":4000.00,\"stock\":0,\"stockMinimo\":null,\"stockMaximo\":null,\"categoria\":null,\"idCategoria\":null,\"proveedor\":null,\"idProveedor\":null,\"estado\":true}','{\"idProducto\":7,\"codigo\":\"PROD-002\",\"nombre\":\"Cerveza\",\"descripcion\":\"123\",\"precioCosto\":3000.00,\"precioVenta\":4000.00,\"stock\":0,\"stockMinimo\":1,\"stockMaximo\":11,\"categoria\":{\"idCategoria\":1,\"nombre\":\"Bebidas alcoholicas\",\"duracionGarantia\":1,\"descripcion\":\"Licores\",\"estado\":true},\"idCategoria\":null,\"proveedor\":{\"idProveedor\":2,\"nombre\":\"test2\",\"contacto\":\"test2\",\"telefono\":\"3186406689\",\"correo\":\"test2@gmail.com\",\"direccion\":\"av\"},\"idProveedor\":null,\"estado\":true}');
/*!40000 ALTER TABLE `auditoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auditoria_caja`
--

DROP TABLE IF EXISTS `auditoria_caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria_caja` (
  `id_auditoria` int NOT NULL AUTO_INCREMENT,
  `id_caja` int NOT NULL,
  `dinero_esperado` decimal(10,2) NOT NULL,
  `dinero_real` decimal(10,2) NOT NULL,
  `diferencia` decimal(10,2) GENERATED ALWAYS AS ((`dinero_real` - `dinero_esperado`)) STORED,
  `fecha` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `motivo` varchar(255) DEFAULT NULL,
  `id_usuario` int NOT NULL,
  PRIMARY KEY (`id_auditoria`),
  KEY `id_caja` (`id_caja`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `auditoria_caja_ibfk_1` FOREIGN KEY (`id_caja`) REFERENCES `caja` (`id_caja`),
  CONSTRAINT `auditoria_caja_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=24 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `auditoria_caja`
--

LOCK TABLES `auditoria_caja` WRITE;
/*!40000 ALTER TABLE `auditoria_caja` DISABLE KEYS */;
INSERT INTO `auditoria_caja` (`id_auditoria`, `id_caja`, `dinero_esperado`, `dinero_real`, `fecha`, `motivo`, `id_usuario`) VALUES (1,1,200000.00,150000.00,'2025-05-25 06:36:01','Discrepancia al cierre',31),(2,2,200000.00,1500000.00,'2025-05-25 06:45:49','Discrepancia al cierre',31),(3,3,200000.00,1500000.00,'2025-05-25 06:51:24','Discrepancia al cierre',31),(4,4,3000000.00,1600000.00,'2025-05-25 06:52:07','Discrepancia al cierre',31),(5,1,150.00,149.50,'2025-05-25 07:26:09','Corrección de diferencia',31),(6,1,150000.00,100000.00,'2025-05-25 07:32:01','Corrección de diferencia',31),(7,1,100000.00,150000.00,'2025-05-25 07:32:38','Corrección de diferencia',31),(8,5,2140750.00,1140750.00,'2025-05-27 04:54:08','Discrepancia al cierre',31),(9,6,2000000.00,1000000.00,'2025-05-27 10:04:55','Discrepancia al cierre',31),(10,7,2000000.00,1140750.00,'2025-05-27 10:19:57','Discrepancia al cierre',31),(11,8,20000000.00,1140750.00,'2025-05-27 10:24:17','Discrepancia al cierre',31),(12,9,20000000.00,1140750.00,'2025-05-27 10:28:56','Discrepancia al cierre',31),(13,10,2281500.00,1140750.00,'2025-05-27 10:31:02','Discrepancia al cierre',31),(14,11,200.00,10000.00,'2025-05-27 10:35:34','Discrepancia al cierre',27),(15,12,20000.00,120000.00,'2025-05-27 10:37:45','Discrepancia al cierre',27),(16,13,260000.00,140000.00,'2025-05-27 10:38:26','Discrepancia al cierre',27),(17,14,280000.00,150000.00,'2025-05-27 11:36:16','Discrepancia al cierre',27),(18,15,300000.00,160000.00,'2025-05-27 21:26:54','Discrepancia al cierre',27),(19,16,320000.00,160000.00,'2025-05-27 21:29:10','Discrepancia al cierre',27),(20,17,2288500.00,1147750.00,'2025-05-27 21:31:27','Discrepancia al cierre',31),(21,18,320000.00,160000.00,'2025-05-27 23:13:32','Discrepancia al cierre',27),(22,19,320000.00,170000.00,'2025-05-28 04:50:27','Discrepancia al cierre',27),(23,20,340176.00,170176.00,'2025-05-28 05:47:13','Discrepancia al cierre',27);
/*!40000 ALTER TABLE `auditoria_caja` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `auditoria_stock`
--

DROP TABLE IF EXISTS `auditoria_stock`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `auditoria_stock` (
  `id_auditoria` int NOT NULL AUTO_INCREMENT,
  `id_producto` int DEFAULT NULL,
  `stock_esperado` int NOT NULL,
  `stock_real` int NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `motivo` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_usuario` int DEFAULT NULL,
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
  `id_caja` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int NOT NULL,
  `fecha_apertura` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_cierre` timestamp NULL DEFAULT NULL,
  `dinero_inicial` decimal(10,2) NOT NULL,
  `dinero_total` decimal(10,2) DEFAULT '0.00',
  `estado` varchar(50) DEFAULT 'ABIERTA',
  PRIMARY KEY (`id_caja`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `caja_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=21 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `caja`
--

LOCK TABLES `caja` WRITE;
/*!40000 ALTER TABLE `caja` DISABLE KEYS */;
INSERT INTO `caja` VALUES (1,31,'2025-05-25 06:33:18','2025-05-25 06:36:01',100000.00,NULL,'CERRADA'),(2,31,'2025-05-25 06:45:38','2025-05-25 06:45:49',100000.00,NULL,'CERRADA'),(3,31,'2025-05-25 06:51:15','2025-05-25 06:51:24',100000.00,1500000.00,'CERRADA'),(4,31,'2025-05-25 06:51:51','2025-05-25 06:52:07',1500000.00,1600000.00,'CERRADA'),(5,31,'2025-05-26 08:10:12','2025-05-27 04:54:08',1000000.00,1140750.00,'CERRADA'),(6,31,'2025-05-27 04:57:17','2025-05-27 10:04:55',1000000.00,1000000.00,'CERRADA'),(7,31,'2025-05-27 10:19:48','2025-05-27 10:19:57',1000000.00,1140750.00,'CERRADA'),(8,31,'2025-05-27 10:23:35','2025-05-27 10:24:17',10000000.00,1140750.00,'CERRADA'),(9,31,'2025-05-27 10:28:44','2025-05-27 10:28:56',10000000.00,1140750.00,'CERRADA'),(10,31,'2025-05-27 10:30:17','2025-05-27 10:31:02',1140750.00,1140750.00,'CERRADA'),(11,27,'2025-05-27 10:33:55','2025-05-27 10:35:34',100.00,10000.00,'CERRADA'),(12,27,'2025-05-27 10:37:35','2025-05-27 10:37:45',10000.00,120000.00,'CERRADA'),(13,27,'2025-05-27 10:38:04','2025-05-27 10:38:26',130000.00,140000.00,'CERRADA'),(14,27,'2025-05-27 11:36:06','2025-05-27 11:36:16',140000.00,150000.00,'CERRADA'),(15,27,'2025-05-27 11:39:15','2025-05-27 21:26:54',150000.00,160000.00,'CERRADA'),(16,27,'2025-05-27 21:27:00','2025-05-27 21:29:10',160000.00,160000.00,'CERRADA'),(17,31,'2025-05-27 21:29:25','2025-05-27 21:31:27',1140750.00,1147750.00,'CERRADA'),(18,27,'2025-05-27 23:13:22','2025-05-27 23:13:32',160000.00,160000.00,'CERRADA'),(19,27,'2025-05-27 23:59:30','2025-05-28 04:50:27',160000.00,170000.00,'CERRADA'),(20,27,'2025-05-28 04:50:39','2025-05-28 05:47:13',170000.00,170176.00,'CERRADA');
/*!40000 ALTER TABLE `caja` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categorias`
--

DROP TABLE IF EXISTS `categorias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categorias` (
  `id_categoria` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `duracion_garantia` int NOT NULL,
  `descripcion` text COLLATE utf8mb4_general_ci,
  `estado` tinyint(1) NOT NULL DEFAULT '1',
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
  `id_cliente` int NOT NULL AUTO_INCREMENT,
  `documento_identidad` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `celular` varchar(15) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `correo` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `direccion` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `requiere_factura_default` tinyint(1) DEFAULT '0',
  `razon_social` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `identificacion_fiscal` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `direccion_facturacion` text COLLATE utf8mb4_general_ci,
  `correo_facturacion` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `tipo_factura_default` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `total_comprado` decimal(12,2) DEFAULT '0.00',
  `puntos_fidelidad` int DEFAULT '0',
  `ultima_compra` timestamp NULL DEFAULT NULL,
  `limite_credito` decimal(10,2) DEFAULT '0.00',
  `fecha_registro` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `activo` tinyint(1) DEFAULT '1',
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `uq_cliente_correo` (`correo`)
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (2,'09293131','Ana López de García','6009876557','ana.lopez.g@example.com','new direccion',0,'Nueva Razón Social S.A.','NRS010101XYZ','Av. Fiscal 123, Colonia Centro','facturacion.nueva@example.com','G03',98681.00,50,'2025-05-28 05:40:13',750.00,'2025-05-09 01:25:10','2025-05-28 05:40:13',1),(3,NULL,'Venta General',NULL,NULL,NULL,0,NULL,NULL,NULL,NULL,NULL,491299.00,0,'2025-05-28 05:45:19',0.00,'2025-05-09 02:46:49','2025-05-28 05:45:19',1),(7,'1144222','Andre Botina','313444','testAndres@gmail.com','n/a',1,'Botina S.A','093993181','DIRECCCION FACTO','testfactos@gmail.com','Electro',98803.00,0,'2025-05-28 05:45:46',10000.00,'2025-05-15 08:01:30','2025-05-28 05:45:46',1),(8,'123','Juan Suarez','31313','test2@gmail.com','na',1,'Juan Suarez','TEST1','test facto','ctest@gmail.com','E',13.00,0,'2025-05-26 03:42:56',40000.00,'2025-05-16 10:52:24','2025-05-26 03:42:56',1),(9,'1233333','clienteT','13333333','tclient@gmail.com','n/a',0,'','','','','',0.00,0,NULL,0.00,'2025-05-17 20:41:03','2025-05-17 20:41:03',1),(10,'31442222','Cliente2','313422222','isclient2@gmail.com','n/a',0,NULL,NULL,NULL,NULL,NULL,0.00,0,NULL,0.00,'2025-05-17 21:06:36','2025-05-17 21:06:44',1),(11,'00001001','Cliente Test','3122222','test0001@gmail.com','n/a',0,NULL,NULL,NULL,NULL,NULL,3000.00,0,'2025-05-17 21:07:45',0.00,'2025-05-17 21:07:43','2025-05-17 21:07:45',1),(12,'33333','test mod','3111111','test32@gmail.com','00-000-000',0,'','','','','',0.00,0,NULL,0.00,'2025-05-17 21:08:41','2025-05-17 21:08:41',1),(13,'332221111','Cliene test2','31233321','test211@gmail.com','n/a',1,NULL,NULL,NULL,NULL,NULL,10000.00,0,'2025-05-18 00:17:21',0.00,'2025-05-17 22:05:01','2025-05-18 00:17:21',1),(14,'44412221','Cliente E','3123882','ecliente@gmail.com','n/a',0,'','','','','',3000.00,0,'2025-05-17 22:17:50',0.00,'2025-05-17 22:17:33','2025-05-17 22:17:50',1),(15,'124444','Cliente X','55512221','xlcliente@gmail.com','n/a',0,'','','','','',46344.00,0,'2025-05-26 03:30:21',0.00,'2025-05-17 22:20:48','2025-05-26 03:30:21',1),(16,'039222','Cliente M','39131111','mclientex@gmail.com','n/a',0,'','','','','',13.00,0,'2025-05-17 22:23:45',0.00,'2025-05-17 22:23:35','2025-05-17 22:23:45',1),(17,'1333','TEST','3313','tes1111t@.gmail.com','n/a',0,'','','','','',49000.00,0,'2025-05-18 02:51:27',0.00,'2025-05-18 02:51:10','2025-05-18 02:51:27',1),(18,'939898911','Daniel Macias','310032212','danielmc@gmail.com','n/a',1,'Daniel Macias','939898911','','','',13.00,0,'2025-05-26 03:44:33',0.00,'2025-05-26 03:44:16','2025-05-26 03:44:33',1);
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comisiones`
--

DROP TABLE IF EXISTS `comisiones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comisiones` (
  `id_comision` int NOT NULL AUTO_INCREMENT,
  `id_vendedor` int DEFAULT NULL,
  `id_venta` int DEFAULT NULL,
  `base_comisionable` decimal(10,2) NOT NULL,
  `porcentaje_comision` decimal(5,2) NOT NULL,
  `monto_comision` decimal(10,2) NOT NULL,
  `fecha_calculo` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  PRIMARY KEY (`id_comision`),
  KEY `id_vendedor` (`id_vendedor`),
  KEY `id_venta` (`id_venta`),
  CONSTRAINT `comisiones_ibfk_1` FOREIGN KEY (`id_vendedor`) REFERENCES `vendedores` (`id_vendedor`),
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
  `id_configuracion` int NOT NULL AUTO_INCREMENT,
  `razon_social_emisor` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `nombre_comercial_emisor` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `identificacion_fiscal_emisor` varchar(20) COLLATE utf8mb4_general_ci NOT NULL,
  `domicilio_calle` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `domicilio_numero_exterior` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `domicilio_numero_interior` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `domicilio_barrio_colonia` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `domicilio_localidad` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `domicilio_municipio` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `domicilio_estado_provincia` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `domicilio_pais` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `domicilio_codigo_postal` varchar(10) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `regimen_fiscal_emisor` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `telefono_contacto` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email_contacto` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `pagina_web` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email_facturacion` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `logo_url` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_configuracion`),
  UNIQUE KEY `identificacion_fiscal_emisor` (`identificacion_fiscal_emisor`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `configuracion_empresa`
--

LOCK TABLES `configuracion_empresa` WRITE;
/*!40000 ALTER TABLE `configuracion_empresa` DISABLE KEYS */;
INSERT INTO `configuracion_empresa` VALUES (1,'Sistema de ventas D2 S.A','Sistema de ventas D2','901.234.567-8','Calle Ficticia','123','2B','Barrio Test','X','Mocoa','Dpt. Putumayo','Colombia','12345','Régimen Ordinario','5551234567','contacto@ejemplo.com','https://www.ejemplo.com','facturacion@ejemplo.com','https://www.ejemplo.com/logo.png','2025-05-08 22:20:15','2025-05-25 06:17:56');
/*!40000 ALTER TABLE `configuracion_empresa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `configuraciones`
--

DROP TABLE IF EXISTS `configuraciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `configuraciones` (
  `id_config` int NOT NULL AUTO_INCREMENT,
  `clave` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `valor` text COLLATE utf8mb4_general_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_general_ci,
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
  `id_credito` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int DEFAULT NULL,
  `id_venta` int DEFAULT NULL,
  `monto` decimal(10,2) NOT NULL,
  `saldo_pendiente` decimal(10,2) NOT NULL,
  `fecha_inicio` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_vencimiento` timestamp NULL DEFAULT NULL,
  `estado` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
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
  `id_detalle` int NOT NULL AUTO_INCREMENT,
  `id_factura` int DEFAULT NULL,
  `id_tipo_impuesto` int DEFAULT NULL,
  `base_imponible` decimal(10,2) NOT NULL,
  `tasa_aplicada` decimal(5,2) NOT NULL,
  `monto_impuesto` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  KEY `id_factura` (`id_factura`),
  KEY `id_tipo_impuesto` (`id_tipo_impuesto`),
  CONSTRAINT `detalle_impuestos_factura_ibfk_1` FOREIGN KEY (`id_factura`) REFERENCES `facturas` (`id_factura`),
  CONSTRAINT `detalle_impuestos_factura_ibfk_2` FOREIGN KEY (`id_tipo_impuesto`) REFERENCES `tipos_impuestos` (`id_tipo_impuesto`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_impuestos_factura`
--

LOCK TABLES `detalle_impuestos_factura` WRITE;
/*!40000 ALTER TABLE `detalle_impuestos_factura` DISABLE KEYS */;
INSERT INTO `detalle_impuestos_factura` VALUES (1,23,1,8000.00,16.00,1280.00),(2,25,1,8000.00,16.00,1280.00),(3,26,1,8000.00,16.00,1280.00),(5,28,1,12000.00,16.00,1920.00),(6,29,1,4000.00,16.00,640.00);
/*!40000 ALTER TABLE `detalle_impuestos_factura` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_venta`
--

DROP TABLE IF EXISTS `detalle_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_venta` (
  `id_detalle_venta` int NOT NULL AUTO_INCREMENT,
  `id_venta` int NOT NULL,
  `id_producto` int NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario_original` decimal(10,2) DEFAULT NULL,
  `id_promocion_aplicada` int DEFAULT NULL,
  `precio_unitario_final` decimal(10,2) NOT NULL,
  `subtotal` decimal(12,2) NOT NULL,
  `costo_unitario_producto` decimal(10,2) DEFAULT '0.00',
  `ganancia_detalle` decimal(12,2) DEFAULT '0.00',
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_detalle_venta`),
  KEY `id_venta` (`id_venta`),
  KEY `id_producto` (`id_producto`),
  KEY `id_promocion_aplicada` (`id_promocion_aplicada`),
  CONSTRAINT `detalle_venta_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`),
  CONSTRAINT `detalle_venta_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `detalle_venta_ibfk_4` FOREIGN KEY (`id_promocion_aplicada`) REFERENCES `promociones` (`id_promocion`)
) ENGINE=InnoDB AUTO_INCREMENT=142 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_venta`
--

LOCK TABLES `detalle_venta` WRITE;
/*!40000 ALTER TABLE `detalle_venta` DISABLE KEYS */;
INSERT INTO `detalle_venta` VALUES (1,3,3,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-08 21:54:57'),(2,3,4,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-08 21:54:57'),(3,4,3,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-08 22:10:18'),(4,4,7,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-08 22:10:18'),(13,9,3,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-08 22:39:34'),(14,9,7,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-08 22:39:34'),(15,10,3,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-08 22:56:10'),(16,10,7,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-08 22:56:10'),(17,11,3,6,13.00,NULL,13.00,78.00,12.00,6.00,'2025-05-09 21:30:37'),(18,12,7,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-13 16:00:07'),(19,12,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-13 16:00:07'),(22,14,7,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-13 16:16:20'),(23,14,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-13 16:16:20'),(24,15,3,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-16 21:13:34'),(25,15,4,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-16 21:13:34'),(26,16,16,1,3000.00,NULL,3000.00,3000.00,2000.00,1000.00,'2025-05-16 21:14:44'),(27,16,7,1,4000.00,NULL,4000.00,4000.00,3000.00,1000.00,'2025-05-16 21:14:44'),(28,17,3,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-16 21:17:29'),(29,17,16,2,3000.00,NULL,3000.00,6000.00,2000.00,2000.00,'2025-05-16 21:17:29'),(30,17,7,2,4000.00,NULL,4000.00,8000.00,3000.00,2000.00,'2025-05-16 21:17:29'),(31,18,3,3,13.00,NULL,13.00,39.00,12.00,3.00,'2025-05-16 21:20:49'),(32,19,3,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-16 21:42:33'),(33,20,3,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-16 22:39:59'),(34,21,7,1,4000.00,NULL,4000.00,4000.00,3000.00,1000.00,'2025-05-17 03:24:05'),(35,22,16,1,3000.00,NULL,3000.00,3000.00,2000.00,1000.00,'2025-05-17 03:25:12'),(36,23,3,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-17 04:07:05'),(37,24,7,1,4000.00,NULL,4000.00,4000.00,3000.00,1000.00,'2025-05-17 04:07:54'),(38,25,3,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-17 04:47:23'),(39,26,3,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-17 04:49:23'),(40,27,3,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-17 05:09:45'),(41,28,7,1,4000.00,NULL,4000.00,4000.00,3000.00,1000.00,'2025-05-17 05:10:26'),(42,29,3,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-17 15:39:46'),(43,30,16,1,3000.00,NULL,3000.00,3000.00,2000.00,1000.00,'2025-05-17 16:07:44'),(44,31,16,1,3000.00,NULL,3000.00,3000.00,2000.00,1000.00,'2025-05-17 16:43:31'),(45,32,16,1,3000.00,NULL,3000.00,3000.00,2000.00,1000.00,'2025-05-17 16:45:23'),(46,33,16,1,3000.00,NULL,3000.00,3000.00,2000.00,1000.00,'2025-05-17 17:05:04'),(47,34,16,1,3000.00,NULL,3000.00,3000.00,2000.00,1000.00,'2025-05-17 17:17:50'),(48,35,16,1,3000.00,NULL,3000.00,3000.00,2000.00,1000.00,'2025-05-17 17:20:47'),(49,36,13,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-17 17:23:45'),(50,37,13,1,7000.00,NULL,7000.00,7000.00,5000.00,2000.00,'2025-05-17 19:17:21'),(51,38,3,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-17 21:15:53'),(52,39,13,7,7000.00,NULL,7000.00,49000.00,5000.00,14000.00,'2025-05-17 21:51:26'),(53,40,13,1,7000.00,NULL,7000.00,7000.00,5000.00,2000.00,'2025-05-18 05:47:25'),(54,41,7,2,4000.00,NULL,4000.00,8000.00,3000.00,2000.00,'2025-05-18 22:36:50'),(55,41,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-18 22:36:50'),(56,42,3,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-18 23:18:23'),(57,42,4,5,13.00,NULL,13.00,65.00,12.00,5.00,'2025-05-18 23:18:23'),(58,42,8,6,124.00,NULL,124.00,744.00,123.00,6.00,'2025-05-18 23:18:23'),(59,42,10,6,13.00,NULL,13.00,78.00,12.00,6.00,'2025-05-18 23:18:23'),(60,42,14,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-18 23:18:23'),(61,42,13,7,7000.00,NULL,7000.00,49000.00,5000.00,14000.00,'2025-05-18 23:18:23'),(62,43,7,10,4000.00,NULL,4000.00,40000.00,3000.00,10000.00,'2025-05-19 02:14:36'),(63,44,7,2,4000.00,NULL,4000.00,8000.00,3000.00,2000.00,'2025-05-23 22:54:44'),(64,44,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-23 22:54:44'),(65,45,7,2,4000.00,NULL,4000.00,8000.00,3000.00,2000.00,'2025-05-25 03:22:00'),(66,45,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-25 03:22:00'),(70,48,13,8,7000.00,NULL,7000.00,56000.00,5000.00,16000.00,'2025-05-25 03:35:44'),(71,49,4,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-25 22:30:20'),(72,49,8,3,124.00,NULL,124.00,372.00,123.00,3.00,'2025-05-25 22:30:20'),(73,49,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-25 22:30:20'),(74,49,7,3,4000.00,NULL,4000.00,12000.00,3000.00,3000.00,'2025-05-25 22:30:20'),(75,49,16,5,3000.00,NULL,3000.00,15000.00,2000.00,5000.00,'2025-05-25 22:30:20'),(76,49,13,2,7000.00,NULL,7000.00,14000.00,5000.00,4000.00,'2025-05-25 22:30:20'),(77,50,4,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-25 22:37:34'),(78,50,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-25 22:37:34'),(79,50,10,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-25 22:37:34'),(80,50,7,1,4000.00,NULL,4000.00,4000.00,3000.00,1000.00,'2025-05-25 22:37:34'),(81,50,13,1,7000.00,NULL,7000.00,7000.00,5000.00,2000.00,'2025-05-25 22:37:34'),(82,50,16,1,3000.00,NULL,3000.00,3000.00,2000.00,1000.00,'2025-05-25 22:37:34'),(83,51,4,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-25 22:42:56'),(84,52,10,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-25 22:44:33'),(87,54,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-26 01:37:13'),(88,54,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-26 01:37:13'),(89,55,13,1,7000.00,NULL,7000.00,7000.00,5000.00,2000.00,'2025-05-26 01:46:27'),(90,56,4,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-26 01:48:24'),(93,58,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-26 03:10:19'),(94,58,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-26 03:10:19'),(95,59,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-26 03:10:22'),(96,59,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-26 03:10:22'),(97,60,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-26 03:10:23'),(98,60,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-26 03:10:23'),(99,61,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-26 03:10:24'),(100,61,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-26 03:10:24'),(101,62,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-26 03:10:25'),(102,62,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-26 03:10:25'),(103,63,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-26 03:12:22'),(104,63,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-26 03:12:22'),(105,64,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-26 03:12:23'),(106,64,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-26 03:12:23'),(107,65,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-26 03:12:24'),(108,65,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-26 03:12:24'),(109,66,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-26 03:12:25'),(110,66,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-26 03:12:25'),(111,67,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-26 03:12:26'),(112,67,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-26 03:12:26'),(113,68,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-26 03:12:33'),(114,68,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-26 03:12:33'),(115,69,13,10,7000.00,NULL,7000.00,70000.00,5000.00,20000.00,'2025-05-26 22:32:14'),(116,70,13,1,7000.00,NULL,7000.00,7000.00,5000.00,2000.00,'2025-05-26 22:36:29'),(117,71,13,10,7000.00,NULL,7000.00,70000.00,5000.00,20000.00,'2025-05-26 22:37:25'),(118,72,13,10,7000.00,NULL,7000.00,70000.00,5000.00,20000.00,'2025-05-26 22:45:13'),(119,73,13,6,7000.00,NULL,7000.00,42000.00,5000.00,12000.00,'2025-05-26 22:45:30'),(120,74,13,1,7000.00,NULL,7000.00,7000.00,5000.00,2000.00,'2025-05-26 22:58:09'),(121,75,13,10,7000.00,NULL,7000.00,70000.00,5000.00,20000.00,'2025-05-26 22:58:31'),(122,76,13,10,7000.00,NULL,7000.00,70000.00,5000.00,20000.00,'2025-05-26 22:59:05'),(124,78,13,1,7000.00,NULL,7000.00,7000.00,5000.00,2000.00,'2025-05-27 06:41:33'),(125,79,13,1,7000.00,NULL,7000.00,7000.00,5000.00,2000.00,'2025-05-27 16:27:35'),(127,81,13,1,7000.00,NULL,7000.00,7000.00,5000.00,2000.00,'2025-05-27 16:30:36'),(129,83,13,1,7000.00,NULL,7000.00,7000.00,5000.00,2000.00,'2025-05-27 18:59:51'),(135,89,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-28 00:39:42'),(136,89,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-28 00:39:42'),(137,90,10,2,13.00,NULL,13.00,26.00,12.00,2.00,'2025-05-28 00:40:13'),(138,90,8,1,124.00,NULL,124.00,124.00,123.00,1.00,'2025-05-28 00:40:13'),(139,91,4,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-28 00:44:59'),(140,92,4,1,13.00,NULL,13.00,13.00,12.00,1.00,'2025-05-28 00:45:18'),(141,93,13,1,7000.00,NULL,7000.00,7000.00,5000.00,2000.00,'2025-05-28 00:45:45');
/*!40000 ALTER TABLE `detalle_venta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_venta_lote_uso`
--

DROP TABLE IF EXISTS `detalle_venta_lote_uso`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_venta_lote_uso` (
  `id_detalle_venta_lote_uso` bigint NOT NULL AUTO_INCREMENT,
  `id_detalle_venta` int NOT NULL,
  `id_lote` int NOT NULL,
  `cantidad_tomada` int NOT NULL,
  PRIMARY KEY (`id_detalle_venta_lote_uso`),
  KEY `id_detalle_venta` (`id_detalle_venta`),
  KEY `id_lote` (`id_lote`),
  CONSTRAINT `detalle_venta_lote_uso_ibfk_1` FOREIGN KEY (`id_detalle_venta`) REFERENCES `detalle_venta` (`id_detalle_venta`),
  CONSTRAINT `detalle_venta_lote_uso_ibfk_2` FOREIGN KEY (`id_lote`) REFERENCES `lotes` (`id_lote`)
) ENGINE=InnoDB AUTO_INCREMENT=142 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_venta_lote_uso`
--

LOCK TABLES `detalle_venta_lote_uso` WRITE;
/*!40000 ALTER TABLE `detalle_venta_lote_uso` DISABLE KEYS */;
INSERT INTO `detalle_venta_lote_uso` VALUES (1,1,2,2),(2,2,1,1),(3,3,2,2),(4,4,3,1),(13,13,2,2),(14,14,7,1),(15,15,2,2),(16,16,7,1),(17,17,2,6),(18,18,7,2),(19,19,4,1),(22,22,7,2),(23,23,5,1),(24,24,20,1),(25,25,19,1),(26,26,18,1),(27,27,7,1),(28,28,20,1),(29,29,18,2),(30,30,7,2),(31,31,20,3),(32,32,20,1),(33,33,20,1),(34,34,7,1),(35,35,18,1),(36,36,20,1),(37,37,7,1),(38,38,20,1),(39,39,20,1),(40,40,20,1),(41,41,7,1),(42,42,20,1),(43,43,18,1),(44,44,18,1),(45,45,18,1),(46,46,18,1),(47,47,18,1),(48,48,18,1),(49,49,12,1),(50,50,12,1),(51,51,20,1),(52,52,12,7),(53,53,12,1),(54,54,21,2),(55,55,5,1),(56,56,20,2),(57,57,19,5),(58,58,5,6),(59,59,9,6),(60,60,17,1),(61,61,12,7),(62,62,21,10),(63,63,21,2),(64,64,5,1),(65,65,21,2),(66,66,5,1),(70,70,12,8),(71,71,19,2),(72,72,6,3),(73,73,9,2),(74,74,21,3),(75,75,18,5),(76,76,12,2),(77,77,19,1),(78,78,6,1),(79,79,9,1),(80,80,21,1),(81,81,12,1),(82,82,18,1),(83,83,19,1),(84,84,9,1),(87,87,9,2),(88,88,6,1),(89,89,12,1),(90,90,19,1),(93,93,9,2),(94,94,6,1),(95,95,9,2),(96,96,6,1),(97,97,9,2),(98,98,6,1),(99,99,9,2),(100,100,6,1),(101,101,9,2),(102,102,6,1),(103,103,9,2),(104,104,6,1),(105,105,9,2),(106,106,6,1),(107,107,9,2),(108,108,6,1),(109,109,9,2),(110,110,6,1),(111,111,9,2),(112,112,6,1),(113,113,9,2),(114,114,6,1),(115,115,12,10),(116,116,12,1),(117,117,12,10),(118,118,12,10),(119,119,12,6),(120,120,12,1),(121,121,12,10),(122,122,12,10),(124,124,12,1),(125,125,12,1),(127,127,12,1),(129,129,12,1),(135,135,9,2),(136,136,6,1),(137,137,9,2),(138,138,6,1),(139,139,19,1),(140,140,19,1),(141,141,12,1);
/*!40000 ALTER TABLE `detalle_venta_lote_uso` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `devoluciones`
--

DROP TABLE IF EXISTS `devoluciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `devoluciones` (
  `id_devolucion` int NOT NULL AUTO_INCREMENT,
  `id_venta` int DEFAULT NULL,
  `id_producto` int DEFAULT NULL,
  `cantidad` int NOT NULL,
  `motivo` text COLLATE utf8mb4_general_ci,
  `fecha_devolucion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `estado` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `id_detalle` int DEFAULT NULL,
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
  `id_entrada` int NOT NULL AUTO_INCREMENT,
  `id_producto` int NOT NULL,
  `id_proveedor` int DEFAULT NULL,
  `cantidad` int NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `tipo_movimiento` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `motivo` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  PRIMARY KEY (`id_entrada`),
  KEY `id_producto` (`id_producto`),
  KEY `id_proveedor` (`id_proveedor`),
  CONSTRAINT `entradas_productos_ibfk_1` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `entradas_productos_ibfk_2` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedores` (`id_proveedor`)
) ENGINE=InnoDB AUTO_INCREMENT=217 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `entradas_productos`
--

LOCK TABLES `entradas_productos` WRITE;
/*!40000 ALTER TABLE `entradas_productos` DISABLE KEYS */;
INSERT INTO `entradas_productos` VALUES (1,4,1,12,'2025-04-20 00:47:32','ENTRADA',12.00,'Creación de lote: LOT-test2-20254'),(2,3,2,100,'2025-04-20 00:49:10','ENTRADA',12.00,'Creación de lote: LOT-test1-20254'),(3,7,2,11,'2025-04-26 03:10:48','ENTRADA',12.00,'Creación de lote: LOT-test3-20254'),(4,8,1,1001,'2025-05-01 09:47:37','ENTRADA',123.00,'Creación de lote: LOT-test4-20254'),(5,8,1,10,'2025-05-01 10:12:47','ENTRADA',123.00,'Creación de lote: LOT-test4-20255'),(6,8,1,100,'2025-05-01 10:23:08','ENTRADA',123.00,'Creación de lote: LOT-test4-20255'),(7,7,2,12,'2025-05-01 10:23:56','ENTRADA',12.00,'Creación de lote: LOT-test3-20255'),(8,10,2,11,'2025-05-02 21:29:05','ENTRADA',12.00,'Creación de lote: LOT-test5-20255'),(9,10,2,100,'2025-05-03 02:21:40','ENTRADA',12.00,'Creación de lote: LOT-test5-20255'),(10,10,2,100,'2025-05-03 02:21:59','ENTRADA',12.00,'Creación de lote: LOT-test5-20255'),(11,4,1,1,'2025-05-03 02:22:20','ENTRADA',12.00,'Creación de lote: LOT-test2-20255'),(12,13,1,2222,'2025-05-03 02:50:48','ENTRADA',12.00,'Creación de lote: LOT-test6-20255'),(13,13,1,12,'2025-05-03 02:56:02','ENTRADA',12.00,'Creación de lote: test'),(14,13,1,200,'2025-05-03 03:16:22','ENTRADA',12.00,'Creación de lote: LOT006'),(15,13,1,200,'2025-05-03 03:16:42','ENTRADA',12.00,'Creación de lote: LOT006'),(16,13,1,1,'2025-05-03 23:45:29','ENTRADA',12.00,'Creación de lote: LOT-test6-20255'),(31,3,NULL,2,'2025-05-05 02:28:04','SALIDA',13.00,'Venta VTA-00123'),(32,4,NULL,1,'2025-05-05 02:28:04','SALIDA',13.00,'Venta VTA-00123'),(45,3,NULL,2,'2025-05-05 02:48:55','SALIDA',13.00,'Venta VTA-00124'),(46,4,NULL,1,'2025-05-05 02:48:55','SALIDA',13.00,'Venta VTA-00124'),(47,3,NULL,2,'2025-05-05 04:06:44','SALIDA',13.00,'Venta VTA-00126'),(48,4,NULL,1,'2025-05-05 04:06:44','SALIDA',13.00,'Venta VTA-00126'),(49,3,NULL,2,'2025-05-05 04:13:54','SALIDA',13.00,'Venta VTA-00127'),(50,4,NULL,1,'2025-05-05 04:13:54','SALIDA',13.00,'Venta VTA-00127'),(51,3,2,2,'2025-05-05 04:24:44','SALIDA',13.00,'Venta VTA-00128'),(52,4,1,1,'2025-05-05 04:24:44','SALIDA',13.00,'Venta VTA-00128'),(53,3,2,2,'2025-05-05 04:54:03','SALIDA',13.00,'Venta VTA-00129'),(54,4,1,1,'2025-05-05 04:54:03','SALIDA',13.00,'Venta VTA-00129'),(55,14,1,1,'2025-05-05 04:59:47','ENTRADA',12.00,'Creación de lote: LOT-test7-20255'),(56,3,2,6,'2025-05-08 02:05:11','SALIDA',13.00,'Venta VTA-1746651911650'),(57,3,2,4,'2025-05-08 02:07:55','SALIDA',13.00,'Venta VTA-1746652075626'),(58,3,2,3,'2025-05-08 02:28:09','SALIDA',13.00,'Venta VTA-1746653289745'),(59,3,2,1,'2025-05-08 03:17:54','SALIDA',13.00,'Venta VTA-1746656274740'),(60,3,2,8,'2025-05-08 03:55:06','SALIDA',13.00,'Venta VTA-1746658506634'),(61,3,2,1,'2025-05-08 04:41:54','SALIDA',13.00,'Venta VTA-1746661314011'),(62,16,2,25,'2025-05-08 04:46:06','ENTRADA',2000.00,'Creación de lote: LOT-PROD-001-20255'),(65,3,2,2,'2025-05-08 07:02:38','SALIDA',13.00,'Venta VTA-00129'),(66,4,1,1,'2025-05-08 07:02:38','SALIDA',13.00,'Venta VTA-00129'),(71,3,2,2,'2025-05-09 02:54:57','SALIDA',13.00,'Venta [Pendiente]'),(72,4,1,1,'2025-05-09 02:54:57','SALIDA',13.00,'Venta [Pendiente]'),(73,3,2,2,'2025-05-09 02:58:35','SALIDA',13.00,'Venta [Pendiente]'),(74,3,2,2,'2025-05-09 02:59:52','SALIDA',13.00,'Venta [Pendiente]'),(75,3,2,2,'2025-05-09 03:10:18','SALIDA',13.00,'Venta [Pendiente]'),(76,7,2,1,'2025-05-09 03:10:18','SALIDA',13.00,'Venta [Pendiente]'),(85,3,2,2,'2025-05-09 03:39:34','SALIDA',13.00,'Venta [Pendiente]'),(86,7,2,1,'2025-05-09 03:39:34','SALIDA',13.00,'Venta [Pendiente]'),(87,3,2,2,'2025-05-09 03:56:10','SALIDA',13.00,'Venta [Pendiente]'),(88,7,2,1,'2025-05-09 03:56:10','SALIDA',13.00,'Venta [Pendiente]'),(89,4,1,22,'2025-05-09 21:45:06','ENTRADA',12.00,'Creación de lote: LOT-test2-20255'),(90,3,2,6,'2025-05-10 02:30:37','SALIDA',13.00,'Venta VTA-1746826236812'),(91,7,2,2,'2025-05-13 21:00:07','SALIDA',13.00,'Venta [Pendiente]'),(92,8,1,1,'2025-05-13 21:00:08','SALIDA',124.00,'Venta [Pendiente]'),(95,7,2,2,'2025-05-13 21:16:20','SALIDA',13.00,'Venta [Pendiente]'),(96,8,1,1,'2025-05-13 21:16:20','SALIDA',124.00,'Venta [Pendiente]'),(97,3,2,15,'2025-05-16 10:53:19','ENTRADA',12.00,'Creación de lote: LOT-test1-20255'),(98,3,2,1,'2025-05-17 02:13:34','SALIDA',13.00,'Venta [Pendiente]'),(99,4,1,1,'2025-05-17 02:13:34','SALIDA',13.00,'Venta [Pendiente]'),(100,16,2,1,'2025-05-17 02:14:44','SALIDA',3000.00,'Venta [Pendiente]'),(101,7,NULL,1,'2025-05-17 02:14:44','SALIDA',4000.00,'Venta [Pendiente]'),(102,3,2,1,'2025-05-17 02:17:29','SALIDA',13.00,'Venta [Pendiente]'),(103,16,2,2,'2025-05-17 02:17:29','SALIDA',3000.00,'Venta [Pendiente]'),(104,7,NULL,2,'2025-05-17 02:17:29','SALIDA',4000.00,'Venta [Pendiente]'),(105,3,2,3,'2025-05-17 02:20:49','SALIDA',13.00,'Venta [Pendiente]'),(106,3,2,1,'2025-05-17 02:42:33','SALIDA',13.00,'Venta [Pendiente]'),(107,3,2,1,'2025-05-17 03:39:59','SALIDA',13.00,'Venta [Pendiente]'),(108,7,NULL,1,'2025-05-17 08:24:05','SALIDA',4000.00,'Venta [Pendiente]'),(109,16,2,1,'2025-05-17 08:25:12','SALIDA',3000.00,'Venta [Pendiente]'),(110,3,2,1,'2025-05-17 09:07:05','SALIDA',13.00,'Venta [Pendiente]'),(111,7,NULL,1,'2025-05-17 09:07:55','SALIDA',4000.00,'Venta [Pendiente]'),(112,3,2,1,'2025-05-17 09:47:23','SALIDA',13.00,'Venta [Pendiente]'),(113,3,2,1,'2025-05-17 09:49:24','SALIDA',13.00,'Venta [Pendiente]'),(114,3,2,1,'2025-05-17 10:09:45','SALIDA',13.00,'Venta [Pendiente]'),(115,7,NULL,1,'2025-05-17 10:10:26','SALIDA',4000.00,'Venta [Pendiente]'),(116,3,2,1,'2025-05-17 20:39:46','SALIDA',13.00,'Venta [Pendiente]'),(117,16,2,1,'2025-05-17 21:07:45','SALIDA',3000.00,'Venta [Pendiente]'),(118,16,2,1,'2025-05-17 21:43:31','SALIDA',3000.00,'Venta [Pendiente]'),(119,16,2,1,'2025-05-17 21:45:24','SALIDA',3000.00,'Venta [Pendiente]'),(120,16,2,1,'2025-05-17 22:05:05','SALIDA',3000.00,'Venta [Pendiente]'),(121,16,2,1,'2025-05-17 22:17:50','SALIDA',3000.00,'Venta [Pendiente]'),(122,16,2,1,'2025-05-17 22:20:48','SALIDA',3000.00,'Venta [Pendiente]'),(123,13,1,1,'2025-05-17 22:23:45','SALIDA',13.00,'Venta [Pendiente]'),(124,13,1,1,'2025-05-18 00:17:21','SALIDA',7000.00,'Venta [Pendiente]'),(125,3,2,1,'2025-05-18 02:15:53','SALIDA',13.00,'Venta [Pendiente]'),(126,13,1,7,'2025-05-18 02:51:27','SALIDA',7000.00,'Venta [Pendiente]'),(127,13,1,1,'2025-05-18 10:47:25','SALIDA',7000.00,'Venta [Pendiente]'),(128,7,2,20,'2025-05-19 03:36:46','ENTRADA',3000.00,'Creación de lote: LOT-PROD-002-20255'),(129,7,2,2,'2025-05-19 03:36:50','SALIDA',4000.00,'Venta [Pendiente]'),(130,8,1,1,'2025-05-19 03:36:50','SALIDA',124.00,'Venta [Pendiente]'),(131,3,2,2,'2025-05-19 04:18:23','SALIDA',13.00,'Venta [Pendiente]'),(132,4,1,5,'2025-05-19 04:18:23','SALIDA',13.00,'Venta [Pendiente]'),(133,8,1,6,'2025-05-19 04:18:23','SALIDA',124.00,'Venta [Pendiente]'),(134,10,2,6,'2025-05-19 04:18:23','SALIDA',13.00,'Venta [Pendiente]'),(135,14,1,1,'2025-05-19 04:18:23','SALIDA',13.00,'Venta [Pendiente]'),(136,13,1,7,'2025-05-19 04:18:23','SALIDA',7000.00,'Venta [Pendiente]'),(137,7,2,10,'2025-05-19 07:14:36','SALIDA',4000.00,'Venta [Pendiente]'),(138,7,2,2,'2025-05-24 03:54:44','SALIDA',4000.00,'Venta [Pendiente]'),(139,8,1,1,'2025-05-24 03:54:45','SALIDA',124.00,'Venta [Pendiente]'),(140,7,2,2,'2025-05-25 08:22:00','SALIDA',4000.00,'Venta VTA-1746826236813'),(141,8,1,1,'2025-05-25 08:22:00','SALIDA',124.00,'Venta VTA-1746826236813'),(145,13,1,8,'2025-05-25 08:35:44','SALIDA',7000.00,'Venta VTA-1746826236814'),(146,4,1,2,'2025-05-26 03:30:20','SALIDA',13.00,'Venta VTA-1746826236815'),(147,8,1,3,'2025-05-26 03:30:21','SALIDA',124.00,'Venta VTA-1746826236815'),(148,10,2,2,'2025-05-26 03:30:21','SALIDA',13.00,'Venta VTA-1746826236815'),(149,7,2,3,'2025-05-26 03:30:21','SALIDA',4000.00,'Venta VTA-1746826236815'),(150,16,2,5,'2025-05-26 03:30:21','SALIDA',3000.00,'Venta VTA-1746826236815'),(151,13,1,2,'2025-05-26 03:30:21','SALIDA',7000.00,'Venta VTA-1746826236815'),(152,4,1,1,'2025-05-26 03:37:34','SALIDA',13.00,'Venta VTA-1746826236816'),(153,8,1,1,'2025-05-26 03:37:34','SALIDA',124.00,'Venta VTA-1746826236816'),(154,10,2,1,'2025-05-26 03:37:34','SALIDA',13.00,'Venta VTA-1746826236816'),(155,7,2,1,'2025-05-26 03:37:34','SALIDA',4000.00,'Venta VTA-1746826236816'),(156,13,1,1,'2025-05-26 03:37:34','SALIDA',7000.00,'Venta VTA-1746826236816'),(157,16,2,1,'2025-05-26 03:37:34','SALIDA',3000.00,'Venta VTA-1746826236816'),(158,4,1,1,'2025-05-26 03:42:56','SALIDA',13.00,'Venta VTA-1746826236817'),(159,10,2,1,'2025-05-26 03:44:33','SALIDA',13.00,'Venta VTA-1746826236818'),(162,10,2,2,'2025-05-26 06:37:14','SALIDA',13.00,'Venta VTA-1746826236819'),(163,8,1,1,'2025-05-26 06:37:14','SALIDA',124.00,'Venta VTA-1746826236819'),(164,13,1,1,'2025-05-26 06:46:27','SALIDA',7000.00,'Venta VTA-1746826236820'),(165,4,1,1,'2025-05-26 06:48:25','SALIDA',13.00,'Venta VTA-1746826236821'),(168,10,2,2,'2025-05-26 08:10:19','SALIDA',13.00,'Venta VTA-1746826236822'),(169,8,1,1,'2025-05-26 08:10:19','SALIDA',124.00,'Venta VTA-1746826236822'),(170,10,2,2,'2025-05-26 08:10:22','SALIDA',13.00,'Venta VTA-1746826236823'),(171,8,1,1,'2025-05-26 08:10:22','SALIDA',124.00,'Venta VTA-1746826236823'),(172,10,2,2,'2025-05-26 08:10:23','SALIDA',13.00,'Venta VTA-1746826236824'),(173,8,1,1,'2025-05-26 08:10:23','SALIDA',124.00,'Venta VTA-1746826236824'),(174,10,2,2,'2025-05-26 08:10:25','SALIDA',13.00,'Venta VTA-1746826236825'),(175,8,1,1,'2025-05-26 08:10:25','SALIDA',124.00,'Venta VTA-1746826236825'),(176,10,2,2,'2025-05-26 08:10:26','SALIDA',13.00,'Venta VTA-1746826236826'),(177,8,1,1,'2025-05-26 08:10:26','SALIDA',124.00,'Venta VTA-1746826236826'),(178,10,2,2,'2025-05-26 08:12:22','SALIDA',13.00,'Venta VTA-1746826236827'),(179,8,1,1,'2025-05-26 08:12:22','SALIDA',124.00,'Venta VTA-1746826236827'),(180,10,2,2,'2025-05-26 08:12:23','SALIDA',13.00,'Venta VTA-1746826236828'),(181,8,1,1,'2025-05-26 08:12:23','SALIDA',124.00,'Venta VTA-1746826236828'),(182,10,2,2,'2025-05-26 08:12:24','SALIDA',13.00,'Venta VTA-1746826236829'),(183,8,1,1,'2025-05-26 08:12:24','SALIDA',124.00,'Venta VTA-1746826236829'),(184,10,2,2,'2025-05-26 08:12:25','SALIDA',13.00,'Venta VTA-1746826236830'),(185,8,1,1,'2025-05-26 08:12:25','SALIDA',124.00,'Venta VTA-1746826236830'),(186,10,2,2,'2025-05-26 08:12:26','SALIDA',13.00,'Venta VTA-1746826236831'),(187,8,1,1,'2025-05-26 08:12:26','SALIDA',124.00,'Venta VTA-1746826236831'),(188,10,2,2,'2025-05-26 08:12:34','SALIDA',13.00,'Venta VTA-1746826236832'),(189,8,1,1,'2025-05-26 08:12:34','SALIDA',124.00,'Venta VTA-1746826236832'),(190,13,1,10,'2025-05-27 03:32:14','SALIDA',7000.00,'Venta VTA-1746826236833'),(191,13,1,1,'2025-05-27 03:36:30','SALIDA',7000.00,'Venta VTA-1746826236834'),(192,13,1,10,'2025-05-27 03:37:26','SALIDA',7000.00,'Venta VTA-1746826236835'),(193,13,1,10,'2025-05-27 03:45:13','SALIDA',7000.00,'Venta VTA-1746826236836'),(194,13,1,6,'2025-05-27 03:45:30','SALIDA',7000.00,'Venta VTA-1746826236837'),(195,13,1,1,'2025-05-27 03:58:10','SALIDA',7000.00,'Venta VTA-1746826236838'),(196,13,1,10,'2025-05-27 03:58:32','SALIDA',7000.00,'Venta VTA-1746826236839'),(197,13,1,10,'2025-05-27 03:59:05','SALIDA',7000.00,'Venta VTA-1746826236840'),(199,13,1,1,'2025-05-27 11:41:34','SALIDA',7000.00,'Venta VTA-1746826236841'),(200,13,1,1,'2025-05-27 21:27:36','SALIDA',7000.00,'Venta VTA-1746826236842'),(202,13,1,1,'2025-05-27 21:30:36','SALIDA',7000.00,'Venta VTA-1746826236843'),(204,13,1,1,'2025-05-27 23:59:51','SALIDA',7000.00,'Venta VTA-1746826236844'),(210,10,2,2,'2025-05-28 05:39:42','SALIDA',13.00,'Venta VTA-1746826236845'),(211,8,1,1,'2025-05-28 05:39:42','SALIDA',124.00,'Venta VTA-1746826236845'),(212,10,2,2,'2025-05-28 05:40:13','SALIDA',13.00,'Venta VTA-1746826236846'),(213,8,1,1,'2025-05-28 05:40:13','SALIDA',124.00,'Venta VTA-1746826236846'),(214,4,1,1,'2025-05-28 05:44:59','SALIDA',13.00,'Venta VTA-1746826236847'),(215,4,1,1,'2025-05-28 05:45:19','SALIDA',13.00,'Venta VTA-1746826236848'),(216,13,1,1,'2025-05-28 05:45:46','SALIDA',7000.00,'Venta VTA-1746826236849');
/*!40000 ALTER TABLE `entradas_productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `facturas`
--

DROP TABLE IF EXISTS `facturas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `facturas` (
  `id_factura` int NOT NULL AUTO_INCREMENT,
  `id_venta` int DEFAULT NULL,
  `numero_factura` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `fecha_emision` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `subtotal` decimal(10,2) NOT NULL,
  `total_impuestos` decimal(10,2) NOT NULL,
  `total_con_impuestos` decimal(10,2) NOT NULL,
  `datos_fiscales` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_bin,
  `estado` varchar(50) COLLATE utf8mb4_general_ci NOT NULL DEFAULT 'EMITIDA',
  PRIMARY KEY (`id_factura`),
  UNIQUE KEY `numero_factura` (`numero_factura`),
  KEY `id_venta` (`id_venta`),
  CONSTRAINT `facturas_ibfk_1` FOREIGN KEY (`id_venta`) REFERENCES `ventas` (`id_venta`),
  CONSTRAINT `facturas_chk_1` CHECK (json_valid(`datos_fiscales`))
) ENGINE=InnoDB AUTO_INCREMENT=52 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `facturas`
--

LOCK TABLES `facturas` WRITE;
/*!40000 ALTER TABLE `facturas` DISABLE KEYS */;
INSERT INTO `facturas` VALUES (1,9,'F-9-0FAFAD3F','2025-05-09 03:39:34',39.00,0.00,39.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"rfc\":\"ABC123456789\",\"domicilio\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"rfc\":\"NRS010101XYZ\",\"domicilio\":\"Av. Fiscal 123, Colonia Centro\",\"usoFactura\":\"G03\"}}','EMITIDA'),(2,10,'F-10-FC72BC5C','2025-05-09 03:56:11',39.00,0.00,39.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"rfc\":\"ABC123456789\",\"domicilio\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"rfc\":\"NRS010101XYZ\",\"domicilio\":\"Av. Fiscal 123, Colonia Centro\",\"usoFactura\":\"G03\"}}','EMITIDA'),(3,12,'F-12-41354EFF','2025-05-13 21:00:08',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(5,14,'F-14-FA4D636A','2025-05-13 21:16:20',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(6,21,'F-21-7EBBF1FB','2025-05-17 08:24:06',4000.00,0.00,4000.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(7,22,'F-22-EB782AB5','2025-05-17 08:25:12',3000.00,0.00,3000.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(8,23,'F-23-1E900120','2025-05-17 09:07:05',13.00,0.00,13.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(9,24,'F-24-6126D100','2025-05-17 09:07:55',4000.00,0.00,4000.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(10,25,'F-25-05613568','2025-05-17 09:47:24',13.00,0.00,13.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(11,26,'F-26-D72030CF','2025-05-17 09:49:24',13.00,0.00,13.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(12,27,'F-27-E484F58D','2025-05-17 10:09:45',13.00,0.00,13.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(13,28,'F-28-C8A83BA6','2025-05-17 10:10:26',4000.00,0.00,4000.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(14,29,'F-29-0FDB0B1E','2025-05-17 20:39:47',13.00,0.00,13.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(15,30,'F-30-2DC5D4C9','2025-05-17 21:07:45',3000.00,0.00,3000.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"PÚBLICO EN GENERAL\",\"identificacionFiscal\":\"222222222222\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"tipoFactura\":\"S01\"}}','EMITIDA'),(16,32,'F-32-5AF1360D','2025-05-17 21:45:24',3000.00,0.00,3000.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(17,33,'F-33-5AC17089','2025-05-17 22:05:05',3000.00,0.00,3000.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"PÚBLICO EN GENERAL\",\"identificacionFiscal\":\"222222222222\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"tipoFactura\":\"S01\"}}','EMITIDA'),(18,34,'F-34-7663A56D','2025-05-17 22:17:50',3000.00,0.00,3000.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"PÚBLICO EN GENERAL\",\"identificacionFiscal\":\"222222222222\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"tipoFactura\":\"S01\"}}','EMITIDA'),(19,35,'F-35-EA76FC1B','2025-05-17 22:20:48',3000.00,0.00,3000.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"PÚBLICO EN GENERAL\",\"identificacionFiscal\":\"222222222222\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"tipoFactura\":\"S01\"}}','EMITIDA'),(20,36,'F-36-2EF41DB7','2025-05-17 22:23:45',13.00,0.00,13.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"PÚBLICO EN GENERAL\",\"identificacionFiscal\":\"222222222222\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"tipoFactura\":\"S01\"}}','EMITIDA'),(21,37,'F-37-9E2500D8','2025-05-18 00:17:21',7000.00,0.00,7000.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"PÚBLICO EN GENERAL\",\"identificacionFiscal\":\"222222222222\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"tipoFactura\":\"S01\"}}','EMITIDA'),(22,39,'F-39-507536CC','2025-05-18 02:51:27',49000.00,0.00,49000.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"PÚBLICO EN GENERAL\",\"identificacionFiscal\":\"222222222222\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"tipoFactura\":\"S01\"}}','EMITIDA'),(23,41,'F-41-4AA40091','2025-05-19 03:36:50',8124.00,1280.00,9404.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(24,42,'F-42-24D67C12','2025-05-19 04:18:24',49926.00,0.00,49926.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(25,44,'F-44-5E0D8520','2025-05-24 03:54:45',8124.00,1280.00,9404.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(26,45,'F-45-040487D0','2025-05-25 08:22:00',8124.00,1280.00,9404.00,'{\"emisor\":{\"razonSocial\":\"Empresa de Ejemplo S.A. de C.V.\",\"identificacionFiscal\":\"ABC123456789\",\"direccionFacturacion\":\"Calle Ficticia, 123, Colonia Inventada, Ciudad Falsa, Municipio Inventado, Dpt Imaginario, Colombia, C.P. 12345\",\"regimenFiscal\":\"General de Ley Personas Morales\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(28,49,'F-49-977B860E','2025-05-26 03:30:21',41424.00,1920.00,43344.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"PÚBLICO EN GENERAL\",\"identificacionFiscal\":\"222222222222\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"tipoFactura\":\"S01\"}}','EMITIDA'),(29,50,'F-50-11914F28','2025-05-26 03:37:34',14150.00,640.00,14790.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Botina S.A\",\"identificacionFiscal\":\"093993181\",\"direccionFacturacion\":\"DIRECCCION FACTO\",\"tipoFactura\":\"Electro\"}}','EMITIDA'),(30,51,'F-51-84B9A8E8','2025-05-26 03:42:56',13.00,0.00,13.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Juan Suarez\",\"identificacionFiscal\":\"TEST1\",\"direccionFacturacion\":\"test facto\",\"tipoFactura\":\"E\"}}','EMITIDA'),(31,52,'F-52-A6D13BE3','2025-05-26 03:44:33',13.00,0.00,13.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Daniel Macias\",\"identificacionFiscal\":\"939898911\",\"direccionFacturacion\":\"n/a\",\"tipoFactura\":\"P01\"}}','EMITIDA'),(33,54,'F-54-D96CDE2E','2025-05-26 06:37:14',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(34,55,'F-55-391B135B','2025-05-26 06:46:27',7000.00,0.00,7000.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Botina S.A\",\"identificacionFiscal\":\"093993181\",\"direccionFacturacion\":\"DIRECCCION FACTO\",\"tipoFactura\":\"Electro\"}}','EMITIDA'),(35,56,'F-56-E9378636','2025-05-26 06:48:25',13.00,0.00,13.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Botina S.A\",\"identificacionFiscal\":\"093993181\",\"direccionFacturacion\":\"DIRECCCION FACTO\",\"tipoFactura\":\"Electro\"}}','EMITIDA'),(37,58,'F-58-7D96EDC3','2025-05-26 08:10:19',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(38,59,'F-59-D6C46D4A','2025-05-26 08:10:22',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(39,60,'F-60-9B6E97A8','2025-05-26 08:10:24',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(40,61,'F-61-C12589A6','2025-05-26 08:10:25',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(41,62,'F-62-219B4E0C','2025-05-26 08:10:26',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(42,63,'F-63-1F546DCA','2025-05-26 08:12:22',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(43,64,'F-64-7232C651','2025-05-26 08:12:23',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(44,65,'F-65-69231656','2025-05-26 08:12:24',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(45,66,'F-66-5E8FC48E','2025-05-26 08:12:25',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(46,67,'F-67-D8D26C3C','2025-05-26 08:12:26',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(47,68,'F-68-57D62C4F','2025-05-26 08:12:34',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(48,76,'F-76-5AA593B5','2025-05-27 03:59:05',70000.00,0.00,70000.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Botina S.A\",\"identificacionFiscal\":\"093993181\",\"direccionFacturacion\":\"DIRECCCION FACTO\",\"tipoFactura\":\"Electro\"}}','EMITIDA'),(49,89,'F-89-3DF8312E','2025-05-28 05:39:43',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(50,90,'F-90-A58BCE1E','2025-05-28 05:40:13',150.00,0.00,150.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Nueva Razón Social S.A.\",\"identificacionFiscal\":\"NRS010101XYZ\",\"direccionFacturacion\":\"Av. Fiscal 123, Colonia Centro\",\"tipoFactura\":\"G03\"}}','EMITIDA'),(51,93,'F-93-37D540C1','2025-05-28 05:45:46',7000.00,0.00,7000.00,'{\"emisor\":{\"razonSocial\":\"Sistema de ventas D2 S.A\",\"identificacionFiscal\":\"901.234.567-8\",\"direccionFacturacion\":\"Calle Ficticia, 123, Barrio Test, X, Mocoa, Dpt. Putumayo, Colombia, C.P. 12345\",\"regimenFiscal\":\"Régimen Ordinario\"},\"receptor\":{\"razonSocial\":\"Botina S.A\",\"identificacionFiscal\":\"093993181\",\"direccionFacturacion\":\"DIRECCCION FACTO\",\"tipoFactura\":\"Electro\"}}','EMITIDA');
/*!40000 ALTER TABLE `facturas` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `garantias`
--

DROP TABLE IF EXISTS `garantias`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `garantias` (
  `id_garantia` int NOT NULL AUTO_INCREMENT,
  `id_venta` int DEFAULT NULL,
  `id_producto` int DEFAULT NULL,
  `id_cliente` int DEFAULT NULL,
  `numero_ticket` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_motivo` int DEFAULT NULL,
  `fecha_reclamo` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_resolucion` timestamp NULL DEFAULT NULL,
  `estado` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_general_ci,
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
  `id_impuesto_aplicable` int NOT NULL AUTO_INCREMENT,
  `id_tasa` int DEFAULT NULL,
  `id_producto` int DEFAULT NULL,
  `id_categoria` int DEFAULT NULL,
  `aplica` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date DEFAULT NULL,
  PRIMARY KEY (`id_impuesto_aplicable`),
  UNIQUE KEY `uk_impuesto_aplicable` (`id_tasa`,`id_producto`,`id_categoria`,`fecha_inicio`),
  KEY `id_producto` (`id_producto`),
  KEY `id_categoria` (`id_categoria`),
  CONSTRAINT `impuestos_aplicables_ibfk_1` FOREIGN KEY (`id_tasa`) REFERENCES `tasas_impuestos` (`id_tasa`),
  CONSTRAINT `impuestos_aplicables_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`),
  CONSTRAINT `impuestos_aplicables_ibfk_3` FOREIGN KEY (`id_categoria`) REFERENCES `categorias` (`id_categoria`),
  CONSTRAINT `chk_producto_o_categoria` CHECK (((`id_producto` is null) or (`id_categoria` is null)))
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `impuestos_aplicables`
--

LOCK TABLES `impuestos_aplicables` WRITE;
/*!40000 ALTER TABLE `impuestos_aplicables` DISABLE KEYS */;
INSERT INTO `impuestos_aplicables` VALUES (1,1,7,NULL,1,'2025-05-14',NULL);
/*!40000 ALTER TABLE `impuestos_aplicables` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lotes`
--

DROP TABLE IF EXISTS `lotes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `lotes` (
  `id_lote` int NOT NULL AUTO_INCREMENT,
  `id_entrada` int DEFAULT NULL,
  `id_producto` int DEFAULT NULL,
  `numero_lote` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `fecha_entrada` date DEFAULT NULL,
  `fecha_vencimiento` date DEFAULT NULL,
  `cantidad` int NOT NULL,
  `activo` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id_lote`),
  KEY `id_entrada` (`id_entrada`),
  KEY `id_producto` (`id_producto`),
  CONSTRAINT `lotes_ibfk_1` FOREIGN KEY (`id_entrada`) REFERENCES `entradas_productos` (`id_entrada`),
  CONSTRAINT `lotes_ibfk_2` FOREIGN KEY (`id_producto`) REFERENCES `productos` (`id_producto`)
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lotes`
--

LOCK TABLES `lotes` WRITE;
/*!40000 ALTER TABLE `lotes` DISABLE KEYS */;
INSERT INTO `lotes` VALUES (1,1,4,'LOT-test2-20254','2025-04-19','2025-05-09',0,0),(2,2,3,'LOT-test1-20254','2025-04-17','2025-05-09',27,0),(3,3,7,'LOT-test3-20254','2025-04-25',NULL,0,0),(4,4,8,'LOT-test4-20254','2025-04-30',NULL,0,0),(5,5,8,'LOT-test4-20255','2025-05-01',NULL,0,0),(6,6,8,'LOT-test4-20255','2025-04-30','2025-05-29',82,1),(7,7,7,'LOT-test3-20255','2025-05-01',NULL,0,0),(8,8,10,'LOT-test5-20255','2025-05-02',NULL,11,0),(9,9,10,'LOT-test5-20255','2025-05-02',NULL,62,1),(10,10,10,'LOT-test5-20255','2025-05-02',NULL,100,1),(11,11,4,'LOT-test2-20255','2025-05-02',NULL,0,0),(12,12,13,'LOT-test6-20255','2025-05-02',NULL,18,1),(13,13,13,'test','2025-05-01','2025-06-06',12,0),(14,14,13,'LOT006','2025-05-02','2025-12-30',200,1),(15,15,13,'LOT006','2025-05-02','2025-12-30',200,1),(16,16,13,'LOT-test6-20255','2025-05-03',NULL,1,1),(17,55,14,'LOT-test7-20255','2025-05-04',NULL,0,0),(18,62,16,'LOT-PROD-001-20255','2025-05-07',NULL,9,1),(19,89,4,'LOT-test2-20255','2025-05-09',NULL,9,1),(20,97,3,'LOT-test1-20255','2025-05-16',NULL,0,0),(21,128,7,'LOT-PROD-002-20255','2025-05-18',NULL,0,0);
/*!40000 ALTER TABLE `lotes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `metodos_pago`
--

DROP TABLE IF EXISTS `metodos_pago`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `metodos_pago` (
  `id_metodo_pago` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
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
  `id_motivo` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
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
-- Table structure for table `movimiento_caja`
--

DROP TABLE IF EXISTS `movimiento_caja`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento_caja` (
  `id_movimiento` int NOT NULL AUTO_INCREMENT,
  `id_caja` int NOT NULL,
  `tipo_movimiento` varchar(50) NOT NULL,
  `descripcion` varchar(255) DEFAULT NULL,
  `monto` decimal(10,2) NOT NULL,
  `fecha` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `id_usuario` int NOT NULL,
  `referencia_externa` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`id_movimiento`),
  KEY `id_caja` (`id_caja`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `movimiento_caja_ibfk_1` FOREIGN KEY (`id_caja`) REFERENCES `caja` (`id_caja`),
  CONSTRAINT `movimiento_caja_ibfk_2` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimiento_caja`
--

LOCK TABLES `movimiento_caja` WRITE;
/*!40000 ALTER TABLE `movimiento_caja` DISABLE KEYS */;
INSERT INTO `movimiento_caja` VALUES (1,1,'APERTURA','Apertura de caja manual: Saldo inicial del día',100000.00,'2025-05-25 06:33:18',31,NULL),(2,2,'APERTURA','Apertura de caja manual: Saldo inicial del día',100000.00,'2025-05-25 06:45:38',31,NULL),(3,3,'APERTURA','Apertura de caja manual: Saldo inicial del día',100000.00,'2025-05-25 06:51:15',31,NULL),(4,4,'APERTURA','Apertura de caja con saldo heredado',1500000.00,'2025-05-25 06:51:51',31,NULL),(5,1,'INGRESO','Ajuste manual de efectivo',75.50,'2025-05-25 07:13:43',31,NULL),(6,1,'AJUSTE_RETIRO_AUDITORIA','Ajuste por auditoría (ID: 6): Corrección de diferencia',50000.00,'2025-05-25 07:32:01',31,NULL),(7,1,'AJUSTE_INGRESO_AUDITORIA','Ajuste por auditoría (ID: 7): Corrección de diferencia',50000.00,'2025-05-25 07:32:38',31,NULL),(8,5,'APERTURA','Apertura de caja manual: Saldo inicial del día',1000000.00,'2025-05-26 08:10:12',31,NULL),(9,5,'VENTA','Venta No. VTA-1746826236822',150.00,'2025-05-26 08:10:19',31,'58'),(10,5,'VENTA','Venta No. VTA-1746826236823',150.00,'2025-05-26 08:10:22',31,'59'),(11,5,'VENTA','Venta No. VTA-1746826236824',150.00,'2025-05-26 08:10:24',31,'60'),(12,5,'VENTA','Venta No. VTA-1746826236825',150.00,'2025-05-26 08:10:25',31,'61'),(13,5,'VENTA','Venta No. VTA-1746826236826',150.00,'2025-05-26 08:10:26',31,'62'),(14,5,'VENTA','Venta No. VTA-1746826236833',70000.00,'2025-05-27 03:32:14',31,'69'),(15,5,'VENTA','Venta No. VTA-1746826236840',70000.00,'2025-05-27 03:59:05',31,'76'),(16,6,'APERTURA','Apertura de caja manual: Saldo inicial del día',1000000.00,'2025-05-27 04:57:17',31,NULL),(17,7,'APERTURA','Apertura de caja manual: Saldo inicial del día',1000000.00,'2025-05-27 10:19:48',31,NULL),(18,8,'APERTURA','Apertura de caja manual: Saldo inicial del día',10000000.00,'2025-05-27 10:23:35',31,NULL),(19,9,'APERTURA','Apertura de caja manual: Saldo inicial del día',10000000.00,'2025-05-27 10:28:44',31,NULL),(20,10,'APERTURA','Apertura de caja con saldo heredado',1140750.00,'2025-05-27 10:30:17',31,NULL),(21,11,'APERTURA','Apertura de caja manual: si',100.00,'2025-05-27 10:33:55',27,NULL),(22,12,'APERTURA','Apertura de caja con saldo heredado',10000.00,'2025-05-27 10:37:35',27,NULL),(23,13,'APERTURA','Apertura de caja manual: Asi me entregaron la caja',130000.00,'2025-05-27 10:38:04',27,NULL),(24,14,'APERTURA','Apertura de caja con saldo heredado',140000.00,'2025-05-27 11:36:06',27,NULL),(25,15,'APERTURA','Apertura de caja con saldo heredado',150000.00,'2025-05-27 11:39:15',27,NULL),(26,16,'APERTURA','Apertura de caja con saldo heredado',160000.00,'2025-05-27 21:27:00',27,NULL),(27,17,'APERTURA','Apertura de caja con saldo heredado',1140750.00,'2025-05-27 21:29:25',31,NULL),(28,17,'VENTA','Venta No. VTA-1746826236843',7000.00,'2025-05-27 21:30:37',31,'81'),(29,18,'APERTURA','Apertura de caja con saldo heredado',160000.00,'2025-05-27 23:13:22',27,NULL),(30,19,'APERTURA','Apertura de caja con saldo heredado',160000.00,'2025-05-27 23:59:30',27,NULL),(31,20,'APERTURA','Apertura de caja con saldo heredado',170000.00,'2025-05-28 04:50:39',27,NULL),(32,20,'VENTA','Venta No. VTA-1746826236846',150.00,'2025-05-28 05:40:13',27,'90'),(33,20,'VENTA','Venta No. VTA-1746826236847',13.00,'2025-05-28 05:44:59',27,'91'),(34,20,'VENTA','Venta No. VTA-1746826236848',13.00,'2025-05-28 05:45:19',27,'92');
/*!40000 ALTER TABLE `movimiento_caja` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notificaciones`
--

DROP TABLE IF EXISTS `notificaciones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notificaciones` (
  `id_notificacion` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int DEFAULT NULL,
  `titulo` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `mensaje` text COLLATE utf8mb4_general_ci NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `leida` tinyint(1) DEFAULT '0',
  `tipo` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `id_referencia` int DEFAULT NULL,
  PRIMARY KEY (`id_notificacion`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `notificaciones_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=142 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notificaciones`
--

LOCK TABLES `notificaciones` WRITE;
/*!40000 ALTER TABLE `notificaciones` DISABLE KEYS */;
INSERT INTO `notificaciones` VALUES (3,28,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',3),(4,29,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',3),(5,30,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',3),(6,31,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',3),(7,33,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',3),(10,28,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 1011 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',8),(11,29,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 1011 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',8),(12,30,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 1011 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',8),(13,31,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 1011 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',8),(14,33,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 1011 unidades, superando el máximo recomendado.','2025-05-01 10:23:00',0,'ALERTA_SOBRESTOCK',8),(17,28,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',1),(18,29,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',1),(19,30,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',1),(20,31,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',1),(21,33,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',1),(24,28,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',2),(25,29,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',2),(26,30,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',2),(27,31,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',2),(28,33,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:23:00',0,'LOTE_PROXIMO_VENCER',2),(31,28,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',1),(32,29,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',1),(33,30,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',1),(34,31,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',1),(35,33,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',1),(38,28,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',2),(39,29,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',2),(40,30,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',2),(41,31,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',2),(42,33,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:24:00',0,'LOTE_PROXIMO_VENCER',2),(45,28,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',3),(46,29,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',3),(47,30,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',3),(48,31,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',3),(49,33,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',3),(52,28,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',7),(53,29,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',7),(54,30,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',7),(55,31,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',7),(56,33,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',7),(59,28,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',8),(60,29,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',8),(61,30,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',8),(62,31,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',8),(63,33,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:24:00',0,'ALERTA_SOBRESTOCK',8),(66,28,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',1),(67,29,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',1),(68,30,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',1),(69,31,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',1),(70,33,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',1),(73,28,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',2),(74,29,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',2),(75,30,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',2),(76,31,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',2),(77,33,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:25:00',0,'LOTE_PROXIMO_VENCER',2),(80,28,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',3),(81,29,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',3),(82,30,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',3),(83,31,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',3),(84,33,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',3),(87,28,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',7),(88,29,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',7),(89,30,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',7),(90,31,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',7),(91,33,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',7),(94,28,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',8),(95,29,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',8),(96,30,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',8),(97,31,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',8),(98,33,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:25:00',0,'ALERTA_SOBRESTOCK',8),(101,28,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',3),(102,29,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',3),(103,30,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',3),(104,31,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',3),(105,33,'Sobrestock: test1','El producto test1 (Código: test1) tiene un stock de 100 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',3),(108,28,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',7),(109,29,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',7),(110,30,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',7),(111,31,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',7),(112,33,'Sobrestock: test3','El producto test3 (Código: test3) tiene un stock de 23 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',7),(115,28,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',8),(116,29,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',8),(117,30,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',8),(118,31,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',8),(119,33,'Sobrestock: test4','El producto test4 (Código: test4) tiene un stock de 111 unidades, superando el máximo recomendado.','2025-05-01 10:26:00',0,'ALERTA_SOBRESTOCK',8),(122,28,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',1),(123,29,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',1),(124,30,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',1),(125,31,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',1),(126,33,'Lote próximo a vencer: test2','El lote \'LOT-test2-20254\' del producto \'test2\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',1),(129,28,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',2),(130,29,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',2),(131,30,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',2),(132,31,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',2),(133,33,'Lote próximo a vencer: test1','El lote \'LOT-test1-20254\' del producto \'test1\' vencerá el 2025-05-09.','2025-05-01 10:26:00',0,'LOTE_PROXIMO_VENCER',2),(134,24,'Lote Vencido Desactivado: test1','El lote \'LOT-test1-20254\' del producto \'test1\' (vencido el 2025-05-09) ha sido desactivado automáticamente.','2025-05-14 10:00:01',0,'LOTE_VENCIDO',2),(136,28,'Lote Vencido Desactivado: test1','El lote \'LOT-test1-20254\' del producto \'test1\' (vencido el 2025-05-09) ha sido desactivado automáticamente.','2025-05-14 10:00:01',0,'LOTE_VENCIDO',2),(137,29,'Lote Vencido Desactivado: test1','El lote \'LOT-test1-20254\' del producto \'test1\' (vencido el 2025-05-09) ha sido desactivado automáticamente.','2025-05-14 10:00:01',0,'LOTE_VENCIDO',2),(138,30,'Lote Vencido Desactivado: test1','El lote \'LOT-test1-20254\' del producto \'test1\' (vencido el 2025-05-09) ha sido desactivado automáticamente.','2025-05-14 10:00:01',0,'LOTE_VENCIDO',2),(139,35,'Lote Vencido Desactivado: test1','El lote \'LOT-test1-20254\' del producto \'test1\' (vencido el 2025-05-09) ha sido desactivado automáticamente.','2025-05-14 10:00:01',0,'LOTE_VENCIDO',2),(140,31,'Lote Vencido Desactivado: test1','El lote \'LOT-test1-20254\' del producto \'test1\' (vencido el 2025-05-09) ha sido desactivado automáticamente.','2025-05-14 10:00:01',0,'LOTE_VENCIDO',2),(141,33,'Lote Vencido Desactivado: test1','El lote \'LOT-test1-20254\' del producto \'test1\' (vencido el 2025-05-09) ha sido desactivado automáticamente.','2025-05-14 10:00:01',0,'LOTE_VENCIDO',2);
/*!40000 ALTER TABLE `notificaciones` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pagos_credito`
--

DROP TABLE IF EXISTS `pagos_credito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pagos_credito` (
  `id_pago` int NOT NULL AUTO_INCREMENT,
  `id_credito` int DEFAULT NULL,
  `monto_pagado` decimal(10,2) NOT NULL,
  `fecha_pago` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `metodo_pago` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
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
  `id_pago` int NOT NULL AUTO_INCREMENT,
  `id_venta` int DEFAULT NULL,
  `id_metodo_pago` int DEFAULT NULL,
  `monto` decimal(10,2) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
  `id_permiso` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_general_ci,
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
  `id_usuario` int NOT NULL,
  `id_permiso` int NOT NULL,
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
  `id_producto` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nombre` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_general_ci,
  `precio_costo` decimal(10,2) NOT NULL,
  `precio_venta` decimal(10,2) NOT NULL,
  `stock` int NOT NULL DEFAULT '0',
  `stock_minimo` int DEFAULT NULL,
  `stock_maximo` int DEFAULT NULL,
  `id_categoria` int DEFAULT NULL,
  `id_proveedor` int DEFAULT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT '1',
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
INSERT INTO `productos` VALUES (3,'test1','test1','123',12.00,13.00,0,1,11,NULL,2,1),(4,'test2','test2','123',12.00,13.00,9,1,11,NULL,1,1),(7,'PROD-002','Cerveza','123',3000.00,4000.00,0,1,11,1,2,1),(8,'test4','test4','123',123.00,124.00,82,5,50,1,1,1),(10,'test5','test5','123',12.00,13.00,162,1,10,NULL,2,1),(13,'PROD-003','Cereales','123',5000.00,7000.00,419,1,11,NULL,1,1),(14,'test7','test7','123',12.00,13.00,0,1,12,NULL,1,1),(16,'PROD-001','Panela','De caña',2000.00,3000.00,9,15,30,4,2,1);
/*!40000 ALTER TABLE `productos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `promociones`
--

DROP TABLE IF EXISTS `promociones`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `promociones` (
  `id_promocion` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `tipo_promocion` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `valor` decimal(10,2) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date NOT NULL,
  `id_categoria` int DEFAULT NULL,
  `id_producto` int DEFAULT NULL,
  `activo` tinyint(1) DEFAULT '1',
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
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
  `id_proveedor` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `contacto` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `correo` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `direccion` varchar(150) COLLATE utf8mb4_general_ci DEFAULT NULL,
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
  `id_recompensa` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int DEFAULT NULL,
  `puntos_usados` int NOT NULL,
  `descripcion_recompensa` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `fecha_reclamo` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
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
  `id_rol` int NOT NULL AUTO_INCREMENT,
  `rol` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
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
  `id_rol` int NOT NULL,
  `id_permiso` int NOT NULL,
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
INSERT INTO `roles_permisos` VALUES (1,1),(1,2),(1,3),(1,4),(9,4),(1,5),(9,5),(1,6),(9,6),(1,7),(2,7),(9,7),(1,8),(9,8),(1,9),(9,9),(1,10),(9,10),(1,11),(9,11),(1,12),(9,12),(1,13),(9,13),(1,14),(9,14),(1,15),(9,15),(1,16),(9,16),(1,17),(2,17),(9,17),(1,18),(2,18),(9,18),(1,19),(2,19),(9,19),(1,20),(2,20),(9,20),(1,21),(9,21),(1,22),(2,22),(9,22),(1,23),(2,23),(9,23),(1,24),(9,24),(1,25),(2,25),(9,25),(1,26),(2,26),(9,26),(1,27),(2,27),(9,27),(1,28),(2,28),(9,28),(1,29),(2,29),(9,29),(1,30),(2,30),(9,30),(1,31),(9,31),(1,32),(2,32),(9,32),(1,33),(2,33),(9,33),(1,34),(9,34),(1,35),(9,35),(1,36),(9,36),(1,37),(9,37),(1,38),(9,38),(1,39),(9,39),(1,40),(9,40),(1,41),(2,41),(9,41),(1,42),(9,42),(1,43),(9,43),(1,44),(9,44),(1,46),(9,46),(1,47),(9,47),(1,50);
/*!40000 ALTER TABLE `roles_permisos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicios`
--

DROP TABLE IF EXISTS `servicios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicios` (
  `id_servicio` int NOT NULL AUTO_INCREMENT,
  `id_garantia` int DEFAULT NULL,
  `descripcion` text COLLATE utf8mb4_general_ci NOT NULL,
  `costo` decimal(10,2) DEFAULT NULL,
  `fecha_inicio` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_fin` timestamp NULL DEFAULT NULL,
  `estado` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
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
  `id_sesion` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int DEFAULT NULL,
  `fecha_login` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_logout` timestamp NULL DEFAULT NULL,
  `ip_address` varchar(45) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `dispositivo` varchar(100) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `estado` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
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
  `id_tasa` int NOT NULL AUTO_INCREMENT,
  `id_tipo_impuesto` int DEFAULT NULL,
  `tasa` decimal(5,2) NOT NULL,
  `fecha_inicio` date NOT NULL,
  `fecha_fin` date DEFAULT NULL,
  `descripcion` text COLLATE utf8mb4_general_ci,
  PRIMARY KEY (`id_tasa`),
  UNIQUE KEY `uk_tasa_impuesto_fecha` (`id_tipo_impuesto`,`fecha_inicio`),
  CONSTRAINT `tasas_impuestos_ibfk_1` FOREIGN KEY (`id_tipo_impuesto`) REFERENCES `tipos_impuestos` (`id_tipo_impuesto`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tasas_impuestos`
--

LOCK TABLES `tasas_impuestos` WRITE;
/*!40000 ALTER TABLE `tasas_impuestos` DISABLE KEYS */;
INSERT INTO `tasas_impuestos` VALUES (1,1,16.00,'2025-05-16','2026-05-15','Iva');
/*!40000 ALTER TABLE `tasas_impuestos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipos_impuestos`
--

DROP TABLE IF EXISTS `tipos_impuestos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tipos_impuestos` (
  `id_tipo_impuesto` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) COLLATE utf8mb4_general_ci NOT NULL,
  `descripcion` text COLLATE utf8mb4_general_ci,
  `es_porcentual` tinyint(1) NOT NULL DEFAULT '1',
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_tipo_impuesto`),
  UNIQUE KEY `nombre` (`nombre`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipos_impuestos`
--

LOCK TABLES `tipos_impuestos` WRITE;
/*!40000 ALTER TABLE `tipos_impuestos` DISABLE KEYS */;
INSERT INTO `tipos_impuestos` VALUES (1,'IVA','Impuesto',1,1);
/*!40000 ALTER TABLE `tipos_impuestos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuarios` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `correo` varchar(100) COLLATE utf8mb4_general_ci NOT NULL,
  `telefono` varchar(20) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `contraseña` varchar(255) COLLATE utf8mb4_general_ci NOT NULL,
  `id_rol` int NOT NULL,
  `estado` tinyint(1) NOT NULL DEFAULT '1',
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
INSERT INTO `usuarios` VALUES (24,'Admin','admin','1234567890','$2a$10$017NV/XpgjRQ0jxxbJaw6eO.P2J1BgTYo85xYz7uPrzxXS5l01XSm',1,1),(27,'Neider','neider','312','$2a$10$v0.lXmfN1iJbfXq8iunZaeHCN..FfsDCB4IO2rLszr8ZRF00fA/7y',1,1),(28,'Andres','andres','33131','$2a$10$DlL9REAu4Se0t28jLGuoYelAxqcN72qkYckIhVo6gDlzTogZiM4be',1,1),(29,'Leonel','leonel','3131313','$2a$10$ADoHT6KFAdEoB1FaCUB.U.4Fzi/45lWsxbEQ5eUAf9XyOVe8jdHQy',1,0),(30,'Daniel','daniel','31313131','$2a$10$n5K8RmKAvqUAH2Nw5oeXX.dUhAgJzhjvYPSR/UAiFQy5gBXU7w1L2',1,1),(31,'Vendedor','vendedor','313','$2a$10$0zm.eHCfzngIYyzkOVMUounwteDrLGQldDeGLq53LIYYaZBJWhvxW',2,1),(33,'testUpdapte','testUpdate','5555555555','$2a$10$e92kA4sc5HJBqv2Je/x3EuVv2LNXGDgEId2v6LIvsD3VNoFJVGX1C',9,1),(35,'test','test','123','$2a$10$Jjt74NbKQ8dmUD2XmlG2W.NGB9bg7oUR.1ipXNTEeRxXiE.17RGxa',1,1);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `vendedores`
--

DROP TABLE IF EXISTS `vendedores`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `vendedores` (
  `id_vendedor` int NOT NULL AUTO_INCREMENT,
  `id_usuario` int DEFAULT NULL,
  `objetivo_ventas` decimal(10,2) DEFAULT NULL,
  `fecha_contratacion` date DEFAULT NULL,
  PRIMARY KEY (`id_vendedor`),
  KEY `id_usuario` (`id_usuario`),
  CONSTRAINT `vendedores_ibfk_1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `vendedores`
--

LOCK TABLES `vendedores` WRITE;
/*!40000 ALTER TABLE `vendedores` DISABLE KEYS */;
INSERT INTO `vendedores` VALUES (1,31,1000000.00,'2025-04-19'),(2,29,550000.00,'2025-05-26'),(4,27,100000.00,'2025-05-26');
/*!40000 ALTER TABLE `vendedores` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ventas`
--

DROP TABLE IF EXISTS `ventas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ventas` (
  `id_venta` int NOT NULL AUTO_INCREMENT,
  `id_cliente` int DEFAULT NULL,
  `id_vendedor` int NOT NULL,
  `numero_venta` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `requiere_factura` tinyint(1) DEFAULT '0',
  `aplicar_impuestos` tinyint(1) DEFAULT '0',
  `tipo_pago` varchar(50) COLLATE utf8mb4_general_ci DEFAULT NULL,
  `subtotal` decimal(12,2) DEFAULT '0.00',
  `total_impuestos` decimal(12,2) DEFAULT '0.00',
  `total_con_impuestos` decimal(12,2) DEFAULT '0.00',
  `estado_venta` varchar(50) COLLATE utf8mb4_general_ci DEFAULT 'PENDIENTE',
  `observaciones` text COLLATE utf8mb4_general_ci,
  `fecha_creacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_actualizacion` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_venta`),
  UNIQUE KEY `numero_venta` (`numero_venta`),
  KEY `id_cliente` (`id_cliente`),
  KEY `id_vendedor` (`id_vendedor`),
  CONSTRAINT `ventas_ibfk_1` FOREIGN KEY (`id_cliente`) REFERENCES `clientes` (`id_cliente`),
  CONSTRAINT `ventas_ibfk_2` FOREIGN KEY (`id_vendedor`) REFERENCES `vendedores` (`id_vendedor`)
) ENGINE=InnoDB AUTO_INCREMENT=94 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ventas`
--

LOCK TABLES `ventas` WRITE;
/*!40000 ALTER TABLE `ventas` DISABLE KEYS */;
INSERT INTO `ventas` VALUES (3,3,1,NULL,'2025-05-09 02:54:57',0,0,NULL,39.00,0.00,39.00,NULL,NULL,'2025-05-09 02:54:57','2025-05-09 02:54:57'),(4,3,1,NULL,'2025-05-09 03:10:18',0,0,NULL,39.00,0.00,39.00,NULL,NULL,'2025-05-09 03:10:18','2025-05-09 03:10:18'),(9,2,1,NULL,'2025-05-09 03:39:34',1,0,NULL,39.00,0.00,39.00,NULL,NULL,'2025-05-09 03:39:34','2025-05-09 03:39:34'),(10,2,1,NULL,'2025-05-09 03:56:10',1,0,'EFECTIVO',39.00,0.00,39.00,NULL,NULL,'2025-05-09 03:56:10','2025-05-09 03:56:10'),(11,3,1,'VTA-1746826236812','2025-05-10 02:30:36',0,1,NULL,78.00,0.00,78.00,NULL,NULL,'2025-05-10 02:30:37','2025-05-10 02:30:37'),(12,2,1,NULL,'2025-05-13 21:00:07',1,1,'EFECTIVO',150.00,0.00,150.00,NULL,NULL,'2025-05-13 21:00:08','2025-05-13 21:00:08'),(14,2,1,NULL,'2025-05-13 21:16:20',1,1,'EFECTIVO',150.00,0.00,150.00,NULL,NULL,'2025-05-13 21:16:20','2025-05-13 21:16:20'),(15,3,1,NULL,'2025-05-17 02:13:34',0,0,'EFECTIVO',26.00,0.00,26.00,NULL,NULL,'2025-05-17 02:13:34','2025-05-17 02:13:34'),(16,3,1,NULL,'2025-05-17 02:14:44',0,0,'EFECTIVO',7000.00,0.00,7000.00,NULL,NULL,'2025-05-17 02:14:44','2025-05-17 02:14:44'),(17,3,1,NULL,'2025-05-17 02:17:29',0,0,'EFECTIVO',14013.00,0.00,14013.00,NULL,NULL,'2025-05-17 02:17:29','2025-05-17 02:17:29'),(18,3,1,NULL,'2025-05-17 02:20:49',0,0,'TARJETA',39.00,0.00,39.00,NULL,NULL,'2025-05-17 02:20:49','2025-05-17 02:20:49'),(19,3,1,NULL,'2025-05-17 02:42:33',0,0,'EFECTIVO',13.00,0.00,13.00,NULL,NULL,'2025-05-17 02:42:33','2025-05-17 02:42:33'),(20,3,1,NULL,'2025-05-17 03:39:59',0,0,'TARJETA',13.00,0.00,13.00,NULL,NULL,'2025-05-17 03:39:59','2025-05-17 03:39:59'),(21,2,1,NULL,'2025-05-17 08:24:05',1,1,'EFECTIVO',4000.00,0.00,4000.00,NULL,NULL,'2025-05-17 08:24:06','2025-05-17 08:24:06'),(22,2,1,NULL,'2025-05-17 08:25:12',1,1,'TARJETA',3000.00,0.00,3000.00,NULL,NULL,'2025-05-17 08:25:12','2025-05-17 08:25:12'),(23,2,1,NULL,'2025-05-17 09:07:05',1,1,'TARJETA',13.00,0.00,13.00,NULL,NULL,'2025-05-17 09:07:05','2025-05-17 09:07:05'),(24,2,1,NULL,'2025-05-17 09:07:55',1,1,'TARJETA',4000.00,0.00,4000.00,NULL,NULL,'2025-05-17 09:07:55','2025-05-17 09:07:55'),(25,2,1,NULL,'2025-05-17 09:47:23',1,1,'TARJETA',13.00,0.00,13.00,NULL,NULL,'2025-05-17 09:47:24','2025-05-17 09:47:24'),(26,2,1,NULL,'2025-05-17 09:49:24',1,1,'TARJETA',13.00,0.00,13.00,NULL,NULL,'2025-05-17 09:49:24','2025-05-17 09:49:24'),(27,2,1,NULL,'2025-05-17 10:09:45',1,1,'TARJETA',13.00,0.00,13.00,NULL,NULL,'2025-05-17 10:09:45','2025-05-17 10:09:45'),(28,2,1,NULL,'2025-05-17 10:10:26',1,1,'TARJETA',4000.00,0.00,4000.00,NULL,NULL,'2025-05-17 10:10:26','2025-05-17 10:10:26'),(29,2,1,NULL,'2025-05-17 20:39:46',1,1,'TARJETA',13.00,0.00,13.00,NULL,NULL,'2025-05-17 20:39:46','2025-05-17 20:39:46'),(30,11,1,NULL,'2025-05-17 21:07:45',1,1,'TARJETA',3000.00,0.00,3000.00,NULL,NULL,'2025-05-17 21:07:45','2025-05-17 21:07:45'),(31,3,1,NULL,'2025-05-17 21:43:31',0,0,'TARJETA',3000.00,0.00,3000.00,NULL,NULL,'2025-05-17 21:43:31','2025-05-17 21:43:31'),(32,2,1,NULL,'2025-05-17 21:45:24',1,1,'TARJETA',3000.00,0.00,3000.00,NULL,NULL,'2025-05-17 21:45:24','2025-05-17 21:45:24'),(33,13,1,NULL,'2025-05-17 22:05:05',1,1,'TARJETA',3000.00,0.00,3000.00,NULL,NULL,'2025-05-17 22:05:05','2025-05-17 22:05:05'),(34,14,1,NULL,'2025-05-17 22:17:50',1,1,'TARJETA',3000.00,0.00,3000.00,NULL,NULL,'2025-05-17 22:17:50','2025-05-17 22:17:50'),(35,15,1,NULL,'2025-05-17 22:20:48',1,1,'TARJETA',3000.00,0.00,3000.00,NULL,NULL,'2025-05-17 22:20:48','2025-05-17 22:20:48'),(36,16,1,NULL,'2025-05-17 22:23:45',1,1,'TARJETA',13.00,0.00,13.00,NULL,NULL,'2025-05-17 22:23:45','2025-05-17 22:23:45'),(37,13,1,NULL,'2025-05-18 00:17:21',1,1,'TARJETA',7000.00,0.00,7000.00,NULL,NULL,'2025-05-18 00:17:21','2025-05-18 00:17:21'),(38,3,1,NULL,'2025-05-18 02:15:53',0,0,'TARJETA',13.00,0.00,13.00,NULL,NULL,'2025-05-18 02:15:54','2025-05-18 02:15:54'),(39,17,1,NULL,'2025-05-18 02:51:27',1,1,'TARJETA',49000.00,0.00,49000.00,NULL,NULL,'2025-05-18 02:51:27','2025-05-18 02:51:27'),(40,3,1,NULL,'2025-05-18 10:47:25',0,0,'TARJETA',7000.00,0.00,7000.00,NULL,NULL,'2025-05-18 10:47:26','2025-05-18 10:47:26'),(41,2,1,NULL,'2025-05-19 03:36:50',1,1,'EFECTIVO',8124.00,1280.00,9404.00,NULL,NULL,'2025-05-19 03:36:50','2025-05-19 03:36:50'),(42,2,1,NULL,'2025-05-19 04:18:23',1,1,'TARJETA',49926.00,0.00,49926.00,NULL,NULL,'2025-05-19 04:18:24','2025-05-19 04:18:24'),(43,3,1,NULL,'2025-05-19 07:14:36',0,0,'TARJETA',40000.00,0.00,40000.00,NULL,NULL,'2025-05-19 07:14:36','2025-05-19 07:14:36'),(44,2,1,NULL,'2025-05-24 03:54:44',1,1,'EFECTIVO',8124.00,1280.00,9404.00,NULL,NULL,'2025-05-24 03:54:45','2025-05-24 03:54:45'),(45,2,1,'VTA-1746826236813','2025-05-25 08:22:00',1,1,'TARJETA',8124.00,1280.00,9404.00,NULL,NULL,'2025-05-25 08:22:00','2025-05-25 08:22:00'),(48,3,1,'VTA-1746826236814','2025-05-25 08:35:44',0,0,'TARJETA',56000.00,0.00,56000.00,NULL,NULL,'2025-05-25 08:35:44','2025-05-25 08:35:44'),(49,15,1,'VTA-1746826236815','2025-05-26 03:30:20',1,1,'TARJETA',41424.00,1920.00,43344.00,NULL,NULL,'2025-05-26 03:30:21','2025-05-26 03:30:21'),(50,7,1,'VTA-1746826236816','2025-05-26 03:37:34',1,1,'TARJETA',14150.00,640.00,14790.00,NULL,NULL,'2025-05-26 03:37:34','2025-05-26 03:37:34'),(51,8,1,'VTA-1746826236817','2025-05-26 03:42:56',1,1,'TARJETA',13.00,0.00,13.00,NULL,NULL,'2025-05-26 03:42:56','2025-05-26 03:42:56'),(52,18,1,'VTA-1746826236818','2025-05-26 03:44:33',1,1,'TARJETA',13.00,0.00,13.00,NULL,NULL,'2025-05-26 03:44:33','2025-05-26 03:44:33'),(54,2,1,'VTA-1746826236819','2025-05-26 06:37:14',1,1,'TARJETA',150.00,0.00,150.00,NULL,NULL,'2025-05-26 06:37:14','2025-05-26 06:37:14'),(55,7,1,'VTA-1746826236820','2025-05-26 06:46:27',1,1,'TARJETA',7000.00,0.00,7000.00,NULL,NULL,'2025-05-26 06:46:27','2025-05-26 06:46:27'),(56,7,1,'VTA-1746826236821','2025-05-26 06:48:25',1,1,'TARJETA',13.00,0.00,13.00,NULL,NULL,'2025-05-26 06:48:25','2025-05-26 06:48:25'),(58,2,1,'VTA-1746826236822','2025-05-26 08:10:19',1,1,'EFECTIVO',150.00,0.00,150.00,NULL,NULL,'2025-05-26 08:10:19','2025-05-26 08:10:19'),(59,2,1,'VTA-1746826236823','2025-05-26 08:10:22',1,1,'EFECTIVO',150.00,0.00,150.00,NULL,NULL,'2025-05-26 08:10:22','2025-05-26 08:10:22'),(60,2,1,'VTA-1746826236824','2025-05-26 08:10:23',1,1,'EFECTIVO',150.00,0.00,150.00,NULL,NULL,'2025-05-26 08:10:24','2025-05-26 08:10:24'),(61,2,1,'VTA-1746826236825','2025-05-26 08:10:25',1,1,'EFECTIVO',150.00,0.00,150.00,NULL,NULL,'2025-05-26 08:10:25','2025-05-26 08:10:25'),(62,2,1,'VTA-1746826236826','2025-05-26 08:10:26',1,1,'EFECTIVO',150.00,0.00,150.00,NULL,NULL,'2025-05-26 08:10:26','2025-05-26 08:10:26'),(63,2,1,'VTA-1746826236827','2025-05-26 08:12:22',1,1,'TARJETA',150.00,0.00,150.00,NULL,NULL,'2025-05-26 08:12:22','2025-05-26 08:12:22'),(64,2,1,'VTA-1746826236828','2025-05-26 08:12:23',1,1,'TARJETA',150.00,0.00,150.00,NULL,NULL,'2025-05-26 08:12:23','2025-05-26 08:12:23'),(65,2,1,'VTA-1746826236829','2025-05-26 08:12:24',1,1,'TARJETA',150.00,0.00,150.00,NULL,NULL,'2025-05-26 08:12:24','2025-05-26 08:12:24'),(66,2,1,'VTA-1746826236830','2025-05-26 08:12:25',1,1,'TARJETA',150.00,0.00,150.00,NULL,NULL,'2025-05-26 08:12:25','2025-05-26 08:12:25'),(67,2,1,'VTA-1746826236831','2025-05-26 08:12:26',1,1,'TARJETA',150.00,0.00,150.00,NULL,NULL,'2025-05-26 08:12:26','2025-05-26 08:12:26'),(68,2,1,'VTA-1746826236832','2025-05-26 08:12:34',1,1,'TARJETA',150.00,0.00,150.00,NULL,NULL,'2025-05-26 08:12:34','2025-05-26 08:12:34'),(69,3,1,'VTA-1746826236833','2025-05-27 03:32:14',0,0,'EFECTIVO',70000.00,0.00,70000.00,NULL,NULL,'2025-05-27 03:32:14','2025-05-27 03:32:14'),(70,3,1,'VTA-1746826236834','2025-05-27 03:36:30',0,0,'TARJETA',7000.00,0.00,7000.00,NULL,NULL,'2025-05-27 03:36:30','2025-05-27 03:36:30'),(71,3,1,'VTA-1746826236835','2025-05-27 03:37:26',0,0,'TARJETA',70000.00,0.00,70000.00,NULL,NULL,'2025-05-27 03:37:26','2025-05-27 03:37:26'),(72,3,1,'VTA-1746826236836','2025-05-27 03:45:13',0,0,'TARJETA',70000.00,0.00,70000.00,NULL,NULL,'2025-05-27 03:45:13','2025-05-27 03:45:13'),(73,3,1,'VTA-1746826236837','2025-05-27 03:45:30',0,0,'TARJETA',42000.00,0.00,42000.00,NULL,NULL,'2025-05-27 03:45:30','2025-05-27 03:45:30'),(74,3,1,'VTA-1746826236838','2025-05-27 03:58:10',0,0,'TARJETA',7000.00,0.00,7000.00,NULL,NULL,'2025-05-27 03:58:10','2025-05-27 03:58:10'),(75,3,1,'VTA-1746826236839','2025-05-27 03:58:32',0,0,'TARJETA',70000.00,0.00,70000.00,NULL,NULL,'2025-05-27 03:58:32','2025-05-27 03:58:32'),(76,7,1,'VTA-1746826236840','2025-05-27 03:59:05',1,1,'EFECTIVO',70000.00,0.00,70000.00,NULL,NULL,'2025-05-27 03:59:05','2025-05-27 03:59:05'),(78,3,1,'VTA-1746826236841','2025-05-27 11:41:34',0,0,'TARJETA',7000.00,0.00,7000.00,NULL,NULL,'2025-05-27 11:41:34','2025-05-27 11:41:34'),(79,3,1,'VTA-1746826236842','2025-05-27 21:27:36',0,0,'TARJETA',7000.00,0.00,7000.00,NULL,NULL,'2025-05-27 21:27:36','2025-05-27 21:27:36'),(81,3,1,'VTA-1746826236843','2025-05-27 21:30:36',0,0,'EFECTIVO',7000.00,0.00,7000.00,NULL,NULL,'2025-05-27 21:30:37','2025-05-27 21:30:37'),(83,3,1,'VTA-1746826236844','2025-05-27 23:59:51',0,0,'TARJETA',7000.00,0.00,7000.00,NULL,NULL,'2025-05-27 23:59:51','2025-05-27 23:59:51'),(89,2,4,'VTA-1746826236845','2025-05-28 05:39:42',1,1,'TARJETA',150.00,0.00,150.00,NULL,NULL,'2025-05-28 05:39:43','2025-05-28 05:39:43'),(90,2,4,'VTA-1746826236846','2025-05-28 05:40:13',1,1,'EFECTIVO',150.00,0.00,150.00,NULL,NULL,'2025-05-28 05:40:13','2025-05-28 05:40:13'),(91,3,4,'VTA-1746826236847','2025-05-28 05:44:59',0,0,'EFECTIVO',13.00,0.00,13.00,NULL,NULL,'2025-05-28 05:44:59','2025-05-28 05:44:59'),(92,3,4,'VTA-1746826236848','2025-05-28 05:45:19',0,0,'EFECTIVO',13.00,0.00,13.00,NULL,NULL,'2025-05-28 05:45:19','2025-05-28 05:45:19'),(93,7,4,'VTA-1746826236849','2025-05-28 05:45:46',1,1,'TARJETA',7000.00,0.00,7000.00,NULL,NULL,'2025-05-28 05:45:46','2025-05-28 05:45:46');
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

-- Dump completed on 2025-06-03 21:46:49
