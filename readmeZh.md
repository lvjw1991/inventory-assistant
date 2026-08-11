# plan
## 第1周：项目基础 + 主数据
## 第2周：核心业务
## 第3周：库存 + 临期 + 企业级

一个面向零售门店的商品有效期管理系统，用于补充现有 POS 系统无法维护商品有效期的问题。

系统通过导入 POS 库存、供应商来货单以及仓库点货数据，建立商品 Barcode、有效期与库存之间的关联，帮助门店快速发现临期商品，并支持管理员进行有效期维护和处理记录。

---

## 1. 项目介绍

现有 POS 系统主要负责收银、商品和库存管理，但无法维护商品有效期。

本系统作为 POS 的辅助系统，不替代 POS，而是专门解决：

* 商品有效期录入
* 来货点货
* Barcode 与供应商货号映射
* 有效期库存管理
* 临期商品查询
* 临期商品处理记录
* POS 库存数据导入

### 核心业务流程

```text
供应商货单 PDF
      ↓
Excel 转换
      ↓
导入来货单
      ↓
仓库手机点货
      ↓
录入 Barcode + 有效期
      ↓
完成点货
      ↓
生成有效期记录
      ↓
每月月初导入 POS 库存
      ↓
查询下月临期商品并确认是否存在再进行处理
      ↓
正常销售 / 打折等处理
```

---

## 2. 系统架构

系统采用前后端分离架构。

```text
                    ┌──────────────────┐
                    │     Browser      │
                    │                  │
                    │ PC 管理后台       │
                    │ 手机点货,临时处理  │
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

外部数据
    │
    ├── POS 库存 Excel
    │
    └── 供应商货单 Excel
```

### PC端管理后台

用于：

* 供应商管理
* 来货单管理
* 有效期管理
* 临期商品查询
* 有效期处理
* 库存数据导入
* 点货流程

### 手机端 //todo

通过浏览器访问，不需要安装 App。

主要用于：

* 查询待点货商品
* 扫描 Barcode
* 录入有效期
* 修改错误数据
* 查看点货进度
* 临期商品查询

---

## 3. 技术栈

### Backend

* Java 17
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

## 4. 数据库设计

系统主要包含以下核心数据。

### Supplier

供应商。

```text
supplier
├── id
├── name
└── ...
```

### Product

商品主数据，以 Barcode 为核心。

```text
product
├── id
├── barcode
├── name
├── imgUrl
└── ...
```

Barcode唯一。

### Supplier Product

供应商货号与 Barcode 的映射。

```text
supplier_product
├── id
├── supplier_id
├── supplier_code
└── barcode
```

一个供应商货号可以对应多个 Barcode：

```text
Supplier Code
      │
      ├── Barcode A
      ├── Barcode B
      └── Barcode C
```

### Receiving Order

一次供应商来货。

```text
receiving_order
├── id
├── supplier_id
└── status
```

来货单可能需要数天完成点货，因此通过状态管理整个点货过程。

```text
READY
  ↓
CHECKING
  ↓
COMPLETED
```

### Receiving Order Item

供应商货单中的商品明细，同时保存仓库实际点货结果。

```text
receiving_order_item
├── id
├── receiving_order_id
├── supplier_code
├── barcode
├── expiry_dates
└── ...
```

点货期间可以反复修改 Barcode 和有效期。

完成点货后，再统一生成正式有效期数据和code映射关系。

### Expiry Record

系统核心业务表。

```text
expiry_record
├── id
├── barcode
├── expiry_date
├── stock_qty
├── confirmed
└── processed
```

核心唯一约束：

```text
UNIQUE (
    barcode,
    expiry_date
)
```

因此：

```text
690001 + 2026-08-15
```

只能存在一条有效期记录。

---

## 5. API

系统采用 RESTful API。

### Authentication

```http
POST /api/auth/login
```

返回 JWT Token。

后续请求：

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

支持分页查询。

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

将供应商货单 Excel 导入来货明细。

### Check / Receiving

```http
GET /api/receiving-orders/{id}/items
PUT /api/receiving-orders/items/{id}/check
```

电脑/手机端通过该接口完成：

```text
Barcode
+
Expiry Dates
```

的录入和修改。

### Complete Receiving

```http
POST /api/receiving-orders/{id}/complete
```

完成点货后,统一生成code对应关系和有效期记录

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

管理员可以在后台修改错误的 Barcode、有效期以及处理状态。

---

## 6. Docker  //fixme

系统使用 Docker 进行部署。

核心服务：

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

启动：

```bash
docker compose up -d
```

查看运行状态：

```bash
docker compose ps
```

停止：

```bash
docker compose down
```

---

## 7. 项目截图

### PC 管理后台

#### Dashboard //todo

> TODO: 添加 Dashboard 截图

#### 来货单管理

<img width="1920" height="1008" alt="image" src="https://github.com/user-attachments/assets/96d739ff-999a-4aee-ae4d-9908504d879c" />


#### 来货单详情

<img width="1920" height="1008" alt="image" src="https://github.com/user-attachments/assets/5bb77c57-53a0-403a-a98d-98cf4461d647" />

#### 点货列表

<img width="1920" height="1008" alt="image" src="https://github.com/user-attachments/assets/77f78855-8369-499f-8cb7-65fa4698b154" />


#### 有效期管理

<img width="1920" height="1008" alt="image" src="https://github.com/user-attachments/assets/c6050cad-7af3-4ded-a8b3-89bb5b37743f" />


### 手机点货 //todo

#### 点货列表

> TODO: 添加手机点货列表截图

#### Barcode 扫描

> TODO: 添加 Barcode 扫描截图

#### 有效期录入

> TODO: 添加有效期录入截图

#### 点货进度

> TODO: 添加点货进度截图

---

## 8. 未来计划

### Phase 1 — MVP

当前系统重点完成：

* [x] Supplier 管理
* [x] 商品 Barcode 管理
* [x] Supplier Code → Barcode 映射
* [x] 来货单 Excel 导入
//todo 手机点货
* [x] Barcode 录入
* [x] 有效期录入
* [x] 点货结果修改
* [x] 完成点货后生成有效期记录
* [x] POS 库存导入
* [x] 临期商品查询
* [x] 有效期处理记录
* [x] 正常销售 / 已打折等处理方式

### Phase 2 — Sales Integration

进一步导入 POS 销售数据。

```text
POS Sales
    ↓
Expiry Record
    ↓
库存变化分析
    ↓
临期商品销售速度
```

根据销售速度分析商品是否需要提前打折。

例如：

```text
剩余 7 天
库存 30
最近销量 2 / 天
        ↓
预计无法售完
        ↓
建议打折
```

### Phase 3 — Intelligent Discount Recommendation

根据：

* 剩余有效期
* 当前库存
* 最近销量
* 商品毛利率
* 历史销售速度

自动计算临期风险，并给出：

```text
无需处理
    ↓
建议关注
    ↓
建议打折
    ↓
高风险
```

### Phase 4 — POS Integration

进一步探索与 POS 系统的数据接口，实现：

* 自动获取库存
* 自动获取销售数据
* 自动同步商品
* 减少 Excel 手工导入

---

## Project Goal

本项目不是重新开发一个 POS 或 ERP，而是作为现有 POS 系统的补充。

核心目标：

> **POS 管理商品、销售和库存，Expiry Management 专门管理商品有效期。**

通过简单的来货点货流程和定期库存更新，让门店能够低成本地建立有效期管理能力，同时为后续销售分析和自动折扣推荐提供数据基础。
