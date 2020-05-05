-- phpMyAdmin SQL Dump
-- version 5.0.2
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Creato il: Mag 05, 2020 alle 16:33
-- Versione del server: 10.4.11-MariaDB
-- Versione PHP: 7.4.4

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `justmeet`
--

-- --------------------------------------------------------

--
-- Struttura della tabella `categoriesdb`
--

CREATE TABLE `categoriesdb` (
  `id` int(11) NOT NULL,
  `nome` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Struttura della tabella `eventsdb`
--

CREATE TABLE `eventsdb` (
  `id` int(11) NOT NULL,
  `categoria` int(11) NOT NULL,
  `titolo` varchar(50) NOT NULL,
  `descrizione` text NOT NULL,
  `citta` varchar(30) NOT NULL,
  `via` varchar(50) NOT NULL,
  `data` date NOT NULL,
  `oraInizio` time NOT NULL,
  `oraFine` time NOT NULL,
  `prezzo` float NOT NULL,
  `minPartecipanti` int(11) UNSIGNED NOT NULL,
  `maxPartecipanti` int(11) UNSIGNED NOT NULL,
  `emailOrganizzatore` varchar(50) NOT NULL,
  `chiuso` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Struttura della tabella `partecipantsdb`
--

CREATE TABLE `partecipantsdb` (
  `emailUtente` varchar(50) NOT NULL,
  `idEvento` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Struttura della tabella `reviewsdb`
--

CREATE TABLE `reviewsdb` (
  `id` int(11) NOT NULL,
  `voto` int(11) NOT NULL,
  `descrizione` varchar(500) NOT NULL,
  `emailRecensore` varchar(50) NOT NULL,
  `emailRecensito` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- --------------------------------------------------------

--
-- Struttura della tabella `userdb`
--

CREATE TABLE `userdb` (
  `email` varchar(50) NOT NULL,
  `password` varchar(50) NOT NULL,
  `nome` varchar(30) NOT NULL,
  `cognome` varchar(30) NOT NULL,
  `eta` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='Tabella contenente i dati relativi all''utente';

--
-- Indici per le tabelle scaricate
--

--
-- Indici per le tabelle `categoriesdb`
--
ALTER TABLE `categoriesdb`
  ADD PRIMARY KEY (`id`);

--
-- Indici per le tabelle `eventsdb`
--
ALTER TABLE `eventsdb`
  ADD PRIMARY KEY (`id`),
  ADD KEY `categoria esterna` (`categoria`),
  ADD KEY `email esterna` (`emailOrganizzatore`);

--
-- Indici per le tabelle `partecipantsdb`
--
ALTER TABLE `partecipantsdb`
  ADD PRIMARY KEY (`emailUtente`,`idEvento`),
  ADD KEY `id evento` (`idEvento`);

--
-- Indici per le tabelle `reviewsdb`
--
ALTER TABLE `reviewsdb`
  ADD PRIMARY KEY (`id`),
  ADD KEY `email recensito` (`emailRecensito`),
  ADD KEY `email recensore` (`emailRecensore`);

--
-- Indici per le tabelle `userdb`
--
ALTER TABLE `userdb`
  ADD PRIMARY KEY (`email`) USING BTREE;

--
-- AUTO_INCREMENT per le tabelle scaricate
--

--
-- AUTO_INCREMENT per la tabella `categoriesdb`
--
ALTER TABLE `categoriesdb`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `eventsdb`
--
ALTER TABLE `eventsdb`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT per la tabella `reviewsdb`
--
ALTER TABLE `reviewsdb`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT;

--
-- Limiti per le tabelle scaricate
--

--
-- Limiti per la tabella `eventsdb`
--
ALTER TABLE `eventsdb`
  ADD CONSTRAINT `categoria esterna` FOREIGN KEY (`categoria`) REFERENCES `categoriesdb` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `email esterna` FOREIGN KEY (`emailOrganizzatore`) REFERENCES `userdb` (`email`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `partecipantsdb`
--
ALTER TABLE `partecipantsdb`
  ADD CONSTRAINT `esterna` FOREIGN KEY (`emailUtente`) REFERENCES `userdb` (`email`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `id evento` FOREIGN KEY (`idEvento`) REFERENCES `eventsdb` (`id`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Limiti per la tabella `reviewsdb`
--
ALTER TABLE `reviewsdb`
  ADD CONSTRAINT `email recensito` FOREIGN KEY (`emailRecensito`) REFERENCES `userdb` (`email`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `email recensore` FOREIGN KEY (`emailRecensore`) REFERENCES `userdb` (`email`) ON DELETE CASCADE ON UPDATE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
