-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- ホスト: 127.0.0.1
-- 生成日時: 2025-12-04 05:01:38
-- サーバのバージョン： 10.4.32-MariaDB
-- PHP のバージョン: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- データベース: `library_db`
--

-- --------------------------------------------------------

--
-- テーブルの構造 `admintb`
--

DROP TABLE IF EXISTS `admintb`;
CREATE TABLE `admintb` (
  `admin_id` bigint(20) NOT NULL,
  `admin_email` varchar(255) DEFAULT NULL,
  `admin_password` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- テーブルの構造 `authortb`
--

DROP TABLE IF EXISTS `authortb`;
CREATE TABLE `authortb` (
  `author_id` bigint(20) NOT NULL,
  `author_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- テーブルのデータのダンプ `authortb`
--

INSERT INTO `authortb` (`author_id`, `author_name`) VALUES
(1, 'バーモンド田中'),
(2, '勝山走舞'),
(3, '負山歩退'),
(4, '勝山歩散'),
(5, '負山走退'),
(6, '勝山走散'),
(7, '荒KIKIMI'),
(8, '負海走舞'),
(9, '勝海歩退'),
(10, '負海歩散');

-- --------------------------------------------------------

--
-- テーブルの構造 `booktb`
--

DROP TABLE IF EXISTS `booktb`;
CREATE TABLE `booktb` (
  `id` bigint(20) NOT NULL,
  `author_id` bigint(20) DEFAULT NULL,
  `author_name` varchar(255) DEFAULT NULL,
  `available_copies` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `is_update` bit(1) NOT NULL,
  `isbn` varchar(255) DEFAULT NULL,
  `sub_name` varchar(255) DEFAULT NULL,
  `title` varchar(255) DEFAULT NULL,
  `total_copies` int(11) NOT NULL,
  `janre_id` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- テーブルのデータのダンプ `booktb`
--

INSERT INTO `booktb` (`id`, `author_id`, `author_name`, `available_copies`, `description`, `is_update`, `isbn`, `sub_name`, `title`, `total_copies`, `janre_id`) VALUES
(1, 1, 'バーモンド田中', 4, '不快値数による、社会心理学に基づいた研究結果をまとめた本です', b'0', '978-3-16-148410-0', '0', '不快値数', 4, '004'),
(2, 2, '勝山走舞', 5, '舞台、映画、テレビと芸能界の激変期を駆け抜け、数多の歓喜と絶望を享受しながらも、芝居だけに生きてきた男たち。', b'0', '978-0-306-40615-7', '0', '君のこと', 5, '009'),
(3, 3, '負山歩退', 6, '東京駅から飛騨へと旅立つが―。出会うことのない二人の出逢い。少女と少年の奇跡の物語、運命が動き出す', b'0', '978-1-4028-9462-6', '0', '君の名は', 6, '010'),
(4, 4, '勝山歩散', 4, 'javaを始めたい人必見!!', b'0', '978-0-8264-2911-3', '0', 'java基礎', 4, '003'),
(5, 5, '負山走退', 3, '何の変哲もない専門学生のそらくん、しかしゲームで運命の人との出会い、睡眠時間を引き換えに女の子と出会う旅', b'0', '978-1-84356-028-9', '0', 'そら君', 3, '005'),
(6, 6, '勝山走散', 4, 'ある日家族が解散し、家がなくった中学生どう生き延びていくのでしょうか', b'0', '978-0-9752298-0-1', '0', 'ホームレス中学生', 4, '004'),
(7, 7, '荒KIKIMI', 5, '孤島に生息するsora人の生活ドキュメンタリー小説', b'0', '978-0-471-48648-0', '0', 'soranji', 5, '006'),
(8, 8, '負海走舞', 5, '幼少期の記憶とともに語られる、懐かしくて優しい「めん料理」と家族の物語', b'0', '978-3-540-49697-3', '0', 'たいたいめん', 5, '006'),
(9, 9, '勝海歩退', 6, 'ある老紳士の人生を辿る感動の回顧録。歩んだ軌跡が静かに胸を打つ', b'0', '978-0-674-00661-3', '0', '歩み', 6, '004'),
(10, 10, '負海歩散', 6, '空を自由に飛び回る不思議な生き物“そらちん”と少年の成長ファンタジー', b'0', '978-81-7992-162-3', '0', 'そらちん', 6, '001');

-- --------------------------------------------------------

--
-- テーブルの構造 `janretb`
--

DROP TABLE IF EXISTS `janretb`;
CREATE TABLE `janretb` (
  `janre_id` varchar(255) NOT NULL,
  `janre_name` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- テーブルのデータのダンプ `janretb`
--

INSERT INTO `janretb` (`janre_id`, `janre_name`) VALUES
('001', 'ちょいエロ'),
('002', 'ホラー'),
('003', '恋愛'),
('004', 'アクション'),
('005', 'ファンタジー'),
('006', 'SF'),
('007', 'ルポルタージュ'),
('008', 'エッセイ'),
('009', '経営'),
('010', 'マーケティング');

-- --------------------------------------------------------

--
-- テーブルの構造 `loantb`
--

DROP TABLE IF EXISTS `loantb`;
CREATE TABLE `loantb` (
  `id` bigint(20) NOT NULL,
  `book_id` bigint(20) DEFAULT NULL,
  `loanday` date DEFAULT NULL,
  `member_id` bigint(20) DEFAULT NULL,
  `rem_flag` bit(1) DEFAULT NULL,
  `return_day` date DEFAULT NULL,
  `s_return` date DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- テーブルのデータのダンプ `loantb`
--

INSERT INTO `loantb` (`id`, `book_id`, `loanday`, `member_id`, `rem_flag`, `return_day`, `s_return`) VALUES
(1, 1, '2025-10-23', 1, NULL, '2025-10-23', '2025-11-20'),
(2, 10, '2025-10-23', 1, NULL, '2025-10-23', '2025-11-20'),
(3, 1, '2025-10-23', 1, NULL, '2025-10-23', '2025-11-20'),
(4, 10, '2025-10-23', 10, NULL, '2025-10-23', '2025-11-20'),
(5, 10, '2025-10-23', 10, NULL, '2025-10-23', '2025-11-20'),
(6, 8, '2025-10-23', 10, b'0', '2025-10-23', '2025-11-20'),
(7, 10, '2025-10-23', 10, b'0', '2025-10-23', '2025-11-20'),
(8, 10, '2025-10-23', 10, b'0', '2025-10-23', '2025-11-20'),
(9, 10, '2025-10-23', 13, b'0', '2025-10-23', '2025-11-20');

-- --------------------------------------------------------

--
-- テーブルの構造 `membertb`
--

DROP TABLE IF EXISTS `membertb`;
CREATE TABLE `membertb` (
  `member_id` bigint(20) NOT NULL,
  `address` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL,
  `nickname` varchar(255) DEFAULT NULL,
  `password` varchar(255) DEFAULT NULL,
  `phone` varchar(255) DEFAULT NULL,
  `profile_mail_sent` varchar(255) DEFAULT NULL,
  `reg_mail_sent` bit(1) NOT NULL,
  `zipcode` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- テーブルのデータのダンプ `membertb`
--

INSERT INTO `membertb` (`member_id`, `address`, `email`, `nickname`, `password`, `phone`, `profile_mail_sent`, `reg_mail_sent`, `zipcode`) VALUES
(2, '福岡県 宮若市 龍徳234', 'miyasita34@gmail.com', 'みや', 'miya2345', '090-4434-5937', '000000', b'0', '823-0001'),
(3, '東京都千代田区千代田', 'hikaru849@gmail.com', 'ひか', 'hika1234', '090-3421-7934', '000000', b'0', '100-0000'),
(4, '富山県射水市高場新町9-9', 'kimutaku890@gmail.com', 'たく', 'kimu0984', '070-4434-3456', '000000', b'0', '934-0034'),
(5, '北海道深川市太子町23', 'mattyan38@gmail.com', '松田', 'mattya1234', '070-8954-8885', '000000', b'0', '074-0023'),
(6, '岩手県奥州市水沢羽田町向畑5', 'bakabakaba9@gmail.com', 'バカ', 'bakaba54', '070-6794-3656', '000000', b'0', '023-0108'),
(7, '新潟県 十日町市儀明3-4-2', 'butanosuke@gmail.com', '豚', 'buta2452', '090-4434-3434', '000000', b'0', '942-1563'),
(8, '岡山県倉敷市玉島服部498', 'yusuke453@gmail.com', 'ゆう', 'yuu09284', '080-4434-3456', '000000', b'0', '713-8111'),
(9, '埼玉県飯脳市仲町857', 'tamanoura2@gmail.com', '玉裏', 'tama9853', '090-4344-3456', '000000', b'0', '357-0038'),
(11, '3', 'kaikai@kaitiku', 'kaikai', 'kaitiku', '5', NULL, b'1', '443'),
(12, '4', 'test@user', 'kaitiku', 'user', '4', NULL, b'1', '2'),
(13, '3', 'makisimamu@makisimamu', 'makisimamu', 'makisimamu', '3', NULL, b'1', '33'),
(14, '1', 'gaizi@gaizi', 'gagainogai', 'gagainogai', '1', 'UIXYZZ', b'0', '1'),
(15, '11', 'gaizi@gaizidesu', 'gaizi', 'gagainogai', '22', NULL, b'1', '1'),
(16, '4', 'omaenokotodaregasukinan@sosina', 'sosina', 'sosina', '4', NULL, b'1', '4'),
(18, '1', 'mubiko@shchiba.uk', 'mubikonnn', 'mubiko', '1', NULL, b'1', '1');

--
-- ダンプしたテーブルのインデックス
--

--
-- テーブルのインデックス `admintb`
--
ALTER TABLE `admintb`
  ADD PRIMARY KEY (`admin_id`);

--
-- テーブルのインデックス `authortb`
--
ALTER TABLE `authortb`
  ADD PRIMARY KEY (`author_id`);

--
-- テーブルのインデックス `booktb`
--
ALTER TABLE `booktb`
  ADD PRIMARY KEY (`id`);

--
-- テーブルのインデックス `janretb`
--
ALTER TABLE `janretb`
  ADD PRIMARY KEY (`janre_id`);

--
-- テーブルのインデックス `loantb`
--
ALTER TABLE `loantb`
  ADD PRIMARY KEY (`id`);

--
-- テーブルのインデックス `membertb`
--
ALTER TABLE `membertb`
  ADD PRIMARY KEY (`member_id`);

--
-- ダンプしたテーブルの AUTO_INCREMENT
--

--
-- テーブルの AUTO_INCREMENT `booktb`
--
ALTER TABLE `booktb`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- テーブルの AUTO_INCREMENT `loantb`
--
ALTER TABLE `loantb`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- テーブルの AUTO_INCREMENT `membertb`
--
ALTER TABLE `membertb`
  MODIFY `member_id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=19;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
