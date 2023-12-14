    DROP DATABASE IF EXISTS TheQuailRanch;

    CREATE DATABASE IF NOT EXISTS TheQuailRanch;

    USE TheQuailRanch;

    CREATE TABLE IF NOT EXISTS User (
        user_id VARCHAR(10) PRIMARY KEY,
        name VARCHAR(30) NOT NULL,
        pw VARCHAR(8) NOT NULL,
        tel VARCHAR(10) UNIQUE
    );

    CREATE TABLE IF NOT EXISTS Employee (
        emp_id VARCHAR(10) PRIMARY KEY,
        name VARCHAR(30) NOT NULL,
        tel VARCHAR(10) UNIQUE
    );

    CREATE TABLE IF NOT EXISTS Customer (
        cus_id VARCHAR(10) PRIMARY KEY,
        name VARCHAR(30) NOT NULL,
        tel VARCHAR(10) UNIQUE
    );

    CREATE TABLE IF NOT EXISTS Quails (
        ranch_id VARCHAR(10) PRIMARY KEY,
        date DATE NOT NULL,
        category VARCHAR(20) NOT NULL,
        amount_of_birds INT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS Nests (
        nest_id VARCHAR(10) PRIMARY KEY,
        category VARCHAR(20) NOT NULL,
        amount_of_birds INT NOT NULL
    );

    CREATE TABLE IF NOT EXISTS Supplier (
        sup_id VARCHAR(10) PRIMARY KEY,
        name VARCHAR(30) NOT NULL
    );

    CREATE TABLE IF NOT EXISTS Sell_stock (
        sell_stock_id VARCHAR(10) PRIMARY KEY,
        category VARCHAR(20) NOT NULL,
        quantity INT NOT NULL,
        unit_price DECIMAL(10, 2) NOT  NULL
    );

    CREATE TABLE IF NOT EXISTS Farm_stock (
        farm_stock_id VARCHAR(10) PRIMARY KEY,
        sup_id VARCHAR(10) NOT NULL,
        category VARCHAR(20) NOT NULL,
        quantity INT NOT NULL,
        CONSTRAINT FOREIGN KEY(sup_id) REFERENCES Supplier(sup_id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    CREATE TABLE IF NOT EXISTS Salary (
        sal_id VARCHAR(10) PRIMARY KEY,
        emp_id VARCHAR(10) NOT NULL,
        amount DECIMAL(10, 2) NOT NULL,
        paid_date DATE NOT NULL,
        CONSTRAINT FOREIGN KEY(emp_id) REFERENCES Employee(emp_id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    CREATE TABLE IF NOT EXISTS Orders (
        order_id VARCHAR(10) PRIMARY KEY,
        cus_id VARCHAR(10) NOT NULL,
        date DATE NOT NULL,
        CONSTRAINT FOREIGN KEY(cus_id) REFERENCES Customer(cus_id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    CREATE TABLE IF NOT EXISTS Order_details (
        order_id VARCHAR(10) NOT NULL,
        sell_stock_id VARCHAR(10) NOT NULL,
        quantity INT NOT NULL,
        unit_price DECIMAL(10, 2) NOT NULL,
        CONSTRAINT FOREIGN KEY(order_id) REFERENCES Orders(order_id) ON DELETE CASCADE ON UPDATE CASCADE,
        CONSTRAINT FOREIGN KEY(sell_stock_id) REFERENCES Sell_stock(sell_stock_id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    CREATE TABLE IF NOT EXISTS Payment (
        receipt_id VARCHAR(10) PRIMARY KEY,
        cus_id VARCHAR(10) NOT NULL,
        user_id VARCHAR(10) NOT NULL,
        amount DECIMAL(10, 2) NOT NULL,
        date DATE NOT NULL,
        CONSTRAINT FOREIGN KEY(cus_id) REFERENCES Customer(cus_id) ON DELETE CASCADE ON UPDATE CASCADE,
        CONSTRAINT FOREIGN KEY(user_id) REFERENCES User(user_id) ON DELETE CASCADE ON UPDATE CASCADE
    );

    CREATE TABLE IF NOT EXISTS Cleaning_tt (
        tt_id VARCHAR(10) PRIMARY KEY,
        emp_id VARCHAR(10) NOT NULL,
        nest_id VARCHAR(10) NOT NULL,
        date DATE NOT NULL,
        CONSTRAINT FOREIGN KEY(emp_id) REFERENCES Employee(emp_id) ON DELETE CASCADE ON UPDATE CASCADE,
        CONSTRAINT FOREIGN KEY(nest_id) REFERENCES Nests(nest_id) ON DELETE CASCADE ON UPDATE CASCADE
    );





















