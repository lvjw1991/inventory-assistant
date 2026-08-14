# Expiry Management System

An expiry date management system for retail stores.

The system complements an existing POS system by managing product expiry dates, receiving processes, barcode mappings, and near-expiry inventory.

---

## 1. Project Overview

The existing POS system mainly manages products, sales, and inventory, but does not maintain product expiry dates.

This system is designed as a supporting system rather than a replacement for the POS system.

It focuses on:

* Product expiry date management
* Receiving and warehouse checking
* Supplier code to barcode mapping
* Near-expiry product management
* Inventory import from POS
* Expiry handling and discount records

### Receiving Workflow

```text
Supplier Delivery PDF
        ↓
Manual PDF → Excel conversion
        ↓
Import Excel
        ↓
Receiving Order
        ↓
Warehouse Checking
        ↓
Scan / Enter Barcode
        ↓
Enter Expiry Date
        ↓
Complete Checking
        ↓
Generate Supplier Code → Barcode Mapping
        ↓
Generate Expiry Records
```

The supplier delivery document normally contains supplier product codes rather than barcodes.

During warehouse checking, the actual barcode and expiry date are recorded.

The actual barcode is treated as the authoritative product identifier because it is also used for comparison with the POS system.

---

## 2. System Architecture

The system uses a frontend-backend separated architecture.

```text
                    ┌──────────────────┐
                    │     Browser      │
                    │                  │
                    │ PC Admin Panel   │
                    │ Mobile Checking  │
                    └────────┬─────────┘
                             │ HTTP / REST
                             ↓
                    ┌──────────────────┐
                    │   Spring Boot    │
                    │     REST API     │
                    ├──────────────────┤
                    │ Controller       │
                    │ Service          │
                    │ Repository       │
                    │ Security / JWT   │
                    └────────┬─────────┘
                             │
                             ↓
                    ┌──────────────────┐
                    │      MySQL       │
                    └──────────────────┘

External Data
    │
    ├── POS Inventory Excel
    │
    └── Supplier Delivery Excel
```

### PC Admin Panel

The PC interface is used for:

* Supplier management
* Receiving order management
* Expiry management
* Near-expiry product queries
* Expiry handling
* POS inventory import
* Receiving and checking management

### Mobile Checking

The mobile interface is browser-based and does not require a native mobile application.

It is mainly used for:

* Searching products to be checked
* Scanning barcodes
* Entering expiry dates
* Correcting checking data
* Viewing checking progress

---

## 3. Technology Stack

### Backend

* Java 21
* Spring Boot
* Spring MVC
* Spring Data JPA
* Spring Security
* JWT
* Hibernate
* MySQL
* Maven

### Frontend

* Vue 3
* TypeScript
* Element Plus
* Axios

### Testing

* JUnit 5
* Spring Boot Test
* Mockito
* Postman

### Deployment

* Docker
* Docker Compose

---

## 4. Database Design

The system contains the following core entities.

### Supplier

Stores supplier information.

```text
supplier
├── id
├── name
└── ...
```

### Product

Stores the product master data.

Barcode is the unique product identifier.

```text
product
├── id
├── barcode
├── name
├── imgUrl
└── ...
```

The same product name with different barcodes is treated as separate product records.

### Supplier Product

Stores the mapping between a supplier product code and an actual barcode.

```text
supplier_product
├── id
├── supplier_id
├── supplier_code
└── barcode
```

One supplier product code can correspond to multiple barcodes:

```text
Supplier Code
      │
      ├── Barcode A
      ├── Barcode B
      └── Barcode C
```

The mapping is created during warehouse checking based on the actual barcode.

### Receiving Order

Represents one supplier delivery.

```text
receiving_order
├── id
├── supplier_id
└── status
```

A receiving order may take several days to complete, so its progress is managed through a status:

```text
READY
  ↓
CHECKING
  ↓
COMPLETED
```

### Receiving Order Item

Stores the original supplier delivery item together with the actual warehouse checking results.

```text
receiving_order_item
├── id
├── receiving_order_id
├── supplier_code
├── barcode
├── expiry_dates
└── ...
```

The barcode and expiry dates can be modified during the checking process.

After checking is completed, the system generates the supplier code-to-barcode mapping and expiry records.

### Expiry Record

The expiry record is the core business table.

```text
expiry_record
├── id
├── barcode
├── expiry_date
├── stock_qty
├── confirmed
└── processed
```

A unique constraint is applied to the combination of barcode and expiry date:

```sql
UNIQUE (
    barcode,
    expiry_date
)
```

Therefore, the following combination can only exist once:

```text
690001 + 2026-08-15
```

---

## 5. API

The system provides RESTful APIs.

Swagger UI:

```text
/swagger-ui/index.html
```

### Authentication

```http
POST /api/auth/login
```

Returns a JWT token.

Subsequent requests use:

```http
Authorization: Bearer {token}
```

### Supplier

```http
GET    /api/suppliers
GET    /api/suppliers/{id}
POST   /api/suppliers
PUT    /api/suppliers/{id}
DELETE /api/suppliers/{id}
```

Supplier list queries support pagination.

### Receiving Order

```http
GET  /api/receiving-orders
POST /api/receiving-orders
GET  /api/receiving-orders/{id}
```

### Excel Import

```http
POST /api/receiving-orders/{id}/import
```

Imports supplier delivery data from Excel into the receiving order items.

### Checking / Receiving

```http
GET /api/receiving-orders/{id}/items
PUT /api/receiving-orders/items/{id}/check
```

The PC or mobile interface uses these APIs to enter and modify:

```text
Barcode
+
Expiry Dates
```

### Complete Receiving

```http
POST /api/receiving-orders/{id}/complete
```

After checking is completed, the system generates the supplier code-to-barcode mapping and expiry records.

```text
ReceivingOrderItem
        ↓
SupplierProduct
        ↓
ExpiryRecord
```

### Expiry Management

```http
GET /api/expiry-records
PUT /api/expiry-records/{id}
```

Administrators can modify incorrect barcodes, expiry dates, and expiry handling information.

---

## 6. Docker //fixme

The system uses Docker for deployment.

The deployment environment includes:

```text
docker-compose
│
├── backend
│   └── Spring Boot
│
├── frontend
│   └── Vue
│
└── mysql
    └── MySQL 8
```

Start the application:

```bash
docker compose up -d
```

Check the running services:

```bash
docker compose ps
```

Stop the application:

```bash
docker compose down
```

---

## 7. Screenshots

### PC Admin Panel

#### Dashboard

> TODO: Add Dashboard screenshot

#### Receiving Order Management

<img width="1920" height="1008" alt="Receiving Order Management" src="https://github.com/user-attachments/assets/96d739ff-999a-4aee-ae4d-9908504d879c" />

#### Receiving Order Details

<img width="1920" height="1008" alt="Receiving Order Details" src="https://github.com/user-attachments/assets/5bb77c57-53a0-403a-a98d-98cf4461d647" />

#### Receiving / Checking List

<img width="1920" height="1008" alt="Receiving Checking List" src="https://github.com/user-attachments/assets/77f78855-8369-499f-8cb7-65fa4698b154" />

#### Expiry Management

<img width="1920" height="1008" alt="Expiry Management" src="https://github.com/user-attachments/assets/c6050cad-7af3-4ded-a8b3-89bb5b37743f" />

### Mobile Checking 

#### Order List

<img width="761" height="863" alt="image" src="https://github.com/user-attachments/assets/aa5689d2-43df-401e-b1de-75cbfab06dc5" />

#### Checking List

<img width="789" height="857" alt="image" src="https://github.com/user-attachments/assets/e06ce5e3-9c60-4d46-a3f2-4957b1aad267" />

#### Checking detail

<img width="608" height="878" alt="image" src="https://github.com/user-attachments/assets/59ba5d4b-f71b-41cf-837f-b3234aac200a" />

#### Expiry handling

<img width="569" height="864" alt="image" src="https://github.com/user-attachments/assets/1203c85b-60d8-4464-81a4-473cd88a8513" />

---

## 8. Future Plans

### Phase 1 — MVP

The current MVP includes:

* [x] Supplier management
* [x] Product barcode management
* [x] Supplier Code → Barcode mapping
* [x] Supplier delivery Excel import
* [x] Barcode entry
* [x] Expiry date entry
* [x] Checking result modification
* [x] Generate expiry records after checking is completed
* [x] POS inventory import
* [x] Near-expiry product queries
* [x] Expiry handling records
* [x] Normal sale / discounted handling

### Phase 2 — Sales Integration

Import POS sales data to provide sales analysis for expiry records.

```text
POS Sales
    ↓
Expiry Records
    ↓
Inventory Change Analysis
    ↓
Near-Expiry Sales Velocity
```

The system can use sales velocity to determine whether a product should be discounted earlier.

For example:

```text
7 days remaining
30 units in stock
Recent sales: 2 units/day
        ↓
Expected to remain unsold
        ↓
Discount Recommended
```

### Phase 3 — Intelligent Discount Recommendation

Analyze:

* Remaining shelf life
* Current inventory
* Recent sales
* Product gross margin
* Historical sales velocity

The system can then classify expiry risk:

```text
No Action Required
        ↓
Monitor
        ↓
Discount Recommended
        ↓
High Risk
```

### Phase 4 — POS Integration

Further integrate with the POS system to reduce manual Excel-based data exchange.

Potential features include:

* Automatic inventory synchronization
* Automatic sales data synchronization
* Automatic product synchronization
* Reduced manual Excel imports

---

## Project Goal

This project is not intended to replace a POS or ERP system.

Instead, it works as a dedicated expiry management system alongside an existing POS system.

```text
POS System
    ↓
Products / Sales / Inventory

Expiry Management System
    ↓
Expiry Dates / Near-Expiry Inventory / Expiry Handling
```

The goal is to provide a simple and practical way for retail stores to manage product expiry dates while keeping the existing POS system as the source of product and inventory data.
