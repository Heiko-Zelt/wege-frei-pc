CREATE TABLE PUBLIC.PHOTOS (
    PATH VARCHAR(512) NOT NULL,
    DATETIME TIMESTAMP(6),
    HASH BINARY(20),
    LATITUDE FLOAT(53),
    CONSTRAINT PK_PHOTOS_PATH PRIMARY KEY (PATH)
);

CREATE TABLE PUBLIC.EMAIL_ADDRESSES (
    ADDRESS VARCHAR(255) NOT NULL,
    NAME VARCHAR(255),
    CONSTRAINT PK_EMAIL_ADDRESSES_ADDRESS PRIMARY KEY (ADDRESS),
    CONSTRAINT UNIQUE_EMAIL_ADDRESSES_NAME UNIQUE (NAME)
);

CREATE TABLE PUBLIC.NOTICES (
    ID INT NOT NULL,
    COLOR VARCHAR(255),
    COUNTRYSYMBOL VARCHAR(3),
    LICENSEPLATE VARCHAR(255),
    LOCATIONDESCRIPTION VARCHAR(255),
    NOTE VARCHAR(255),
    OFFENSE VARCHAR(255),
    QUARTER VARCHAR(255),
    RECIPIENTEMAILADDRESS VARCHAR(255),
    RECIPIENTNAME VARCHAR(255),
    STREET VARCHAR(255),
    TOWN VARCHAR(255),
    VEHICLEMAKE VARCHAR(255),
    VEHICLETYPE VARCHAR(255),
    ZIPCODE VARCHAR(255),
    OBSERVATIONTIME TIMESTAMP(6),
    ENDTIME TIMESTAMP(6),
    CREATEDTIME TIMESTAMP(6),
    FINALIZEDTIME TIMESTAMP(6),
    SENTTIME TIMESTAMP(6),
    LATITUDE FLOAT(53),
    LONGITUDE FLOAT(53),
    ENDANGERING BOOLEAN,
    ENVIRONMENTALSTICKERMISS BOOLEAN,
    OBSTRUCTION BOOLEAN,
    VEHICLEABANDONED BOOLEAN,
    VEHICLEINSPECTIONEXPIRED BOOLEAN,
    WARNINGLIGHTS BOOLEAN,
    VEHICLEINSPECTIONMONTH TINYINT,
    VEHICLEINSPECTIONYEAR SMALLINT,
    MESSAGEID BINARY(20),
    SENDFAILURES INT,
    DELIVERYTYPE CHAR(1),
    CONSTRAINT PK_NOTICES_ID PRIMARY KEY (ID)
);

CREATE TABLE PUBLIC.NOTICES_PHOTOS (
    NOTICE_ID INT NOT NULL,
    PHOTO_PATH VARCHAR(512) NOT NULL,
    CONSTRAINT FK_NOTICES_ID FOREIGN KEY (NOTICE_ID) REFERENCES PUBLIC.NOTICES(ID),
    CONSTRAINT FK_PHOTOS_PATH FOREIGN KEY (PHOTO_PATH) REFERENCES PUBLIC.PHOTOS(PATH)
);

ALTER TABLE PUBLIC.NOTICES_PHOTOS
    ADD CONSTRAINT PK_NOTICES_PHOTOS PRIMARY KEY (NOTICE_ID, PHOTO_PATH);