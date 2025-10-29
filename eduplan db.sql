-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Servidor: 127.0.0.1
-- Tiempo de generación: 29-10-2025 a las 21:43:15
-- Versión del servidor: 10.4.32-MariaDB
-- Versión de PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Base de datos: `eduplan`
--
CREATE DATABASE IF NOT EXISTS `eduplan` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `eduplan`;

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `asignatura`
--

CREATE TABLE `asignatura` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `asignatura`
--

INSERT INTO `asignatura` (`id`, `nombre`) VALUES
(7, 'Artes Visuales'),
(4, 'Ciencias Naturales'),
(6, 'Educación Física y Salud'),
(3, 'Historia, Geografía y Ciencias Sociales'),
(5, 'Inglés'),
(2, 'Lenguaje y Comunicación'),
(1, 'Matemática'),
(8, 'Música'),
(10, 'Orientación'),
(9, 'Tecnología');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `curso`
--

CREATE TABLE `curso` (
  `id` bigint(20) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `nivel` int(11) NOT NULL COMMENT 'Nivel:\r\n1= 1° Basico A, 1= 1° Basico B\r\n2= 2° Basico A, 2= 2° Basico B'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `curso`
--

INSERT INTO `curso` (`id`, `nombre`, `nivel`) VALUES
(1, '1° Básico A', 1),
(2, '2° Básico A', 2),
(3, '3° Básico A', 3),
(4, '4° Básico A', 4),
(5, '5° Básico A', 5),
(6, '6° Básico A', 6),
(7, '7° Básico A', 7),
(8, '8° Básico A', 8);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `curso_alumno`
--

CREATE TABLE `curso_alumno` (
  `id` bigint(20) NOT NULL,
  `curso_id` bigint(20) NOT NULL,
  `alumno_id` int(11) NOT NULL,
  `anio_escolar` int(11) NOT NULL DEFAULT 2025
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `curso_alumno`
--

INSERT INTO `curso_alumno` (`id`, `curso_id`, `alumno_id`, `anio_escolar`) VALUES
(1, 1, 2, 2025);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `curso_asignatura_docente`
--

CREATE TABLE `curso_asignatura_docente` (
  `id` bigint(20) NOT NULL,
  `curso_id` bigint(20) NOT NULL,
  `asignatura_id` bigint(20) NOT NULL,
  `docente_id` int(11) NOT NULL,
  `anio_escolar` int(11) NOT NULL DEFAULT 2025
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `curso_asignatura_docente`
--

INSERT INTO `curso_asignatura_docente` (`id`, `curso_id`, `asignatura_id`, `docente_id`, `anio_escolar`) VALUES
(1, 1, 1, 1, 2025),
(2, 1, 2, 1, 2025),
(3, 2, 1, 1, 2025),
(4, 2, 2, 1, 2025),
(5, 3, 1, 1, 2025);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `evaluacion`
--

CREATE TABLE `evaluacion` (
  `id` bigint(20) NOT NULL,
  `fecha` date NOT NULL,
  `hora` time NOT NULL,
  `descripcion` text DEFAULT NULL,
  `curso_asignatura_docente_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `evaluacion`
--

INSERT INTO `evaluacion` (`id`, `fecha`, `hora`, `descripcion`, `curso_asignatura_docente_id`) VALUES
(1, '2025-11-05', '09:00:00', 'Prueba de Números hasta el 20', 1),
(2, '2025-11-05', '14:00:00', 'Control de Lectura: \"El Principito\"', 2),
(3, '2025-11-08', '10:00:00', 'Evaluación de Sumas y Restas', 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `rol`
--

CREATE TABLE `rol` (
  `id_Rol` int(11) NOT NULL,
  `rol` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `rol`
--

INSERT INTO `rol` (`id_Rol`, `rol`) VALUES
(1, 'Director'),
(2, 'Docente'),
(3, 'Alumno');

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `tarea`
--

CREATE TABLE `tarea` (
  `id` bigint(20) NOT NULL,
  `titulo` varchar(255) NOT NULL,
  `descripcion` text DEFAULT NULL,
  `fecha` date NOT NULL,
  `hora` time DEFAULT NULL,
  `alumno_id` int(11) NOT NULL,
  `curso_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `tarea`
--

INSERT INTO `tarea` (`id`, `titulo`, `descripcion`, `fecha`, `hora`, `alumno_id`, `curso_id`) VALUES
(1, 'Estudiar tablas de multiplicar', 'Repasar tablas del 1 al 5', '2025-11-04', '15:00:00', 2, 1),
(2, 'Leer capítulo 3', 'Leer y hacer resumen del capítulo 3 del libro', '2025-11-06', '09:00:00', 2, 1);

-- --------------------------------------------------------

--
-- Estructura de tabla para la tabla `usuario`
--

CREATE TABLE `usuario` (
  `id` int(11) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `apellido_paterno` varchar(255) NOT NULL,
  `apellido_materno` varchar(255) NOT NULL,
  `correo_electronico` varchar(255) NOT NULL,
  `contraseña` varchar(255) NOT NULL,
  `id_Rol` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Volcado de datos para la tabla `usuario`
--

INSERT INTO `usuario` (`id`, `nombre`, `apellido_paterno`, `apellido_materno`, `correo_electronico`, `contraseña`, `id_Rol`) VALUES
(1, '', '', '', 'profesor@gmail.com', '123', 2),
(2, '', '', '', 'alumno@gmail.com', '321', 3);

--
-- Índices para tablas volcadas
--

--
-- Indices de la tabla `asignatura`
--
ALTER TABLE `asignatura`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indices de la tabla `curso`
--
ALTER TABLE `curso`
  ADD PRIMARY KEY (`id`);

--
-- Indices de la tabla `curso_alumno`
--
ALTER TABLE `curso_alumno`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unico_curso_alumno_anio` (`curso_id`,`alumno_id`,`anio_escolar`),
  ADD KEY `idx_curso` (`curso_id`),
  ADD KEY `idx_alumno` (`alumno_id`);

--
-- Indices de la tabla `curso_asignatura_docente`
--
ALTER TABLE `curso_asignatura_docente`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `unico_curso_asignatura_anio` (`curso_id`,`asignatura_id`,`anio_escolar`),
  ADD KEY `idx_curso` (`curso_id`),
  ADD KEY `idx_asignatura` (`asignatura_id`),
  ADD KEY `idx_docente` (`docente_id`);

--
-- Indices de la tabla `evaluacion`
--
ALTER TABLE `evaluacion`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_fecha` (`fecha`),
  ADD KEY `idx_cad` (`curso_asignatura_docente_id`);

--
-- Indices de la tabla `rol`
--
ALTER TABLE `rol`
  ADD PRIMARY KEY (`id_Rol`);

--
-- Indices de la tabla `tarea`
--
ALTER TABLE `tarea`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_alumno` (`alumno_id`),
  ADD KEY `idx_curso` (`curso_id`),
  ADD KEY `idx_fecha` (`fecha`);

--
-- Indices de la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_Rol` (`id_Rol`);

--
-- AUTO_INCREMENT de las tablas volcadas
--

--
-- AUTO_INCREMENT de la tabla `asignatura`
--
ALTER TABLE `asignatura`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT de la tabla `curso`
--
ALTER TABLE `curso`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT de la tabla `curso_alumno`
--
ALTER TABLE `curso_alumno`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT de la tabla `curso_asignatura_docente`
--
ALTER TABLE `curso_asignatura_docente`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT de la tabla `evaluacion`
--
ALTER TABLE `evaluacion`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT de la tabla `tarea`
--
ALTER TABLE `tarea`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- Restricciones para tablas volcadas
--

--
-- Filtros para la tabla `curso_alumno`
--
ALTER TABLE `curso_alumno`
  ADD CONSTRAINT `ca_alumno_fk` FOREIGN KEY (`alumno_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `ca_curso_fk` FOREIGN KEY (`curso_id`) REFERENCES `curso` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `curso_asignatura_docente`
--
ALTER TABLE `curso_asignatura_docente`
  ADD CONSTRAINT `cad_asignatura_fk` FOREIGN KEY (`asignatura_id`) REFERENCES `asignatura` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `cad_curso_fk` FOREIGN KEY (`curso_id`) REFERENCES `curso` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `cad_docente_fk` FOREIGN KEY (`docente_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `evaluacion`
--
ALTER TABLE `evaluacion`
  ADD CONSTRAINT `eval_cad_fk` FOREIGN KEY (`curso_asignatura_docente_id`) REFERENCES `curso_asignatura_docente` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `tarea`
--
ALTER TABLE `tarea`
  ADD CONSTRAINT `tarea_alumno_fk` FOREIGN KEY (`alumno_id`) REFERENCES `usuario` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `tarea_curso_fk` FOREIGN KEY (`curso_id`) REFERENCES `curso` (`id`) ON DELETE CASCADE;

--
-- Filtros para la tabla `usuario`
--
ALTER TABLE `usuario`
  ADD CONSTRAINT `usuario_ibfk_1` FOREIGN KEY (`id_Rol`) REFERENCES `rol` (`id_Rol`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
