CREATE TABLE `PocketCampus`.`RecommendedApps` (
  `AppId` INT NOT NULL,
  `AppName` VARCHAR(45) NULL,
  `AppDescription_EN` TEXT NULL,
  PRIMARY KEY (`AppId`),
  UNIQUE INDEX `AppId_UNIQUE` (`AppId` ASC));

CREATE TABLE `PocketCampus`.`RecommendedAppsOSConfigurations` (
  `AppId` INT NOT NULL,
  `AppStore` INT NULL,
  `AppStoreQuery` VARCHAR(45) NULL,
  `AppOpenURLPattern` TEXT NULL,
  `AppLogoURL` TEXT NULL);


CREATE TABLE `PocketCampus`.`RecommendedAppsCategories` (
  `CategoryId` INT NOT NULL,
  `CategoryLogoURL` TEXT NULL,
  `CategoryName_EN` VARCHAR(45) NULL,
  `CategoryDescription_EN` TEXT NULL,
  PRIMARY KEY (`CategoryId`),
  UNIQUE INDEX `CategoryId_UNIQUE` (`CategoryId` ASC));

CREATE TABLE `PocketCampus`.`RecommendedAppsCategoriesToApps` (
  `CategoryId` INT NOT NULL,
  `AppId` INT NOT NULL);

