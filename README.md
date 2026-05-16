EatMe — Aplicación de Pedidos de Comida Rápida
EatMe es una aplicación de escritorio desarrollada en Java como Proyecto Integrado del Ciclo Formativo de Grado Superior en Desarrollo de Aplicaciones Multiplataforma (DAM). Permite a los clientes consultar el catálogo de productos, gestionar su carrito y realizar pedidos, mientras que el personal dispone de un panel de administración completo.
Tecnologías utilizadas

Java JDK 21
Java Swing + FlatLaf 3.4.1
PostgreSQL (Supabase) + Driver JDBC 42.7.8
Eclipse IDE 2024
Git / GitHub

Requisitos previos

Windows 10 / 11 (64 bits)
Java JDK 17 o superior
Conexión a internet (la base de datos está alojada en Supabase)

Instalación

Clona o descarga el repositorio
Importa el proyecto en Eclipse: File → Import → Existing Projects into Workspace
Añade las librerías externas en Build Path → Configure Build Path → Add External JARs:

postgresql-42.7.x.jar
flatlaf-3.4.1.jar
flatlaf-extras-3.4.1.jar


Ejecuta Main.java

Funcionalidades
Usuario:

Registro e inicio de sesión con validación de correo @gmail.com
Catálogo de productos con gestión de favoritos
Carrito de compra con validación de stock
Historial de pedidos agrupado por sesión con visualización de ticket

Administrador:

Gestión completa de productos, usuarios y pedidos
Exportación de pedidos a PDF
Panel de control con pestañas diferenciadas

Características técnicas

Arquitectura cliente-servidor con Supabase (PostgreSQL en la nube)
Patrón de diseño DAO para separación de lógica SQL y presentación
Patrón Singleton para la gestión de la conexión a base de datos
Bilingüismo ES/EN con ResourceBundle y archivos .properties en UTF-8
Procesos con ProcessBuilder y concurrencia con SwingWorker
Interfaz oscura moderna con animaciones fadeIn y notificaciones Toast

Autor
Iván Liñán Vega — IES Vega de Mijas — 2026

EatMe — Fast Food Order Application
EatMe is a desktop application developed in Java as the Integrated Project for the Higher Degree in Multiplatform Application Development (DAM). It allows customers to browse the product catalogue, manage their cart and place orders, while staff have access to a full administration panel.
Technologies used

Java JDK 21
Java Swing + FlatLaf 3.4.1
PostgreSQL (Supabase) + JDBC Driver 42.7.8
Eclipse IDE 2024
Git / GitHub

Prerequisites

Windows 10 / 11 (64-bit)
Java JDK 17 or higher
Internet connection (database hosted on Supabase)

Installation

Clone or download the repository
Import the project in Eclipse: File → Import → Existing Projects into Workspace
Add the external libraries in Build Path → Configure Build Path → Add External JARs:

postgresql-42.7.x.jar
flatlaf-3.4.1.jar
flatlaf-extras-3.4.1.jar


Run Main.java

Features
User:

Registration and login with @gmail.com email validation
Product catalogue with favourites management
Shopping cart with stock validation
Order history grouped by session with ticket view

Admin:

Full management of products, users and orders
Export orders to PDF
Control panel with separate tabs

Technical highlights

Client-server architecture with Supabase (cloud PostgreSQL)
DAO design pattern for separation of SQL logic and presentation
Singleton pattern for database connection management
ES/EN bilingualism with ResourceBundle and UTF-8 .properties files
Processes with ProcessBuilder and concurrency with SwingWorker
Modern dark UI with fadeIn animations and Toast notifications

Author
Iván Liñán Vega — IES Vega de Mijas — 2026
