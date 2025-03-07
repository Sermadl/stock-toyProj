# 📈 Stock Market

사용자가 가상의 주식을 사고팔며 투자 전략을 연습할 수 있는 웹 애플리케이션입니다. 
Vue 3와 Spring Boot를 기반으로 하며, 로그인/로그아웃, 주식 거래, 포트폴리오 관리 등의 기능을 제공합니다.

---

## 🚀 **기능 개요**

### 🔐 **인증 및 사용자 관리**
- **로그인/로그아웃**  
  - 로그인 시 세션 기반 인증이 적용되며, 로그아웃 후 즉시 로그인 화면으로 이동  
  - 로그인 상태일 때만 주식 거래 및 포트폴리오 관리 가능  

- **네비게이션 바**  
  - 로그인 상태일 때 `Market`, `My Portfolio`, `Account`, `Logout` 버튼 표시  
  - 로그아웃 시 네비게이션 바가 즉시 업데이트되고 로그인 페이지로 이동  

---

### 📊 **주식 거래**
- **주식 시장 (`Market View`)**
  - 사용자가 현재 시장에서 거래 가능한 주식을 확인할 수 있음  
  - 주식 목록에는 `이름(Name)`, `코드(Code)`, `가격(Price)`, `거래 옵션(Actions)`이 포함됨  
  - `Buy` 버튼을 클릭하면 **구매 모달(Purchase Modal)**이 나타남  
  - `Sell` 버튼을 클릭하면 **판매 모달(Sell Modal)**이 나타남  

- **주식 구매 (`Purchase Modal`)**
  - 사용자가 주식을 선택하여 구매할 수 있는 팝업 창  
  - 입력 필드: `구매 수량(Quantity)`, `비밀번호(Password)`  
  - 구매 버튼 클릭 시 총 금액이 계산되어 표시됨  
  - 성공 시 **"Purchase successful!"** 메시지가 표시되며 자동으로 모달이 닫힘  

- **주식 판매 (`Sell Modal`)**
  - 사용자가 보유한 주식을 선택하여 판매할 수 있는 팝업 창  
  - 입력 필드: `판매 수량(Quantity)`, `비밀번호(Password)`  
  - 판매 버튼 클릭 시 예상 판매 수익이 표시됨  
  - 성공 시 **"Stock sold successfully!"** 메시지가 표시되며 자동으로 모달이 닫힘  

---

### 📁 **포트폴리오 관리 (`Portfolio View`)**
- **보유 주식 목록 확인**
  - 사용자가 현재 보유한 주식을 조회할 수 있음  
  - `이름(Name)`, `코드(Code)`, `보유 수량(Quantity)`, `현재 가치(Current Value)`를 표시  

- **포트폴리오 내 주식 판매**
  - 보유한 주식 중 일부를 판매할 수 있음  
  - `Sell` 버튼을 클릭하면 판매 모달이 표시되며, 원하는 수량을 입력 후 판매 가능  

---

### 👤 **사용자 계정 관리 (`Account View`)**
- **계정 정보 조회**
  - 로그인한 사용자의 이름, 보유 현금 잔액을 표시  

- **입금 (`Deposit`)**
  - 사용자가 계좌에 금액을 추가할 수 있음  
  - 입력 필드: `입금 금액(Amount)`, `비밀번호(Password)`  
  - 성공 시 잔액이 즉시 업데이트되며 **"Deposit successful!"** 메시지 표시  

- **출금 (`Withdraw`)**
  - 사용자가 계좌에서 금액을 인출할 수 있음  
  - 입력 필드: `출금 금액(Amount)`, `비밀번호(Password)`  
  - 성공 시 잔액이 즉시 업데이트되며 **"Withdrawal successful!"** 메시지 표시  

---

## 🔧 **기술 스택**
- **Frontend**: Vue 3, Vue Router, Pinia  
- **Backend**: Spring Boot, Spring Security  
- **Database**: MariaDB  

---

## 🎯 **설치 및 실행**
```sh
# npm 설치 및 mariaDB 실행이 되었다는 가정 하에 다음 명령어를 실행합니다.

# ** mariaDB에 'stock' database를 추가해야합니다.
# ** /stock/src/main/resources/application.properties 안의 password 설정을 개인이 설정한 비밀번호로 변경해야합니다.

# 현재 위치 확인
cd stock-refactor
pwd # -> ~/stock-refactor
ls # -> /stock  /stock-app  README.md

# 프론트엔드 실행
cd stock-app
npm install
npm run dev

# 백엔드 실행 (Spring Boot)
cd ../stock
./mvnw spring-boot:run
```

---

## 📜 **API 엔드포인트**

### 🔐 **사용자 인증 (Authentication)**
| Method | Endpoint          | Description |
|--------|------------------|-------------|
| `POST` | `/player/login`  | 사용자 로그인 (세션 기반 인증) |
| `POST` | `/player/logout` | 사용자 로그아웃 (세션 삭제) |
| `POST` | `/player/register` | 신규 사용자 회원가입 |

---

### 👤 **사용자 계정 관리 (Account) - 로그인 필요**
| Method | Endpoint            | Description |
|--------|--------------------|-------------|
| `GET`  | `/player/info`     | 사용자 계정 정보 조회 (이름, 보유 잔액 등) |
| `POST` | `/player/deposit`  | 계좌에 돈 입금 |
| `POST` | `/player/withdraw` | 계좌에서 돈 출금 |

---

### 📈 **주식 관리 (Stock)**
| Method | Endpoint            | Description |
|--------|--------------------|-------------|
| `GET` | `/stock/all`  | 모든 주식 목록 보기 |
| `GET` | `/stock/{stockId}` | {stockId} 를 가진 주식 상세 보기 |
| `POST`  | `/stock`     | [ Admin 기능 ] 주식 추가 |
| `POST`  | `/stock/description-update`     | [ Admin 기능 ] 주식 설명 변경 |

> Admin 계정은 자동으로 생성됩니다.
>
> **Id**: admin
>
> **Pwd**: admin

---

### 📊 **주식 거래 (Market) - 로그인 필요**
| Method | Endpoint              | Description |
|--------|----------------------|-------------|
| `GET`  | `/market/my-stocks`      | 사용자가 보유한 주식 목록 보기 |
| `POST` | `/market/purchase`    | 주식 구매 요청 (사용자 계좌에서 돈 차감) |
| `POST` | `/market/sell`        | 보유 주식 판매 요청 (판매 후 계좌에 돈 추가) |
