# Installing the PocketCampus development environment

* Install Ubuntu

* Install the JDK: `apt-get install openjdk-7-sdk`

* Download and extract the ADT bundle in your home directory

* Open Eclipse (from ADT), open the Android SDK Manager, and install API level 8

* Install Git: `apt-get install git`

* Install MySQL: `apt-get install mysql-server`

* Install PHP: `apt-get install php5-cli`

* Clone the PocketCampus repo: `git clone https://github.com/dslab-epfl/pocketcampus.git`

* Run all of the create_database, create_tables and initialize_tables SQL files:  
```
mysql -u root -p < create_database.sql
mysql -u root -p pocketcampus < create_tables.sql
mysql -u root -p pocketcampus < initialize_tables.sql
```

* Copy the sever config file to /etc:  
```
cp platform/platform.server/src/org/pocketcampus/platform/server/launcher/pocketcampus-server.config /etc/
```

* Edit the config file accordingly (e.g. `DB_USERNAME`, `DB_PASSWORD`, `DB_URL`)

* Copy the android config file to /sdcard: 
```
adb push platform/platform.android/res/raw/pocketcampus.config /sdcard/
```

* Edit the android config file (e.g. `SERVER_ADDRESS`)